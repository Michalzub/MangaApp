package com.example.mangaapp.model.chapterModel

import kotlinx.serialization.Serializable

@Serializable
data class Chapter(
    val id: String,
    val type: String,
    val attributes: ChapterAttributes,
    val relationships: List<ChapterRelationship>
)
