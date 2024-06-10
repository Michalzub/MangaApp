package com.example.mangaapp.ui

import MangaDetailsViewModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.mangaapp.ui.screens.MangaScreen
import com.example.mangaapp.ui.screens.MangaScreenViewModel

import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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
    val mangaScreenViewModel: MangaScreenViewModel = viewModel(factory = MangaScreenViewModel.Factory)
    val mangaDetailsViewModel: MangaDetailsViewModel = viewModel(factory = MangaDetailsViewModel.Factory)

    val backStackEntry by navController.currentBackStackEntryAsState()

    NavHost(
        navController = navController,
        startDestination = MangaAppScreens.HomeScreen.name,
        modifier = Modifier
    ) {
        composable(route = MangaAppScreens.HomeScreen.name) {
            MangaScreen(
                viewModel = mangaScreenViewModel,
                modifier = Modifier,
                loadMore = {
                    if (mangaScreenViewModel.mangaSearchState.offset < mangaScreenViewModel.mangaSearchState.total) {
                        mangaScreenViewModel.loadMoreManga()
                    }
                },
                onMangaClick = { manga ->
                    mangaDetailsViewModel.setManga(manga)
                    navController.navigate(MangaAppScreens.MangaDetailsScreen.name)
                },
            )
        }

        composable(route = MangaAppScreens.MangaDetailsScreen.name) {
            MangaDetailsScreen(mangaDetailUiState = mangaDetailsViewModel.mangaDetailUiState,
                onClickBack = {
                    navController.navigateUp()
                    mangaDetailsViewModel.mangaDetailsLeave()
                }) /* TODO uncomment when screen done */
        }
    }
}
