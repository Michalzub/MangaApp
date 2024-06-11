package com.example.mangaapp.model.chapterModel
import kotlinx.serialization.Serializable

@Serializable
data class ChapterImageData(
    val hash: String,
    val data: List<String>,
    val dataSaver: List<String>
)
