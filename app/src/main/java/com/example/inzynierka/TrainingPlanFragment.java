package com.example.inzynierka;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class TrainingPlanFragment extends Fragment {

    private LinearLayout linearLayoutTrainingPlan;
    private FirebaseFirestore Firebasedb = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private Button addToPlan;
    private SharedPreferences sharedPreferences;
    private String userRole;
    private String userClub;
    private Map<String, Integer> exerciseCountMap = new HashMap<>();

    @Override
    public void onPause() {
        super.onPause();
        saveExerciseCountMap();
    }

    @Override
    public void onResume() {
        super.onResume();
        restoreExerciseCountMap();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trainingplan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences("TrainingPlanPrefs_" + userClub, Context.MODE_PRIVATE);
        restoreExerciseCountMap();
        restoreSavedExercises();
        addToPlan = view.findViewById(R.id.buttonAddExercisetoPlan);
        linearLayoutTrainingPlan = view.findViewById(R.id.linearLayoutTrainingPlan);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            Firebasedb.collection("Users")
                    .whereEqualTo("userId", currentUserId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot userDocument = task.getResult().getDocuments().get(0);
                            userClub = userDocument.getString("club");
                            userRole = userDocument.getString("role");
                            if (userClub != null && userRole != null) {
                                if (isAdded()) {
                                    sharedPreferences = getActivity().getSharedPreferences("TrainingPlanPrefs_" + userClub, Context.MODE_PRIVATE);
                                    restoreExerciseCountMap();
                                    restoreSavedExercises();
                                    addToPlan.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (isConnectedToInternet(requireContext())) {
                                                if (Objects.equals(userRole, "Trainer")) {
                                                    showExerciseSelection(userClub);
                                                } else {
                                                    Toast.makeText(getActivity(), "You can't add plan exercise, because you are not a trainer", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
        }
    }

    private void showExerciseSelection(String userClub) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Exercise");

        ScrollView scrollView = new ScrollView(getContext());
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(layout);
        builder.setView(scrollView);
        final EditText filterEditText = new EditText(getContext());
        filterEditText.setHint("Filter by exercise name");
        layout.addView(filterEditText);
        Button applyFilterButton = new Button(getContext());
        applyFilterButton.setText("Apply Filter");
        layout.addView(applyFilterButton);
        final AlertDialog dialog = builder.create();
        dialog.show();
        loadExercisesfromFirebase(layout, dialog, userClub, "");
        applyFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filterText = filterEditText.getText().toString().trim();
                layout.removeAllViews();
                layout.addView(filterEditText);
                layout.addView(applyFilterButton);
                loadExercisesfromFirebase(layout, dialog, userClub, filterText);
            }
        });
    }

    private void loadExercisesfromFirebase(LinearLayout layout, final AlertDialog dialog, final String userClub, String exerciseNameFilter) {
        List<String> collections = Arrays.asList("Warm up", "Strength", "Tactic", "Technique");

        for (String collection : collections) {
            Firebasedb.collection(collection)
                    .whereEqualTo("club", userClub)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.getString("name");
                                String description = document.getString("description");
                                if (name != null && description != null) {
                                    if (exerciseNameFilter.isEmpty() || name.toLowerCase().contains(exerciseNameFilter.toLowerCase())) {
                                        addExerciseNameToSelection(layout, name, description, dialog);
                                    }
                                }
                            }
                        }
                    });
        }
    }

    private void addExerciseNameToSelection(LinearLayout layout, String name, String description, AlertDialog dialog) {
        TextView textViewName = new TextView(getContext());
        textViewName.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textViewName.setPadding(30, 30, 30, 30);
        textViewName.setText(name);
        textViewName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textViewName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addExerciseToScrollView(name, description);
                saveExercise(name, description);
                dialog.dismiss();
            }
        });
        layout.addView(textViewName);
    }

    private void addExerciseToScrollView(String name, String description) {
        // Check if the exercise is already in the plan
        if (!isExerciseInPlan(name, description)) {
            int count = exerciseCountMap.getOrDefault(name, 0) + 1;
            exerciseCountMap.put(name, count);

            saveExerciseCountMap();
        }

        View exerciseView = LayoutInflater.from(getContext()).inflate(R.layout.exercise_item_layout, linearLayoutTrainingPlan, false);

        TextView textViewName = exerciseView.findViewById(R.id.textViewExerciseName);
        TextView textViewDescription = exerciseView.findViewById(R.id.textViewExerciseDescription);
        ImageView imageViewCancel = exerciseView.findViewById(R.id.imageViewCancel);
        TextView textViewCount = exerciseView.findViewById(R.id.textViewExerciseCount);

        textViewName.setText(name);
        textViewDescription.setText(description);
        textViewCount.setText(exerciseCountMap.getOrDefault(name, 0) +" time used");

        imageViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnectedToInternet(requireContext())) {
                    if (Objects.equals(userRole, "Trainer")) {
                        linearLayoutTrainingPlan.removeView(exerciseView);
                        removeExercise(name, description);
                    } else {
                        Toast.makeText(getActivity(), "You can't delete plan exercise because you are not a trainer", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        linearLayoutTrainingPlan.addView(exerciseView);
    }

    // Check if the exercise is already in the plan
    private boolean isExerciseInPlan(String name, String description) {
        Set<String> exercisesSet = sharedPreferences.getStringSet("exercises", new HashSet<>());
        String exerciseInfo = name + "||" + description;
        return exercisesSet.contains(exerciseInfo);
    }

    private void saveExercise(String name, String description) {
        Set<String> exercisesSet = new HashSet<>(sharedPreferences.getStringSet("exercises", new HashSet<>()));
        exercisesSet.add(name + "||" + description);
        sharedPreferences.edit().putStringSet("exercises", exercisesSet).apply();
    }

    private void restoreSavedExercises() {
        Set<String> exerciseSet = sharedPreferences.getStringSet("exercises", new HashSet<>());
        if (exerciseSet != null) {
            for (String exerciseEntry : exerciseSet) {
                String[] parts = exerciseEntry.split("\\|\\|");
                if (parts.length == 2) {
                    addExerciseToScrollView(parts[0], parts[1]);
                }
            }
        }
    }

    private void removeExercise(String name, String description) {
        String exerciseInfo = name + "||" + description;
        Set<String> exercisesSet = sharedPreferences.getStringSet("exercises", new HashSet<>());
        List<String> exercisesList = new ArrayList<>(exercisesSet);

        exercisesList.remove(exerciseInfo);
        sharedPreferences.edit().putStringSet("exercises", new HashSet<>(exercisesList)).apply();
    }

    public boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    private void saveExerciseCountMap() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (Map.Entry<String, Integer> entry : exerciseCountMap.entrySet()) {
            editor.putInt(entry.getKey(), entry.getValue());
        }
        editor.apply();
    }

    private void restoreExerciseCountMap() {
        exerciseCountMap.clear();
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getValue() instanceof Integer) {
                exerciseCountMap.put(entry.getKey(), (Integer) entry.getValue());
            }
        }
    }
}