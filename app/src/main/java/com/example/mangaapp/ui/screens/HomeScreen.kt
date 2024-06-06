package com.example.mangaapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mangaapp.R
import com.example.mangaapp.model.Manga
import com.example.mangaapp.model.MangaAttributes


@Composable
fun HomeScreen(
    mangaUiState: MangaUiState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    when (mangaUiState) {
        is MangaUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is MangaUiState.Success -> MangaGridScreen(
            mangaUiState.manga, modifier = modifier.fillMaxWidth()
        )

        is MangaUiState.Error -> ErrorScreen( modifier = modifier.fillMaxSize())
    }
}

@Composable
fun MangaGridScreen(
    manga: List<Manga>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Text(text = manga[0].attributes.title["en"] ?: "Title Not Found")
    }

    /*
        LazyVerticalGrid(columns = GridCells.Adaptive(256.dp),
            modifier = modifier
                .padding(horizontal = 4.dp),
            contentPadding = contentPadding
        ) {
        }

     */
}

@Composable
fun MangaCard(
    manga: MangaAttributes,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Card (
        modifier = modifier
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ){
        val mangaTitle = manga.title["en"] ?: "Title Not Found"
        Text(text = mangaTitle)
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier.size(200.dp),
        painter = painterResource(R.drawable.ic_launcher_foreground),
        contentDescription = stringResource(R.string.loading)
    )
}

@Composable
fun ErrorScreen(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Text(text = "Error", fontSize = 30.sp)
    }
}