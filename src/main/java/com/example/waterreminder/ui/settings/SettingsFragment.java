package com.example.waterreminder.ui.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import com.example.waterreminder.LoginActivity;
import com.example.waterreminder.R;
import com.example.waterreminder.ReminderService;
import com.google.android.material.button.MaterialButton;
import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends Fragment {

    private TimePicker tpStartTime, tpEndTime;
    private EditText etReminderInterval;
    private SwitchCompat switchTimeReminder, switchIntervalReminder;
    private ViewGroup timeReminderContainer, intervalReminderContainer;
    private MaterialButton btnSave;
    private MaterialButton btnLogout;
    private SharedPreferences prefs;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        prefs = requireActivity().getSharedPreferences("settings", MODE_PRIVATE);
        
        // 初始化视图
        initViews(root);
        // 加载设置
        loadSettings();
        // 设置监听器
        setupListeners();

        return root;
    }

    private void initViews(View root) {
        tpStartTime = root.findViewById(R.id.tp_start_time);
        tpEndTime = root.findViewById(R.id.tp_end_time);
        etReminderInterval = root.findViewById(R.id.et_reminder_interval);
        switchTimeReminder = root.findViewById(R.id.switch_time_reminder);
        switchIntervalReminder = root.findViewById(R.id.switch_interval_reminder);
        timeReminderContainer = root.findViewById(R.id.time_reminder_container);
        intervalReminderContainer = root.findViewById(R.id.interval_reminder_container);
        btnSave = root.findViewById(R.id.btn_save);
        btnLogout = root.findViewById(R.id.btnLogout);
    }

    private void setupListeners() {
        // 保存设置
        btnSave.setOnClickListener(v -> saveSettings());

        // 退出登录
        btnLogout.setOnClickListener(v -> {
            requireActivity().getSharedPreferences("user_prefs", MODE_PRIVATE)
                .edit()
                .remove("is_logged_in")
                .remove("current_user")
                .apply();

            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        // 定时提醒开关
        switchTimeReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            timeReminderContainer.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (isChecked) {
                switchIntervalReminder.setChecked(false);
            }
        });

        // 间隔提醒开关
        switchIntervalReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            intervalReminderContainer.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (isChecked) {
                switchTimeReminder.setChecked(false);
            }
        });
    }

    private void loadSettings() {
        // 加载提醒类型
        boolean isTimeReminder = prefs.getBoolean("is_time_reminder", false);
        boolean isIntervalReminder = prefs.getBoolean("is_interval_reminder", false);
        
        switchTimeReminder.setChecked(isTimeReminder);
        switchIntervalReminder.setChecked(isIntervalReminder);
        
        timeReminderContainer.setVisibility(isTimeReminder ? View.VISIBLE : View.GONE);
        intervalReminderContainer.setVisibility(isIntervalReminder ? View.VISIBLE : View.GONE);

        // 加载时间范围
        int startHour = prefs.getInt("start_hour", 8);
        int startMinute = prefs.getInt("start_minute", 0);
        int endHour = prefs.getInt("end_hour", 22);
        int endMinute = prefs.getInt("end_minute", 0);

        tpStartTime.setHour(startHour);
        tpStartTime.setMinute(startMinute);
        tpEndTime.setHour(endHour);
        tpEndTime.setMinute(endMinute);

        // 加载提醒间隔
        int reminderInterval = prefs.getInt("reminder_interval", 60);
        etReminderInterval.setText(String.valueOf(reminderInterval));
    }

    private void saveSettings() {
        try {
            // 验证间隔时间
            if (switchIntervalReminder.isChecked()) {
                int interval = Integer.parseInt(etReminderInterval.getText().toString());
                if (interval < 1) {
                    Toast.makeText(requireContext(), "提醒间隔不能小于1分钟", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            SharedPreferences.Editor editor = prefs.edit();
            
            // 保存提醒类型
            editor.putBoolean("is_time_reminder", switchTimeReminder.isChecked());
            editor.putBoolean("is_interval_reminder", switchIntervalReminder.isChecked());

            // 保存时间范围
            editor.putInt("start_hour", tpStartTime.getHour());
            editor.putInt("start_minute", tpStartTime.getMinute());
            editor.putInt("end_hour", tpEndTime.getHour());
            editor.putInt("end_minute", tpEndTime.getMinute());

            // 保存提醒间隔
            if (switchIntervalReminder.isChecked()) {
                editor.putInt("reminder_interval", 
                    Integer.parseInt(etReminderInterval.getText().toString()));
            }

            editor.apply();

            Toast.makeText(requireContext(), "设置已保存", Toast.LENGTH_SHORT).show();
            
            // 重启提醒服务
            Intent serviceIntent = new Intent(requireContext(), ReminderService.class);
            requireContext().stopService(serviceIntent);
            requireContext().startService(serviceIntent);
            
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "请输入有效的提醒间隔", Toast.LENGTH_SHORT).show();
        }
    }
} 