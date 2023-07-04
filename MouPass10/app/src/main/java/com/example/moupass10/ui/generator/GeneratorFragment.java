package com.example.moupass10.ui.generator;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.moupass10.R;
import com.example.moupass10.databinding.FragmentGeneratorBinding;
import com.example.moupass10.databinding.FragmentHomeBinding;
import com.example.moupass10.ui.home.HomeViewModel;

public class GeneratorFragment extends Fragment {

/*    private FragmentGeneratorBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GeneratorViewModel generatorViewModel =
                new ViewModelProvider(this).get(GeneratorViewModel.class);

        binding = FragmentGeneratorBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

*//*        final TextView textView = binding.textGenerator;
        GeneratorViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);*//*
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }*/


    private GeneratorViewModel mViewModel;

    public static GeneratorFragment newInstance() {
        return new GeneratorFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_generator, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(GeneratorViewModel.class);
        // TODO: Use the ViewModel
    }


}