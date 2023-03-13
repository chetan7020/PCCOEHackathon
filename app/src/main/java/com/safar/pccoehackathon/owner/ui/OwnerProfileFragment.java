package com.safar.pccoehackathon.owner.ui;

import android.Manifest;
import android.app.Dialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.safar.pccoehackathon.LoginActivity;
import com.safar.pccoehackathon.OwnerSignUpActivity;
import com.safar.pccoehackathon.R;
import com.safar.pccoehackathon.UserModel;
import com.safar.pccoehackathon.databinding.FragmentOwnerProfileBinding;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OwnerProfileFragment extends Fragment {

    private FragmentOwnerProfileBinding binding;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private final static int REQUEST_CODE=100;
    private EditText etLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    double lat, lang;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOwnerProfileBinding.inflate(getLayoutInflater());
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        getProfileData();
        binding.btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name, messName, phoneNumber, messLocation, upi;

                name = binding.tvName.getText().toString().trim();
                messName = binding.tvMessName.getText().toString().trim();
                phoneNumber = binding.tvPhoneNumber.getText().toString().trim();
                messLocation = binding.tvLocation.getText().toString().trim();
                upi = binding.tvUPI.getText().toString().trim();

                createDialog(name, messName, phoneNumber, messLocation, upi);
            }
        });

        binding.btnProfileLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });

        return binding.getRoot();

    }

    private void getProfileData() {
        Log.d("TAG", "getProfileData: "+firebaseAuth.getCurrentUser().getEmail());
        firebaseFirestore
                .collection("Owner")
                .document(firebaseAuth.getCurrentUser().getEmail())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        String name, messName, phoneNumber, messLocation, upi;

                        name = value.getString("name");
                        messName = value.getString("messname");
                        phoneNumber = value.getString("ownerphone");
                        messLocation = value.getString("location");
                        upi = value.getString("upi");

                        Log.d("TAG", "onEvent: "+name);

                        binding.tvName.setText(name);
                        binding.tvMessName.setText(messName);
                        binding.tvPhoneNumber.setText(phoneNumber);
                        binding.tvLocation.setText(messLocation);
                        binding.tvUPI.setText(upi);
                    }
                });
    }

    private void createDialog(String name, String messName, String ownerphone, String messLocation, String upi) {
        Dialog dialog = new Dialog(getActivity());

        dialog.setContentView(R.layout.owner_profile_edit_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        EditText etName, etMessName, etPhoneNumber, etUPI;
        Button btnChangeLocation, btnSaveChanges;

        btnSaveChanges = dialog.findViewById(R.id.btnSaveChanges);
        btnChangeLocation = dialog.findViewById(R.id.btnChangeLocation);

        etName = dialog.findViewById(R.id.etName);
        etMessName = dialog.findViewById(R.id.etMessName);
        etPhoneNumber = dialog.findViewById(R.id.etPhoneNumber);
        etLocation = dialog.findViewById(R.id.etLocation);
        etUPI = dialog.findViewById(R.id.etUPI);

        etName.setText(name);
        etMessName.setText(messName);
        etPhoneNumber.setText(ownerphone);
        etUPI.setText(upi);
        etLocation.setText(messLocation);

        btnChangeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLastLocation();
            }
        });

        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GeoPoint geo_pointLocation = new GeoPoint(lat, lang);
                Map<String, Object> data = new HashMap<>();
                data.put("name", etName.getText().toString().trim());
                data.put("messname", etMessName.getText().toString().trim());
                data.put("ownerphone", etPhoneNumber.getText().toString().trim());
                data.put("location", etLocation.getText().toString().trim());
                data.put("upi", etUPI.getText().toString().trim());
                data.put("geo_pointLocation", geo_pointLocation);

                firebaseFirestore
                        .collection("Owner")
                        .document(firebaseAuth.getCurrentUser().getEmail())
                        .update(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getActivity(), "Profile Updated", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Failed to Update", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        });
            }
        });
        dialog.show();
    }
    private void getLastLocation() {

        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {

                            if(location!=null)
                            {
                                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                    etLocation.setText(addresses.get(0).getAddressLine(0));
                                    lat = addresses.get(0).getLatitude();
                                    lang = addresses.get(0).getLongitude();
                                } catch (IOException e)
                                {
                                    e.printStackTrace();
                                }

                            }
                        }
                    });
        }
        else
        {
            askPermission();
        }

    }

    private void askPermission() {

        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE)
        {
            if(grantResults.length>0 &&  grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                getLastLocation();
            }
            else
            {
                Toast.makeText(getActivity(), "Please provide required Permission", Toast.LENGTH_SHORT).show();
            }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}