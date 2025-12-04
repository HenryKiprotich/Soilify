package com.example.soilifymobileapp.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soilifymobileapp.R;
import com.example.soilifymobileapp.models.AlertRead;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AlertsAdapter extends RecyclerView.Adapter<AlertsAdapter.AlertViewHolder> {

    private List<AlertRead> alerts;
    private Context context;

    public AlertsAdapter(Context context, List<AlertRead> alerts) {
        this.context = context;
        this.alerts = alerts;
    }

    @NonNull
    @Override
    public AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alert, parent, false);
        return new AlertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertViewHolder holder, int position) {
        AlertRead alert = alerts.get(position);
        holder.bind(alert);
    }

    @Override
    public int getItemCount() {
        return alerts.size();
    }

    static class AlertViewHolder extends RecyclerView.ViewHolder {
        private TextView textMessage, textCreatedAt, tvFieldName, textTimeAgo;

        public AlertViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);
            textCreatedAt = itemView.findViewById(R.id.textTimeAgo);
            tvFieldName = itemView.findViewById(R.id.textFieldName);
        }

        public void bind(final AlertRead alert) {
            textMessage.setText(alert.getMessage());
            if (alert.getFieldName() != null) {
                tvFieldName.setText(alert.getFieldName());
                tvFieldName.setVisibility(View.VISIBLE);
            } else {
                tvFieldName.setVisibility(View.GONE);
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
            textCreatedAt.setText("Created on: " + dateFormat.format(alert.getCreatedAt()));
        }
    }
}
