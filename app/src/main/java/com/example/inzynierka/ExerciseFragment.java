package com.example.inzynierka;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ExerciseFragment extends Fragment {

    private Spinner spinnerItemGroup;
    private ImageView imageViewItemGraphic;
    private Button buttonAddExercise;
    private FirebaseFirestore Firebasedb = FirebaseFirestore.getInstance();
    private LinearLayout linearLayoutExercise;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercise, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        spinnerItemGroup = view.findViewById(R.id.spinnerItemGroupExercise);
        imageViewItemGraphic = view.findViewById(R.id.imageViewExercise);
        buttonAddExercise = view.findViewById(R.id.buttonAddExercise);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.item_groups, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerItemGroup.setAdapter(adapter);

        linearLayoutExercise = view.findViewById(R.id.linearLayoutExercise);
        spinnerItemGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateGraphic(position);
                String selectedGroup = spinnerItemGroup.getSelectedItem().toString();
                clearExistingExercises();
                loadExerciseFromFirebase(selectedGroup);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            Firebasedb.collection("Users")
                    .whereEqualTo("userId", currentUserId)
                    .limit(1)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                                String userRole = documentSnapshot.getString("role");
                                String userClub = documentSnapshot.getString("club");
                                buttonAddExercise.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (Objects.equals(userRole, "Trainer")) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                            LayoutInflater inflater = getActivity().getLayoutInflater();
                                            View dialogView = inflater.inflate(R.layout.exercise_layout, null);
                                            EditText editTextName = dialogView.findViewById(R.id.editTextExerciseName);
                                            EditText editTextDescription = dialogView.findViewById(R.id.editTextExerciseDescription);

                                            builder.setView(dialogView)
                                                    .setTitle("Add Exercise")
                                                    .setPositiveButton("Add", null) // We'll override this below
                                                    .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                                            AlertDialog dialog = builder.create();
                                            dialog.setOnShowListener(dialogInterface -> {
                                                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                                button.setOnClickListener(view -> {
                                                    String name = editTextName.getText().toString().trim();
                                                    String description = editTextDescription.getText().toString().trim();
                                                    String group = spinnerItemGroup.getSelectedItem().toString();
                                                    if (name.isEmpty()) {
                                                        Toast.makeText(getActivity(), "Exercise name field is empty", Toast.LENGTH_SHORT).show();
                                                    } else if (description.isEmpty()) {
                                                        Toast.makeText(getActivity(), "Exercise description field is empty", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        saveExerciseToFirebase(name, description, group,userClub);
                                                        dialog.dismiss();
                                                        String selectedGroup = spinnerItemGroup.getSelectedItem().toString();
                                                        clearExistingExercises();
                                                        loadExerciseFromFirebase(selectedGroup);
                                                    }
                                                });
                                            });
                                            dialog.show();
                                        }else {
                                            Toast.makeText(getActivity(), "Only trainer can add exercise", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
    }
    private void updateGraphic(int groupPosition) {
        int graphic;
        switch (groupPosition) {
            case 1:
                graphic = R.drawable.exercise_technique_background;
                break;
            case 2:
                graphic = R.drawable.exercise_tactic_background;
                break;
            case 3:
                graphic = R.drawable.exercise_strenght_background;
                break;
            case 0:
            default:
                graphic = R.drawable.exercise_warmup_background;
                break;
        }
        imageViewItemGraphic.setImageResource(graphic);
    }
    private void saveExerciseToFirebase(String name, String description, String group, String club) {
        Map<String, Object> exercise = new HashMap<>();
        exercise.put("name", name);
        exercise.put("description", description);
        exercise.put("club", club);

        if (isConnectedToInternet(requireContext())) {
            Firebasedb.collection(group).add(exercise)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }else{
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }
    private void loadExerciseFromFirebase(String group) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            Firebasedb.collection("Users")
                    .whereEqualTo("userId", currentUserId)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot userDocSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            String userClub = userDocSnapshot.getString("club");

                            Firebasedb.collection(group)
                                    .whereEqualTo("club", userClub)
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            QuerySnapshot querySnapshot = task.getResult();
                                            if (querySnapshot != null) {
                                                clearExistingExercises();
                                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                                    String name = document.getString("name");
                                                    String description = document.getString("description");
                                                    String documentId = document.getId();
                                                    addExerciseToScrollView(name, description, documentId);
                                                }
                                            }
                                        }
                                    });
                        }
                    });
        }
    }

    private void addExerciseToScrollView(String name, String description, String documentId) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            Firebasedb.collection("Users")
                    .whereEqualTo("userId", currentUserId)
                    .limit(1)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (isAdded()) {

                                    LayoutInflater inflater = LayoutInflater.from(getContext());
                                    View exerciseLayout = inflater.inflate(R.layout.exercise_item_layout, linearLayoutExercise, false);

                                    TextView textViewName = exerciseLayout.findViewById(R.id.textViewExerciseName);
                                    TextView textViewDescription = exerciseLayout.findViewById(R.id.textViewExerciseDescription);
                                    ImageView imageViewCancel = exerciseLayout.findViewById(R.id.imageViewCancel);

                                    textViewName.setText(name);
                                    textViewDescription.setText(description);

                                        imageViewCancel.setOnClickListener(new View.OnClickListener() {

                                            @Override
                                            public void onClick(View v) {
                                                if (isConnectedToInternet(requireContext())) {
                                                    if (!queryDocumentSnapshots.isEmpty()) {
                                                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                                                        String userRole = documentSnapshot.getString("role");
                                                        if (Objects.equals(userRole, "Trainer")) {
                                                            removeExerciseFromFirebase(documentId, spinnerItemGroup.getSelectedItem().toString());
                                                        } else {
                                                            Toast.makeText(getActivity(), "Only trainer can remove exercise", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }else{
                                                    Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                        linearLayoutExercise.addView(exerciseLayout);
                                    }
                                }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

        }
    }
    private void clearExistingExercises() {
        linearLayoutExercise.removeAllViews();
    }
    private void removeExerciseFromFirebase(String documentId, String group) {
        Firebasedb.collection(group).document(documentId).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        clearExistingExercises();
                        loadExerciseFromFirebase(spinnerItemGroup.getSelectedItem().toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
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