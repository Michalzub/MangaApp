package com.example.mangaapp.model.mangaModel

import kotlinx.serialization.Serializable

@Serializable
data class MangaRelationshipAttributes(
    val description: String? = null,
    val volume: String? = null,
    val fileName: String? = null,
    val name: String? = null,
    val locale: String? = null,
    val createdAt: String,
    val updatedAt: String,
    val version: Int
)