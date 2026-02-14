package com.example.animebrowser

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FavouritesFragment : Fragment() {

    private lateinit var rvFavouritesList: RecyclerView
    private lateinit var emptyStateContainer: LinearLayout
    private lateinit var tvEmptyMessage: TextView
    private lateinit var tvEmptySubMessage: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourites, container, false)

        rvFavouritesList = view.findViewById(R.id.rvFavouritesList)
        emptyStateContainer = view.findViewById(R.id.emptyStateContainer)
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage)
        tvEmptySubMessage = view.findViewById(R.id.tvEmptySubMessage)

        loadFavourites()

        return view
    }

    override fun onResume() {
        super.onResume()
        // Refresh when returning from detail page (user might have removed a favourite)
        loadFavourites()
    }

    private fun loadFavourites() {
        // Check if user is logged in
        if (!UserSession.isLoggedIn(requireContext())) {
            // Guest user - show empty state with login message
            rvFavouritesList.visibility = View.GONE
            emptyStateContainer.visibility = View.VISIBLE
            tvEmptyMessage.text = "Login to see your favourites"
            tvEmptySubMessage.text = "Create an account or login to save your favourite anime"
            return
        }

        // Logged in user - load their favourites
        val favouriteIds = FavoritesManager.getFavorites(requireContext())

        if (favouriteIds.isEmpty()) {
            // No favourites yet
            rvFavouritesList.visibility = View.GONE
            emptyStateContainer.visibility = View.VISIBLE
            tvEmptyMessage.text = "No favourites yet"
            tvEmptySubMessage.text = "Add anime to your favourites to see them here"
            return
        }

        // Get anime data from store
        val favouriteAnime = AnimeDataStore.getAnimeList(requireContext(), favouriteIds)

        if (favouriteAnime.isEmpty()) {
            // IDs exist but anime data not found in store
            rvFavouritesList.visibility = View.GONE
            emptyStateContainer.visibility = View.VISIBLE
            tvEmptyMessage.text = "No favourites yet"
            tvEmptySubMessage.text = "Add anime to your favourites to see them here"
            return
        }

        // Show the favourites list
        emptyStateContainer.visibility = View.GONE
        rvFavouritesList.visibility = View.VISIBLE

        rvFavouritesList.layoutManager = LinearLayoutManager(requireContext())
        rvFavouritesList.adapter = TrendingListAdapter(favouriteAnime) { anime ->
            openAnimeDetail(anime)
        }
    }

    private fun openAnimeDetail(anime: Anime) {
        val intent = Intent(requireContext(), AnimeDetailActivity::class.java).apply {
            putExtra("ANIME_ID", anime.id)
            putExtra("ANIME_TITLE", anime.title)
            putExtra("ANIME_RATING", anime.rating)
        }
        startActivity(intent)
    }
}
