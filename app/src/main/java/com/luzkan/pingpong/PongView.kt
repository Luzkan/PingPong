package com.luzkan.pingpong

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.media.MediaPlayer
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

@SuppressLint("ViewConstructor")
class PongView (context: Context, var mScreenX: Int, var mScreenY: Int) : SurfaceView(context), Runnable {


    // This is our thread
    var mGameThread: Thread? = null

    private var mOurHolder: SurfaceHolder = holder

    // A boolean which we will set and unset when the game is running- or not
    @Volatile
    var mPlaying: Boolean = false
    private var mPaused = true

    // This variable tracks the game frame rate
    var mFPS: Long = 0

    // Create Upper & Lower Bar
    var topBar: Bar = Bar(mScreenX, mScreenY, true)
    var bottomBar: Bar = Bar(mScreenX, mScreenY, false)

    // Create a pongBall
    var pongBall: Ball = Ball(mScreenX, mScreenY)

    // Stats
    var score = 0
    var topScore = 0
    var topLives = 3
    var botLives = 3

    // Sounds
    private var hitlife: MediaPlayer = MediaPlayer.create(context, R.raw.hitlife)
    private var hitbar: MediaPlayer = MediaPlayer.create(context, R.raw.hitbar)
    private var hitwall: MediaPlayer = MediaPlayer.create(context, R.raw.hitwall)

    private fun restart() {
        // Put the pongBall back to the start
        pongBall.reset(mScreenX, mScreenY)

        // if game over reset scores and mLives
        if (topLives == 0 || botLives == 0) {
            if(score > topScore) topScore = score
            score = 0
            topLives = 3
            botLives = 3
            pongBall.resetVelocity()
        }
    }

    override fun run() {
        while (mPlaying) {

            // Capture the current time in milliseconds in startFrameTime
            val startFrameTime = System.currentTimeMillis()

            // Update the frame
            if (!mPaused) update()

            // Draw the frame
            draw()

            // Calculate FPS
            val timeThisFrame = System.currentTimeMillis() - startFrameTime
            if (timeThisFrame >= 1) {
                mFPS = 1000 / timeThisFrame
            }
        }
    }

    // Everything that needs to be updated goes in here
    // Movement, collision detection etc.
    fun update() {

        // Move the mBat if required
        bottomBar.update(mFPS)
        topBar.update(mFPS)
        pongBall.update(mFPS)

        // PongBall hits bot bar
        if (RectF.intersects(bottomBar.rect, pongBall.rect)) {
            pongBall.setRandomXVelocity()
            pongBall.reverseYVelocity()
            pongBall.clearObstacleY(bottomBar.rect.top - 2)

            score++
            pongBall.increaseVelocity()
            hitbar.start()
        }


        // PongBall hits top bar
        if (RectF.intersects(topBar.rect, pongBall.rect)) {
            pongBall.setRandomXVelocity()
            pongBall.reverseYVelocity()
            pongBall.clearObstacleY(topBar.rect.bottom + 8f)

            score++
            pongBall.increaseVelocity()
            hitbar.start()
        }


        // PongBall hits bottom wall
        if (pongBall.rect.bottom > mScreenY) {
            pongBall.reverseYVelocity()
            pongBall.clearObstacleY((mScreenY - 2).toFloat())

            // Lose a life
            botLives--
            if (botLives == 0) {
                mPaused = true
                // TODO: Do smth when bottom player lose; atm does nothing instead and that's alright too.
            }

            pongBall.setRandomXVelocity()
            restart()
            hitlife.start()
        }
                
        // PongBall hits top wall
        if (pongBall.rect.top < 0) {
            pongBall.reverseYVelocity()
            pongBall.clearObstacleY(12f)

            // Lose a life
            topLives--
            if (topLives == 0) {
                mPaused = true
                // TODO: Do smth when top player lose; atm does nothing instead and that's alright too.
            }

            pongBall.setRandomXVelocity()
            restart()
            hitlife.start()
        }

        // PongBall hits left wall
        if (pongBall.rect.left < 0) {
            pongBall.reverseXVelocity()
            pongBall.clearObstacleX(2f)
            hitwall.start()
        }

        // PongBall hits right wall
        if (pongBall.rect.right > mScreenX) {
            pongBall.reverseXVelocity()
            pongBall.clearObstacleX((mScreenX - 22).toFloat())
            hitwall.start()
        }
    }

    // Draw the newly updated scene
    private fun draw() {

        if (mOurHolder.surface.isValid) {
            val canvas = mOurHolder.lockCanvas()
            val styleMovingObjects = Paint()

            // Background color
            canvas.drawColor(Color.argb(255, 108,91,123))

            // Bars Color
            styleMovingObjects.color = Color.argb(255, 248,177,149)
            canvas.drawRect(bottomBar.rect, styleMovingObjects)
            canvas.drawRect(topBar.rect, styleMovingObjects)
            canvas.drawOval(pongBall.rect, styleMovingObjects)

            // Score Style
            val styleScore = Paint()
            styleScore.setARGB(135,248,177,149)
            styleScore.textSize = 200f

            val styleTopScore = Paint()
            styleTopScore.setARGB(135,248,177,149)
            styleTopScore.textSize = 40f

            val styleLives = Paint()
            styleLives.setARGB(200,248,177,149)
            styleLives.textSize = 40f

            // Draw the mScore
            if(score < 10) canvas.drawText("$score", mScreenX/2f-53f, mScreenY/2f, styleScore) else canvas.drawText("$score", mScreenX/2f-105f, mScreenY/2f, styleScore)
            canvas.drawText("Top Score: $topScore", mScreenX/2f-115f, mScreenY/2f+45f, styleTopScore)
            canvas.drawText("Lives: $topLives", mScreenX/50f, (mScreenY/35f), styleLives)
            canvas.drawText("Lives: $botLives", mScreenX-(mScreenX/40f)-140f, mScreenY-(mScreenY/50f), styleLives)

            // Draw everything to the screen
            mOurHolder.unlockCanvasAndPost(canvas)
        }
    }

    // If the Activity is paused/stopped - shutdown our thread.
    fun pause() {
        mPlaying = false
        try {
            mGameThread!!.join()
        } catch (e: InterruptedException) {
            Log.e("Error:", "joining thread")
        }
    }

    // If the Activity starts/restarts - start our thread.
    fun resume() {
        mPlaying = true
        mGameThread = Thread(this)
        mGameThread!!.start()
    }

    // TODO: (Bug) Fix when more than one finger touches screen - first finger to hold moves both bars
    override fun onTouchEvent(motEvent: MotionEvent): Boolean {

        mPaused = false
        for(i in 0 until motEvent.pointerCount){
            if (motEvent.getY(i) > height/2){
                if (motEvent.x > mScreenX / 2) {
                    bottomBar.setMovementState(bottomBar.RIGHT)
                } else {
                    bottomBar.setMovementState(bottomBar.LEFT)
                }

                when (motEvent.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_UP -> {
                        bottomBar.setMovementState(bottomBar.STOPPED)
                    }
                }
            }else{
                if (motEvent.x > mScreenX / 2) {
                    topBar.setMovementState(topBar.RIGHT)
                } else {
                    topBar.setMovementState(topBar.LEFT)
                }

                when (motEvent.action and MotionEvent.ACTION_MASK) {
                    // Player has removed finger from screen
                    MotionEvent.ACTION_UP -> {
                        topBar.setMovementState(topBar.STOPPED)
                    }
                }
            }
        }
        return true
    }
}
