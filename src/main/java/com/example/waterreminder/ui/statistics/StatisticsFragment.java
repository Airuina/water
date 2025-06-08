package com.example.waterreminder.ui.statistics;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.waterreminder.DrinkDatabase;
import com.example.waterreminder.R;
import com.example.waterreminder.StatsUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import com.example.waterreminder.DrinkRecord;

public class StatisticsFragment extends Fragment {

    private DrinkDatabase db;
    private TextView tvTotalDrinks, tvAvgInterval, tvConsecutiveDays;
    private ViewGroup chartContainer;
    private SimpleDateFormat dateFormat;
    private TextView tvWaterAmount, tvCoffeeAmount, tvTeaAmount;
    private ProgressBar progressWater, progressCoffee, progressTea;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = DrinkDatabase.getInstance(requireContext());
        dateFormat = new SimpleDateFormat("MM-dd", Locale.getDefault());
        
        // 初始化视图
        tvTotalDrinks = view.findViewById(R.id.tv_total_drinks);
        tvAvgInterval = view.findViewById(R.id.tv_avg_interval);
        tvConsecutiveDays = view.findViewById(R.id.tv_consecutive_days);
        chartContainer = view.findViewById(R.id.chart_container);
        tvWaterAmount = view.findViewById(R.id.tv_water_amount);
        tvCoffeeAmount = view.findViewById(R.id.tv_coffee_amount);
        tvTeaAmount = view.findViewById(R.id.tv_tea_amount);
        progressWater = view.findViewById(R.id.progress_water);
        progressCoffee = view.findViewById(R.id.progress_coffee);
        progressTea = view.findViewById(R.id.progress_tea);

        // 创建图表
        setupChart();
        
        // 加载数据
        loadStatistics();
        loadDrinkDetails();
    }

    private void setupChart() {
        BarChart chart = new BarChart(requireContext());
        chart.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT));
        
        // 配置图表
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setHighlightFullBarEnabled(false);
        
        // 配置X轴
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_YEAR, (int)value - 6); // 从最近7天开始
                return dateFormat.format(cal.getTime());
            }
        });
        
        // 配置Y轴
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.getDefault(), "%.0f ml", value);
            }
        });
        
        chart.getAxisRight().setEnabled(false);
        
        // 添加到容器
        chartContainer.addView(chart);
        
        // 加载图表数据
        loadChartData(chart);
    }

    private void loadChartData(BarChart chart) {
        Executors.newSingleThreadExecutor().execute(() -> {
            // 获取最近7天的数据
            List<BarEntry> entries = new ArrayList<>();
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            
            // 获取7天前的开始时间
            cal.add(Calendar.DAY_OF_YEAR, -6);
            
            for (int i = 0; i < 7; i++) {
                long startTime = cal.getTimeInMillis();
                cal.add(Calendar.DAY_OF_YEAR, 1);
                long endTime = cal.getTimeInMillis();
                
                // 获取该天的总饮水量
                int totalAmount = db.drinkDao().getTotalAmountBetween(startTime, endTime);
                entries.add(new BarEntry(i, totalAmount));
            }

            // 创建数据集
            BarDataSet dataSet = new BarDataSet(entries, "每日饮水量(ml)");
            dataSet.setColor(Color.BLUE);
            dataSet.setValueTextColor(Color.BLACK);
            dataSet.setValueTextSize(10f);
            dataSet.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.format(Locale.getDefault(), "%.0f", value);
                }
            });

            // 设置数据
            BarData barData = new BarData(dataSet);
            barData.setBarWidth(0.7f);
            
            // 在主线程更新UI
            requireActivity().runOnUiThread(() -> {
                chart.setData(barData);
                chart.invalidate();
            });
        });
    }

    private void loadStatistics() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // 获取本月总饮水量
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long monthStart = cal.getTimeInMillis();
            
            int monthlyTotal = db.drinkDao().getTotalAmountBetween(
                monthStart,
                System.currentTimeMillis()
            );

            long avgInterval = StatsUtils.getAverageInterval(
                db.drinkDao().getRecordsBetween(
                    StatsUtils.getTodayStartMillis(),
                    System.currentTimeMillis()
                )
            );

            int consecutiveDays = StatsUtils.getConsecutiveGoalDays(
                db.drinkDao(),
                requireContext().getSharedPreferences("settings", 0).getInt("daily_goal", 2000)
            );

            // 更新UI
            requireActivity().runOnUiThread(() -> {
                tvTotalDrinks.setText("本月总计：" + monthlyTotal + " ml");
                tvAvgInterval.setText("平均间隔：" + (avgInterval > 0 ? (avgInterval / 60000) : "暂无数据") + " 分钟");
                tvConsecutiveDays.setText("连续达标：" + consecutiveDays + " 天");
            });
        });
    }

    private void loadDrinkDetails() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // 获取最近7天的数据
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            
            // 获取7天前的开始时间
            cal.add(Calendar.DAY_OF_YEAR, -6);
            long weekStart = cal.getTimeInMillis();
            long now = System.currentTimeMillis();
            
            java.util.List<DrinkRecord> weekRecords = db.drinkDao().getRecordsBetween(weekStart, now);
            int total = 0, water = 0, coffee = 0, tea = 0;
            
            for (DrinkRecord r : weekRecords) {
                total += r.amount;
                if ("白水".equals(r.type)) water += r.amount;
                else if ("咖啡".equals(r.type)) coffee += r.amount;
                else if ("茶".equals(r.type)) tea += r.amount;
            }
            
            int waterPercent = total > 0 ? (water * 100 / total) : 0;
            int coffeePercent = total > 0 ? (coffee * 100 / total) : 0;
            int teaPercent = total > 0 ? (tea * 100 / total) : 0;
            
            final int fWater = water;
            final int fCoffee = coffee;
            final int fTea = tea;
            final int fWaterPercent = waterPercent;
            final int fCoffeePercent = coffeePercent;
            final int fTeaPercent = teaPercent;
            
            requireActivity().runOnUiThread(() -> {
                tvWaterAmount.setText(fWater + " ml [" + fWaterPercent + "%]");
                progressWater.setProgress(fWaterPercent);
                tvCoffeeAmount.setText(fCoffee + " ml [" + fCoffeePercent + "%]");
                progressCoffee.setProgress(fCoffeePercent);
                tvTeaAmount.setText(fTea + " ml [" + fTeaPercent + "%]");
                progressTea.setProgress(fTeaPercent);
            });
        });
    }
} 