package com.example.animebrowser

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SearchResultAdapter(
    private var animeList: List<Anime>,
    private val onAnimeClick: (Anime) -> Unit
) : RecyclerView.Adapter<SearchResultAdapter.SearchViewHolder>() {

    class SearchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivAnimePoster: ImageView = view.findViewById(R.id.ivAnimePoster)
        val tvAnimeTitle: TextView = view.findViewById(R.id.tvAnimeTitle)
        val tvRating: TextView = view.findViewById(R.id.tvRating)
        val tvTypeEpisodes: TextView = view.findViewById(R.id.tvTypeEpisodes)
        val tvYear: TextView = view.findViewById(R.id.tvYear)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_result, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val anime = animeList[position]

        holder.tvAnimeTitle.text = anime.title
        holder.tvRating.text = anime.rating.toString()

        val typeText = anime.type ?: "TV"
        val episodesText = if (anime.episodes != null) "${anime.episodes} Episodes" else "? Episodes"
        holder.tvTypeEpisodes.text = "$typeText • $episodesText"
        holder.tvYear.text = anime.year?.toString() ?: "N/A"

        // Load poster image
        if (!anime.imageUrl.isNullOrEmpty()) {
            com.bumptech.glide.Glide.with(holder.itemView.context)
                .load(anime.imageUrl)
                .centerCrop()
                .placeholder(android.R.color.darker_gray)
                .into(holder.ivAnimePoster)
        } else {
            holder.ivAnimePoster.setBackgroundColor(
                android.graphics.Color.parseColor("#2A2A2A")
            )
        }

        holder.itemView.setOnClickListener {
            onAnimeClick(anime)
        }
    }

    override fun getItemCount(): Int = animeList.size

    fun updateResults(newList: List<Anime>) {
        animeList = newList
        notifyDataSetChanged()
    }
}
