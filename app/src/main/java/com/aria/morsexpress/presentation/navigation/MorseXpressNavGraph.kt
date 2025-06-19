package com.aria.morsexpress.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aria.morsexpress.presentation.screen.home.HomeScreen
import com.aria.morsexpress.presentation.screen.imageinput.ImageInputScreen
import com.aria.morsexpress.presentation.screen.textinput.TextInputScreen
import com.aria.morsexpress.presentation.screen.imageinput.CameraCaptureScreen
import com.aria.morsexpress.presentation.screen.imageinput.OcrScreen
import com.aria.morsexpress.presentation.screen.imageinput.MorseRecognitionScreen

object Routes {
    const val HOME = "home"
    const val TEXT_INPUT = "text_input"
    const val IMAGE_INPUT = "image_input"
    const val CAMERA_INPUT = "camera_input"
    const val CAMERA_CAPTURE = "camera_capture_screen"
    const val OCR_SCREEN = "ocr_screen"
    const val MORSE_RECOGNITION = "morse_recognition_screen"
}

@Composable
fun MorseXpressNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) { HomeScreen(navController) }
        composable(Routes.TEXT_INPUT) { TextInputScreen(navController) }
        composable(Routes.IMAGE_INPUT) { ImageInputScreen(navController) }
        composable(Routes.CAMERA_INPUT) { CameraCaptureScreen(navController) }
        composable(Routes.CAMERA_CAPTURE) { CameraCaptureScreen(navController) }
        composable(Routes.OCR_SCREEN) { OcrScreen(navController) }
        composable(Routes.MORSE_RECOGNITION) { MorseRecognitionScreen(navController) }
    }
}
