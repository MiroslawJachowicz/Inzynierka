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
    private Map<String, int[]> userGoalsAssistsCardMap = new HashMap<>();

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
            ImageView imageViewIncreaseYellowCard = userLayout.findViewById(R.id.imageViewIncrementYellowCards);
            ImageView imageViewDecreaseYellowCard = userLayout.findViewById(R.id.imageViewDecrementYellowCards);
            ImageView imageViewIncreaseRedCard = userLayout.findViewById(R.id.imageViewIncrementRedCards);
            ImageView imageViewDecreaseRedCard = userLayout.findViewById(R.id.imageViewDecrementRedCards);
            TextView textViewName = userLayout.findViewById(R.id.textViewMemberNameSurname);
            TextView textViewRole = userLayout.findViewById(R.id.textViewMemberRole);
            TextView textViewGoals = userLayout.findViewById(R.id.textViewMemberGoals);
            TextView textViewAssists = userLayout.findViewById(R.id.textViewMemberAssists);
            TextView textViewYellowCard = userLayout.findViewById(R.id.textViewMemberYellowCards);
            TextView textViewRedCard = userLayout.findViewById(R.id.textViewMemberRedCards);

            textViewName.setText(name + " " + surname);
            textViewRole.setText(role);

            fetchGoalsAssistsCards(userId, textViewGoals, textViewAssists,textViewYellowCard,textViewRedCard);

                imageViewIncreaseGoals.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       if(Objects.equals(userRole, "Trainer"))
                        updateGoalsInDatabase(userId, getUpdatedCount(userId, textViewGoals, 0, 1));
                       else{
                           Toast.makeText(getActivity(), "Only trainer can change stats", Toast.LENGTH_SHORT).show();
                       }
                    }
                });

                imageViewDecreaseGoals.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Objects.equals(userRole, "Trainer"))
                        updateGoalsInDatabase(userId, getUpdatedCount(userId, textViewGoals, 0, -1));
                        else{
                            Toast.makeText(getActivity(), "Only trainer can change stats", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                imageViewIncreaseAssist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Objects.equals(userRole, "Trainer"))
                        updateAssistsInDatabase(userId, getUpdatedCount(userId, textViewAssists, 1, 1));
                        else{
                            Toast.makeText(getActivity(), "Only trainer can change stats", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                imageViewDecreaseAssist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Objects.equals(userRole, "Trainer"))
                        updateAssistsInDatabase(userId, getUpdatedCount(userId, textViewAssists, 1, -1));
                        else{
                            Toast.makeText(getActivity(), "Only trainer can change stats", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            imageViewIncreaseYellowCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Objects.equals(userRole, "Trainer"))
                        updateYellowCardInDatabase(userId, getUpdatedCount(userId, textViewYellowCard, 2, 1));
                    else{
                        Toast.makeText(getActivity(), "Only trainer can change stats", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            imageViewDecreaseYellowCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Objects.equals(userRole, "Trainer"))
                        updateYellowCardInDatabase(userId, getUpdatedCount(userId, textViewYellowCard, 2, -1));
                    else{
                        Toast.makeText(getActivity(), "Only trainer can change stats", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            imageViewIncreaseRedCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Objects.equals(userRole, "Trainer"))
                        updateRedCardInDatabase(userId, getUpdatedCount(userId, textViewRedCard, 3, 1));
                    else{
                        Toast.makeText(getActivity(), "Only trainer can change stats", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            imageViewDecreaseRedCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Objects.equals(userRole, "Trainer"))
                        updateRedCardInDatabase(userId, getUpdatedCount(userId, textViewRedCard, 3, -1));
                    else{
                        Toast.makeText(getActivity(), "Only trainer can change stats", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            memberLinearLayout.addView(userLayout);
        }
    }

    private void fetchGoalsAssistsCards(String userId, TextView textViewGoals, TextView textViewAssists, TextView textViewYellowCard, TextView textViewRedCard) {
        Firebasedb.collection("Stats").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        int goals = documentSnapshot.getLong("goals") == null ? 0 : documentSnapshot.getLong("goals").intValue();
                        int assists = documentSnapshot.getLong("assists") == null ? 0 : documentSnapshot.getLong("assists").intValue();
                        int yellowcards = documentSnapshot.getLong("yellowcards") == null ? 0 : documentSnapshot.getLong("yellowcards").intValue();
                        int redcards = documentSnapshot.getLong("redcards") == null ? 0 : documentSnapshot.getLong("redcards").intValue();
                        userGoalsAssistsCardMap.put(userId, new int[]{goals, assists,yellowcards,redcards});

                        textViewGoals.setText("Goals: " + goals);
                        textViewAssists.setText("Assists: " + assists);
                        textViewYellowCard.setText("Yellow Cards: " + yellowcards);
                        textViewRedCard.setText("Red Cards: " + redcards);
                    } else {
                        textViewGoals.setText("Goals: 0");
                        textViewAssists.setText("Assists: 0");
                        textViewYellowCard.setText("Yellow Cards: 0");
                        textViewRedCard.setText("Red Cards: 0");
                        setDefaultValuesInDatabase(userId);
                        fetchGoalsAssistsCards(userId,textViewGoals,textViewAssists,textViewYellowCard,textViewRedCard);
                    }
                })
                .addOnFailureListener(e -> {
                });
    }

    private void setDefaultValuesInDatabase(String userId) {
        Map<String, Object> defaultData = new HashMap<>();
        defaultData.put("goals", 0);
        defaultData.put("assists", 0);
        defaultData.put("yellowcards", 0);
        defaultData.put("redcards", 0);
        Firebasedb.collection("Stats").document(userId)
                .set(defaultData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                })
                .addOnFailureListener(e -> {
                });
    }

    private int getUpdatedCount(String userId, TextView textView, int eventType, int change) {
        int[] userValues = userGoalsAssistsCardMap.get(userId);

        if (userValues != null && userValues.length == 4) {
            int index;

            switch (eventType) {
                case 0:  // Goal
                    index = 0;
                    int updatedGoals = Math.max(userValues[index] + change, 0);
                    textView.setText("Goals: " + updatedGoals);
                    userValues[index] = updatedGoals;
                    break;
                case 1:  // Assist
                    index = 1;
                    int updatedAssists = Math.max(userValues[index] + change, 0);
                    textView.setText("Assists: " + updatedAssists);
                    userValues[index] = updatedAssists;
                    break;
                case 2:  // Yellow Card
                    index = 2;
                    int updatedYellowCards = Math.max(userValues[index] + change, 0);
                    textView.setText("Yellow Cards: " + updatedYellowCards);
                    if (change > 0 && updatedYellowCards % 4 == 0) {
                        // Pause the next match
                        pauseNextMatchYellowCard();
                    }
                    userValues[index] = updatedYellowCards;
                    break;
                case 3:  // Red Card
                    index = 3;
                    int updatedRedCards = Math.max(userValues[index] + change, 0);
                    textView.setText("Red Cards: " + updatedRedCards);
                    if (change > 0) { // Check if red cards are increased
                        // Pause the next match
                        pauseNextMatchRedCard();
                    }
                    userValues[index] = updatedRedCards;
                    break;
                default:
                    return 0;
            }

            userGoalsAssistsCardMap.put(userId, userValues);
            return userValues[index];
        } else {
            return 0;
        }
    }
    private void pauseNextMatchYellowCard() {

        Toast.makeText(getContext(), "Next match paused due to yellow cards.", Toast.LENGTH_SHORT).show();
    }
    private void pauseNextMatchRedCard() {

        Toast.makeText(getContext(), "Next match paused due to red cards.", Toast.LENGTH_SHORT).show();
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
    private void updateYellowCardInDatabase(String userId, int yellowcards) {
        Map<String, Object> data = new HashMap<>();
        data.put("yellowcards", yellowcards);

        Firebasedb.collection("Stats").document(userId)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                })
                .addOnFailureListener(e -> {
                });
    }
    private void updateRedCardInDatabase(String userId, int redcards) {
        Map<String, Object> data = new HashMap<>();
        data.put("redcards", redcards);

        Firebasedb.collection("Stats").document(userId)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                })
                .addOnFailureListener(e -> {
                });
    }
}
