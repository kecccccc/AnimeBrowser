package com.example.animebrowser

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface JikanApiService {

    // Get top anime
    @GET("top/anime")
    fun getTopAnime(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 25
    ): Call<JikanTopAnimeResponse>

    // Get anime by season
    @GET("seasons/now")
    fun getCurrentSeasonAnime(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 25
    ): Call<JikanSeasonAnimeResponse>

    // Get anime details by ID
    @GET("anime/{id}/full")
    fun getAnimeDetails(@Path("id") id: Int): Call<JikanAnimeDetailResponse>

    // Search anime
    @GET("anime")
    fun searchAnime(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 25
    ): Call<JikanSearchResponse>
}

// Response models
data class JikanTopAnimeResponse(
    val data: List<JikanAnime>
)

data class JikanSeasonAnimeResponse(
    val data: List<JikanAnime>
)

data class JikanSearchResponse(
    val data: List<JikanAnime>
)

data class JikanAnimeDetailResponse(
    val data: JikanAnime
)

data class JikanAnime(
    val mal_id: Int,
    val title: String,
    val title_english: String?,
    val score: Double?,
    val synopsis: String?,
    val episodes: Int?,
    val type: String?,
    val status: String?,
    val aired: Aired?,
    val genres: List<Genre>?,
    val images: Images?,
    val trailer: Trailer?
)

data class Aired(
    val string: String?,
    val prop: AiredProp?
)

data class AiredProp(
    val from: AiredDate?
)

data class AiredDate(
    val year: Int?
)

data class Genre(
    val name: String
)

data class Images(
    val jpg: ImageUrl?,
    val webp: ImageUrl?
)

data class ImageUrl(
    val image_url: String?,
    val small_image_url: String?,
    val large_image_url: String?
)

data class Trailer(
    val youtube_id: String?,
    val url: String?,
    val embed_url: String?
)

// Extension to convert JikanAnime to our Anime model
fun JikanAnime.toAnime(): Anime {
    return Anime(
        id = mal_id,
        title = title_english ?: title,
        rating = score ?: 0.0,
        imageUrl = images?.jpg?.image_url,
        year = aired?.prop?.from?.year,
        episodes = episodes,
        type = type,
        synopsis = synopsis,
        genres = genres?.joinToString(", ") { it.name }
    )
}