package com.example.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class MyRequestsActivity extends AppCompatActivity {
    private DBHelper db;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_requests);

        db = new DBHelper(this);
        username = getIntent().getStringExtra("username");

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView recycler = findViewById(R.id.recyclerRequests);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        List<DBHelper.ServiceRequest> requests = db.getServiceRequestsByUser(username);
        recycler.setAdapter(new RequestAdapter(db, requests));
    }
}