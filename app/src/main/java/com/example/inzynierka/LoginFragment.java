package com.example.inzynierka;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import Util.CurrentUser;

public class LoginFragment extends Fragment {
    EditText editTextEmailAddress;
    EditText editTextPassword;
    Button login_btn;
    TextView textViewsignup;
    TextView textViewforgottenpassword;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        firebaseAuth=FirebaseAuth.getInstance();

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

        editTextEmailAddress = requireView().findViewById(R.id.editTextTextEmailAddress);

        editTextPassword = requireView().findViewById(R.id.editTextTextPassword);

        editTextPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (editTextPassword.getRight() - editTextPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        int inputType = editTextPassword.getInputType();

                        if (inputType == 129) {
                            editTextPassword.setInputType(145); // Zmiana na "text"
                        } else {
                            editTextPassword.setInputType(129); // Zmiana na "textPassword"
                        }

                        editTextPassword.setSelection(editTextPassword.getText().length());
                        return true;
                    }
                }
                return false;
            }
        });

        login_btn = requireView().findViewById(R.id.login_button);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(editTextEmailAddress.getText().toString())
                        && !TextUtils.isEmpty(editTextPassword.getText().toString())
                        && editTextPassword.length()>=6
                ) {
                    String email = editTextEmailAddress.getText().toString().trim();
                    String password = editTextPassword.getText().toString().trim();
                    Login(email,password);
                } else if (!TextUtils.isEmpty(editTextEmailAddress.getText().toString())){
                    Toast.makeText(requireContext(),"E-mail field is empty",Toast.LENGTH_SHORT).show();
                } else if (!TextUtils.isEmpty(editTextPassword.getText().toString())) {
                    Toast.makeText(requireContext(),"Password field is empty",Toast.LENGTH_SHORT).show();
                } else if (editTextPassword.length()>=6) {
                    Toast.makeText(requireContext(),"Password is too short",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void Login(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser currentUser=firebaseAuth.getCurrentUser();
                assert currentUser!=null;
                final String currentUserId= currentUser.getUid();

                collectionReference.whereEqualTo("userId",currentUserId).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error!=null){

                        }
                        assert value!=null;
                        if(!value.isEmpty()){
                            for(QueryDocumentSnapshot snapshot: value){
                                CurrentUser CurrentUser = Util.CurrentUser.getInstance();
                                CurrentUser.setUserName(snapshot.getString("userName"));
                                CurrentUser.setUserSurname(snapshot.getString("userSurname"));
                                CurrentUser.setUserEmail(snapshot.getString("userEmail"));
                                CurrentUser.setUserClub(snapshot.getString("userClub"));
                                CurrentUser.setUserRole(snapshot.getString("userRole"));
                                CurrentUser.setUserId(snapshot.getString("UserId"));

                                Navigation.findNavController(login_btn).navigate(LoginFragmentDirections.actionLoginFragmentToClubFragment());
                            }
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireContext(),"Something went wrong"+e,Toast.LENGTH_SHORT).show();
            }
        });
    }
}



