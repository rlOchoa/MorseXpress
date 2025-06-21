package com.aria.morsexpress.presentation.screen.imageinput

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aria.morsexpress.data.local.database.AppDatabase
import com.aria.morsexpress.presentation.viewmodel.TranslationViewModel
import com.aria.morsexpress.presentation.viewmodel.TranslationViewModelFactory
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.OutputStream
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OcrScreen(navController: NavController, photoUri: Uri? = null) {
    val context = LocalContext.current
    val dao = AppDatabase.getInstance(context).translationDao()
    val viewModel: TranslationViewModel = viewModel(
        factory = TranslationViewModelFactory(dao)
    )
//    val db = remember { AppDatabase.getInstance(context).translationDao() }

    val imageUriState = remember { mutableStateOf(photoUri) }
    val resultTextState = remember { mutableStateOf("") }
    val bitmapState = remember { mutableStateOf<Bitmap?>(null) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var recognizedText by remember { mutableStateOf<String?>(null) }
    var morseTranslation by remember { mutableStateOf<String?>(null) }

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
                        writer.write("Texto original:\n$recognizedText\n\nCódigo Morse:\n$morseTranslation")
                    }
                    Toast.makeText(context, "Archivo exportado", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Error al exportar archivo", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )

    LaunchedEffect(photoUri) {
        photoUri?.let {
            val stream = context.contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(stream)
            stream?.close()
            bitmapState.value = bitmap

            bitmap?.let { bmp ->
                val image = InputImage.fromBitmap(bmp, 0)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        resultTextState.value = visionText.text
                    }
                    .addOnFailureListener {
                        resultTextState.value = "Error al procesar la imagen"
                    }
            }
        }
    }

    LaunchedEffect(imageUri) {
        imageUri?.let { uri ->
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val inputStream = context.contentResolver.openInputStream(uri)
            inputStream?.use {
                val bitmap = BitmapFactory.decodeStream(it)
                val inputImage = InputImage.fromBitmap(bitmap, 0)
                recognizer.process(inputImage)
                    .addOnSuccessListener { visionText ->
                        recognizedText = visionText.text
                        morseTranslation = null
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            imageUriState.value?.let {
                bitmapState.value?.let { bmp ->
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "Imagen OCR",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            } ?: Button(onClick = {
                galleryLauncher.launch(arrayOf("image/*"))
            }) {
                Icon(Icons.Default.Image, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Seleccionar imagen")
            }

            if (resultTextState.value.isNotBlank()) {
                Text(text = "Texto reconocido:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(resultTextState.value)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    val morse = resultTextState.value.toMorse()
                    morseTranslation = morse
//                    morseTranslation = resultTextState.value.toMorse()
//                    morseTranslation?.let { morse ->
                    viewModel.insertTranslation(
                        originalText = resultTextState.value,
                        translatedText = morse,
                        inputType = "OCR",
                        inputPathOrContent = photoUri?.toString() ?: "desconocido",
                        morseCode = morse
                    )
                    Toast.makeText(context, "Guardado en historial", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(Icons.Default.Translate, contentDescription = "Traducir")
                    Spacer(Modifier.width(8.dp))
                    Text("Traducir a Morse")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
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
                            .height(200.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            recognizedText?.let {
                Text("Texto reconocido:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(it)

                Spacer(modifier = Modifier.height(16.dp))
                // Reemplaza este bloque dentro del onClick del botón "Traducir a Morse"
                Button(onClick = {
                    val morse = it.toMorse()
                    morseTranslation = morse
                    viewModel.insertTranslation(
                        originalText = it,
                        translatedText = morse,
                        inputType = "Imagen",
                        inputPathOrContent = imageUri?.toString() ?: "desconocido",
                        morseCode = morse
                    )
                    Toast.makeText(context, "Guardado en historial", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(Icons.Default.Translate, contentDescription = "Traducir")
                    Spacer(Modifier.width(8.dp))
                    Text("Traducir a Morse")
                }
            }

            morseTranslation?.let {
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
        /* Old code
            Button(onClick = { galleryLauncher.launch(arrayOf("image/*")) }) {
                Text("Seleccionar Imagen")
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
                            .height(200.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.height(16.dp))
        */*/
    }
}

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