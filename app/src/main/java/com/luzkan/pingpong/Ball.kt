package com.luzkan.pingpong

import android.graphics.RectF
import java.util.Random

class Ball(screenX: Int, screenY: Int) {

    // Give access to the Rect
    val rect: RectF
    private var mXVelocity: Float = 0.toFloat()
    private var mYVelocity: Float = 0.toFloat()
    private val mBallWidth: Float
    private val mBallHeight: Float
    var itsBuggedSoItsWorkaroundSorry = screenX

    init {
        // Make the mBall size relative to the screen resolution
        mBallWidth = (screenX / 80).toFloat()
        mBallHeight = mBallWidth

        // Start the ball travelling straight up
        // at a quarter of the screen height per second
        mYVelocity = (screenY / 4).toFloat()
        mXVelocity = mYVelocity

        // Initialize the Rect that represents the mBall
        rect = RectF()
    }

    // Change the position each frame
    fun update(fps: Long) {
        rect.left = rect.left + mXVelocity / fps
        rect.top = rect.top + mYVelocity / fps
        rect.right = rect.left + mBallWidth
        rect.bottom = rect.top - mBallHeight
    }

    // Reverse the vertical heading
    fun reverseYVelocity() {
        mYVelocity = -mYVelocity
    }

    // Reverse the horizontal heading
    fun reverseXVelocity() {
        mXVelocity = -mXVelocity
    }

    fun setRandomXVelocity() {
        val generator = Random()
        val answer = generator.nextInt(2)

        if (answer == 0) {
            reverseXVelocity()
        }
    }

    // Speed up by 10%
    fun increaseVelocity() {
        mXVelocity += mXVelocity / 10
        mYVelocity += mYVelocity / 10
    }

    fun resetVelocity(){
        mYVelocity = (itsBuggedSoItsWorkaroundSorry / 4).toFloat()
        mXVelocity = mYVelocity
    }

    fun clearObstacleY(y: Float) {
        rect.bottom = y
        rect.top = y - mBallHeight
    }

    fun clearObstacleX(x: Float) {
        rect.left = x
        rect.right = x + mBallWidth
    }

    fun reset(x: Int, y: Int) {
        rect.left = (x / 2).toFloat()
        rect.top = (y / 2).toFloat()
        rect.right = (x / 2) + mBallWidth
        rect.bottom = (y.toFloat() / 2) - mBallHeight
    }
}