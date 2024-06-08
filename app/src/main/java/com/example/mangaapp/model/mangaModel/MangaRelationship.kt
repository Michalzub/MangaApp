package com.example.mangaapp.model.mangaModel

import kotlinx.serialization.Serializable

@Serializable
data class MangaRelationship(
    val id: String,
    val type: String,
    val related: String? = null,
    val attributes: MangaRelationshipAttributes? = null
)
