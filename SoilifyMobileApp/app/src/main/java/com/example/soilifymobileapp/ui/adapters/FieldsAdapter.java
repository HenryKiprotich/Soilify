package com.example.soilifymobileapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soilifymobileapp.R;
import com.example.soilifymobileapp.models.FieldRead;

import java.util.List;

public class FieldsAdapter extends RecyclerView.Adapter<FieldsAdapter.FieldViewHolder> {

    private List<FieldRead> fields;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(FieldRead field);
        void onDeleteClick(FieldRead field);
    }

    public FieldsAdapter(List<FieldRead> fields, OnItemClickListener listener) {
        this.fields = fields;
        this.listener = listener;
    }

    public void setFields(List<FieldRead> fields) {
        this.fields = fields;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FieldViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_field, parent, false);
        return new FieldViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FieldViewHolder holder, int position) {
        FieldRead field = fields.get(position);
        holder.bind(field, listener);
    }

    @Override
    public int getItemCount() {
        return fields.size();
    }

    static class FieldViewHolder extends RecyclerView.ViewHolder {
        private TextView tvFieldName, tvSoilType, tvCropType, tvSize;
        private Button btnEdit, btnDelete;

        public FieldViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFieldName = itemView.findViewById(R.id.textViewFieldName);
            tvSoilType = itemView.findViewById(R.id.textViewSoilType);
            tvCropType = itemView.findViewById(R.id.textViewCropType);
            tvSize = itemView.findViewById(R.id.textViewSize);
            btnEdit = itemView.findViewById(R.id.buttonEdit);
            btnDelete = itemView.findViewById(R.id.buttonDelete);
        }

        public void bind(final FieldRead field, final OnItemClickListener listener) {
            tvFieldName.setText(field.getFieldName());
            tvSoilType.setText("Soil Type: " + field.getSoilType());
            tvCropType.setText("Crop Type: " + field.getCropType());
            tvSize.setText("Size: " + field.getSizeHectares() + " ha");

            btnEdit.setOnClickListener(v -> listener.onEditClick(field));
            btnDelete.setOnClickListener(v -> listener.onDeleteClick(field));
        }
    }
}
