package com.example.breakout


import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.view.View
import android.view.animation.AccelerateInterpolator

class Particle( var X: Float, var Y: Float, var Vx: Float, var Vy: Float, var size: Float)
{
    private val MyPaint:Paint = Paint();
    private var LifeTime:Float = (Math.random() * 1 + 0.1).toFloat();

    init
    {
        MyPaint.color = Color.RED;
    }
    fun Update(DeltaTime: Float)
    {
        X += Vx * DeltaTime;
        Y += Vy * DeltaTime;

        LifeTime -= DeltaTime;
    }

    fun Draw(canvas: Canvas?)
    {
        canvas?.drawCircle(X, Y, size, MyPaint)
    }

    fun GetLifeTime():Float
    {
        return LifeTime;
    }
}

class Brick(X:Float, Y:Float, W:Float, H:Float, ScreenSize:Vector2) : GameObject(X, Y, W, H, ScreenSize)
{
    init
    {
        paint.color = Color.RED;
        paint.style = Paint.Style.FILL;

    }
}
