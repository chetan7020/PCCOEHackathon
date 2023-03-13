package com.safar.pccoehackathon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.safar.pccoehackathon.databinding.ActivitySignUpCustomerBinding;

public class SignUpCustomer extends AppCompatActivity {
    ActivitySignUpCustomerBinding binding;
    FirebaseAuth auth;
    ProgressDialog progressDialog;
    FirebaseFirestore firebaseFirestore;

    String[] items = {"Veg","Non-Veg","Veg / Nog-Veg"};
    String item;

    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpCustomerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Register Now");
        progressDialog.setMessage("Please wait loading");



        adapterItems = new ArrayAdapter<String>(this,R.layout.list_item, items);
        binding.actvPreference.setAdapter(adapterItems);

        binding.actvPreference.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                item = parent.getItemAtPosition(position).toString();

            }
        });



        binding.btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpCustomer.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


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
                    Drawable drawable = ContextCompat.getDrawable(SignUpCustomer.this, R.drawable.baseline_check_circle_24);
                    drawable = DrawableCompat.wrap(drawable);
                    drawable.setBounds( 0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

                    binding.etemail.setCompoundDrawables(null, null, drawable, null);
                }
                else
                {
                    Drawable drawable = ContextCompat.getDrawable(SignUpCustomer.this, R.drawable.baseline_check_circle_outline_24);
                    drawable = DrawableCompat.wrap(drawable);
                    drawable.setBounds( 0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    binding.etemail.setCompoundDrawables(null, null, drawable, null);
                }
            }
        });




        binding.btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = binding.etname.getText().toString();
                String customerphone = binding.etphone.getText().toString();
                String email = binding.etemail.getText().toString();
                String password = binding.etpassword.getText().toString();


                if (binding.etname.getText().toString().trim().isEmpty())
                {
                    binding.etname.setError("Please Enter Name");
                } else if (binding.etphone.getText().toString().trim().isEmpty())
                {
                    binding.etphone.setError("Please Enter Mess name");
                }
                else if(binding.etphone.getText().length()!=10)
                {
                    Toast.makeText(SignUpCustomer.this, "Enter valid Mobile number", Toast.LENGTH_SHORT).show();
                }
                else if (binding.etemail.getText().toString().trim().isEmpty()) {
                    binding.etemail.setError("Please Enter email");
                }
                else if (binding.etpassword.getText().toString().trim().isEmpty())
                {
                    binding.etpassword.setError("Please Enter password");
                }
                else if(binding.etpassword.getText().length()<6)
                {
                    binding.etpassword.setError("Password must be greater than 6 character");
                }
                else if(binding.actvPreference.getText().toString().isEmpty())
                {
                    Toast.makeText(SignUpCustomer.this, "Select location first", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    progressDialog.show();
                    auth.createUserWithEmailAndPassword(email,password)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Intent intent = new Intent(SignUpCustomer.this,LoginActivity.class);
                                    startActivity(intent);
                                    progressDialog.cancel();

                                    firebaseFirestore.collection("Customer")
                                            .document(email)
                                            .set(new UserModel1(name,customerphone,email,password,item));

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignUpCustomer.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    progressDialog.cancel();
                                }
                            });
                }

            }
        });



    }
}