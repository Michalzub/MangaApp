package com.example.mangaapp.ui

import MangaDetailsViewModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.mangaapp.ui.screens.MangaScreen
import com.example.mangaapp.ui.screens.HomeScreenViewModel

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
    val homeScreenViewModel: HomeScreenViewModel = viewModel(factory = HomeScreenViewModel.Factory)
    val mangaDetailsViewModel: MangaDetailsViewModel = viewModel(factory = MangaDetailsViewModel.Factory)

    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentScreen = MangaAppScreens.valueOf(
        backStackEntry?.destination?.route ?: MangaAppScreens.HomeScreen.name)

    NavHost(
        navController = navController,
        startDestination = MangaAppScreens.HomeScreen.name,
        modifier = Modifier
    ) {
        composable(route = MangaAppScreens.HomeScreen.name) {
            MangaScreen(
                viewModel = homeScreenViewModel,
                modifier = Modifier,
                loadMore = { homeScreenViewModel.loadMoreManga() },
                onMangaClick = { manga ->
                    mangaDetailsViewModel.setManga(manga)
                    navController.navigate(MangaAppScreens.MangaDetailsScreen.name)
                },
                onSearchClick = {}
            )
        }

        composable(route = MangaAppScreens.MangaDetailsScreen.name) {
            MangaDetailsScreen(mangaDetailUiState = mangaDetailsViewModel.mangaDetailUiState,
                onClickBack = {
                    navController.navigateUp()
                }) /* TODO uncomment when screen done */
        }
    }
}
