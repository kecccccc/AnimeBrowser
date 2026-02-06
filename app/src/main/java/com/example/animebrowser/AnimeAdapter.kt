package com.example.animebrowser

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AnimeAdapter(
    private val animeList: List<Anime>,
    private val onAnimeClick: (Anime) -> Unit,
    private val isTopRated: Boolean = false  // Нови параметар за разликовање
) : RecyclerView.Adapter<AnimeAdapter.AnimeViewHolder>() {

    class AnimeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivAnimeImage: ImageView = view.findViewById(R.id.ivAnimeImage)
        val tvAnimeTitle: TextView = view.findViewById(R.id.tvAnimeTitle)
        val tvRating: TextView = view.findViewById(R.id.tvRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        // Бира layout на основу isTopRated флага
        val layoutId = if (isTopRated) {
            R.layout.item_anime_top_rated
        } else {
            R.layout.item_anime_trending
        }

        val view = LayoutInflater.from(parent.context)
            .inflate(layoutId, parent, false)
        return AnimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        val anime = animeList[position]

        holder.tvAnimeTitle.text = anime.title
        holder.tvRating.text = anime.rating.toString()

        // За сад остави placeholder боју, касније додајемо Glide/Coil за слике
        holder.ivAnimeImage.setBackgroundColor(
            android.graphics.Color.parseColor("#2A2A2A")
        )

        holder.itemView.setOnClickListener {
            onAnimeClick(anime)
        }
    }

    override fun getItemCount(): Int = animeList.size
}