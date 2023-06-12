package com.example.moupass10;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.navigation.ui.AppBarConfiguration;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

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
/*        // Check if password fulfill the requirements
        if (password.length() < 8) {
            Toast.makeText(MainActivity.this,"Password must have more than 8 alphanumeric with capital letter",Toast.LENGTH_LONG).show();
            return;
        } else {
            Toast.makeText(MainActivity.this, "Registered", Toast.LENGTH_SHORT).show();
            //Proceed to next form
        }*/
        int result = PasswordRequirements(password);

        switch (result){
            case 0:
                Toast.makeText(Register.this,"Registered",Toast.LENGTH_SHORT).show();
                //Store Master Password into file
                //Redirect to Backup Page
                setContentView(R.layout.activity_recovery);
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


