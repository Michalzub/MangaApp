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
import androidx.navigation.NavHostController
import com.example.mangaapp.R
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mangaapp.ui.screens.MangaDetailsScreen

enum class MangaAppScreens() {
    HomeScreen,
    MangaDetailsScreen
}

@Composable
fun MangaApp(
    navController: NavHostController = rememberNavController()
) {
    val homeScreenViewModel: HomeScreenViewModel = viewModel(factory = HomeScreenViewModel.Factory)
    val mangaDetailsViewModel: MangaDetailsViewModel = viewModel(factory = MangaDetailsViewModel.Factory)
    NavHost(
        navController = navController,
        startDestination = MangaAppScreens.HomeScreen.name,
        modifier = Modifier
    ) {
        composable(route = MangaAppScreens.HomeScreen.name) {
            HomeScreen(
                mangaUiState = homeScreenViewModel.mangaUiState,
                modifier = Modifier,
                loadMore = { homeScreenViewModel.loadMoreManga() },
                onMangaClick = { manga ->
                    mangaDetailsViewModel.setManga(manga)
                    navController.navigate(MangaAppScreens.MangaDetailsScreen.name)
                }
            )
        }

        composable(route = MangaAppScreens.MangaDetailsScreen.name) {
            MangaDetailsScreen(mangaDetailUiState = mangaDetailsViewModel.mangaDetailUiState,
                onClickBack = {}) /* TODO uncomment when screen done */
        }
    }
}
