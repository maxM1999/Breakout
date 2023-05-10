package com.example.breakout
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.MotionEvent
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class Breakout() : AppCompatActivity(), SensorEventListener
{
    lateinit var breakoutLayout:BreakoutLayout;
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    lateinit private var Bmp:Bitmap;

    external fun generateQRCode(): IntArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val QrCodeData:IntArray = generateQRCode();
        val W = Math.sqrt(QrCodeData.size.toDouble()).toInt();
        val Bmp = Bitmap.createBitmap(W, W, Bitmap.Config.RGB_565);
        Bmp.setPixels(QrCodeData, 0, W, 0, 0, W, W)

        breakoutLayout = BreakoutLayout(this);
        breakoutLayout.BreakoutParent = this;
        breakoutLayout.setBackgroundColor(Color.WHITE);
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        breakoutLayout.layoutParams = params;
        breakoutLayout.CodeBitmap = Bmp;

        setContentView(breakoutLayout, params)

        startSensorDetection();
    }

    fun startSensorDetection()
    {
        sensorManager = this.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        sensorManager?.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }
    override fun onTouchEvent(event: MotionEvent?): Boolean
    {
        InputManager.getInstance().handleTouchEvent(event)
        return super.onTouchEvent(event)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]

            // Mettre à jour la valeur de DirX en fonction de l'accélération sur l'axe x
            if (x < -1.5) {
                InputManager.getInstance().SensorDirX = 1;
                InputManager.getInstance().IsScreenRotated = true;
            } else if (x > 1.5) {
                InputManager.getInstance().SensorDirX = -1;
                InputManager.getInstance().IsScreenRotated = true;
            } else {
                InputManager.getInstance().SensorDirX = 0;
                InputManager.getInstance().IsScreenRotated = false;
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    fun RestartBreakout()
    {
        val intent = Intent(this, Breakout::class.java)
        startActivity(intent);
    }
}