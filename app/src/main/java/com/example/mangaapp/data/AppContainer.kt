package com.example.mangaapp.data

import com.example.mangaapp.network.MangaDexApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val mangaDexRepo: MangaDexRepo
}

class DefaultAppContainer : AppContainer {
    private val baseUrl =
        "https://api.mangadex.org"

    private val json = Json { ignoreUnknownKeys = true
    coerceInputValues = true}

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    private val retrofitService: MangaDexApiService by lazy {
        retrofit.create(MangaDexApiService::class.java)
    }
    override val mangaDexRepo: MangaDexRepo by lazy {
        NetworkMangaDexRepo(retrofitService)
    }
}