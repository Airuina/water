package com.example.waterreminder;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChartActivity extends AppCompatActivity {

    private BarChart barChart;
    private DrinkDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        barChart = findViewById(R.id.barChart);
        db = DrinkDatabase.getInstance(this);

        loadChartData();
    }

    private void loadChartData() {
        new Thread(() -> {
            List<BarEntry> entries = new ArrayList<>();
            List<String> labels = new ArrayList<>();

            long todayStart = StatsUtils.getTodayStartMillis();
            long oneDayMillis = 24 * 60 * 60 * 1000L;

            for (int i = 6; i >= 0; i--) {
                long dayStart = todayStart - i * oneDayMillis;
                long dayEnd = dayStart + oneDayMillis;

                int count = db.drinkDao().getCountBetween(dayStart, dayEnd);
                entries.add(new BarEntry(6 - i, count));

                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(dayStart);
                labels.add(String.format("%d/%d", c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH)));
            }

            runOnUiThread(() -> {
                BarDataSet dataSet = new BarDataSet(entries, "近7天喝水次数");
                dataSet.setColor(Color.BLUE);

                BarData barData = new BarData(dataSet);
                barData.setBarWidth(0.9f);

                barChart.setData(barData);
                barChart.getXAxis().setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        int index = (int) value;
                        if (index >= 0 && index < labels.size()) {
                            return labels.get(index);
                        } else {
                            return "";
                        }
                    }
                });
                barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                barChart.getXAxis().setGranularity(1f);
                barChart.getXAxis().setGranularityEnabled(true);
                barChart.getAxisRight().setEnabled(false);
                barChart.getDescription().setEnabled(false);
                barChart.invalidate();
            });
        }).start();
    }
}

