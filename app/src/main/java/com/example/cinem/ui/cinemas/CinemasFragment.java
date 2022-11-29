package com.example.cinem.ui.cinemas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cinem.databinding.FragmentCinemasBinding;

public class CinemasFragment extends Fragment {

    private FragmentCinemasBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CinemasViewModel dashboardViewModel =
                new ViewModelProvider(this).get(CinemasViewModel.class);

        binding = FragmentCinemasBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.testeCinemas;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}