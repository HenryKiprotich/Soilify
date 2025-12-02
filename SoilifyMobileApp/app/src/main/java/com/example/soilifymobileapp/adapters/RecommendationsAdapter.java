package com.example.soilifymobileapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.soilifymobileapp.R;
import com.example.soilifymobileapp.models.Recommendation;
import java.util.List;

public class RecommendationsAdapter extends RecyclerView.Adapter<RecommendationsAdapter.ViewHolder> {

    private final Context context;
    private final List<Recommendation> recommendationList;

    public RecommendationsAdapter(Context context, List<Recommendation> recommendationList) {
        this.context = context;
        this.recommendationList = recommendationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recommendation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recommendation recommendation = recommendationList.get(position);
        holder.tvTitle.setText(recommendation.getTitle());
        holder.tvDescription.setText(recommendation.getDescription());
    }

    @Override
    public int getItemCount() {
        return recommendationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}
