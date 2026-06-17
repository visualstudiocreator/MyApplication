package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ComplaintVH> {
    public interface OnReportRequestListener {
        void onReportRequested(DBHelper.Complaint complaint);
    }

    private final List<DBHelper.Complaint> items;
    @Nullable
    private final OnReportRequestListener reportListener;

    public ComplaintAdapter(List<DBHelper.Complaint> items) {
        this(items, null);
    }

    public ComplaintAdapter(List<DBHelper.Complaint> items, @Nullable OnReportRequestListener reportListener) {
        this.items = items;
        this.reportListener = reportListener;
    }

    @NonNull
    @Override
    public ComplaintVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_complaint, parent, false);
        return new ComplaintVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintVH holder, int position) {
        DBHelper.Complaint c = items.get(position);
        holder.tvComplaintText.setText(c.text);
        holder.tvComplaintStatus.setText("Статус: " + mapStatus(c.status));

        if (c.response != null && !c.response.isEmpty()) {
            holder.tvComplaintResponse.setVisibility(View.VISIBLE);
            holder.tvComplaintResponse.setText("Ответ администрации: " + c.response);
        } else {
            holder.tvComplaintResponse.setVisibility(View.VISIBLE);
            holder.tvComplaintResponse.setText("Ответ администрации: пока не дан");
        }

        if (c.district != null && !c.district.isEmpty()) {
            holder.tvComplaintDistrict.setText("Район: " + c.district);
            holder.tvComplaintDistrict.setVisibility(View.VISIBLE);
        } else {
            holder.tvComplaintDistrict.setVisibility(View.GONE);
        }

        if (c.address != null && !c.address.isEmpty()) {
            holder.tvComplaintAddress.setText("Адрес: " + c.address);
            holder.tvComplaintAddress.setVisibility(View.VISIBLE);
        } else {
            holder.tvComplaintAddress.setVisibility(View.GONE);
        }

        if (c.serviceType != null && !c.serviceType.isEmpty()) {
            holder.tvComplaintService.setText("Служба: " + c.serviceType);
            holder.tvComplaintService.setVisibility(View.VISIBLE);
        } else {
            holder.tvComplaintService.setVisibility(View.GONE);
        }

        boolean canGetReport = ComplaintReportHelper.isCompleted(c.status) && reportListener != null;
        holder.btnGetReport.setVisibility(canGetReport ? View.VISIBLE : View.GONE);
        holder.btnGetReport.setOnClickListener(v -> {
            if (reportListener != null) {
                reportListener.onReportRequested(c);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ComplaintVH extends RecyclerView.ViewHolder {
        TextView tvComplaintText;
        TextView tvComplaintStatus;
        TextView tvComplaintResponse;
        TextView tvComplaintDistrict;
        TextView tvComplaintAddress;
        TextView tvComplaintService;
        MaterialButton btnGetReport;

        ComplaintVH(@NonNull View itemView) {
            super(itemView);
            tvComplaintText = itemView.findViewById(R.id.tvComplaintText);
            tvComplaintStatus = itemView.findViewById(R.id.tvComplaintStatus);
            tvComplaintResponse = itemView.findViewById(R.id.tvComplaintResponse);
            tvComplaintDistrict = itemView.findViewById(R.id.tvComplaintDistrict);
            tvComplaintAddress = itemView.findViewById(R.id.tvComplaintAddress);
            tvComplaintService = itemView.findViewById(R.id.tvComplaintService);
            btnGetReport = itemView.findViewById(R.id.btnGetReport);
        }
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
