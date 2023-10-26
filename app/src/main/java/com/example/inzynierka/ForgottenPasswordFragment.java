package com.example.inzynierka;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class ForgottenPasswordFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore Firebasedb = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference= Firebasedb.collection("Users");

    EditText editTextTextEmailAddress;
    Button password_restart_btn;
    Button back_btn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgotten_password, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        firebaseAuth=FirebaseAuth.getInstance();

        editTextTextEmailAddress=requireView().findViewById(R.id.editTextTextForgottenEmailAddress);

        password_restart_btn=requireView().findViewById(R.id.password_restart_button);
        password_restart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=editTextTextEmailAddress.getText().toString().trim();
                if (!TextUtils.isEmpty(email)) {
                    collectionReference.whereEqualTo("email", editTextTextEmailAddress.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (!task.getResult().isEmpty()) {
                                            firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(requireContext(), "Reset password link has been sent", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }else {
                                            Toast.makeText(requireContext(), "E-mail is not assigned to any account", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                } else {
                    Toast.makeText(requireContext(), "E-mail field is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        back_btn = requireView().findViewById(R.id.back_to_login_button);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(ForgottenPasswordFragmentDirections.actionForgottenPasswordFragmentToLoginFragment());
            }
        });
    }
}