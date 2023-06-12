package com.example.moupass10;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import android.os.Bundle;

import android.content.Context;
import android.util.Base64;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class Recovery extends AppCompatActivity {
    //Recovery Code Variable
    private static final int CODE_LENGTH = 8;
    private static final int NUM_CODES = 10;
    private static final String CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String CSV_FILENAME = "recovery_codes.csv";

    private TextView txtCode1;
    private TextView txtCode2;

    private void generateAndSaveRecoveryCodes(Context context) {
        Set<String> codesSet1 = generateRecoveryCodes();
        Set<String> codesSet2 = generateRecoveryCodes();

        // Display codes in TextViews
        txtCode1.setText(convertSetToString(codesSet1));
        txtCode2.setText(convertSetToString(codesSet2));

        // Generate a random encryption key
        SecretKey encryptionKey = generateEncryptionKey();

        // Save codes to encrypted CSV file
        saveCodesToCSV(context, codesSet1, codesSet2, encryptionKey);
    }

    private Set<String> generateRecoveryCodes() {
        Set<String> codesSet = new HashSet<>();
        SecureRandom secureRandom = new SecureRandom();

        while (codesSet.size() < NUM_CODES) {
            StringBuilder code = new StringBuilder(CODE_LENGTH);

            for (int i = 0; i < CODE_LENGTH; i++) {
                int randomIndex = secureRandom.nextInt(CHARSET.length());
                code.append(CHARSET.charAt(randomIndex));
            }

            codesSet.add(code.toString());
        }

        return codesSet;
    }

    private String convertSetToString(Set<String> codeSet) {
        StringBuilder builder = new StringBuilder();

        for (String code : codeSet) {
            builder.append(code).append("\n");
        }

        return builder.toString();
    }

    private SecretKey generateEncryptionKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            return keyGenerator.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveCodesToCSV(Context context, Set<String> codesSet1, Set<String> codesSet2, SecretKey encryptionKey) {
        try {
            // Create the CSV file in the app's private directory
            File privateDir = context.getExternalFilesDir(null);
            if (privateDir == null) {
                return; // Unable to access external storage
            }

            File csvFile = new File(privateDir, CSV_FILENAME);

            // Encrypt the codes
            String encryptedCodes1 = encryptCodes(convertSetToString(codesSet1), encryptionKey);
            String encryptedCodes2 = encryptCodes(convertSetToString(codesSet2), encryptionKey);

            // Write the encrypted codes to the CSV file
            FileOutputStream fos = new FileOutputStream(csvFile);
            OutputStreamWriter writer = new OutputStreamWriter(fos);

            writer.write(encryptedCodes1);
            writer.write(encryptedCodes2);

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

    private String encryptCodes(String codes, SecretKey encryptionKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey);
            byte[] encryptedBytes = cipher.doFinal(codes.getBytes());
            return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);

        txtCode1 = findViewById(R.id.txtCode1);
        txtCode2 = findViewById(R.id.txtCode2);

        //Call Recovery Codes and Save to .csv file method
        generateAndSaveRecoveryCodes(this);
    }
}