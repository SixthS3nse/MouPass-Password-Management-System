package com.example.moupass;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.service.controls.actions.FloatAction;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moupass.databinding.ActivityMainBinding;
import com.google.android.material.textfield.TextInputLayout;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //App Launch
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
                    Toast.makeText(MainActivity.this,"Confirm Password is incorrect",Toast.LENGTH_SHORT).show();

            }
        });

        //Information Button
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"⚠️Stored data cannot be recovered the master password is lost⚠️",Toast.LENGTH_LONG).show();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
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
                Toast.makeText(MainActivity.this,"Registered",Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(MainActivity.this,"Password must have more than 8 characters",Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(MainActivity.this,"Password must contain at least one uppercase letter",Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(MainActivity.this,"Password must be alphanumeric",Toast.LENGTH_SHORT).show();
                break;
        }

        }
    }


