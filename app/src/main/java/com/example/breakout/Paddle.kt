package com.example.breakout

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class Paddle(X:Float, Y:Float, W:Float, H:Float):GameObject(X, Y, W, H)
{
    companion object
    {
        const val LEFT = -1;
        const val RIGHT = 1
        const val IDLE = 0
    }

    private var Speed:Float = 5000f;
    private var Dir:Int = 0

    init
    {
        paint.color = Color.BLACK;
        paint.style = Paint.Style.FILL;
    }

    override fun Update(DeltaTime: Float) {
        super.Update(DeltaTime)

        if(InputManager.getInstance().IsTouched)
        {
            Dir = GetDirectionTowardTouchX(InputManager.getInstance().TouchX);
            X += Dir * Speed * DeltaTime;
            AdjustPosition(InputManager.getInstance().TouchX)
        }

        UpdateRect();
    }

    override fun Draw(canvas: Canvas?) {
        super.Draw(canvas)

        canvas?.drawRect(Rect, paint)
    }

    /* Retourne la direction que la paddle doit avoir pour atteindre le point touché par le joueur */
    private fun GetDirectionTowardTouchX(TouchX:Float):Int
    {
        var CurrPosX = X + (W / 2);

        if(CurrPosX < TouchX)
        {
            return RIGHT;
        }
        else if(CurrPosX > TouchX)
        {
            return LEFT;
        }
        else
        {
            return IDLE
        }
    }

    /* Si après son déplacement la paddle dépasse de son point de destination, la ramener ou elle doit être. */
    private fun AdjustPosition(TouchX:Float)
    {
        var CurrPosX = X + (W / 2);

        when(Dir)
        {
            RIGHT ->
            {
                if(CurrPosX > TouchX)
                {
                    X = TouchX - (W / 2);
                }
                else if(X + W > ScreenX)
                {
                    X = ScreenX - W;
                }
            }
            LEFT ->
            {
                if(CurrPosX < TouchX)
                {
                    X = TouchX - (W / 2);
                }
            }
        }
    }
}