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
import com.example.mangaapp.ui.screens.MangaUiState
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface MangaDetailUiState {
    data class Success(val manga: Manga, val chapters: List<Chapter>, val offset: Int): MangaDetailUiState
    object Error : MangaDetailUiState
    object Loading : MangaDetailUiState
}

class MangaDetailsViewModel(
    private val mangaDexRepo: MangaDexRepo,
) : ViewModel() {
    /** The mutable State that stores the status of the most recent request */
    var mangaDetailUiState: MangaDetailUiState by mutableStateOf(MangaDetailUiState.Loading)
        private set

    init{
        mangaDetailUiState = MangaDetailUiState.Loading
    }

    fun setManga(manga: Manga) {
        mangaDetailUiState = MangaDetailUiState.Success(manga, getChapterList(manga), 0)
    }
    fun mangaDetailsLeave() {
        mangaDetailUiState = MangaDetailUiState.Loading
    }

    private fun getChapterList(manga: Manga): List<Chapter> {
        var loadingOffset = 0
        val tempList = mutableListOf<Chapter>()
        viewModelScope.launch {
            try {
                val alreadyLoaded = mutableMapOf<String, Boolean>()
                do {
                    val response = mangaDexRepo.getChapters(
                        id = manga.id,
                        limit = 500,
                        offset = loadingOffset
                    )

                    for(chapter in response.data) {
                        if(chapter.attributes.chapter != null && alreadyLoaded[chapter.attributes.chapter] == null) {
                            alreadyLoaded[chapter.attributes.chapter] = true
                            tempList.add(chapter)
                        }
                    }
                    loadingOffset += 500
                } while (loadingOffset < response.total)
                tempList.sortBy { chapter -> (chapter.attributes.chapter)!!.toDouble() }
            } catch (e: IOException) {
                MangaUiState.Error
            } catch (e: HttpException) {
                MangaUiState.Error
            }
        }
        return tempList
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MangaApplication)
                val mangaDexRepo = application.container.mangaDexRepo
                MangaDetailsViewModel(mangaDexRepo = mangaDexRepo)
            }
        }
    }
}