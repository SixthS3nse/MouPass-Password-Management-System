package com.example.moupass10.ui.settings;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.moupass10.Login;
import com.example.moupass10.MainActivity;
import com.example.moupass10.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class ChangePassword extends AppCompatActivity {
    MaterialButton btnSubmit;
    private TextInputLayout txtMasterPass;
    private TextInputLayout txtConfirmPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

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
        Toolbar formTool = (Toolbar) findViewById(R.id.cpToolbar);
        setSupportActionBar(formTool);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Change Password");

        // Enable the Up button
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        //Change password form input field
        txtMasterPass = findViewById(R.id.txtMasterPass);
        txtConfirmPass = findViewById(R.id.txtConfirmPass);

        MaterialButton register = (MaterialButton) findViewById(R.id.btnSubmit);

        //Change password form input validation
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
                                Toast.makeText(ChangePassword.this, "Password Changed", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ChangePassword.this, "⚠️Error saving password!⚠️", Toast.LENGTH_SHORT).show();
                            }

                            // Save encrypted Key to CSV file
                            if (saveKey(getApplicationContext(), encryptionKey)) {
                                //Toast.makeText(ChangePassword.this, "Key saved successfully.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ChangePassword.this, "⚠️Error saving Key!⚠️", Toast.LENGTH_SHORT).show();
                            }

                            //Logout From Session
                            logoutSession();
                            onBackPressed();
                            break;
                        case 1:
                            Toast.makeText(ChangePassword.this, "Password must have more than 8 characters", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Toast.makeText(ChangePassword.this, "Password must contain at least one uppercase letter", Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            Toast.makeText(ChangePassword.this, "Password must be alphanumeric", Toast.LENGTH_SHORT).show();
                            break;
                    }
                } else {
                    Toast.makeText(ChangePassword.this, "Incorrect confirm password or no password is entered", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Handling Back button pressed
    @Override
    public void onBackPressed() {
        finish();
    }

    // Handling press on the Back button in Toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Insert the fragment transaction here to replace the current fragment
                startActivity(new Intent(ChangePassword.this, MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    private void logoutSession() {
        // Clear Session Data
        SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Example: Navigate to the login screen
        startActivity(new Intent(this, Login.class));
    }

}