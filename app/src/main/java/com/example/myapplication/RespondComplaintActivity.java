package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RespondComplaintActivity extends AppCompatActivity {
    private static final String[] STATUS_VALUES = {"pending", "in_progress", "completed"};
    private static final String[] STATUS_LABELS = {"Ожидает", "В работе", "Завершено"};

    private DBHelper db;
    private int complaintId;
    private EditText etResponse;
    private Spinner spinnerStatus;
    private Button btnSendResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respond_complaint);
        db = new DBHelper(this);
        complaintId = getIntent().getIntExtra("complaint_id", -1);

        etResponse = findViewById(R.id.etResponse);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnSendResponse = findViewById(R.id.btnSendResponse);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, STATUS_LABELS);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        DBHelper.Complaint complaint = db.getComplaintById(complaintId);
        if (complaint != null) {
            spinnerStatus.setSelection(statusIndexByValue(complaint.status));
            if (!TextUtils.isEmpty(complaint.response)) {
                etResponse.setText(complaint.response);
            }
        }

        btnSendResponse.setOnClickListener(v -> {
            String resp = etResponse.getText().toString().trim();
            if (complaintId == -1) {
                Toast.makeText(RespondComplaintActivity.this, "Ошибка: жалоба не найдена", Toast.LENGTH_SHORT).show();
                return;
            }
            String selectedStatus = STATUS_VALUES[spinnerStatus.getSelectedItemPosition()];
            db.updateComplaintProgress(complaintId, selectedStatus, resp);
            Toast.makeText(RespondComplaintActivity.this, "Статус и ответ сохранены", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private int statusIndexByValue(String status) {
        if ("in_progress".equals(status)) return 1;
        if ("completed".equals(status) || "answered".equals(status)) return 2;
        return 0;
    }
}
