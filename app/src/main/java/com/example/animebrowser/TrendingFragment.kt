package com.example.animebrowser

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TrendingFragment : Fragment() {

    private lateinit var rvTrendingList: RecyclerView
    private lateinit var ivSearch: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trending, container, false)

        // Initialize views
        rvTrendingList = view.findViewById(R.id.rvTrendingList)
        ivSearch = view.findViewById(R.id.ivSearch)

        // Setup RecyclerView
        setupTrendingList()

        // Search click listener
        ivSearch.setOnClickListener {
            startActivity(android.content.Intent(requireContext(), SearchActivity::class.java))
        }

        return view
    }

    private fun setupTrendingList() {
        // Fetch current season anime from API
        RetrofitClient.apiService.getCurrentSeasonAnime(page = 1, limit = 25).enqueue(object : retrofit2.Callback<JikanSeasonAnimeResponse> {
            override fun onResponse(call: retrofit2.Call<JikanSeasonAnimeResponse>, response: retrofit2.Response<JikanSeasonAnimeResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val animeList = response.body()!!.data.map { it.toAnime() }

                    // Save anime data
                    animeList.forEach { anime ->
                        AnimeDataStore.saveAnime(requireContext(), anime)
                    }

                    rvTrendingList.layoutManager = LinearLayoutManager(requireContext())
                    rvTrendingList.adapter = TrendingListAdapter(animeList) { anime ->
                        openAnimeDetail(anime)
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to load trending anime", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<JikanSeasonAnimeResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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