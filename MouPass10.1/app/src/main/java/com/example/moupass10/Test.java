package com.example.moupass10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;



public class Test extends AppCompatActivity {

    private EditText txtTitle, txtUser, txtPass, txtWebsite;
    private static final String FILENAME = "content.snf";
    private static final String AES = "AES";
    private static final String CIPHER_INSTANCE = "AES/CBC/PKCS5Padding";
    private static final String PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        txtTitle = findViewById(R.id.txtTitle);
        txtUser = findViewById(R.id.txtUser);
        txtPass = findViewById(R.id.txtPass);
        txtWebsite = findViewById(R.id.txtWebsite);
    }
    public void saveData(View view) {
        try {
            String title = txtTitle.getText().toString();
            String user = txtUser.getText().toString();
            String pass = txtPass.getText().toString();
            String website = txtWebsite.getText().toString();

            String combinedData = title + "," + user + "," + pass + "," + website;

            KeyGenerator keyGen = KeyGenerator.getInstance(AES);
            keyGen.init(256);
            SecretKey secretKey = keyGen.generateKey();

            SecureRandom secureRandom = new SecureRandom();
            byte[] iv = new byte[16];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE);
            SecretKeySpec skeySpec = new SecretKeySpec(secretKey.getEncoded(), AES);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParameterSpec);

            byte[] encrypted = cipher.doFinal(combinedData.getBytes(StandardCharsets.UTF_8));

            FileOutputStream fos = openFileOutput(FILENAME, MODE_PRIVATE);
            fos.write(android.util.Base64.encode(encrypted, android.util.Base64.DEFAULT));
            fos.close();

            SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("secretKey", android.util.Base64.encodeToString(secretKey.getEncoded(), android.util.Base64.DEFAULT));
            editor.putString("iv", android.util.Base64.encodeToString(iv, android.util.Base64.DEFAULT));
            editor.commit();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }
}

