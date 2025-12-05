package com.example.soilifymobileapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soilifymobileapp.R;
import com.example.soilifymobileapp.models.Field;

import java.util.List;

public class FieldsAdapter extends RecyclerView.Adapter<FieldsAdapter.FieldViewHolder> {

    private List<Field> fieldList;
    private Context context;
    private OnFieldListener onFieldListener;

    public interface OnFieldListener {
        void onEditClick(Field field);
        void onDeleteClick(Field field);
    }

    public FieldsAdapter(Context context, List<Field> fieldList, OnFieldListener onFieldListener) {
        this.context = context;
        this.fieldList = fieldList;
        this.onFieldListener = onFieldListener;
    }

    @NonNull
    @Override
    public FieldViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_field, parent, false);
        return new FieldViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FieldViewHolder holder, int position) {
        Field field = fieldList.get(position);
        holder.textViewFieldName.setText(field.getFieldName());
        holder.textViewSoilType.setText("Soil Type: " + field.getSoilType());
        holder.textViewCropType.setText("Crop Type: " + field.getCropType());
        holder.textViewSize.setText("Size: " + field.getSizeHectares() + " hectares");

        holder.buttonEdit.setOnClickListener(v -> onFieldListener.onEditClick(field));
        holder.buttonDelete.setOnClickListener(v -> onFieldListener.onDeleteClick(field));
    }

    @Override
    public int getItemCount() {
        return fieldList.size();
    }

    public void setFields(List<Field> fields) {
        this.fieldList = fields;
        notifyDataSetChanged();
    }

    public static class FieldViewHolder extends RecyclerView.ViewHolder {
        TextView textViewFieldName, textViewSoilType, textViewCropType, textViewSize;
        Button buttonEdit, buttonDelete;

        public FieldViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFieldName = itemView.findViewById(R.id.textViewFieldName);
            textViewSoilType = itemView.findViewById(R.id.textViewSoilType);
            textViewCropType = itemView.findViewById(R.id.textViewCropType);
            textViewSize = itemView.findViewById(R.id.textViewSize);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
