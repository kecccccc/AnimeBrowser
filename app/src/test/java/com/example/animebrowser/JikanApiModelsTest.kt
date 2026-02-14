package com.example.animebrowser

import org.junit.Assert.*
import org.junit.Test

class JikanApiModelsTest {

    // --- toAnime() conversion tests ---

    @Test
    fun `toAnime converts all fields correctly`() {
        val jikanAnime = JikanAnime(
            mal_id = 1,
            title = "Cowboy Bebop",
            title_english = "Cowboy Bebop",
            score = 8.78,
            synopsis = "A bounty hunter crew",
            episodes = 26,
            type = "TV",
            status = "Finished Airing",
            aired = Aired("Apr 3, 1998 to Apr 24, 1999", AiredProp(AiredDate(1998))),
            genres = listOf(Genre("Action"), Genre("Sci-Fi")),
            images = Images(ImageUrl("http://img.jpg", "http://small.jpg", "http://large.jpg"), null),
            trailer = Trailer("abc123", "https://youtube.com/watch?v=abc123", "https://youtube.com/embed/abc123")
        )

        val anime = jikanAnime.toAnime()

        assertEquals(1, anime.id)
        assertEquals("Cowboy Bebop", anime.title)
        assertEquals(8.78, anime.rating, 0.001)
        assertEquals("http://img.jpg", anime.imageUrl)
        assertEquals(1998, anime.year)
        assertEquals(26, anime.episodes)
        assertEquals("TV", anime.type)
        assertEquals("A bounty hunter crew", anime.synopsis)
        assertEquals("Action, Sci-Fi", anime.genres)
    }

    @Test
    fun `toAnime uses title when title_english is null`() {
        val jikanAnime = JikanAnime(
            mal_id = 1,
            title = "進撃の巨人",
            title_english = null,
            score = 8.5,
            synopsis = null,
            episodes = null,
            type = null,
            status = null,
            aired = null,
            genres = null,
            images = null,
            trailer = null
        )

        val anime = jikanAnime.toAnime()
        assertEquals("進撃の巨人", anime.title)
    }

    @Test
    fun `toAnime prefers title_english over title`() {
        val jikanAnime = JikanAnime(
            mal_id = 1,
            title = "Shingeki no Kyojin",
            title_english = "Attack on Titan",
            score = 8.5,
            synopsis = null,
            episodes = null,
            type = null,
            status = null,
            aired = null,
            genres = null,
            images = null,
            trailer = null
        )

        val anime = jikanAnime.toAnime()
        assertEquals("Attack on Titan", anime.title)
    }

    @Test
    fun `toAnime handles null score as 0`() {
        val jikanAnime = JikanAnime(
            mal_id = 1, title = "Test", title_english = null,
            score = null, synopsis = null, episodes = null, type = null,
            status = null, aired = null, genres = null, images = null, trailer = null
        )

        val anime = jikanAnime.toAnime()
        assertEquals(0.0, anime.rating, 0.001)
    }

    @Test
    fun `toAnime handles null images`() {
        val jikanAnime = JikanAnime(
            mal_id = 1, title = "Test", title_english = null,
            score = null, synopsis = null, episodes = null, type = null,
            status = null, aired = null, genres = null, images = null, trailer = null
        )

        val anime = jikanAnime.toAnime()
        assertNull(anime.imageUrl)
    }

    @Test
    fun `toAnime extracts year from nested aired prop`() {
        val jikanAnime = JikanAnime(
            mal_id = 1, title = "Test", title_english = null,
            score = null, synopsis = null, episodes = null, type = null,
            status = null,
            aired = Aired("2023", AiredProp(AiredDate(2023))),
            genres = null, images = null, trailer = null
        )

        val anime = jikanAnime.toAnime()
        assertEquals(2023, anime.year)
    }

    @Test
    fun `toAnime handles null aired`() {
        val jikanAnime = JikanAnime(
            mal_id = 1, title = "Test", title_english = null,
            score = null, synopsis = null, episodes = null, type = null,
            status = null, aired = null, genres = null, images = null, trailer = null
        )

        val anime = jikanAnime.toAnime()
        assertNull(anime.year)
    }

    @Test
    fun `toAnime handles empty genres list`() {
        val jikanAnime = JikanAnime(
            mal_id = 1, title = "Test", title_english = null,
            score = null, synopsis = null, episodes = null, type = null,
            status = null, aired = null, genres = emptyList(), images = null, trailer = null
        )

        val anime = jikanAnime.toAnime()
        assertEquals("", anime.genres)
    }

    @Test
    fun `toAnime joins multiple genres with comma`() {
        val jikanAnime = JikanAnime(
            mal_id = 1, title = "Test", title_english = null,
            score = null, synopsis = null, episodes = null, type = null,
            status = null, aired = null,
            genres = listOf(Genre("Action"), Genre("Comedy"), Genre("Drama")),
            images = null, trailer = null
        )

        val anime = jikanAnime.toAnime()
        assertEquals("Action, Comedy, Drama", anime.genres)
    }

    @Test
    fun `toAnime handles single genre`() {
        val jikanAnime = JikanAnime(
            mal_id = 1, title = "Test", title_english = null,
            score = null, synopsis = null, episodes = null, type = null,
            status = null, aired = null,
            genres = listOf(Genre("Action")),
            images = null, trailer = null
        )

        val anime = jikanAnime.toAnime()
        assertEquals("Action", anime.genres)
    }

    // --- Data class model tests ---

    @Test
    fun `Trailer data class stores youtube_id`() {
        val trailer = Trailer("abc123", "https://youtube.com/watch?v=abc123", "https://youtube.com/embed/abc123")
        assertEquals("abc123", trailer.youtube_id)
        assertEquals("https://youtube.com/watch?v=abc123", trailer.url)
        assertEquals("https://youtube.com/embed/abc123", trailer.embed_url)
    }

    @Test
    fun `Trailer with null youtube_id but valid embed_url`() {
        val trailer = Trailer(null, null, "https://www.youtube-nocookie.com/embed/gY5nDXOtv_o?enablejsapi=1")
        assertNull(trailer.youtube_id)
        assertNotNull(trailer.embed_url)
    }

    @Test
    fun `Images data class with jpg url`() {
        val images = Images(
            jpg = ImageUrl("http://image.jpg", "http://small.jpg", "http://large.jpg"),
            webp = null
        )
        assertEquals("http://image.jpg", images.jpg?.image_url)
        assertEquals("http://large.jpg", images.jpg?.large_image_url)
        assertNull(images.webp)
    }

    @Test
    fun `Genre data class stores name`() {
        val genre = Genre("Action")
        assertEquals("Action", genre.name)
    }

    @Test
    fun `Aired data class with nested year`() {
        val aired = Aired("Apr 3, 1998 to Apr 24, 1999", AiredProp(AiredDate(1998)))
        assertEquals(1998, aired.prop?.from?.year)
    }

    @Test
    fun `JikanTopAnimeResponse holds list of JikanAnime`() {
        val anime1 = JikanAnime(1, "A", null, null, null, null, null, null, null, null, null, null)
        val anime2 = JikanAnime(2, "B", null, null, null, null, null, null, null, null, null, null)
        val response = JikanTopAnimeResponse(listOf(anime1, anime2))

        assertEquals(2, response.data.size)
        assertEquals("A", response.data[0].title)
        assertEquals("B", response.data[1].title)
    }

    @Test
    fun `JikanAnimeDetailResponse holds single JikanAnime`() {
        val anime = JikanAnime(1, "Test", null, null, null, null, null, null, null, null, null, null)
        val response = JikanAnimeDetailResponse(anime)

        assertEquals(1, response.data.mal_id)
    }

    @Test
    fun `JikanSearchResponse holds list of JikanAnime`() {
        val anime = JikanAnime(5, "Search Result", null, 7.0, null, null, null, null, null, null, null, null)
        val response = JikanSearchResponse(listOf(anime))

        assertEquals(1, response.data.size)
        assertEquals(7.0, response.data[0].score!!, 0.001)
    }
}
