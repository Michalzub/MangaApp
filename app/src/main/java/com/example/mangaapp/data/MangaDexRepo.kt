package com.example.mangaapp.data

import com.example.mangaapp.model.mangaModel.MangaResponse
import com.example.mangaapp.network.MangaDexApiService

interface MangaDexRepo {
    suspend fun getManga(offset: Int): MangaResponse
}

class NetworkMangaDexRepo(
    private val mangaDexApiService: MangaDexApiService
): MangaDexRepo {
    private val includes = listOf("author", "artist", "cover_art")
    private val contentRating = listOf("safe")
    override suspend fun getManga(offset: Int): MangaResponse = mangaDexApiService.getManga(includes, contentRating, 20, offset)

}