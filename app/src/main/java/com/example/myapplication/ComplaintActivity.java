package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

public class ComplaintActivity extends AppCompatActivity {
    private EditText etText;
    private EditText etAddress;
    private Spinner spinnerDistrict;
    private Spinner spinnerService;
    private Button btnSend;
    private DBHelper db;
    private String username;

    // Районы Энгельса
    private static final String[] DISTRICTS = {
        "Выберите район",
        "Центральный район",
        "Северный район",
        "Южный район",
        "Западный район",
        "Восточный район",
        "Приволжский район"
    };

    // Службы
    private static final String[] SERVICES = {
        "Выберите службу",
        "Пожарные",
        "МЧС",
        "Медицина"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);
        db = new DBHelper(this);
        username = getIntent().getStringExtra("username");

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Отправить жалобу");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        etText = findViewById(R.id.etComplaint);
        etAddress = findViewById(R.id.etAddress);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        spinnerService = findViewById(R.id.spinnerService);
        btnSend = findViewById(R.id.btnSendComplaint);

        // Настройка спиннеров
        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, DISTRICTS);
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDistrict.setAdapter(districtAdapter);

        ArrayAdapter<String> serviceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SERVICES);
        serviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerService.setAdapter(serviceAdapter);

        btnSend.setOnClickListener(v -> {
            String text = etText.getText().toString().trim();
            if (TextUtils.isEmpty(text)) {
                Toast.makeText(ComplaintActivity.this, "Введите текст жалобы", Toast.LENGTH_SHORT).show();
                return;
            }

            String district = spinnerDistrict.getSelectedItem().toString();
            if (district.equals("Выберите район")) {
                Toast.makeText(ComplaintActivity.this, "Выберите район", Toast.LENGTH_SHORT).show();
                return;
            }

            String address = etAddress.getText().toString().trim();
            if (TextUtils.isEmpty(address)) {
                Toast.makeText(ComplaintActivity.this, "Введите адрес", Toast.LENGTH_SHORT).show();
                return;
            }

            String serviceType = spinnerService.getSelectedItem().toString();
            if (serviceType.equals("Выберите службу")) {
                Toast.makeText(ComplaintActivity.this, "Выберите службу", Toast.LENGTH_SHORT).show();
                return;
            }

            db.addComplaint(username, text, district, address, serviceType);
            Toast.makeText(ComplaintActivity.this, "Жалоба отправлена", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}