package com.aria.morsexpress.util

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.roundToInt

object VisualMorseAnalyzer {

    data class Settings(
        val threshold: Int = 160,
        val dotMaxLength: Int = 4,
        val dashMinLength: Int = 6,
        val letterSpaceMinLength: Int = 6,
        val wordSpaceMinLength: Int = 14,
        val scanStep: Int = 1,
        val linesToScan: Int = 1
    )

    fun analyze(
        bitmap: Bitmap,
        settings: Settings = Settings()
    ): String {
        val grayBitmap = bitmap.toGrayscale()
        val binaryBitmap = grayBitmap.toBinary(settings.threshold)

        val morseResult = StringBuilder()
        val height = binaryBitmap.height
        val centerLine = height / 2
        val startLine = centerLine - (settings.scanStep * settings.linesToScan) / 2

        for (i in 0 until settings.linesToScan) {
            val y = startLine + i * settings.scanStep
            if (y in 0 until height) {
                val linePattern = analyzeLine(binaryBitmap, y, settings)
                if (linePattern.isNotBlank()) {
                    morseResult.append(linePattern).append(" / ")
                }
            }
        }

        return morseResult.toString().trim().replace(Regex("(/\\s*)+$"), "")
    }

    private fun Bitmap.toGrayscale(): Bitmap {
        val gray = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = getPixel(x, y)
                val r = Color.red(pixel)
                val g = Color.green(pixel)
                val b = Color.blue(pixel)
                val avg = (0.299 * r + 0.587 * g + 0.114 * b).roundToInt()
                gray.setPixel(x, y, Color.rgb(avg, avg, avg))
            }
        }
        return gray
    }

    private fun Bitmap.toBinary(threshold: Int): Bitmap {
        val binary = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = getPixel(x, y)
                val brightness = Color.red(pixel) // Ya está en escala de grises
                val color = if (brightness < threshold) Color.BLACK else Color.WHITE
                binary.setPixel(x, y, color)
            }
        }
        return binary
    }

    private fun analyzeLine(bitmap: Bitmap, y: Int, settings: Settings): String {
        val result = StringBuilder()
        var count = 0
        var currentColor = bitmap.getPixel(0, y)

        for (x in 0 until bitmap.width) {
            val pixel = bitmap.getPixel(x, y)
            if (pixel == currentColor) {
                count++
            } else {
                // Procesar el segmento anterior
                result.append(classifySegment(currentColor, count, settings))
                currentColor = pixel
                count = 1
            }
        }

        // Último segmento
        result.append(classifySegment(currentColor, count, settings))

        return result.toString().trim()
    }

    private fun classifySegment(
        color: Int,
        length: Int,
        settings: Settings
    ): String {
        return when (color) {
            Color.BLACK -> {
                when {
                    length <= settings.dotMaxLength -> "."
                    length >= settings.dashMinLength -> "-"
                    else -> "-" // Si está entre dot y dash, consideramos raya
                }
            }
            Color.WHITE -> {
                when {
                    length >= settings.wordSpaceMinLength -> " / "
                    length >= settings.letterSpaceMinLength -> " "
                    else -> "" // espacio menor, ignorar
                }
            }
            else -> ""
        }
    }
}
