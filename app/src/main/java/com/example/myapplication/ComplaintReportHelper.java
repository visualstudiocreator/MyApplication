package com.example.myapplication;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class ComplaintReportHelper {

    private ComplaintReportHelper() {
    }

    public static boolean isCompleted(String status) {
        if (status == null) return false;
        String v = status.toLowerCase(Locale.ROOT);
        return "completed".equals(v) || "answered".equals(v);
    }

    public static String buildReport(DBHelper.Complaint complaint) {
        if (complaint == null || !isCompleted(complaint.status)) {
            return null;
        }

        String service = complaint.serviceType == null ? "" : complaint.serviceType.trim().toLowerCase(Locale.ROOT);
        String workDescription;
        String resultText;
        String serviceName = TextUtils.isEmpty(complaint.serviceType) ? "Профильная служба" : complaint.serviceType;

        if (service.contains("пожар")) {
            workDescription = "Пожарным подразделением выполнен выезд по адресу обращения. "
                    + "Проведён осмотр объекта, оценены пожарные риски и соответствие требованиям пожарной безопасности. "
                    + "По результатам проверки выданы рекомендации и при необходимости предписания об устранении нарушений.";
            resultText = "Угроза для жизни и здоровья граждан устранена. Контроль за выполнением предписаний передан в установленном порядке.";
        } else if (service.contains("мчс")) {
            workDescription = "Подразделением МЧС проведена проверка по факту обращения. "
                    + "Выполнена оценка чрезвычайной ситуации и потенциальных угроз для населения и инфраструктуры. "
                    + "Организованы меры по локализации риска и информированию ответственных структур.";
            resultText = "Обстановка стабилизирована. Риск для граждан снижен, повторный мониторинг участка назначен.";
        } else if (service.contains("мед")) {
            workDescription = "Медицинской службой проведена проверка обращения и оценка доступности оказания помощи по указанному адресу. "
                    + "При необходимости организовано взаимодействие с профильными подразделениями и даны рекомендации по устранению выявленных проблем.";
            resultText = "Условия для оказания медицинской помощи приведены в соответствие с требованиями. "
                    + "Информация о результатах работы передана заявителю.";
        } else {
            workDescription = "По обращению проведена проверка, профильной службой выполнены необходимые мероприятия "
                    + "по устранению выявленных нарушений.";
            resultText = "Работы по обращению завершены, контроль выполнения назначен.";
        }

        String district = TextUtils.isEmpty(complaint.district) ? "не указан" : complaint.district;
        String address = TextUtils.isEmpty(complaint.address) ? "не указан" : complaint.address;
        String date = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(new Date(complaint.ts));

        StringBuilder sb = new StringBuilder();
        sb.append("ОТЧЁТ О ВЫПОЛНЕННЫХ РАБОТАХ\n\n");
        sb.append("Служба: ").append(serviceName).append("\n");
        sb.append("Район: ").append(district).append("\n");
        sb.append("Адрес: ").append(address).append("\n");
        sb.append("Дата обращения: ").append(date).append("\n\n");
        sb.append("Содержание жалобы:\n").append(complaint.text).append("\n\n");
        sb.append("Выполненные работы:\n").append(workDescription).append("\n\n");
        sb.append("Результат:\n").append(resultText);

        if (!TextUtils.isEmpty(complaint.response)) {
            sb.append("\n\nКомментарий администрации:\n").append(complaint.response);
        }

        return sb.toString();
    }
}
