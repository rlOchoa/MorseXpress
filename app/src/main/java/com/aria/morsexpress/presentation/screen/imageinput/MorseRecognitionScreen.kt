package com.aria.morsexpress.presentation.screen.imageinput

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aria.morsexpress.data.local.database.AppDatabase
import com.aria.morsexpress.presentation.viewmodel.TranslationViewModel
import com.aria.morsexpress.presentation.viewmodel.TranslationViewModelFactory
import com.aria.morsexpress.util.RobustMorseDetector
import com.aria.morsexpress.util.VisualMorseAnalyzer
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.regex.Pattern

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MorseRecognitionScreen(
    navController: NavController,
    photoUri: Uri? = null
) {
    val context = LocalContext.current
    val dao = AppDatabase.getInstance(context).translationDao()
    val viewModel: TranslationViewModel = viewModel(factory = TranslationViewModelFactory(dao))

    val scope = rememberCoroutineScope()

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var recognizedMorse by remember { mutableStateOf("") }
    var translatedText by remember { mutableStateOf("") }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri -> uri?.let { imageUri = it } }
    )

    val activeUri = photoUri ?: imageUri

    LaunchedEffect(activeUri) {
        activeUri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            imageBitmap = bitmap
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reconocer Código Morse Visual") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (activeUri != null && imageBitmap != null) {
                Image(
                    bitmap = imageBitmap!!.asImageBitmap(),
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))


                var settings by remember { mutableStateOf(VisualMorseAnalyzer.Settings()) }

                MorseSettingsControls(settings = settings, onChange = { settings = it })

                // Visual Analysis with: Bitmapping, GrayScale, Binarization, and Morse Detection
                Button(onClick = {
                    scope.launch {
                        if (imageBitmap != null) {
                            val rawMorse = VisualMorseAnalyzer.analyze(imageBitmap!!, settings)
                            recognizedMorse = rawMorse
                            translatedText = rawMorse.toText()
                            viewModel.insertTranslation(
                                originalText = rawMorse,
                                translatedText = translatedText,
                                inputType = "MORSE_IMAGE",
                                inputPathOrContent = activeUri.toString(),
                                morseCode = rawMorse
                            )
                        } else {
                            recognizedMorse = "No se detectó imagen"
                        }
                    }
                }) {
                    Text("Traducir Morse Visual")
                }

                // Regex Try
//                Button(onClick = {
//                    scope.launch {
//                        val result = runCatching {
//                            val ocrText = extractMorseFromImage(context, activeUri)
//                            recognizedMorse = ocrText
//                            translatedText = ocrText.toText()
//                            viewModel.insertTranslation(
//                                originalText = ocrText,
//                                translatedText = translatedText,
//                                inputType = "MORSE_IMAGE",
//                                inputPathOrContent = activeUri.toString(),
//                                morseCode = ocrText
//                            )
//                        }
//                        if (result.isFailure) {
//                            recognizedMorse = "No se pudo reconocer el patrón."
//                        }
//                    }
//                }) {
//                    Text("Traducir Morse Visual")
//                }
            } else {
                Button(onClick = { galleryLauncher.launch(arrayOf("image/*")) }) {
                    Icon(Icons.Default.Image, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Seleccionar imagen")
                }
            }

            if (recognizedMorse.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Código Morse Reconocido:", style = MaterialTheme.typography.titleMedium)
                Text(text = recognizedMorse, textAlign = TextAlign.Center)
            }

            if (translatedText.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Texto Traducido:", style = MaterialTheme.typography.titleMedium)
                Text(text = translatedText, textAlign = TextAlign.Center)
            }
        }
    }
}

//private suspend fun extractMorseFromImage(context: android.content.Context, uri: Uri): String {
//    val inputImage = InputImage.fromFilePath(context, uri)
//    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
//
//    return suspendCancellableCoroutine { continuation ->
//        recognizer.process(inputImage)
//            .addOnSuccessListener { visionText ->
//                val rawText = visionText.text
//                val extracted = RobustMorseDetector.extractMorseLines(rawText)
//                continuation.resume(extracted, null)
//            }
//            .addOnFailureListener {
//                continuation.resume("", null)
//            }
//    }
//}

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
        "-.---" to 'Á', "..-.." to 'É', "..---" to 'Í', "---." to 'Ó'
    )
    return this.trim().split(" ").map { reverseMap[it] ?: '?' }.joinToString("")
}

@Composable
fun MorseSettingsControls(
    settings: VisualMorseAnalyzer.Settings,
    onChange: (VisualMorseAnalyzer.Settings) -> Unit
) {
    Column {
        Text("Configuración de Análisis", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        SettingSlider("Umbral binarización", settings.threshold.toFloat(), 0f, 255f) {
            onChange(settings.copy(threshold = it.toInt()))
        }

        SettingSlider("Tamaño máximo punto", settings.dotMaxLength.toFloat(), 1f, 20f) {
            onChange(settings.copy(dotMaxLength = it.toInt()))
        }

        SettingSlider("Tamaño mínimo raya", settings.dashMinLength.toFloat(), 1f, 30f) {
            onChange(settings.copy(dashMinLength = it.toInt()))
        }

        SettingSlider("Espacio letra mínimo", settings.letterSpaceMinLength.toFloat(), 1f, 20f) {
            onChange(settings.copy(letterSpaceMinLength = it.toInt()))
        }

        SettingSlider("Espacio palabra mínimo", settings.wordSpaceMinLength.toFloat(), 5f, 50f) {
            onChange(settings.copy(wordSpaceMinLength = it.toInt()))
        }

        SettingSlider("Saltos entre líneas", settings.scanStep.toFloat(), 1f, 20f) {
            onChange(settings.copy(scanStep = it.toInt()))
        }

        SettingSlider("Líneas a escanear", settings.linesToScan.toFloat(), 1f, 10f) {
            onChange(settings.copy(linesToScan = it.toInt()))
        }
    }
}

@Composable
fun SettingSlider(
    label: String,
    value: Float,
    valueRangeStart: Float,
    valueRangeEnd: Float,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text("$label: ${value.toInt()}", style = MaterialTheme.typography.bodyMedium)
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRangeStart..valueRangeEnd
        )
    }
}
