package com.example.breakout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.widget.Button
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView
import com.example.breakout.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var ButtonLayout:LinearLayout;
    private lateinit var StartBtn: Button;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Example of a call to a native method
        binding.sampleText.text = stringFromJNI()

        ButtonLayout = LinearLayout(this);
        ButtonLayout.orientation = LinearLayout.VERTICAL;
        var params:LayoutParams = LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
        )

        ButtonLayout.layoutParams = params;

        StartBtn = Button(this);
        StartBtn.text = "Start";
        StartBtn.gravity = Gravity.CENTER_HORIZONTAL;

        StartBtn.layoutParams = params;
        ButtonLayout.addView(StartBtn);
        StartBtn.setOnClickListener {
            OnStartBtnClicked();
        }

        setContentView(ButtonLayout);

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

    fun OnStartBtnClicked()
    {
        val intent = Intent(this, Breakout::class.java)
        startActivity(intent);
    }
}