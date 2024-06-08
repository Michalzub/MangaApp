package com.example.mangaapp.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.mangaapp.MangaApplication
import com.example.mangaapp.data.MangaDexRepo
import com.example.mangaapp.data.NetworkMangaDexRepo
import com.example.mangaapp.model.Manga
import com.example.mangaapp.model.MangaResponse
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException

sealed interface MangaUiState {
    data class Success(val manga: List<Manga>) : MangaUiState
    object Error : MangaUiState
    object Loading : MangaUiState
}

class HomeScreenViewModel(
    private val mangaDexRepo: MangaDexRepo
) : ViewModel() {
    /** The mutable State that stores the status of the most recent request */
    var mangaUiState: MangaUiState by mutableStateOf(MangaUiState.Loading)
        private set

    private var offsetState: Int by mutableIntStateOf(0)

    init {
        getManga(offsetState)
    }

    fun loadMoreManga() {
        viewModelScope.launch {
            when(val currentState = mangaUiState) {
                is MangaUiState.Success -> {
                    val updatedMangaList = currentState.manga.toMutableList()
                    for(manga in mangaDexRepo.getManga(offsetState).data) {
                        if(!updatedMangaList.any {it.id == manga.id}) {
                            updatedMangaList.add(manga)
                            offsetState++
                        }
                    }
                    mangaUiState = MangaUiState.Success(updatedMangaList)
                }
                else -> {

                }
            }
        }
    }

    private fun getManga(offset: Int) {
        viewModelScope.launch {
            mangaUiState = try {
                val tempState = MangaUiState.Success(mangaDexRepo.getManga(offset).data)
                offsetState += 20
                tempState
            } catch (e: IOException) {
                MangaUiState.Error
            } catch (e: HttpException) {
                MangaUiState.Error
            }

        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MangaApplication)
                val mangaDexRepo = application.container.mangaDexRepo
                HomeScreenViewModel(mangaDexRepo = mangaDexRepo)
            }
        }
    }
}
