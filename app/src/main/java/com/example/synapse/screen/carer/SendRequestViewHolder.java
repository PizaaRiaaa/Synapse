package com.example.synapse.screen.carer;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.synapse.R;

public class SendRequestViewHolder extends RecyclerView.ViewHolder {

    TextView fullname;

    public SendRequestViewHolder(@NonNull View itemView) {
        super(itemView);

        fullname = itemView.findViewById(R.id.tvSenior_FullName);
    }
}