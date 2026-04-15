package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestVH> {
    private final List<DBHelper.ServiceRequest> items;
    private final DBHelper db;

    public RequestAdapter(DBHelper db, List<DBHelper.ServiceRequest> items) {
        this.db = db;
        this.items = items;
    }

    @NonNull
    @Override
    public RequestVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false);
        return new RequestVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestVH holder, int position) {
        DBHelper.ServiceRequest r = items.get(position);
        String serviceName = db.getServiceNameById(r.serviceId);
        holder.tvServiceName.setText(serviceName);
        holder.tvComment.setText(r.comment);
        holder.tvStatus.setText("Статус: " + mapStatus(r.status));
    }

    private String mapStatus(String s) {
        if ("submitted".equalsIgnoreCase(s)) return "Отправлена";
        if ("in_progress".equalsIgnoreCase(s)) return "В обработке";
        if ("completed".equalsIgnoreCase(s)) return "Завершена";
        return s;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class RequestVH extends RecyclerView.ViewHolder {
        TextView tvServiceName, tvComment, tvStatus;
        RequestVH(@NonNull View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}