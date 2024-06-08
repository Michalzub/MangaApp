package com.example.mangaapp.model

import kotlinx.serialization.Serializable

@Serializable
data class RelationshipAttributes(
    val description: String? = null,
    val volume: String? = null,
    val fileName: String? = null,
    val locale: String? = null,
    val createdAt: String,
    val updatedAt: String,
    val version: Int
)