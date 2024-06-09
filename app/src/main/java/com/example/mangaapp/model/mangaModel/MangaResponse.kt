package com.example.mangaapp.model.mangaModel

import kotlinx.serialization.Serializable


@Serializable
data class MangaResponse(
    val result: String,
    val response: String,
    val data: List<Manga>,
    val limit: Int,
    val offset: Int,
    val total: Int
)