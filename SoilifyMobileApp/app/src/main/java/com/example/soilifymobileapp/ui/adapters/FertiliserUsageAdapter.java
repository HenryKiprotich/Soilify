package com.example.soilifymobileapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soilifymobileapp.R;
import com.example.soilifymobileapp.models.FertiliserUsageRead;

import java.util.List;
import java.util.Locale;

public class FertiliserUsageAdapter extends RecyclerView.Adapter<FertiliserUsageAdapter.FertiliserUsageViewHolder> {

    private List<FertiliserUsageRead> usageList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(FertiliserUsageRead usage);
        void onDeleteClick(FertiliserUsageRead usage);
    }

    public FertiliserUsageAdapter(List<FertiliserUsageRead> usageList, OnItemClickListener listener) {
        this.usageList = usageList;
        this.listener = listener;
    }

    public void setUsageList(List<FertiliserUsageRead> usageList) {
        this.usageList = usageList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FertiliserUsageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fertiliser_usage, parent, false);
        return new FertiliserUsageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FertiliserUsageViewHolder holder, int position) {
        FertiliserUsageRead usage = usageList.get(position);
        holder.bind(usage, listener);
    }

    @Override
    public int getItemCount() {
        return usageList.size();
    }

    static class FertiliserUsageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvFieldName, tvFertiliserType, tvAmount, tvDate, tvWeather, tvNotes;
        private Button btnEdit, btnDelete;

        public FertiliserUsageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFieldName = itemView.findViewById(R.id.tvFieldName);
            tvFertiliserType = itemView.findViewById(R.id.tvFertiliserType);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvWeather = itemView.findViewById(R.id.tvWeather);
            tvNotes = itemView.findViewById(R.id.tvNotes);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(final FertiliserUsageRead usage, final OnItemClickListener listener) {
            tvFieldName.setText(usage.getFieldName());
            tvFertiliserType.setText("Fertiliser: " + usage.getFertiliserType());
            tvAmount.setText(String.format(Locale.getDefault(), "Amount: %.2f kg", usage.getAmountKg()));
            
            // Date is already a formatted string (YYYY-MM-DD) from the API
            String dateStr = usage.getDate() != null ? usage.getDate() : "N/A";
            tvDate.setText("Date: " + dateStr);

            tvWeather.setText("Weather: " + (usage.getWeather() != null ? usage.getWeather() : "N/A"));
            tvNotes.setText("Notes: " + (usage.getNotes() != null ? usage.getNotes() : "N/A"));

            btnEdit.setOnClickListener(v -> listener.onEditClick(usage));
            btnDelete.setOnClickListener(v -> listener.onDeleteClick(usage));
        }
    }
}
