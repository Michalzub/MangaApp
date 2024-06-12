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
import com.example.mangaapp.model.mangaModel.Manga
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/**
 * Represents the different states of the UI in the MangaDetail screen.
 */
sealed interface MangaDetailUiState {
    /**
     * Represents a successful state with manga details and a list of chapters.
     * @param manga The manga details.
     * @param chapters The list of chapters.
     */
    data class Success(val manga: Manga, val chapters: List<Chapter>) : MangaDetailUiState

    /**
     * Represents an error state.
     * @param manga The manga details.
     */
    data class Error(val manga: Manga) : MangaDetailUiState

    /**
     * Represents a loading state.
     */
    data object Loading : MangaDetailUiState
}

/**
 * ViewModel for managing the state and logic of the MangaDetails screen.
 * @param mangaDexRepo Repository for interacting with the MangaDex API.
 */
class MangaDetailsViewModel(
    private val mangaDexRepo: MangaDexRepo,
) : ViewModel() {

    // Holds the current state of the MangaDetails UI.
    var mangaDetailUiState: MangaDetailUiState by mutableStateOf(MangaDetailUiState.Loading)
        private set

    init {
        mangaDetailUiState = MangaDetailUiState.Loading
    }

    /**
     * Resets the UI state to the initial loading state.
     */
    fun mangaDetailsLeave() {
        mangaDetailUiState = MangaDetailUiState.Loading
    }

    /**
     * Loads the manga details and its chapters.
     * @param manga The manga to load details for.
     */
    fun loadMangaDetails(manga: Manga) {
        mangaDetailUiState = MangaDetailUiState.Loading
        var loadingOffset = 0
        val tempList = mutableListOf<Chapter>()
        viewModelScope.launch {
            try {
                val alreadyLoaded = mutableMapOf<String, Boolean>()
                do {
                    val response = mangaDexRepo.getChapters(
                        id = manga.id, limit = 500, offset = loadingOffset
                    )

                    for (chapter in response.data) {
                        // Check if chapter has already been loaded
                        if (chapter.attributes.chapter != null && alreadyLoaded[chapter.attributes.chapter] == null) {
                            alreadyLoaded[chapter.attributes.chapter] = true
                            tempList.add(chapter)
                        }
                    }
                    loadingOffset += 500
                } while (loadingOffset < response.total)
                tempList.sortBy { chapter -> (chapter.attributes.chapter)!!.toDouble() }
                mangaDetailUiState = MangaDetailUiState.Success(manga = manga, chapters = tempList)
            } catch (e: IOException) {
                mangaDetailUiState = MangaDetailUiState.Error(manga)
            } catch (e: HttpException) {
                mangaDetailUiState = MangaDetailUiState.Error(manga)
            }
        }
    }

    /**
     * Factory for creating an instance of MangaDetailsViewModel.
     */
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MangaApplication)
                val mangaDexRepo = application.container.mangaDexRepo
                MangaDetailsViewModel(mangaDexRepo = mangaDexRepo)
            }
        }
    }
}