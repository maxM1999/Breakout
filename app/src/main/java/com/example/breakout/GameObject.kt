package com.example.breakout

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

open class GameObject(protected var X:Float, protected var Y:Float, protected var W:Float, protected var H:Float, protected var ScreenSize:Vector2)
{
    protected val Rect = RectF(X, Y, X + W, Y + H);
    protected val paint = Paint();

    open fun Update(DeltaTime:Float)
    {}

    fun Draw(canvas: Canvas?)
    {
        canvas?.drawRect(Rect, paint);
    }

    fun UpdateRect()
    {
        Rect.set(X, Y, X + W, Y + H)
    }

    /* Get collision rectangle */
    fun GetRect():RectF
    {
        return Rect;
    }
}