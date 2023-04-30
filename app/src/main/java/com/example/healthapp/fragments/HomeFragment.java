package com.example.healthapp.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.healthapp.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FirebaseFirestore db;
    private String userId;
    private TextView last7DaysAvgSleepTextView;
    private TextView last30DaysAvgSleepTextView;
    private TextView last7DaysAvgStepTextView;
    private TextView last30DaysAvgStepTextView;
    private TextView estimatedTimeTextView;
    private TextView last7DaysSleepAvgTextView;
    private BarChart chart;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        last7DaysAvgSleepTextView = view.findViewById(R.id.last7DaysAvgSleepTextView);
        last30DaysAvgSleepTextView = view.findViewById(R.id.last30DaysAvgSleepTextView);
        last7DaysAvgStepTextView = view.findViewById(R.id.last7DaysAvgStepTextView);
        last30DaysAvgStepTextView = view.findViewById(R.id.last30DaysAvgStepTextView);
        estimatedTimeTextView = view.findViewById(R.id.estimatedTimeTextView);
        last7DaysSleepAvgTextView = view.findViewById(R.id.last7DaysSleepAvgTextView);
        chart = view.findViewById(R.id.chart);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        calculateAvgSleepAllGroup();

    }

    private void calculateAvgSleepAllGroup() {
        db.collection("Users")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Double> last7DaysSleepDataList = new ArrayList<>();
                        List<Double> last30DaysSleepDataList = new ArrayList<>();
                        List<Double> last7DaysStepDataList = new ArrayList<>();
                        List<Double> last30DaysStepDataList = new ArrayList<>();

                        for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(i);

                            Number last7DaysSleepData = documentSnapshot.getDouble("last7DaysSleepAvg");
                            if (last7DaysSleepData != null) {
                                last7DaysSleepDataList.add(last7DaysSleepData.doubleValue());
                            }

                            Number last30DaysSleepData = documentSnapshot.getDouble("last30DaysSleepAvg");
                            if (last30DaysSleepData != null) {
                                last30DaysSleepDataList.add(last30DaysSleepData.doubleValue());
                            }
                            Number last7DaysStepData = documentSnapshot.getDouble("last7DaysStepsAvg");
                            if (last7DaysStepData != null) {
                                last7DaysStepDataList.add(last7DaysStepData.doubleValue());
                            }
                            Number last30DaysStepData = documentSnapshot.getDouble("last30DaysStepsAvg");
                            if (last30DaysStepData != null) {
                                last30DaysStepDataList.add(last30DaysStepData.doubleValue());
                            }
                        }

                        double avgLast7DaysSleepData = 0;
                        if (!last7DaysSleepDataList.isEmpty()) {
                            avgLast7DaysSleepData = last7DaysSleepDataList.stream().mapToDouble(Double::doubleValue).average().orElse(0);

                        }

                        double avgLast30DaysSleepData = 0;
                        if (!last30DaysSleepDataList.isEmpty()) {
                            avgLast30DaysSleepData = last30DaysSleepDataList.stream().mapToDouble(Double::doubleValue).average().orElse(0);
                        }
                        double avgLast7DaysStepData = 0;
                        if (!last7DaysStepDataList.isEmpty()) {
                            avgLast7DaysStepData = last7DaysStepDataList.stream().mapToDouble(Double::doubleValue).average().orElse(0);
                        }

                        double avgLast30DaysStepData = 0;
                        if (!last30DaysStepDataList.isEmpty()) {
                            avgLast30DaysStepData = last30DaysStepDataList.stream().mapToDouble(Double::doubleValue).average().orElse(0);
                        }

                        last7DaysAvgSleepTextView.setText(String.format(Locale.getDefault(), "People Slept on Average : %.1f hours in Last 7 days", avgLast7DaysSleepData));
                        last30DaysAvgSleepTextView.setText(String.format(Locale.getDefault(), "People Slept on Average : %.1f hours in Last 30 days", avgLast30DaysSleepData));
                        last7DaysAvgStepTextView.setText(String.format(Locale.getDefault(), "People Walked on Average : %.1f Steps in Last 7 days", avgLast7DaysStepData));
                        last30DaysAvgStepTextView.setText(String.format(Locale.getDefault(), "People Walked on Average : %.1f Steps in Last 30 days", avgLast30DaysStepData));

                        double finalAvgLast7DaysSleepData = avgLast7DaysSleepData;
                        db.collection("Users")
                                .document(userId)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        Double estimatedTime = documentSnapshot.getDouble("time_diff");
                                        double estimatedTime1 = estimatedTime / 60;
                                        double last7DaysSleepAvg = documentSnapshot.getDouble("last7DaysSleepAvg");

                                        // Create a BarEntry object for each value
                                        ArrayList<BarEntry> entries = new ArrayList<>();
                                        entries.add(new BarEntry(0, (float) estimatedTime1));
                                        entries.add(new BarEntry(1, (float) last7DaysSleepAvg));
                                        entries.add(new BarEntry(2, (float) finalAvgLast7DaysSleepData));


                                        // Create a BarDataSet object to hold the entries
                                        BarDataSet dataSet = new BarDataSet(entries, "Sleep Comparison");

                                        // Set the colors for the bars
                                        dataSet.setColors(new int[]{Color.BLUE, Color.GREEN, Color.BLACK});
                                        dataSet.setValueTextColor(Color.BLACK);


                                        // Create a BarData object to hold the dataSet
                                        BarData barData = new BarData(dataSet);

                                        // Customize the appearance of the chart
                                        chart.setDescription(null);
                                        chart.setData(barData);
                                        chart.setDrawGridBackground(false);
                                        chart.setDrawBarShadow(false);
                                        chart.setDrawValueAboveBar(true);
                                        chart.setPinchZoom(false);
                                        chart.setDrawGridBackground(false);

                                        // Refresh the chart to display the new data
                                        chart.invalidate();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the error
                    }
                });
    }
}

