package com.example.mangaapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mangaapp.R

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChapterReaderScreen(
    viewModel: ChapterReaderViewModel,
    isHorizontal: Boolean,
    onReadingModeChange: () -> Unit,
    onClickBack: () -> Unit
) {
    var isTopBarVisible by remember { mutableStateOf(false)}

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if(isTopBarVisible) {
                ChapterViewerBar(
                    onBackClick = onClickBack,
                    isHorizontal = isHorizontal,
                    onReadingModeChange = onReadingModeChange,
                    backgroundColor = Color.Black
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { isTopBarVisible = !isTopBarVisible }
        ) {
            when(val currentState = viewModel.chapterReaderUiState) {
                is ChapterReaderUiState.Success -> {
                    if(!isHorizontal) {
                        LazyColumn {
                            items(currentState.chapterImageLinks) {image ->
                                AsyncImage(
                                    model = ImageRequest.Builder(context = LocalContext.current)
                                        .data(image)
                                        .crossfade(true)
                                        .build(),
                                    error = painterResource(R.drawable.ic_launcher_background),
                                    placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                                    contentScale = ContentScale.FillWidth,
                                    contentDescription = "",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.Black)
                                )
                            }
                        }
                    } else {
                        val pagerState = rememberPagerState(pageCount = {currentState.chapterImageLinks.size})
                        HorizontalPager(
                            state = pagerState,
                            key = { currentState.chapterImageLinks[it] }
                        ) {index ->

                            AsyncImage(
                                model = ImageRequest.Builder(context = LocalContext.current)
                                    .data(currentState.chapterImageLinks[index])
                                    .crossfade(true)
                                    .build(),
                                error = painterResource(R.drawable.ic_launcher_background),
                                placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                                contentScale = ContentScale.FillWidth,
                                contentDescription = "",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black)
                            )

                        }
                    }
                } else -> {}
            }

        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterViewerBar(
    onBackClick: () -> Unit,
    isHorizontal: Boolean,
    onReadingModeChange: () -> Unit,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    tint = Color.White,
                    contentDescription = "Back",
                    modifier = Modifier
                )
            }
        },
        actions = {
            IconButton(onClick = onReadingModeChange) {
                Icon(
                    painter =
                    if(isHorizontal) {
                        painterResource(R.drawable.swap_vert)
                    } else {
                        painterResource(R.drawable.swap_horiz)
                    },
                    tint = Color.White,
                    contentDescription = "Back",
                    modifier = Modifier
                )
            }
        },
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
    )
}