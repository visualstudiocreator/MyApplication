package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class AddNewsActivity extends AppCompatActivity {
    private EditText etTitle, etContent;
    private Button btnSave, btnPickImage;
    private ImageView imgPreview;
    private DBHelper db;
    private String imageBase64;

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::onImagePicked);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_news);

        // Инициализация тулбара и обработка стрелки назад
        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Добавить новость");
            }
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        db = new DBHelper(this);

        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        btnSave = findViewById(R.id.btnSaveNews);
        btnPickImage = findViewById(R.id.btnPickImage);
        imgPreview = findViewById(R.id.imgPreview);

        btnPickImage.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        btnSave.setOnClickListener(v -> {
            String t = etTitle.getText().toString().trim();
            String c = etContent.getText().toString().trim();
            if (TextUtils.isEmpty(t) || TextUtils.isEmpty(c)) {
                Toast.makeText(AddNewsActivity.this, "Введите заголовок и текст", Toast.LENGTH_SHORT).show();
                return;
            }
            db.addNews(t, c, imageBase64);
            Toast.makeText(AddNewsActivity.this, "Новость добавлена", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void onImagePicked(Uri uri) {
        if (uri == null) return;
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            if (is == null) return;
            Bitmap bmp = BitmapFactory.decodeStream(is);
            is.close();
            // Optionally compress to reduce size
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] bytes = baos.toByteArray();
            imageBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP);
            imgPreview.setImageBitmap(bmp);
            imgPreview.setVisibility(ImageView.VISIBLE);
        } catch (Exception e) {
            Toast.makeText(this, "Не удалось загрузить фото", Toast.LENGTH_SHORT).show();
        }
    }
}