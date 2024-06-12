package com.example.mangaapp.model.mangaModel

import kotlinx.serialization.Serializable

@Serializable
data class MangaTag(
    val id: String,
    val type: String,
    val attributes: TagAttributes,
    val relationships: List<MangaRelationship>
)