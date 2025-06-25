package com.aria.morsexpress.util

import android.media.MediaExtractor
import android.media.MediaFormat
import java.io.InputStream
import kotlin.math.abs

object MorseAudioAnalyzer {

    data class AudioAnalysisResult(
        val morseCode: String,
        val peakDurations: List<Long>
    )

    fun extractMorseFromAudio(pcm: ShortArray, sampleRate: Int): String {
        val amplitudes = pcm.map { it.toInt().toDouble() }
        return analyzeAmplitudes(amplitudes, sampleRate)
    }

    // Función real que analiza InputStream de audio
//    fun analyzeAudio(
//        inputStream: InputStream,
//        sampleRate: Int = 44100, // usar valor default si no se detecta
//        threshold: Int = 2000
//    ): AudioAnalysisResult {
//        // Convertimos el input stream a arreglo de bytes
//        val audioBytes = inputStream.readBytes()
//
//        // Convertimos los bytes a shortArray
//        val samples = ShortArray(audioBytes.size / 2)
//        for (i in samples.indices) {
//            val low = audioBytes[i * 2].toInt() and 0xff
//            val high = audioBytes[i * 2 + 1].toInt()
//            samples[i] = ((high shl 8) or low).toShort()
//        }
//
//        return analyzeAmplitudes(
//            samples = samples,
//            sampleRate = sampleRate,
//            threshold = threshold
//        )
//    }

    fun analyzeAmplitudes(amplitudes: List<Double>, sampleRate: Int): String {
        val threshold = amplitudes.maxOrNull()?.times(0.5) ?: return ""
        val unitTime = sampleRate / 10 // aprox. 100ms

        val binarySequence = amplitudes.map { if (it > threshold) 1 else 0 }

        val morseBuilder = StringBuilder()
        var i = 0
        while (i < binarySequence.size) {
            val bit = binarySequence[i]
            var length = 1
            while (i + length < binarySequence.size && binarySequence[i + length] == bit) {
                length++
            }

            val duration = length.toDouble() / sampleRate
            if (bit == 1) {
                morseBuilder.append(
                    when {
                        duration < 0.2 -> "."  // dot
                        duration < 0.5 -> "-"  // dash
                        else -> {}
                    }
                )
            } else {
                morseBuilder.append(
                    when {
                        duration in 0.2..0.5 -> " "     // space between letters
                        duration >= 0.5 -> "   "        // space between words
                        else -> {}                      // ignore short silence
                    }
                )
            }
            i += length
        }

        return morseBuilder.toString().trim()
    }

    // Función alternativa que analiza un arreglo de muestras PCM
//    fun analyzeAmplitudes(
//        samples: ShortArray,
//        sampleRate: Int,
//        threshold: Int = 2000,
//        minSilenceDurationMs: Long = 200,
//        dotMaxDurationMs: Long = 150,
//        dashMinDurationMs: Long = 250
//    ): AudioAnalysisResult {
//        val morseBuilder = StringBuilder()
//        val durations = mutableListOf<Long>()
//
//        val msPerSample = 1000.0 / sampleRate
//        var inPeak = false
//        var durationMs = 0.0
//        var lastWasPeak = false
//
//        for (sample in samples) {
//            val amplitude = abs(sample.toInt())
//
//            if (amplitude > threshold) {
//                if (!inPeak) {
//                    inPeak = true
//                    if (!lastWasPeak) {
//                        // Fin de silencio
//                        if (durationMs > minSilenceDurationMs) {
//                            morseBuilder.append(" / ") // espacio entre palabras
//                        } else if (durationMs > dotMaxDurationMs) {
//                            morseBuilder.append(" ") // espacio entre letras
//                        }
//                        durations.add(durationMs.toLong())
//                    }
//                    durationMs = 0.0
//                }
//            } else {
//                if (inPeak) {
//                    inPeak = false
//                    // Fin de tono
//                    if (durationMs < dotMaxDurationMs) {
//                        morseBuilder.append(".")
//                    } else if (durationMs >= dashMinDurationMs) {
//                        morseBuilder.append("-")
//                    }
//                    durations.add(durationMs.toLong())
//                    durationMs = 0.0
//                }
//            }
//
//            durationMs += msPerSample
//            lastWasPeak = inPeak
//        }
//
//        return AudioAnalysisResult(
//            morseCode = morseBuilder.toString().trim(),
//            peakDurations = durations
//        )
//    }
}
