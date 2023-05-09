package com.example.breakout

import android.graphics.Color
import android.graphics.Paint

import java.util.Vector

class Ball(X:Float, Y:Float, W:Float, H:Float, ScreenSize:Vector2, private val Speed:Float):GameObject(X,Y, W, H, ScreenSize)
{
    private var DirX:Int = 1;
    private var DirY:Int = -1;

    init
    {
        paint.color = Color.BLACK;
        paint.style = Paint.Style.FILL;
    }

    override fun Update(DeltaTime: Float) {
        super.Update(DeltaTime)

        X += DirX * Speed * DeltaTime;
        Y += DirY * Speed * DeltaTime;

        if(IsOutOfScreenX())
        {
            ChangeDir(true, false);

            if(X <= 0f)
            {
                X = 0f;
            }
            else
            {
                X = ScreenSize.X - W;
            }
        }

        if(IsOutOfScreenY())
        {
            ChangeDir(false, true);

            if(Y <= 0f)
            {
                Y = 0f;
            }
            else
            {
                Parent.ShowPopup(false);
                Parent.SetShouldUpdate(false);
                Y = ScreenSize.Y - H;
            }
        }

        UpdateRect();
    }

    fun ChangeDir(ChangeX:Boolean, ChangeY:Boolean)
    {
        if(ChangeX)
        {
            DirX = -DirX;
        }
        if(ChangeY)
        {
            DirY = -DirY;
        }
    }

    fun GetDir():Vector2
    {
        return Vector2(DirX.toFloat(), DirY.toFloat());
    }

    fun SetPos(NewPos:Vector2)
    {
        X = NewPos.X;
        Y = NewPos.Y;

        UpdateRect();
    }

    private fun IsOutOfScreenX():Boolean
    {
        if(X <= 0 || X + W >= ScreenSize.X)
        {
            return true;
        }
        return false;
    }

    private fun IsOutOfScreenY():Boolean
    {
        if(Y <= 0 || Y + H >= ScreenSize.Y)
        {
            return true;
        }
        return false;
    }
}