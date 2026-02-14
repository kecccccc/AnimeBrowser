package com.example.animebrowser

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
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
    private lateinit var btnWatched: MaterialButton
    private lateinit var btnWatchlist: MaterialButton
    private lateinit var llTrailerSection: LinearLayout
    private lateinit var ivTrailerThumbnail: ImageView
    private lateinit var cvTrailer: CardView
    private lateinit var tvNoTrailer: TextView

    private var isFavorite = false
    private var isWatched = false
    private var isInWatchlist = false
    private var animeId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anime_detail)

        // Force dark status bar
        window.statusBarColor = android.graphics.Color.parseColor("#121212")

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
        btnWatched = findViewById(R.id.btnWatched)
        btnWatchlist = findViewById(R.id.btnWatchlist)
        llTrailerSection = findViewById(R.id.llTrailerSection)
        ivTrailerThumbnail = findViewById(R.id.ivTrailerThumbnail)
        cvTrailer = findViewById(R.id.cvTrailer)
        tvNoTrailer = findViewById(R.id.tvNoTrailer)

        // Get data from intent
        animeId = intent.getIntExtra("ANIME_ID", -1)
        val animeTitle = intent.getStringExtra("ANIME_TITLE") ?: "Unknown"
        val animeRating = intent.getDoubleExtra("ANIME_RATING", 0.0)

        // Check if user is logged in
        if (!UserSession.isLoggedIn(this)) {
            btnFavorite.isEnabled = false
            btnFavorite.text = "Login to add favorites"
            btnWatched.isEnabled = false
            btnWatched.text = "Login to track anime"
            btnWatchlist.isEnabled = false
            btnWatchlist.text = "Login to add to watchlist"
        } else {
            isFavorite = FavoritesManager.isFavorite(this, animeId)
            isWatched = FavoritesManager.isWatched(this, animeId)
            isInWatchlist = FavoritesManager.isInWatchlist(this, animeId)
            updateFavoriteButton()
            updateWatchedButton()
            updateWatchlistButton()
        }

        // Set data to views
        tvAnimeTitle.text = animeTitle
        tvRating.text = "$animeRating/10"

        // Try to load from stored data first
        val storedAnime = AnimeDataStore.getAnime(this, animeId)
        if (storedAnime != null) {
            tvSummary.text = storedAnime.synopsis ?: "No summary available"
            tvType.text = storedAnime.type ?: "Unknown"
            tvEpisodes.text = storedAnime.episodes?.toString() ?: "?"
            tvTags.text = storedAnime.genres ?: "No tags"
        } else {
            tvSummary.text = "Loading..."
            tvType.text = "Loading..."
            tvEpisodes.text = "..."
            tvTags.text = "Loading..."
        }

        // Fetch full details from API
        if (animeId != -1) {
            RetrofitClient.apiService.getAnimeDetails(animeId).enqueue(object : retrofit2.Callback<JikanAnimeDetailResponse> {
                override fun onResponse(call: retrofit2.Call<JikanAnimeDetailResponse>, response: retrofit2.Response<JikanAnimeDetailResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val jikanAnime = response.body()!!.data
                        val anime = jikanAnime.toAnime()

                        tvSummary.text = anime.synopsis ?: "No summary available"
                        tvType.text = anime.type ?: "Unknown"
                        tvEpisodes.text = anime.episodes?.toString() ?: "?"
                        tvTags.text = anime.genres ?: "No tags"

                        val largeImageUrl = jikanAnime.images?.jpg?.large_image_url ?: anime.imageUrl
                        if (!largeImageUrl.isNullOrEmpty()) {
                            com.bumptech.glide.Glide.with(this@AnimeDetailActivity)
                                .load(largeImageUrl)
                                .centerCrop()
                                .placeholder(android.R.color.darker_gray)
                                .into(ivAnimePoster)
                        }

                        AnimeDataStore.saveAnime(this@AnimeDetailActivity, anime)

                        // Load trailer thumbnail
                        val youtubeId = jikanAnime.trailer?.youtube_id
                            ?: jikanAnime.trailer?.embed_url?.let { url ->
                                url.substringAfter("/embed/", "").substringBefore("?").ifEmpty { null }
                            }
                        if (!youtubeId.isNullOrEmpty()) {
                            llTrailerSection.visibility = View.VISIBLE
                            tvNoTrailer.visibility = View.GONE

                            // Load YouTube thumbnail
                            val thumbnailUrl = "https://img.youtube.com/vi/$youtubeId/hqdefault.jpg"
                            com.bumptech.glide.Glide.with(this@AnimeDetailActivity)
                                .load(thumbnailUrl)
                                .centerCrop()
                                .placeholder(android.R.color.darker_gray)
                                .into(ivTrailerThumbnail)

                            // Open YouTube on click
                            cvTrailer.setOnClickListener {
                                val youtubeIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$youtubeId"))
                                startActivity(youtubeIntent)
                            }
                        } else {
                            llTrailerSection.visibility = View.GONE
                            tvNoTrailer.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onFailure(call: retrofit2.Call<JikanAnimeDetailResponse>, t: Throwable) {
                    if (storedAnime == null) {
                        tvSummary.text = "Failed to load summary"
                        tvType.text = "Unknown"
                        tvEpisodes.text = "?"
                        tvTags.text = "Unknown"
                    }
                }
            })
        }

        // Load poster image from stored data
        if (storedAnime != null && !storedAnime.imageUrl.isNullOrEmpty()) {
            com.bumptech.glide.Glide.with(this)
                .load(storedAnime.imageUrl)
                .centerCrop()
                .placeholder(android.R.color.darker_gray)
                .into(ivAnimePoster)
        } else {
            ivAnimePoster.setBackgroundColor(
                android.graphics.Color.parseColor("#2A2A2A")
            )
        }

        // Back button
        ivBack.setOnClickListener {
            finish()
        }

        // Favorite button
        btnFavorite.setOnClickListener {
            if (UserSession.isLoggedIn(this)) {
                toggleFavorite()
            }
        }

        // Watched button
        btnWatched.setOnClickListener {
            if (UserSession.isLoggedIn(this)) {
                toggleWatched()
            }
        }

        // Watchlist button
        btnWatchlist.setOnClickListener {
            if (UserSession.isLoggedIn(this)) {
                toggleWatchlist()
            }
        }
    }

    private fun toggleFavorite() {
        isFavorite = !isFavorite
        if (isFavorite) {
            FavoritesManager.addFavorite(this, animeId)
            Toast.makeText(this, "Added to favorites!", Toast.LENGTH_SHORT).show()
        } else {
            FavoritesManager.removeFavorite(this, animeId)
            Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show()
        }
        updateFavoriteButton()
    }

    private fun updateFavoriteButton() {
        if (isFavorite) {
            btnFavorite.text = "Remove from favorites"
            btnFavorite.setIconResource(android.R.drawable.btn_star_big_on)
        } else {
            btnFavorite.text = "Add to favorites"
            btnFavorite.setIconResource(android.R.drawable.btn_star_big_off)
        }
    }

    private fun toggleWatched() {
        isWatched = !isWatched
        if (isWatched) {
            FavoritesManager.markAsWatched(this, animeId)
            Toast.makeText(this, "Marked as watched!", Toast.LENGTH_SHORT).show()
        } else {
            FavoritesManager.markAsNotWatched(this, animeId)
            Toast.makeText(this, "Marked as not watched", Toast.LENGTH_SHORT).show()
        }
        updateWatchedButton()
    }

    private fun updateWatchedButton() {
        if (isWatched) {
            btnWatched.text = "Watched ✓"
            btnWatched.setIconResource(android.R.drawable.checkbox_on_background)
            btnWatched.setStrokeColorResource(android.R.color.transparent)
            btnWatched.backgroundTintList = android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor("#4CAF50")
            )
        } else {
            btnWatched.text = "Mark as Watched"
            btnWatched.setIconResource(android.R.drawable.checkbox_off_background)
            btnWatched.strokeColor = android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor("#4CAF50")
            )
            btnWatched.backgroundTintList = android.content.res.ColorStateList.valueOf(
                android.graphics.Color.TRANSPARENT
            )
        }
    }

    private fun toggleWatchlist() {
        isInWatchlist = !isInWatchlist
        if (isInWatchlist) {
            FavoritesManager.addToWatchlist(this, animeId)
            Toast.makeText(this, "Added to watchlist!", Toast.LENGTH_SHORT).show()
        } else {
            FavoritesManager.removeFromWatchlist(this, animeId)
            Toast.makeText(this, "Removed from watchlist", Toast.LENGTH_SHORT).show()
        }
        updateWatchlistButton()
    }

    private fun updateWatchlistButton() {
        if (isInWatchlist) {
            btnWatchlist.text = "In Watchlist ✓"
            btnWatchlist.setIconResource(android.R.drawable.ic_menu_agenda)
            btnWatchlist.setStrokeColorResource(android.R.color.transparent)
            btnWatchlist.backgroundTintList = android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor("#2196F3")
            )
        } else {
            btnWatchlist.text = "Add to Watchlist"
            btnWatchlist.setIconResource(android.R.drawable.ic_menu_add)
            btnWatchlist.strokeColor = android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor("#2196F3")
            )
            btnWatchlist.backgroundTintList = android.content.res.ColorStateList.valueOf(
                android.graphics.Color.TRANSPARENT
            )
        }
    }
}