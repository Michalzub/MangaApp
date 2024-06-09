package com.example.mangaapp.ui.screens

import MangaDetailUiState
import MangaDetailsViewModel
import android.widget.GridLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mangaapp.R
import com.example.mangaapp.model.mangaModel.Manga

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaDetailsScreen(
    mangaDetailUiState: MangaDetailUiState,
    onClickBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        topBar = { DetailScreenTopBar("", modifier = modifier, scrollBehavior = scrollBehavior) }
    ) { innerValues ->
        Box(modifier = Modifier.padding(innerValues)) {
            when (mangaDetailUiState) {
                is MangaDetailUiState.Loading -> {
                    LoadingScreen(modifier = modifier.fillMaxSize())
                }
                is MangaDetailUiState.Success -> {
                    MangaDetails(manga = mangaDetailUiState.manga,modifier = modifier.fillMaxSize())
                }
                is MangaDetailUiState.Error -> {
                    ErrorScreen( modifier = modifier.fillMaxSize())
                }
                else -> {
                    ErrorScreen( modifier = modifier.fillMaxSize())
                    /*TODO delete else branch*/
                }
            }
        }
    }
}
@Composable
fun MangaDetails(
    manga: Manga,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Box(
            modifier = modifier.height(180.dp)
        ) {
            val coverName = manga.relationships.find{ it.type == "cover_art"}?.attributes?.fileName

            if(coverName != null) {
                val coverLink = "https://uploads.mangadex.org/covers/${manga.id}/${coverName}.256.jpg"

                AsyncImage(
                    model = ImageRequest.Builder(context = LocalContext.current)
                        .data(coverLink)
                        .crossfade(true)
                        .build(),
                    error = painterResource(R.drawable.ic_launcher_background),
                    placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentScale =  ContentScale.Crop,
                    contentDescription = "cover",
                    modifier = Modifier.fillMaxSize().clip(RectangleShape).align(Alignment.Center)
                )

                Row(
                    Modifier.background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black),
                        )
                    ).fillMaxWidth()
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context = LocalContext.current)
                            .data(coverLink)
                            .crossfade(true)
                            .build(),
                        error = painterResource(R.drawable.ic_launcher_background),
                        placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentScale =  ContentScale.Crop,
                        contentDescription = "cover",
                        modifier = Modifier.width(126.dp).height(180.dp)
                    )


                    Text(text = manga.attributes.title["en"] ?: "",
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterVertically).padding(start = 5.dp)
                    )
                }
            }

        }
    }
}

@Composable
fun ChapterList() {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreenTopBar(
    text: String,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        modifier = modifier
    )
}

@Preview
@Composable
fun MangaDetailsPreview() {
    //MangaDetailsScreen(onClickBack = { /*TODO*/ })
}