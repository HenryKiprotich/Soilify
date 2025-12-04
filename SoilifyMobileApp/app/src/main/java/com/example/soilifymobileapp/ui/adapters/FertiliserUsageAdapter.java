package com.example.soilifymobileapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soilifymobileapp.R;
import com.example.soilifymobileapp.models.FertiliserUsageRead;

import java.text.SimpleDateFormat;
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
        private TextView tvFieldName, tvFertiliserType, tvAmount, tvWeather, tvNotes, tvDate;
        private ImageButton btnEdit, btnDelete;

        public FertiliserUsageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFieldName = itemView.findViewById(R.id.tvFieldName);
            tvFertiliserType = itemView.findViewById(R.id.tvFertiliserType);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvWeather = itemView.findViewById(R.id.tvWeather);
            tvNotes = itemView.findViewById(R.id.tvNotes);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(final FertiliserUsageRead usage, final OnItemClickListener listener) {
            tvFieldName.setText(usage.getFieldName());
            tvFertiliserType.setText("Fertiliser: " + usage.getFertiliserType());
            tvAmount.setText("Amount: " + usage.getAmountKg() + " kg");
            tvWeather.setText("Weather: " + usage.getWeather());
            tvNotes.setText("Notes: " + usage.getNotes());

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            tvDate.setText("Date: " + dateFormat.format(usage.getDate()));

            btnEdit.setOnClickListener(v -> listener.onEditClick(usage));
            btnDelete.setOnClickListener(v -> listener.onDeleteClick(usage));
        }
    }
}
