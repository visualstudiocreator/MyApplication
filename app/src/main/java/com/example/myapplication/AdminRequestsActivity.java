package com.example.myapplication;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class AdminRequestsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_requests);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView recycler = findViewById(R.id.recyclerAdminRequests);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        DBHelper db = new DBHelper(this);
        List<DBHelper.ServiceRequest> all = db.getAllServiceRequests();
        AdminRequestAdapter adapter = new AdminRequestAdapter(this, all, db);
        recycler.setAdapter(adapter);
    }
}