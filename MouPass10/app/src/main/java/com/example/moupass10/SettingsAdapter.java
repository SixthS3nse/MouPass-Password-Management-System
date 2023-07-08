package com.example.moupass10;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moupass10.ui.settings.Backup;
import com.example.moupass10.ui.settings.ChangePassword;
import com.example.moupass10.ui.settings.ClearData;
import com.example.moupass10.ui.settings.Restore;

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
            case 2: // Restore
                importFiles();
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

    //Export Files - Files will be exported to Downloads folder
    private void exportFiles() {
        File exportDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        File sourceDirectory = new File(context.getFilesDir().getAbsolutePath());
        File[] files = sourceDirectory.listFiles();

        boolean isContentSnfFound = false;
        boolean isK3y3SnfFound = false;

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    if (file.getName().equals("content.snf")) {
                        isContentSnfFound = true;
                    } else if (file.getName().equals("k3y3.snf")) {
                        isK3y3SnfFound = true;
                    }
                }
            }
        }

        if (isContentSnfFound && isK3y3SnfFound) {
            for (File file : files) {
                if (file.isFile() && (file.getName().equals("content.snf") || file.getName().equals("k3y3.snf"))) {
                    try {
                        exportFile(file, exportDirectory);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            Toast.makeText(context, "Files Exported", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "File Not Found", Toast.LENGTH_SHORT).show();
        }
    }

    private void exportFile(File file, File exportDirectory) throws IOException {
        File destinationFile = new File(exportDirectory, file.getName());
        copyExportFile(file, destinationFile);

        // Scan the file to make it visible in the device's file system
        MediaScannerConnection.scanFile(context, new String[]{destinationFile.getAbsolutePath()}, null, null);
    }

    private void copyExportFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

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

    // Import Files - Files will be imported from Downloads folder
    private void importFiles() {
        File importDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        File[] files = importDirectory.listFiles();

        boolean isContentSnfFound = false;
        boolean isK3y3SnfFound = false;

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    if (file.getName().equals("content.snf")) {
                        isContentSnfFound = true;
                    } else if (file.getName().equals("k3y3.snf")) {
                        isK3y3SnfFound = true;
                    }
                }
            }
        }

        if (isContentSnfFound && isK3y3SnfFound) {
            for (File file : files) {
                if (file.isFile() && (file.getName().equals("content.snf") || file.getName().equals("k3y3.snf"))) {
                    try {
                        importFile(file, importDirectory);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            Toast.makeText(context, "Files Imported", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "File Not Found", Toast.LENGTH_SHORT).show();
        }
    }

    private void importFile(File file, File importDirectory) throws IOException {
        importDirectory = new File(context.getFilesDir().getAbsolutePath());

        File destinationFile = new File(importDirectory, file.getName());
        copyImportFile(file, destinationFile);
    }

    private void copyImportFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

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

}
