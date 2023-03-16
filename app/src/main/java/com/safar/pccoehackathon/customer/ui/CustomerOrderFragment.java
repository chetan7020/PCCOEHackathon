package com.safar.pccoehackathon.customer.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.safar.pccoehackathon.R;
import com.safar.pccoehackathon.databinding.FragmentCustomerOrderBinding;

public class CustomerOrderFragment extends Fragment {


    FragmentCustomerOrderBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCustomerOrderBinding.inflate(getLayoutInflater());


        return binding.getRoot();
    }
}