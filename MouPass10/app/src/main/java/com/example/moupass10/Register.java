package com.example.moupass10;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.navigation.ui.AppBarConfiguration;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import android.os.Environment;
import android.util.Base64;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.security.SecureRandom;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import android.content.SharedPreferences;

public class Register extends AppCompatActivity {

    private static final String PREFS_NAME = "Register";
    private static final String KEY_REGISTERED = "userRegistered";

    private TextInputLayout txtMasterPass;
    private TextInputLayout txtConfirmPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //App Launch
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Fix white bar under screen
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        txtMasterPass = findViewById(R.id.txtMasterPass);
        txtConfirmPass = findViewById(R.id.txtConfirmPass);
        TextView imp = (TextView) findViewById(R.id.lblImport);

        MaterialButton register = (MaterialButton) findViewById(R.id.btnRegister);
        FloatingActionButton info = (FloatingActionButton) findViewById(R.id.btnInfo);

        //Register Page Input Validation
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String masterPass = txtMasterPass.getEditText().getText().toString();
                String confirmPass = txtConfirmPass.getEditText().getText().toString();
                int result = PasswordRequirements(masterPass);

                if (!masterPass.isEmpty() && masterPass.equals(confirmPass)) {
                    switch (result) {
                        case 0:
                            // Generate random encryption key
                            byte[] encryptionKey = generateEncryptionKey();

                            // Encrypt the passwords
                            byte[] encryptedMasterPass = encrypt(masterPass, encryptionKey);
                            //byte[] encryptedConfirmPass = encrypt(confirmPass, encryptionKey);

                            // Save encrypted passwords to CSV file
                            if (saveToSNF(getApplicationContext(), encryptedMasterPass)) {
                                Toast.makeText(Register.this, "Registered", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Register.this, "⚠️Error saving password!⚠️", Toast.LENGTH_SHORT).show();
                            }

                            // Save encrypted Key to CSV file
                            if (saveKey(getApplicationContext(), encryptionKey)) {
                                //Toast.makeText(Register.this, "Key saved successfully.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Register.this, "⚠️Error saving Key!⚠️", Toast.LENGTH_SHORT).show();
                            }

                            //Save Registration Status
                            saveRegistrationStatus();

                            //Redirect to Next Page
                            startActivity(new Intent(Register.this,Recovery.class));
                            finish();
                            break;
                        case 1:
                            Toast.makeText(Register.this, "Password must have more than 8 characters", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Toast.makeText(Register.this, "Password must contain at least one uppercase letter", Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            Toast.makeText(Register.this, "Password must be alphanumeric", Toast.LENGTH_SHORT).show();
                            break;
                    }
                } else {
                    Toast.makeText(Register.this, "Incorrect confirm password or no password is entered", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Information Button
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Register.this,"⚠️Stored data cannot be recovered the master password is lost⚠️",Toast.LENGTH_LONG).show();
            }
        });

        //Import Data
        imp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                builder.setTitle("Import Data");
                builder.setMessage("Kindly make sure the data files are saved in download folder before importing the data");

                // Add positive button with confirmation action
                builder.setPositiveButton("Import", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        importFiles();
                    }
                });

                // Add negative button to cancel Import
                builder.setNegativeButton("Cancel", null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    //Password Requirements
    private int PasswordRequirements(String password) {
        // Check if password has more than 8 characters
        if (password.length() < 8) {
            return 1;
        }

        // Check if password has at least one uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            return 2;
        }

        // Check if password is alphanumeric
        if (!password.matches("[a-zA-Z0-9]*")) {
            return 3;
        }

        // Password meets all requirements
        return 0;
    }

    //Storing Password into txt file
    private byte[] generateEncryptionKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256, new SecureRandom());
            SecretKey secretKey = keyGenerator.generateKey();
            return secretKey.getEncoded();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] encrypt(String password, byte[] encryptionKey) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(encryptionKey, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            return cipher.doFinal(password.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean saveToSNF(Context context, byte[] encryptedMasterPass) {
        try {
            // Convert encrypted passwords to Base64 strings
            String base64MasterPass = Base64.encodeToString(encryptedMasterPass, Base64.DEFAULT);

            // Concatenate the encrypted passwords
            String csvData = base64MasterPass + "\n";

            // Create a file stream for writing
            FileOutputStream fos = context.openFileOutput("p@ssw0rds.snf", Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos);

            // Write the encrypted passwords to the CSV file
            osw.write(csvData);

            // Close the file stream
            osw.close();
            fos.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean saveKey(Context context, byte [] encryptionKey) {
        try {
            // Convert encrypted Key to Base64 strings
            String base64EncryptionKey = Base64.encodeToString(encryptionKey,Base64.DEFAULT);

            // Concatenate the encrypted passwords
            String csvData = base64EncryptionKey + "\n";

            // Create a file stream for writing
            FileOutputStream fos = context.openFileOutput("k3y.snf", Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos);

            // Write the encrypted passwords to the CSV file
            osw.write(csvData);

            // Close the file stream
            osw.close();
            fos.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    private void saveRegistrationStatus() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_REGISTERED, true);
        editor.apply();
    }

    // Import Files - Files will be imported from Downloads folder
    private void importFiles() {
        File importDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        File[] files = importDirectory.listFiles();

        HashMap<String, Boolean> fileFoundStatus = new HashMap<>();
        String[] targetFiles = {"p@ssw0rds.snf", "k3y.snf", "r3c0v3ry.snf", "k3y2.snf", "k3y3.snf", "Data.snf"};

        for (String targetFile : targetFiles) {
            fileFoundStatus.put(targetFile, false);
        }

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    if (fileFoundStatus.containsKey(file.getName())) {
                        fileFoundStatus.put(file.getName(), true);
                    }
                }
            }
        }

        for (File file : files) {
            if (file.isFile() && fileFoundStatus.containsKey(file.getName())) {
                try {
                    importSNF(file, importDirectory);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        for (String targetFile : targetFiles) {
            if (!fileFoundStatus.get(targetFile)) {
                Toast.makeText(this, "File " + targetFile + " Not Found", Toast.LENGTH_SHORT).show();
            }
        }

        Toast.makeText(this, "Import Completed, Kindly Restart The Application", Toast.LENGTH_SHORT).show();
        //Save Registration Status
        saveRegistrationStatus();
        //Save Login Status
        LoginSession();
        //End Activity
        finish();
    }

    private void importSNF(File file, File importDirectory) throws IOException {
        importDirectory = new File(this.getFilesDir().getAbsolutePath());

        File destinationFile = new File(importDirectory, file.getName());
        copyImportSNF(file, destinationFile);
    }

    private void copyImportSNF(File sourceFile, File destFile) throws IOException {
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

    private void LoginSession() {
        SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.apply();
    }
}



