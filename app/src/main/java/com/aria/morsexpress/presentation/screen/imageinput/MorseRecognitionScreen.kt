package com.aria.morsexpress.presentation.screen.imageinput

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MorseRecognitionScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Morse Visual a Texto") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
            })
        }
    ) { paddingValues ->
        // TODO IMPLEMENTAR LA LÓGICA DE RECONOCIMIENTO VISUAL DE MORSE AQUÍ
        Text(
            text = "Placeholder para la pantalla de reconocimiento visual de Morse",
            modifier = Modifier.padding(paddingValues).padding(16.dp)
        )
    }
}

private fun String.toText(): String {
    val reverseMap = mapOf(
        // Letters
        ".-" to 'A', "-..." to 'B', "-.-." to 'C', "-.." to 'D', "." to 'E',
        "..-." to 'F', "--." to 'G', "...." to 'H', ".." to 'I', ".---" to 'J',
        "-.-" to 'K', ".-.." to 'L', "--" to 'M', "-." to 'N', "--.--" to 'Ñ',
        "---" to 'O', ".--." to 'P', "--.-" to 'Q', ".-." to 'R', "..." to 'S',
        "-" to 'T', "..-" to 'U', "...-" to 'V', ".--" to 'W', "-..-" to 'X',
        "-.--" to 'Y', "--.." to 'Z',
        // Numbers
        "-----" to '0', ".----" to '1', "..---" to '2', "...--" to '3', "....-" to '4',
        "....." to '5', "-...." to '6', "--..." to '7', "---.." to '8', "----." to '9',
        // Space
        "/" to ' ', "\n" to '\n',
        // Punctuation
        ".-.-.-" to '.', "--..--" to ',', "..--.." to '?', ".----." to '\'',
        "-.-.--" to '!', "-..-." to '/', "-.--." to '(', "-.--.-" to ')',
        ".-..." to '&', "---..." to ':', "-.-.-." to ';', "-...-" to '=',
        ".-.-." to '+', "-....-" to '-', "..--.-" to '_', ".-..-." to '"',
        "...-..-" to '$', ".--.-." to '@',
        // Latin Accents
        "-.---" to 'Á', "..-.." to 'É', "..---" to 'Í', "---." to 'Ó', "..-" to 'Ú'
    )
    return this.trim().split(" ").map { reverseMap[it] ?: '?'}.joinToString("")
}