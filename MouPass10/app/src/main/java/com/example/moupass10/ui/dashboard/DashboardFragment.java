package com.example.moupass10.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.Toast;

import com.example.moupass10.CryptoUtils;
import com.example.moupass10.Login;
import com.example.moupass10.MainActivity;
import com.example.moupass10.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;

public class DashboardFragment extends Fragment {

    private RecyclerView recyclerView;
    private DataItemAdapter dataItemAdapter;
    private List<DataItem> dataItems;
    private Key secretKey;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Check "Login" SharedPreferences value
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            requireActivity().finishAffinity();
            System.exit(0);
        }

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

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Load Stored Data
        secretKey = ((MainActivity) getActivity()).loadKey("k3y3.snf");
        if (secretKey != null) {
            loadData();
        } else {
            //Toast.makeText(getActivity(), "Error Loading Key", Toast.LENGTH_SHORT).show();
        }

        //FAB - Add Button
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Form.class));
            }
        });

        return view;
    }

    private void loadData() {
        try {
            FileInputStream in = getContext().openFileInput("Data.snf");
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            dataItems = new ArrayList<>();
            while ((line = bufferedReader.readLine()) != null) {
                byte[] decodedData = android.util.Base64.decode(line, android.util.Base64.DEFAULT);
                byte[] decryptedData = CryptoUtils.doAES(Cipher.DECRYPT_MODE, secretKey, new byte[16], decodedData);
                String decryptedString = new String(decryptedData, StandardCharsets.UTF_8);
                String[] parts = decryptedString.split(",");
                if (parts.length == 4) {
                    DataItem item = new DataItem(parts[0], parts[1], parts[2], parts[3]);
                    dataItems.add(item);
                }
            }
            dataItemAdapter = new DataItemAdapter(getActivity(),dataItems);
            dataItemAdapter.setOnItemClickListener(new DataItemAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(DataItem dataItem) {
                    Intent intent = new Intent(getActivity(), DashboardDetails.class);
                    intent.putExtra("dataItem", dataItem);
                    startActivity(intent);
                }
            });
            recyclerView.setAdapter(dataItemAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


