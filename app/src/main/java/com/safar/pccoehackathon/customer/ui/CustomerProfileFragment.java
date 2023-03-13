package com.safar.pccoehackathon.customer.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.safar.pccoehackathon.LoginActivity;
import com.safar.pccoehackathon.R;
import com.safar.pccoehackathon.databinding.FragmentDashboardBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomerProfileFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    String GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
    int GOOGLE_PAY_REQUEST_CODE = 123;

    final int PAY_REQUEST = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(getLayoutInflater());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        setProfile();
        binding.btnProfileLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        binding.btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProfile();
            }
        });


        return binding.getRoot();

    }

    private void editProfile() {
        Dialog dialog = new Dialog(getActivity());

        dialog.setContentView(R.layout.customer_edit_profile_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        EditText etName, etPreferances, etPhoneNumber;
        Button btnSaveChanges;

        etName = dialog.findViewById(R.id.etName);
        etPreferances = dialog.findViewById(R.id.etPreferances);
        etPhoneNumber = dialog.findViewById(R.id.etPhoneNumber);
        btnSaveChanges = dialog.findViewById(R.id.btnSaveChanges);

        String name = binding.tvName.getText().toString().trim();
        String preferances = binding.tvPreference.getText().toString().trim();
        String phoneNumber = binding.tvPhoneNumber.getText().toString().trim();

        etName.setText(name);
        etPreferances.setText(preferances);
        etPhoneNumber.setText(phoneNumber);
        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btnSaveChanges.setEnabled(false);

                String name = etName.getText().toString().trim();
                String preferances = etPreferances.getText().toString().trim();
                String phoneNumber = etPhoneNumber.getText().toString().trim();

                Map<String, Object> data = new HashMap<>();
                data.put("name", name);
                data.put("item", preferances);
                data.put("customerphone", phoneNumber);

                firebaseFirestore
                        .collection("Customer")
                        .document(firebaseAuth.getCurrentUser().getEmail())
                        .update(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getActivity(), "Profile Updated", Toast.LENGTH_SHORT).show();
                                btnSaveChanges.setEnabled(true);
                                dialog.cancel();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Failed to Update", Toast.LENGTH_SHORT).show();
                                btnSaveChanges.setEnabled(true);
                                dialog.cancel();
                            }
                        });

            }
        });

        dialog.show();
    }

    private void setProfile() {
        firebaseFirestore
                .collection("Customer")
                .document(firebaseAuth.getCurrentUser().getEmail())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        String name = value.getString("name");
                        String item = value.getString("item");
                        String customerphone = value.getString("customerphone");


                        binding.tvName.setText(name);
                        binding.tvPreference.setText(item);
                        binding.tvPhoneNumber.setText(customerphone);
                    }
                });
    }

    private void payUsingUpi() {

        Uri uri =
                new Uri.Builder()
                        .scheme("upi")
                        .authority("pay")
                        .appendQueryParameter("pa", "aheryash2004@okicici")
                        .appendQueryParameter("pn", "Yash Sandip Aher")
                        .build();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);


        Intent chooser = Intent.createChooser(intent, "Pay With");

        if (null != chooser.resolveActivity(getActivity().getPackageManager())) {
            startActivityForResult(chooser, PAY_REQUEST);
        } else {
            Toast.makeText(getActivity(), "No Upi Id Found", Toast.LENGTH_SHORT).show();
        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PAY_REQUEST) {
            if (isInternetAvailable(getActivity())) {
                if (data == null) {
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    String temp = "nothing";
                    Toast.makeText(getActivity(), "Transaction not complete", Toast.LENGTH_SHORT).show();
                } else {
                    String text = data.getStringExtra("response");
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add(text);

                    upiPaymentCheck(text);
                }
            }
        }

    }

    void upiPaymentCheck(String data) {
        String str = data;
        String payment_cancel = "";
        String status = "";
        String response[] = str.split("&");

        for (int i = 0; i < response.length; i++) {
            String equalStr[] = response[i].split("");
            if (equalStr.length >= 2) {
                if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                    status = equalStr[1].toLowerCase();
                }
            } else {
                payment_cancel = "Payment Cancel";
            }

            if (status.equals("success")) {
                Toast.makeText(getActivity(), "Transaction Successfully", Toast.LENGTH_SHORT).show();
            } else if ("Payment Cancel".equals(payment_cancel)) {
                Toast.makeText(getActivity(), "Payment Cancel by user", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Transaction Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo.isConnected() && networkInfo.isConnectedOrConnecting() && networkInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }

}