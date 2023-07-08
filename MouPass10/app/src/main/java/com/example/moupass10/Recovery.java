package com.example.moupass10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Context;
import android.os.Bundle;
import android.util.Base64;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Recovery extends AppCompatActivity {
    private static final int CODE_LENGTH = 8;
    private static final String CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private TextView txtCode1;

    private MaterialButton btnProceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);

        //Fix white bar under screen
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        txtCode1 = findViewById(R.id.txtCode1);
        FloatingActionButton info = (FloatingActionButton) findViewById(R.id.btnInfo1);

        // Generate and save recovery codes
        GenerateSaveCodes(getApplicationContext());
        Toast.makeText(this, "⚠️Kindly note down the recovery code!⚠️", Toast.LENGTH_SHORT).show();

        //Proceed Button
        btnProceed = findViewById(R.id.btnProceed);

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Recovery.this,Login.class));
                finish();
            }
        });

        //Information Button
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Recovery.this,"⚠️Note down the recovery code for account recovery⚠️",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void GenerateSaveCodes(Context context) {
        String code1 = GenerateRecoveryCode();

        // Display codes in TextViews
        txtCode1.setText(code1);

        // Generate random encryption key
        byte[] encryptionKey = generateEncryptionKey();

        // Encrypt the passwords
        byte[] encryptedCode1 = encrypt(code1, encryptionKey);

        // Save encrypted passwords to CSV file
        if (saveToCSV(getApplicationContext(), encryptedCode1)) {
            //Toast.makeText(Recovery.this, "Code saved successfully.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Recovery.this, "Error saving Code!.", Toast.LENGTH_SHORT).show();
        }

        // Save encrypted Key to CSV file
        if (saveKey(getApplicationContext(), encryptionKey)) {
            //Toast.makeText(Recovery.this, "Key saved successfully.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Recovery.this, "Error saving Key!.", Toast.LENGTH_SHORT).show();
        }
    }

    private String GenerateRecoveryCode() {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = secureRandom.nextInt(CHARSET.length());
            code.append(CHARSET.charAt(randomIndex));
        }

        return code.toString();
    }

    //Storing Recovery Key into txt file
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

    private byte[] encrypt(String code, byte[] encryptionKey) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(encryptionKey, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            return cipher.doFinal(code.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean saveToCSV(Context context, byte[] encryptedCode1) {
        try {
            // Convert encrypted passwords to Base64 strings
            String base64Code1 = Base64.encodeToString(encryptedCode1, Base64.DEFAULT);

            // Concatenate the encrypted passwords
            String csvData = base64Code1 + "\n";

            // Create a file stream for writing
            FileOutputStream fos = context.openFileOutput("r3c0v3ry.snf", Context.MODE_PRIVATE);
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
            FileOutputStream fos = context.openFileOutput("k3y2.snf", Context.MODE_PRIVATE);
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