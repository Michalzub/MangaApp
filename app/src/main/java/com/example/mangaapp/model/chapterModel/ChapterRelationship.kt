package com.example.mangaapp.model.chapterModel

import com.example.mangaapp.model.mangaModel.MangaRelationshipAttributes
import kotlinx.serialization.Serializable

@Serializable
data class ChapterRelationship(
    val id: String,
    val type: String,
    val related: String,
    val attributes: MangaRelationshipAttributes
)
