package com.example.animebrowser

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MyAnimeListActivity : AppCompatActivity() {

    private lateinit var ivBack: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var rvAnimeList: RecyclerView
    private lateinit var llEmptyState: LinearLayout
    private lateinit var tvEmptyMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_anime_list)

        // Initialize views
        ivBack = findViewById(R.id.ivBack)
        tvTitle = findViewById(R.id.tvTitle)
        rvAnimeList = findViewById(R.id.rvAnimeList)
        llEmptyState = findViewById(R.id.llEmptyState)
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage)

        // Get list type from intent
        val listType = intent.getStringExtra("LIST_TYPE") ?: "favorites"

        // Setup UI based on list type
        setupUI(listType)

        // Back button
        ivBack.setOnClickListener {
            finish()
        }
    }

    private fun setupUI(listType: String) {
        when (listType) {
            "favorites" -> {
                tvTitle.text = "My Favorites"
                tvEmptyMessage.text = "No favorites yet"
                loadFavorites()
            }
            "watchlist" -> {
                tvTitle.text = "My Watchlist"
                tvEmptyMessage.text = "Your watchlist is empty"
                loadWatchlist()
            }
            "watched" -> {
                tvTitle.text = "My Watched"
                tvEmptyMessage.text = "You haven't marked any anime as watched"
                loadWatched()
            }
        }
    }

    private fun loadFavorites() {
        val favoriteIds = FavoritesManager.getFavorites(this)
        displayAnimeList(favoriteIds)
    }

    private fun loadWatchlist() {
        val watchlistIds = FavoritesManager.getWatchlist(this)
        displayAnimeList(watchlistIds)
    }

    private fun loadWatched() {
        val watchedIds = FavoritesManager.getWatched(this)
        displayAnimeList(watchedIds)
    }

    private fun displayAnimeList(animeIds: Set<Int>) {
        if (animeIds.isEmpty()) {
            // Show empty state
            rvAnimeList.visibility = View.GONE
            llEmptyState.visibility = View.VISIBLE
        } else {
            // Hide empty state and show list
            llEmptyState.visibility = View.GONE
            rvAnimeList.visibility = View.VISIBLE

            // Get real anime data from store
            val animeList = AnimeDataStore.getAnimeList(this, animeIds)

            rvAnimeList.layoutManager = LinearLayoutManager(this)
            rvAnimeList.adapter = TrendingListAdapter(animeList) { anime ->
                openAnimeDetail(anime)
            }
        }
    }

    private fun openAnimeDetail(anime: Anime) {
        val intent = Intent(this, AnimeDetailActivity::class.java).apply {
            putExtra("ANIME_ID", anime.id)
            putExtra("ANIME_TITLE", anime.title)
            putExtra("ANIME_RATING", anime.rating)
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        // Refresh the list when returning from detail page
        val listType = intent.getStringExtra("LIST_TYPE") ?: "favorites"
        setupUI(listType)
    }
}