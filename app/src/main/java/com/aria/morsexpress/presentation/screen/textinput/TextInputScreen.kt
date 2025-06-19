package com.aria.morsexpress.presentation.screen.textinput

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aria.morsexpress.presentation.viewmodel.TranslationViewModel
import com.aria.morsexpress.presentation.viewmodel.TranslationViewModelFactory
import androidx.compose.ui.platform.LocalContext
import com.aria.morsexpress.data.local.database.AppDatabase


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
            "-.---" to 'Á', "..-.." to 'É', "..---" to 'Í', "---." to 'Ó', "..-" to 'Ú'
        )
        return morse.trim().split(" ").map { reverseMap[it] ?: '?' }.joinToString("")
    }

    LaunchedEffect(inputText.text, isMorseToText) {
        outputText = if (isMorseToText) {
            convertMorseToText(inputText.text)
        } else {
            convertTextToMorse(inputText.text)
        }

        viewModel.saveTranslation(
            input = inputText.text,
            output = outputText,
            inputType = if (isMorseToText) "Morse" else "Texto"
        )
    }

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
                        Icon(Icons.Default.SwapHoriz, contentDescription = "Cambiar dirección")
                    }
                }
            )
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
                onValueChange = { inputText = it },
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