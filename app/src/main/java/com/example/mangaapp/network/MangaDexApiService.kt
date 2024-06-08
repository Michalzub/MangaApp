package com.example.mangaapp.network

import com.example.mangaapp.model.mangaModel.MangaResponse
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.Retrofit
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.http.GET
import retrofit2.http.Query
import kotlinx.serialization.json.Json

private const val BASE_URL =
    "https://api.mangadex.org"

private val json = Json { ignoreUnknownKeys = true }

private val retrofit = Retrofit.Builder()
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()

interface MangaDexApiService {
    @GET("manga")
    suspend fun getManga(
        @Query("includes[]") includes: List<String>,
        @Query("contentRating[]") contentRating: List<String>,
        @Query("limit")  limit: Int,
        @Query("offset")  offset: Int
    ): MangaResponse
}