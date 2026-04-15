package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsVH> {
    private final List<DBHelper.NewsItem> items;

    public NewsAdapter(List<DBHelper.NewsItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public NewsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new NewsVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsVH holder, int position) {
        DBHelper.NewsItem item = items.get(position);
        holder.tvTitle.setText(item.title);
        holder.tvContent.setText(item.content);
        // date
        String dateStr = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(new Date(item.ts));
        holder.tvDate.setText("Дата: " + dateStr);
        // image
        if (item.imageBase64 != null && !item.imageBase64.isEmpty()) {
            try {
                byte[] bytes = Base64.decode(item.imageBase64, Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.imgNews.setImageBitmap(bmp);
                holder.imgNews.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                holder.imgNews.setVisibility(View.GONE);
            }
        } else {
            holder.imgNews.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class NewsVH extends RecyclerView.ViewHolder {
        ImageView imgNews;
        TextView tvTitle;
        TextView tvContent;
        TextView tvDate;
        public NewsVH(@NonNull View itemView) {
            super(itemView);
            imgNews = itemView.findViewById(R.id.imgNews);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}