package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "civic_app.db";
    private static final int DB_VERSION = 5;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT, role TEXT)");
        db.execSQL("CREATE TABLE news (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, content TEXT, ts INTEGER, image_base64 TEXT)");
        db.execSQL("CREATE TABLE complaints (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, text TEXT, ts INTEGER, response TEXT, status TEXT, district TEXT, address TEXT, service_type TEXT)");
        db.execSQL("CREATE TABLE services (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, description TEXT, requirements TEXT)");
        db.execSQL("CREATE TABLE service_requests (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, service_id INTEGER, comment TEXT, ts INTEGER, status TEXT)");

        // seed users
        ContentValues admin = new ContentValues();
        admin.put("username", "admin");
        admin.put("password", "admin");
        admin.put("role", "admin");
        db.insert("users", null, admin);

        ContentValues user = new ContentValues();
        user.put("username", "user");
        user.put("password", "user");
        user.put("role", "user");
        db.insert("users", null, user);

        // seed services catalog
        ContentValues s1 = new ContentValues();
        s1.put("name", "Регистрация по месту жительства");
        s1.put("description", "Подача заявления на регистрацию или изменение места жительства.");
        s1.put("requirements", "Паспорт, заявление, договор найма или свидетельство собственности.");
        db.insert("services", null, s1);

        ContentValues s2 = new ContentValues();
        s2.put("name", "Выдача справки");
        s2.put("description", "Получение официальной справки из администрации (о составе семьи, проживании и т.п.).");
        s2.put("requirements", "Паспорт, заявление, при необходимости — подтверждающие документы.");
        db.insert("services", null, s2);

        ContentValues s3 = new ContentValues();
        s3.put("name", "Запись на прием к специалисту");
        s3.put("description", "Онлайн-запись на консультацию в профильном отделе администрации.");
        s3.put("requirements", "Паспорт, краткое описание вопроса.");
        db.insert("services", null, s3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE news ADD COLUMN image_base64 TEXT");
        }
        if (oldVersion < 3) {
            db.execSQL("CREATE TABLE IF NOT EXISTS services (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, description TEXT, requirements TEXT)");
            db.execSQL("CREATE TABLE IF NOT EXISTS service_requests (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, service_id INTEGER, comment TEXT, ts INTEGER, status TEXT)");
            // optional seed to ensure catalog exists after upgrade
            Cursor cur = db.query("services", new String[]{"id"}, null, null, null, null, null);
            try {
                if (!cur.moveToFirst()) {
                    ContentValues s1 = new ContentValues();
                    s1.put("name", "Регистрация по месту жительства");
                    s1.put("description", "Подача заявления на регистрацию или изменение места жительства.");
                    s1.put("requirements", "Паспорт, заявление, договор найма или свидетельство собственности.");
                    db.insert("services", null, s1);

                    ContentValues s2 = new ContentValues();
                    s2.put("name", "Выдача справки");
                    s2.put("description", "Получение официальной справки из администрации (о составе семьи, проживании и т.п.).");
                    s2.put("requirements", "Паспорт, заявление, при необходимости — подтверждающие документы.");
                    db.insert("services", null, s2);
                }
            } finally {
                cur.close();
            }
        }
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE complaints ADD COLUMN district TEXT");
            db.execSQL("ALTER TABLE complaints ADD COLUMN address TEXT");
            db.execSQL("ALTER TABLE complaints ADD COLUMN service_type TEXT");
        }
        if (oldVersion < 5) {
            // Очищаем тестовые данные — оставляем только пользователей и каталог услуг
            db.execSQL("DELETE FROM news");
            db.execSQL("DELETE FROM complaints");
            db.execSQL("DELETE FROM service_requests");
        }
    }

    public String getRoleIfValid(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("users", new String[]{"role"}, "username=? AND password=?", new String[]{username, password}, null, null, null);
        try {
            if (c.moveToFirst()) {
                return c.getString(0);
            }
            return null;
        } finally {
            c.close();
        }
    }

    public List<NewsItem> getAllNews() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("news", new String[]{"id", "title", "content", "ts", "image_base64"}, null, null, null, null, "ts DESC");
        List<NewsItem> list = new ArrayList<>();
        try {
            while (c.moveToNext()) {
                NewsItem item = new NewsItem();
                item.id = c.getInt(0);
                item.title = c.getString(1);
                item.content = c.getString(2);
                item.ts = c.getLong(3);
                item.imageBase64 = c.getString(4);
                list.add(item);
            }
        } finally {
            c.close();
        }
        return list;
    }

    public void addNews(String title, String content) {
        addNews(title, content, null);
    }

    public void addNews(String title, String content, String imageBase64) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("content", content);
        cv.put("ts", System.currentTimeMillis());
        cv.put("image_base64", imageBase64);
        db.insert("news", null, cv);
    }

    public List<Complaint> getComplaintsByUser(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("complaints", new String[]{"id", "username", "text", "ts", "response", "status", "district", "address", "service_type"}, "username=?", new String[]{username}, null, null, "ts DESC");
        List<Complaint> list = new ArrayList<>();
        try {
            while (c.moveToNext()) {
                Complaint comp = new Complaint();
                comp.id = c.getInt(0);
                comp.username = c.getString(1);
                comp.text = c.getString(2);
                comp.ts = c.getLong(3);
                comp.response = c.getString(4);
                comp.status = c.getString(5);
                comp.district = c.getString(6);
                comp.address = c.getString(7);
                comp.serviceType = c.getString(8);
                list.add(comp);
            }
        } finally {
            c.close();
        }
        return list;
    }

    public void addComplaint(String username, String text) {
        addComplaint(username, text, null, null, null);
    }

    public void addComplaint(String username, String text, String district, String address, String serviceType) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("text", text);
        cv.put("ts", System.currentTimeMillis());
        cv.put("response", "");
        cv.put("status", "open");
        cv.put("district", district);
        cv.put("address", address);
        cv.put("service_type", serviceType);
        db.insert("complaints", null, cv);
    }

    public List<Complaint> getAllComplaints() {
        return getAllComplaints("ts DESC");
    }

    public List<Complaint> getAllComplaints(String orderBy) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("complaints", new String[]{"id", "username", "text", "ts", "response", "status", "district", "address", "service_type"}, null, null, null, null, orderBy);
        List<Complaint> list = new ArrayList<>();
        try {
            while (c.moveToNext()) {
                Complaint comp = new Complaint();
                comp.id = c.getInt(0);
                comp.username = c.getString(1);
                comp.text = c.getString(2);
                comp.ts = c.getLong(3);
                comp.response = c.getString(4);
                comp.status = c.getString(5);
                comp.district = c.getString(6);
                comp.address = c.getString(7);
                comp.serviceType = c.getString(8);
                list.add(comp);
            }
        } finally {
            c.close();
        }
        return list;
    }

    public void respondToComplaint(int id, String response) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("response", response);
        cv.put("status", "answered");
        db.update("complaints", cv, "id=?", new String[]{String.valueOf(id)});
    }

    // Simple models
    public static class NewsItem {
        public int id;
        public String title;
        public String content;
        public long ts;
        public String imageBase64;
    }

    public static class Complaint {
        public int id;
        public String username;
        public String text;
        public long ts;
        public String response;
        public String status;
        public String district;
        public String address;
        public String serviceType;
    }

    public List<Service> getAllServices() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("services", new String[]{"id", "name", "description", "requirements"}, null, null, null, null, "name ASC");
        List<Service> list = new ArrayList<>();
        try {
            while (c.moveToNext()) {
                Service s = new Service();
                s.id = c.getInt(0);
                s.name = c.getString(1);
                s.description = c.getString(2);
                s.requirements = c.getString(3);
                list.add(s);
            }
        } finally {
            c.close();
        }
        return list;
    }

    public String getServiceNameById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("services", new String[]{"name"}, "id=?", new String[]{String.valueOf(id)}, null, null, null);
        try {
            if (c.moveToFirst()) {
                return c.getString(0);
            }
            return String.valueOf(id);
        } finally {
            c.close();
        }
    }

    public void addServiceRequest(String username, int serviceId, String comment) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("service_id", serviceId);
        cv.put("comment", comment);
        cv.put("ts", System.currentTimeMillis());
        cv.put("status", "submitted");
        db.insert("service_requests", null, cv);
    }

    public List<ServiceRequest> getServiceRequestsByUser(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("service_requests", new String[]{"id", "username", "service_id", "comment", "ts", "status"}, "username=?", new String[]{username}, null, null, "ts DESC");
        List<ServiceRequest> list = new ArrayList<>();
        try {
            while (c.moveToNext()) {
                ServiceRequest r = new ServiceRequest();
                r.id = c.getInt(0);
                r.username = c.getString(1);
                r.serviceId = c.getInt(2);
                r.comment = c.getString(3);
                r.ts = c.getLong(4);
                r.status = c.getString(5);
                list.add(r);
            }
        } finally {
            c.close();
        }
        return list;
    }

    public void updateServiceRequestStatus(int id, String status) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("status", status);
        db.update("service_requests", cv, "id=?", new String[]{String.valueOf(id)});
    }

    public List<ServiceRequest> getAllServiceRequests() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("service_requests", new String[]{"id", "username", "service_id", "comment", "ts", "status"}, null, null, null, null, "ts DESC");
        List<ServiceRequest> list = new ArrayList<>();
        try {
            while (c.moveToNext()) {
                ServiceRequest r = new ServiceRequest();
                r.id = c.getInt(0);
                r.username = c.getString(1);
                r.serviceId = c.getInt(2);
                r.comment = c.getString(3);
                r.ts = c.getLong(4);
                r.status = c.getString(5);
                list.add(r);
            }
        } finally {
            c.close();
        }
        return list;
    }

    public static class Service {
        public int id;
        public String name;
        public String description;
        public String requirements;
    }

    public static class ServiceRequest {
        public int id;
        public String username;
        public int serviceId;
        public String comment;
        public long ts;
        public String status;
    }
}