package com.example.moupass10.ui.detector;

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
import com.example.moupass10.databinding.FragmentDetectorBinding;
import com.example.moupass10.databinding.FragmentGeneratorBinding;
import com.example.moupass10.ui.generator.GeneratorViewModel;

public class DetectorFragment extends Fragment {

    private FragmentDetectorBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DetectorViewModel generatorViewModel =
                new ViewModelProvider(this).get(DetectorViewModel.class);

        binding = FragmentDetectorBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

/*        final TextView textView = binding.textDetector;
        DetectorViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);*/
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

/*    private DetectorViewModel mViewModel;

    public static DetectorFragment newInstance() {
        return new DetectorFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detector, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DetectorViewModel.class);
        // TODO: Use the ViewModel
    }*/

}