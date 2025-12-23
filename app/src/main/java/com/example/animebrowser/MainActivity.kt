package com.example.animebrowser

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        // DEFAULT TAB
        bottomNav.selectedItemId = R.id.nav_home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    showHome()
                    true
                }
                R.id.nav_trending -> {
                    showTrending()
                    true
                }
                R.id.nav_top_rated -> {
                    showTopRated()
                    true
                }
                R.id.nav_favourites -> {
                    showFavourites()
                    true
                }
                R.id.nav_account -> {
                    showAccount()
                    true
                }
                else -> false
            }
        }
    }

    private fun showHome() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, HomeFragment())  // НЕ SimpleTextFragment!
            .commit()
    }

    private fun showTrending() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, SimpleTextFragment("Trending"))
            .commit()
    }

    private fun showTopRated() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, SimpleTextFragment("Top Rated"))
            .commit()
    }

    private fun showFavourites() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, SimpleTextFragment("Favourites"))
            .commit()
    }

    private fun showAccount() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, SimpleTextFragment("Account"))
            .commit()
    }
}
