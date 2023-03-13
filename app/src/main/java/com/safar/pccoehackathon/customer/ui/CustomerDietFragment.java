package com.safar.pccoehackathon.customer.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.safar.pccoehackathon.R;
import com.safar.pccoehackathon.databinding.FragmentCustomerDietBinding;
import com.safar.pccoehackathon.databinding.FragmentHomeBinding;

public class CustomerDietFragment extends Fragment {

    FragmentCustomerDietBinding binding;

    private FirebaseFirestore firebaseFirestore;

    private FirebaseAuth firebaseAuth;
    String item;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCustomerDietBinding.inflate(getLayoutInflater());

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseFirestore
                .collection("Customer")
                .document(firebaseAuth.getCurrentUser().getEmail())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        item = value.getString("item");

                        getData(item);
                    }
                });

        return binding.getRoot();
    }

    private void getData(String item) {
        firebaseFirestore
                .collection("Dishes")
                .whereEqualTo("type",item)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String dish_name = document.getString("dish_name");
                            String allergies = document.getString("allergies");
                            String dish_contents = document.getString("dish_contents");
                            String glutenFree = document.getString("glutenFree");
                            String price = document.getString("price");
                            String type = document.getString("type");

                            createCard(dish_name, allergies, dish_contents, glutenFree, price, type);
                        }
                    }
                });
    }

    private void createCard(String dish_name, String allergies, String dish_contents, String glutenFree, String price, String type) {
        View plateView = getLayoutInflater().inflate(R.layout.customer_diet_layout, null, false);

        TextView tvDishName = plateView.findViewById(R.id.tvDishName);
        TextView tvPrice = plateView.findViewById(R.id.tvPrice);
        TextView tvType = plateView.findViewById(R.id.tvType);
        TextView tvAllergies = plateView.findViewById(R.id.tvAllergies);
        TextView tvContents = plateView.findViewById(R.id.tvContents);

        tvDishName.setText(dish_name);
        tvPrice.setText(price);
        tvType.setText(type);
        tvAllergies.setText(allergies);
        tvContents.setText(dish_contents);

        binding.llData.addView(plateView);
    }
}