package com.example.mangaapp.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangaapp.model.Manga
import com.example.mangaapp.model.MangaResponse
import com.example.mangaapp.network.MangaDexApi
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException

sealed interface MangaUiState {
    data class Success(val manga: List<Manga>) : MangaUiState
    object Error : MangaUiState
    object Loading : MangaUiState
}

class HomeScreenViewModel() : ViewModel() {
    /** The mutable State that stores the status of the most recent request */
    var mangaUiState: MangaUiState by mutableStateOf(MangaUiState.Loading)
        private set

    init {
        getManga()
    }

    private fun getManga() {
        viewModelScope.launch {
            mangaUiState = try {
                val listResult = MangaDexApi.retrofitService.getManga("Naruto").data
                MangaUiState.Success(listResult)
            } catch (e: IOException) {
                MangaUiState.Error
            } catch (e: HttpException) {
                MangaUiState.Error
            }

        }
    }
}
