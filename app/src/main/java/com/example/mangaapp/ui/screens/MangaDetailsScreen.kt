package com.example.mangaapp.ui.screens

import MangaDetailUiState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import com.example.mangaapp.model.mangaModel.MangaTag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaDetailsScreen(
    mangaDetailUiState: MangaDetailUiState,
    onClickBack: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Black,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        topBar = { DetailScreenTopBar("", modifier = modifier, scrollBehavior = scrollBehavior, onClickBack =  onClickBack, backgroundColor = backgroundColor) }
    ) { innerValues ->
        Box(modifier = Modifier
            .background(backgroundColor)
            .padding(innerValues)
            .nestedScroll(scrollBehavior.nestedScrollConnection)) {
            when (mangaDetailUiState) {
                is MangaDetailUiState.Loading -> {
                    LoadingScreen(modifier = modifier.fillMaxSize())
                }
                is MangaDetailUiState.Success -> {
                    MangaDetails(
                        manga = mangaDetailUiState.manga,
                        modifier = Modifier.fillMaxSize(),
                        backgroundColor = backgroundColor
                    )
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
    modifier: Modifier = Modifier,
    backgroundColor: Color
) {
    LazyColumn(
        modifier = modifier
    ) {
        item{ DetailsHeader(manga = manga, modifier = modifier, backgroundColor = backgroundColor) }
        item{ TagList(manga.attributes.tags, backgroundColor = backgroundColor) }
    }
}

@Composable
fun DetailsHeader(
    manga: Manga,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier.height(250.dp)
    ) {
        val coverName = manga.relationships.find { it.type == "cover_art" }?.attributes?.fileName
        if (coverName != null) {
            val coverLink = "https://uploads.mangadex.org/covers/${manga.id}/${coverName}.256.jpg"

            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(coverLink)
                    .crossfade(true)
                    .build(),
                error = painterResource(R.drawable.ic_launcher_background),
                placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                contentScale = ContentScale.Crop,
                contentDescription = "cover",
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RectangleShape)
                    .align(Alignment.Center)
                    .alpha(0.7f)
            )

            Row(
                modifier
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, backgroundColor),
                        )
                    )
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context = LocalContext.current)
                        .data(coverLink)
                        .crossfade(true)
                        .build(),
                    error = painterResource(R.drawable.ic_launcher_background),
                    placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "cover",
                    modifier = Modifier
                        .width(126.dp)
                        .height(180.dp)
                        .padding(start = 10.dp, bottom = 10.dp)
                        .align(Alignment.Bottom),
                )

                Column(
                    modifier = Modifier.padding(top = 100.dp)
                ) {
                    Text(
                        text = manga.attributes.title["en"] ?: "",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White,
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .align(Alignment.Start),
                    )
                    val author = manga.relationships.find { it.type == "author" }?.attributes?.name ?: ""
                    Text(
                        text = author,
                        fontSize = 15.sp,
                        color = Color.White,
                        modifier = Modifier
                            .padding(start = 5.dp, top = 20.dp)
                            .align(Alignment.Start)
                    )
                }
            }
        }
    }
}

@Composable
fun TagList(
    tags: List<MangaTag>,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    LazyRow {
        items(tags) {tag ->
            val tagName = tag.attributes.name["en"]
            if(tagName != null) {
                Card(
                    modifier = modifier
                        .height(26.dp)
                        .padding(2.dp)
                        .border(width = 1.dp, color = Color.Cyan, shape = RoundedCornerShape(10.dp))
                        .background(backgroundColor),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = backgroundColor)
                ) {

                    Text(text = tagName, fontSize = 15.sp,color = Color.Cyan,modifier = Modifier.padding(start = 5.dp, end = 5.dp))
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
    onClickBack: () -> Unit,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor),
        title = {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        modifier = modifier,
        actions =
        {
            IconButton(onClick = onClickBack) {
            Icon(imageVector = Icons.Filled.ArrowBack,
                tint = Color.White,
                contentDescription = "Back",
                modifier = Modifier)
            }
        }
    )
}

@Preview
@Composable
fun MangaDetailsPreview() {
    //MangaDetailsScreen(onClickBack = { /*TODO*/ })
}