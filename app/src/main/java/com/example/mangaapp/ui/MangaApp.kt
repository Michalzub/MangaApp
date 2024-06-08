package com.example.mangaapp.ui

import MangaDetailsViewModel
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import com.example.mangaapp.ui.screens.HomeScreen
import com.example.mangaapp.ui.screens.HomeScreenViewModel

import androidx.compose.foundation.layout.padding
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mangaapp.R
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mangaapp.ui.screens.MangaDetailsScreen

enum class MangaAppScreens() {
    HomeScreen,
    MangaDetailsScreen
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaApp() {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { MangaAppTopBar(scrollBehavior = scrollBehavior) }
    ) { paddingValues ->
        Surface(
            modifier = Modifier.padding(paddingValues)
        ) {
            val homeScreenViewModel: HomeScreenViewModel = viewModel(factory = HomeScreenViewModel.Factory)
            val mangaDetailsViewModel: MangaDetailsViewModel = viewModel(factory = MangaDetailsViewModel.provideFactory())
            NavHost(
                navController = navController, /*TODO navController*/
                startDestination = MangaAppScreens.HomeScreen.name,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(route = MangaAppScreens.HomeScreen.name) {
                    HomeScreen(
                        mangaUiState = homeScreenViewModel.mangaUiState,
                        modifier = Modifier,
                        contentPadding = paddingValues,
                        loadMore = { homeScreenViewModel.loadMoreManga() },
                        onMangaClick = { /*TODO*/ }
                    )
                }

                composable(route = MangaAppScreens.MangaDetailsScreen.name) {
                    //MangaDetailsScreen(mangaDetailUiState = mangaDetailsViewModel.mangaDetailUiState) /* TODO uncomment when screen done */
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaAppTopBar(scrollBehavior: TopAppBarScrollBehavior, modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        modifier = modifier
    )
}
