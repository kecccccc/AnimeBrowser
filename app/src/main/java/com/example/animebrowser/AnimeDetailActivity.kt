package com.example.animebrowser

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class AnimeDetailActivity : AppCompatActivity() {

    private lateinit var ivBack: ImageView
    private lateinit var ivAnimePoster: ImageView
    private lateinit var tvAnimeTitle: TextView
    private lateinit var tvRating: TextView
    private lateinit var tvSummary: TextView
    private lateinit var tvType: TextView
    private lateinit var tvEpisodes: TextView
    private lateinit var tvTags: TextView
    private lateinit var btnFavorite: MaterialButton

    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anime_detail)

        // Initialize views
        ivBack = findViewById(R.id.ivBack)
        ivAnimePoster = findViewById(R.id.ivAnimePoster)
        tvAnimeTitle = findViewById(R.id.tvAnimeTitle)
        tvRating = findViewById(R.id.tvRating)
        tvSummary = findViewById(R.id.tvSummary)
        tvType = findViewById(R.id.tvType)
        tvEpisodes = findViewById(R.id.tvEpisodes)
        tvTags = findViewById(R.id.tvTags)
        btnFavorite = findViewById(R.id.btnFavorite)

        // Get data from intent
        val animeId = intent.getIntExtra("ANIME_ID", -1)
        val animeTitle = intent.getStringExtra("ANIME_TITLE") ?: "Unknown"
        val animeRating = intent.getDoubleExtra("ANIME_RATING", 0.0)

        // Set data to views
        tvAnimeTitle.text = animeTitle
        tvRating.text = "$animeRating/10"

        // За сад користимо dummy податке, касније ћеш повући из API-ја
        tvSummary.text = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."
        tvType.text = "TV Series"
        tvEpisodes.text = "24"
        tvTags.text = "Action, Drama, Fantasy"

        // Set placeholder за poster
        ivAnimePoster.setBackgroundColor(
            android.graphics.Color.parseColor("#2A2A2A")
        )

        // Back button
        ivBack.setOnClickListener {
            finish()
        }

        // Favorite button
        btnFavorite.setOnClickListener {
            toggleFavorite()
        }
    }

    private fun toggleFavorite() {
        isFavorite = !isFavorite

        if (isFavorite) {
            btnFavorite.text = "Remove from favorites"
            btnFavorite.setIconResource(android.R.drawable.btn_star_big_on)
            Toast.makeText(this, "Added to favorites!", Toast.LENGTH_SHORT).show()
        } else {
            btnFavorite.text = "Add to favorites"
            btnFavorite.setIconResource(android.R.drawable.btn_star_big_off)
            Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show()
        }
    }
}