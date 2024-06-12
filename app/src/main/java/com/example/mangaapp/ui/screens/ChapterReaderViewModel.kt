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

/**
 * Represents the different states of the UI in the ChapterReader screen.
 */
sealed interface ChapterReaderUiState {
    /**
     * Represents a successful state with a list of chapter image links.
     * @param chapterImageLinks List of image URLs for the chapter.
     */
    data class Success(val chapterImageLinks: List<String>) : ChapterReaderUiState

    /**
     * Represents an error state with the chapter that is supposed to be read.
     * @param chapter Chapter to be read.
     */
    data class Error(val chapter: Chapter) : ChapterReaderUiState

    /**
     * Represents a loading state.
     */
    data object Loading : ChapterReaderUiState
}

/**
 * ViewModel for managing the state and logic of the ChapterReader screen.
 * @param mangaDexRepo Repository for interacting with the MangaDex API.
 */
class ChapterReaderViewModel(
    private val mangaDexRepo: MangaDexRepo,
) : ViewModel() {

    // Holds the current state of the ChapterReader UI.
    var chapterReaderUiState: ChapterReaderUiState by mutableStateOf(ChapterReaderUiState.Loading)
        private set

    // Indicates whether the reading mode is horizontal.
    var isReadingHorizontal by mutableStateOf(true)

    /**
     * Toggles the reading mode between horizontal and vertical.
     */
    fun changeReadingMode() {
        isReadingHorizontal = !isReadingHorizontal
    }

    /**
     * Loads the image links for a given chapter and updates the UI state.
     * @param chapter The chapter to load images for.
     */
    fun loadChapterImageLinks(chapter: Chapter) {
        chapterReaderUiState = ChapterReaderUiState.Loading
        viewModelScope.launch {
            try {
                val response = mangaDexRepo.getChapterImages(chapter.id)
                val tempList = mutableListOf<String>()
                for (image in response.chapter.data) {
                    // Construct the full image URLs.
                    tempList.add("${response.baseUrl}/data/${response.chapter.hash}/${image}")
                }
                // Update the state with the list of image links.
                chapterReaderUiState = ChapterReaderUiState.Success(tempList)
            } catch (e: IOException) {
                chapterReaderUiState = ChapterReaderUiState.Error(chapter)
            } catch (e: HttpException) {
                chapterReaderUiState = ChapterReaderUiState.Error(chapter)
            }
        }
    }

    /**
     * Factory for creating an instance of ChapterReaderViewModel.
     */
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