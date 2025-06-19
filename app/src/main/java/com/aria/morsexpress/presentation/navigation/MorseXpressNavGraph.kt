package com.aria.morsexpress.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aria.morsexpress.presentation.screen.home.HomeScreen
import com.aria.morsexpress.presentation.screen.imageinput.ImageInputScreen
import com.aria.morsexpress.presentation.screen.textinput.TextInputScreen

object Routes {
    const val HOME = "home"
    const val TEXT_INPUT = "text_input"
    const val IMAGE_INPUT = "image_input"
}

@Composable
fun MorseXpressNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) { HomeScreen(navController) }
        composable(Routes.TEXT_INPUT) { TextInputScreen(navController) }
        composable(Routes.IMAGE_INPUT) { ImageInputScreen(navController) }
    }
}