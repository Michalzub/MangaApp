package com.example.mangaapp.data

import com.example.mangaapp.model.MangaResponse
import com.example.mangaapp.network.MangaDexApiService

interface MangaDexRepo {
    suspend fun getManga(offset: Int): MangaResponse
}

class NetworkMangaDexRepo(
    private val mangaDexApiService: MangaDexApiService
): MangaDexRepo {
    private val includes = listOf("author", "artist", "cover_art")
    override suspend fun getManga(offset: Int): MangaResponse = mangaDexApiService.getManga(includes, 20, offset)

}