package com.example.animebrowser

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class SplashActivity : AppCompatActivity() {

    private lateinit var characterCard: CardView
    private lateinit var tvAnimeBrowser: TextView
    private lateinit var underline: View
    private lateinit var dot1: View
    private lateinit var dot2: View
    private lateinit var dot3: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Initialize views
        characterCard = findViewById(R.id.characterCard)
        tvAnimeBrowser = findViewById(R.id.tvAnimeBrowser)
        underline = findViewById(R.id.underline)
        dot1 = findViewById(R.id.dot1)
        dot2 = findViewById(R.id.dot2)
        dot3 = findViewById(R.id.dot3)

        // Set everything invisible first
        characterCard.visibility = View.INVISIBLE
        tvAnimeBrowser.visibility = View.INVISIBLE
        underline.visibility = View.INVISIBLE
        dot1.visibility = View.INVISIBLE
        dot2.visibility = View.INVISIBLE
        dot3.visibility = View.INVISIBLE

        // Start animations after a short delay
        Handler(Looper.getMainLooper()).postDelayed({
            startAnimations()
        }, 200)

        // Navigate to appropriate screen after 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            // Check if user is logged in or in guest mode
            if (UserSession.isLoggedIn(this) || UserSession.isGuest(this)) {
                // User is logged in or continuing as guest, go to main
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // User needs to login, go to login screen
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 3000)
    }

    private fun startAnimations() {
        // Show and animate character card
        characterCard.visibility = View.VISIBLE
        characterCard.alpha = 0f
        characterCard.scaleX = 0.3f
        characterCard.scaleY = 0.3f

        characterCard.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(1000)
            .start()

        // Show and animate text after 500ms
        Handler(Looper.getMainLooper()).postDelayed({
            tvAnimeBrowser.visibility = View.VISIBLE
            tvAnimeBrowser.alpha = 0f
            tvAnimeBrowser.translationY = 100f

            tvAnimeBrowser.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(800)
                .start()
        }, 500)

        // Show and animate underline after 800ms
        Handler(Looper.getMainLooper()).postDelayed({
            underline.visibility = View.VISIBLE
            underline.scaleX = 0f

            underline.animate()
                .scaleX(1f)
                .setDuration(600)
                .start()
        }, 800)

        // Show and animate dots after 1200ms
        Handler(Looper.getMainLooper()).postDelayed({
            animateDot(dot1, 0)
            animateDot(dot2, 150)
            animateDot(dot3, 300)
        }, 1200)

        // Continuous rotation for the card
        Handler(Looper.getMainLooper()).postDelayed({
            characterCard.animate()
                .rotationBy(360f)
                .setDuration(3000)
                .start()
        }, 1000)
    }

    private fun animateDot(dot: View, delay: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            dot.visibility = View.VISIBLE

            // Create repeating bounce animation
            val runnable = object : Runnable {
                override fun run() {
                    dot.animate()
                        .translationY(-50f)
                        .scaleX(1.2f)
                        .scaleY(1.2f)
                        .setDuration(400)
                        .withEndAction {
                            dot.animate()
                                .translationY(0f)
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(400)
                                .withEndAction {
                                    Handler(Looper.getMainLooper()).postDelayed(this, 200)
                                }
                                .start()
                        }
                        .start()
                }
            }
            runnable.run()
        }, delay)
    }
}