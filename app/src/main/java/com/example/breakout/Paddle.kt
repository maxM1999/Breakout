package com.example.breakout

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.renderscript.ScriptGroup.Input

class Paddle(X:Float, Y:Float, W:Float, H:Float, ScreenSize:Vector2):GameObject(X,Y, W, H, ScreenSize)
{
    companion object
    {
        const val LEFT = -1;
        const val RIGHT = 1
        const val IDLE = 0
    }

    private var Speed:Float = 5000f; // Vitesse lorsque bougée par le toucher
    private var ButtonSpeed:Float = 2000f; // Vitesse lorsque bougée par les bouttons
    private var SpeedForScreenRotation:Float = 700f;// Vitesse lorsque bougée par rotation de l'écran
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
        else if(InputManager.getInstance().IsButtonClicked())
        {
            Dir = InputManager.getInstance().GetDirX();
            X += Dir * ButtonSpeed * DeltaTime;
            AdjustPositionFromButton();
        }
        else if(InputManager.getInstance().IsScreenRotated)
        {
            Dir = InputManager.getInstance().SensorDirX;
            X += Dir * SpeedForScreenRotation * DeltaTime;
            AdjustPositionFromButton();
        }

        UpdateRect();
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
                if(X + W > ScreenSize.X)
                {
                    X = ScreenSize.X - W;
                }
            }
            LEFT ->
            {
                if(CurrPosX < TouchX)
                {
                    X = TouchX - (W / 2);
                }
                if(X < 0f)
                {
                    X = 0f;
                }
            }
        }
    }

    /* Si après son déplacement la paddle dépasse l'écran, la ramener ou elle doit être. */
    private fun AdjustPositionFromButton()
    {
        var CurrPosX = X + (W / 2);

        when(Dir)
        {
            RIGHT ->
            {
                if(X + W > ScreenSize.X)
                {
                    X = ScreenSize.X - W;
                }
            }
            LEFT ->
            {
                if(X < 0f)
                {
                    X = 0f;
                }
            }
        }
    }
}