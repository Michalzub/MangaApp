package com.example.mangaapp.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class MangaRelationships(
    val id: String,
    val type: String,
    val related: String,
)
