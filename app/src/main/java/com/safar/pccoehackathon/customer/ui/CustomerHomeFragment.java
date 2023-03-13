package com.safar.pccoehackathon.customer.ui;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.safar.pccoehackathon.R;
import com.safar.pccoehackathon.customer.CustomerMessInfoActivity;
import com.safar.pccoehackathon.customer.GeoFirestoreUtils;
import com.safar.pccoehackathon.databinding.FragmentHomeBinding;

import java.io.IOException;
import java.util.List;

public class CustomerHomeFragment extends Fragment implements OnMapReadyCallback {

    private FragmentHomeBinding binding;

    private FirebaseFirestore firebaseFirestore;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater());

        firebaseFirestore = FirebaseFirestore.getInstance();

        getAllOwners();

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = binding.searchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(getActivity());
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Address address = addressList.get(0);

                    double centerLatitude = address.getLatitude();
                    double centerLongitude = address.getLongitude();
                    double radius = 10.0; // in kilometers

                    GeoPoint center = new GeoPoint(centerLatitude, centerLongitude);

                    binding.llData.removeAllViews();

                    firebaseFirestore
                            .collection("Owner")
                            .orderBy("geo_pointLocation", Query.Direction.ASCENDING)
                            .whereGreaterThan("geo_pointLocation", GeoFirestoreUtils.getGeoPointAtLocation(center, radius))
                            .whereLessThan("geo_pointLocation", GeoFirestoreUtils.getGeoPointAtLocation(center, radius))
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        Log.d("TAG", "onSuccess: "+document.get("messname"));
                                    }
                                }
                            });

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        return binding.getRoot();

    }


    private void getAllOwners() {
        Log.d("TAG", "getAllOwners: ");
        firebaseFirestore
                .collection("Owner")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            String id = dc.getDocument().getId();
                            String messname = dc.getDocument().getData().get("messname").toString();
                            String location = dc.getDocument().getData().get("location").toString();
                            String monthlyPrice = dc.getDocument().getData().get("monthlyPrice").toString();
                            String email = dc.getDocument().getData().get("email").toString();
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d("TAG", "onEvent: " + "ADDED");
                                    createCard(id, messname, location, monthlyPrice, email);
                                    break;
                                case MODIFIED:
                                    Log.d("TAG", "onEvent: " + "MODIFIED");
                                    updateCard(id, messname, location, monthlyPrice, email);
                                    break;
                                case REMOVED:
                                    Log.d("TAG", "onEvent: " + "ADDED");
                                    for (int i = 0; i < binding.llData.getChildCount(); i++) {

                                        TextView tvID = binding.llData.getChildAt(i).findViewById(R.id.tvID);

                                        String firebase_id = tvID.getText().toString().trim();

                                        if (firebase_id.equals(id)) {
                                            binding.llData.removeView(binding.llData.getChildAt(i));
                                        }
                                    }
                                    break;
                            }

                        }
                    }
                });

    }

    private void updateCard(String id, String messname, String location, String monthlyPrice, String email) {
        for (int i = 0; i < binding.llData.getChildCount(); i++) {
            TextView tvMessName, tvMonthlyPrice, tvLocation, tvMail, tvID;

            tvID = binding.llData.findViewById(R.id.tvID);
            tvMessName = binding.llData.findViewById(R.id.tvMessName);
            tvMonthlyPrice = binding.llData.findViewById(R.id.tvMonthlyPrice);
            tvLocation = binding.llData.findViewById(R.id.tvLocation);
            tvMail = binding.llData.findViewById(R.id.tvMail);


            if (tvID.getText().toString().trim().equals(id)) {
                tvID.setText(id);
                tvMessName.setText(messname);
                tvMonthlyPrice.setText(monthlyPrice);
                tvLocation.setText(location);
                tvMail.setText(email);

            }

        }

    }

    private void createCard(String id, String messname, String location, String monthlyPrice, String email) {
        View messView = getLayoutInflater().inflate(R.layout.activity_layout_customer_mess_container, null, false);

        TextView tvMessName, tvMonthlyPrice, tvLocation, tvMail, tvID;
        LinearLayout llView;

        tvID = messView.findViewById(R.id.tvID);
        tvMessName = messView.findViewById(R.id.tvMessName);
        tvMonthlyPrice = messView.findViewById(R.id.tvMonthlyPrice);
        tvLocation = messView.findViewById(R.id.tvLocation);
        tvMail = messView.findViewById(R.id.tvMail);

        llView = messView.findViewById(R.id.llView);

        tvID.setText(id);
        tvMessName.setText(messname);
        tvMonthlyPrice.setText(monthlyPrice);
        tvLocation.setText(location);
        tvMail.setText(email);

        llView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CustomerMessInfoActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        binding.llData.addView(messView);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}