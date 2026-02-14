package com.example.animebrowser

import android.content.Context
import android.content.SharedPreferences
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class AnimeDataStoreTest {

    private lateinit var context: Context
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    @Before
    fun setUp() {
        editor = mock<SharedPreferences.Editor> {
            on { putString(any(), any()) } doReturn it
        }
        prefs = mock<SharedPreferences> {
            on { edit() } doReturn editor
        }
        context = mock<Context> {
            on { getSharedPreferences(eq("AnimeDataStore"), eq(Context.MODE_PRIVATE)) } doReturn prefs
        }
    }

    @Test
    fun `getAnime returns null when no data stored`() {
        whenever(prefs.getString("anime_data", null)).thenReturn(null)

        val anime = AnimeDataStore.getAnime(context, 1)
        assertNull(anime)
    }

    @Test
    fun `getAllAnime returns empty map when no data stored`() {
        whenever(prefs.getString("anime_data", null)).thenReturn(null)

        val result = AnimeDataStore.getAllAnime(context)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getAllAnime returns empty map for invalid JSON`() {
        whenever(prefs.getString("anime_data", null)).thenReturn("invalid json {{{")

        val result = AnimeDataStore.getAllAnime(context)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `saveAnime calls editor with JSON data`() {
        whenever(prefs.getString("anime_data", null)).thenReturn(null)

        val anime = Anime(id = 1, title = "Test", rating = 8.0)
        AnimeDataStore.saveAnime(context, anime)

        verify(editor).putString(eq("anime_data"), any())
        verify(editor).apply()
    }

    @Test
    fun `getAnimeList returns empty list for empty ids`() {
        whenever(prefs.getString("anime_data", null)).thenReturn(null)

        val result = AnimeDataStore.getAnimeList(context, emptySet())
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getAnimeList returns empty list when no matching ids`() {
        whenever(prefs.getString("anime_data", null)).thenReturn(null)

        val result = AnimeDataStore.getAnimeList(context, setOf(1, 2, 3))
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getAllAnime returns parsed data for valid JSON`() {
        val json = """{"1":{"id":1,"title":"Cowboy Bebop","rating":8.78}}"""
        whenever(prefs.getString("anime_data", null)).thenReturn(json)

        val result = AnimeDataStore.getAllAnime(context)
        assertEquals(1, result.size)
        assertEquals("Cowboy Bebop", result[1]?.title)
    }

    @Test
    fun `getAnime returns correct anime for valid JSON`() {
        val json = """{"1":{"id":1,"title":"Cowboy Bebop","rating":8.78},"2":{"id":2,"title":"Naruto","rating":7.5}}"""
        whenever(prefs.getString("anime_data", null)).thenReturn(json)

        val anime = AnimeDataStore.getAnime(context, 2)
        assertNotNull(anime)
        assertEquals("Naruto", anime?.title)
        assertEquals(7.5, anime?.rating ?: 0.0, 0.001)
    }

    @Test
    fun `getAnime returns null for non-existent id`() {
        val json = """{"1":{"id":1,"title":"Cowboy Bebop","rating":8.78}}"""
        whenever(prefs.getString("anime_data", null)).thenReturn(json)

        val anime = AnimeDataStore.getAnime(context, 99)
        assertNull(anime)
    }

    @Test
    fun `getAnimeList returns matching anime from stored data`() {
        val json = """{"1":{"id":1,"title":"A","rating":8.0},"2":{"id":2,"title":"B","rating":7.0},"3":{"id":3,"title":"C","rating":6.0}}"""
        whenever(prefs.getString("anime_data", null)).thenReturn(json)

        val result = AnimeDataStore.getAnimeList(context, setOf(1, 3))
        assertEquals(2, result.size)
    }
}
