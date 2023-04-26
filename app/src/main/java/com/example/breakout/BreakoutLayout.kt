package com.example.breakout

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.SystemClock
import android.widget.LinearLayout

class BreakoutLayout(context: Context) : LinearLayout(context)
{
    private val GameObjectList = mutableListOf<GameObject>();
    private var LastFrameTime = SystemClock.elapsedRealtime()
    private var DeltaTime:Float = 0f;

    init
    {
        val DisplayMetrics = resources.displayMetrics;
        val ScreenWidth = DisplayMetrics.widthPixels.toFloat();
        val ScreenHeight = DisplayMetrics.heightPixels.toFloat();

        val MyPaddle:GameObject = Paddle(ScreenWidth / 2, ScreenHeight / 2, 350f, 150f);
        MyPaddle.SetScreenSize(ScreenWidth, ScreenHeight);
        GameObjectList.add(MyPaddle);
    }

    override fun onDraw(canvas: Canvas?)
    {
        super.onDraw(canvas)

        UpdateDeltaTime();

        // Update gameObjects
        GameObjectList.forEach { it.Update(DeltaTime) }

        // Draw gameObjects
        GameObjectList.forEach { it.Draw(canvas) }

        invalidate();
    }

    private fun UpdateDeltaTime()
    {
        val currentTime = SystemClock.elapsedRealtime()
        DeltaTime = (currentTime - LastFrameTime) / 1000f
        LastFrameTime = currentTime
    }
}