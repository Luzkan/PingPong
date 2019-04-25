package com.luzkan.pingpong

import android.graphics.RectF

class Bar (private val screenSizeX: Int, private val screenSizeY: Int, onTop: Boolean) {
    
    // Rect is object that holds coordinates; it's a getter method
    val rect: RectF
    
    private val barLength: Float = (screenSizeX / 6).toFloat()
    private val barHeight: Float = (screenSizeY / 50).toFloat()

    // X is the far left of the rectangle which forms our bar and Y is the top coordinate
    private var barLeftX: Float = ((screenSizeX / 2) - (barLength/2))
    private val barTopY: Float = (screenSizeY - 20).toFloat()

    // How fast is the bar in pixels per second
    // Gets from one edge to the other in 0,5s
    private val mBatSpeed: Float = screenSizeX.toFloat()*2

    // Which ways can the mBat move
    val STOPPED = 0
    val LEFT = 1
    val RIGHT = 2

    // Is the mBat moving and in which direction
    private var mBatMoving = STOPPED

    init {
        rect = if(onTop) {
            RectF(barLeftX, 35f, barLeftX + barLength, 35f + barHeight)
        }else{
            RectF(barLeftX, barTopY - 55f, barLeftX + barLength, barTopY + barHeight - 55f)
        }
    }

    // This method will be used to change/set if the mBat is going left, right or nowhere
    fun setMovementState(state: Int) {
        mBatMoving = state
    }

    // This update method will be called from update in GameView
    // It determines if the Bat needs to move and changes the coordinates
    // contained in mRect if necessary
    fun update(fps: Long) {
        if (mBatMoving == LEFT) {
            barLeftX = barLeftX - mBatSpeed / fps
        }

        if (mBatMoving == RIGHT) {
            barLeftX = barLeftX + mBatSpeed / fps
        }


        // Make sure it's not leaving screen
        if (rect.left < 0) {
            barLeftX = 0f
        }

        if (rect.right > screenSizeX) {
            barLeftX = screenSizeX -
                    // The width of the Bat
                    (rect.right - rect.left)
        }

        // Update the Bat graphics
        rect.left = barLeftX
        rect.right = barLeftX + barLength
    }
}
