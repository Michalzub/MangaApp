package com.example.mangaapp.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.mangaapp.MangaApplication
import com.example.mangaapp.data.MangaDexRepo
import com.example.mangaapp.model.chapterModel.Chapter
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException


sealed interface ChapterReaderUiState {
    data class Success(val chapterImageLinks: List<String>) : ChapterReaderUiState
    data object Error : ChapterReaderUiState
    data object Loading : ChapterReaderUiState
}

class ChapterReaderViewModel(
    private val mangaDexRepo: MangaDexRepo,
) : ViewModel() {

    var chapterReaderUiState: ChapterReaderUiState by mutableStateOf(ChapterReaderUiState.Loading)
        private set

    var isHorizontal by mutableStateOf(true)

    fun changeReadingMode() {
        isHorizontal = !isHorizontal
    }

    fun loadChapterImageLinks(chapter: Chapter) {
        chapterReaderUiState = ChapterReaderUiState.Loading
        viewModelScope.launch {
            try {
                val response = mangaDexRepo.getChapterImages(chapter.id)
                val tempList = mutableListOf<String>()
                for (image in response.chapter.data) {
                    tempList.add("${response.baseUrl}/data/${response.chapter.hash}/${image}")
                }
                chapterReaderUiState = ChapterReaderUiState.Success(tempList)
            } catch (e: IOException) {
                chapterReaderUiState = ChapterReaderUiState.Error
            } catch (e: HttpException) {
                chapterReaderUiState = ChapterReaderUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MangaApplication)
                val mangaDexRepo = application.container.mangaDexRepo
                ChapterReaderViewModel(mangaDexRepo = mangaDexRepo)
            }
        }
    }
}