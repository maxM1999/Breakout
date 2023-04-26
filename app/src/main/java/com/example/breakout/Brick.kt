package com.example.breakout

import android.graphics.Color
import android.graphics.Paint

class Brick(X:Float, Y:Float, W:Float, H:Float, ScreenSize:Vector2) : GameObject(X, Y, W, H, ScreenSize)
{
    init
    {
        paint.color = Color.RED;
        paint.style = Paint.Style.FILL;
    }
}
