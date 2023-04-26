package com.example.breakout

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.MotionEvent
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class Breakout() : AppCompatActivity()
{
    lateinit var breakoutLayout:BreakoutLayout;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        breakoutLayout = BreakoutLayout(this);
        breakoutLayout.setBackgroundColor(Color.WHITE);
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        breakoutLayout.layoutParams = params;

        setContentView(breakoutLayout, params)
    }
    override fun onTouchEvent(event: MotionEvent?): Boolean
    {
        InputManager.getInstance().handleTouchEvent(event)
        return super.onTouchEvent(event)
    }
}