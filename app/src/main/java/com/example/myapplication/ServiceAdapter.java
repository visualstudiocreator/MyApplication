package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceVH> {
    private final List<DBHelper.Service> items;
    private final Context context;
    private final String username;
    private final String role;

    public ServiceAdapter(Context context, List<DBHelper.Service> items, String username, String role) {
        this.context = context;
        this.items = items;
        this.username = username;
        this.role = role;
    }

    @NonNull
    @Override
    public ServiceVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
        return new ServiceVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceVH holder, int position) {
        DBHelper.Service s = items.get(position);
        holder.tvName.setText(s.name);
        holder.tvShort.setText(s.description);
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, ServiceDetailsActivity.class);
            i.putExtra("service_id", s.id);
            i.putExtra("name", s.name);
            i.putExtra("description", s.description);
            i.putExtra("requirements", s.requirements);
            i.putExtra("username", username);
            i.putExtra("role", role);
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ServiceVH extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvShort;
        ServiceVH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvShort = itemView.findViewById(R.id.tvShort);
        }
    }
}