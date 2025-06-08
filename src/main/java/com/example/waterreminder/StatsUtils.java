package com.example.waterreminder;

import java.util.Calendar;
import java.util.List;

public class StatsUtils {

    // 返回今天零点毫秒数
    public static long getTodayStartMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    // 连续达标天数统计
    public static int getConsecutiveGoalDays(DrinkDao dao, int dailyGoal) {
        int consecutiveDays = 0;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        while (true) {
            long dayStart = cal.getTimeInMillis();
            cal.add(Calendar.DAY_OF_YEAR, 1);
            long dayEnd = cal.getTimeInMillis();
            
            int totalAmount = dao.getTotalAmountBetween(dayStart, dayEnd);
            if (totalAmount >= dailyGoal) {
                consecutiveDays++;
                cal.add(Calendar.DAY_OF_YEAR, -2); // 检查前一天
            } else {
                break;
            }
        }
        return consecutiveDays;
    }

    // 计算平均喝水间隔（毫秒），若无足够数据返回0
    public static long getAverageInterval(List<DrinkRecord> records) {
        if (records == null || records.size() < 2) {
            return 0;
        }
        
        long totalInterval = 0;
        for (int i = 1; i < records.size(); i++) {
            totalInterval += records.get(i).timestamp - records.get(i-1).timestamp;
        }
        return totalInterval / (records.size() - 1);
    }
}

