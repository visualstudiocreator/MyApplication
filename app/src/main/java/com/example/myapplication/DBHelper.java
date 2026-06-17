package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "civic_app.db";
    private static final int DB_VERSION = 7;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT, role TEXT)");
        db.execSQL("CREATE TABLE news (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, content TEXT, ts INTEGER, image_base64 TEXT)");
        db.execSQL("CREATE TABLE complaints (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, text TEXT, ts INTEGER, response TEXT, status TEXT, district TEXT, address TEXT, service_type TEXT)");

        // seed users
        ContentValues admin = new ContentValues();
        admin.put("username", "admin");
        admin.put("password", "admin");
        admin.put("role", "admin");
        db.insert("users", null, admin);

        ContentValues user = new ContentValues();
        user.put("username", "user");
        user.put("password", "user");
        user.put("role", "user");
        db.insert("users", null, user);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE news ADD COLUMN image_base64 TEXT");
        }
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE complaints ADD COLUMN district TEXT");
            db.execSQL("ALTER TABLE complaints ADD COLUMN address TEXT");
            db.execSQL("ALTER TABLE complaints ADD COLUMN service_type TEXT");
        }
        if (oldVersion < 5) {
            // Очищаем тестовые данные.
            db.execSQL("DELETE FROM news");
            db.execSQL("DELETE FROM complaints");
        }
        if (oldVersion < 6) {
            // Миграция старых статусов жалоб к новой модели.
            db.execSQL("UPDATE complaints SET status='pending' WHERE status IS NULL OR status='' OR status='open'");
            db.execSQL("UPDATE complaints SET status='completed' WHERE status='answered'");
            db.execSQL("DROP TABLE IF EXISTS services");
            db.execSQL("DROP TABLE IF EXISTS service_requests");
        }
        if (oldVersion < 7) {
            db.execSQL("DELETE FROM news");
            db.execSQL("DELETE FROM complaints");
        }
    }

    public String getRoleIfValid(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("users", new String[]{"role"}, "username=? AND password=?", new String[]{username, password}, null, null, null);
        try {
            if (c.moveToFirst()) {
                return c.getString(0);
            }
            return null;
        } finally {
            c.close();
        }
    }

    public List<NewsItem> getAllNews() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("news", new String[]{"id", "title", "content", "ts", "image_base64"}, null, null, null, null, "ts DESC");
        List<NewsItem> list = new ArrayList<>();
        try {
            while (c.moveToNext()) {
                NewsItem item = new NewsItem();
                item.id = c.getInt(0);
                item.title = c.getString(1);
                item.content = c.getString(2);
                item.ts = c.getLong(3);
                item.imageBase64 = c.getString(4);
                list.add(item);
            }
        } finally {
            c.close();
        }
        return list;
    }

    public void addNews(String title, String content) {
        addNews(title, content, null);
    }

    public void addNews(String title, String content, String imageBase64) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("content", content);
        cv.put("ts", System.currentTimeMillis());
        cv.put("image_base64", imageBase64);
        db.insert("news", null, cv);
    }

    public List<Complaint> getComplaintsByUser(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("complaints", new String[]{"id", "username", "text", "ts", "response", "status", "district", "address", "service_type"}, "username=?", new String[]{username}, null, null, "ts DESC");
        List<Complaint> list = new ArrayList<>();
        try {
            while (c.moveToNext()) {
                Complaint comp = new Complaint();
                comp.id = c.getInt(0);
                comp.username = c.getString(1);
                comp.text = c.getString(2);
                comp.ts = c.getLong(3);
                comp.response = c.getString(4);
                comp.status = c.getString(5);
                comp.district = c.getString(6);
                comp.address = c.getString(7);
                comp.serviceType = c.getString(8);
                list.add(comp);
            }
        } finally {
            c.close();
        }
        return list;
    }

    public void addComplaint(String username, String text) {
        addComplaint(username, text, null, null, null);
    }

    public void addComplaint(String username, String text, String district, String address, String serviceType) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("text", text);
        cv.put("ts", System.currentTimeMillis());
        cv.put("response", "");
        cv.put("status", "pending");
        cv.put("district", district);
        cv.put("address", address);
        cv.put("service_type", serviceType);
        db.insert("complaints", null, cv);
    }

    public List<Complaint> getAllComplaints() {
        return getAllComplaints("ts DESC");
    }

    public List<Complaint> getAllComplaints(String orderBy) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("complaints", new String[]{"id", "username", "text", "ts", "response", "status", "district", "address", "service_type"}, null, null, null, null, orderBy);
        List<Complaint> list = new ArrayList<>();
        try {
            while (c.moveToNext()) {
                Complaint comp = new Complaint();
                comp.id = c.getInt(0);
                comp.username = c.getString(1);
                comp.text = c.getString(2);
                comp.ts = c.getLong(3);
                comp.response = c.getString(4);
                comp.status = c.getString(5);
                comp.district = c.getString(6);
                comp.address = c.getString(7);
                comp.serviceType = c.getString(8);
                list.add(comp);
            }
        } finally {
            c.close();
        }
        return list;
    }

    public DBHelper.Complaint getComplaintById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(
                "complaints",
                new String[]{"id", "username", "text", "ts", "response", "status", "district", "address", "service_type"},
                "id=?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );
        try {
            if (!c.moveToFirst()) {
                return null;
            }
            Complaint comp = new Complaint();
            comp.id = c.getInt(0);
            comp.username = c.getString(1);
            comp.text = c.getString(2);
            comp.ts = c.getLong(3);
            comp.response = c.getString(4);
            comp.status = c.getString(5);
            comp.district = c.getString(6);
            comp.address = c.getString(7);
            comp.serviceType = c.getString(8);
            return comp;
        } finally {
            c.close();
        }
    }

    public void respondToComplaint(int id, String response) {
        updateComplaintProgress(id, "completed", response);
    }

    public void updateComplaintProgress(int id, String status, String response) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("response", response);
        cv.put("status", status);
        db.update("complaints", cv, "id=?", new String[]{String.valueOf(id)});
    }

    // Simple models
    public static class NewsItem {
        public int id;
        public String title;
        public String content;
        public long ts;
        public String imageBase64;
    }

    public static class Complaint {
        public int id;
        public String username;
        public String text;
        public long ts;
        public String response;
        public String status;
        public String district;
        public String address;
        public String serviceType;
    }
}