package com.example.animebrowser

import android.content.Context
import android.content.SharedPreferences
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class FavoritesManagerTest {

    private lateinit var context: Context
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    @Before
    fun setUp() {
        editor = mock<SharedPreferences.Editor> {
            on { putStringSet(any(), any()) } doReturn it
        }
        prefs = mock<SharedPreferences> {
            on { edit() } doReturn editor
        }
        context = mock<Context> {
            on { getSharedPreferences(eq("AnimeFavorites"), eq(Context.MODE_PRIVATE)) } doReturn prefs
        }
    }

    // --- Favorites Tests ---

    @Test
    fun `addFavorite adds anime id to favorites`() {
        whenever(prefs.getStringSet("favorites", emptySet())).thenReturn(emptySet())

        FavoritesManager.addFavorite(context, 42)

        verify(editor).putStringSet(eq("favorites"), argThat { contains("42") })
        verify(editor).apply()
    }

    @Test
    fun `removeFavorite removes anime id from favorites`() {
        whenever(prefs.getStringSet("favorites", emptySet())).thenReturn(setOf("42", "99"))

        FavoritesManager.removeFavorite(context, 42)

        verify(editor).putStringSet(eq("favorites"), argThat { !contains("42") && contains("99") })
        verify(editor).apply()
    }

    @Test
    fun `isFavorite returns true for favorite anime`() {
        whenever(prefs.getStringSet("favorites", emptySet())).thenReturn(setOf("42"))

        assertTrue(FavoritesManager.isFavorite(context, 42))
    }

    @Test
    fun `isFavorite returns false for non-favorite anime`() {
        whenever(prefs.getStringSet("favorites", emptySet())).thenReturn(setOf("42"))

        assertFalse(FavoritesManager.isFavorite(context, 99))
    }

    @Test
    fun `getFavorites returns set of integers`() {
        whenever(prefs.getStringSet("favorites", emptySet())).thenReturn(setOf("1", "2", "3"))

        val favorites = FavoritesManager.getFavorites(context)

        assertEquals(3, favorites.size)
        assertTrue(favorites.contains(1))
        assertTrue(favorites.contains(2))
        assertTrue(favorites.contains(3))
    }

    @Test
    fun `getFavorites returns empty set when none exist`() {
        whenever(prefs.getStringSet("favorites", emptySet())).thenReturn(emptySet())

        val favorites = FavoritesManager.getFavorites(context)
        assertTrue(favorites.isEmpty())
    }

    @Test
    fun `getFavoritesCount returns correct count`() {
        whenever(prefs.getStringSet("favorites", emptySet())).thenReturn(setOf("1", "2"))

        assertEquals(2, FavoritesManager.getFavoritesCount(context))
    }

    // --- Watched Tests ---

    @Test
    fun `markAsWatched adds anime id to watched`() {
        whenever(prefs.getStringSet("watched", emptySet())).thenReturn(emptySet())

        FavoritesManager.markAsWatched(context, 10)

        verify(editor).putStringSet(eq("watched"), argThat { contains("10") })
        verify(editor).apply()
    }

    @Test
    fun `markAsNotWatched removes anime id from watched`() {
        whenever(prefs.getStringSet("watched", emptySet())).thenReturn(setOf("10", "20"))

        FavoritesManager.markAsNotWatched(context, 10)

        verify(editor).putStringSet(eq("watched"), argThat { !contains("10") && contains("20") })
        verify(editor).apply()
    }

    @Test
    fun `isWatched returns true for watched anime`() {
        whenever(prefs.getStringSet("watched", emptySet())).thenReturn(setOf("10"))

        assertTrue(FavoritesManager.isWatched(context, 10))
    }

    @Test
    fun `isWatched returns false for unwatched anime`() {
        whenever(prefs.getStringSet("watched", emptySet())).thenReturn(emptySet())

        assertFalse(FavoritesManager.isWatched(context, 10))
    }

    @Test
    fun `getWatchedCount returns correct count`() {
        whenever(prefs.getStringSet("watched", emptySet())).thenReturn(setOf("1", "2", "3"))

        assertEquals(3, FavoritesManager.getWatchedCount(context))
    }

    // --- Watchlist Tests ---

    @Test
    fun `addToWatchlist adds anime id`() {
        whenever(prefs.getStringSet("watchlist", emptySet())).thenReturn(emptySet())

        FavoritesManager.addToWatchlist(context, 5)

        verify(editor).putStringSet(eq("watchlist"), argThat { contains("5") })
        verify(editor).apply()
    }

    @Test
    fun `removeFromWatchlist removes anime id`() {
        whenever(prefs.getStringSet("watchlist", emptySet())).thenReturn(setOf("5", "10"))

        FavoritesManager.removeFromWatchlist(context, 5)

        verify(editor).putStringSet(eq("watchlist"), argThat { !contains("5") && contains("10") })
        verify(editor).apply()
    }

    @Test
    fun `isInWatchlist returns true for watchlisted anime`() {
        whenever(prefs.getStringSet("watchlist", emptySet())).thenReturn(setOf("5"))

        assertTrue(FavoritesManager.isInWatchlist(context, 5))
    }

    @Test
    fun `isInWatchlist returns false for non-watchlisted anime`() {
        whenever(prefs.getStringSet("watchlist", emptySet())).thenReturn(emptySet())

        assertFalse(FavoritesManager.isInWatchlist(context, 5))
    }

    @Test
    fun `getWatchlistCount returns correct count`() {
        whenever(prefs.getStringSet("watchlist", emptySet())).thenReturn(setOf("1"))

        assertEquals(1, FavoritesManager.getWatchlistCount(context))
    }

    // --- Edge Case Tests ---

    @Test
    fun `adding same favorite twice does not duplicate`() {
        whenever(prefs.getStringSet("favorites", emptySet())).thenReturn(setOf("42"))

        FavoritesManager.addFavorite(context, 42)

        verify(editor).putStringSet(eq("favorites"), argThat { size == 1 && contains("42") })
    }

    @Test
    fun `removing non-existent favorite does not crash`() {
        whenever(prefs.getStringSet("favorites", emptySet())).thenReturn(setOf("1"))

        FavoritesManager.removeFavorite(context, 99)

        verify(editor).putStringSet(eq("favorites"), argThat { contains("1") && size == 1 })
    }
}
