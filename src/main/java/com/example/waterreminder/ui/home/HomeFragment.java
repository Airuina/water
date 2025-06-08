package com.example.waterreminder.ui.home;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.waterreminder.DrinkDatabase;
import com.example.waterreminder.DrinkRecord;
import com.example.waterreminder.R;
import com.example.waterreminder.StatsUtils;
import com.example.waterreminder.ui.custom.WaterGlassView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private static final int DAILY_GOAL = 2000; // 每日目标2000ml
    private DrinkDatabase db;
    private TextView tvTodayCount;
    private WaterGlassView waterGlassView;
    private MaterialButton btnWater, btnCoffee, btnTea;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = DrinkDatabase.getInstance(requireContext());
        
        // 初始化视图
        tvTodayCount = view.findViewById(R.id.tv_today_count);
        waterGlassView = view.findViewById(R.id.water_glass_view);
        btnWater = view.findViewById(R.id.btn_water);
        btnCoffee = view.findViewById(R.id.btn_coffee);
        btnTea = view.findViewById(R.id.btn_tea);

        // 设置点击事件
        btnWater.setOnClickListener(v -> showWaterAmountDialog());
        btnCoffee.setOnClickListener(v -> addDrinkRecord("咖啡", 200));
        btnTea.setOnClickListener(v -> addDrinkRecord("茶", 250));

        // 更新今日统计
        updateTodayCount();
    }

    private void showWaterAmountDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_custom_amount, null);
        TextInputEditText etAmount = dialogView.findViewById(R.id.et_water_amount);
        MaterialButton btnConfirm = dialogView.findViewById(R.id.btn_confirm);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btn_cancel);

        // 设置默认值
        etAmount.setText("350");
        etAmount.selectAll();

        Dialog dialog = new MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .create();

        btnConfirm.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString();
            if (!amountStr.isEmpty()) {
                try {
                    int amount = Integer.parseInt(amountStr);
                    if (amount > 0) {
                        addDrinkRecord("白水", amount);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(requireContext(), "请输入大于0的数值", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(requireContext(), "请输入有效的数字", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void addDrinkRecord(String type, int amount) {
        Executors.newSingleThreadExecutor().execute(() -> {
            DrinkRecord record = new DrinkRecord();
            record.timestamp = System.currentTimeMillis();
            record.type = type;
            record.amount = amount;
            
            db.drinkDao().insert(record);
            
            // 更新UI
            requireActivity().runOnUiThread(() -> {
                updateTodayCount();
            });
        });
    }

    private void updateTodayCount() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // 获取今日起始时间
            long todayStart = StatsUtils.getTodayStartMillis();
            
            // 获取今日总量
            int totalAmount = db.drinkDao().getTotalAmountBetween(todayStart, System.currentTimeMillis());
            
            // 更新UI
            requireActivity().runOnUiThread(() -> {
                tvTodayCount.setText(String.format("今日已喝：%d ml", totalAmount));
                // 更新水位动画
                float progress = Math.min(1f, (float) totalAmount / DAILY_GOAL);
                waterGlassView.setWaterLevel(progress);
            });
        });
    }
} 