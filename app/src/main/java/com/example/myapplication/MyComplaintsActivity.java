package com.example.myapplication;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class MyComplaintsActivity extends AppCompatActivity {
    private DBHelper db;
    private String username;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_complaints);

        db = new DBHelper(this);
        username = getIntent().getStringExtra("username");

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Мои жалобы");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerComplaints);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadComplaints();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadComplaints();
    }

    private void loadComplaints() {
        List<DBHelper.Complaint> list = db.getComplaintsByUser(username);
        recyclerView.setAdapter(new ComplaintAdapter(list, this::showReportDialog));
    }

    private void showReportDialog(DBHelper.Complaint complaint) {
        String report = ComplaintReportHelper.buildReport(complaint);
        if (report == null) {
            return;
        }

        TextView messageView = new TextView(this);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        messageView.setPadding(padding, padding, padding, padding);
        messageView.setText(report);
        messageView.setTextSize(14f);
        messageView.setMovementMethod(new ScrollingMovementMethod());

        new AlertDialog.Builder(this)
                .setTitle("Отчёт о выполненных работах")
                .setView(messageView)
                .setPositiveButton("Закрыть", null)
                .show();
    }
}
