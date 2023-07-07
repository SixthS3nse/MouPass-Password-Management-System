package com.example.moupass10;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moupass10.ui.settings.Backup;
import com.example.moupass10.ui.settings.ChangePassword;
import com.example.moupass10.ui.settings.ClearData;
import com.example.moupass10.ui.settings.Restore;

import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {

    private List<SettingsOption> settingsOptions;
    private Context context;

    public SettingsAdapter(List<SettingsOption> settingsOptions, Context context) {
        this.settingsOptions = settingsOptions;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_settings, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SettingsOption option = settingsOptions.get(position);

        holder.icon.setImageResource(option.getIconID());
        holder.title.setText(option.getTitle());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int clickedPosition = holder.getAdapterPosition();
                if (clickedPosition != RecyclerView.NO_POSITION) {
                    handleItemClick(clickedPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return settingsOptions.size();
    }

    private void handleItemClick(int position) {
        // Handle click event here
        // You can perform different actions based on the selected option
        switch (position) {
            case 0: // Change Password
                Intent frmChangePassword = new Intent(context, ChangePassword.class);
                context.startActivity(frmChangePassword);
                break;
            case 1: // Export/Backup
                Intent frmExportBackup = new Intent(context, Backup.class);
                context.startActivity(frmExportBackup);
                break;
            case 2: // Restore
                Intent frmRestore = new Intent(context, Restore.class);
                context.startActivity(frmRestore);
                break;
            case 3: // Delete Data
                Intent frmClearData = new Intent(context, ClearData.class);
                context.startActivity(frmClearData);
                break;
            // Add cases for other options as needed
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}
