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
import com.example.mangaapp.model.mangaModel.Manga
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface MangaDetailUiState {
    data class Success(val manga: Manga, val chapterDetails: String) : MangaDetailUiState
    object Error : MangaDetailUiState
    object Loading : MangaDetailUiState
}
class MangaDetailsViewModel(
    private val mangaDexRepo: MangaDexRepo,
    private val manga: Manga
) : ViewModel() {
    /** The mutable State that stores the status of the most recent request */
    var mangaDetailUiState: MangaDetailUiState by mutableStateOf(MangaDetailUiState.Loading)
        private set


    init {
        loadMangaDetails()
    }

    private fun loadMangaDetails() {
        viewModelScope.launch {
            mangaDetailUiState = try {
                val tempState = MangaDetailUiState.Success(manga = manga, chapterDetails = "TODO")
                tempState
            } catch (e: IOException) {
                MangaDetailUiState.Error
            } catch (e: HttpException) {
                MangaDetailUiState.Error
            }

        }
    }

    companion object {
        fun provideFactory(manga: Manga): ViewModelProvider.Factory{
            return viewModelFactory {
                initializer {
                    val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MangaApplication)
                    val mangaDexRepo = application.container.mangaDexRepo
                    MangaDetailsViewModel(mangaDexRepo = mangaDexRepo, manga)
                }
            }
        }

    }
}