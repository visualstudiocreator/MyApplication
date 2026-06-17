package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class AdminComplaintsActivity extends AppCompatActivity {
    private DBHelper db;
    private ListView listView;
    private List<DBHelper.Complaint> complaints;
    private String currentSortOrder = "address ASC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_complaints);
        db = new DBHelper(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        listView = findViewById(R.id.listComplaints);
        loadComplaints();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {
                DBHelper.Complaint c = complaints.get(position);
                Intent i = new Intent(AdminComplaintsActivity.this, RespondComplaintActivity.class);
                i.putExtra("complaint_id", c.id);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin_complaints, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_sort_by_address) {
            currentSortOrder = "address ASC";
            loadComplaints();
            return true;
        } else if (id == R.id.menu_sort_by_date) {
            currentSortOrder = "ts DESC";
            loadComplaints();
            return true;
        } else if (id == R.id.menu_sort_by_service) {
            currentSortOrder = "service_type ASC";
            loadComplaints();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadComplaints();
    }

    private void loadComplaints() {
        complaints = db.getAllComplaints(currentSortOrder);
        List<String> lines = new ArrayList<>();
        for (DBHelper.Complaint c : complaints) {
            StringBuilder sb = new StringBuilder();
            sb.append("Пользователь: ").append(c.username);
            sb.append("\nРайон: ").append(c.district != null ? c.district : "Не указан");
            sb.append("\nАдрес: ").append(c.address != null ? c.address : "Не указан");
            sb.append("\nСлужба: ").append(c.serviceType != null ? c.serviceType : "Не указана");
            sb.append("\nЖалоба: ").append(c.text);
            sb.append("\nСтатус: ").append(mapStatus(c.status));
            if (c.response != null && !c.response.isEmpty()) {
                sb.append("\nОтвет: ").append(c.response);
            }
            lines.add(sb.toString());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lines);
        listView.setAdapter(adapter);
    }

    private String mapStatus(String s) {
        if (s == null || s.isEmpty()) return "Ожидает";
        String v = s.toLowerCase();
        if (v.equals("open") || v.equals("pending")) return "Ожидает";
        if (v.equals("in_progress")) return "В работе";
        if (v.equals("answered") || v.equals("completed")) return "Завершено";
        return s;
    }
}