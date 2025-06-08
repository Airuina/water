package com.example.waterreminder;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class WaterWidget extends AppWidgetProvider {

    public static final String ACTION_WIDGET_CLICK = "com.example.waterreminder.WIDGET_CLICK";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_water);

            // 点击Widget的按钮，发送广播
            Intent intent = new Intent(context, WaterWidget.class);
            intent.setAction(ACTION_WIDGET_CLICK);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.btnDrinkWater, pendingIntent);

            // 显示今日喝水次数
            int todayCount = WaterWidgetProvider.getTodayDrinkCount(context);
            views.setTextViewText(R.id.tvDrinkCount, "今日已喝水 " + todayCount + " 次");

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_WIDGET_CLICK.equals(intent.getAction())) {
            // 这里可以做点击事件，比如新增喝水记录
            WaterWidgetProvider.insertDrinkRecord(context);

            // 更新所有Widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context, WaterWidget.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }
}

