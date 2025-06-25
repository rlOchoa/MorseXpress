package com.aria.morsexpress.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aria.morsexpress.data.local.dao.TranslationDao
import com.aria.morsexpress.data.local.entity.TranslationEntity
import com.aria.morsexpress.util.AudioDecoder
import com.aria.morsexpress.util.MorseAudioAnalyzer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AudioInputViewModel(
    private val dao: TranslationDao
) : ViewModel() {

    private val _detectedMorse = MutableStateFlow("")
    val detectedMorse = _detectedMorse.asStateFlow()

    private val _translatedText = MutableStateFlow("")
    val translatedText = _translatedText.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing = _isAnalyzing.asStateFlow()

    fun analyzeAudioFromUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            _isAnalyzing.value = true
            try {
                val decoded = AudioDecoder.decodeWavToPCM(context, uri)
                if (decoded != null) {
                    val morse = MorseAudioAnalyzer.extractMorseFromAudio(decoded.pcmData, decoded.sampleRate)
                    _detectedMorse.value = morse
                    _translatedText.value = MorseCodeTranslator.toText(morse)
                } else {
                    _detectedMorse.value = ""
                    _translatedText.value = "Error al analizar el audio"
                }
            } catch (e: Exception) {
                _detectedMorse.value = ""
                _translatedText.value = "Error inesperado"
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    fun saveTranslation() {
        viewModelScope.launch {
            dao.insert(
                TranslationEntity(
                    originalText = _translatedText.value,
                    translatedText = _detectedMorse.value,
                    inputType = "AUDIO",
                    timestamp = System.currentTimeMillis(),
                    inputPathOrContent = "",
                    morseCode = _detectedMorse.value
                )
            )
        }
    }
}

object MorseCodeTranslator {

    private val morseMap = mapOf(
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
        'Á' to ".-.-", 'É' to "..-..", 'Í' to "..--", 'Ó' to "---."
    )

    private val inverseMorseMap = morseMap.entries.associate { it.value to it.key }

    fun toMorse(text: String): String {
        return text.uppercase().mapNotNull {
            morseMap[it]
        }.joinToString(" ")
    }

    fun toText(morse: String): String {
        return morse.trim().split(" ").mapNotNull {
            inverseMorseMap[it]
        }.joinToString("")
    }
}