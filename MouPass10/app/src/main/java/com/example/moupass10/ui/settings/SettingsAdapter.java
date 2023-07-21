package com.example.moupass10.ui.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moupass10.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
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

    //Handle When Each Setting Clicked

    private void handleItemClick(int position) {
        // Handle click event here
        // You can perform different actions based on the selected option
        switch (position) {
            case 0: // Change Password
                Intent frmChangePassword = new Intent(context, ChangePassword.class);
                context.startActivity(frmChangePassword);
                break;
            case 1: // Export/Backup
                exportFiles();
                break;
            case 2: // Delete Data
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirm Deletion");
                builder.setMessage("Are you sure you want to delete the files? This action cannot be undone.");

                // Add positive button with confirmation action
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteSNF();
                    }
                });

                // Add negative button to cancel the deletion
                builder.setNegativeButton("Cancel", null);

                AlertDialog dialog = builder.create();
                dialog.show();
                break;
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

    //Export Files - Files will be exported to Downloads folder
    private void exportFiles() {
        File exportDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        String[] targetFiles = {"p@ssw0rds.snf", "k3y.snf", "r3c0v3ry.snf", "k3y2.snf", "k3y3.snf", "Data.snf"};

        for (String targetFile : targetFiles) {
            File fileToExport = new File(context.getFilesDir(), targetFile);
            if (fileToExport.exists()) {
                try {
                    File destinationFile = new File(exportDirectory, fileToExport.getName());
                    copyExportSNF(fileToExport, destinationFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, "File " + targetFile + " Not Found", Toast.LENGTH_SHORT).show();
            }
        }

        Toast.makeText(context, "Files Exported", Toast.LENGTH_SHORT).show();
    }

    private void copyExportSNF(File sourceFile, File destFile) throws IOException {
        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }
    private void deleteSNF() {
        try {
            File filesDirectory = context.getFilesDir();
            File contentFile = new File(filesDirectory, "Data.snf");
            File k3y3File = new File(filesDirectory, "k3y3.snf");

            if (contentFile.exists() && k3y3File.exists()) {
                if (contentFile.delete() && k3y3File.delete()) {
                    Toast.makeText(context, "Data Deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Error Occured", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Data Not Found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "An error occurred while deleting data", Toast.LENGTH_SHORT).show();
        }
    }


}
