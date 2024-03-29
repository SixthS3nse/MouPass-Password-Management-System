package com.example.moupass10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class ForgotPassword extends AppCompatActivity {

    private TextInputLayout txtCode1;

    private static final String ENCRYPTION_ALGORITHM = "AES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //Fix white bar under screen
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        txtCode1 = findViewById(R.id.txtRecoveryCode1);

        MaterialButton confirm = (MaterialButton) findViewById(R.id.btnConfirm);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = getFilesDir().getPath();
                // Read the encryption key from the key file
                byte[] encryptionKey = readKeySNF(path + "/k3y2.snf");

                // Read the encrypted content from the file
                byte[] encryptedContent = readPasswordSNF(path + "/r3c0v3ry.snf");

                // Decrypt the content
                byte[] decryptedContent = decrypt(encryptedContent, encryptionKey);

                if (decryptedContent != null) {
                    // Convert the decrypted content from bytes to String
                    String decryptedText = new String(decryptedContent);

                    // Perform login validation using the decrypted text // if else function
                    if (txtCode1.getEditText().getText().toString().isEmpty()) {
                        Toast.makeText(ForgotPassword.this, "Please enter the recovery codes!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (txtCode1.getEditText().getText().toString().equals(decryptedText)) {
                            // Codes match, display a success message
                            Toast.makeText(ForgotPassword.this, "Account recovered successfully!", Toast.LENGTH_SHORT).show();

                            //Save Login Session
                            LoginSession();

                            //Redirect to Next Page
                            startActivity(new Intent(ForgotPassword.this, MainActivity.class));
                        } else {
                            //Print Login Failed
                            Toast.makeText(ForgotPassword.this, "Invalid recovery codes!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(ForgotPassword.this, "File Corrupted, Kindly Proceed with Password Login or Delete Application Data", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
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