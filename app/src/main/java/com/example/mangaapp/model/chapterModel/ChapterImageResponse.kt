package com.example.mangaapp.model.chapterModel

import kotlinx.serialization.Serializable

@Serializable
data class ChapterImageResponse(
    val result: String,
    val baseUrl: String,
    val chapter: ChapterImageData
    )