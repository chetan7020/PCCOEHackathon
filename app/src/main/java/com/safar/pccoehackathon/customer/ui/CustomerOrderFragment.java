package com.safar.pccoehackathon.customer.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.safar.pccoehackathon.R;
import com.safar.pccoehackathon.databinding.FragmentCustomerOrderBinding;
import com.safar.pccoehackathon.trackmap;

import java.net.URI;

public class CustomerOrderFragment extends Fragment {


    FragmentCustomerOrderBinding binding;

    Button btntrack_order;
    String s,s1;

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


                s =  "pune";
                 s1 =  "mumbai";

                if(s.equals("") && s1.equals("") )
                {
                    Toast.makeText(getActivity(), "Location access is denied", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    DisplayTrack(s,s1);
                }


            }
        });

        binding.llData.addView(trackOrderView);
    }

    private void DisplayTrack(String s, String s1) {

        try {
            Uri uri = Uri.parse("https://www.google.co.in/maps/dir/"+s+"/"+s1);

            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            intent.setPackage("com.google.android.apps.maps");

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        catch (ActivityNotFoundException e)
        {
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }
}