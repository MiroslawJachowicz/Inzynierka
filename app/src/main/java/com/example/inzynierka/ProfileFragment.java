package com.example.inzynierka;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfileFragment extends Fragment {

    TextView nameTextView;
    TextView surnameTextView;
    TextView roleTextView;
    TextView clubTextView;
    Button logout_btn;
    private FirebaseAuth firebaseAuth;
    EditText nameEditText;
    ImageView imageViewEditName;
    ImageView imageViewCancelName;
    ImageView imageViewDoneName;
    EditText surnameEditText;
    ImageView imageViewEditSurname;
    ImageView imageViewCancelSurname;
    ImageView imageViewDoneSurname;
    EditText roleEditText;
    ImageView imageViewEditRole;
    ImageView imageViewCancelRole;
    ImageView imageViewDoneRole;
    EditText clubEditText;
    ImageView imageViewEditClub;
    ImageView imageViewCancelClub;
    ImageView imageViewDoneClub;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            nameTextView = requireView().findViewById(R.id.textViewProfileName);
            surnameTextView = requireView().findViewById(R.id.textViewProfileSurname);
            roleTextView = requireView().findViewById(R.id.textViewProfileRole);
            clubTextView = requireView().findViewById(R.id.textViewProfileClub);

            CollectionReference usersRef = FirebaseFirestore.getInstance().collection("Users");
            usersRef.whereEqualTo("userId", currentUserId).limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                    String name = documentSnapshot.getString("username");
                    String surname = documentSnapshot.getString("surname");
                    String club = documentSnapshot.getString("club");
                    String role = documentSnapshot.getString("role");

                    nameTextView.setText(name);
                    surnameTextView.setText(surname);
                    roleTextView.setText(role);
                    clubTextView.setText(club);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        }
        firebaseAuth = FirebaseAuth.getInstance();
        logout_btn = requireView().findViewById(R.id.profile_logout_button);
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseAuth.signOut();

                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();


            }
        });
        imageViewEditName = requireView().findViewById(R.id.imageViewEditName);
        imageViewCancelName = requireView().findViewById(R.id.imageViewCancelName);
        imageViewDoneName = requireView().findViewById(R.id.imageViewDoneName);
        nameEditText = requireView().findViewById(R.id.editTextProfileName);
        imageViewEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameTextView.setVisibility(View.GONE);
                imageViewEditName.setVisibility(View.GONE);
                nameEditText.setText(nameTextView.getText());
                nameEditText.setVisibility(View.VISIBLE);
                imageViewCancelName.setVisibility(View.VISIBLE);
                imageViewDoneName.setVisibility(View.VISIBLE);
                nameEditText.requestFocus();
            }
        });
        imageViewCancelName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameTextView.setVisibility(View.VISIBLE);
                imageViewEditName.setVisibility(View.VISIBLE);
                nameEditText.setVisibility(View.GONE);
                imageViewCancelName.setVisibility(View.GONE);
                imageViewDoneName.setVisibility(View.GONE);
            }
        });
        imageViewDoneName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(nameEditText.getText().toString())) {
                    Toast.makeText(requireContext(), "Name field cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String currentUserId = currentUser.getUid();
                CollectionReference usersRef = FirebaseFirestore.getInstance().collection("Users");
                usersRef.whereEqualTo("userId", currentUserId).limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("DetachAndAttachSameFragment")
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String documentId = documentSnapshot.getId();
                        usersRef.document(documentId).update("username",nameEditText.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                    Toast.makeText(requireContext(), "Name has been updated", Toast.LENGTH_SHORT).show();
                                    nameTextView.setText(String.valueOf(nameEditText.getText()));
                                    nameTextView.setVisibility(View.VISIBLE);
                                    imageViewEditName.setVisibility(View.VISIBLE);
                                    nameEditText.setVisibility(View.GONE);
                                    imageViewCancelName.setVisibility(View.GONE);
                                    imageViewDoneName.setVisibility(View.GONE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        imageViewEditSurname = requireView().findViewById(R.id.imageViewEditSurname);
        imageViewCancelSurname = requireView().findViewById(R.id.imageViewCancelSurname);
        imageViewDoneSurname = requireView().findViewById(R.id.imageViewDoneSurname);
        surnameEditText = requireView().findViewById(R.id.editTextProfileSurname);
        imageViewEditSurname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                surnameTextView.setVisibility(View.GONE);
                imageViewEditSurname.setVisibility(View.GONE);
                surnameEditText.setText(surnameTextView.getText());
                surnameEditText.setVisibility(View.VISIBLE);
                imageViewCancelSurname.setVisibility(View.VISIBLE);
                imageViewDoneSurname.setVisibility(View.VISIBLE);
                surnameEditText.requestFocus();
            }
        });
        imageViewCancelSurname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                surnameTextView.setVisibility(View.VISIBLE);
                imageViewEditSurname.setVisibility(View.VISIBLE);
                surnameEditText.setVisibility(View.GONE);
                imageViewCancelSurname.setVisibility(View.GONE);
                imageViewDoneSurname.setVisibility(View.GONE);
            }
        });
        imageViewDoneSurname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(surnameEditText.getText().toString())) {
                    Toast.makeText(requireContext(), "Surname field cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String currentUserId = currentUser.getUid();
                CollectionReference usersRef = FirebaseFirestore.getInstance().collection("Users");
                usersRef.whereEqualTo("userId", currentUserId).limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("DetachAndAttachSameFragment")
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String documentId = documentSnapshot.getId();
                        usersRef.document(documentId).update("surname",surnameEditText.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                    Toast.makeText(requireContext(), "Surname has been updated", Toast.LENGTH_SHORT).show();
                                    surnameTextView.setText(String.valueOf(surnameEditText.getText()));
                                    surnameTextView.setVisibility(View.VISIBLE);
                                    imageViewEditSurname.setVisibility(View.VISIBLE);
                                    surnameEditText.setVisibility(View.GONE);
                                    imageViewCancelSurname.setVisibility(View.GONE);
                                    imageViewDoneSurname.setVisibility(View.GONE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        imageViewEditRole = requireView().findViewById(R.id.imageViewEditRole);
        imageViewCancelRole = requireView().findViewById(R.id.imageViewCancelRole);
        imageViewDoneRole = requireView().findViewById(R.id.imageViewDoneRole);
        roleEditText = requireView().findViewById(R.id.editTextProfileRole);

        imageViewEditRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roleTextView.setVisibility(View.GONE);
                imageViewEditRole.setVisibility(View.GONE);
                roleEditText.setText(roleTextView.getText());
                roleEditText.setVisibility(View.VISIBLE);
                imageViewCancelRole.setVisibility(View.VISIBLE);
                imageViewDoneRole.setVisibility(View.VISIBLE);
                roleEditText.requestFocus();
            }
        });
        imageViewCancelRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                roleTextView.setVisibility(View.VISIBLE);
                imageViewEditRole.setVisibility(View.VISIBLE);
                roleEditText.setVisibility(View.GONE);
                imageViewCancelRole.setVisibility(View.GONE);
                imageViewDoneRole.setVisibility(View.GONE);
            }
        });

        imageViewDoneRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(roleEditText.getText().toString())) {
                    Toast.makeText(requireContext(), "Role field cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!roleEditText.getText().toString().equals("Player") && !roleEditText.getText().toString().equals("Trainer")){
                    Toast.makeText(requireContext(), "Role must be Player or Trainer!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String currentUserId = currentUser.getUid();
                CollectionReference usersRef = FirebaseFirestore.getInstance().collection("Users");
                usersRef.whereEqualTo("userId", currentUserId).limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("DetachAndAttachSameFragment")
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String documentId = documentSnapshot.getId();
                        usersRef.document(documentId).update("role",roleEditText.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(requireContext(), "Role has been updated", Toast.LENGTH_SHORT).show();
                                roleTextView.setText(String.valueOf(roleEditText.getText()));
                                roleTextView.setVisibility(View.VISIBLE);
                                imageViewEditRole.setVisibility(View.VISIBLE);
                                roleEditText.setVisibility(View.GONE);
                                imageViewCancelRole.setVisibility(View.GONE);
                                imageViewDoneRole.setVisibility(View.GONE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        imageViewEditClub = requireView().findViewById(R.id.imageViewEditClub);
        imageViewCancelClub = requireView().findViewById(R.id.imageViewCancelClub);
        imageViewDoneClub = requireView().findViewById(R.id.imageViewDoneClub);
        clubEditText = requireView().findViewById(R.id.editTextProfileClub);

        imageViewEditClub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clubTextView.setVisibility(View.GONE);
                imageViewEditClub.setVisibility(View.GONE);
                clubEditText.setText(clubTextView.getText());
                clubEditText.setVisibility(View.VISIBLE);
                imageViewCancelClub.setVisibility(View.VISIBLE);
                imageViewDoneClub.setVisibility(View.VISIBLE);
                clubEditText.requestFocus();
            }
        });
        imageViewCancelClub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clubTextView.setVisibility(View.VISIBLE);
                imageViewEditClub.setVisibility(View.VISIBLE);
                clubEditText.setVisibility(View.GONE);
                imageViewCancelClub.setVisibility(View.GONE);
                imageViewDoneClub.setVisibility(View.GONE);
            }
        });

        imageViewDoneClub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(clubEditText.getText().toString())) {
                    Toast.makeText(requireContext(), "Club field cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String currentUserId = currentUser.getUid();
                CollectionReference usersRef = FirebaseFirestore.getInstance().collection("Users");
                usersRef.whereEqualTo("userId", currentUserId).limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("DetachAndAttachSameFragment")
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String documentId = documentSnapshot.getId();
                        usersRef.document(documentId).update("club",clubEditText.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(requireContext(), "Club has been updated", Toast.LENGTH_SHORT).show();
                                clubTextView.setText(String.valueOf(clubEditText.getText()));
                                clubTextView.setVisibility(View.VISIBLE);
                                imageViewEditClub.setVisibility(View.VISIBLE);
                                clubEditText.setVisibility(View.GONE);
                                imageViewCancelClub.setVisibility(View.GONE);
                                imageViewDoneClub.setVisibility(View.GONE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}