package com.example.animebrowser

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TopRatedGridAdapter(
    private val animeList: List<Anime>,
    private val onAnimeClick: (Anime) -> Unit
) : RecyclerView.Adapter<TopRatedGridAdapter.GridViewHolder>() {

    class GridViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivAnimePoster: ImageView = view.findViewById(R.id.ivAnimePoster)
        val tvAnimeTitle: TextView = view.findViewById(R.id.tvAnimeTitle)
        val tvTypeDuration: TextView = view.findViewById(R.id.tvTypeDuration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_top_rated_grid, parent, false)
        return GridViewHolder(view)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        val anime = animeList[position]

        holder.tvAnimeTitle.text = anime.title

        // Real data from API
        val typeText = anime.type ?: "TV"
        holder.tvTypeDuration.text = typeText

        // Load poster image from API
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
}