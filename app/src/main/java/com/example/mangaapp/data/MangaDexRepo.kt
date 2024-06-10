package com.example.mangaapp.data

import com.example.mangaapp.model.mangaModel.MangaResponse
import com.example.mangaapp.model.mangaModel.MangaTagResponse
import com.example.mangaapp.network.MangaDexApiService

interface MangaDexRepo {
    suspend fun getManga(
        title: String?,
        includedTags: List<String>?,
        excludedTags: List<String>?,
        order: Map<String, String>?,
        offset: Int
    ): MangaResponse

    suspend fun getMangaTags(): MangaTagResponse
}

class NetworkMangaDexRepo(
    private val mangaDexApiService: MangaDexApiService
): MangaDexRepo {

    override suspend fun getManga(title: String?,
                                  includedTags: List<String>?,
                                  excludedTags: List<String>?,
                                  order: Map<String, String>?,
                                  offset: Int
    ): MangaResponse = mangaDexApiService.getManga(
        title = title,
        includedTags = includedTags,
        excludedTags = excludedTags,
        order = order,
        limit = 20,
        offset = offset
    )

    override suspend fun getMangaTags(): MangaTagResponse = mangaDexApiService.getTags()
}