package com.example.healthapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firestore.v1.DocumentTransform;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
public class TestActivity extends AppCompatActivity {
   private FirebaseFirestore db = FirebaseFirestore.getInstance();
   private  FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
   String userId = currentUser.getUid();

    private CollectionReference userStepsDataRef = db.collection("Data").document(userId).collection("StepsData");
    private CollectionReference userSleepDataRef = db.collection("Data").document(userId).collection("SleepData");
    private CollectionReference userHeartDataRef = db.collection("Data").document(userId).collection("HeartData");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Button generateButton = findViewById(R.id.generate_button);
        Button generateButton1 = findViewById(R.id.generate_button1);
        Button generateButton2 = findViewById(R.id.generate_button2);
        Button nextButton = findViewById(R.id.next_button);

        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random();
                int minSteps = 100;
                int maxSteps = 2000;
                DateFormat dateFormat = new SimpleDateFormat("yyMMdd");

                for (int i = 1; i <= 31; i++) {  // Loop through 31 days in January 2023
                    String dateStr = dateFormat.format(new Date(2023, 0, i));  // Generate date in the format ddmmyy
                    DocumentReference dateStepsDataRef = userStepsDataRef.document(dateStr);
                    Map<String, Object> hourlyStepsData = new HashMap<>();

                    for (int j = 0; j < 24; j++) {  // Generate 24 random step values for each hour of the day
                        int randomSteps = random.nextInt((maxSteps - minSteps) + 1) + minSteps;
                        hourlyStepsData.put( j+"_", randomSteps);

                    }
                    hourlyStepsData.put("timestamp", FieldValue.serverTimestamp()); // add a timestamp field to the hourlyStepsData map
                    dateStepsDataRef.set(hourlyStepsData);  // Store the hourly StepsData in the "Date" document with the generated date as the document name
                }
            }
        });
        generateButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random();
                int minSleep = 2;
                int maxSleep = 9;
                DateFormat dateFormat = new SimpleDateFormat("yyMMdd");

                for (int i = 1; i <= 31; i++) {  // Loop through 31 days in January 2023
                    int randomSteps = random.nextInt((maxSleep - minSleep) + 1) + minSleep;
                    String dateStr = dateFormat.format(new Date(2023, 0, i));  // Generate date in the format ddmmyy
                    DocumentReference dateSleepDataRef = userSleepDataRef.document(dateStr);
                    Map<String, Object> sleepData = new HashMap<>();
                    sleepData.put("Actual Sleep Hours", randomSteps);
                    Timestamp timestamp = Timestamp.now(); // Add timestamp to document
                    sleepData.put("timestamp", timestamp);

                    dateSleepDataRef.set(sleepData);  // Store the randomly generated SleepData in the "Date" document with the generated date as the document name
                }
            }
        });
        generateButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random();
                int minHeartRate = 60;
                int maxHeartRate = 100;
                DateFormat dateFormat = new SimpleDateFormat("yyMMdd");

                CollectionReference userHeartDataRef = db.collection("Data").document(userId).collection("HeartData");

                for (int i = 1; i <= 31; i++) {  // Loop through 31 days in January 2023
                    String dateStr = dateFormat.format(new Date(2023, 0, i));  // Generate date in the format ddmmyy
                    DocumentReference dateHeartDataRef = userHeartDataRef.document(dateStr);

                    for (int j = 1; j <= 24; j++) { // Loop through 24 hours in a day
                        int randomHeartRate = random.nextInt((maxHeartRate - minHeartRate) + 1) + minHeartRate;
                        String hourStr = "hour_" + random.nextInt(24); // Generate a random hour string
                        CollectionReference hourHeartDataRef = dateHeartDataRef.collection(hourStr);

                        Map<String, Object> heartData = new HashMap<>();
                        heartData.put("Heart Rate", randomHeartRate);
                        Timestamp timestamp = Timestamp.now(); // Add timestamp to document
                        heartData.put("timestamp", timestamp);

                        hourHeartDataRef.document("HeartRate").set(heartData);  // Store the randomly generated HeartData in the "hour_i" collection under the "dateStr" document
                    }
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestActivity.this,MainActivity.class));
            }
        });
    }
}