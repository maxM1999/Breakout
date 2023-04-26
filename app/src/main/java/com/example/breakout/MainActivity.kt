package com.example.breakout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.TextView
import com.example.breakout.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Example of a call to a native method
        binding.sampleText.text = stringFromJNI()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean
    {
        when(event?.actionMasked)
        {
            MotionEvent.ACTION_MOVE ->
            {
                val intent = Intent(this, Breakout::class.java)
                startActivity(intent);
            }
        }

        return super.onTouchEvent(event);
    }

    /**
     * A native method that is implemented by the 'breakout' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'breakout' library on application startup.
        init {
            System.loadLibrary("breakout")
        }
    }
}