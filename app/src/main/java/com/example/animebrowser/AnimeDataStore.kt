package com.example.animebrowser

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object AnimeDataStore {
    private const val PREF_NAME = "AnimeDataStore"
    private const val KEY_ANIME_DATA = "anime_data"

    private val gson = Gson()

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // Save anime data when user interacts with it
    fun saveAnime(context: Context, anime: Anime) {
        val allAnime = getAllAnime(context).toMutableMap()
        allAnime[anime.id] = anime

        val json = gson.toJson(allAnime)
        getPrefs(context).edit().putString(KEY_ANIME_DATA, json).apply()
    }

    // Get anime by ID
    fun getAnime(context: Context, animeId: Int): Anime? {
        return getAllAnime(context)[animeId]
    }

    // Get all stored anime
    fun getAllAnime(context: Context): Map<Int, Anime> {
        val json = getPrefs(context).getString(KEY_ANIME_DATA, null) ?: return emptyMap()

        val type = object : TypeToken<Map<Int, Anime>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }

    // Get multiple anime by IDs
    fun getAnimeList(context: Context, animeIds: Set<Int>): List<Anime> {
        val allAnime = getAllAnime(context)
        return animeIds.mapNotNull { allAnime[it] }
    }
}