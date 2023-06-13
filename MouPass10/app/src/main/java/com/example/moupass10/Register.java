package com.example.moupass10;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //App Launch
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Register Variable
        TextInputLayout password = (TextInputLayout) findViewById(R.id.txtMasterPass);
        TextInputLayout cpassword = (TextInputLayout) findViewById(R.id.txtConfirmPass);

        MaterialButton register = (MaterialButton) findViewById(R.id.btnRegister);

        FloatingActionButton info = (FloatingActionButton) findViewById(R.id.btnInfo);

        //Register Page Input Validation
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(password.getEditText().getText().toString().equals(cpassword.getEditText().getText().toString())) {
                    //Passphrase Input Validation - Call Method
                    PasswordValidation(password.getEditText().getText().toString());
                }else
                    //Prompt Password Not Same
                    Toast.makeText(Register.this,"Confirm Password is incorrect",Toast.LENGTH_SHORT).show();

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

    //Password Validation
    private void PasswordValidation(String password) {
        int result = PasswordRequirements(password);

        switch (result){
            case 0:
                Toast.makeText(Register.this,"Registered",Toast.LENGTH_SHORT).show();
                //Shared Preferences
                SharedPreferences.Editor editor = getSharedPreferences("Shared", MODE_PRIVATE).edit();
                editor.putBoolean("userRegistered", true);
                editor.apply();
                //Store Master Password into file
                //Redirect to Backup Page
                startActivity(new Intent(Register.this,Recovery.class));
                break;
            case 1:
                Toast.makeText(Register.this,"Password must have more than 8 characters",Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(Register.this,"Password must contain at least one uppercase letter",Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(Register.this,"Password must be alphanumeric",Toast.LENGTH_SHORT).show();
                break;
        }

    }

    //Storing Password into txt file
}


