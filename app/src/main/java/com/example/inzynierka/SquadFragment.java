package com.example.inzynierka;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class SquadFragment extends Fragment {

    private Set<String> addedPlayers = new HashSet<>();
    private FirebaseFirestore Firebasedb = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_squad, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        Firebasedb = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        for (int i = 1; i <= 18; i++) {
            final int index = i;
            setupSquadLayout(view, index, currentUser);
        }

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
                            if (userClub != null) {
                                loadSquadFromFirebase(userClub);
                            }
                        }
                    }).addOnFailureListener(e -> {

                    });
        }
    }

    private void AddPlayertoSquad(List<String> playerNames, int squadIndex, String userClub) {
        List<String> filteredPlayerNames = new ArrayList<>();
        for (String name : playerNames) {
            if (!addedPlayers.contains(name)) {
                filteredPlayerNames.add(name);
            }
        }
        CharSequence[] playerItems = filteredPlayerNames.toArray(new CharSequence[0]);
        if (isConnectedToInternet(requireContext())) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose a player to add")
                .setItems(playerItems, (dialog, which) -> {
                        String selectedPlayerName = filteredPlayerNames.get(which);
                        addUserToSquad(selectedPlayerName, squadIndex);
                        addedPlayers.add(selectedPlayerName);
                        saveSquadToFirebase(selectedPlayerName, squadIndex, userClub);
                });

        AlertDialog dialog = builder.create();
        dialog.show();
        }else {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void getPlayersFromSameClub(String userClub, FirebaseCallback callback) {
        Firebasedb.collection("Users").whereEqualTo("club", userClub).get().addOnCompleteListener(task -> {
            List<String> playerNames = new ArrayList<>();
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String name = document.getString("username");
                    String surname = document.getString("surname");
                    playerNames.add(name + " " + surname);
                }
                callback.onCallback(playerNames);
            } else {
                callback.onCallback(Collections.emptyList());
            }
        });
    }

    private void addUserToSquad(String playerName, int squadIndex) {
        if (isAdded()) {
            String squadLayoutID = "linearLayoutSquad" + squadIndex;
            int resID = getResources().getIdentifier(squadLayoutID, "id", getActivity().getPackageName());

            ConstraintLayout squadLayout = getView().findViewById(resID);

            if (squadLayout != null) {
                TextView textViewPlayerName = squadLayout.findViewById(R.id.textViewSquadName);
                textViewPlayerName.setVisibility(View.VISIBLE);
                TextView textViewPlayer = squadLayout.findViewById(R.id.textViewSquad);
                textViewPlayer.setVisibility(View.GONE);
                ImageView imageViewSquad = squadLayout.findViewById(R.id.imageViewSquadIcon);
                imageViewSquad.setVisibility(View.GONE);
                ImageView imageViewCancelSquad = squadLayout.findViewById(R.id.imageViewCancelSquad);
                imageViewCancelSquad.setVisibility(View.VISIBLE);
                ImageView imageViewPersonSquad = squadLayout.findViewById(R.id.imageViewSquadPerson);
                imageViewPersonSquad.setVisibility(View.VISIBLE);
                textViewPlayerName.setText(playerName);

            }
        }
    }

    interface FirebaseCallback {
        void onCallback(List<String> playerNames);
    }

    private void saveSquadToFirebase(String playerName, int squadIndex, String userClub) {

        String playerField = "player" + squadIndex;

        Firebasedb.collection("Squad").document(userClub)
                .update(playerField, playerName)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                })
                .addOnFailureListener(e -> {
                    if (e instanceof FirebaseFirestoreException &&
                            ((FirebaseFirestoreException) e).getCode() == FirebaseFirestoreException.Code.NOT_FOUND) {
                        Map<String, Object> newSquadState = new HashMap<>();
                        newSquadState.put(playerField, playerName);
                        Firebasedb.collection("Squad").document(userClub)
                                .set(newSquadState);
                    }
                });
    }

    private void loadSquadFromFirebase(String userClub) {
            Firebasedb.collection("Squad").document(userClub)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            for (int i = 1; i <= 18; i++) {
                                String playerName = documentSnapshot.getString("player" + i);
                                if (playerName != null) {
                                    addUserToSquad(playerName, i);
                                    addedPlayers.add(playerName);

                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
    }

    private void setupSquadLayout(View view, int index, FirebaseUser currentUser) {

            String squadLayoutID = "linearLayoutSquad" + index;
        int resourcesID = getResources().getIdentifier(squadLayoutID, "id", getActivity().getPackageName());
        View squadLayout = view.findViewById(resourcesID);
        if (squadLayout != null) {
            ImageView imageViewSquadIcon = squadLayout.findViewById(R.id.imageViewSquadIcon);
            ImageView imageViewCancelSquad = squadLayout.findViewById(R.id.imageViewCancelSquad);
            TextView textViewPlayerName = squadLayout.findViewById(R.id.textViewSquadName);
            TextView textViewPlayer = squadLayout.findViewById(R.id.textViewSquad);
            ImageView imageViewPersonSquad = squadLayout.findViewById(R.id.imageViewSquadPerson);
            imageViewSquadIcon.setOnClickListener(v -> {
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
                                    if (Objects.equals(userRole, "Trainer")) {
                                        if (userClub != null) {
                                            getPlayersFromSameClub(userClub, playerNames -> AddPlayertoSquad(playerNames, index, userClub));
                                        }
                                    } else {
                                        Toast.makeText(getActivity(), "You  can't change squad, because you are not trainer", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                }
            });
            imageViewCancelSquad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                                        if (Objects.equals(userRole, "Trainer")) {
                                            if (userClub != null) {
                                                if (isConnectedToInternet(requireContext())) {
                                                    String playerName = textViewPlayerName.getText().toString();
                                                    removePlayerFromSquad(playerName, index, userClub);
                                                    textViewPlayerName.setVisibility(View.GONE);
                                                    imageViewCancelSquad.setVisibility(View.GONE);
                                                    textViewPlayer.setVisibility(View.VISIBLE);
                                                    imageViewPersonSquad.setVisibility(View.GONE);
                                                    imageViewSquadIcon.setVisibility(View.VISIBLE);
                                                    addedPlayers.remove(playerName);
                                                }else{
                                                    Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        } else {
                                            Toast.makeText(getActivity(), "You  can't change squad,  because you are not trainer", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
            });
        }
    }
    private void removePlayerFromSquad(String playerName, int squadIndex, String userClub) {

        String playerField = "player" + squadIndex;

        Firebasedb.collection("Squad").document(userClub)
                .update(playerField, null)
                .addOnSuccessListener(aVoid -> Log.d("Squad", "Player removed from squad successfully"))
                .addOnFailureListener(e -> Log.w("Squad", "Error removing player from squad", e));
    }
    public boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
}

