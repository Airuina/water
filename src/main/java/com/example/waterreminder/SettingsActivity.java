package com.example.waterreminder;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import java.time.LocalTime;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private EditText etGoal, etInterval;
    private TextView tvStartTime, tvEndTime;
    private Button btnSave, btnStartTime, btnEndTime;
    private Switch switchSmartReminder;
    private LocalTime startTime, endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeViews();
        loadSettings();
        setupListeners();
    }

    private void initializeViews() {
        etGoal = findViewById(R.id.etGoal);
        etInterval = findViewById(R.id.etInterval);
        tvStartTime = findViewById(R.id.tvStartTime);
        tvEndTime = findViewById(R.id.tvEndTime);
        btnStartTime = findViewById(R.id.btnStartTime);
        btnEndTime = findViewById(R.id.btnEndTime);
        btnSave = findViewById(R.id.btnSave);
        switchSmartReminder = findViewById(R.id.switchSmartReminder);
    }

    private void setupListeners() {
        btnStartTime.setOnClickListener(v -> showTimePickerDialog(true));
        btnEndTime.setOnClickListener(v -> showTimePickerDialog(false));

        btnSave.setOnClickListener(v -> {
            if (validateInputs()) {
                saveSettings();
                Toast.makeText(this, "设置保存成功", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        switchSmartReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            etInterval.setEnabled(!isChecked);
            if (isChecked) {
                Toast.makeText(this, "已启用智能提醒", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTimePickerDialog(boolean isStartTime) {
        LocalTime currentTime = isStartTime ? startTime : endTime;
        TimePickerDialog dialog = new TimePickerDialog(
            this,
            (view, hourOfDay, minute) -> {
                LocalTime newTime = LocalTime.of(hourOfDay, minute);
                if (isStartTime) {
                    if (newTime.isBefore(endTime)) {
                        startTime = newTime;
                        updateTimeDisplay(true);
                    } else {
                        Toast.makeText(this, "开始时间必须早于结束时间", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (newTime.isAfter(startTime)) {
                        endTime = newTime;
                        updateTimeDisplay(false);
                    } else {
                        Toast.makeText(this, "结束时间必须晚于开始时间", Toast.LENGTH_SHORT).show();
                    }
                }
            },
            currentTime.getHour(),
            currentTime.getMinute(),
            true
        );
        dialog.show();
    }

    private void updateTimeDisplay(boolean isStartTime) {
        String timeStr = String.format(Locale.getDefault(), "%02d:%02d",
            isStartTime ? startTime.getHour() : endTime.getHour(),
            isStartTime ? startTime.getMinute() : endTime.getMinute());
        
        if (isStartTime) {
            tvStartTime.setText("开始时间: " + timeStr);
        } else {
            tvEndTime.setText("结束时间: " + timeStr);
        }
    }

    private boolean validateInputs() {
            String goalStr = etGoal.getText().toString();
            String intervalStr = etInterval.getText().toString();

        if (goalStr.isEmpty() || (!switchSmartReminder.isChecked() && intervalStr.isEmpty())) {
                Toast.makeText(this, "请输入完整信息", Toast.LENGTH_SHORT).show();
            return false;
            }

        try {
            int goal = Integer.parseInt(goalStr);
            if (goal <= 0 || goal > 5000) {
                Toast.makeText(this, "每日目标应在0-5000ml之间", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (!switchSmartReminder.isChecked()) {
                int interval = Integer.parseInt(intervalStr);
                if (interval < 15 || interval > 240) {
                    Toast.makeText(this, "提醒间隔应在15-240分钟之间", Toast.LENGTH_SHORT).show();
                    return false;
            }
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "请输入有效的数字", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        
        // 加载每日目标和提醒间隔
        int goal = prefs.getInt("dailyWaterGoal", 2000);
        int interval = prefs.getInt("reminderInterval", 60);

        // 加载时间设置
        startTime = LocalTime.of(
            prefs.getInt("startHour", 8),
            prefs.getInt("startMinute", 0)
        );
        endTime = LocalTime.of(
            prefs.getInt("endHour", 22),
            prefs.getInt("endMinute", 0)
        );
        
        // 加载智能提醒设置
        boolean smartReminder = prefs.getBoolean("smartReminder", false);

        // 更新UI
        etGoal.setText(String.valueOf(goal));
        etInterval.setText(String.valueOf(interval));
        switchSmartReminder.setChecked(smartReminder);
        etInterval.setEnabled(!smartReminder);
        updateTimeDisplay(true);
        updateTimeDisplay(false);
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = getSharedPreferences("settings", MODE_PRIVATE).edit();
        
        // 保存每日目标和提醒间隔
        editor.putInt("dailyWaterGoal", Integer.parseInt(etGoal.getText().toString()));
        if (!switchSmartReminder.isChecked()) {
            editor.putInt("reminderInterval", Integer.parseInt(etInterval.getText().toString()));
        }
        
        // 保存时间设置
        editor.putInt("startHour", startTime.getHour());
        editor.putInt("startMinute", startTime.getMinute());
        editor.putInt("endHour", endTime.getHour());
        editor.putInt("endMinute", endTime.getMinute());
        
        // 保存智能提醒设置
        editor.putBoolean("smartReminder", switchSmartReminder.isChecked());
        
        editor.apply();
    }
}

