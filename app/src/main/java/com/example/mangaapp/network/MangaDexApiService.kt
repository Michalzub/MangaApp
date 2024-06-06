package com.example.mangaapp.network

import com.example.mangaapp.model.MangaResponse
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.Retrofit
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL =
    "https://api.mangadex.org"

private val json = Json { ignoreUnknownKeys = true }

private val retrofit = Retrofit.Builder()
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()

interface MangaDexApiService {
    @GET("manga")
    suspend fun getManga(@Query("title") title: String): MangaResponse
}

object MangaDexApi {
    val retrofitService: MangaDexApiService by lazy {
        retrofit.create(MangaDexApiService::class.java)
    }
}