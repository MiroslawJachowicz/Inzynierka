package com.example.inzynierka;

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

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore Firebasedb = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference= Firebasedb.collection("Users");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        firebaseAuth=FirebaseAuth.getInstance();

        authStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser= firebaseAuth.getCurrentUser();
                if(currentUser!=null){

                }
                else{

                }
            }
        };
        edit_Text_Name= requireView().findViewById(R.id.editTextName);

        edit_Text_Surname= requireView().findViewById(R.id.editTextSurname);

        Trainer_CheckBox= requireView().findViewById(R.id.Trainer_CheckBox);

        editText_TextEmailAddress=requireView().findViewById(R.id.editTextTextEmailAddress);

        editText_Password=requireView().findViewById(R.id.editTextPassword);

        editText_ConfirmPassword=requireView().findViewById(R.id.editTextConfirmPassword);

        signup_btn=requireView().findViewById(R.id.signup_button);
        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(edit_Text_Name.getText().toString())
                        && !TextUtils.isEmpty(edit_Text_Surname.getText().toString())
                        && !TextUtils.isEmpty(editText_TextEmailAddress.getText().toString())
                        && !TextUtils.isEmpty(editText_Password.getText().toString())
                        && !TextUtils.isEmpty(editText_ConfirmPassword.getText().toString())
                        && TextUtils.equals(editText_Password.getText().toString(), editText_ConfirmPassword.getText().toString()))
                {
                    String name=edit_Text_Name.getText().toString().trim();
                    String Surname=edit_Text_Surname.getText().toString().trim();
                    String email=editText_TextEmailAddress.getText().toString().trim();
                    String password=editText_Password.getText().toString().trim();
                    CreateAccount(name,Surname,email,password);
                }
                else if (!TextUtils.equals(editText_Password.getText().toString(), editText_ConfirmPassword.getText().toString())) {
                    Toast.makeText(requireContext(),"Passwords aren't the same",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(requireContext(),"Some fields are empty",Toast.LENGTH_SHORT).show();
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

    private void CreateAccount(final String name, final String Surname, String email, String password) {

        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    currentUser=firebaseAuth.getCurrentUser();
                    final String currentUserId=currentUser.getUid();
                    Map<String,String> userObject= new HashMap<>();
                    userObject.put("userId",currentUserId);
                    userObject.put("username",name);
                    userObject.put("surname",Surname);

                    collectionReference.add(userObject).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.getResult().exists()){
                                        String name=task.getResult().getString("username");
                                        String surname=task.getResult().getString("surname");
                                        Navigation.findNavController(signup_btn).navigate(SignupFragmentDirections.actionSignupFragmentToClubFragment());
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
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
}