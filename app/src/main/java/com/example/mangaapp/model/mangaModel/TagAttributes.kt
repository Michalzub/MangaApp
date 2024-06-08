package com.example.mangaapp.model.mangaModel

import kotlinx.serialization.Serializable

@Serializable
data class TagAttributes(
    val name: Map<String, String>,
    val description: Map<String, String>,
    val group: String,
    val version: Int
)