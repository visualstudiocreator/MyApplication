package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RespondComplaintActivity extends AppCompatActivity {
    private DBHelper db;
    private int complaintId;
    private EditText etResponse;
    private Button btnSendResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respond_complaint);
        db = new DBHelper(this);
        complaintId = getIntent().getIntExtra("complaint_id", -1);

        etResponse = findViewById(R.id.etResponse);
        btnSendResponse = findViewById(R.id.btnSendResponse);

        btnSendResponse.setOnClickListener(v -> {
            String resp = etResponse.getText().toString().trim();
            if (TextUtils.isEmpty(resp)) {
                Toast.makeText(RespondComplaintActivity.this, "Введите ответ", Toast.LENGTH_SHORT).show();
                return;
            }
            if (complaintId != -1) {
                db.respondToComplaint(complaintId, resp);
                Toast.makeText(RespondComplaintActivity.this, "Ответ отправлен", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(RespondComplaintActivity.this, "Ошибка: жалоба не найдена", Toast.LENGTH_SHORT).show();
            }
        });
    }
}