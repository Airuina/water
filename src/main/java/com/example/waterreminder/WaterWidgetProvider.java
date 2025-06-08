package com.example.waterreminder;

import android.content.Context;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WaterWidgetProvider {

    // 获取今天喝水次数（同步阻塞调用）
    public static int getTodayDrinkCount(Context context) {
        DrinkDatabase db = DrinkDatabase.getInstance(context);
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Future<Integer> future = executor.submit(new Callable<Integer>() {
            @Override
            public Integer call() {
                return db.drinkDao().getTodayCount(StatsUtils.getTodayStartMillis());
            }
        });

        try {
            return future.get(); // 阻塞等待查询结果
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return 0;
        } finally {
            executor.shutdown();
        }
    }

    // 异步插入喝水记录，不阻塞调用线程
    public static void insertDrinkRecord(Context context) {
        DrinkDatabase db = DrinkDatabase.getInstance(context);
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> db.drinkDao().insert(new DrinkRecord(System.currentTimeMillis(), 350, "白水")));

        executor.shutdown();
    }
}
