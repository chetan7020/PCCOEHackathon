package com.safar.pccoehackathon.owner.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.safar.pccoehackathon.Plate;
import com.safar.pccoehackathon.R;
import com.safar.pccoehackathon.databinding.FragmentOwnerFoodBinding;

import java.util.HashMap;
import java.util.Map;

public class OwnerFoodFragment extends Fragment {

    private FragmentOwnerFoodBinding binding;
    private FirebaseFirestore firebaseFirestore;
    private Dialog dialog;
    private FirebaseAuth firebaseAuth;

    String[] foodtypeitem = {"Veg","Non-Veg","Veg / Nog-Veg"};
    String[] foodallergies = {"throat","mutton","chapati"};

    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;
    ArrayAdapter<String> adapterItems1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOwnerFoodBinding.inflate(getLayoutInflater());

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        displayPlate();


        binding.fabAddPlate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(getActivity());

                dialog.setContentView(R.layout.owner_food_new_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);



                EditText etPlateName, etPlatePrice, etContents;
                AutoCompleteTextView actvPlateType, actvAllergies;
                ImageView upload_img;
                CheckBox cbGlutenFree;
                Button btnAddDish;

                etPlateName = dialog.findViewById(R.id.etPlateName);
                etPlatePrice = dialog.findViewById(R.id.etPlatePrice);
                etContents = dialog.findViewById(R.id.etContents);

                actvPlateType = dialog.findViewById(R.id.actvPlateType);
                actvAllergies = dialog.findViewById(R.id.actvAllergies);

                cbGlutenFree = dialog.findViewById(R.id.cbGlutenFree);

                btnAddDish = dialog.findViewById(R.id.btnAddDish);



                autoCompleteTextView = dialog.findViewById(R.id.actvPlateType);
                adapterItems = new ArrayAdapter<String>(getActivity(),R.layout.list_item_1, foodtypeitem);
                autoCompleteTextView.setAdapter(adapterItems);

                autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String item = adapterView.getItemAtPosition(i).toString();
                    }
         });

                autoCompleteTextView = dialog.findViewById(R.id.actvAllergies);
                adapterItems1 = new ArrayAdapter<String>(getActivity(),R.layout.list_item_1, foodallergies);
                autoCompleteTextView.setAdapter(adapterItems1);

                autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String item = adapterView.getItemAtPosition(i).toString();
                    }
                });


                btnAddDish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        String plateName, allergies, glutenFree, price, type, contents;

                        glutenFree = "No";

                        plateName = etPlateName.getText().toString().trim();
                        price = etPlatePrice.getText().toString().trim();
                        allergies = actvAllergies.getText().toString().trim();
                        contents = etContents.getText().toString().trim();

                        if (cbGlutenFree.isChecked()) {
                            glutenFree = "Yes";
                        }
                        type = actvPlateType.getText().toString().trim();

                        addPlate(plateName, type, allergies, glutenFree, price, contents);
                    }
                });
                dialog.show();
            }
        });

        return binding.getRoot();

    }

    private void displayPlate() {
        firebaseFirestore
                .collection("Owner")
                .document(firebaseAuth.getCurrentUser().getEmail())
                .collection("plates")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        //String plateName, String type, String allergies, String glutenFree, String price
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            String id = dc.getDocument().getId();
                            String plateName = dc.getDocument().getData().get("plateName").toString();
                            String type = dc.getDocument().getData().get("type").toString();
                            String allergies = dc.getDocument().getData().get("allergies").toString();
                            String glutenFree = dc.getDocument().getData().get("glutenFree").toString();
                            String price = dc.getDocument().getData().get("price").toString();
                            String contents = dc.getDocument().getData().get("contents").toString();
                            String available = dc.getDocument().getData().get("available").toString();
                            switch (dc.getType()) {
                                case ADDED:
                                    createCard(id, plateName, type, allergies, glutenFree, price, contents, available);
                                    break;
                                case MODIFIED:
                                    updatePlate(id, plateName, type, allergies, glutenFree, price, contents, available);
                                    break;
                                case REMOVED:
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


    private void updatePlate(String id, String plateName, String type, String allergies, String glutenFree, String price, String contents, String available) {
        for (int i = 0; i < binding.llData.getChildCount(); i++) {

            TextView tvID = binding.llData.getChildAt(i).findViewById(R.id.tvID);
            TextView tvDishName = binding.llData.getChildAt(i).findViewById(R.id.tvDishName);
            TextView etPlatePrice = binding.llData.getChildAt(i).findViewById(R.id.tvPrice);
            TextView etContents = binding.llData.getChildAt(i).findViewById(R.id.tvContents);
            TextView tvType = binding.llData.getChildAt(i).findViewById(R.id.tvType);
            TextView tvAllergies = binding.llData.getChildAt(i).findViewById(R.id.tvAllergies);
            TextView tvGluttenFree = binding.llData.getChildAt(i).findViewById(R.id.tvGluttenFree);
            TextView tvAvailable = binding.llData.getChildAt(i).findViewById(R.id.tvAvailable);


            if (tvID.getText().toString().trim().equals(id)) {
                tvDishName.setText(plateName);
                etPlatePrice.setText(price);
                etContents.setText(contents);
                tvType.setText(type);
                tvAllergies.setText(allergies);
                tvGluttenFree.setText(glutenFree);
                tvAvailable.setText(available);

            }

        }
    }

    private void createCard(String id, String plateName, String type, String allergies, String glutenFree, String price, String contents, String available) {
        View plateView = getLayoutInflater().inflate(R.layout.owner_food_layout, null, false);

        TextView tvDishName, tvID, tvContents, tvGluttenFree, tvAllergies, tvType, tvPrice, tvAvailable;
        Button btnEdit, btnDelete;

        btnEdit = plateView.findViewById(R.id.btnEdit);
        btnDelete = plateView.findViewById(R.id.btnDelete);

        tvID = plateView.findViewById(R.id.tvID);
        tvDishName = plateView.findViewById(R.id.tvDishName);
        tvContents = plateView.findViewById(R.id.tvContents);
        tvGluttenFree = plateView.findViewById(R.id.tvGluttenFree);
        tvAllergies = plateView.findViewById(R.id.tvAllergies);
        tvType = plateView.findViewById(R.id.tvType);
        tvPrice = plateView.findViewById(R.id.tvPrice);
        tvAvailable = plateView.findViewById(R.id.tvAvailable);

        tvID.setText(id);
        tvDishName.setText(plateName);
        tvContents.setText(contents);
        tvGluttenFree.setText(glutenFree);
        tvAllergies.setText(allergies);
        tvType.setText(type);
        tvPrice.setText(price);
        tvAvailable.setText(available);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(getActivity());

                dialog.setContentView(R.layout.owner_food_edit_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                EditText etPlateName, etPlatePrice, etContents;
                AutoCompleteTextView actvPlateType, actvAllergies, actvAvailable;
                CheckBox cbGlutenFree;
                Button btnUpdateDish;

                etPlateName = dialog.findViewById(R.id.etPlateName);
                etPlatePrice = dialog.findViewById(R.id.etPlatePrice);
                etContents = dialog.findViewById(R.id.etContents);

                actvPlateType = dialog.findViewById(R.id.actvPlateType);
                actvAllergies = dialog.findViewById(R.id.actvAllergies);
                actvAvailable = dialog.findViewById(R.id.actvAvailable);

                cbGlutenFree = dialog.findViewById(R.id.cbGlutenFree);

                btnUpdateDish = dialog.findViewById(R.id.btnUpdateDish);

                etPlateName.setText(tvDishName.getText().toString().trim());
                etPlatePrice.setText(tvPrice.getText().toString().trim());
                etContents.setText(tvContents.getText().toString().trim());
                actvPlateType.setText(tvType.getText().toString().trim());
                actvAllergies.setText(tvAllergies.getText().toString().trim());
                actvAvailable.setText(tvAvailable.getText().toString().trim());
                btnUpdateDish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        String plateName, allergies, glutenFree, price, type, contents, available;

                        glutenFree = "No";

                        plateName = etPlateName.getText().toString().trim();
                        price = etPlatePrice.getText().toString().trim();
                        allergies = actvAllergies.getText().toString().trim();
                        contents = actvAllergies.getText().toString().trim();
                        available = actvAvailable.getText().toString().trim();

                        if (cbGlutenFree.isChecked()) {
                            glutenFree = "Yes";
                        }
                        type = actvPlateType.getText().toString().trim();

                        Map<String, Object> data = new HashMap<>();
                        data.put("plateName", plateName);
                        data.put("type", type);
                        data.put("contents", contents);
                        data.put("allergies", allergies);
                        data.put("glutenFree", glutenFree);
                        data.put("available", available);
                        data.put("price", price);

                        firebaseFirestore
                                .collection("Owner")
                                .document(firebaseAuth.getCurrentUser().getEmail()) //edit
                                .collection("plates")
                                .document(tvID.getText().toString().trim())
                                .update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getActivity(), "Plate Data Updated", Toast.LENGTH_SHORT).show();
                                        dialog.cancel();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        dialog.cancel();
                                    }
                                });

                    }
                });

                dialog.show();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFood(tvID.getText().toString());
            }
        });



        binding.llData.addView(plateView);
    }

    private void addPlate(String plateName, String type, String allergies, String glutenFree, String price, String contents) {
        String id = String.valueOf(System.currentTimeMillis());
        firebaseFirestore
                .collection("Owner")
                .document(firebaseAuth.getCurrentUser().getEmail()) //edit
                .collection("plates")
                .document(id)
                .set(new Plate(plateName, type, allergies, glutenFree, price, contents))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getActivity(), "Plate added", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to add plate", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
    }

    private void deleteFood(String id) {
        firebaseFirestore
                .collection("Owner")
                .document(firebaseAuth.getCurrentUser().getEmail()) //edit
                .collection("plates")
                .document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Food Data Deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}