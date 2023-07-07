package com.example.moupass10;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder{
    public TextView titleTextView;
    public TextView usernameTextView;
    public TextView passwordTextView;
    public TextView websiteTextView;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);

        titleTextView = itemView.findViewById(R.id.txtTitle);
        usernameTextView = itemView.findViewById(R.id.txtUser);
        passwordTextView = itemView.findViewById(R.id.txtPass);
        websiteTextView = itemView.findViewById(R.id.txtWebsite);
    }
}