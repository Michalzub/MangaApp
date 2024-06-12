package com.example.mangaapp.model.mangaModel

import kotlinx.serialization.Serializable
@Serializable
data class MangaAttributes(
    val title: Map<String, String>,
    val altTitles: List<Map<String,String>>,
    val description: Map<String, String>,
    val isLocked: Boolean,
    val links: Map<String, String>? = null,
    val originalLanguage: String,
    val lastVolume: String?,
    val lastChapter: String?,
    val publicationDemographic: String?,
    val status: String,
    val year: Int?,
    val contentRating: String,
    val chapterNumbersResetOnNewVolume: Boolean,
    val availableTranslatedLanguages: List<String?>,
    val latestUploadedChapter: String? = null,
    val tags: List<MangaTag>,
    val state: String,
    val version: Int,
    val createdAt: String,
    val updatedAt: String
)