package com.example.waterreminder.ui.custom;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class WaterGlassView extends View {
    private Paint glassPaint;
    private Paint waterPaint;
    private Path waterPath;
    private RectF glassRect;
    private float waterLevel = 0f;
    private float waveOffset = 0f;
    private ValueAnimator waveAnimator;

    public WaterGlassView(Context context) {
        super(context);
        init();
    }

    public WaterGlassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // 初始化玻璃杯画笔
        glassPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        glassPaint.setStyle(Paint.Style.STROKE);
        glassPaint.setColor(Color.BLUE);
        glassPaint.setStrokeWidth(4f);

        // 初始化水画笔
        waterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        waterPaint.setStyle(Paint.Style.FILL);
        waterPaint.setColor(Color.argb(128, 33, 150, 243));

        waterPath = new Path();
        glassRect = new RectF();

        // 初始化波浪动画
        setupWaveAnimation();
    }

    private void setupWaveAnimation() {
        waveAnimator = ValueAnimator.ofFloat(0f, 2f * (float) Math.PI);
        waveAnimator.setDuration(2000);
        waveAnimator.setRepeatCount(ValueAnimator.INFINITE);
        waveAnimator.setInterpolator(new DecelerateInterpolator());
        waveAnimator.addUpdateListener(animation -> {
            waveOffset = (float) animation.getAnimatedValue();
            invalidate();
        });
        waveAnimator.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float padding = 20f;
        glassRect.set(padding, padding, w - padding, h - padding);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // 绘制玻璃杯
        canvas.drawRoundRect(glassRect, 20f, 20f, glassPaint);

        // 计算水位高度
        float waterHeight = glassRect.height() * waterLevel;
        float baseY = glassRect.bottom - waterHeight;

        // 绘制水
        waterPath.reset();
        waterPath.moveTo(glassRect.left, glassRect.bottom);
        
        // 绘制波浪
        float amplitude = 15f;
        float waveLength = glassRect.width() / 2;
        
        for (float x = glassRect.left; x <= glassRect.right; x += 5) {
            float dx = (x - glassRect.left) / waveLength;
            float y = baseY + amplitude * (float) Math.sin(dx * Math.PI * 2 + waveOffset);
            if (x == glassRect.left) {
                waterPath.moveTo(x, y);
            } else {
                waterPath.lineTo(x, y);
            }
        }

        waterPath.lineTo(glassRect.right, glassRect.bottom);
        waterPath.lineTo(glassRect.left, glassRect.bottom);
        waterPath.close();

        canvas.drawPath(waterPath, waterPaint);
    }

    public void setWaterLevel(float level) {
        ValueAnimator animator = ValueAnimator.ofFloat(this.waterLevel, level);
        animator.setDuration(500);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            this.waterLevel = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (waveAnimator != null) {
            waveAnimator.cancel();
        }
    }
} 