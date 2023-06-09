package com.safar.pccoehackathon;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.messaging.FirebaseMessaging;
import com.safar.pccoehackathon.databinding.ActivityOwnerSignUpBinding;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class OwnerSignUpActivity extends AppCompatActivity {

    private static final String TAG = "OwnerSignUpActivity";

    double lat, lang;
    ActivityOwnerSignUpBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore firebaseFirestore;
    ProgressDialog progressDialog;
    FusedLocationProviderClient fusedLocationProviderClient;
    private final static int REQUEST_CODE=100;
    String sample,currentlocation;
    private String token;
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

                                    String geohash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(lat, lang));

                                    // Code to get Device Token

                                    FirebaseMessaging.getInstance().getToken()
                                            .addOnCompleteListener(new OnCompleteListener<String>() {
                                                @Override
                                                public void onComplete(@NonNull Task<String> task) {
                                                    if (!task.isSuccessful()) {
                                                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                                        return;
                                                    }

                                                    // Get new FCM registration token
                                                    token = task.getResult();

                                                }
                                            });

                                    String userUID = "69";
                                    FirebaseUser firebaseUser;
                                    firebaseUser = auth.getCurrentUser();
                                    if (firebaseUser != null) {
                                        userUID = firebaseUser.getUid().toString();
                                    }

                                    firebaseFirestore.collection("Owner")
                                            .document(email)
                                            .set(new UserModel(id, name, messname, ownerphone, upi, email, monthlyPrice, location, lat, lang, new GeoPoint(lat, lang), geohash,token,userUID));

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

                Intent intent =new Intent(OwnerSignUpActivity.this,map.class);
                intent.putExtra("passdata",currentlocation);
                startActivityForResult(intent,REQUEST_CODE);


//                getLastLocation();

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                currentlocation = data.getStringExtra("location");
                lat = Double.parseDouble(data.getStringExtra("lat"));
                lang = Double.parseDouble(data.getStringExtra("lang"));
                Toast.makeText(this, currentlocation, Toast.LENGTH_SHORT).show();
                binding.etlocation.setText(currentlocation);
            }
        }
    }
}
