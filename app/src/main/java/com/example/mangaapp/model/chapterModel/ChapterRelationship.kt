package com.example.mangaapp.model.chapterModel

import kotlinx.serialization.Serializable

@Serializable
data class ChapterRelationship(
    val id: String,
    val type: String,
    val related: String? = null,
    val attributes: ChapterRelationshipAttributes? = null
)
