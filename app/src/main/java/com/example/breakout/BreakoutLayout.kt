package com.example.breakout

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.SystemClock
import android.widget.LinearLayout

class Grid(var Rows:Int, var Col:Int)
{
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
    private var LastFrameTime = SystemClock.elapsedRealtime()
    private var DeltaTime:Float = 0f;
    lateinit var ScreenSize:Vector2;
    lateinit var BrickGrid:Grid;

    /* Espace laissé sur les côtés de la grid de briques */
    private val BrickGridOffset:Vector2 = Vector2(0.05f, 0.05f);

    override fun onDraw(canvas: Canvas?)
    {
        super.onDraw(canvas)

        // Créer les game objects quand la size du layout est initialisé.
        if(!this::ScreenSize.isInitialized){
            ScreenSize = Vector2(width.toFloat(), height.toFloat());
            SetBricksGrid(4, 4);

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
        if(this::BrickGrid.isInitialized) return;

        BrickGrid = Grid(RowsCnt, ColCnt);

        var SizeToRemoveFromBrickWidthCalculation:Float = width * (BrickGridOffset.X * 2)
        var WidthReservedToBricks:Float = width - SizeToRemoveFromBrickWidthCalculation;
        var BrickW:Float = WidthReservedToBricks / BrickGrid.Rows;

        var BricksGridPercentHeight:Float = 0.25f;
        var HeightReservedToBricks:Float = height * BricksGridPercentHeight;
        var BrickH:Float = HeightReservedToBricks / BrickGrid.Col;

        for(i in 0 .. BrickGrid.Rows * BrickGrid.Col)
        {
            val GridX = i % BrickGrid.Rows;
            val GridY = i / BrickGrid.Rows;

            val NewBrick:GameObject = Brick(
                GridX * BrickW + BrickGridOffset.X * width,
                GridY * BrickH + BrickGridOffset.Y * height,
                BrickW,
                BrickH,
                ScreenSize
            );

            GameObjectList.add(NewBrick);
        }
    }
}