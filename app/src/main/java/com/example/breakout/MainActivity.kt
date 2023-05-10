package com.example.breakout
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import android.widget.LinearLayout.LayoutParams
import com.example.breakout.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var ButtonLayout:LinearLayout;
    private lateinit var StartBtn: Button;
    private lateinit var EndBtn: Button;

    @SuppressLint("WrongThread")
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

        val buttonLayoutParams = LinearLayout.LayoutParams(
            LayoutParams.MATCH_PARENT, 0, 1f
        )

        ButtonLayout.layoutParams = params;

        // Créer les boutons du menu et les ajouter au layout
        StartBtn = Button(this);
        StartBtn.text = "Start";
        StartBtn.gravity = Gravity.CENTER_HORIZONTAL;
        StartBtn.layoutParams = buttonLayoutParams;
        ButtonLayout.addView(StartBtn);
        StartBtn.setOnClickListener {
            OnStartBtnClicked();
        }

        EndBtn = Button(this);
        EndBtn.text = "Quit";
        EndBtn.gravity = Gravity.CENTER_HORIZONTAL;
        EndBtn.layoutParams = buttonLayoutParams;
        EndBtn.setOnClickListener {
            finishAffinity();
        }

        ButtonLayout.addView(EndBtn);

       /* val QrCodeData:IntArray = generateQRCode();
        val W = Math.sqrt(QrCodeData.size.toDouble()).toInt();
        val bmp = Bitmap.createBitmap(W, W, Bitmap.Config.RGB_565);
        bmp.setPixels(QrCodeData, 0, W, 0, 0, W, W)

        var MyImage = ImageView(this);
        MyImage.setImageBitmap(bmp);

        val frameLayout = FrameLayout(this);
        val layoutParams = FrameLayout.LayoutParams(500, 500, Gravity.CENTER);
        frameLayout.addView(MyImage, layoutParams);

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("message")
        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            // Action à effectuer lorsque l'utilisateur clique sur le bouton OK
            dialog.dismiss() // Ferme la fenêtre contextuelle
        }
        alertDialogBuilder.setView(frameLayout);

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()*/

        ButtonLayout.setBackgroundColor(Color.BLACK)


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