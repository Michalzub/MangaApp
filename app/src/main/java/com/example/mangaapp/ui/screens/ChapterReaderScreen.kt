package com.example.mangaapp.ui.screens

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mangaapp.R

/**
 * Composable function to display the ChapterReader screen.
 *
 * @param viewModel The ViewModel associated with the ChapterReader screen.
 * @param onBackClick Lambda function to handle back button click.
 * @param modifier Modifier for additional formatting.
 * @param primaryColor Primary color of the UI.
 * @param secondaryColor Secondary color of the UI.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChapterReaderScreen(
    viewModel: ChapterReaderViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    primaryColor: Color = Color.White,
    secondaryColor: Color = Color.Black
) {
    var isTopBarVisible by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(secondaryColor),
        topBar = {
            if (isTopBarVisible) {
                ChapterViewerBar(
                    onBackClick = onBackClick,
                    isReadingHorizontal = viewModel.isReadingHorizontal,
                    onReadingModeChange = { viewModel.changeReadingMode() },
                    primaryColor = primaryColor,
                    secondaryColor = secondaryColor
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { isTopBarVisible = !isTopBarVisible }
                .background(secondaryColor)
        ) {
            val isLandscape =
                LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
            //based on the state displays different screens
            when (val currentState = viewModel.chapterReaderUiState) {
                is ChapterReaderUiState.Success -> {
                    if (currentState.chapterImageLinks.isNotEmpty()) {
                        if (!viewModel.isReadingHorizontal) {
                            LazyColumn {
                                items(currentState.chapterImageLinks) { image ->
                                    AsyncImage(
                                        model = ImageRequest.Builder(context = LocalContext.current)
                                            .data(image)
                                            .crossfade(true)
                                            .build(),
                                        error = if (secondaryColor == Color.Black) {
                                            painterResource(R.drawable.white_error_outline)
                                        } else {
                                            painterResource(R.drawable.error_outline)
                                        },
                                        placeholder = if (secondaryColor == Color.Black) {
                                            painterResource(R.drawable.white_cloud_queue)
                                        } else {
                                            painterResource(R.drawable.cloud_queue)
                                        },
                                        contentScale = ContentScale.FillWidth,
                                        contentDescription = "",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(secondaryColor)
                                            .align(Alignment.Center)
                                    )
                                }
                            }
                        } else {
                            val pagerState =
                                rememberPagerState(pageCount = { currentState.chapterImageLinks.size })
                            HorizontalPager(
                                state = pagerState,
                                key = { currentState.chapterImageLinks[it] },
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) { index ->
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context = LocalContext.current)
                                            .data(currentState.chapterImageLinks[index])
                                            .crossfade(true)
                                            .build(),
                                        //decides the color of the loading and error icons
                                        error = if (secondaryColor == Color.Black) {
                                            painterResource(R.drawable.white_error_outline)
                                        } else {
                                            painterResource(R.drawable.error_outline)
                                        },
                                        placeholder = if (secondaryColor == Color.Black) {
                                            painterResource(R.drawable.white_cloud_queue)
                                        } else {
                                            painterResource(R.drawable.cloud_queue)
                                        },
                                        contentScale = if (isLandscape) {
                                            ContentScale.FillHeight
                                        } else {
                                            ContentScale.FillWidth
                                        },
                                        contentDescription = "",
                                        modifier = Modifier
                                            .then(
                                                if (isLandscape) {
                                                    Modifier
                                                        .fillMaxHeight()

                                                } else {
                                                    Modifier
                                                        .fillMaxWidth()
                                                }
                                            )
                                            .background(secondaryColor)
                                            .align(Alignment.Center)
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = stringResource(R.string.no_pages),
                            fontSize = 25.sp,
                            color = primaryColor,
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.Center)
                        )
                    }

                }

                is ChapterReaderUiState.Loading -> {
                    LoadingScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center)
                    )
                }

                is ChapterReaderUiState.Error -> {
                    ErrorScreen(
                        text = stringResource(R.string.couldnt_load_pages),
                        onReloadClick = {},
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center)
                    )
                }
            }

        }

    }
}

/**
 * Composable function to display the top app bar in the ChapterReader screen.
 *
 * @param onBackClick Lambda function to handle back button click.
 * @param isReadingHorizontal Boolean to indicate if the reading mode is horizontal.
 * @param onReadingModeChange Lambda function to handle reading mode change.
 * @param modifier Modifier for additional formatting.
 * @param primaryColor Primary color of the UI.
 * @param secondaryColor Secondary color of the UI.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterViewerBar(
    onBackClick: () -> Unit,
    isReadingHorizontal: Boolean,
    onReadingModeChange: () -> Unit,
    modifier: Modifier = Modifier,
    primaryColor: Color = Color.White,
    secondaryColor: Color = Color.Black
) {
    CenterAlignedTopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    tint = primaryColor,
                    contentDescription = stringResource(R.string.back),
                    modifier = Modifier
                )
            }
        },
        actions = {
            IconButton(onClick = onReadingModeChange) {
                Icon(
                    painter =
                    if (isReadingHorizontal) {
                        painterResource(R.drawable.swap_vert)
                    } else {
                        painterResource(R.drawable.swap_horiz)
                    },
                    tint = primaryColor,
                    contentDescription = "reading mode",
                    modifier = Modifier
                )
            }
        },
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = secondaryColor)
    )
}