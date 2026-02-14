package com.example.animebrowser

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TopRatedFragment : Fragment() {

    private lateinit var rvTopRatedGrid: RecyclerView
    private lateinit var ivSearch: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_top_rated, container, false)

        // Initialize views
        rvTopRatedGrid = view.findViewById(R.id.rvTopRatedGrid)
        ivSearch = view.findViewById(R.id.ivSearch)

        // Setup Grid
        setupTopRatedGrid()

        // Search click listener
        ivSearch.setOnClickListener {
            startActivity(android.content.Intent(requireContext(), SearchActivity::class.java))
        }

        return view
    }

    private fun setupTopRatedGrid() {
        // Fetch top anime from API
        RetrofitClient.apiService.getTopAnime(page = 1, limit = 25).enqueue(object : retrofit2.Callback<JikanTopAnimeResponse> {
            override fun onResponse(call: retrofit2.Call<JikanTopAnimeResponse>, response: retrofit2.Response<JikanTopAnimeResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val animeList = response.body()!!.data.map { it.toAnime() }

                    // Save anime data
                    animeList.forEach { anime ->
                        AnimeDataStore.saveAnime(requireContext(), anime)
                    }

                    // Grid with 3 columns
                    rvTopRatedGrid.layoutManager = GridLayoutManager(requireContext(), 3)
                    rvTopRatedGrid.adapter = TopRatedGridAdapter(animeList) { anime ->
                        openAnimeDetail(anime)
                    }
                } else {
                    // Response unsuccessful, silently ignore
                }
            }

            override fun onFailure(call: retrofit2.Call<JikanTopAnimeResponse>, t: Throwable) {
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