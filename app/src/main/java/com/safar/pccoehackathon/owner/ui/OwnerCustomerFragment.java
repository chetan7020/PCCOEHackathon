package com.safar.pccoehackathon.owner.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.safar.pccoehackathon.R;
import com.safar.pccoehackathon.Transactions;
import com.safar.pccoehackathon.databinding.FragmentOwnerCustomerBinding;
import com.safar.pccoehackathon.databinding.FragmentOwnerFoodBinding;

public class OwnerCustomerFragment extends Fragment {
    private FragmentOwnerCustomerBinding binding;
    FirebaseFirestore firebaseFirestore;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOwnerCustomerBinding.inflate(getLayoutInflater());
        firebaseFirestore = FirebaseFirestore.getInstance();

        return binding.getRoot();

    }

//    private void displayCustomer() {
//        firebaseFirestore
//                .collection("Owner")
//                .document("12345") //edit
//                .collection("customer")
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                        for (DocumentChange dc : value.getDocumentChanges()) {
//                            String id = dc.getDocument().getData().get("id").toString();
//                            String name = dc.getDocument().getData().get("name").toString();
//                            String phoneNumber = dc.getDocument().getData().get("phoneNumber").toString();
//                            switch (dc.getType()) {
//                                case ADDED:
//                                    createCard(id, name, phoneNumber);
//                                    break;
//                                case MODIFIED:
//                                    updateCustomer(id, name, phoneNumber);
//                                    break;
//                                case REMOVED:
//                                    for (int i = 0; i < binding.llData.getChildCount(); i++) {
//
////                                        TextView tvID = binding.llData.getChildAt(i).findViewById(R.id.tvID);
//
////                                        String firebase_id = tvID.getText().toString().trim();
//
////                                        if (firebase_id.equals(id)) {
////                                            binding.llData.removeView(binding.llData.getChildAt(i));
////                                        }
//                                    }
//                                    break;
//                            }
//                        }
//                    }
//                });
//    }

//    private void updateCustomer(String id, String name, String phoneNumber) {
//
//        for (int i = 0; i < binding.llData.getChildCount(); i++) {
//
//            TextView tvID = binding.llData.getChildAt(i).findViewById(R.id.tvID);
//            TextView tvName = binding.llData.getChildAt(i).findViewById(R.id.tvName);
//            TextView tvPhoneNumber = binding.llData.getChildAt(i).findViewById(R.id.tvPhoneNumber);
//
//
//            if (tvID.getText().toString().trim().equals(id)) {
//                tvName.setText(name);
//                tvPhoneNumber.setText(phoneNumber);
//
//            }
//
//        }
//    }

    private void createCard(String id, String name, String phoneNumber) {


        View plateView = getLayoutInflater().inflate(R.layout.owner_customer_layout, null, false);

        binding.llData.addView(plateView);
    }

    private void retrieveTransactions(String id) {


        firebaseFirestore
                .collection("Transactions")
                .document("ownerID")
                .collection("customerID")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            String transaction = dc.getDocument().getData().get("id").toString();
                        }
                    }
                });
    }


}