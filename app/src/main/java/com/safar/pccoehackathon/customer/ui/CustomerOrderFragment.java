package com.safar.pccoehackathon.customer.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.safar.pccoehackathon.R;
import com.safar.pccoehackathon.databinding.FragmentCustomerOrderBinding;
import com.safar.pccoehackathon.trackmap;

public class CustomerOrderFragment extends Fragment {


    FragmentCustomerOrderBinding binding;

    Button btntrack_order;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCustomerOrderBinding.inflate(getLayoutInflater());

        for (int i = 0; i < 5; i++) {
            createCard();
        }


        return binding.getRoot();
    }

    private void createCard() {
        View trackOrderView = getLayoutInflater().inflate(R.layout.layout_customer_order, null, false);

        btntrack_order = trackOrderView.findViewById(R.id.btntrack_order);

        btntrack_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(getActivity(), trackmap.class);
                startActivity(intent);


            }
        });

        binding.llData.addView(trackOrderView);
    }
}