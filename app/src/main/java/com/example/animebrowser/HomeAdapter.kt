package com.example.animebrowser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment() {

    private lateinit var rvTrending: RecyclerView
    private lateinit var rvTopRated: RecyclerView
    private lateinit var ivSearch: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize views
        rvTrending = view.findViewById(R.id.rvTrending)
        rvTopRated = view.findViewById(R.id.rvTopRated)
        ivSearch = view.findViewById(R.id.ivSearch)

        // Setup RecyclerViews
        setupTrending()
        setupTopRated()

        // Search click listener
        ivSearch.setOnClickListener {
            Toast.makeText(requireContext(), "Search clicked!", Toast.LENGTH_SHORT).show()
            // Овде ћеш касније отворити SearchActivity
        }

        return view
    }

    private fun setupTrending() {
        // Dummy data - касније ћеш повући из API-ја
        val trendingList = listOf(
            Anime(1, "Attack on Titan", 9.1),
            Anime(2, "One Piece", 9.1),
            Anime(3, "Bleach", 9.1),
            Anime(4, "Naruto", 8.9),
            Anime(5, "My Hero Academia", 8.7)
        )

        rvTrending.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )

        rvTrending.adapter = AnimeAdapter(trendingList) { anime ->
            Toast.makeText(requireContext(), "Clicked: ${anime.title}", Toast.LENGTH_SHORT).show()
            // Овде ћеш касније отворити DetailActivity
        }
    }

    private fun setupTopRated() {
        // Dummy data
        val topRatedList = listOf(
            Anime(6, "Solo Leveling", 9.1),
            Anime(7, "Demon Slayer", 9.1),
            Anime(8, "Jujutsu Kaisen", 8.9),
            Anime(9, "Chainsaw Man", 8.8)
        )

        rvTopRated.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )

        rvTopRated.adapter = AnimeAdapter(topRatedList) { anime ->
            Toast.makeText(requireContext(), "Clicked: ${anime.title}", Toast.LENGTH_SHORT).show()
        }
    }
}