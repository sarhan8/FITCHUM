package com.example.healthapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

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

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    EditText password_create;
    EditText username_create;
    EditText email_create;

    EditText age_create;
    RadioGroup genderRadioGroup;


    Button createBTN;


    // Firebase Auth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    // Firebase Connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();


        // Firebase Auth require Google Account on the device to run successfully


        createBTN = findViewById(R.id.acc_sign_up_button);
        password_create = findViewById(R.id.password_create);
        email_create = findViewById(R.id.email_create);
        username_create = findViewById(R.id.userName_create_ET);
        age_create = findViewById(R.id.age_create_ET);
        genderRadioGroup = findViewById(R.id.gender_selection_radio_group);

        // Authentication
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                if (currentUser != null) {

                    // User Already Logged IN

                } else {

                    // No user yet!
                }
            }
        };

        createBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(email_create.getText().toString())
                        && !TextUtils.isEmpty(username_create.getText().toString())
                        && !TextUtils.isEmpty(password_create.getText().toString())) {

                    String email = email_create.getText().toString().trim();
                    String password = password_create.getText().toString().trim();
                    String username = username_create.getText().toString().trim();
                    String age = age_create.getText().toString().trim();
                    String gender = null;


                    int selectedId = genderRadioGroup.getCheckedRadioButtonId();
                    switch (selectedId) {
                        case R.id.male_radio_button:
                            gender = "male";
                            break;
                        case R.id.female_radio_button:
                            gender = "female";
                            break;
                        case R.id.non_binary_radio_button:
                            gender = "non-binary";
                            break;
                        case R.id.no_radio_button:
                            gender = "prefer not to mention";
                            break;
                    }

                    CreateUserEmailAccount(email, password, username, age, gender);

                } else {
                    Toast.makeText(SignUpActivity.this,
                            "Empty Fields", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void CreateUserEmailAccount(String email, String password, String username, String age, String gender) {
        if (!TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(age)) {

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                currentUser = firebaseAuth.getCurrentUser();
                                assert currentUser != null;
                                String currentUserId = currentUser.getUid();
                                DocumentReference userDataRef = collectionReference.document(currentUserId);

                                // Create a userMap so we can create a user in the User Collection in Firestore
                                Map<String, String> userObj = new HashMap<>();
                                userObj.put("userId", currentUserId);
                                userObj.put("username", username);
                                userObj.put("age", age);
                                userObj.put("Gender", gender);


                                //Adding Users to Firestore


                                userDataRef.set(userObj)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(SignUpActivity.this, "Data added successfully", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(SignUpActivity.this, SignUpActivity1.class);
                                                startActivity(intent);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@androidx.annotation.NonNull Exception e) {
                                                Toast.makeText(SignUpActivity.this, "Error adding data", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }

                        }
                    });
        }
    }
}