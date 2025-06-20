package com.aria.morsexpress.presentation.screen.imageinput

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.compose.ui.viewinterop.AndroidView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraLivePreviewScreen(navController: NavController) {
    val context = LocalContext.current
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    LaunchedEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = androidx.camera.core.Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val cameraSelector = androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    context as androidx.lifecycle.LifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                Toast.makeText(context, "No se pudo iniciar la cámara", Toast.LENGTH_SHORT).show()
                Log.e("CameraX", "Error: ${e.message}", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun takePhoto(onImageCaptured: (Uri) -> Unit) {
        val photoFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        photoFile
                    )
                    capturedImageUri = savedUri
                    onImageCaptured(savedUri)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraX", "Fallo al capturar imagen", exception)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Cámara en Vivo") })
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    takePhoto { uri ->
                        Handler(Looper.getMainLooper()).post {
                            navController.navigate("camera_result_screen/${Uri.encode(uri.toString())}")
                        }
                    }
                }) {
                    Icon(Icons.Default.Camera, contentDescription = "Capturar Imagen")
                }

                IconButton(
                    enabled = capturedImageUri != null,
                    onClick = {
                        capturedImageUri?.let {
                            Handler(Looper.getMainLooper()).post {
                                navController.navigate("camera_result_screen/${Uri.encode(it.toString())}")
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Traducir Imagen")
                }
            }
        }
    ) { padding ->
        AndroidView(
            factory = { previewView },
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        )
    }
}