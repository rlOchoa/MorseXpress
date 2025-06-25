package com.aria.morsexpress.presentation.viewmodel

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aria.morsexpress.data.local.dao.TranslationDao
import com.aria.morsexpress.data.local.entity.TranslationEntity
import com.aria.morsexpress.util.MorseAudioAnalyzer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.nio.ByteBuffer

class AudioInputViewModel(
    private val dao: TranslationDao
) : ViewModel() {

    private val _detectedMorse = MutableStateFlow("")
    val detectedMorse: StateFlow<String> = _detectedMorse.asStateFlow()

    private val _translatedText = MutableStateFlow("")
    val translatedText: StateFlow<String> = _translatedText.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    fun setMorseCode(morse: String) {
        _detectedMorse.value = morse
    }

    fun setTranslatedText(text: String) {
        _translatedText.value = text
    }

    fun setAnalyzing(analyzing: Boolean) {
        _isAnalyzing.value = analyzing
    }

    fun saveTranslation() {
        val morse = _detectedMorse.value
        val text = _translatedText.value

        if (morse.isNotBlank() && text.isNotBlank()) {
            viewModelScope.launch(Dispatchers.IO) {
                dao.insert(
                    TranslationEntity(
                        timestamp = System.currentTimeMillis(),
                        inputPathOrContent = morse,
                        morseCode = morse,
                        inputType = "AUDIO",
                        originalText = morse,
                        translatedText = text
                    )
                )
            }
        }
    }

    fun clearData() {
        _detectedMorse.value = ""
        _translatedText.value = ""
    }

    fun analyzeAudioFromUri(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isAnalyzing.value = true

                val extractor = MediaExtractor()
                extractor.setDataSource(context, uri, null)

                var audioTrackIndex = -1
                for (i in 0 until extractor.trackCount) {
                    val format = extractor.getTrackFormat(i)
                    val mime = format.getString(MediaFormat.KEY_MIME)
                    if (mime?.startsWith("audio/") == true) {
                        audioTrackIndex = i
                        break
                    }
                }

                if (audioTrackIndex == -1) {
                    _detectedMorse.value = "No se encontró pista de audio"
                    _isAnalyzing.value = false
                    return@launch
                }

                extractor.selectTrack(audioTrackIndex)
                val format = extractor.getTrackFormat(audioTrackIndex)
                val sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)

                val pcmData = mutableListOf<Short>()
                val buffer = ByteBuffer.allocate(1024)
                while (true) {
                    val sampleSize = extractor.readSampleData(buffer, 0)
                    if (sampleSize < 0) break
                    buffer.rewind()

                    for (i in 0 until sampleSize step 2) {
                        val low = buffer.get().toInt() and 0xFF
                        val high = buffer.get().toInt()
                        val sample = (high shl 8) or low
                        pcmData.add(sample.toShort())
                    }

                    extractor.advance()
                }

                extractor.release()

                val (morse, text) = MorseAudioAnalyzer.analyzeAmplitudes(pcmData.toShortArray(), sampleRate)
                _detectedMorse.value = morse
                _translatedText.value = text.toString()

            } catch (e: Exception) {
                _detectedMorse.value = "Error al analizar el audio: ${e.message}"
            } finally {
                _isAnalyzing.value = false
            }
        }
    }
}

class AudioTranslationViewModelFactory(
    private val dao: TranslationDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AudioInputViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AudioInputViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}