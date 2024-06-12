package com.example.mangaapp.ui.screens

import MangaDetailUiState
import MangaDetailsViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mangaapp.R
import com.example.mangaapp.model.chapterModel.Chapter
import com.example.mangaapp.model.mangaModel.Manga
import com.example.mangaapp.model.mangaModel.MangaTag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaDetailsScreen(
    mangaDetailsViewModel: MangaDetailsViewModel,
    onClickBack: () -> Unit,
    onChapterClick: (Chapter) -> Unit,
    modifier: Modifier = Modifier,
    primaryColor: Color = Color.White,
    secondaryColor: Color = Color.Black,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        topBar = {
            DetailScreenTopBar(
                text = "",
                scrollBehavior = scrollBehavior,
                onClickBack = onClickBack,
                secondaryColor = secondaryColor
            )
        }
    ) { innerValues ->
        Box(
            modifier = Modifier
                .background(secondaryColor)
                .padding(innerValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            when (val currentState = mangaDetailsViewModel.mangaDetailUiState) {
                is MangaDetailUiState.Loading -> {
                    LoadingScreen(modifier = modifier.fillMaxSize())
                }

                is MangaDetailUiState.Success -> {
                    MangaDetails(
                        manga = currentState.manga,
                        modifier = Modifier.fillMaxSize(),
                        secondaryColor = secondaryColor,
                        chapterList = currentState.chapters,
                        onChapterClick = onChapterClick
                    )
                }

                is MangaDetailUiState.Error -> {
                    ErrorScreen(modifier = modifier.fillMaxSize(),
                        onReloadClick = {mangaDetailsViewModel.loadMangaDetails(currentState.manga)})
                }

                else -> {}
            }
        }
    }
}

@Composable
fun MangaDetails(
    manga: Manga,
    chapterList: List<Chapter>,
    onChapterClick: (Chapter) -> Unit,
    modifier: Modifier = Modifier,
    primaryColor: Color = Color.White,
    secondaryColor: Color = Color.Black
) {
    LazyColumn(
        modifier = modifier
    ) {
        item { DetailsHeader(manga = manga, modifier = modifier, secondaryColor = secondaryColor) }
        item { TagList(manga.attributes.tags, secondaryColor = secondaryColor) }
        item {
            Row(
                modifier = Modifier.height(50.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total chapters: ${chapterList.size}",
                    fontSize = 25.sp,
                    color = primaryColor
                )
            }
        }
        items(chapterList) { chapter ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clickable { onChapterClick(chapter) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Chapter ${chapter.attributes.chapter}",
                    fontSize = 20.sp,
                    color = primaryColor,
                    modifier = Modifier.padding(start = 5.dp)
                )
            }
        }
    }
}

@Composable
fun DetailsHeader(
    manga: Manga,
    modifier: Modifier = Modifier,
    primaryColor: Color = Color.White,
    secondaryColor: Color = Color.Black
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
                error = if(secondaryColor == Color.Black) {
                    painterResource(R.drawable.white_error_outline)
                } else {
                    painterResource(R.drawable.error_outline)
                },
                placeholder = if(secondaryColor == Color.Black) {
                    painterResource(R.drawable.white_cloud_queue)
                } else {
                    painterResource(R.drawable.cloud_queue)
                },
                contentScale = ContentScale.Crop,
                contentDescription = stringResource(R.string.cover),
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
                            colors = listOf(Color.Transparent, secondaryColor),
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
                    error = if(secondaryColor == Color.Black) {
                        painterResource(R.drawable.white_error_outline)
                    } else {
                        painterResource(R.drawable.error_outline)
                    },
                    placeholder = if(secondaryColor == Color.Black) {
                        painterResource(R.drawable.white_cloud_queue)
                    } else {
                        painterResource(R.drawable.cloud_queue)
                    },
                    contentDescription = stringResource(R.string.cover),
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
                        color = primaryColor,
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .align(Alignment.Start),
                    )
                    val author =
                        manga.relationships.find { it.type == "author" }?.attributes?.name ?: ""
                    Text(
                        text = author,
                        fontSize = 15.sp,
                        color = primaryColor,
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
    modifier: Modifier = Modifier,
    primaryColor: Color = Color.White,
    secondaryColor: Color = Color.Black
) {
    LazyRow {
        items(tags) { tag ->
            val tagName = tag.attributes.name["en"]
            if (tagName != null) {
                Card(
                    modifier = modifier
                        .height(26.dp)
                        .padding(2.dp)
                        .border(
                            width = 1.dp,
                            color = primaryColor,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .background(secondaryColor),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = secondaryColor)
                ) {

                    Text(
                        text = tagName,
                        fontSize = 15.sp,
                        color = primaryColor,
                        modifier = Modifier.padding(start = 5.dp, end = 5.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreenTopBar(
    text: String,
    scrollBehavior: TopAppBarScrollBehavior,
    onClickBack: () -> Unit,
    modifier: Modifier = Modifier,
    primaryColor: Color = Color.White,
    secondaryColor: Color = Color.Black
) {
    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = secondaryColor),
        title = {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        modifier = modifier,
        navigationIcon =
        {
            IconButton(onClick = onClickBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    tint = primaryColor,
                    contentDescription = stringResource(R.string.back),
                    modifier = Modifier
                )
            }
        }
    )
}