package com.example.breakout

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

open class GameObject(protected var X:Float, protected var Y:Float, protected var W:Float, protected var H:Float)
{
    protected val Rect = RectF(X, Y, X + W, Y + H);
    protected val paint = Paint();
    protected var ScreenX:Float = 0f;
    protected var ScreenY:Float = 0f;


    open fun Update(DeltaTime:Float)
    {}

    open fun Draw(canvas: Canvas?)
    {

    }

    fun UpdateRect()
    {
        Rect.set(X, Y, X + W, Y + H)
    }

    fun SetScreenSize(InScreenX:Float, InScreenY:Float)
    {
        ScreenX = InScreenX;
        ScreenY = InScreenY;
    }

    /* Get collision rectangle */
    fun GetRect():RectF
    {
        return Rect;
    }
}