package com.example.waterreminder.ui.reminder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.waterreminder.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.List;

public class TimeReminderAdapter extends RecyclerView.Adapter<TimeReminderAdapter.ViewHolder> {

    private List<ReminderSettingsFragment.TimeReminder> reminders;
    private final OnReminderToggledListener toggleListener;
    private final OnReminderDeletedListener deleteListener;

    public interface OnReminderToggledListener {
        void onReminderToggled(int position, boolean isEnabled);
    }

    public interface OnReminderDeletedListener {
        void onReminderDeleted(int position);
    }

    public TimeReminderAdapter(List<ReminderSettingsFragment.TimeReminder> reminders,
                             OnReminderToggledListener toggleListener,
                             OnReminderDeletedListener deleteListener) {
        this.reminders = reminders;
        this.toggleListener = toggleListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_time_reminder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReminderSettingsFragment.TimeReminder reminder = reminders.get(position);
        holder.tvTime.setText(reminder.getTimeString());
        holder.switchEnabled.setChecked(reminder.isEnabled());

        holder.switchEnabled.setOnCheckedChangeListener((buttonView, isChecked) ->
            toggleListener.onReminderToggled(position, isChecked));

        holder.btnDelete.setOnClickListener(v ->
            deleteListener.onReminderDeleted(position));
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    public void updateReminders(List<ReminderSettingsFragment.TimeReminder> newReminders) {
        this.reminders = newReminders;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime;
        SwitchMaterial switchEnabled;
        ImageView btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            switchEnabled = itemView.findViewById(R.id.switchEnabled);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
} 