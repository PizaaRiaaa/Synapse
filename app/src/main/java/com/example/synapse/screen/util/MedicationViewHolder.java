package com.example.synapse.screen.util;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.synapse.R;

public class MedicationViewHolder extends RecyclerView.ViewHolder {

    public TextView Name;
    public TextView Dose;

    public MedicationViewHolder(@NonNull View itemView) {
        super(itemView);

        Name = itemView.findViewById(R.id.tvNameOfPill);
        Dose = itemView.findViewById(R.id.tvDoseOfPill);
    }
}
