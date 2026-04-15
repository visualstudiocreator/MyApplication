package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddServiceRequestActivity extends AppCompatActivity {
    private DBHelper db;
    private int serviceId;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service_request);

        db = new DBHelper(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        serviceId = getIntent().getIntExtra("service_id", -1);
        username = getIntent().getStringExtra("username");
        String serviceName = getIntent().getStringExtra("service_name");

        TextView tvServiceName = findViewById(R.id.tvServiceName);
        tvServiceName.setText("Услуга: " + serviceName);

        TextInputEditText etComment = findViewById(R.id.etComment);
        MaterialButton btnSend = findViewById(R.id.btnSendRequest);
        btnSend.setOnClickListener(v -> {
            String comment = etComment.getText() != null ? etComment.getText().toString().trim() : "";
            if (TextUtils.isEmpty(comment)) {
                Toast.makeText(AddServiceRequestActivity.this, "Введите комментарий", Toast.LENGTH_SHORT).show();
                return;
            }
            if (username == null) {
                Toast.makeText(AddServiceRequestActivity.this, "Неизвестный пользователь", Toast.LENGTH_SHORT).show();
                return;
            }
            db.addServiceRequest(username, serviceId, comment);
            Toast.makeText(AddServiceRequestActivity.this, "Заявка отправлена", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}