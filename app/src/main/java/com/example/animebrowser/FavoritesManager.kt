package com.example.animebrowser

import android.content.Context
import android.content.SharedPreferences

object FavoritesManager {
    private const val PREF_NAME = "AnimeFavorites"
    private const val KEY_FAVORITES = "favorites"
    private const val KEY_WATCHED = "watched"
    private const val KEY_WATCHLIST = "watchlist"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // FAVORITES
    fun addFavorite(context: Context, animeId: Int) {
        val favorites = getFavorites(context).toMutableSet()
        favorites.add(animeId)
        getPrefs(context).edit().putStringSet(KEY_FAVORITES, favorites.map { it.toString() }.toSet()).apply()
    }

    fun removeFavorite(context: Context, animeId: Int) {
        val favorites = getFavorites(context).toMutableSet()
        favorites.remove(animeId)
        getPrefs(context).edit().putStringSet(KEY_FAVORITES, favorites.map { it.toString() }.toSet()).apply()
    }

    fun isFavorite(context: Context, animeId: Int): Boolean {
        return getFavorites(context).contains(animeId)
    }

    fun getFavorites(context: Context): Set<Int> {
        val stringSet = getPrefs(context).getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
        return stringSet.map { it.toInt() }.toSet()
    }

    fun getFavoritesCount(context: Context): Int {
        return getFavorites(context).size
    }

    // WATCHED
    fun markAsWatched(context: Context, animeId: Int) {
        val watched = getWatched(context).toMutableSet()
        watched.add(animeId)
        getPrefs(context).edit().putStringSet(KEY_WATCHED, watched.map { it.toString() }.toSet()).apply()
    }

    fun markAsNotWatched(context: Context, animeId: Int) {
        val watched = getWatched(context).toMutableSet()
        watched.remove(animeId)
        getPrefs(context).edit().putStringSet(KEY_WATCHED, watched.map { it.toString() }.toSet()).apply()
    }

    fun isWatched(context: Context, animeId: Int): Boolean {
        return getWatched(context).contains(animeId)
    }

    fun getWatched(context: Context): Set<Int> {
        val stringSet = getPrefs(context).getStringSet(KEY_WATCHED, emptySet()) ?: emptySet()
        return stringSet.map { it.toInt() }.toSet()
    }

    fun getWatchedCount(context: Context): Int {
        return getWatched(context).size
    }

    // WATCHLIST
    fun addToWatchlist(context: Context, animeId: Int) {
        val watchlist = getWatchlist(context).toMutableSet()
        watchlist.add(animeId)
        getPrefs(context).edit().putStringSet(KEY_WATCHLIST, watchlist.map { it.toString() }.toSet()).apply()
    }

    fun removeFromWatchlist(context: Context, animeId: Int) {
        val watchlist = getWatchlist(context).toMutableSet()
        watchlist.remove(animeId)
        getPrefs(context).edit().putStringSet(KEY_WATCHLIST, watchlist.map { it.toString() }.toSet()).apply()
    }

    fun isInWatchlist(context: Context, animeId: Int): Boolean {
        return getWatchlist(context).contains(animeId)
    }

    fun getWatchlist(context: Context): Set<Int> {
        val stringSet = getPrefs(context).getStringSet(KEY_WATCHLIST, emptySet()) ?: emptySet()
        return stringSet.map { it.toInt() }.toSet()
    }

    fun getWatchlistCount(context: Context): Int {
        return getWatchlist(context).size
    }
}