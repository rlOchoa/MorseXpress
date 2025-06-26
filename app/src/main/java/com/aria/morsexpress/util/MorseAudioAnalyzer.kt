package com.aria.morsexpress.util

import kotlin.math.abs

object MorseAudioAnalyzer {

    private const val THRESHOLD_PERCENTILE = 1  // percentil para detectar señal
    private const val DOT_DASH_RATIO = 3.0
    private const val INTER_SYMBOL_RATIO = 1.0
    private const val LETTER_GAP_RATIO = 3.0
    private const val WORD_GAP_RATIO = 7.0

    fun extractMorseFromAudio(samples: ShortArray, sampleRate: Int): String {
        val threshold = computeDynamicThreshold(samples)
        val segments = segmentAudio(samples, threshold, sampleRate)

        val tones = segments.filter { it.isTone }
        val toneDurations = tones.map { it.durationMs }

        if (toneDurations.isEmpty()) return ""

        val sortedDurations = toneDurations.sorted()
        val dotDuration = sortedDurations[(sortedDurations.size * 0.1).toInt()]  // percentil 10

        val dashThreshold = dotDuration * DOT_DASH_RATIO

        val sb = StringBuilder()
        for ((isTone, duration) in segments) {
            if (isTone) {
                sb.append(
                    if (duration < dashThreshold) "." else "-"
                )
            } else {
                when {
                    duration >= dotDuration * WORD_GAP_RATIO -> sb.append(" / ")
                    duration >= dotDuration * LETTER_GAP_RATIO -> sb.append(" ")
                    // silencios cortos se ignoran (espacio entre símbolos)
                }
            }
        }

        return sb.toString().replace(Regex("\\s+"), " ").trim()
    }

    private fun computeDynamicThreshold(samples: ShortArray): Int {
        val absSamples: List<Int> = samples.map { abs(it.toInt()) }
        val peak = absSamples.maxOrNull() ?: 30000
        return (peak * 0.25).toInt() // usar 25% del pico real
    }

    private fun segmentAudio(samples: ShortArray, threshold: Int, sampleRate: Int): List<AudioSegment> {
        val segments = mutableListOf<AudioSegment>()
        var i = 0
        while (i < samples.size) {
            val isTone = abs(samples[i].toInt()) >= threshold
            var count = 0
            while (i < samples.size && (abs(samples[i].toInt()) >= threshold) == isTone) {
                count++
                i++
            }
            val durationMs = (count / sampleRate.toDouble()) * 1000
            segments.add(AudioSegment(isTone, durationMs.toInt()))
        }
        return segments
    }

    private data class AudioSegment(val isTone: Boolean, val durationMs: Int)
}
