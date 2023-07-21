package com.example.moupass10.ui.dashboard;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.moupass10.CryptoUtils;
import com.example.moupass10.Login;
import com.example.moupass10.MainActivity;
import com.example.moupass10.R;
import com.example.moupass10.databinding.ActivityMainBinding;
import com.example.moupass10.ui.settings.ChangePassword;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Form extends AppCompatActivity {

    private TextInputLayout txtTitle, txtUser, txtPass, txtWebsite;
    private MaterialButton btnSave;

    private Key secretKey;

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

        //Toolbar
        Toolbar formTool = (Toolbar) findViewById(R.id.formToolbar);
        setSupportActionBar(formTool);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Add Data");

        // Enable the Up button
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        txtTitle = findViewById(R.id.txtTitle);
        txtUser = findViewById(R.id.txtUser);
        txtPass = findViewById(R.id.txtPass);
        txtWebsite = findViewById(R.id.txtWebsite);
        btnSave = findViewById(R.id.btnConfirm);

        secretKey = loadKey("k3y3.snf");
        if (secretKey == null) {
            try {
                secretKey = CryptoUtils.generateKey();
                saveKey(secretKey, "k3y3.snf");
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData(getApplicationContext()); //or content or getActivity()
            }
        });
    }

    public void saveData(Context context) {
        try {
            String data = txtTitle.getEditText().getText().toString() + "," +
                    txtUser.getEditText().getText().toString() + "," +
                    txtPass.getEditText().getText().toString() + "," +
                    txtWebsite.getEditText().getText().toString() + "\n";
            byte[] encryptedData = CryptoUtils.doAES(Cipher.ENCRYPT_MODE, secretKey, new byte[16], data.getBytes(StandardCharsets.UTF_8));
            String encryptedString = android.util.Base64.encodeToString(encryptedData, android.util.Base64.DEFAULT);
            FileOutputStream out = openFileOutput("Data.snf", MODE_APPEND);
            out.write(encryptedString.getBytes());
            out.close();

            // Displaying Toast message
            Toast.makeText(Form.this, "Added", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Form.this, MainActivity.class));
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            // Displaying Toast message
            Toast.makeText(Form.this, "Error Occured", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveKey(Key key, String fileName) {
        byte[] keyBytes = key.getEncoded();
        String keyString = android.util.Base64.encodeToString(keyBytes, android.util.Base64.DEFAULT);

        try (FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE)) {
            fos.write(keyString.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Key loadKey(String fileName) {
        try (FileInputStream fis = openFileInput(fileName)) {
            byte[] keyBytes = new byte[fis.available()];
            int read = fis.read(keyBytes);

            if (read > 0) {
                byte[] decodedKey = android.util.Base64.decode(new String(keyBytes), android.util.Base64.DEFAULT);
                return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
            }

        } catch (Exception e) {
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
                startActivity(new Intent(Form.this, MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
