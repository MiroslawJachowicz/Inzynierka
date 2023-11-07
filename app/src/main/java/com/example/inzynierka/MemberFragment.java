package com.example.inzynierka;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


public class MemberFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private LinearLayout memberLinearLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_member, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        memberLinearLayout = view.findViewById(R.id.linearLayoutMembers);

        getCurrentMemberClub();
    }

    private void getCurrentMemberClub() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            // Załóżmy, że mamy kolekcję o nazwie 'Users', gdzie każdy dokument ma pole 'userId'
            db.collection("Users")
                    .whereEqualTo("userId", currentUserId)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Pobieramy pierwszy (i jedyny) dokument w wyniku zapytania
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            String userClub = documentSnapshot.getString("club");
                            if (userClub != null) {
                                loadUsersFromSameClub(userClub);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle the error
                    });
        }
    }

    private void loadUsersFromSameClub(String userClub) {
        db.collection("Users")
                .whereEqualTo("club", userClub)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("username");
                            String surname = document.getString("surname");
                            String role = document.getString("role");
                            addUserToScrollView(name, surname, role);
                        }
                    } else {
                        // Handle the error
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void addUserToScrollView(String name, String surname, String role) {
        // Inflate the custom layout for each user item
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View userLayout = inflater.inflate(R.layout.member_layout, memberLinearLayout, false);

        // Find the views inside the inflated layout
        ImageView imageView = userLayout.findViewById(R.id.imageViewMember);
        TextView textViewName = userLayout.findViewById(R.id.textViewMemberNameSurname);
        TextView textViewRole = userLayout.findViewById(R.id.textViewMemberRole);

        // Assuming you have a drawable resource named 'person_icon'
        imageView.setImageResource(R.drawable.person_icon);
        textViewName.setText(name+" "+surname);
        textViewRole.setText(role);

        // Add the inflated layout to the memberLinearLayout
        memberLinearLayout.addView(userLayout);
    }
}
