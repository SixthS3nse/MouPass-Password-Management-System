package com.example.moupass10.ui.dashboard;

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

public class Form extends AppCompatActivity {

    private TextInputLayout txtTitle;
    private TextInputLayout txtUser;
    private TextInputLayout txtPass;
    private TextInputLayout txtWebsite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

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

        txtTitle = findViewById(R.id.txtTitle);
        txtUser = findViewById(R.id.txtUser);
        txtPass = findViewById(R.id.txtPass);
        txtWebsite = findViewById(R.id.txtWebsite);

        //Toolbar
        Toolbar formTool = (Toolbar) findViewById(R.id.formToolbar);
        setSupportActionBar(formTool);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Enable the Up button
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        MaterialButton confirm = (MaterialButton) findViewById(R.id.btnConfirm );

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = txtTitle.getEditText().getText().toString();
                String user = txtUser.getEditText().getText().toString();
                String pass = txtPass.getEditText().getText().toString();
                String website = txtWebsite.getEditText().getText().toString();

                if (!title.isEmpty() && !user.isEmpty() && !pass.isEmpty() && !website.isEmpty()) {
                    // Generate random encryption key
                    byte[] encryptionKey = generateEncryptionKey();

                    // Encrypt the passwords
                    byte[] encryptedTitle = encrypt(title, encryptionKey);
                    byte[] encryptedUser = encrypt(user, encryptionKey);
                    byte[] encryptedPass = encrypt(pass, encryptionKey);
                    byte[] encryptedWebsite = encrypt(website, encryptionKey);

                    // Save encrypted content to CSV file
                    if (saveToCSV(getApplicationContext(), encryptedTitle, encryptedUser, encryptedPass, encryptedWebsite)) {
                        Toast.makeText(Form.this, "Added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Form.this, "⚠️Fail Adding Entry!⚠️", Toast.LENGTH_SHORT).show();
                    }

                    // Save encrypted Key to CSV file
                    if (saveKey(getApplicationContext(), encryptionKey)) {
                        //Toast.makeText(Register.this, "Key saved successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Form.this, "⚠️Error Saving Key!⚠️", Toast.LENGTH_SHORT).show();
                    }

                    //Redirect to Dashboard
                    startActivity(new Intent(Form.this, MainActivity.class));
                } else {
                    Toast.makeText(Form.this, "Kindly fill in all field", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Handling press on the Back button in Toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Insert the fragment transaction here to replace the current fragment
                startActivity(new Intent(Form.this, MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Storing Data into txt file
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

    private byte[] encrypt(String content, byte[] encryptionKey) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(encryptionKey, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            return cipher.doFinal(content.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean saveToCSV(Context context, byte[] encryptedTitle, byte[] encryptedUser, byte[] encryptedPass, byte[] encryptedWebsite) {
        try {
            // Convert encrypted content to Base64 strings
            String base64Title = Base64.encodeToString(encryptedTitle, Base64.DEFAULT);
            String base64User = Base64.encodeToString(encryptedUser, Base64.DEFAULT);
            String base64Pass = Base64.encodeToString(encryptedPass, Base64.DEFAULT);
            String base64Website = Base64.encodeToString(encryptedWebsite, Base64.DEFAULT);


            // Concatenate the encrypted passwords
            String csvData = "," + base64Title + "," + base64User + "," + base64Pass + "," + base64Website + "\n";

            // Create a file stream for writing
            FileOutputStream fos = context.openFileOutput("content.snf", Context.MODE_APPEND);
            fos.write(csvData.getBytes());
            fos.write("\r\n".getBytes());
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
            FileOutputStream fos = context.openFileOutput("k3y3.snf", Context.MODE_PRIVATE);
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