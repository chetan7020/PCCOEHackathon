package com.safar.pccoehackathon;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class Add_review extends AppCompatActivity {

    private static final String TAG = "Add_review";
    private TextView descibe_review;
    private MaterialRatingBar rating_bar;

    private Button btn_post;

    private FirebaseFirestore firebaseFirestore;

    private FirebaseAuth firebaseAuth;

    String email;
    int avg_review;
    int customer_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        descibe_review = findViewById(R.id.descibe_review);
        rating_bar = findViewById(R.id.rating_bar);
        btn_post = findViewById(R.id.btn_post);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        email = getIntent().getStringExtra("email");
        avg_review = Integer.parseInt(getIntent().getStringExtra("avg_review"));
        customer_count = Integer.parseInt(getIntent().getStringExtra("customer_count"));

        Log.d(TAG, "onCreate: " + email + avg_review + customer_count);

        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btn_post.setEnabled(false);
                String text = descibe_review.getText().toString().trim();
                int star = (int) rating_bar.getRating();

                firebaseFirestore.collection("Owner")
                        .document(email)
                        .collection("reviews")
                        .document(firebaseAuth.getCurrentUser().getEmail()).set(new ReviewModel(text, star)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                boolean flag = false;

                                firebaseFirestore
                                        .collection("Owner")
                                        .document(email)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        Map<String, Object> data = new HashMap<>();
                                                        data.put("avg_review", String.valueOf((avg_review+star)/(customer_count)));

                                                        firebaseFirestore
                                                                .collection("Owner")
                                                                .document(email)
                                                                .update(data)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        Toast.makeText(Add_review.this, "Review Added", Toast.LENGTH_SHORT).show();

                                                                        btn_post.setEnabled(true);
                                                                        descibe_review.setText("");
                                                                        rating_bar.setRating(0);

                                                                        btn_post.setEnabled(true);

                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(Add_review.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                        btn_post.setEnabled(true);
                                                                    }

                                                                });
                                                    } else {
                                                        Map<String, Object> data = new HashMap<>();
                                                        data.put("customer_count", String.valueOf(customer_count+1));
                                                        data.put("avg_review", String.valueOf((avg_review+star)/(customer_count+1)));

                                                        firebaseFirestore
                                                                .collection("Owner")
                                                                .document(email)
                                                                .update(data)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        Toast.makeText(Add_review.this, "Review Added", Toast.LENGTH_SHORT).show();

                                                                        btn_post.setEnabled(true);
                                                                        descibe_review.setText("");
                                                                        rating_bar.setRating(0);

                                                                        btn_post.setEnabled(true);

                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(Add_review.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                        btn_post.setEnabled(true);
                                                                    }

                                                                });
                                                    }
                                                } else {
                                                    Toast.makeText(Add_review.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Add_review.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                btn_post.setEnabled(true);

                            }
                        });
            }
        });


    }
}