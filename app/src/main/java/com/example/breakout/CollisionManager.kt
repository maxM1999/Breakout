package com.example.breakout

import android.graphics.Rect
import android.graphics.RectF

object CollisionManager
{
    fun GetCollidingBricks(ball: Ball, Bricks: List<Brick>): List<Brick>
    {
        val CollidingBricks = mutableListOf<Brick>()
        val BallRect = ball.GetRect();
        for (Brick in Bricks)
        {
            if (RectF.intersects(BallRect, Brick.GetRect()))
            {
                CollidingBricks.add(Brick)
            }
        }
        return CollidingBricks
    }

    fun IsBallCollidesWithBaddle(ball:Ball, paddle:Paddle) : Boolean
    {
        val BallRect = ball.GetRect();
        val PaddleRect = paddle.GetRect();

        if(RectF.intersects(BallRect, PaddleRect))
        {
            return true;
        }

        return false;
    }
}