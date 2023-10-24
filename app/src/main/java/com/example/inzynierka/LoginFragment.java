package com.example.inzynierka;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginFragment extends Fragment {
    EditText editTextEmailAddress;
    EditText editTextTextPassword;
    CheckBox RemembermeCheckBox;
    Button login_btn;
    TextView textViewsignup;
    TextView textViewforgottenpassword;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore Firebasedb = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        textViewsignup= requireView().findViewById(R.id.sign_up_TextView);
        textViewsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(LoginFragmentDirections.actionLoginFragmentToSignupFragment());
            }
        });

        textViewforgottenpassword= requireView().findViewById(R.id.Forgotten_Password_TextView);
        textViewforgottenpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(LoginFragmentDirections.actionLoginFragmentToForgottenPasswordFragment());
            }

        });

        editTextEmailAddress= requireView().findViewById(R.id.editTextTextEmailAddress);

        editTextTextPassword= requireView().findViewById(R.id.editTextTextPassword);

        RemembermeCheckBox= requireView().findViewById(R.id.Remember_me_CheckBox);

        login_btn= requireView().findViewById(R.id.login_button);

    }
}
