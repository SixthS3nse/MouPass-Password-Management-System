package com.example.moupass10;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Register extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    private TextInputLayout txtMasterPass;
    private TextInputLayout txtConfirmPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //App Launch
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtMasterPass = findViewById(R.id.txtMasterPass);
        txtConfirmPass = findViewById(R.id.txtConfirmPass);

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
                            if (saveToCSV(getApplicationContext(), encryptedMasterPass)) {
                                Toast.makeText(Register.this, "Passwords saved successfully.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Register.this, "Error saving password!.", Toast.LENGTH_SHORT).show();
                            }

                            // Save encrypted Key to CSV file
                            if (saveKey(getApplicationContext(), encryptionKey)) {
                                Toast.makeText(Register.this, "Key saved successfully.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Register.this, "Error saving Key!.", Toast.LENGTH_SHORT).show();
                            }

                            //Redirect to Next Page
                            startActivity(new Intent(Register.this,Recovery.class));
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
                    Toast.makeText(Register.this, "Incorrect confirm password or no password in entered", Toast.LENGTH_SHORT).show();
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

 /*       //Password Validation
    private void PasswordValidation(String password) {
        int result = PasswordRequirements(password);

        switch (result){
            case 0:
                //Display Success Message
                Toast.makeText(Register.this,"Registered",Toast.LENGTH_SHORT).show();

*//*                //Shared Preferences
                SharedPreferences.Editor editor = getSharedPreferences("PrefsStatus", MODE_PRIVATE).edit();
                editor.putBoolean("userRegistered", true);
                editor.apply();*//*

                //Redirect to Backup Page
                startActivity(new Intent(Register.this,Recovery.class));
                break;
            case 1:
                Toast.makeText(Register.this,"Password must have moe than 8 characters",Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(Register.this,"Password must contain at least one uppercase letter",Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(Register.this,"Password must be alphanumeric",Toast.LENGTH_SHORT).show();
                break;
        }

    }*/

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

    private boolean saveToCSV(Context context, byte[] encryptedMasterPass) {
        try {
            // Convert encrypted passwords to Base64 strings
            String base64MasterPass = Base64.encodeToString(encryptedMasterPass, Base64.DEFAULT);
            //String base64ConfirmPass = Base64.encodeToString(encryptedConfirmPass, Base64.DEFAULT);
            //String base64EncryptionKey = Base64.encodeToString(encryptionKey,Base64.DEFAULT);

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
}



