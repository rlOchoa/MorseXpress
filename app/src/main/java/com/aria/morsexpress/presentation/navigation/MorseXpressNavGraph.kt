package com.aria.morsexpress.presentation.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aria.morsexpress.presentation.screen.home.HomeScreen
import com.aria.morsexpress.presentation.screen.textinput.TextInputScreen
import com.aria.morsexpress.presentation.screen.imageinput.CameraLivePreviewScreen
import com.aria.morsexpress.presentation.screen.imageinput.CameraInputSelectorScreen
import com.aria.morsexpress.presentation.screen.imageinput.OcrScreen
import com.aria.morsexpress.presentation.screen.imageinput.MorseRecognitionScreen
import com.aria.morsexpress.presentation.screen.result.CameraResultScreen
import androidx.core.net.toUri

object Routes {
    const val HOME = "home"
    const val TEXT_INPUT = "text_input"
    const val CAMERA_MENU = "camera_input_screen"
    const val CAMERA_CAPTURE = "camera_capture_screen"
//    const val OCR_SCREEN = "ocr_screen"
//    const val MORSE_RECOGNITION = "morse_recognition_screen"
}

@Composable
fun MorseXpressNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) { HomeScreen(navController) }
        composable(Routes.TEXT_INPUT) { TextInputScreen(navController) }
        composable(Routes.CAMERA_MENU) { CameraInputSelectorScreen(navController) }
        composable(Routes.CAMERA_CAPTURE) { CameraLivePreviewScreen(navController) }
//        composable(Routes.OCR_SCREEN) { OcrScreen(navController) }
//        composable(Routes.MORSE_RECOGNITION) { MorseRecognitionScreen(navController) }
        composable(
            route = "camera_result_screen/{photoUri}",
            arguments = listOf(navArgument("photoUri") { type = NavType.StringType })
        ) { backStackEntry ->
            val photoUriString = backStackEntry.arguments?.getString("photoUri")
            val photoUri = photoUriString?.toUri()
            CameraResultScreen(navController = navController, photoUri = photoUri)
        }
        composable(
            route = "ocr_screen/{photoUri}",
            arguments = listOf(navArgument("photoUri") { type = NavType.StringType })
        ) { backStackEntry ->
            val uri = Uri.parse(backStackEntry.arguments?.getString("photoUri"))
            OcrScreen(navController, uri)
        }

        composable(
            route = "morse_recognition_screen/{photoUri}",
            arguments = listOf(navArgument("photoUri") { type = NavType.StringType })
        ) { backStackEntry ->
            val uri = Uri.parse(backStackEntry.arguments?.getString("photoUri"))
            MorseRecognitionScreen(navController, uri) // puedes dejarlo en blanco de momento
        }
    }
}
