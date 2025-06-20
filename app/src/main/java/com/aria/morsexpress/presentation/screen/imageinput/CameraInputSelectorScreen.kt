
package com.aria.morsexpress.presentation.screen.imageinput

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aria.morsexpress.R
import com.aria.morsexpress.presentation.navigation.Routes

@Composable
fun CameraInputSelectorScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Selecciona el tipo de análisis",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        AnimatedOptionCard(
            iconRes = R.drawable.ic_ocr,
            title = "Texto a Morse",
            description = "Detecta texto desde una imagen y tradúcelo a código Morse.",
            onClick = { navController.navigate(Routes.OCR_SCREEN) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedOptionCard(
            iconRes = R.drawable.ic_morse,
            title = "Morse a Texto",
            description = "Detecta patrones visuales de puntos y rayas y tradúcelos.",
            onClick = { navController.navigate(Routes.MORSE_RECOGNITION) } // Go to Morse recognition screen
        )

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedOptionCard(
            iconRes = R.drawable.ic_camera,
            title = "Captura de Imagen",
            description = "Captura una imagen directamente desde la cámara.",
            onClick = { navController.navigate(Routes.CAMERA_CAPTURE) } // Go to camera usage screen
        )
    }
}

@Composable
fun AnimatedOptionCard(
    iconRes: Int,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    val scale by rememberInfiniteTransition(label = "scaleAnim").animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "scaleAnimSpec"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                modifier = Modifier.size(60.dp)
            )

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@Composable
fun AnimatedSectionTitle(
    iconRes: Int,
    title: String,
    iconRes2: Int
) {
    val scale by rememberInfiniteTransition(label = "scaleAnim").animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "scaleAnimSpec"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .scale(scale)
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = title,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.width(12.dp))

        Image(
            painter = painterResource(id = iconRes2),
            contentDescription = title,
            modifier = Modifier.size(32.dp)
        )
    }
}
