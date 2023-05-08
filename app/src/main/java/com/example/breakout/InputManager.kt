package com.example.breakout


import android.drm.DrmStore.RightsStatus
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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
    var SensorDirX = 0;
    var LeftButtonClicked:Boolean = false;
    var RightButtonClicked:Boolean = false;
    var IsScreenRotated:Boolean = false;

    fun handleTouchEvent(event: MotionEvent?)
    {
        // Ne rien mettre à jour si un des deux boutons d'inputs est appuyé.
        if(LeftButtonClicked == true or RightButtonClicked == true) return;

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

    fun SetLeftButtonClicked(State:Boolean)
    {
        LeftButtonClicked = State;
        if(State == false)
        {
            if(RightButtonClicked == false)
            {
                if(IsTouched == false)
                {
                    DirX = 0;
                }
            }
        }
        else
        {
            DirX = -1;
        }
    }

    fun SetRightButtonClicked(State:Boolean)
    {
        RightButtonClicked = State;
        if(State == false)
        {
            if(LeftButtonClicked == false)
            {
                if(IsTouched == false)
                {
                    DirX = 0;
                }
            }
        }
        else
        {
            DirX = 1;
        }
    }

    fun IsButtonClicked():Boolean
    {
        if(LeftButtonClicked or RightButtonClicked) return true;
        return false;
    }

    fun GetDirX():Int
    {
        return DirX;
    }

    fun SetDirRotated(State: Boolean)
    {
        IsScreenRotated = State;
    }
}