package com.example.moupass10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.Key;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Login extends AppCompatActivity {

    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final int MAX_ATTEMPTS = 3;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Fix white bar under screen
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        MaterialButton login = (MaterialButton) findViewById(R.id.btnLogin);
        TextInputLayout txtMasterPass = (TextInputLayout) findViewById(R.id.txtMasterPass);
        FloatingActionButton info = (FloatingActionButton) findViewById(R.id.btnInfo);
        TextView forgot = (TextView) findViewById(R.id.lblForgotPass);
        sharedPreferences = getSharedPreferences("LOGIN_ATTEMPTS", MODE_PRIVATE);

        String MasterPassword = txtMasterPass.getEditText().getText().toString();

        login.setOnClickListener(new View.OnClickListener() {
            //int result = PasswordRequirements(MasterPassword);
            private String txtMasterPass;
            TextInputLayout MasterPass = (TextInputLayout) findViewById(R.id.txtMasterPass);

            @Override
            public void onClick(View v) {
                // Read the encryption key from the key file
                String keyFilePath = getFilesDir().getPath() + "/k3y.snf";
                byte[] encryptionKey = readKeySNF(keyFilePath);

                // Read the encrypted content from the file
                String contentFilePath = getFilesDir().getPath() + "/p@ssw0rds.snf";
                byte[] encryptedContent = readPasswordSNF(contentFilePath);

                // Decrypt the content
                byte[] decryptedContent = decrypt(encryptedContent, encryptionKey);

                if (decryptedContent != null) {
                    // Convert the decrypted content from bytes to String
                    String decryptedText = new String(decryptedContent);

                    // Perform login validation using the decrypted text // if else function
                    if (MasterPass.getEditText().getText().toString().equals(decryptedText)) {
                        //Print Login Successful
                        Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        //Save Login Session
                        LoginSession();
                        //Reset Attempts
                        sharedPreferences.edit().putInt("ATTEMPTS", 0).apply();
                        //Redirect to next page
                        startActivity(new Intent(Login.this, MainActivity.class));
                        finish();
                    } else {
                        int attempts = sharedPreferences.getInt("ATTEMPTS", 0) + 1;
                        if(attempts > MAX_ATTEMPTS) {
                            deleteUserData(); // delete user data if attempts >= MAX_ATTEMPTS
                            Toast.makeText(Login.this, "Login Failed, Application Data has been wiped!", Toast.LENGTH_SHORT).show();
                        } else {
                            sharedPreferences.edit().putInt("ATTEMPTS", attempts).apply();
                            Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        //Information Button
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Login.this,"⚠️Click on Forgot Password for account recovery⚠️",Toast.LENGTH_LONG).show();
            }
        });

        //Forgot Password
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, ForgotPassword.class));
            }
        });

    }

    private void deleteUserData() {
        // Delete all files in the app data directory
        File dir = getFilesDir();
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                new File(dir, aChildren).delete();
            }
        }

        // Delete all the SharedPreference files
        File sharedPreferenceFile = new File(getFilesDir().getParent() + "/shared_prefs/");
        String[] listFiles = sharedPreferenceFile.list();
        if (listFiles != null) {
            for (String file : listFiles) {
                getSharedPreferences(file.replace(".xml", ""), MODE_PRIVATE).edit().clear().apply();
            }
        }
    }

    private static byte[] readKeySNF(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String base64Key = reader.readLine();
            reader.close();
            return Base64.decode(base64Key, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] readPasswordSNF (String filename){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String base64Content = reader.readLine();
            reader.close();
            return Base64.decode(base64Content, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] decrypt ( byte[] encryptedData, byte[] encryptionKey){
        try {
            Key key = new SecretKeySpec(encryptionKey, ENCRYPTION_ALGORITHM);
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void LoginSession() {
        SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.apply();
    }
}
