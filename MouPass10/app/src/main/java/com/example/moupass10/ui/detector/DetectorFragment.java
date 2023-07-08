package com.example.moupass10.ui.detector;

import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.EditText;
import android.widget.TextView;

import com.example.moupass10.R;
import com.example.moupass10.databinding.FragmentDetectorBinding;
import com.example.moupass10.databinding.FragmentGeneratorBinding;
import com.example.moupass10.ui.generator.GeneratorViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class DetectorFragment extends Fragment {

    TextInputLayout txtUserpass;
    TextView lblIndicator;
    MaterialButton btnCheck;
    TextView Result;

    private static final long GUESSES_PER_SECOND = 100_000_000_000L;

    private FragmentDetectorBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

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

        binding = FragmentDetectorBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        txtUserpass = root.findViewById(R.id.txtUserPass);
        lblIndicator = root.findViewById(R.id.lblIndicator);
        btnCheck = root.findViewById(R.id.btnCheck);
        Result = root.findViewById(R.id.result);

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Indicator color
                String password = txtUserpass.getEditText().getText().toString();
                PasswordStrengthIndicator(password, lblIndicator);

                //Estimated time to crack
                long years = calculateYears(password);

                Result.setText(years + " Years.");

                if (years < 1) {
                    Result.setTextColor(Color.RED);
                } else if (years >= 1 && years <= 10) {
                    Result.setTextColor(Color.YELLOW);
                } else {
                    Result.setTextColor(Color.GREEN);
                }
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void PasswordStrengthIndicator(String password, TextView lblIndicator) {
        if (password.isEmpty()) {
            lblIndicator.setText("No Input");
        } else if (password.length() < 10) { //<10 = Weak
            lblIndicator.setText("Weak password");
            lblIndicator.setTextColor(Color.RED);
        } else if (password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+={}|:;<>?,./~`])(?=\\S+$).{10,}$")) { //>=10 + uppercase + lowercase + digit + symbols
            lblIndicator.setText("Strong password");
            lblIndicator.setTextColor(Color.GREEN);
        } else { //else = Medium, has letter or digit = medium password
            lblIndicator.setText("Medium password");
            lblIndicator.setTextColor(Color.YELLOW);
        }
    }

    private long calculateYears(String password) {
        int length = password.length();
        long years = 0;

        if (length == 8) {
            if (password.matches(".*[a-z].*")) years += 0; // Lowercase letters add no time
            if (password.matches(".*[A-Z].*")) years += (long) 0.5 / 24 / 365; // 30 minutes in years
            if (password.matches(".*[0-9].*")) years += (long) 1 / 24 / 365; // 1 hour in years
            if (password.matches(".*[^a-zA-Z0-9].*")) years += 1; // 1 day in years
        } else if (length == 10) {
            if (password.matches(".*[a-z].*")) years += 0;
            if (password.matches(".*[A-Z].*")) years += (long) 30 / 365; // 1 month in years
            if (password.matches(".*[0-9].*")) years += 6;
            if (password.matches(".*[^a-zA-Z0-9].*")) years += 50;
        } else if (length > 12) {
            if (password.matches(".*[a-z].*")) years += 0;
            if (password.matches(".*[A-Z].*")) years += 5;
            if (password.matches(".*[0-9].*")) years += 2000;
            if (password.matches(".*[^a-zA-Z0-9].*")) years += 63000;
        }

        return years;
    }
}