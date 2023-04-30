package com.example.healthapp;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HeartActivity extends AppCompatActivity {
    private static final String TAG = "HeartRateActivity";
    private FirebaseFirestore db;
    private String userId;
    private ArrayList<Entry> chartDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        chartDataList = new ArrayList<>();

        getLastSevenDaysData();
    }

    private void getLastSevenDaysData() {
        CollectionReference userHeartDataRef = db.collection("Data").document(userId).collection("HeartData");

        Query query = userHeartDataRef.orderBy(FieldPath.documentId(), Query.Direction.DESCENDING).limit(1);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    DocumentSnapshot lastDateDoc = queryDocumentSnapshots.getDocuments().get(0);
                    String lastDateStr = lastDateDoc.getId();

                    for (int i = 0; i < 7; i++) {
                        String dateStr = lastDateStr;
                        if (i > 0) {
                            dateStr = getDateBefore(lastDateStr, i);
                        }

                        DocumentReference dateHeartDataRef = userHeartDataRef.document(dateStr);
                        CollectionReference hourHeartDataRef = dateHeartDataRef.collection(getCurrentHourStr());

                        Query query = hourHeartDataRef.orderBy("timestamp", Query.Direction.DESCENDING).limit(1);
                        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    DocumentSnapshot lastHeartRateDoc = queryDocumentSnapshots.getDocuments().get(0);
                                    int lastHeartRate = lastHeartRateDoc.getLong("HeartRate").intValue();
                                    long lastTimestamp = lastHeartRateDoc.getTimestamp("timestamp").getSeconds();

                                    chartDataList.add(new Entry(lastTimestamp, lastHeartRate));

                                    if (chartDataList.size() == 7) {
                                        plotDataOnChart();
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private String getDateBefore(String dateStr, int days) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = dateFormat.parse(dateStr);
            calendar.setTime(date);
            calendar.add(Calendar.DATE, -days);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFormat.format(calendar.getTime());
    }

    private String getCurrentHourStr() {
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
        return "hour_" + hourFormat.format(new Date());
    }

    private void plotDataOnChart() {
        LineChart chart = findViewById(R.id.lineChart);

        LineDataSet dataSet = new LineDataSet(chartDataList, "Heart Rate Data");
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setColor(Color.BLUE);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
    }
}
