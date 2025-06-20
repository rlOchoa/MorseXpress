package com.aria.morsexpress.presentation.screen.result

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraResultScreen(
    navController: NavController,
    photoUri: Uri?
) {
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    var bitmap by remember(photoUri) {
        mutableStateOf<Bitmap?>(null)
    }

    var recognizedText by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(photoUri) {
        try {
            photoUri?.let {
                context.contentResolver.openInputStream(it)?.use { stream ->
                    bitmap = BitmapFactory.decodeStream(stream)
                }
            }
        } catch (e: Exception) {
            Log.e("CameraResultScreen", "Error al cargar imagen desde URI", e)
        }
    }


    suspend fun <T> Task<T>.await(): T =
        suspendCancellableCoroutine { continuation ->
            addOnSuccessListener { result -> continuation.resume(result) }
            addOnFailureListener { exception -> continuation.resumeWithException(exception) }
        }

    suspend fun recognizeTextFromImage(bitmap: Bitmap): String {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.Builder().build())

        return withContext(Dispatchers.IO) {
            try {
                val result = recognizer.process(image).await() // Extension function used below
                result.text
            } catch (e: Exception) {
                "Error al reconocer texto: ${e.localizedMessage}"
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resultado de Captura") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            photoUri?.let {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap!!.asImageBitmap(),
                        contentDescription = "Foto Capturada",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(3f / 4f)
                    )
                } else {
                    Text(
                        text = "No se pudo cargar la imagen.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "¿Qué deseas hacer con esta imagen?",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        photoUri?.let {
                            navController.navigate("ocr_screen/${Uri.encode(it.toString())}")
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Detectar Texto")
                }

                Button(
                    onClick = {
                        photoUri?.let {
                            navController.navigate("morse_recognition_screen/${Uri.encode(it.toString())}")
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Detectar Morse")
                }
            }

            //Previous version of the code that displayed the image and recognized text
//            bitmap?.let {
//                Image(
//                    bitmap = it.asImageBitmap(),
//                    contentDescription = "Foto Capturada",
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .aspectRatio(3f / 4f)
//                )
//            } ?: Text("No se pudo cargar la imagen.")
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            if (isLoading) {
//                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
//            }
//
//            recognizedText?.let {
//                Text(
//                    text = "Texto detectado:\n$it",
//                    style = MaterialTheme.typography.bodyMedium
//                )
//            }
//
//            Button(
//                modifier = Modifier.fillMaxWidth(),
//                onClick = {
//                    bitmap?.let { bmp ->
//                        isLoading = true
//                        recognizedText = null
//                        coroutineScope.launch {
//                            recognizedText = recognizeTextFromImage(bmp)
//                            isLoading = false
//                        }
//                    }
//                }
//            ) {
//                Text("Analizar Imagen")
//            }
        }
    }
}