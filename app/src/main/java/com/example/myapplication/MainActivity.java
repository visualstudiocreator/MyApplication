package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DBHelper db;
    private String username;
    private String role;
    private RecyclerView recyclerView;
    private FloatingActionButton fabPrimary;
    private MaterialButton btnAdminComplaints, btnSendComplaint, btnMyComplaints, btnServices, btnMyRequests, btnAdminRequests;
    private LinearLayout rowComplaints, rowServicesRequests, rowAdminRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DBHelper(this);
        username = getIntent().getStringExtra("username");
        role = getIntent().getStringExtra("role");

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        recyclerView = findViewById(R.id.recyclerNews);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fabPrimary = findViewById(R.id.fabPrimary);
        btnAdminComplaints = findViewById(R.id.btnAdminComplaints);
        btnSendComplaint = findViewById(R.id.btnSendComplaint);
        btnMyComplaints = findViewById(R.id.btnMyComplaints);
        btnServices = findViewById(R.id.btnServices);
        btnMyRequests = findViewById(R.id.btnMyRequests);
        btnAdminRequests = findViewById(R.id.btnAdminRequests);

        rowComplaints = findViewById(R.id.rowComplaints);
        rowServicesRequests = findViewById(R.id.rowServicesRequests);
        rowAdminRequests = findViewById(R.id.rowAdminRequests);

        setupButtons();
        loadNews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNews();
    }

    private void setupButtons() {
        if ("admin".equals(role)) {
            fabPrimary.setContentDescription("Добавить новость");
            fabPrimary.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddNewsActivity.class)));
            btnAdminComplaints.setVisibility(View.VISIBLE);
            btnAdminComplaints.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AdminComplaintsActivity.class)));
            btnSendComplaint.setVisibility(View.GONE);
            btnMyComplaints.setVisibility(View.GONE);
            // Админ не отправляет заявки: скрываем целую строку услуг/заявок, чтобы не было лишнего зазора
            rowServicesRequests.setVisibility(View.GONE);

            // Показать управление заявками (строка видна)
            rowAdminRequests.setVisibility(View.VISIBLE);
            btnAdminRequests.setVisibility(View.VISIBLE);
            btnAdminRequests.setOnClickListener(v -> {
                Intent i = new Intent(MainActivity.this, AdminRequestsActivity.class);
                startActivity(i);
            });
        } else {
            fabPrimary.setContentDescription("Отправить жалобу");
            fabPrimary.setOnClickListener(v -> {
                Intent i = new Intent(MainActivity.this, ComplaintActivity.class);
                i.putExtra("username", username);
                startActivity(i);
            });
            btnAdminComplaints.setVisibility(View.GONE);
            btnSendComplaint.setVisibility(View.GONE);
            btnMyComplaints.setVisibility(View.VISIBLE);
            btnMyComplaints.setOnClickListener(v -> {
                Intent i = new Intent(MainActivity.this, MyComplaintsActivity.class);
                i.putExtra("username", username);
                startActivity(i);
            });

            rowAdminRequests.setVisibility(View.GONE);
            btnAdminRequests.setVisibility(View.GONE);

            rowServicesRequests.setVisibility(View.VISIBLE);
            btnServices.setVisibility(View.VISIBLE);
            btnServices.setOnClickListener(v -> {
                Intent i = new Intent(MainActivity.this, ServicesActivity.class);
                i.putExtra("username", username);
                i.putExtra("role", role);
                startActivity(i);
            });
            btnMyRequests.setVisibility(View.VISIBLE);
            btnMyRequests.setOnClickListener(v -> {
                Intent i = new Intent(MainActivity.this, MyRequestsActivity.class);
                i.putExtra("username", username);
                startActivity(i);
            });
        }
    }

    private void loadNews() {
        List<DBHelper.NewsItem> items = db.getAllNews();
        recyclerView.setAdapter(new NewsAdapter(items));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}