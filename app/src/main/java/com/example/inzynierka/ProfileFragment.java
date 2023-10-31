package com.example.inzynierka;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfileFragment extends Fragment {

    TextView nameTextView;
    TextView surnameTextView;
    TextView emailTextView;
    TextView roleTextView;
    TextView clubTextView;
    Button logout_btn;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            nameTextView = requireView().findViewById(R.id.textViewProfileName);
            surnameTextView = requireView().findViewById(R.id.textViewProfileSurname);
            emailTextView = requireView().findViewById(R.id.textViewProfileEmail);
            roleTextView = requireView().findViewById(R.id.textViewProfileRole);
            clubTextView = requireView().findViewById(R.id.textViewProfileClub);

            CollectionReference usersRef = FirebaseFirestore.getInstance().collection("Users");
            usersRef.whereEqualTo("userId", currentUserId).limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                    String name = documentSnapshot.getString("username");
                    String surname = documentSnapshot.getString("surname");
                    String email = documentSnapshot.getString("email");
                    String club = documentSnapshot.getString("club");
                    String role = documentSnapshot.getString("role");

                    nameTextView.setText(name);
                    surnameTextView.setText(surname);
                    emailTextView.setText(email);
                    roleTextView.setText(role);
                    clubTextView.setText(club);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        }
        firebaseAuth = FirebaseAuth.getInstance();
        logout_btn = requireView().findViewById(R.id.profile_logout_button);
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseAuth.signOut();

                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();


            }
        });
    }
}