package com.aria.morsexpress.presentation.screen.imageinput

import android.app.Activity
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.InputStream
import java.io.OutputStream
import java.util.Date
import java.util.Locale

private fun String.toMorse(): String {
    val morseMap = mapOf(
        // Letters
        'A' to ".-", 'B' to "-...", 'C' to "-.-.", 'D' to "-..", 'E' to ".",
        'F' to "..-.", 'G' to "--.", 'H' to "....", 'I' to "..", 'J' to ".---",
        'K' to "-.-", 'L' to ".-..", 'M' to "--", 'N' to "-.", 'Ñ' to "--.--",
        'O' to "---", 'P' to ".--.", 'Q' to "--.-", 'R' to ".-.", 'S' to "...",
        'T' to "-", 'U' to "..-", 'V' to "...-", 'W' to ".--", 'X' to "-..-",
        'Y' to "-.--", 'Z' to "--..",
        // Numbers
        '0' to "-----", '1' to ".----", '2' to "..---", '3' to "...--", '4' to "....-",
        '5' to ".....", '6' to "-....", '7' to "--...", '8' to "---..", '9' to "----.",
        // Spaces
        ' ' to "/", '\n' to "\n",
        // Punctuation
        '.' to ".-.-.-", ',' to "--..--", '?' to "..--..", '\'' to ".----.",
        '!' to "-.-.--", '/' to "-..-.", '(' to "-.--.", ')' to "-.--.-",
        '&' to ".-...", ':' to "---...", ';' to "-.-.-.", '=' to "-...-",
        '+' to ".-.-.", '-' to "-....-", '_' to "..--.-", '"' to ".-..-.",
        '$' to "...-..-", '@' to ".--.-.",
        // Latin Accents
        'Á' to ".-.-", 'É' to "..-..", 'Í' to "..--", 'Ó' to "---.", 'Ú' to "..-"
    )
    return this.uppercase().map { morseMap[it] ?: "" }.joinToString(" ")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageInputScreen(navController: NavController) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var recognizedText by remember { mutableStateOf<String?>(null) }
    var morseResult by remember { mutableStateOf<String?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri -> uri?.let { imageUri = it } }
    )

    val createFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain"),
        onResult = { uri: Uri? ->
            uri?.let {
                try {
                    val outputStream: OutputStream? = context.contentResolver.openOutputStream(it)
                    outputStream?.bufferedWriter()?.use { writer ->
                        writer.write("Texto original:\n$recognizedText\n\nCódigo Morse:\n$morseResult")
                    }
                    Toast.makeText(context, "Archivo exportado", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Error al exportar archivo", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val uri = result.data?.data
            if (result.resultCode == Activity.RESULT_OK && uri != null) {
                imageUri = uri
            }
        }
    )

    LaunchedEffect(imageUri) {
        imageUri?.let { uri ->
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream?.use {
                val bitmap = BitmapFactory.decodeStream(it)
                val inputImage = InputImage.fromBitmap(bitmap, 0)
                recognizer.process(inputImage)
                    .addOnSuccessListener { visionText ->
                        recognizedText = visionText.text
                        morseResult = null // Reset Morse result
                    }
                    .addOnFailureListener {
                        recognizedText = "Error al procesar la imagen."
                    }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("OCR desde Imagen") },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                galleryLauncher.launch(arrayOf("image/*"))
            }) {
                Text("Seleccionar imagen")
            }

            Spacer(modifier = Modifier.height(16.dp))

            imageUri?.let {
                val bitmap = context.contentResolver.openInputStream(it)?.use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Imagen seleccionada",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            recognizedText?.let {
                Text("Texto reconocido:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(it)
                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    morseResult = it.toMorse()
                }) {
                    Text("Convertir a Morse")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            morseResult?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Resultado en Morse:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(it)

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    val date = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                    createFileLauncher.launch("morse_$date.txt")
                }) {
                    Text("Exportar como .txt")
                }
            }
        }
    }
}