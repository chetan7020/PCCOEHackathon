package com.safar.pccoehackathon.customer.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.safar.pccoehackathon.OwnerSignUpActivity;
import com.safar.pccoehackathon.R;
import com.safar.pccoehackathon.customer.CustomerMessInfoActivity;
import com.safar.pccoehackathon.databinding.FragmentHomeBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class CustomerHomeFragment extends Fragment implements OnMapReadyCallback {

    private FragmentHomeBinding binding;
    private FirebaseFirestore firebaseFirestore;
    FusedLocationProviderClient fusedLocationProviderClient;

    public static double cusLat, cusLang;
    public static String customerlat,customerlang;
    private final static int REQUEST_CODE = 100;
        binding.rgFilter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton checkedRadioButton = group.findViewById(checkedId);

                if (checkedRadioButton.getId() == R.id.rbLocation){
                    binding.llData.removeAllViews();
                    getLastLocation();
                }else if(checkedRadioButton.getId() == R.id.rbReview){
                    binding.llData.removeAllViews();
                    getDataByReview();
                }
            }
        });

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater());

        firebaseFirestore = FirebaseFirestore.getInstance();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        getLastLocation();

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                for (int i = 0; i < binding.llData.getChildCount(); i++) {
                    TextView tvMessName = binding.llData.getChildAt(i).findViewById(R.id.tvMessName);
                    TextView tvLocation = binding.llData.getChildAt(i).findViewById(R.id.tvLocation);
                    LinearLayout llView = binding.llData.getChildAt(i).findViewById(R.id.llView);

                    if (!(tvMessName.getText().toString().toLowerCase().trim().contains(query.toLowerCase())
                            || tvLocation.getText().toString().toLowerCase().trim().contains(query.toLowerCase()))) {

                        llView.setVisibility(View.GONE);
                    } else {
                        llView.setVisibility(View.VISIBLE);
                    }
                }

                return false;
            }
        });

        return binding.getRoot();

    }

    private void getDataByReview() {
        firebaseFirestore
                .collection("Owner")
                .orderBy("avg_review")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            String id = dc.getDocument().getId();
                            String messname = dc.getDocument().getData().get("messname").toString();
                            String location = dc.getDocument().getData().get("location").toString();
                            String monthlyPrice = dc.getDocument().getData().get("monthlyPrice").toString();
                            String email = dc.getDocument().getData().get("email").toString();
                            String avg_review = dc.getDocument().getData().get("avg_review").toString();

                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d("TAG", "onEvent: " + "ADDED");
                                    createCard(id, messname, location, monthlyPrice, email, avg_review);
                                    break;
                                case MODIFIED:
                                    Log.d("TAG", "onEvent: " + "MODIFIED");
                                    updateCard(id, messname, location, monthlyPrice, email, avg_review);
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

    private List<GeoPoint> getBoundingBox(GeoPoint center, double radius) {
        double lat = center.getLatitude();
        double lng = center.getLongitude();

        double earthRadius = 6371;
        double latRadius = radius / earthRadius;
        double lngRadius = radius / (earthRadius * Math.cos(Math.PI * lat / 180));

        double minLat = lat - latRadius * 180 / Math.PI;
        double maxLat = lat + latRadius * 180 / Math.PI;
        double minLng = lng - lngRadius * 180 / Math.PI;
        double maxLng = lng + lngRadius * 180 / Math.PI;

        GeoPoint southwest = new GeoPoint(minLat, minLng);
        GeoPoint northeast = new GeoPoint(maxLat, maxLng);
        List<GeoPoint> boundingBox = new ArrayList<>();
        boundingBox.add(southwest);
        boundingBox.add(northeast);

        return boundingBox;
    }

    private void getAllOwners(double lat, double lang) {

        firebaseFirestore
                .collection("Owner")
                .orderBy("geohash")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            String id = dc.getDocument().getId();
                            String messname = dc.getDocument().getData().get("messname").toString();
                            String location = dc.getDocument().getData().get("location").toString();
                            String monthlyPrice = dc.getDocument().getData().get("monthlyPrice").toString();
                            String email = dc.getDocument().getData().get("email").toString();
                            String avg_review = dc.getDocument().getData().get("avg_review").toString();

                            Log.d("TAG", "onEvent: "+avg_review);

                            GeoPoint lc = dc.getDocument().getGeoPoint("geo_pointLocation");

                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d("TAG", "onEvent: " + "ADDED");
                                    createCard(id, messname, location, monthlyPrice, email, avg_review);
                                    break;
                                case MODIFIED:
                                    Log.d("TAG", "onEvent: " + "MODIFIED");
                                    updateCard(id, messname, location, monthlyPrice, email, avg_review);
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

    private void updateCard(String id, String messname, String location, String monthlyPrice, String email, String avg_review) {
        for (int i = 0; i < binding.llData.getChildCount(); i++) {
            TextView tvMessName, tvMonthlyPrice, tvLocation, tvMail, tvID;

            MaterialRatingBar rating;

            tvID = binding.llData.findViewById(R.id.tvID);
            tvMessName = binding.llData.findViewById(R.id.tvMessName);
            tvMonthlyPrice = binding.llData.findViewById(R.id.tvMonthlyPrice);
            tvLocation = binding.llData.findViewById(R.id.tvLocation);
            tvMail = binding.llData.findViewById(R.id.tvMail);

            rating = binding.llData.findViewById(R.id.rating);


            if (tvID.getText().toString().trim().equals(id)) {
                tvID.setText(id);
                tvMessName.setText(messname);
                tvMonthlyPrice.setText(monthlyPrice);
                tvLocation.setText(location);
                tvMail.setText(email);

                rating.setRating(Float.parseFloat(avg_review));

            }

        }

    }

    private void createCard(String id, String messname, String location, String monthlyPrice, String email, String avg_review) {
        View messView = getLayoutInflater().inflate(R.layout.activity_layout_customer_mess_container, null, false);

        TextView tvMessName, tvMonthlyPrice, tvLocation, tvMail, tvID;
        LinearLayout llView;
        MaterialRatingBar rating;

        tvID = messView.findViewById(R.id.tvID);
        tvMessName = messView.findViewById(R.id.tvMessName);
        tvMonthlyPrice = messView.findViewById(R.id.tvMonthlyPrice);
        tvLocation = messView.findViewById(R.id.tvLocation);
        tvMail = messView.findViewById(R.id.tvMail);

        llView = messView.findViewById(R.id.llView);

        rating = messView.findViewById(R.id.rating);

        tvID.setText(id);
        tvMessName.setText(messname);
        tvMonthlyPrice.setText(monthlyPrice);
        tvLocation.setText(location);
        tvMail.setText(email);

        rating.setRating(Float.parseFloat(avg_review));

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

    private void getLastLocation() {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {

                            if (location != null) {
                                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                                    cusLat = addresses.get(0).getLatitude();
                                    cusLang = addresses.get(0).getLongitude();

                                    customerlat = addresses.get(0).getAddressLine(100);
                                    customerlang = addresses.get(0).getAddressLine(100);

                                    getAllOwners(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    });
        } else {
            askPermission();
        }

    }

    private void askPermission() {

        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(getActivity(), "Please provide required Permission", Toast.LENGTH_SHORT).show();
            }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
    public static double calculateMinLatitude(double lat, double lng, int radius) {
        double r = 6371; // Earth's radius in km
        double latRadius = radius / r * (180 / Math.PI); // Convert radius from km to degrees
        return lat - latRadius;
    }

    public static double calculateMaxLatitude(double lat, double lng, int radius) {
        double r = 6371; // Earth's radius in km
        double latRadius = radius / r * (180 / Math.PI); // Convert radius from km to degrees
        return lat + latRadius;
    }

    public static double calculateMinLongitude(double lat, double lng, int radius) {
        double r = 6371; // Earth's radius in km
        double lngRadius = radius / (r * Math.cos(Math.PI / 180 * lat)); // Convert radius from km to degrees
        return lng - lngRadius;
    }

    public static double calculateMaxLongitude(double lat, double lng, int radius) {
        double r = 6371; // Earth's radius in km
        double lngRadius = radius / (r * Math.cos(Math.PI / 180 * lat)); // Convert radius from km to degrees
        return lng + lngRadius;
    }

}