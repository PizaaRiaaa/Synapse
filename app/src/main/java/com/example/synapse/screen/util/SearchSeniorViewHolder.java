package com.example.synapse.screen.util;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.synapse.R;

public class SearchSeniorViewHolder extends RecyclerView.ViewHolder {

    public TextView fullName;
    public ImageView profileImage;

    public SearchSeniorViewHolder(@NonNull View itemView) {
        super(itemView);

        fullName = itemView.findViewById(R.id.tvSenior_FullName);
        profileImage = itemView.findViewById(R.id.ivSearchSeniorProfileImage);
    }
}
