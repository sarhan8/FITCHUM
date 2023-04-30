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


public class SignUpActivity2 extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private String userId = currentUser.getUid();

    private RadioGroup coldquestion2RadioGroup;
    private Button submitButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);

        coldquestion2RadioGroup = findViewById(R.id.cold_question1_radio_group);

        submitButton = findViewById(R.id.next_button2);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String coldquestion2 = null;
                int selectedId2 = coldquestion2RadioGroup.getCheckedRadioButtonId();
                switch (selectedId2) {
                    case R.id.Question4_radio_button:
                        coldquestion2 = "Morning Person";
                        break;
                    case R.id.Question5_radio_button:
                        coldquestion2 = "Night Owl";
                        break;
                }
                Map<String, Object> userColdQuestionData = new HashMap<>();
                userColdQuestionData.put("Question112", coldquestion2);

                DocumentReference userRef = db.collection("Users").document(userId);
                userRef.set(userColdQuestionData, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(SignUpActivity2.this, "Data added successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignUpActivity2.this, SignUpActivity3.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignUpActivity2.this, "Error adding data", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
    }
}