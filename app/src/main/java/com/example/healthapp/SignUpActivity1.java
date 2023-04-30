package com.example.healthapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.RadioGroup;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;


import java.util.HashMap;
import java.util.Map;


public class SignUpActivity1 extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private String userId = currentUser.getUid();

    RadioGroup coldquestion1RadioGroup;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up1);

        coldquestion1RadioGroup = findViewById(R.id.cold_question_radio_group);

        submitButton = findViewById(R.id.next_button1);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String coldquestion1 = null;
                int selectedId1 = coldquestion1RadioGroup.getCheckedRadioButtonId();
                switch (selectedId1) {
                    case R.id.Question1_radio_button:
                        coldquestion1 = "Highly Active";
                        break;
                    case R.id.Question2_radio_button:
                        coldquestion1 = "Indoor Person";
                        break;
                    case R.id.Question3_radio_button:
                        coldquestion1 = "Mix of Both World";
                        break;
                }
                Map<String, Object> userColdQuestionData = new HashMap<>();
                userColdQuestionData.put("Question12", coldquestion1);

                DocumentReference userRef = db.collection("Users").document(userId);
                userRef.set(userColdQuestionData, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(SignUpActivity1.this, "Data added successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignUpActivity1.this, SignUpActivity2.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignUpActivity1.this, "Error adding data", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
    }
}