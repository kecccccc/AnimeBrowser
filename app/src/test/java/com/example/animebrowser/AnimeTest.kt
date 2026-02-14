package com.example.animebrowser

import org.junit.Assert.*
import org.junit.Test

class AnimeTest {

    @Test
    fun `create Anime with all fields`() {
        val anime = Anime(
            id = 1,
            title = "Cowboy Bebop",
            rating = 8.78,
            imageUrl = "https://example.com/image.jpg",
            year = 1998,
            episodes = 26,
            type = "TV",
            synopsis = "A bounty hunter story",
            genres = "Action, Sci-Fi"
        )

        assertEquals(1, anime.id)
        assertEquals("Cowboy Bebop", anime.title)
        assertEquals(8.78, anime.rating, 0.001)
        assertEquals("https://example.com/image.jpg", anime.imageUrl)
        assertEquals(1998, anime.year)
        assertEquals(26, anime.episodes)
        assertEquals("TV", anime.type)
        assertEquals("A bounty hunter story", anime.synopsis)
        assertEquals("Action, Sci-Fi", anime.genres)
    }

    @Test
    fun `create Anime with only required fields`() {
        val anime = Anime(id = 1, title = "Test", rating = 0.0)

        assertEquals(1, anime.id)
        assertEquals("Test", anime.title)
        assertEquals(0.0, anime.rating, 0.001)
        assertNull(anime.imageUrl)
        assertNull(anime.year)
        assertNull(anime.episodes)
        assertNull(anime.type)
        assertNull(anime.synopsis)
        assertNull(anime.genres)
    }

    @Test
    fun `Anime data class equality`() {
        val anime1 = Anime(id = 1, title = "Test", rating = 9.0)
        val anime2 = Anime(id = 1, title = "Test", rating = 9.0)
        val anime3 = Anime(id = 2, title = "Test", rating = 9.0)

        assertEquals(anime1, anime2)
        assertNotEquals(anime1, anime3)
    }

    @Test
    fun `Anime copy with modified field`() {
        val original = Anime(id = 1, title = "Test", rating = 5.0)
        val copy = original.copy(rating = 9.5)

        assertEquals(1, copy.id)
        assertEquals("Test", copy.title)
        assertEquals(9.5, copy.rating, 0.001)
    }

    @Test
    fun `Anime with zero rating`() {
        val anime = Anime(id = 1, title = "Unrated", rating = 0.0)
        assertEquals(0.0, anime.rating, 0.001)
    }

    @Test
    fun `Anime with negative id`() {
        val anime = Anime(id = -1, title = "Invalid", rating = 0.0)
        assertEquals(-1, anime.id)
    }
}
