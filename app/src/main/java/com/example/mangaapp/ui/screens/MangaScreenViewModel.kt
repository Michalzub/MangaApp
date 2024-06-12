package com.example.mangaapp.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.unit.toSize
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
import com.example.mangaapp.model.mangaModel.TagAttributes
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface MangaUiState {
    data class Success(val manga: List<Manga>) : MangaUiState
    data object Error : MangaUiState
    data object Loading : MangaUiState
}

enum class TagSelectionStatus {
    Included,
    Excluded,
    Unselected,
}

data class TagChange(
    val change: Boolean,
    val tagsMapState: Map<String, TagState>
)

data class OrderState(
    val expanded: Boolean,
    val list: List<String>,
    val selectedItem: String,
    val textFiledSize: Size
)

data class TagState(
    val mangaTag: MangaTag,
    var tagSelectionStatus: TagSelectionStatus
)

data class MangaSearchState(
    val isSearching: Boolean,
    val title: String,
    val offset: Int,
    val total: Int
)

class MangaScreenViewModel(
    private val mangaDexRepo: MangaDexRepo
) : ViewModel() {
    /** The mutable State that stores the status of the most recent request */
    var mangaUiState: MangaUiState by mutableStateOf(MangaUiState.Loading)
        private set


    var mangaSearchState: MangaSearchState by mutableStateOf(MangaSearchState(false,"", 0, 0))
        private set
    var isSheetOpen: Boolean by mutableStateOf(false)
        private set

    var tagChange: TagChange by mutableStateOf(TagChange(false, mapOf()))
        private set

    var orderState: OrderState by mutableStateOf(OrderState(false, listOf("Latest", "Rating", "Followed"), "Latest", Size.Zero))
        private set

    fun orderExpandedChange() {
        orderState = orderState.copy(expanded = !orderState.expanded)
    }

    fun closeOrderDropdown() {
        orderState = orderState.copy(expanded = false)
    }

    fun changeSelectedOrderItem(item: String) {
        orderState = orderState.copy(selectedItem = item)
    }

    fun setOrderTextFiledSize(layoutCoordinates: LayoutCoordinates) {
        orderState = orderState.copy(textFiledSize = layoutCoordinates.size.toSize())
    }

    fun resetOrderState() {
        orderState = OrderState(false, listOf("Latest", "Rating", "Followed"), "Latest", Size.Zero )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    var sheetState: SheetState by mutableStateOf(SheetState(skipPartiallyExpanded = false))
        private set

    fun openSheet() {
        isSheetOpen = true
    }

    fun closeSheet() {
        isSheetOpen = false
    }

    init {
        getManga()
        getMangaTags()
    }

    fun reload() {
        getManga()
        getMangaTags()
    }

    fun startSearching() {
        mangaSearchState = mangaSearchState.copy(isSearching = true)
    }
    fun stopSearching() {
        mangaSearchState = mangaSearchState.copy(isSearching = false)
    }

    fun search() {
        getManga()
    }

    fun changeTitleQuery(title: String) {
        mangaSearchState = mangaSearchState.copy(title = title)
    }

    fun cycleTagSelectionStatus(tag: String?) {
        if(tag != null && tagChange.tagsMapState[tag] != null) {
            val tempTags = tagChange.tagsMapState.toMutableMap()
            when (tempTags[tag]?.tagSelectionStatus) {
                TagSelectionStatus.Included -> tempTags[tag]!!.tagSelectionStatus = TagSelectionStatus.Excluded
                TagSelectionStatus.Excluded -> tempTags[tag]!!.tagSelectionStatus = TagSelectionStatus.Unselected
                TagSelectionStatus.Unselected -> tempTags[tag]!!.tagSelectionStatus = TagSelectionStatus.Included
                else -> {}
            }
            tagChange = tagChange.copy(change = !tagChange.change, tagsMapState = tempTags)
        }
    }

    fun resetSearchState() {
        val tempTags = tagChange.tagsMapState
        for(tag in tempTags) {
            tag.component2().tagSelectionStatus = TagSelectionStatus.Unselected
        }
        tagChange = tagChange.copy(tagsMapState = tempTags)
    }

    private fun getMangaTags() {
        viewModelScope.launch {
            try {
                val tempTags = tagChange.tagsMapState.toMutableMap()
                for(tag in mangaDexRepo.getMangaTags().data) {
                    val tagName = tag.attributes.name["en"]
                    if(tagName != null) {
                        tempTags[tagName] = TagState(tag, TagSelectionStatus.Unselected)
                    }
                }
                tagChange = tagChange.copy(tagsMapState = tempTags)
            } catch (e: IOException) {
                tagChange = tagChange.copy(tagsMapState = emptyMap())
            } catch (e: HttpException) {
                tagChange = tagChange.copy(tagsMapState = emptyMap())
            }

        }
    }

    fun loadMoreManga() {
        viewModelScope.launch {
            val includedTags = mutableListOf<String>()
            val excludedTags = mutableListOf<String>()
            for(tag in tagChange.tagsMapState) {
                if(tag.component2().tagSelectionStatus == TagSelectionStatus.Included) {
                    includedTags.add(tag.component2().mangaTag.id)
                } else if( tag.component2().tagSelectionStatus == TagSelectionStatus.Excluded) {
                    excludedTags.add(tag.component2().mangaTag.id)
                }
            }

            when(val currentState = mangaUiState) {
                is MangaUiState.Success -> {
                    val updatedMangaList = currentState.manga.toMutableList()
                    try {
                        val response = mangaDexRepo.getManga(
                            title = if(mangaSearchState.title == "") {
                                null
                            } else {
                                mangaSearchState.title
                            },
                            includedTags = includedTags,
                            excludedTags = excludedTags,
                            order = getOrderQuery(),
                            offset = mangaSearchState.offset
                        )
                        for(manga in response.data)
                        {
                            if(!updatedMangaList.any {it.id == manga.id}) {
                                updatedMangaList.add(manga)
                            }
                        }
                        mangaSearchState = mangaSearchState.copy(offset = mangaSearchState.offset + response.limit)
                        mangaUiState = MangaUiState.Success(updatedMangaList)
                    } catch (e: IOException) {
                        //mangaUiState = MangaUiState.Error
                    } catch (e: HttpException) {
                        //mangaUiState = MangaUiState.Error
                    }
                }
                else -> {

                }
            }
        }
    }
    private fun getManga() {
        viewModelScope.launch {
            mangaUiState = MangaUiState.Loading
            val includedTags = mutableListOf<String>()
            val excludedTags = mutableListOf<String>()

            for(tag in tagChange.tagsMapState) {
                if(tag.component2().tagSelectionStatus == TagSelectionStatus.Included) {
                    includedTags.add(tag.component2().mangaTag.id)
                } else if( tag.component2().tagSelectionStatus == TagSelectionStatus.Excluded) {
                    excludedTags.add(tag.component2().mangaTag.id)
                }
            }
            try {
                val response = mangaDexRepo.getManga(
                    title = if(mangaSearchState.title == "") {
                        null
                    } else {
                        mangaSearchState.title
                    },
                    includedTags = includedTags,
                    excludedTags = excludedTags,
                    order =  getOrderQuery(),
                    offset = 0
                )
                    mangaUiState = MangaUiState.Success(response.data)
                    mangaSearchState = mangaSearchState.copy(offset = 20, total = response.total)

            } catch (e: IOException) {
                mangaUiState = MangaUiState.Error
            } catch (e: HttpException) {
                mangaUiState = MangaUiState.Error
            }

        }
    }

    private fun getOrderQuery(): Map<String, String> {
        return when(orderState.selectedItem) {
            "Latest" -> {
                mapOf("order[latestUploadedChapter]" to "desc")
            }

            "Rating" -> {
                mapOf("order[rating]" to "desc")
            }
            "Followed" -> {
                mapOf("order[followedCount]" to "desc")
            }
            else -> {
                mapOf("order[latestUploadedChapter]" to "desc")
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MangaApplication)
                val mangaDexRepo = application.container.mangaDexRepo
                MangaScreenViewModel(mangaDexRepo = mangaDexRepo)
            }
        }
    }
}
