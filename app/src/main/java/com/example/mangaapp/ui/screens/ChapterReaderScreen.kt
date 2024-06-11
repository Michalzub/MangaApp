package com.example.mangaapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mangaapp.R
import kotlin.math.max

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChapterReaderScreen() {

    var horizontal by remember { mutableStateOf(false) }
    var isTopBarVisible by remember { mutableStateOf(false)}
    val images =  listOf(
        "https://cmdxd98sb0x3yprd.mangadex.network/data/a9bd84f3394d1ca211a2e2f16fe65da0/B1-7956ff866e3ead62d87fc4b3ab1e847d70213b2fcc144a5e6103d05ac2effe2c.jpg",
        "https://cmdxd98sb0x3yprd.mangadex.network/data/a9bd84f3394d1ca211a2e2f16fe65da0/B2-fe4a3a1beea07ef60f8ff52486347ffe3091e824a504c403410e14084de6f134.jpg",
        "https://cmdxd98sb0x3yprd.mangadex.network/data/a9bd84f3394d1ca211a2e2f16fe65da0/B3-20040021cecca22a5069331419fc1da9530789ed915f182844b3ab4a7a5d9427.jpg",
        "https://cmdxd98sb0x3yprd.mangadex.network/data/a9bd84f3394d1ca211a2e2f16fe65da0/B4-5e1d69f14b5eea7dfc11c4036c30779e1fceb83fcefc448f0f8264cbce35c1fb.jpg",
        "https://cmdxd98sb0x3yprd.mangadex.network/data/a9bd84f3394d1ca211a2e2f16fe65da0/B5-1518c1bd0e1875df28a4bcb7d0630763c73cb8ca80babe47386754a3dc877be1.jpg",
        "https://cmdxd98sb0x3yprd.mangadex.network/data/a9bd84f3394d1ca211a2e2f16fe65da0/B6-2d98a50cbe90aafb63f94ea578a59f8f41461c297b82ff6d3c6cea6abcd2cbd4.jpg",
    )
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if(isTopBarVisible) {
                ChapterViewerBar(
                    onBackClick = { horizontal = !horizontal },
                    isHorizontal = horizontal,
                    onReadingModeChange = { horizontal = !horizontal},
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
            if(!horizontal) {

                LazyColumn {
                    items(images) {image ->
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
                val pagerState = rememberPagerState(pageCount = { 6 })
                HorizontalPager(
                    state = pagerState,
                    key = { images[it] }
                ) {index ->

                    AsyncImage(
                        model = ImageRequest.Builder(context = LocalContext.current)
                            .data(images[index])
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