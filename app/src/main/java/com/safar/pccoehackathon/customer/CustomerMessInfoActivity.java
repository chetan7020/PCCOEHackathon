package com.safar.pccoehackathon.customer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.safar.pccoehackathon.R;

import java.util.ArrayList;

public class CustomerMessInfoActivity extends AppCompatActivity {

    private String email;
    private FirebaseFirestore firebaseFirestore;
    private LinearLayout llData;
    private TextView tvMessName, tvLocation, tvPhoneNumber, tvEmail, tvUPI;
    private Button btnMakePayment;

    String GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
    int GOOGLE_PAY_REQUEST_CODE = 123;
    final int PAY_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_mess_info);

        email = getIntent().getStringExtra("email");
        firebaseFirestore = FirebaseFirestore.getInstance();

        tvMessName = findViewById(R.id.tvMessName);
        tvLocation = findViewById(R.id.tvLocation);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        tvEmail = findViewById(R.id.tvEmail);
        tvUPI = findViewById(R.id.tvUPI);

        btnMakePayment = findViewById(R.id.btnMakePayment);

        llData = findViewById(R.id.llData);

        setHeader(email);

        getDishes(email);

        btnMakePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payUsingUpi();
            }
        });

    }

    private void getDishes(String email) {
        firebaseFirestore
                .collection("Owner")
                .document(email)
                .collection("plates")
                .whereEqualTo("available","Yes")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            String id = dc.getDocument().getId();
                            String plateName = dc.getDocument().getData().get("plateName").toString();
                            String type = dc.getDocument().getData().get("type").toString();
                            String allergies = dc.getDocument().getData().get("allergies").toString();
                            String price = dc.getDocument().getData().get("price").toString();
                            String contents = dc.getDocument().getData().get("contents").toString();
                            String available = dc.getDocument().getData().get("available").toString();
                            switch (dc.getType()) {
                                case ADDED:
                                    createCard(id, plateName, price, available, contents, allergies, type);
                                    break;
                                case MODIFIED:
                                    updatePlate(id, plateName, price, available, contents, allergies, type);
                                    break;
                                case REMOVED:
                                    for (int i = 0; i < llData.getChildCount(); i++) {

                                        TextView tvID = llData.getChildAt(i).findViewById(R.id.tvID);

                                        String firebase_id = tvID.getText().toString().trim();

                                        if (firebase_id.equals(id)) {
                                            llData.removeView(llData.getChildAt(i));
                                        }
                                    }
                                    break;
                            }
                        }

                    }
                });
    }

    private void updatePlate(String id, String plateName, String price, String available, String contents, String allergies, String type) {
        for (int i = 0; i < llData.getChildCount(); i++) {

            TextView tvDishName, tvPrice, tvID, tvAvailable, tvContents, tvAllergies, tvType;

            tvDishName = llData.getChildAt(i).findViewById(R.id.tvDishName);
            tvPrice = llData.getChildAt(i).findViewById(R.id.tvPrice);
            tvID = llData.getChildAt(i).findViewById(R.id.tvID);
            tvAvailable = llData.getChildAt(i).findViewById(R.id.tvAvailable);
            tvContents = llData.getChildAt(i).findViewById(R.id.tvContents);
            tvAllergies = llData.getChildAt(i).findViewById(R.id.tvAllergies);
            tvType = llData.getChildAt(i).findViewById(R.id.tvType);

            if (tvID.getText().toString().trim().equals(id)) {
                tvDishName.setText(plateName);
                tvPrice.setText(price);
                tvContents.setText(contents);
                tvType.setText(type);
                tvAllergies.setText(allergies);
                tvAvailable.setText(available);

            }

        }
    }

    private void createCard(String id, String plateName, String price, String available, String contents, String allergies, String type) {
        View messDishView = getLayoutInflater().inflate(R.layout.layout_customer_mess_dish_info, null, false);

        TextView tvDishName, tvPrice, tvID, tvAvailable, tvContents, tvAllergies, tvType;

        tvDishName = messDishView.findViewById(R.id.tvDishName);
        tvPrice = messDishView.findViewById(R.id.tvPrice);
        tvID = messDishView.findViewById(R.id.tvID);
        tvAvailable = messDishView.findViewById(R.id.tvAvailable);
        tvContents = messDishView.findViewById(R.id.tvContents);
        tvAllergies = messDishView.findViewById(R.id.tvAllergies);
        tvType = messDishView.findViewById(R.id.tvType);

        tvDishName.setText(plateName);
        tvPrice.setText(price);
        tvID.setText(id);
        tvAvailable.setText(available);
        tvContents.setText(contents);
        tvAllergies.setText(allergies);
        tvType.setText(type);

        llData.addView(messDishView);
    }

    private void setHeader(String email) {
        firebaseFirestore
                .collection("Owner")
                .document(email)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        String messName = value.getString("messname");
                        String location = value.getString("location");
                        String phoneNumber = value.getString("ownerphone");
                        String email = value.getString("email");
                        String upi = value.getString("upi");

                        tvMessName.setText(messName);
                        tvLocation.setText(location);
                        tvPhoneNumber.setText(phoneNumber);
                        tvEmail.setText(email);
                        tvUPI.setText(upi);
                    }
                });
    }

    private void payUsingUpi() {

        Uri uri =
                new Uri.Builder()
                        .scheme("upi")
                        .authority("pay")
                        .appendQueryParameter("pa", tvUPI.getText().toString().trim())
                        .appendQueryParameter("pn", tvMessName.getText().toString().trim())
                        .build();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);


        Intent chooser = Intent.createChooser(intent, "Pay With");

        if (null != chooser.resolveActivity(CustomerMessInfoActivity.this.getPackageManager())) {
            startActivityForResult(chooser, PAY_REQUEST);
        } else {
            Toast.makeText(CustomerMessInfoActivity.this, "No Upi Id Found", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PAY_REQUEST) {
            if (isInternetAvailable(CustomerMessInfoActivity.this)) {
                if (data == null) {
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    String temp = "nothing";
                    Toast.makeText(CustomerMessInfoActivity.this, "Transaction not complete", Toast.LENGTH_SHORT).show();
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

        Log.d("TAG", "upiPaymentCheck: "+data);
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
                Toast.makeText(CustomerMessInfoActivity.this, "Transaction Successfully", Toast.LENGTH_SHORT).show();
            } else if ("Payment Cancel".equals(payment_cancel)) {
                Toast.makeText(CustomerMessInfoActivity.this, "Payment Cancel by user", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CustomerMessInfoActivity.this, "Transaction Failed", Toast.LENGTH_SHORT).show();
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