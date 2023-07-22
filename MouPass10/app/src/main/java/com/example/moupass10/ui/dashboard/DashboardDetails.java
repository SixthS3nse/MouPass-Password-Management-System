package com.example.moupass10.ui.dashboard;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.moupass10.DataEncryption;
import com.example.moupass10.MainActivity;
import com.example.moupass10.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DashboardDetails extends AppCompatActivity {

    private TextInputLayout txtTitle, txtUser, txtPass, txtWebsite;
    private MaterialButton btnUpdate,btnDelete;
    private DataItem currentDataItem;
    private Key secretKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_details);

        // Check "Login" SharedPreferences value
        SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            finishAffinity();
            System.exit(0);
        }

        //Fix white bar under screen
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        //Toolbar
        Toolbar formTool = (Toolbar) findViewById(R.id.ddToolbar);
        setSupportActionBar(formTool);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Details");

        // Enable the Up button
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        txtTitle = findViewById(R.id.txtTitle);
        txtUser = findViewById(R.id.txtUser);
        txtPass = findViewById(R.id.txtPass);
        txtWebsite = findViewById(R.id.txtWebsite);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        Intent intent = getIntent();
        currentDataItem = (DataItem) intent.getSerializableExtra("dataItem");

        txtTitle.getEditText().setText(currentDataItem.getTitle());
        txtUser.getEditText().setText(currentDataItem.getUser());
        txtPass.getEditText().setText(currentDataItem.getPass());
        txtWebsite.getEditText().setText(currentDataItem.getWebsite());

        secretKey = loadKey("k3y3.snf");
        if (secretKey == null) {
            throw new RuntimeException("Cannot load secret key");
        }

        //Update Button
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtTitle.getEditText().getText().toString().isEmpty() || txtUser.getEditText().getText().toString().isEmpty() || txtPass.getEditText().getText().toString().isEmpty() || txtWebsite.getEditText().getText().toString().isEmpty()) {
                    Toast.makeText(DashboardDetails.this, "Kindly fill in all field", Toast.LENGTH_SHORT).show();
                } else {
                    updateData(getApplicationContext()); //or content or getActivity()
                }
            }
        });

        //Delete Button
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData(getApplicationContext());
            }
        });
    }

    public void updateData(Context context) {
        List<DataItem> dataItems = new ArrayList<>();
        FileInputStream fis = null;
        try {
            fis = context.openFileInput("Data.snf");
            Scanner scanner = new Scanner(fis);
            while (scanner.hasNextLine()) {
                String encryptedString = scanner.nextLine();
                byte[] decryptedBytes = DataEncryption.doAES(Cipher.DECRYPT_MODE, secretKey, new byte[16], Base64.decode(encryptedString, Base64.DEFAULT));
                String line = new String(decryptedBytes, StandardCharsets.UTF_8);
                String[] parts = line.split(",");
                dataItems.add(new DataItem(parts[0], parts[1], parts[2], parts[3]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Update the current item
        for (DataItem dataItem : dataItems) {
            if (dataItem.getTitle().equals(currentDataItem.getTitle()) && dataItem.getUser().equals(currentDataItem.getUser())
                    && dataItem.getWebsite().equals(currentDataItem.getWebsite())) {
                dataItem.setTitle(txtTitle.getEditText().getText().toString());
                dataItem.setUser(txtUser.getEditText().getText().toString());
                dataItem.setPass(txtPass.getEditText().getText().toString());
                dataItem.setWebsite(txtWebsite.getEditText().getText().toString());
                break;
            }
        }

        // Write all data items back to the file
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput("Data.snf", Context.MODE_PRIVATE);
            for (DataItem dataItem : dataItems) {
                String data = dataItem.getTitle() + "," +
                        dataItem.getUser() + "," +
                        dataItem.getPass() + "," +
                        dataItem.getWebsite();
                byte[] encryptedData = DataEncryption.doAES(Cipher.ENCRYPT_MODE, secretKey, new byte[16], data.getBytes(StandardCharsets.UTF_8));
                String encryptedString = Base64.encodeToString(encryptedData, Base64.DEFAULT);
                fos.write(encryptedString.getBytes());
            }
            Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        startActivity(new Intent(DashboardDetails.this, MainActivity.class));
        finish();
    }

    private Key loadKey(String fileName) {
        try (FileInputStream fis = openFileInput(fileName)) {
            byte[] keyBytes = new byte[fis.available()];
            int read = fis.read(keyBytes);

            if (read > 0) {
                byte[] decodedKey = Base64.decode(new String(keyBytes), Base64.DEFAULT);
                return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void deleteData(Context context) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Confirmation")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        List<DataItem> dataItems = loadData(context);

                        // Remove the current item
                        boolean isDeleted = dataItems.removeIf(dataItem -> dataItem.getTitle().equals(currentDataItem.getTitle())
                                && dataItem.getUser().equals(currentDataItem.getUser())
                                && dataItem.getWebsite().equals(currentDataItem.getWebsite()));

                        // Write all remaining data items back to the file
                        if (isDeleted) {
                            writeData(context, dataItems);
                            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error: Item not found", Toast.LENGTH_SHORT).show();
                        }

                        startActivity(new Intent(DashboardDetails.this, MainActivity.class));
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private List<DataItem> loadData(Context context) {
        List<DataItem> dataItems = new ArrayList<>();
        FileInputStream fis = null;
        try {
            fis = context.openFileInput("Data.snf");
            Scanner scanner = new Scanner(fis);
            while (scanner.hasNextLine()) {
                String encryptedString = scanner.nextLine();
                byte[] decryptedBytes = DataEncryption.doAES(Cipher.DECRYPT_MODE, secretKey, new byte[16], Base64.decode(encryptedString, Base64.DEFAULT));
                String line = new String(decryptedBytes, StandardCharsets.UTF_8);
                String[] parts = line.split(",");
                dataItems.add(new DataItem(parts[0], parts[1], parts[2], parts[3]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return dataItems;
    }

    private void writeData(Context context, List<DataItem> dataItems) {
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput("Data.snf", Context.MODE_PRIVATE);
            for (DataItem dataItem : dataItems) {
                String data = dataItem.getTitle() + "," +
                        dataItem.getUser() + "," +
                        dataItem.getPass() + "," +
                        dataItem.getWebsite();
                byte[] encryptedData = DataEncryption.doAES(Cipher.ENCRYPT_MODE, secretKey, new byte[16], data.getBytes(StandardCharsets.UTF_8));
                String encryptedString = Base64.encodeToString(encryptedData, Base64.DEFAULT);
                fos.write(encryptedString.getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Handling press on the Back button in Toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Insert the fragment transaction here to replace the current fragment
                startActivity(new Intent(DashboardDetails.this, MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
