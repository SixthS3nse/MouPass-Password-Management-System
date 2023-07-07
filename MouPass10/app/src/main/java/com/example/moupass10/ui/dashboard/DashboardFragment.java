package com.example.moupass10.ui.dashboard;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;


import com.example.moupass10.DataAdapter;
import com.example.moupass10.DataModel;
import com.example.moupass10.Form;
import com.example.moupass10.R;
import com.example.moupass10.Test;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;


public class DashboardFragment extends Fragment {

    private ArrayList<DataModel> dataModels;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        //Fix white bar under screen
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            final View decorView = getActivity().getWindow().getDecorView();
            decorView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @Override
                public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                    final WindowInsets defaultInsets = decorView.onApplyWindowInsets(insets);
                    return defaultInsets.replaceSystemWindowInsets(
                            defaultInsets.getSystemWindowInsetLeft(),
                            defaultInsets.getSystemWindowInsetTop(),
                            defaultInsets.getSystemWindowInsetRight(),
                            0  // remove bottom inset
                    );
                }
            });
            ViewCompat.requestApplyInsets(decorView);
        }

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        //Display Entry
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        dataModels = new ArrayList<>();
        loadAndDecryptData(getContext());

        //Test RecyclerView Entry
        dataModels.add(new DataModel("Google","google.com"));
        dataModels.add(new DataModel("Google2","google.com"));

        // set up the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DataAdapter adapter = new DataAdapter(getContext(), dataModels);
        recyclerView.setAdapter(adapter);

        //FAB - Add Button
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), Form.class));
            }
        });

        return view;
    }

    private void loadAndDecryptData(Context context) {
        try {
            FileInputStream fis = context.openFileInput("content.snf");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;

            // Loading saved key
            byte[] encryptionKey = loadKey(context);

            while ((line = br.readLine()) != null) {
                String[] content = line.split(",");
                if (content.length < 4) {
                    continue;  // Skip this iteration if there are not enough elements on this line
                }

                // Decrypting the encrypted contents
                String decryptedTitle = decrypt(Base64.decode(content[0], Base64.DEFAULT), encryptionKey);
                String decryptedWebsite = decrypt(Base64.decode(content[3], Base64.DEFAULT), encryptionKey);

                // Adding to dataModels
                dataModels.add(new DataModel(decryptedTitle, decryptedWebsite));
            }

            br.close();
            isr.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] loadKey(Context context) {
        try {
            FileInputStream fis = context.openFileInput("k3y3.snf");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String base64EncryptionKey = br.readLine();

            return Base64.decode(base64EncryptionKey, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String decrypt(byte[] encryptedContent, byte[] encryptionKey) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(encryptionKey, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] original = cipher.doFinal(encryptedContent);

            return new String(original);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
/*public class DashboardFragment extends Fragment {
    private static final String FILENAME = "content.snf";
    private static final String AES = "AES";
    private static final String CIPHER_INSTANCE = "AES/CBC/PKCS5Padding";
    private static final String PREFS_NAME = "MyPrefsFile";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        try {
            FileInputStream fis = getContext().openFileInput(FILENAME);
            byte[] encodedBytes = new byte[fis.available()];
            if (fis.read(encodedBytes) != -1) {
                byte[] decodedBytes = android.util.Base64.decode(encodedBytes, android.util.Base64.DEFAULT);

                SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
                String secretKeyBase64 = settings.getString("secretKey", null);
                String ivBase64 = settings.getString("iv", null);

                if (secretKeyBase64 == null || ivBase64 == null) {
                    // Key or IV do not exist, cannot decrypt
                    Log.e("DashboardFragment", "Cannot decrypt data because key or IV does not exist");
                    return;
                }

                try {
                    byte[] decodedKey = android.util.Base64.decode(secretKeyBase64, android.util.Base64.DEFAULT);
                    SecretKeySpec skeySpec = new SecretKeySpec(decodedKey, AES);

                    byte[] decodedIv = android.util.Base64.decode(ivBase64, android.util.Base64.DEFAULT);
                    IvParameterSpec ivParameterSpec = new IvParameterSpec(decodedIv);

                Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE);
                cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParameterSpec);

                String decryptedString = new String(cipher.doFinal(decodedBytes), StandardCharsets.UTF_8);

                String[] dataParts = decryptedString.split(",");

                // Here you can load your data into RecyclerView
                // Let's just print it out for now
                for (String part : dataParts) {
                    System.out.println(part);
                }
                    } catch (IllegalArgumentException e) {
                        // Error occurred while decoding
                        Log.e("DashboardFragment", "Error decoding key or IV: " + e.getMessage());
                        return;
                    }
            }
            fis.close();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }*/

