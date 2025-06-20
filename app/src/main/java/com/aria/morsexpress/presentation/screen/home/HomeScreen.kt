package com.aria.morsexpress.presentation.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aria.morsexpress.R
import com.aria.morsexpress.presentation.navigation.Routes
import com.aria.morsexpress.presentation.screen.imageinput.AnimatedOptionCard
import com.aria.morsexpress.presentation.screen.imageinput.AnimatedSectionTitle
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = { AnimatedSectionTitle(
            iconRes = R.drawable.ic_morse,
            title = "MorseXpress",
            iconRes2 = R.drawable.ic_morse
        ) },
        bottomBar = {
            BottomAppBar {
                AnimatedOptionCard(
                    iconRes = R.drawable.ic_history,
                    title = "Historial",
                    description = "Revisa tus traducciones anteriores.",
                    onClick = {  }
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
                onClick = { navController.navigate(Routes.CAMERA_CAPTURE) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedOptionCard(
                iconRes = R.drawable.ic_audio,
                title = "Entrada por Audio",
                description = "Graba o selecciona un archivo de audio para traducirlo.",
                onClick = {  }
            )

//            Card(
//                modifier = Modifier
//                    .padding(24.dp)
//                    .fillMaxWidth(),
//                elevation = CardDefaults.cardElevation(8.dp)
//            ) {
//                Column(
//                    modifier = Modifier
//                        .padding(24.dp)
//                        .fillMaxWidth(),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    val textColor = MaterialTheme.colorScheme.onBackground
//                    val transparentColors = ButtonDefaults.buttonColors(
//                        containerColor = Color.Transparent,
//                        contentColor = textColor
//                    )
//
//                    // Text Input
//                    Button(
//                        onClick = { navController.navigate(Routes.TEXT_INPUT) },
//                        colors = transparentColors,
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        Icon(Icons.Default.Edit, contentDescription = "Texto")
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Text("Entrada por Texto")
//                    }
//
//
//                    Spacer(modifier = Modifier.height(24.dp))
//
//                    // Camera Input
//                    Button(
//                        onClick = { navController.navigate(Routes.CAMERA_CAPTURE) },
//                        colors = transparentColors,
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        Icon(Icons.Default.Camera, contentDescription = "Cámara")
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Text("Entrada por Imagen o Cámara")
//                    }
//
//
//                    Spacer(modifier = Modifier.height(16.dp))
//
//                    // Audio Input
////                    Button(
////                        onClick = { navController.navigate(Routes.AUDIO_INPUT) },
////                        colors = transparentColors,
////                        modifier = Modifier.fillMaxWidth()
////                    ) {
////                        Icon(Icons.Default.Image, contentDescription = "Audio")
////                        Spacer(modifier = Modifier.width(8.dp))
////                        Text("Entrada por Audio")
//                    }
//                }
//            }
        }
    }
}