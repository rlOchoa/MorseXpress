package com.aria.morsexpress.presentation.screen.imageinput

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aria.morsexpress.presentation.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraCaptureScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Captura con Cámara") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Selecciona qué tipo de reconocimiento deseas realizar:")

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { navController.navigate(Routes.OCR_SCREEN) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reconocer texto en imagen")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { navController.navigate(Routes.MORSE_RECOGNITION) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reconocer código Morse visual")
            }
        }
    }
}