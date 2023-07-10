package com.example.moupass10.ui.dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.moupass10.MainActivity;
import com.example.moupass10.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DashboardDetails extends AppCompatActivity {

    private TextView txtTitle;
    private TextView txtUser;
    private TextView txtPass;
    private TextView txtWebsite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_details);

        txtTitle = findViewById(R.id.txtTitle);
        txtUser = findViewById(R.id.txtUser);
        txtPass = findViewById(R.id.txtPass);
        txtWebsite = findViewById(R.id.txtWebsite);

        // Retrieve data from "content.snf" file
        String data = retrieveData();

        // Loading saved key
        byte[] encryptionKey = loadKey();

        // Split the data by commas to separate the values
        String[] values = data.split(",");

 /*       // Convert Base64 strings to encrypted byte arrays
        byte[] encryptedTitle = Base64.decode(values[1], Base64.DEFAULT);
        byte[] encryptedUser = Base64.decode(values[2], Base64.DEFAULT);
        byte[] encryptedPass = Base64.decode(values[3], Base64.DEFAULT);
        byte[] encryptedWebsite = Base64.decode(values[4], Base64.DEFAULT);*/

        //Toast.makeText(DashboardDetails.this, encryptedTitle.toString(), Toast.LENGTH_SHORT).show(); // - Test if "content.snf" is loaded (LOADED)

        // Decrypt the values
        String decryptedTitle = decrypt(values[1].getBytes(), encryptionKey);
        String decryptedUser = decrypt(values[2].getBytes(), encryptionKey);
        String decryptedPass = decrypt(values[3].getBytes(), encryptionKey);
        String decryptedWebsite = decrypt(values[4].getBytes(), encryptionKey);

        // Display the decrypted values in the TextViews
        txtTitle.setText(decryptedTitle);
        txtUser.setText(decryptedUser);
        txtPass.setText(decryptedPass);
        txtWebsite.setText(decryptedWebsite);

    }

    private String retrieveData() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            File file = new File(getFilesDir(), "content2.snf");
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }

            br.close();
            isr.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    private String decrypt(byte[] encryptedContent, byte[] encryptionKey) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(encryptionKey, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decryptedBytes = cipher.doFinal(encryptedContent);
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] loadKey() {
        try {
            File file2 = new File(getFilesDir(), "k3y3.snf");
            FileInputStream fis = new FileInputStream(file2);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String base64EncryptionKey = br.readLine();

            return Base64.decode(base64EncryptionKey, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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