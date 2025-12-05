package com.example.soilifymobileapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.soilifymobileapp.R;
import com.example.soilifymobileapp.models.AIConversation;
import java.util.List;

public class AIAdapter extends RecyclerView.Adapter<AIAdapter.ViewHolder> {

    private final Context context;
    private final List<AIConversation> AIConversations;

    public AIAdapter(Context context, List<AIConversation> AIConversations) {
        this.context = context;
        this.AIConversations = AIConversations;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_aiconversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AIConversation AIConversation = AIConversations.get(position);
        holder.tvTitle.setText(AIConversation.getSender());
        holder.tvDescription.setText(AIConversation.getMessage());
    }

    @Override
    public int getItemCount() {
        return AIConversations.size();
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
