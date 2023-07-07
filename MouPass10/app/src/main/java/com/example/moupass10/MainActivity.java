package com.example.moupass10;

import android.os.Bundle;
import android.view.Menu;

import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moupass10.databinding.ActivityMainBinding;

import android.content.Context;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

/*    private RecyclerView recyclerView;
    private PasswordAdapter adapter;
    private List<Password> passwordList;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
/*        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                *//*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*//*
                Fragment fragment = new FormFragment();

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment_content_main, fragment).commit();
            }
        });*/
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,R.id.nav_dash,R.id.nav_detector, R.id.nav_generator)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

/*        //Display Data in Recycle View
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        passwordList = new ArrayList<>();
        adapter = new PasswordAdapter(passwordList);
        recyclerView.setAdapter(adapter);

        loadPasswords();*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

/*    private void loadPasswords() {
        FileInputStream fis = null;
        BufferedReader reader = null;

        try {
            fis = openFileInput("content.snf");
            reader = new BufferedReader(new InputStreamReader(fis));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] encryptedPasswords = line.split(",");
                if (encryptedPasswords.length == 4) {
                    String base64Title = encryptedPasswords[0];
                    String base64User = encryptedPasswords[1];
                    String base64Pass = encryptedPasswords[2];
                    String base64Website = encryptedPasswords[3];

                    byte[] encryptionKey = loadKey();
                    if (encryptionKey != null) {
                        String title = decrypt(Base64.decode(base64Title, Base64.DEFAULT), encryptionKey);
                        String username = decrypt(Base64.decode(base64User, Base64.DEFAULT), encryptionKey);
                        String password = decrypt(Base64.decode(base64Pass, Base64.DEFAULT), encryptionKey);
                        String website = decrypt(Base64.decode(base64Website, Base64.DEFAULT), encryptionKey);

                        Password passwordObj = new Password(title, username, password, website);
                        passwordList.add(passwordObj);
                    }
                }
            }

            adapter.notifyDataSetChanged();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] loadKey() {
        FileInputStream fis = null;
        BufferedReader reader = null;

        try {
            fis = openFileInput("k3y3.snf");
            reader = new BufferedReader(new InputStreamReader(fis));

            String line = reader.readLine();
            if (line != null) {
                String base64EncryptionKey = line.trim();
                return Base64.decode(base64EncryptionKey, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String decrypt(byte[] encryptedData, byte[] encryptionKey) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(encryptionKey, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decryptedBytes = cipher.doFinal(encryptedData);
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class PasswordAdapter extends RecyclerView.Adapter<PasswordAdapter.ViewHolder> {

        private List<Password> passwordList;

        public PasswordAdapter(List<Password> passwordList) {
            this.passwordList = passwordList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Password password = passwordList.get(position);
            holder.titleTextView.setText(password.getTitle());
            holder.usernameTextView.setText(password.getUsername());
            holder.passwordTextView.setText(password.getPassword());
            holder.websiteTextView.setText(password.getWebsite());
        }

        @Override
        public int getItemCount() {
            return passwordList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public TextView titleTextView;
            public TextView usernameTextView;
            public TextView passwordTextView;
            public TextView websiteTextView;

            public ViewHolder(View view) {
                super(view);
                titleTextView = view.findViewById(R.id.txtTitle);
                usernameTextView = view.findViewById(R.id.txtUser);
                passwordTextView = view.findViewById(R.id.txtPass);
                websiteTextView = view.findViewById(R.id.txtWebsite);
            }
        }
    }

    private static class Password {
        private String title;
        private String username;
        private String password;
        private String website;

        public Password(String title, String username, String password, String website) {
            this.title = title;
            this.username = username;
            this.password = password;
            this.website = website;
        }

        public String getTitle() {
            return title;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getWebsite() {
            return website;
        }
    }*/
}