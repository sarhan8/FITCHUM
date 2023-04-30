package com.example.healthapp.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.healthapp.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class StepFragment extends Fragment {


    private FirebaseFirestore db;
    private CollectionReference userStepDataRef;
    private FirebaseUser currentUser;
    private String userId;
    private TextView stepCountTextView;

    private BarChart stepCountBarChart;

    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_step, container, false);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Get current user's ID
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userId = currentUser.getUid();

        // Get a reference to the "StepData" collection for the current user
        userStepDataRef = db.collection("Data").document(userId).collection("StepsData");

        // Find the latest document based on the timestamp field
        userStepDataRef.orderBy("timestamp", Query.Direction.DESCENDING).limit(1).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        // No documents found
                        Toast.makeText(getActivity(), "No step data found", Toast.LENGTH_SHORT).show();
                    } else {
                        // Get the latest document
                        DocumentSnapshot latestDocument = queryDocumentSnapshots.getDocuments().get(0);

                        // Calculate total steps from the latest document
                        int totalSteps = 0;
                        Map<String, Object> data = latestDocument.getData();
                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                            if (!entry.getKey().equals("timestamp")) {
                                totalSteps += Integer.parseInt(entry.getValue().toString());
                            }
                        }

                        // Update the text view with the total step count
                        stepCountTextView = view.findViewById(R.id.stepCountTextView);
                        String totalStepsText = "Total Steps today: " + String.valueOf(totalSteps);
                        stepCountTextView.setText(totalStepsText);
                    }
                });
        // Find the last 30 documents and calculate the average of the total steps
        userStepDataRef.orderBy("timestamp", Query.Direction.DESCENDING).limit(30).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        int totalSteps = 0;
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Map<String, Object> data = document.getData();
                            for (Map.Entry<String, Object> entry : data.entrySet()) {
                                if (!entry.getKey().equals("timestamp")) {
                                    totalSteps += Integer.parseInt(entry.getValue().toString());
                                }
                            }
                        }

                        // Calculate the average and update the text view
                        int averageSteps30days = totalSteps / 30;
                        TextView averageStepsTextView = view.findViewById(R.id.averageSteps30daysTextView);
                        String averageStepsText = "Last 30 days Average: " + String.valueOf(averageSteps30days);
                        averageStepsTextView.setText(averageStepsText);

                        // Store the average values in the Firebase database document
                        DocumentReference userRef = db.collection("Users").document(currentUser.getUid());
                        Map<String, Object> data = new HashMap<>();
                        data.put("last30DaysStepsAvg", averageSteps30days);
                        userRef.set(data, SetOptions.merge());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("GraphActivity2", "Error getting step data", e);
                    Toast.makeText(getActivity(), "Error getting step data", Toast.LENGTH_SHORT).show();
                });
// Find the last 7 documents and calculate the average of the total steps
        userStepDataRef.orderBy("timestamp", Query.Direction.DESCENDING).limit(7).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        int totalSteps = 0;
                        ArrayList<BarEntry> barEntries = new ArrayList<>();

                        // Loop through the documents and get the total steps for each day
                        int index = 0;
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            int steps = 0;
                            Map<String, Object> data = document.getData();
                            for (Map.Entry<String, Object> entry : data.entrySet()) {
                                if (!entry.getKey().equals("timestamp")) {
                                    steps += Integer.parseInt(entry.getValue().toString());
                                }
                            }
                            totalSteps += steps;
                            barEntries.add(new BarEntry(index++, steps));
                        }

                        // Calculate the average and update the text view
                        int averageSteps7Days = totalSteps / 7;
                        TextView averageStepsTextView = view.findViewById(R.id.averageSteps7daysTextView);
                        String averageStepsText = "Last 7 days Average: " + String.valueOf(averageSteps7Days);
                        averageStepsTextView.setText(averageStepsText);

                        // Store the average values in the Firebase database document
                        DocumentReference userRef = db.collection("Users").document(currentUser.getUid());
                        Map<String, Object> data = new HashMap<>();
                        data.put("last7DaysStepsAvg", averageSteps7Days);
                        userRef.set(data, SetOptions.merge());

// Create the dataset and set the data
                        BarDataSet dataSet = new BarDataSet(barEntries, "Total Steps for each Day");
                        dataSet.setColor(Color.WHITE);
                        dataSet.setValueTextColor(Color.WHITE);
                        BarData barData = new BarData(dataSet);

// Set up the chart
                        BarChart barChart = view.findViewById(R.id.barChart);
                        barChart.setData(barData);
                        barChart.setDescription(null);
                        barChart.getXAxis().setEnabled(false);
                        barChart.getAxisLeft().setTextColor(Color.WHITE);
                        barChart.getAxisLeft().setDrawGridLines(false);
                        barChart.getAxisLeft().setAxisLineColor(Color.WHITE);
                        barChart.getAxisRight().setEnabled(false);
                        barChart.getLegend().setEnabled(false);
                        barChart.animateY(2000);
                        barChart.invalidate();
                    }
                    })
                .addOnFailureListener(e -> {
                    Log.e("StepAnalysisActivity", "Error getting step data", e);
                    Toast.makeText(getActivity(), "Error getting step data", Toast.LENGTH_SHORT).show();
                });
        return view;
    }
}