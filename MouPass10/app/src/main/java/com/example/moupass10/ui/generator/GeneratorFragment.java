package com.example.moupass10.ui.generator;

import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.Context;
import android.widget.Toast;

import com.example.moupass10.Login;
import com.example.moupass10.R;
import com.example.moupass10.databinding.FragmentGeneratorBinding;
import com.example.moupass10.databinding.FragmentHomeBinding;
import com.example.moupass10.ui.home.HomeViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.Random;


public class GeneratorFragment extends Fragment {

    TextView btnGenerate;
    Spinner lstLength;
    TextView txtPass;
    MaterialButton btnCopy;
    CheckBox chkDigits;
    CheckBox chkUppercase;
    CheckBox chkLowercase;
    CheckBox chkAlphabet;
    CheckBox chkSymbols;

    private FragmentGeneratorBinding binding;

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

        binding = FragmentGeneratorBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btnGenerate = root.findViewById(R.id.btnRefresh);
        lstLength = root.findViewById(R.id.lstLength);
        txtPass = root.findViewById(R.id.txtPassPhrase);
        btnCopy = root.findViewById(R.id.btnCopy);
        chkDigits = root.findViewById(R.id.chkDigits);
        chkUppercase = root.findViewById(R.id.chkUppercase);
        chkLowercase = root.findViewById(R.id.chkLowercase);
        chkSymbols = root.findViewById(R.id.chkSymbols);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.pass_lengths, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_item);
        // Apply the adapter to the spinner
        lstLength.setAdapter(adapter);

        //Generate Passphrase onCreate
        String password = onCreatePasswordGenerator(10);
        txtPass.setText(password);

        //Generate Passphrase when button clicked
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int length = Integer.parseInt(lstLength.getSelectedItem().toString());
                String password = PasswordGenerator(length, chkDigits.isChecked(), chkUppercase.isChecked(), chkLowercase.isChecked(), chkSymbols.isChecked());
                txtPass.setText(password);
            }
        });

        //Copy Passphrase
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = txtPass.getText().toString();
                copyTextToClipboard(text);
                Toast.makeText(getActivity(), "Copied", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    // Method to generate a random password onCreate
    private String onCreatePasswordGenerator(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        Random random = new Random();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            password.append(chars.charAt(index));
        }

        return password.toString();
    }

    // Method to generate a random password based on selected lengths
    private String PasswordGenerator(int length, boolean useDigits, boolean useUppercase, boolean useLowercase, boolean useSymbols) {
        String pass = "";
        if (useDigits) pass += "0123456789";
        if (useUppercase) pass += "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        if (useLowercase) pass += "abcdefghijklmnopqrstuvwxyz";
        if (useSymbols) pass += "!@#$%^&*()_+={}|:;<>?,./~`";
        if (pass.equals("")) return "Invalid options!";

        StringBuilder password = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            password.append(pass.charAt(random.nextInt(pass.length())));
        }
        return password.toString();
    }

    // Method to copy text to the clipboard
    private void copyTextToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("copiedPass", text);
        clipboard.setPrimaryClip(clip);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}