package com.example.mangaapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mangaapp.R
import com.example.mangaapp.model.mangaModel.Manga
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow


@Composable
fun HomeScreen(
    mangaUiState: MangaUiState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    loadMore: () -> Unit,
    onMangaClick: (Manga) -> Unit
) {
    when (mangaUiState) {
        is MangaUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is MangaUiState.Success -> MangaGridScreen(
            mangaUiState.manga, modifier = modifier.fillMaxWidth(), loadMore = loadMore
        )

        is MangaUiState.Error -> ErrorScreen( modifier = modifier.fillMaxSize())
    }
}

@Composable
fun MangaGridScreen(
    mangaList: List<Manga>,
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    loadMore: () -> Unit
) {
    val reachedBottom: Boolean by remember { derivedStateOf { lazyGridState.reachedBottom() } }

    if(reachedBottom) {
        loadMore()
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.padding(horizontal = 4.dp),
        state = lazyGridState,
        contentPadding = contentPadding
    ) {
        items(items = mangaList, key = {manga -> manga.id}
        ) {
            mangaCover -> MangaCard(manga = mangaCover,
            onClick = { /*TODO*/ },
            modifier = modifier
                .padding(4.dp)
                .fillMaxHeight()
                .aspectRatio(0.7f)
            )
        }
    }
}

@Composable
fun MangaCard(
    manga: Manga,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box (
        modifier = modifier
            .clickable { onClick() }
            .clip(RoundedCornerShape(16.dp))
    ){

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
                modifier = Modifier.fillMaxHeight()
            )

        }
        Box(modifier = Modifier.matchParentSize().background(Brush.verticalGradient(
            colors = listOf(Color.Transparent, Color.Black),
            startY = 500f,
            endY = Float.POSITIVE_INFINITY
        )))
        Text(text = manga.attributes.title["en"]?: "",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.align(Alignment.BottomStart).padding(4.dp),
            maxLines = 2,
        )
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

internal fun LazyGridState.reachedBottom(buffer: Int = 1): Boolean {
    val lastVisibleItem = this.layoutInfo.visibleItemsInfo.lastOrNull()
    return lastVisibleItem?.index != 0 && lastVisibleItem?.index == this.layoutInfo.totalItemsCount - buffer
}