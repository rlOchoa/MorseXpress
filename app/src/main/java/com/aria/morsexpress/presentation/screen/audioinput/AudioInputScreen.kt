package com.aria.morsexpress.presentation.screen.audioinput

import android.media.MediaPlayer
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aria.morsexpress.data.local.database.AppDatabase
import com.aria.morsexpress.presentation.viewmodel.AudioInputViewModel
import com.aria.morsexpress.presentation.viewmodel.AudioTranslationViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AudioInputScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel: AudioInputViewModel = viewModel(
        factory = AudioTranslationViewModelFactory(
            AppDatabase.getInstance(context).translationDao()
        )
    )
    val scope = rememberCoroutineScope()

    var audioUri by remember { mutableStateOf<Uri?>(null) }

    val detectedMorse by viewModel.detectedMorse.collectAsState()
    val translatedText by viewModel.translatedText.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()

    val selectAudioLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri -> audioUri = uri }
    )

    val exportTextLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain"),
        onResult = { uri ->
            uri?.let {
                try {
                    val outputStream = context.contentResolver.openOutputStream(uri)
                    outputStream?.bufferedWriter()?.use { writer ->
                        writer.write("Texto traducido:\n$translatedText\n\nMorse detectado:\n$detectedMorse")
                    }
                    Toast.makeText(context, "Exportación exitosa", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Error al exportar archivo", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text("Reconocimiento desde Audio", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                selectAudioLauncher.launch(arrayOf("audio/wav", "audio/x-wav", "audio/mpeg"))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.AudioFile, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Seleccionar archivo de audio")
        }

        Spacer(modifier = Modifier.height(16.dp))

        audioUri?.let { uri ->

            Button(
                onClick = {
                    MediaPlayer().apply {
                        setDataSource(context, uri)
                        prepare()
                        start()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reproducir Audio")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        try {
                            context.contentResolver.openInputStream(uri)?.let { stream ->
                                viewModel.analyzeAudioFromUri(context, uri)
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error al analizar audio", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isAnalyzing
            ) {
                Text("Analizar y traducir")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (isAnalyzing) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        if (translatedText.isNotBlank()) {
            Text("Texto traducido:\n$translatedText", style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Justify)
        }

        if (detectedMorse.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Código Morse:\n$detectedMorse", style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Justify)

            Button(
                onClick = {
                    viewModel.saveTranslation()
                    Toast.makeText(context, "Traducción guardada", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
            ) {
                Icon(Icons.Default.Save, contentDescription = "Guardar")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Guardar Traducción")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val fileName = "audio_morse_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.txt"
                    exportTextLauncher.launch(fileName)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Exportar como .txt")
            }
        }
    }
}
