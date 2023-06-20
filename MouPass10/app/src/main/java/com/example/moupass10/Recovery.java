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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
public class Recovery extends AppCompatActivity {
    private static final int CODE_LENGTH = 8;
    private static final int NUM_CODES = 1;
    private static final String CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String CSV_FILENAME = "rec0v3ry_c0des.snf";

    private TextView txtCode1;
    private TextView txtCode2;

    private MaterialButton btnProceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);

        txtCode1 = findViewById(R.id.txtCode1);
        txtCode2 = findViewById(R.id.txtCode2);

        // Generate and save recovery codes
        GenerateSaveCodes(getApplicationContext());

        //Proceed Button
        btnProceed = findViewById(R.id.btnProceed);

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Recovery.this,Login.class));
            }
        });
    }

    private void GenerateSaveCodes(Context context) {
        String code1 = GenerateRecoveryCode();
        String code2 = GenerateRecoveryCode();

        // Display codes in TextViews
        txtCode1.setText(code1);
        txtCode2.setText(code2);

        // Generate a random encryption key
        SecretKey encryptionKey = GenerateEncryptionKey();

        // Save codes to encrypted CSV file
        WriteToCSV(context, code1, code2, encryptionKey);
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

    private SecretKey GenerateEncryptionKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            return keyGenerator.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void WriteToCSV(Context context, String code1, String code2, SecretKey encryptionKey) {
        try {
            // Create the CSV file in the app's private directory
            File privateDir = context.getExternalFilesDir(null);
            if (privateDir == null) {
                return; // Unable to access external storage
            }

            File csvFile = new File(privateDir, CSV_FILENAME);

            // Encrypt the codes
            String encryptedCode1 = CodeEncryption(code1, encryptionKey);
            String encryptedCode2 = CodeEncryption(code2, encryptionKey);

            // Write the encrypted codes to the CSV file
            FileOutputStream fos = new FileOutputStream(csvFile);
            OutputStreamWriter writer = new OutputStreamWriter(fos);

            writer.write(encryptedCode1);
            writer.write(encryptedCode2);

            writer.flush();
            writer.close();

            // Display a success message
            Toast.makeText(context, "Recovery codes saved successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            // Display an error message
            Toast.makeText(context, "Error saving recovery codes!", Toast.LENGTH_SHORT).show();
        }
    }

    private String CodeEncryption(String code, SecretKey encryptionKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey);
            byte[] encryptedBytes = cipher.doFinal(code.getBytes());
            return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}