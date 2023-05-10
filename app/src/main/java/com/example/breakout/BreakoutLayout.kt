package com.example.breakout

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.os.Handler
import android.os.SystemClock
import android.view.Gravity
import android.view.MotionEvent
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog


class BrickGrid(var Rows:Int, var Col:Int)
{
    var BricksList = mutableListOf<Brick>();
}
class Vector2(var X:Float, var Y:Float)
{
    fun SetPos(InX:Float, InY:Float)
    {
        X = InX;
        Y = InY;
    }
}

class BreakoutLayout(context: Context) : LinearLayout(context)
{
    private val GameObjectList = mutableListOf<GameObject>();
    private val ParticlesList = mutableListOf<Particle>();
    private var LastFrameTime = SystemClock.elapsedRealtime();
    private var DeltaTime:Float = 0f;
    lateinit var ScreenSize:Vector2;
    lateinit var GridOfBrick:BrickGrid; // Grille contenant une liste de brick
    lateinit var MyPaddle:Paddle;
    lateinit var MyBall:Ball;
    var LeftButton: Button; // Bouton pour bouger vers la gauche
    var RightButton: Button; // Bouton pour bouger vers la droite
    var ButtonLayout:LinearLayout; // Layout qui contient les boutons pour bougewr la paddle
    lateinit var BreakoutParent:Breakout; // Activité Breakout parent de ce layout
    private var ShouldUpdate:Boolean = true;
    val shake = ObjectAnimator.ofFloat(this, "translationX", -10f, 10f);
    private val handler = Handler();

    private val runnable = object : Runnable
    {
        override fun run()
        {
            // Ajouter une rangée de brick tout les 40 secondes
            AddBrickRow();
            handler.postDelayed(this, 40000);
        }
    }

    init
    {
        // Démarrer le timer pour l'ajout des rangées de brick
        handler.postDelayed(runnable, 40000)

        // Config pour le shake de l'écran
        shake.duration = 50
        shake.repeatCount = 5
        shake.interpolator = LinearInterpolator()

        // Créer le layout qui contient les boutons pour bouger la paddle
        ButtonLayout = LinearLayout(context);
        ButtonLayout.orientation = LinearLayout.HORIZONTAL
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = Gravity.BOTTOM;
        ButtonLayout.layoutParams = layoutParams;

        // Créer les boutons pour bouger la paddle
        val buttonLayoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1.0f
        )

        LeftButton = Button(context)
        LeftButton.text = "<---";
        LeftButton.layoutParams = buttonLayoutParams
        LeftButton.width /= 2;
        LeftButton.gravity = Gravity.CENTER;
        LeftButton.setBackgroundColor(Color.TRANSPARENT)
        LeftButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    OnLeftButtonClick() // Fonction lorsqu'il est appuyé
                    true
                }
                MotionEvent.ACTION_UP -> {
                    OnLeftButtonReleased() // Fonction lorsqu'il est relâché
                    true
                }
                else -> false
            }
        }

        RightButton = Button(context)
        RightButton.text = "--->";
        RightButton.layoutParams = buttonLayoutParams;
        RightButton.width /= 2;
        RightButton.gravity = Gravity.CENTER;
        RightButton.setBackgroundColor(Color.TRANSPARENT)
        RightButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    OnRightButtonClick() // Fonction lorsqu'il est appuyé
                    true
                }
                MotionEvent.ACTION_UP -> {
                    OnRightButtonReleased() // Fonction lorsqu'il est relâché
                    true
                }
                else -> false
            }
        }

        ButtonLayout.addView(LeftButton)
        ButtonLayout.addView(RightButton)

        addView(ButtonLayout, childCount)
    }


    /* Espace laissé sur les côtés de la grid de briques en % de l'écran totale */
    private val BrickGridOffset:Vector2 = Vector2(0.05f, 0.05f);

    /* Espace laissé entre chaque briques en % de l'écran totale */
    private var BrickSpaceOffset:Vector2 = Vector2(0.005f, 0.005f);

    lateinit var BrickSize:Vector2;



    override fun onDraw(canvas: Canvas?)
    {
        super.onDraw(canvas)

        // Créer les game objects quand la size du layout est initialisé.
        if(!this::ScreenSize.isInitialized){
            ScreenSize = Vector2(width.toFloat(), height.toFloat());
            SetBricksGrid(6, 6);

            InitGameObjects();
        }

        UpdateDeltaTime();

        if(ShouldUpdate)
        {
            // Update gameObjects
            GameObjectList.forEach { it.Update(DeltaTime) }
        }

        ParticlesList.forEach {it.Update(DeltaTime)}

        // Draw gameObjects
        GameObjectList.forEach { it.Draw(canvas) }

        ParticlesList.forEach { it.Draw(canvas) }

        if(ShouldUpdate)
        {
            CheckCollisions();
        }


        // Supprimer les particles qui doivent l'être selon leur LifeTime
        val ParticlesToDel = mutableListOf<Particle>();
        ParticlesList.forEach {
            if(it.GetLifeTime() <= 0f)
            {
                ParticlesToDel.add(it);
            }
        }

        ParticlesToDel.forEach { ParticlesList.remove(it) }

        invalidate();
    }

    private fun UpdateDeltaTime()
    {
        val currentTime = SystemClock.elapsedRealtime()
        DeltaTime = (currentTime - LastFrameTime) / 1000f
        LastFrameTime = currentTime
    }

    fun InitGameObjects()
    {
        MyPaddle = Paddle(ScreenSize.X / 2, ScreenSize.Y * 0.85f, 350f, 150f, ScreenSize);
        GameObjectList.add(MyPaddle);

        MyBall = Ball(ScreenSize.X / 2, ScreenSize.Y * 0.73f, 75f, 75f, ScreenSize, 500f);
        MyBall.Parent = this;
        GameObjectList.add(MyBall);
    }

    fun SetBricksGrid(RowsCnt:Int, ColCnt:Int)
    {
        if(this::GridOfBrick.isInitialized) return;

        GridOfBrick = BrickGrid(RowsCnt, ColCnt);

        /* Calculer le width des briques */
        var BrickW:Float = CalculateBrickWidth();
        if(this::BrickSize.isInitialized == false)
        {
            BrickSize = Vector2(BrickW, 100f);
        }

        /* Créer les briques */
        for(i in 0 until GridOfBrick.Rows * GridOfBrick.Col)
        {
            val GridX = i % GridOfBrick.Rows;
            val GridY = i / GridOfBrick.Rows;

            val BrickX = GridX * BrickSize.X + BrickGridOffset.X * width + (GridX * (BrickSpaceOffset.X * width));
            val BrickY = GridY * BrickSize.Y + BrickGridOffset.Y * height + (GridY * (BrickSpaceOffset.Y * height));

            val NewBrick:GameObject = Brick(
                BrickX,
                BrickY,
                BrickSize.X,
                BrickSize.Y,
                ScreenSize
            );

            GridOfBrick.BricksList.add(NewBrick as Brick);
            GameObjectList.add(NewBrick);
        }
    }

    fun AddBrickRow()
    {
        // Augmenter la pos y de chaque brick existantes
        for(i in 0 until GridOfBrick.BricksList.size)
        {
            var CurrBrick = GridOfBrick.BricksList[i];
            var CurrY = CurrBrick.GetRect().top;
            var CurrX = CurrBrick.GetRect().left;

            CurrBrick.SetPos(Vector2(CurrX, CurrY + BrickSize.Y + (BrickSpaceOffset.Y * height)))
        }

        // Créer la nouvelle rangée de brick
        for(i in 0 until GridOfBrick.Col)
        {
            val GridX = i % GridOfBrick.Rows;
            val GridY = i / GridOfBrick.Rows;
            val BrickX = GridX * BrickSize.X + BrickGridOffset.X * width + (GridX * (BrickSpaceOffset.X * width));
            val BrickY = GridY * BrickSize.Y + BrickGridOffset.Y * height + (GridY * (BrickSpaceOffset.Y * height));

            val NewBrick:GameObject = Brick(
                BrickX,
                BrickY,
                BrickSize.X,
                BrickSize.Y,
                ScreenSize
            );

            GridOfBrick.BricksList.add(NewBrick as Brick);
            GameObjectList.add(NewBrick);
        }
    }

    fun CalculateBrickWidth():Float
    {
        var SizeToRemoveFromBrickWidthCalculation:Float = width * (BrickGridOffset.X * 2) + (BrickSpaceOffset.X * width * (GridOfBrick.Rows - 1))
        var WidthReservedToBricks:Float = width - SizeToRemoveFromBrickWidthCalculation;
        return WidthReservedToBricks / GridOfBrick.Rows;
    }

    fun CheckCollisions()
    {
        var CollidedBricks:List<Brick> = CollisionManager.GetCollidingBricks(MyBall, GridOfBrick.BricksList);

        val BallX:Float = MyBall.GetRect().left;
        val BallY:Float = MyBall.GetRect().top;
        val BallW:Float = MyBall.GetRect().width();
        val BallH = MyBall.GetRect().height();

        if(CollidedBricks.size > 0)
        {
            shake.start(); // Shaker l'écran lors d'une collision entre la balle et une brick

            val BrickTop:Float = CollidedBricks[0].GetRect().top;
            val BrickBot:Float = CollidedBricks[0].GetRect().top + CollidedBricks[0].GetRect().height();
            val BrickLeft:Float = CollidedBricks[0].GetRect().left;
            val BrickRight:Float = CollidedBricks[0].GetRect().left + CollidedBricks[0].GetRect().width()

            // La balle collisionne sur la gauche de la brique
            if(BallX < BrickLeft && BallY > BrickTop && BallY < BrickBot)
            {
                MyBall.ChangeDir(true, false);
                MyBall.SetPos(Vector2(BrickLeft - BallW, BallY));
            }
            // La balle collisionne sur la droite de la brique
            else if(BallX > BrickRight && BallY > BrickTop && BallY < BrickBot)
            {
                MyBall.ChangeDir(true, false);
                MyBall.SetPos(Vector2(BrickRight, BallY));
            }
            // La balle collisionne sur le dessus de la brique
            else if(MyBall.GetDir().Y == 1f)
            {
                MyBall.SetPos(Vector2(BallX, BrickTop - BallH));
                MyBall.ChangeDir(false, true);
            }
            else // La balle collisionne en dessous de la brique
            {
                MyBall.SetPos(Vector2(BallX, BrickBot));
                MyBall.ChangeDir(false, true);
            }
        }

        CollidedBricks.forEach { GameObjectList.remove(it); GridOfBrick.BricksList.remove(it); AddParticles(it); }

        if(CollisionManager.IsBallCollidesWithBaddle(MyBall, MyPaddle))
        {
            val PaddleTop = MyPaddle.GetRect().top;
            val PaddleBot = MyPaddle.GetRect().top + MyPaddle.GetRect().height();

            if(MyBall.GetDir().Y == 1f)
            {
                MyBall.SetPos(Vector2(BallX, PaddleTop - BallH));
            }
            else
            {
                MyBall.SetPos(Vector2(BallX, PaddleBot));
            }

            MyBall.ChangeDir(false, true);
        }
    }

    fun AddParticles(brick:Brick)
    {
        for(i in 0 until 50)
        {
            var ParticleVx = (Math.random() * 700).toFloat() - 5;
            var ParticleVy = (Math.random() * 700).toFloat() - 5;

            val ShouldNegateVx = (Math.random());
            val ShouldNegateVy = (Math.random());

            if(ShouldNegateVx > 0.5)
            {
                ParticleVx = -ParticleVx;
            }
            if(ShouldNegateVy > 0.5)
            {
                ParticleVy = -ParticleVy;
            }
            ParticlesList.add(Particle(brick.GetRect().centerX(), brick.GetRect().centerY(), ParticleVx, ParticleVy, 10f));
        }
    }

    fun OnLeftButtonClick():Boolean
    {
        InputManager.getInstance().SetLeftButtonClicked(true);
        return false;
    }

    fun OnLeftButtonReleased():Boolean
    {
        InputManager.getInstance().SetLeftButtonClicked(false);
        return false;
    }

    fun OnRightButtonClick():Boolean
    {
        InputManager.getInstance().SetRightButtonClicked(true);
        return false;
    }

    fun OnRightButtonReleased():Boolean
    {
        InputManager.getInstance().SetRightButtonClicked(false);
        return false;
    }

    fun ShowPopup(win:Boolean)
    {
        val Builder = AlertDialog.Builder(context)
        Builder.setMessage("Recommencer?")

        Builder.setPositiveButton("Recommencer") { dialog, which ->
            BreakoutParent.RestartBreakout();
        }

        Builder.setNegativeButton("Quitter") { dialog, which ->
            System.exit(0);
        }

        val dialog = Builder.create()
        dialog.show()
        if(!win)
        {
            Builder.setTitle("Vous avez perdu!")
        }
        else
        {
            Builder.setTitle("Vous avez gagné!")
        }
    }

    fun SetShouldUpdate(State:Boolean)
    {
        ShouldUpdate = State;
    }
}