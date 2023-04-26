package com.example.breakout

import android.view.MotionEvent

class InputManager private constructor()
{
    // éléments static à la classe
    companion object
    {
        private var instance: InputManager? = null

        fun getInstance(): InputManager
        {
            if (instance == null)
            {
                instance = InputManager()
            }
            return instance as InputManager
        }
    }

    var IsTouched:Boolean = false;
    var TouchX:Float = 0f;
    var TouchY:Float = 0f;
    var DirX:Int = 0;
    var DirY:Int = 0;

    fun handleTouchEvent(event: MotionEvent?)
    {
        when (event?.actionMasked)
        {
            MotionEvent.ACTION_DOWN ->
            {
                IsTouched = true;
                TouchX = event.x;
                TouchY = event.y;
            }
            MotionEvent.ACTION_MOVE ->
            {
                UpdateDir(event.x, event.y);

                TouchX = event.x;
                TouchY = event.y;
            }
            MotionEvent.ACTION_UP ->
            {
                IsTouched = false;
                DirX = 0;
                DirY = 0;
            }
        }
    }

    fun UpdateDir(x:Float, y:Float)
    {
        if(x < TouchX)
        {
            DirX = -1
        }
        else
        {
            DirX = 1;
        }

        if(y < TouchY)
        {
            DirY = -1
        }
        else
        {
            DirY = 1;
        }
    }
}