package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class ServiceDetailsActivity extends AppCompatActivity {
    private int serviceId;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_details);

        Intent intent = getIntent();
        serviceId = intent.getIntExtra("service_id", -1);
        String name = intent.getStringExtra("name");
        String description = intent.getStringExtra("description");
        String requirements = intent.getStringExtra("requirements");
        username = intent.getStringExtra("username"); // may be null if not provided

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        TextView tvName = findViewById(R.id.tvName);
        TextView tvDesc = findViewById(R.id.tvDescription);
        TextView tvReq = findViewById(R.id.tvRequirements);
        tvName.setText(name);
        tvDesc.setText(description);
        tvReq.setText("Требования: " + requirements);

        String role = intent.getStringExtra("role");

        MaterialButton btn = findViewById(R.id.btnApply);
        if ("admin".equals(role)) {
            // Администратор не отправляет заявки
            btn.setEnabled(false);
            btn.setText("Только для пользователей");
        } else {
            btn.setOnClickListener(v -> {
                Intent i = new Intent(ServiceDetailsActivity.this, AddServiceRequestActivity.class);
                i.putExtra("service_id", serviceId);
                i.putExtra("service_name", name);
                i.putExtra("username", username);
                startActivity(i);
            });
        }
    }
}