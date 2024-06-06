package com.example.mangaapp.model

import kotlinx.serialization.Serializable
@Serializable
data class Manga(
    val id: String,
    val type: String,
    val attributes: MangaAttributes,
    val relationships: List<MangaRelationships>
)