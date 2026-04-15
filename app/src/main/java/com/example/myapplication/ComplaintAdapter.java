package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ComplaintVH> {
    private final List<DBHelper.Complaint> items;

    public ComplaintAdapter(List<DBHelper.Complaint> items) {
        this.items = items;
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
        String statusRu = mapStatus(c.status);
        holder.tvComplaintStatus.setText("Статус: " + statusRu);
        String resp = (c.response == null || c.response.isEmpty()) ? "Ответ пока не дан" : c.response;
        holder.tvComplaintResponse.setText("Ответ: " + resp);
        
        // Отображение новой информации
        if (c.district != null && !c.district.isEmpty()) {
            holder.tvComplaintDistrict.setText("Район: " + c.district);
            holder.tvComplaintDistrict.setVisibility(android.view.View.VISIBLE);
        } else {
            holder.tvComplaintDistrict.setVisibility(android.view.View.GONE);
        }
        
        if (c.address != null && !c.address.isEmpty()) {
            holder.tvComplaintAddress.setText("Адрес: " + c.address);
            holder.tvComplaintAddress.setVisibility(android.view.View.VISIBLE);
        } else {
            holder.tvComplaintAddress.setVisibility(android.view.View.GONE);
        }
        
        if (c.serviceType != null && !c.serviceType.isEmpty()) {
            holder.tvComplaintService.setText("Служба: " + c.serviceType);
            holder.tvComplaintService.setVisibility(android.view.View.VISIBLE);
        } else {
            holder.tvComplaintService.setVisibility(android.view.View.GONE);
        }
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
        ComplaintVH(@NonNull View itemView) {
            super(itemView);
            tvComplaintText = itemView.findViewById(R.id.tvComplaintText);
            tvComplaintStatus = itemView.findViewById(R.id.tvComplaintStatus);
            tvComplaintResponse = itemView.findViewById(R.id.tvComplaintResponse);
            tvComplaintDistrict = itemView.findViewById(R.id.tvComplaintDistrict);
            tvComplaintAddress = itemView.findViewById(R.id.tvComplaintAddress);
            tvComplaintService = itemView.findViewById(R.id.tvComplaintService);
        }
    }

    private String mapStatus(String s) {
        if (s == null || s.isEmpty()) return "Открыта";
        String v = s.toLowerCase();
        if (v.equals("open")) return "Открыта";
        if (v.equals("answered")) return "Отвечена";
        return s;
    }
}