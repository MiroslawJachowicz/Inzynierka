package com.example.inzynierka;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class SignupFragment extends Fragment {

    EditText edit_Text_Name;
    EditText edit_Text_Surname;
    CheckBox Trainer_CheckBox;
    EditText editText_TextEmailAddress;
    EditText editText_Password;
    EditText editText_ConfirmPassword;
    Button signup_btn ;
    TextView textViewlogin;
    EditText clubselect;


    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore Firebasedb = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference= Firebasedb.collection("Users");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseAuth=FirebaseAuth.getInstance();
        authStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser= firebaseAuth.getCurrentUser();
            }
        };
        edit_Text_Name= requireView().findViewById(R.id.editTextName);

        edit_Text_Surname= requireView().findViewById(R.id.editTextSurname);

        Trainer_CheckBox= requireView().findViewById(R.id.Trainer_CheckBox);

        editText_TextEmailAddress=requireView().findViewById(R.id.editTextTextEmailAddressSignup);

        editText_Password=requireView().findViewById(R.id.editTextPassword);

        editText_ConfirmPassword=requireView().findViewById(R.id.editTextConfirmPassword);

        signup_btn=requireView().findViewById(R.id.signup_button);

        clubselect=requireView().findViewById(R.id.editTextTextClub);

        signup_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isConnectedToInternet(requireContext())) {
                    collectionReference.whereEqualTo("email", editText_TextEmailAddress.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (!task.getResult().isEmpty()) {
                                            Toast.makeText(requireContext(), "E-mail already exist", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                    if (!TextUtils.isEmpty(edit_Text_Name.getText().toString())
                            && !TextUtils.isEmpty(edit_Text_Surname.getText().toString())
                            && !TextUtils.isEmpty(editText_TextEmailAddress.getText().toString())
                            && !TextUtils.isEmpty(editText_Password.getText().toString())
                            && !TextUtils.isEmpty(editText_ConfirmPassword.getText().toString())
                            && !TextUtils.isEmpty(clubselect.getText().toString())
                            && TextUtils.equals(editText_Password.getText().toString(), editText_ConfirmPassword.getText().toString())
                            && editText_Password.length() >= 6
                            && editText_ConfirmPassword.length() >= 6
                            && android.util.Patterns.EMAIL_ADDRESS.matcher(editText_TextEmailAddress.getText().toString().trim()).matches()
                    ) {
                        String name = edit_Text_Name.getText().toString().trim();
                        String Surname = edit_Text_Surname.getText().toString().trim();
                        String email = editText_TextEmailAddress.getText().toString().trim();
                        String password = editText_Password.getText().toString().trim();
                        String club = clubselect.getText().toString().trim();
                        String is_Trainer;
                        if (Trainer_CheckBox.isChecked()) {
                            is_Trainer = "Trainer";
                        } else {
                            is_Trainer = "Player";
                        }
                        CreateAccount(name, Surname, email, password, club, is_Trainer);
                    } else if (TextUtils.isEmpty(edit_Text_Name.getText().toString())) {
                        Toast.makeText(requireContext(), "Name field is empty", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(edit_Text_Surname.getText().toString())) {
                        Toast.makeText(requireContext(), "Surname field is empty", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(editText_TextEmailAddress.getText().toString())) {
                        Toast.makeText(requireContext(), "E-mail field is empty", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(clubselect.getText().toString())) {
                        Toast.makeText(requireContext(), "Club field is empty", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(editText_Password.getText().toString())) {
                        Toast.makeText(requireContext(), "Password field is empty", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(editText_ConfirmPassword.getText().toString())) {
                        Toast.makeText(requireContext(), "Confirm Password field is empty", Toast.LENGTH_SHORT).show();
                    } else if (!TextUtils.equals(editText_Password.getText().toString(), editText_ConfirmPassword.getText().toString())) {
                        Toast.makeText(requireContext(), "Passwords aren't the same", Toast.LENGTH_SHORT).show();
                    } else if (editText_Password.length() < 6 || editText_ConfirmPassword.length() < 6) {
                        Toast.makeText(requireContext(), "Password is too short", Toast.LENGTH_SHORT).show();
                    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(editText_TextEmailAddress.getText().toString().trim()).matches()) {
                        Toast.makeText(requireContext(), "Please enter a email address", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textViewlogin = requireView().findViewById(R.id.login_TextView);
        textViewlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(SignupFragmentDirections.actionSignupFragmentToLoginFragment());
            }
        });

    }

    private void CreateAccount( String name,  String Surname, String email, String password,String club,String is_Trainer ) {

        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    currentUser = firebaseAuth.getCurrentUser();
                    if (currentUser != null) {
                        currentUser.sendEmailVerification()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(requireContext(), "Verification email sent to " + currentUser.getEmail(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                        final String currentUserId = currentUser.getUid();
                        Map<String, Object> userObject = new HashMap<>();
                        userObject.put("userId", currentUserId);
                        userObject.put("username", name);
                        userObject.put("surname", Surname);
                        userObject.put("email", email);
                        userObject.put("role", is_Trainer);
                        userObject.put("club", club);

                        collectionReference.add(userObject).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.getResult().exists()) {
                                            SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putBoolean("RememberMe", false);
                                            editor.apply();
                                            Navigation.findNavController(signup_btn).navigate(SignupFragmentDirections.actionSignupFragmentToLoginFragment());
                                        }
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(requireContext(), "Something went wrong" + e, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        currentUser=firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
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