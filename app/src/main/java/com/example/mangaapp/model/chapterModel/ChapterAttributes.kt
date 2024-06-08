package com.example.mangaapp.model.chapterModel

import kotlinx.serialization.Serializable

@Serializable
data class ChapterAttributes(
    val title: String,
    val volume: String,
    val chapter: String,
    val pages: Int,
    val translatedLanguage: String,
    val uploader: String,
    val externalUrl: String,
    val version: Int,
    val createdAt: String,
    val updatedAt: String,
    val publishAt: String,
    val readableAt: String
)
