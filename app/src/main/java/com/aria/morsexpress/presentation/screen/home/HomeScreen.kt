package com.aria.morsexpress.presentation.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aria.morsexpress.R
import com.aria.morsexpress.presentation.navigation.Routes
import com.aria.morsexpress.presentation.screen.imageinput.AnimatedOptionCard
import com.aria.morsexpress.presentation.screen.imageinput.AnimatedSectionTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            AnimatedSectionTitle(
                iconRes = R.drawable.ic_morse,
                title = "MorseXpress",
                iconRes2 = R.drawable.ic_morse
            )
        },
        bottomBar = {
            BottomAppBar {
                AnimatedOptionCard(
                    iconRes = R.drawable.ic_history,
                    title = "Historial",
                    description = "Revisa tus traducciones anteriores.",
                    onClick = { navController.navigate(Routes.HISTORY_SCREEN) }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AnimatedOptionCard(
                iconRes = R.drawable.ic_pencil,
                title = "Entrada por Texto",
                description = "Escribe o pega texto para traducirlo.",
                onClick = { navController.navigate(Routes.TEXT_INPUT) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedOptionCard(
                iconRes = R.drawable.ic_camera,
                title = "Entrada por Imagen o Cámara",
                description = "Inserta o toma una imágen o fotografía para analizar su contenido.",
                onClick = { navController.navigate(Routes.CAMERA_MENU) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedOptionCard(
                iconRes = R.drawable.ic_audio,
                title = "Entrada por Audio",
                description = "Graba o selecciona un archivo de audio para traducirlo.",
                onClick = { }
            )
        }
    }
}