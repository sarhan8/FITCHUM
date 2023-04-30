package com.example.healthapp.fragments;

import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SleepFragment extends Fragment {
    private FirebaseFirestore db;
    private CollectionReference userSleepDataRef;
    private FirebaseUser currentUser;
    private String userId;
    private TextView todaySleepTextView, last7DaysAvgTextView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sleep, container, false);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Get current user's ID
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userId = currentUser.getUid();

        // Get a reference to the "SleepData" collection for the current user
        userSleepDataRef = db.collection("Data").document(userId).collection("SleepData");

        // Query the last document in the collection to get today's sleep data
        userSleepDataRef.orderBy("timestamp", Query.Direction.DESCENDING).limit(1).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Get the data from the document and display it
                        Double sleepData = document.getDouble("Actual Sleep Hours");
                        if (sleepData != null) {
                            todaySleepTextView = view.findViewById(R.id.todaySleepTextView);
                            todaySleepTextView.setText(String.format("Today you slept %.2f hours", sleepData));
                        } else {
                            Log.e("GraphTestActivity", "Sleep duration is null in document: " + document.getId());
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("GraphTestActivity", "Error getting today's sleep data", e);
                });


        // Query the last 7 documents in the collection and show the average value in the "Last 7 days Avg sleep" TextView
        userSleepDataRef.orderBy("timestamp", Query.Direction.DESCENDING).limit(7).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Double> sleepDataList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Get the data from the document and add it to the list
                        Double sleepData = document.getDouble("Actual Sleep Hours");
                        if (sleepData != null) {
                            sleepDataList.add(sleepData);
                        } else {
                            Log.e("GraphTestActivity", "Sleep duration is null in document: " + document.getId());
                        }
                    }
                    // Calculate the average sleep data value
                    double avgSleepData7Days = 0;
                    if (!sleepDataList.isEmpty()) {
                        avgSleepData7Days = sleepDataList.stream().mapToDouble(Double::doubleValue).average().orElse(0);
                    }

                    // Display the average value in the "Last 7 days Avg sleep" TextView
                    TextView avgSleepTextView = view.findViewById(R.id.last7DaysAvgTextView);
                    avgSleepTextView.setText(String.format(Locale.getDefault(), "Last 7 days Avg sleep: %.1f hours", avgSleepData7Days));
                    // Store the average values in the Firebase database document for the current user
                    DocumentReference userRef = db.collection("Users").document(currentUser.getUid());
                    Map<String, Object> data = new HashMap<>();
                    data.put("last7DaysSleepAvg", avgSleepData7Days);
                    userRef.set(data, SetOptions.merge());
                })
                .addOnFailureListener(e -> {
                    Log.e("GraphTestActivity", "Error getting sleep data", e);
                    Toast.makeText(getActivity(), "Error getting sleep data", Toast.LENGTH_SHORT).show();
                });

        // Query the last 30 documents in the collection and show the average value in the "Last 7 days Avg sleep" TextView
        userSleepDataRef.orderBy("timestamp", Query.Direction.DESCENDING).limit(30).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Double> sleepDataList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Get the data from the document and add it to the list
                        Double sleepData = document.getDouble("Actual Sleep Hours");
                        if (sleepData != null) {
                            sleepDataList.add(sleepData);
                        } else {
                            Log.e("GraphTestActivity", "Sleep duration is null in document: " + document.getId());
                        }
                    }
                    // Calculate the average sleep data value
                    double avgSleepData30Days = 0;
                    if (!sleepDataList.isEmpty()) {
                        avgSleepData30Days = sleepDataList.stream().mapToDouble(Double::doubleValue).average().orElse(0);
                    }

                    // Display the average value in the "Last 30 days Avg sleep" TextView
                    TextView avgSleepTextView = view.findViewById(R.id.last30DaysAvgTextView);
                    avgSleepTextView.setText(String.format(Locale.getDefault(), "Last 30 days Avg sleep: %.1f hours", avgSleepData30Days));
                    // Store the average values in the Firebase database document for the current user
                    DocumentReference userRef = db.collection("Users").document(currentUser.getUid());
                    Map<String, Object> data = new HashMap<>();
                    data.put("last30DaysSleepAvg", avgSleepData30Days);
                    userRef.set(data, SetOptions.merge());

                })
                .addOnFailureListener(e -> {
                    Log.e("GraphTestActivity", "Error getting sleep data", e);
                    Toast.makeText(getActivity(), "Error getting sleep data", Toast.LENGTH_SHORT).show();
                });

        // Query the last 7 documents in the collection and plot the sleep data values in the LineChart
        userSleepDataRef.orderBy("timestamp", Query.Direction.DESCENDING).limit(7).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Entry> sleepDataEntries = new ArrayList<>();
                    ArrayList<String> xValues = new ArrayList<>();
                    int index = 0;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.US);
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Get the data from the document and add it to the list
                        Double sleepData = document.getDouble("Actual Sleep Hours");
                        if (sleepData != null) {
                            sleepDataEntries.add(new Entry(index++, sleepData.floatValue()));
                            Date timestamp = document.getDate("timestamp");
                            if (timestamp != null) {
                                xValues.add(dateFormat.format(timestamp));
                            }
                        } else {
                            Log.e("GraphTestActivity", "Sleep duration is null in document: " + document.getId());
                        }
                    }
                    // Create a dataset and add the sleep data entries to it
                    LineDataSet sleepDataSet = new LineDataSet(sleepDataEntries, "Sleep Duration");

                    // Set the line color
                    sleepDataSet.setColor(Color.WHITE);
                    // Set the circle color
                    sleepDataSet.setCircleColor(Color.WHITE);

                    // Set the style of the line chart
                    LineChart lineChart = view.findViewById(R.id.lineChart);
                    lineChart.setDrawGridBackground(false);
                    lineChart.setDrawBorders(false);
                    lineChart.getDescription().setEnabled(false);
                    lineChart.getLegend().setEnabled(false);
                    lineChart.getXAxis().setEnabled(true); // Enable X-axis
                    lineChart.getAxisRight().setEnabled(false);
                    lineChart.setTouchEnabled(false);

                    // Set up the axis labels
                    YAxis yAxis = lineChart.getAxisLeft();
                    yAxis.setTextColor(Color.WHITE);
                    yAxis.setDrawGridLines(false);

                    XAxis xAxis = lineChart.getXAxis();
                    xAxis.setTextColor(Color.WHITE);
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setDrawGridLines(false);
                    xAxis.setAxisLineColor(Color.WHITE);
                    xAxis.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getAxisLabel(float value, AxisBase axis) {
                            if ((int) value >= 0 && (int) value < xValues.size()) {
                                return xValues.get((int) value);
                            } else {
                                return "";
                            }
                        }
                    });

                    // Create a LineData object and add the dataset to it
                    LineData lineData = new LineData(sleepDataSet);
                    sleepDataSet.setLineWidth(2f);

                    // Set up the chart
                    lineChart.setData(lineData);
                    lineChart.animateXY(2000, 2000);
                    lineChart.invalidate();
                });

        return view;
    }
}