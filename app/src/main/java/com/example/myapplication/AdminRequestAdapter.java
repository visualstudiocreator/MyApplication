package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.color.MaterialColors;

import java.util.List;

public class AdminRequestAdapter extends RecyclerView.Adapter<AdminRequestAdapter.ViewHolder> {
    private final Context context;
    private final List<DBHelper.ServiceRequest> requests;
    private final DBHelper db;

    public AdminRequestAdapter(Context context, List<DBHelper.ServiceRequest> requests, DBHelper db) {
        this.context = context;
        this.requests = requests;
        this.db = db;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_admin_request, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DBHelper.ServiceRequest r = requests.get(position);
        String serviceName = db.getServiceNameById(r.serviceId);
        holder.tvServiceName.setText(serviceName);
        holder.tvUsername.setText("Пользователь: " + r.username);
        holder.tvComment.setText("Комментарий: " + r.comment);
        holder.chipStatus.setText(mapStatus(r.status));
        // Цветовая индикация статуса
        int primary = MaterialColors.getColor(holder.chipStatus, com.google.android.material.R.attr.colorPrimary);
        int error = MaterialColors.getColor(holder.chipStatus, com.google.android.material.R.attr.colorError);
        int neutral = 0xFF9E9E9E; // серый
        switch (r.status) {
            case "approved":
                holder.chipStatus.setChipBackgroundColorResource(android.R.color.transparent);
                holder.chipStatus.setChipBackgroundColor(android.content.res.ColorStateList.valueOf(primary));
                holder.chipStatus.setTextColor(0xFFFFFFFF);
                break;
            case "rejected":
                holder.chipStatus.setChipBackgroundColor(android.content.res.ColorStateList.valueOf(error));
                holder.chipStatus.setTextColor(0xFFFFFFFF);
                break;
            default: // submitted
                holder.chipStatus.setChipBackgroundColor(android.content.res.ColorStateList.valueOf(neutral));
                holder.chipStatus.setTextColor(0xFFFFFFFF);
                break;
        }

        boolean submitted = "submitted".equals(r.status);
        holder.btnApprove.setEnabled(submitted);
        holder.btnReject.setEnabled(submitted);
        holder.btnApprove.setAlpha(submitted ? 1f : 0.6f);
        holder.btnReject.setAlpha(submitted ? 1f : 0.6f);

        holder.btnApprove.setOnClickListener(view -> {
            if (!"submitted".equals(r.status)) return;
            db.updateServiceRequestStatus(r.id, "approved");
            r.status = "approved";
            notifyItemChanged(position);
        });
        holder.btnReject.setOnClickListener(view -> {
            if (!"submitted".equals(r.status)) return;
            db.updateServiceRequestStatus(r.id, "rejected");
            r.status = "rejected";
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    private String mapStatus(String status) {
        if (status == null) return "";
        switch (status) {
            case "submitted":
                return "Отправлено";
            case "approved":
                return "Одобрено";
            case "rejected":
                return "Отклонено";
            default:
                return status;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvServiceName;
        TextView tvUsername;
        TextView tvComment;
        Chip chipStatus;
        MaterialButton btnApprove;
        MaterialButton btnReject;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvComment = itemView.findViewById(R.id.tvComment);
            chipStatus = itemView.findViewById(R.id.chipStatus);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}