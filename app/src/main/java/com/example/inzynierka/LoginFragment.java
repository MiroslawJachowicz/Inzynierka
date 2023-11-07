package com.example.inzynierka;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginFragment extends Fragment {
    EditText editTextEmailAddress;
    EditText editTextPassword;
    Button login_btn;
    TextView textViewsignup;
    TextView textViewforgottenpassword;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private FirebaseFirestore Firebasedb = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=Firebasedb.collection("Users");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

            textViewsignup = requireView().findViewById(R.id.sign_up_TextView);
            textViewsignup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigation.findNavController(v).navigate(LoginFragmentDirections.actionLoginFragmentToSignupFragment());
                }
            });

            textViewforgottenpassword = requireView().findViewById(R.id.Forgotten_Password_TextView);
            textViewforgottenpassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigation.findNavController(v).navigate(LoginFragmentDirections.actionLoginFragmentToForgottenPasswordFragment());
                }

            });

            editTextEmailAddress = requireView().findViewById(R.id.editTextTextEmailAddressLogin);

            editTextPassword = requireView().findViewById(R.id.editTextTextPassword);

            login_btn = requireView().findViewById(R.id.login_button);

            login_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(editTextEmailAddress.getText().toString())
                            && !TextUtils.isEmpty(editTextPassword.getText().toString())
                            && editTextPassword.length() >= 6
                    ) {
                        String email = editTextEmailAddress.getText().toString().trim();
                        String password = editTextPassword.getText().toString().trim();
                        collectionReference.whereEqualTo("email", editTextEmailAddress.getText().toString())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (!task.getResult().isEmpty()) {

                                                Login(email, password);

                                            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(editTextEmailAddress.getText().toString().trim()).matches()) {
                                                Toast.makeText(requireContext(), "Please enter a email address", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(requireContext(), "E-mail is not assigned to any account", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
                    } else if (TextUtils.isEmpty(editTextEmailAddress.getText().toString())) {
                        Toast.makeText(requireContext(), "E-mail field is empty", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(editTextPassword.getText().toString())) {
                        Toast.makeText(requireContext(), "Password field is empty", Toast.LENGTH_SHORT).show();
                    } else if (editTextPassword.length() >= 6) {
                        Toast.makeText(requireContext(), "Password is too short", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    private void Login(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                currentUser = firebaseAuth.getCurrentUser();
                assert currentUser!=null;
                final String currentUserId= currentUser.getUid();
                if (currentUser != null && currentUser.isEmailVerified()){
                collectionReference.whereEqualTo("userId",currentUserId).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error!=null){

                        }
                        assert value!=null;
                        if(!value.isEmpty()){
                            for(QueryDocumentSnapshot snapshot: value){
                                if (isAdded() && getView() != null) {

                                    Navigation.findNavController(getView()).navigate(LoginFragmentDirections.actionLoginFragmentToClubFragment());
                                }
                            }
                        }


                    }
                });
            }else{
                    Toast.makeText(requireContext(), "Verify your email address", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireContext(),"Incorrect email or password",Toast.LENGTH_SHORT).show();
            }
        });
    }
}



