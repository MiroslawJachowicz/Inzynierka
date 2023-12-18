package com.example.inzynierka;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class MemberFragment extends Fragment {

    private FirebaseFirestore Firebasedb = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private LinearLayout memberLinearLayout;
    private Map<String, int[]> userGoalsAssistsMap = new HashMap<>();

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

            Firebasedb.collection("Users")
                    .whereEqualTo("userId", currentUserId)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                                String userClub = documentSnapshot.getString("club");
                                String userRole = documentSnapshot.getString("role");
                                if (userClub != null) {
                                    loadUsersFromSameClub(userClub,userRole);
                                }
                            }
                    })
                    .addOnFailureListener(e -> {
                    });

        }
    }
    private void loadUsersFromSameClub(String userClub, String userRole) {
        Firebasedb.collection("Users").whereEqualTo("club", userClub).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("username");
                            String surname = document.getString("surname");
                            String role = document.getString("role");
                            String userId = document.getString("userId");
                            addUserToScrollView(name, surname, role,userId,userRole);
                        }
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void addUserToScrollView(String name, String surname, String role, String userId,String userRole) {
        if (isAdded()) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View userLayout = inflater.inflate(R.layout.member_layout, memberLinearLayout, false);

            ImageView imageViewIncreaseGoals = userLayout.findViewById(R.id.imageViewIncrementGoals);
            ImageView imageViewDecreaseGoals = userLayout.findViewById(R.id.imageViewDecrementGoals);
            ImageView imageViewIncreaseAssist = userLayout.findViewById(R.id.imageViewIncrementAssists);
            ImageView imageViewDecreaseAssist = userLayout.findViewById(R.id.imageViewDecrementAssists);
            TextView textViewName = userLayout.findViewById(R.id.textViewMemberNameSurname);
            TextView textViewRole = userLayout.findViewById(R.id.textViewMemberRole);
            TextView textViewGoals = userLayout.findViewById(R.id.textViewMemberGoals);
            TextView textViewAssists = userLayout.findViewById(R.id.textViewMemberAssists);

            textViewName.setText(name + " " + surname);
            textViewRole.setText(role);

            fetchGoalsAndAssists(userId, textViewGoals, textViewAssists);

                imageViewIncreaseGoals.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       if(Objects.equals(userRole, "Trainer"))
                        updateGoalsInDatabase(userId, getUpdatedCount(userId, textViewGoals, true, 1));
                       else{
                           Toast.makeText(getActivity(), "Only trainer can change stats", Toast.LENGTH_SHORT).show();
                       }
                    }
                });

                imageViewDecreaseGoals.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Objects.equals(userRole, "Trainer"))
                        updateGoalsInDatabase(userId, getUpdatedCount(userId, textViewGoals, true, -1));
                        else{
                            Toast.makeText(getActivity(), "Only trainer can change stats", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                imageViewIncreaseAssist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Objects.equals(userRole, "Trainer"))
                        updateAssistsInDatabase(userId, getUpdatedCount(userId, textViewAssists, false, 1));
                        else{
                            Toast.makeText(getActivity(), "Only trainer can change stats", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                imageViewDecreaseAssist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Objects.equals(userRole, "Trainer"))
                        updateAssistsInDatabase(userId, getUpdatedCount(userId, textViewAssists, false, -1));
                        else{
                            Toast.makeText(getActivity(), "Only trainer can change stats", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            memberLinearLayout.addView(userLayout);
        }
    }

    private void fetchGoalsAndAssists(String userId, TextView textViewGoals, TextView textViewAssists) {
        Firebasedb.collection("Stats").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        int goals = documentSnapshot.getLong("goals") == null ? 0 : documentSnapshot.getLong("goals").intValue();
                        int assists = documentSnapshot.getLong("assists") == null ? 0 : documentSnapshot.getLong("assists").intValue();
                        userGoalsAssistsMap.put(userId, new int[]{goals, assists});

                        textViewGoals.setText("Goals: " + goals);
                        textViewAssists.setText("Assists: " + assists);
                    } else {
                        textViewGoals.setText("Goals: 0");
                        textViewAssists.setText("Assists: 0");

                        setDefaultValuesInDatabase(userId);
                        fetchGoalsAndAssists(userId,textViewGoals,textViewAssists);
                    }
                })
                .addOnFailureListener(e -> {
                });
    }

    private void setDefaultValuesInDatabase(String userId) {
        Map<String, Object> defaultData = new HashMap<>();
        defaultData.put("goals", 0);
        defaultData.put("assists", 0);

        Firebasedb.collection("Stats").document(userId)
                .set(defaultData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                })
                .addOnFailureListener(e -> {
                });
    }

    private int getUpdatedCount(String userId, TextView textView, boolean isGoals, int change) {
        int[] userValues = userGoalsAssistsMap.get(userId);

        if (userValues != null && userValues.length == 2) {
            int currentValue = isGoals ? userValues[0] : userValues[1];
            int newValue = currentValue + change;

            if (newValue >= 0) {
                textView.setText((isGoals ? "Goals: " : "Assists: ") + newValue);
                if (isGoals) {
                    userValues[0] = newValue;
                } else {
                    userValues[1] = newValue;
                }
                userGoalsAssistsMap.put(userId, userValues);

                return newValue;
            } else {
                return currentValue;
            }
        } else {
            return 0;
        }
    }

    private void updateGoalsInDatabase(String userId, int goals) {
        Map<String, Object> data = new HashMap<>();
        data.put("goals", goals);

        Firebasedb.collection("Stats").document(userId)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                })
                .addOnFailureListener(e -> {
                });
    }

    private void updateAssistsInDatabase(String userId, int assists) {
        Map<String, Object> data = new HashMap<>();
        data.put("assists", assists);

        Firebasedb.collection("Stats").document(userId)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                })
                .addOnFailureListener(e -> {
                });
    }

}
