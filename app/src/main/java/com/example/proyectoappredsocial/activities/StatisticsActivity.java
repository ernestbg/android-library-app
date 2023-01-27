package com.example.proyectoappredsocial.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;


import com.example.proyectoappredsocial.R;
import com.example.proyectoappredsocial.providers.AuthProvider;
import com.example.proyectoappredsocial.providers.BooksProvider;
import com.example.proyectoappredsocial.providers.ReadsProvider;
import com.example.proyectoappredsocial.utils.ViewedMessageHelper;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;

public class StatisticsActivity extends AppCompatActivity {


    PieChart pieChart;
    ArrayList<Integer> colors = new ArrayList<>();
    BooksProvider booksProvider;
    AuthProvider authProvider;
    ReadsProvider readsProvider;

    ImageView iv_arrowBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        pieChart = findViewById(R.id.pieChart);

        booksProvider = new BooksProvider();
        authProvider = new AuthProvider();
        readsProvider = new ReadsProvider();

        iv_arrowBack=findViewById(R.id.iv_arrowBack);

        iv_arrowBack.setOnClickListener(view -> finish());


        setUpPieChart();


        booksProvider.getBooksByUserQueryOrderByTitle(authProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int totalBooks = queryDocumentSnapshots.size();
                readsProvider.getReadByUser(authProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int totalReads = queryDocumentSnapshots.size();
                        loadPieChartData(totalBooks, totalReads);
                    }
                });
            }
        });


    }

    private void setUpPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("Mi biblioteca");
        pieChart.setCenterTextSize(24);
        pieChart.getDescription().setEnabled(false);

        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setEnabled(true);


    }

    private void loadPieChartData(int totalBooks, int totalReads) {



        NumberFormat numberFormat = NumberFormat.getInstance();
        // configurar 2 dígitos después del punto decimal

        float readPercentage= (float) totalReads / (float) totalBooks * 100;



        float noReadPercentage = 100.0f -readPercentage;

        Log.i("erny", String.valueOf(noReadPercentage));

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(noReadPercentage, "Sin leer"));
        entries.add(new PieEntry(readPercentage, "Leído"));

        for (int color : ColorTemplate.MATERIAL_COLORS) {

            colors.add(color);


        }

        for (int color : ColorTemplate.VORDIPLOM_COLORS) {

            colors.add(color);


        }

        PieDataSet dataSet = new PieDataSet(entries, "Mi biblioteca");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate();

        pieChart.animateY(1400, Easing.EasingOption.EaseOutQuad);


    }

    @Override
    protected void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true,StatisticsActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false,StatisticsActivity.this);
    }


}