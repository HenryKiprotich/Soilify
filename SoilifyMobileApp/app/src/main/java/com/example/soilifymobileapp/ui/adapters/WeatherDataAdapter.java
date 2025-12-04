package com.example.soilifymobileapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soilifymobileapp.R;
import com.example.soilifymobileapp.models.WeatherDataRead;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class WeatherDataAdapter extends RecyclerView.Adapter<WeatherDataAdapter.WeatherDataViewHolder> {

    private List<WeatherDataRead> weatherDataList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(WeatherDataRead weatherData);
        void onDeleteClick(WeatherDataRead weatherData);
    }

    public WeatherDataAdapter(List<WeatherDataRead> weatherDataList, OnItemClickListener listener) {
        this.weatherDataList = weatherDataList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WeatherDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather_data, parent, false);
        return new WeatherDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherDataViewHolder holder, int position) {
        WeatherDataRead weatherData = weatherDataList.get(position);
        holder.bind(weatherData, listener);
    }

    @Override
    public int getItemCount() {
        return weatherDataList.size();
    }

    static class WeatherDataViewHolder extends RecyclerView.ViewHolder {
        private TextView tvFieldName, tvTemperature, tvRainfall, tvSoilMoisture, tvDate;
        private ImageButton btnEdit, btnDelete;

        public WeatherDataViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFieldName = itemView.findViewById(R.id.tvFieldName);
            tvTemperature = itemView.findViewById(R.id.tvTemperature);
            tvRainfall = itemView.findViewById(R.id.tvRainfall);
            tvSoilMoisture = itemView.findViewById(R.id.tvSoilMoisture);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(final WeatherDataRead weatherData, final OnItemClickListener listener) {
            tvFieldName.setText(weatherData.getFieldName());
            tvTemperature.setText("Temperature: " + weatherData.getTemperature() + "°C");
            tvRainfall.setText("Rainfall: " + weatherData.getRainfall() + " mm");
            tvSoilMoisture.setText("Soil Moisture: " + weatherData.getSoilMoisture() + "%");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
            tvDate.setText("Recorded on: " + dateFormat.format(weatherData.getCreatedAt()));

            btnEdit.setOnClickListener(v -> listener.onEditClick(weatherData));
            btnDelete.setOnClickListener(v -> listener.onDeleteClick(weatherData));
        }
    }
}
