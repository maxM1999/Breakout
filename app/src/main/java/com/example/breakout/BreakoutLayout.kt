package com.example.breakout

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.SystemClock
import android.transition.Explode
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import androidx.core.view.forEach

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
    lateinit var GridOfBrick:BrickGrid;
    lateinit var MyPaddle:Paddle;
    lateinit var MyBall:Ball;

    val shake = ObjectAnimator.ofFloat(this, "translationX", -10f, 10f);

    init
    {
        shake.duration = 50
        shake.repeatCount = 5
        shake.interpolator = LinearInterpolator()
    }


    /* Espace laissé sur les côtés de la grid de briques en % de l'écran totale */
    private val BrickGridOffset:Vector2 = Vector2(0.05f, 0.05f);

    /* Espace laissé entre chaque briques en % de l'écran totale */
    private var BrickSpaceOffset:Vector2 = Vector2(0.005f, 0.005f);

    override fun onDraw(canvas: Canvas?)
    {
        super.onDraw(canvas)

        // Créer les game objects quand la size du layout est initialisé.
        if(!this::ScreenSize.isInitialized){
            ScreenSize = Vector2(width.toFloat(), height.toFloat());
            SetBricksGrid(3, 3);

            InitGameObjects();
        }

        UpdateDeltaTime();

        // Update gameObjects
        GameObjectList.forEach { it.Update(DeltaTime) }

        ParticlesList.forEach {it.Update(DeltaTime)}

        // Draw gameObjects
        GameObjectList.forEach { it.Draw(canvas) }

        ParticlesList.forEach { it.Draw(canvas) }

        CheckCollisions();

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
        GameObjectList.add(MyBall);
    }

    fun SetBricksGrid(RowsCnt:Int, ColCnt:Int)
    {
        if(this::GridOfBrick.isInitialized) return;

        GridOfBrick = BrickGrid(RowsCnt, ColCnt);

        /* Calculer le width des briques */
        var BrickW:Float = CalculateBrickWidth();

        /* Calculer le height des briques */
        var BrickH:Float = CalculateBrickHeight();

        /* Créer les briques */
        for(i in 0 until GridOfBrick.Rows * GridOfBrick.Col)
        {
            val GridX = i % GridOfBrick.Rows;
            val GridY = i / GridOfBrick.Rows;

            val BrickX = GridX * BrickW + BrickGridOffset.X * width + (GridX * (BrickSpaceOffset.X * width));
            val BrickY = GridY * BrickH + BrickGridOffset.Y * height + (GridY * (BrickSpaceOffset.Y * height));

            val NewBrick:GameObject = Brick(
                BrickX,
                BrickY,
                BrickW,
                BrickH,
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

    fun CalculateBrickHeight():Float
    {
        var BricksGridPercentHeight:Float = 0.25f;
        var HeightReservedToBricks:Float = (height * BricksGridPercentHeight) - BrickGridOffset.Y - (BrickSpaceOffset.Y * width * (GridOfBrick.Col - 1));
        return HeightReservedToBricks / GridOfBrick.Col;
    }

    fun CheckCollisions()
    {
        var CollidedBricks:List<Brick> = CollisionManager.GetCollidingBricks(MyBall, GridOfBrick.BricksList);

        if(CollidedBricks.size > 0)
        {
            shake.start();
            val BrickTop:Float = CollidedBricks[0].GetRect().top;
            val BrickBot:Float = CollidedBricks[0].GetRect().top + CollidedBricks[0].GetRect().height();
            val BallX:Float = MyBall.GetRect().left;

            if(MyBall.GetDir().Y == 1f)
            {
                MyBall.SetPos(Vector2(BallX, BrickTop));
            }
            else
            {
                MyBall.SetPos(Vector2(BallX, BrickBot));
            }

            MyBall.ChangeDir(false, true);
        }


        CollidedBricks.forEach { GameObjectList.remove(it); GridOfBrick.BricksList.remove(it); AddParticles(it); }

        if(CollisionManager.IsBallCollidesWithBaddle(MyBall, MyPaddle))
        {
            val PaddleTop = MyPaddle.GetRect().top;
            val PaddleBot = MyPaddle.GetRect().top + MyPaddle.GetRect().height();
            val BallX:Float = MyBall.GetRect().left;
            val BallH:Float = MyBall.GetRect().height();

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
}