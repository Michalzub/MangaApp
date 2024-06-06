package com.example.mangaapp.model

import kotlinx.serialization.Serializable

@Serializable
data class MangaTag(
    val id: String,
    val type: String,
    val attributes: TagAttributes,
    val relationships: List<MangaRelationships>
    )