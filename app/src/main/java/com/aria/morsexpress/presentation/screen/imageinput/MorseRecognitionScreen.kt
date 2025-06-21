package com.aria.morsexpress.presentation.screen.imageinput

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
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
    var bitmapState = remember { mutableStateOf<Bitmap?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri -> uri?.let { imageUri = it } }
    )

    val imageBitmap = remember(photoUri) {
        photoUri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            bitmap
        }
    }

    LaunchedEffect(imageUri) {
        imageUri?.let {
            val stream = context.contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(stream)
            stream?.close()
            bitmapState.value = bitmap
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
            imageUri?.let {
                bitmapState.value?.let { bmp ->
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "Imagen seleccionada",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    scope.launch {
                        val result = runCatching {
                            val ocrText = extractMorseFromImage(context, it)
                            recognizedMorse = ocrText
                            translatedText = ocrText.toText()
                            viewModel.insertTranslation(
                                originalText = ocrText,
                                translatedText = translatedText,
                                inputType = "MORSE_IMAGE",
                                inputPathOrContent = it.toString(),
                                morseCode = ocrText
                            )
                        }
                        if (result.isFailure) {
                            recognizedMorse = "No se pudo reconocer el patrón."
                        }
                    }
                }) {
                    Text("Traducir Morse Visual")
                }
            } ?: Button(onClick = {
                galleryLauncher.launch(arrayOf("image/*"))
            }) {
                Icon(Icons.Default.Image, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Seleccionar imagen")
            }

            imageBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Imagen Morse",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    scope.launch {
                        val result = runCatching {
                            val ocrText = extractMorseFromImage(context, photoUri!!)
                            recognizedMorse = ocrText
                            translatedText = ocrText.toText()
                            viewModel.insertTranslation(
                                originalText = ocrText,
                                translatedText = translatedText,
                                inputType = "MORSE_IMAGE",
                                inputPathOrContent = photoUri.toString(),
                                morseCode = ocrText
                            )
                        }
                        if (result.isFailure) {
                            recognizedMorse = "No se pudo reconocer el patrón."
                        }
                    }
                }) {
                    Text("Traducir Morse Visual")
                }
            } ?: Text("No se ha seleccionado imagen")

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

private suspend fun extractMorseFromImage(context: android.content.Context, uri: Uri): String {
    val inputImage = InputImage.fromFilePath(context, uri)
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    return suspendCancellableCoroutine { continuation ->
        recognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                val raw = visionText.text
                val cleaned = raw.trim().replace("\\s+".toRegex(), " ")
                val morsePattern = Pattern.compile("[.-]+|/|")
                val matcher = morsePattern.matcher(cleaned)
                val extracted = buildString {
                    while (matcher.find()) {
                        append(matcher.group()).append(" ")
                    }
                }.trim()
                continuation.resume(extracted, onCancellation = null)
            }
            .addOnFailureListener {
                continuation.resume("", onCancellation = null)
            }
    }
}

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
        "-.---" to 'Á', "..-.." to 'É', "..---" to 'Í', "---." to 'Ó', "..-" to 'Ú'
    )
    return this.trim().split(" ").map { reverseMap[it] ?: '?'}.joinToString("")
}