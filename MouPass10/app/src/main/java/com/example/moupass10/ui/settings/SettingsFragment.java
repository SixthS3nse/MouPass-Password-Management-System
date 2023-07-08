package com.example.moupass10.ui.settings;

import androidx.core.view.ViewCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

import com.example.moupass10.R;
import com.example.moupass10.databinding.FragmentSettingsBinding;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    private RecyclerView recyclerView;
    private SettingsAdapter settingsAdapter;
    private List<SettingsOption> settingsOptions;
    private FragmentSettingsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

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

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Create dummy settings options
        settingsOptions = new ArrayList<>();
        settingsOptions.add(new SettingsOption(R.drawable.baseline_password_maintheme_24, "Change Password"));
        settingsOptions.add(new SettingsOption(R.drawable.baseline_backup_24, "Export"));
        settingsOptions.add(new SettingsOption(R.drawable.baseline_restore_24, "Import"));
        settingsOptions.add(new SettingsOption(R.drawable.baseline_delete_24, "Delete Data"));

        // Setup RecyclerView
        recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        settingsAdapter = new SettingsAdapter(settingsOptions, getActivity());
        recyclerView.setAdapter(settingsAdapter);

        return root;
    }


}