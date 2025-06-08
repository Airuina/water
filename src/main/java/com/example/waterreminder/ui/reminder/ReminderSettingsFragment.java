package com.example.waterreminder.ui.reminder;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.waterreminder.R;
import com.example.waterreminder.ReminderService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import static android.content.Context.MODE_PRIVATE;

public class ReminderSettingsFragment extends Fragment {

    private RecyclerView rvTimeReminders;
    private MaterialButton btnAddTime;
    private MaterialButton btnCloseAllReminders;
    private SwitchMaterial switchInterval;
    private TimeReminderAdapter adapter;
    private List<TimeReminder> reminders;
    private SharedPreferences prefs;
    private static final String PREF_TIME_REMINDERS = "time_reminders";
    private static final String PREF_INTERVAL_ENABLED = "interval_enabled";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_reminder_settings, container, false);

        prefs = requireActivity().getSharedPreferences("reminder_settings", MODE_PRIVATE);
        initViews(root);
        loadSettings();
        setupListeners();

        return root;
    }

    private void initViews(View root) {
        rvTimeReminders = root.findViewById(R.id.rvTimeReminders);
        btnAddTime = root.findViewById(R.id.btnAddTime);
        btnCloseAllReminders = root.findViewById(R.id.btnCloseAllReminders);
        switchInterval = root.findViewById(R.id.switchInterval);

        rvTimeReminders.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new TimeReminderAdapter(new ArrayList<>(), this::onTimeReminderToggled, this::onTimeReminderDeleted);
        rvTimeReminders.setAdapter(adapter);
    }

    private void loadSettings() {
        // 加载定时提醒列表
        String remindersJson = prefs.getString(PREF_TIME_REMINDERS, "[]");
        reminders = new Gson().fromJson(remindersJson, new TypeToken<List<TimeReminder>>(){}.getType());
        adapter.updateReminders(reminders);

        // 加载间隔提醒设置
        boolean intervalEnabled = prefs.getBoolean(PREF_INTERVAL_ENABLED, false);
        switchInterval.setChecked(intervalEnabled);
    }

    private void setupListeners() {
        btnAddTime.setOnClickListener(v -> showTimePickerDialog());
        btnCloseAllReminders.setOnClickListener(v -> closeAllReminders());
        switchInterval.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(PREF_INTERVAL_ENABLED, isChecked).apply();
            updateReminderService();
        });
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
            requireContext(),
            (view, hourOfDay, minute1) -> addTimeReminder(hourOfDay, minute1),
            hour,
            minute,
            true
        );
        timePickerDialog.show();
    }

    private void addTimeReminder(int hour, int minute) {
        TimeReminder newReminder = new TimeReminder(hour, minute, true);
        reminders.add(newReminder);
        saveReminders();
        adapter.updateReminders(reminders);
        updateReminderService();
    }

    private void onTimeReminderToggled(int position, boolean isEnabled) {
        reminders.get(position).setEnabled(isEnabled);
        saveReminders();
        updateReminderService();
    }

    private void onTimeReminderDeleted(int position) {
        reminders.remove(position);
        saveReminders();
        adapter.updateReminders(reminders);
        updateReminderService();
    }

    private void closeAllReminders() {
        reminders.clear();
        switchInterval.setChecked(false);
        saveReminders();
        prefs.edit().putBoolean(PREF_INTERVAL_ENABLED, false).apply();
        adapter.updateReminders(reminders);
        updateReminderService();
    }

    private void saveReminders() {
        String remindersJson = new Gson().toJson(reminders);
        prefs.edit().putString(PREF_TIME_REMINDERS, remindersJson).apply();
    }

    private void updateReminderService() {
        Intent serviceIntent = new Intent(requireContext(), ReminderService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(serviceIntent);
        } else {
            requireContext().startService(serviceIntent);
        }
    }

    public static class TimeReminder {
        private int hour;
        private int minute;
        private boolean enabled;

        public TimeReminder(int hour, int minute, boolean enabled) {
            this.hour = hour;
            this.minute = minute;
            this.enabled = enabled;
        }

        public int getHour() { return hour; }
        public int getMinute() { return minute; }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public String getTimeString() {
            return String.format("%02d:%02d", hour, minute);
        }
    }
} 