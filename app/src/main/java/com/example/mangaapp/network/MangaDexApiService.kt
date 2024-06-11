package com.example.mangaapp.network

import com.example.mangaapp.model.chapterModel.ChapterImageResponse
import com.example.mangaapp.model.chapterModel.ChapterResponse
import com.example.mangaapp.model.mangaModel.MangaResponse
import com.example.mangaapp.model.mangaModel.MangaTag
import com.example.mangaapp.model.mangaModel.MangaTagResponse
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.Retrofit
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.http.GET
import retrofit2.http.Query
import kotlinx.serialization.json.Json
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface MangaDexApiService {
    @GET("manga")
    suspend fun getManga(
        @Query("title") title: String?,
        @Query("includedTags[]") includedTags: List<String>?,
        @Query("excludedTags[]") excludedTags: List<String>?,
        @QueryMap order: Map<String,String>?,
        @Query("includes[]") includes: List<String> = listOf("author", "artist", "cover_art"),
        @Query("contentRating[]") contentRating: List<String> = listOf("safe"),
        @Query("limit")  limit: Int,
        @Query("offset")  offset: Int
    ): MangaResponse

    @GET("manga/tag")
    suspend fun getTags(): MangaTagResponse

    @GET("manga/{id}/feed")
    suspend fun getChapters(
        @Path("id") id: String,
        @Query("translatedLanguage[]") translatedLanguage: List<String> = listOf("en"),
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): ChapterResponse

    @GET("/at-home/server/{id}")
    suspend fun getChapterImages(
        @Path("id") chapterId: String,
    ): ChapterImageResponse
}