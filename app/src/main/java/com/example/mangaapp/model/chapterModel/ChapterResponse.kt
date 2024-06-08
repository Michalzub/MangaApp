package com.example.mangaapp.model.chapterModel

import kotlinx.serialization.Serializable


@Serializable
data class ChapterResponse(
    val result: String,
    val response: String,
    val data: List<Chapter>,
    val limit: Int,
    val offset: Int,
    val total: Int
)