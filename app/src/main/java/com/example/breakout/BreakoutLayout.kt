package com.example.breakout

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.SystemClock
import android.widget.LinearLayout

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
    private var LastFrameTime = SystemClock.elapsedRealtime();
    private var DeltaTime:Float = 0f;
    lateinit var ScreenSize:Vector2;
    lateinit var GridOfBrick:BrickGrid;

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
            SetBricksGrid(6, 6);

            InitGameObjects();
        }

        UpdateDeltaTime();

        // Update gameObjects
        GameObjectList.forEach { it.Update(DeltaTime) }

        // Draw gameObjects
        GameObjectList.forEach { it.Draw(canvas) }

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
        val MyPaddle:GameObject = Paddle(ScreenSize.X / 2, ScreenSize.Y * 0.85f, 350f, 150f, ScreenSize);
        GameObjectList.add(MyPaddle);
    }

    fun SetBricksGrid(RowsCnt:Int, ColCnt:Int)
    {
        if(this::GridOfBrick.isInitialized) return;

        GridOfBrick = BrickGrid(RowsCnt, ColCnt);

        /* Calculer le width des briques */
        var SizeToRemoveFromBrickWidthCalculation:Float = width * (BrickGridOffset.X * 2) + (BrickSpaceOffset.X * width * (GridOfBrick.Rows - 1))
        var WidthReservedToBricks:Float = width - SizeToRemoveFromBrickWidthCalculation;
        var BrickW:Float = WidthReservedToBricks / GridOfBrick.Rows;

        /* Calculer le height des briques */
        var BricksGridPercentHeight:Float = 0.25f;
        var HeightReservedToBricks:Float = (height * BricksGridPercentHeight) - BrickGridOffset.Y - (BrickSpaceOffset.Y * width * (GridOfBrick.Col - 1));
        var BrickH:Float = HeightReservedToBricks / GridOfBrick.Col;

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

    fun GetBrickIndex()
    {
    }
}