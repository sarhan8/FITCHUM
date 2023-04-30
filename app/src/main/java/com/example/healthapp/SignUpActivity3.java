package com.example.healthapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SignUpActivity3 extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private String userId = currentUser.getUid();

    private EditText startTimeEditText, endTimeEditText;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up3);

        startTimeEditText = findViewById(R.id.start_time_edit_text);
        endTimeEditText = findViewById(R.id.end_time_edit_text);
        submitButton = findViewById(R.id.submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startTimeStr = startTimeEditText.getText().toString();
                String endTimeStr = endTimeEditText.getText().toString();

                if (startTimeStr.isEmpty() || endTimeStr.isEmpty()) {
                    Toast.makeText(SignUpActivity3.this, "Please enter start and end time", Toast.LENGTH_SHORT).show();
                    return;
                }

                DateFormat dateFormat = new SimpleDateFormat("HH:mm");

                try {
                    Date startTime = dateFormat.parse(startTimeStr);
                    Date endTime = dateFormat.parse(endTimeStr);
                    long timeDiff = endTime.getTime() - startTime.getTime();
                    if (timeDiff < 0) {
                        timeDiff += 24 * 60 * 60 * 1000; // add 1 day in milliseconds
                    }
                    long timeDiffMinutes = timeDiff / (60 * 1000); // Convert time difference to minutes


                    Map<String, Object> userTimeData = new HashMap<>();
                    userTimeData.put("start_time", startTimeStr);
                    userTimeData.put("end_time", endTimeStr);
                    userTimeData.put("time_diff", timeDiffMinutes);

                    DocumentReference userRef = db.collection("Users").document(userId);
                    userRef.set(userTimeData, SetOptions.merge())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(SignUpActivity3.this, " Time Data added successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignUpActivity3.this, TestActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignUpActivity3.this, "Error adding time data", Toast.LENGTH_SHORT).show();
                                }
                            });

                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(SignUpActivity3.this, "Invalid time format", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}