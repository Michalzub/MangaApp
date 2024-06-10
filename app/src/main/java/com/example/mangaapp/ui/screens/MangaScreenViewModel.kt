package com.example.mangaapp.ui.screens

import android.nfc.Tag
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.mangaapp.MangaApplication
import com.example.mangaapp.data.MangaDexRepo
import com.example.mangaapp.model.mangaModel.Manga
import com.example.mangaapp.model.mangaModel.MangaTag
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface MangaUiState {
    data class Success(val manga: List<Manga>) : MangaUiState
    object Error : MangaUiState
    object Loading : MangaUiState
}

enum class TagState {
    Included,
    Excluded,
    Unselected,
}

data class TagData(
    val mangaTag: MangaTag,
    var tagState: TagState
)

data class MangaSearchState(
    val isSearching: Boolean,
    val title: String,
    val tags: MutableMap<String, TagData>,
    val order: Map<String, String>?,
    val offset: Int
)

class HomeScreenViewModel(
    private val mangaDexRepo: MangaDexRepo
) : ViewModel() {
    /** The mutable State that stores the status of the most recent request */
    var mangaUiState: MangaUiState by mutableStateOf(MangaUiState.Loading)
        private set


    var mangaSearchState: MangaSearchState by mutableStateOf(MangaSearchState(false,"", mutableMapOf<String, TagData>(),null, 0))
        private set
    var isSheetOpen: Boolean by mutableStateOf(false)
        private set

    @OptIn(ExperimentalMaterial3Api::class)
    var sheetState: SheetState by mutableStateOf(SheetState(skipPartiallyExpanded = false))
        private set
    init {
        getManga()
        getMangaTags()
    }

    fun openSheet() {
        isSheetOpen = true
    }

    fun closeSheet() {
        isSheetOpen = false
    }

    fun startSearching() {
        mangaSearchState = mangaSearchState.copy(isSearching = true)
    }

    fun cycleTagInclusion(tag: String?) {
        mangaSearchState.tags.let {
            if(tag != null && mangaSearchState.tags[tag] != null) {
                when (mangaSearchState.tags[tag]!!.tagState) {
                    TagState.Included -> it[tag]!!.tagState = TagState.Excluded
                    TagState.Excluded -> it[tag]!!.tagState = TagState.Unselected
                    TagState.Unselected -> it[tag]!!.tagState = TagState.Included
                }
            }
        }
    }

    fun resetSearchState() {
        for(tag in mangaSearchState.tags) {
            tag.component2().tagState = TagState.Unselected
        }
        mangaSearchState = MangaSearchState(false, "", mangaSearchState.tags, null, 0)
    }

    fun getMangaTags() {
        viewModelScope.launch {
            for(tag in mangaDexRepo.getMangaTags().data) {
                val tagName = tag.attributes.name["en"]
                if(tagName != null) {
                    mangaSearchState.tags[tagName] = TagData(tag, TagState.Unselected)
                }
            }
        }
    }

    fun loadMoreManga() {
        viewModelScope.launch {
            val includedTags = mutableListOf<String>()
            val excludedTags = mutableListOf<String>()
            for(tag in mangaSearchState.tags) {
                if(tag.component2().tagState == TagState.Included) {
                    includedTags.add(tag.component2().mangaTag.id)
                } else if( tag.component2().tagState == TagState.Excluded) {
                    excludedTags.add(tag.component2().mangaTag.id)
                }
            }


            when(val currentState = mangaUiState) {
                is MangaUiState.Success -> {
                    val updatedMangaList = currentState.manga.toMutableList()
                    for(manga in mangaDexRepo.getManga(
                        title = if(mangaSearchState.title == "") {
                                null
                            } else {
                                mangaSearchState.title
                            },
                            includedTags = includedTags,
                            excludedTags = excludedTags,
                            order = mangaSearchState.order,
                            offset = mangaSearchState.offset
                        ).data
                    )
                    {
                        if(!updatedMangaList.any {it.id == manga.id}) {
                            updatedMangaList.add(manga)
                        }
                    }
                    mangaSearchState = mangaSearchState.copy(offset = mangaSearchState.offset + updatedMangaList.size)
                    mangaUiState = MangaUiState.Success(updatedMangaList)
                }
                else -> {

                }
            }
        }
    }
    fun getManga() {
        viewModelScope.launch {
            val includedTags = mutableListOf<String>()
            val excludedTags = mutableListOf<String>()
            for(tag in mangaSearchState.tags) {
                if(tag.component2().tagState == TagState.Included) {
                    includedTags.add(tag.component2().mangaTag.id)
                } else if( tag.component2().tagState == TagState.Excluded) {
                    excludedTags.add(tag.component2().mangaTag.id)
                }
            }

            mangaUiState = try {
                val tempState = MangaUiState.Success(
                    mangaDexRepo.getManga(
                        title = if(mangaSearchState.title == "") {
                            null
                        } else {
                            mangaSearchState.title
                        },
                        includedTags = includedTags,
                        excludedTags = excludedTags,
                        order = mangaSearchState.order,
                        offset = 0
                    ).data
                )
                mangaSearchState = mangaSearchState.copy(offset = 20)
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
