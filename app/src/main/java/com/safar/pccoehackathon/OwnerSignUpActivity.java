package com.safar.pccoehackathon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.safar.pccoehackathon.databinding.ActivityOwnerSignUpBinding;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class OwnerSignUpActivity extends AppCompatActivity {

    ActivityOwnerSignUpBinding binding;
    FirebaseAuth auth;
    ProgressDialog progressDialog;
    FirebaseFirestore firebaseFirestore;
    FusedLocationProviderClient fusedLocationProviderClient;
    double lat, lang;
    private final static int REQUEST_CODE=100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOwnerSignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.etlocation.setFocusable(false);
        binding.etlocation.setEnabled(false);
        binding.etlocation.setCursorVisible(false);
        binding.etlocation.setKeyListener(null);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Register Now");
        progressDialog.setMessage("Please wait loading");

        binding.etemail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void afterTextChanged(Editable s) {

                if(Patterns.EMAIL_ADDRESS.matcher(binding.etemail.getText().toString()).matches())
                {
                    Drawable drawable = ContextCompat.getDrawable(OwnerSignUpActivity.this, R.drawable.baseline_check_circle_24);
                    drawable = DrawableCompat.wrap(drawable);
                    drawable.setBounds( 0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

                    binding.etemail.setCompoundDrawables(null, null, drawable, null);
                }
                else
                {
                    Drawable drawable = ContextCompat.getDrawable(OwnerSignUpActivity.this, R.drawable.baseline_check_circle_outline_24);
                    drawable = DrawableCompat.wrap(drawable);
                    drawable.setBounds( 0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    binding.etemail.setCompoundDrawables(null, null, drawable, null);
                }
            }
        });


        binding.btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = createID();
                String name = binding.etname.getText().toString();
                String messname = binding.etmessname.getText().toString();
                String ownerphone = binding.etphone.getText().toString();
                String upi = binding.etupi.getText().toString();
                String email = binding.etemail.getText().toString();
                String password = binding.etpassword.getText().toString();
                String monthlyPrice = binding.etMonthlyPrice.getText().toString();
                String location = binding.etlocation.getText().toString();

                if (binding.etname.getText().toString().trim().isEmpty()) {
                    binding.etname.setError("Please Enter Name");
                } else if (binding.etmessname.getText().toString().trim().isEmpty()) {
                    binding.etmessname.setError("Please Enter Mess name");
                } else if (binding.etphone.getText().toString().trim().isEmpty())
                {
                    binding.etphone.setError("Please Enter phone");
                }
                else if(binding.etphone.getText().length()!=10)
                {
                    Toast.makeText(OwnerSignUpActivity.this, "Enter valid Mobile number", Toast.LENGTH_SHORT).show();
                }
                else if (binding.etupi.getText().toString().trim().isEmpty()) {
                    binding.etupi.setError("Please Enter upi ID");
                } else if (binding.etemail.getText().toString().trim().isEmpty()) {
                    binding.etemail.setError("Please Enter email");
                } else if (binding.etpassword.getText().toString().trim().isEmpty())
                {
                    binding.etpassword.setError("Please Enter password");
                }
                else if(binding.etpassword.getText().length()<6)
                {
                    binding.etpassword.setError("Password must be greater than 6 character");
                }
                else if(binding.etlocation.getText().toString().trim().isEmpty())
                {
                    Toast.makeText(OwnerSignUpActivity.this, "Select your location first!", Toast.LENGTH_SHORT).show();
                }
                else {
                    progressDialog.show();
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Intent intent = new Intent(OwnerSignUpActivity.this, LoginActivity.class);

                                    GeoPoint geo_pointLocation = new GeoPoint(lat, lang);

                                    firebaseFirestore.collection("Owner")
                                            .document(email)
                                            .set(new UserModel(id, name, messname, ownerphone, upi, email, monthlyPrice, location, geo_pointLocation));

                                    //(String id, String name, String messname, String ownerphone, String upi, String email, String monthlyPrice, String location, String lat, String lang)

                                    startActivity(intent);
                                    progressDialog.cancel();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(OwnerSignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    progressDialog.cancel();

                                }
                            });

                }
            }
        });

        binding.btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OwnerSignUpActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.btngetlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getLastLocation();

            }
        });

    }

    private void getLastLocation() {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {

                            if(location!=null)
                            {
                                Geocoder geocoder = new Geocoder(OwnerSignUpActivity.this,Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                    binding.etlocation.setText(addresses.get(0).getAddressLine(0));
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

        ActivityCompat.requestPermissions(OwnerSignUpActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);

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
                Toast.makeText(this, "Please provide required Permission", Toast.LENGTH_SHORT).show();
            }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public String createID() {
        String id = "";
        id = String.valueOf(System.currentTimeMillis());
        return id;
    }




}
