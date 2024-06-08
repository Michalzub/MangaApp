package com.example.mangaapp.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonObject

@Serializable
data class MangaRelationships(
    val id: String,
    val type: String,
    val related: String? = null,
    val attributes: RelationshipAttributes? = null
)
