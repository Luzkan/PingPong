package com.luzkan.pingpong

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Window
import android.view.WindowManager

class MainActivity : Activity() {

    private lateinit var pongView: PongView

    var topScoreRetrieved = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get a Display object to access screen details
        val display = windowManager.defaultDisplay
        window.requestFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        // Load the resolution into a Point object
        val size = Point()
        display.getSize(size)

        val intro = MediaPlayer.create (this, R.raw.applaunch)
        intro.start()

        // Initialize pongView and set it as the view
        pongView = PongView(this, size.x, size.y)
        pongView.topScore = topScoreRetrieved
        setContentView(pongView)
    }

    override fun onResume() {
        super.onResume()

        // Retrieving topscore
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        topScoreRetrieved = sharedPref.getInt("top", 0)
        pongView.topScore = topScoreRetrieved

        // Tell the pongView resume method to execute
        pongView.resume()
    }

    override fun onPause() {
        super.onPause()

        // Saving TopScore
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        if(pongView.topScore <= pongView.score) {
            if (topScoreRetrieved < pongView.score) editor.putInt("top", pongView.score)
        }else {
            if (topScoreRetrieved < pongView.topScore) editor.putInt("top", pongView.topScore)
        }
        editor.apply()

        // Tell the pongView pause method to execute
        pongView.pause()
    }
}
