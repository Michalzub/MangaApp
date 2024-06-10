package com.example.mangaapp.model.mangaModel

import kotlinx.serialization.Serializable

@Serializable
data class MangaTagResponse(
    val result: String,
    val response: String,
    val data: List<MangaTag>,
    val limit: Int,
    val offset: Int,
    val total: Int
)
