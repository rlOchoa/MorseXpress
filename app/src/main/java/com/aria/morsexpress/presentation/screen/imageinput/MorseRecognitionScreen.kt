package com.aria.morsexpress.presentation.screen.imageinput

import androidx.compose.foundation.layout.padding
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
            TopAppBar(title = { Text("Morse Visual a Texto") })
        }
    ) { paddingValues ->
        // Aquí se integrará el análisis de patrones visuales de puntos y rayas
        Text(
            text = "Pantalla de reconocimiento visual de Morse",
            modifier = Modifier.padding(paddingValues).padding(16.dp)
        )
    }
}
