package com.joaootavio.android.readerapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.joaootavio.android.readerapp.screens.ReaderSpalshScreen
import com.joaootavio.android.readerapp.screens.details.BookDetailsScreen
import com.joaootavio.android.readerapp.screens.home.Home
import com.joaootavio.android.readerapp.screens.home.HomeScreenViewModel
import com.joaootavio.android.readerapp.screens.login.ReaderLoginScreen
import com.joaootavio.android.readerapp.screens.search.BooksSearchViewModel
import com.joaootavio.android.readerapp.screens.search.SearchScreen
import com.joaootavio.android.readerapp.screens.stats.ReaderStatsScreen
import com.joaootavio.android.readerapp.screens.update.BookUpdateScreen

@ExperimentalComposeUiApi
@Composable
fun ReaderNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = ReaderScreens.SplashScreen.name
    ) {

        val detailName = ReaderScreens.DetailScreen.name
        composable("$detailName/{bookId}", arguments = listOf(navArgument(name = "bookId"){
            type = NavType.StringType
        })) { backStackEntry ->
            backStackEntry.arguments?.getString("bookId").let {
                BookDetailsScreen(navController = navController, bookId = it.toString())
            }
        }

        composable(ReaderScreens.ReaderHomeScreen.name) {
            val homeViewModel = hiltViewModel<HomeScreenViewModel>()
            Home(navController = navController, viewModel = homeViewModel)
        }

        composable(ReaderScreens.LoginScreen.name) {
            ReaderLoginScreen(navController = navController)
        }

        composable(ReaderScreens.SearchScreen.name) {
            val searchViewModel = hiltViewModel<BooksSearchViewModel>()
            SearchScreen(navController = navController, viewModel = searchViewModel)
        }

        composable(ReaderScreens.ReaderStatsScreen.name) {
            val homeViewModel = hiltViewModel<HomeScreenViewModel>()
            ReaderStatsScreen(navController = navController, viewModel= homeViewModel)
        }

        val updateName = ReaderScreens.UpdateScreen.name
        composable("$updateName/{bookItemId}", arguments = listOf(navArgument("bookItemId") {
            type = NavType.StringType
        })) { navBackStackEntry ->
            navBackStackEntry.arguments?.getString("bookItemId").let {
                BookUpdateScreen(navController = navController, bookItemId = it.toString())
            }
        }

        composable(ReaderScreens.SplashScreen.name) {
            ReaderSpalshScreen(navController = navController)
        }

    }
}

