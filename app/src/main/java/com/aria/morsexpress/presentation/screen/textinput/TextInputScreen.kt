package com.aria.morsexpress.presentation.screen.textinput

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aria.morsexpress.data.local.database.AppDatabase
import com.aria.morsexpress.presentation.viewmodel.TranslationViewModel
import com.aria.morsexpress.presentation.viewmodel.TranslationViewModelFactory


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextInputScreen(
    navController: NavController,
    context: Context = LocalContext.current
) {
    val db = remember { AppDatabase.getInstance(context) }
    val dao = remember { db.translationDao() }

    val viewModel: TranslationViewModel = viewModel(
        factory = TranslationViewModelFactory(dao)
    )

    var inputText by remember { mutableStateOf(TextFieldValue("")) }
    var isMorseToText by remember { mutableStateOf(false) }
    var outputText by remember { mutableStateOf("") }

    fun convertTextToMorse(text: String): String {
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
        return text.uppercase().map { morseMap[it] ?: "" }.joinToString(" ")
    }

    fun convertMorseToText(morse: String): String {
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
        return morse.trim().split(" ").map { reverseMap[it] ?: '?' }.joinToString("")
    }

//    LaunchedEffect(inputText.text, isMorseToText) {
//        outputText = if (isMorseToText) {
//            convertMorseToText(inputText.text)
//        } else {
//            convertTextToMorse(inputText.text)
//        }
//
//        // Save the translation to the database
//        viewModel.insertTranslation(
//            originalText = inputText.text,
//            translatedText = outputText,
//            inputType = if (isMorseToText) "Texto" else "Morse",
//            inputPathOrContent = "", // Not applicable for text input
//            morseCode = if (isMorseToText) outputText else "" // Save morse code only if converting to text
//        )
//    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Entrada de texto") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isMorseToText = !isMorseToText
                    }) {
                        Icon(
                            Icons.Default.SwapHoriz,
                            contentDescription = "Cambiar tipo de traducción"
                        )
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    viewModel.insertTranslation(
                        originalText = inputText.text,
                        translatedText = outputText,
                        inputType = if (isMorseToText) "MORSE" else "TEXT",
                        inputPathOrContent = "",
                        morseCode = if (isMorseToText) inputText.text else outputText
                    )
                }) {
                    Icon(Icons.Default.Translate, contentDescription = "Traducir")
                    Spacer(Modifier.width(8.dp))
                    Text("Guardar traducción")
                }

                Button(onClick = {
                    val fileName = "traduccion_morse_texto_${System.currentTimeMillis()}.txt"
                    val fileOutput = context.openFileOutput(fileName, Context.MODE_PRIVATE)
                    fileOutput.use {
                        it.write(outputText.toByteArray())
                    }
                }) {
                    Icon(Icons.Default.Upload, contentDescription = "Exportar")
                    Spacer(Modifier.width(8.dp))
                    Text("Exportar a .txt")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = {
                    inputText = it
                    outputText = if (isMorseToText)
                        convertMorseToText(it.text)
                    else
                        convertTextToMorse(it.text)
                },
                label = { Text(if (isMorseToText) "Código Morse" else "Texto plano") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Resultado:",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = outputText,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}