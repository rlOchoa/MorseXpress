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
                // Area de Historial
//                Button(
//                    onClick = { navController.navigate(Routes.HISTORY) },
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color.Transparent,
//                        contentColor = MaterialTheme.colorScheme.onBackground
//                    ),
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(12.dp)
//                ) {
//                    Icon(Icons.Default.History, contentDescription = "Historial")
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text("Ver Historial")
//                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val textColor = MaterialTheme.colorScheme.onBackground
                    val transparentColors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = textColor
                    )

                    // Text Input
                    Button(
                        onClick = { navController.navigate(Routes.TEXT_INPUT) },
                        colors = transparentColors,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Texto")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Entrada por Texto")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Camera Input
                    Button(
                        onClick = { navController.navigate(Routes.CAMERA_CAPTURE) },
                        colors = transparentColors,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Camera, contentDescription = "Cámara")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Entrada por Imagen o Cámara")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Audio Input
//                    Button(
//                        onClick = { navController.navigate(Routes.AUDIO_INPUT) },
//                        colors = transparentColors,
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        Icon(Icons.Default.Image, contentDescription = "Audio")
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Text("Entrada por Audio")
//                    }
                }
            }
        }
    }
}