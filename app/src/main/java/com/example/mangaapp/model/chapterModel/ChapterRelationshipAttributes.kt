package com.example.mangaapp.model.chapterModel

import kotlinx.serialization.Serializable

@Serializable
data class ChapterRelationshipAttributes(
    val id: String,
    val type: String,
)
