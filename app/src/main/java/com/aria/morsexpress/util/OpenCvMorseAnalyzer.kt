package com.aria.morsexpress.util

import android.graphics.Bitmap
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import org.opencv.android.Utils
import org.opencv.core.MatOfPoint

object OpenCvMorseAnalyzer {

    data class MorseConfig(
        val threshold: Int = 128,
        val dotMaxWidth: Int = 10,
        val dashMinWidth: Int = 15,
        val letterSpaceMinGap: Int = 20,
        val wordSpaceMinGap: Int = 50
    )

    fun analyzeMorseFromBitmap(bitmap: Bitmap, config: MorseConfig = MorseConfig()): String {
        // 1. Convertir el Bitmap a Mat
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)

        // 2. Escala de grises
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY)

        // 3. Binarización
        Imgproc.threshold(mat, mat, config.threshold.toDouble(), 255.0, Imgproc.THRESH_BINARY_INV)

        // 4. Buscar contornos horizontales
        val contours = mutableListOf<MatOfPoint>()
        val hierarchy = Mat()
        Imgproc.findContours(mat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

        // 5. Clasificar los contornos como punto o raya
        val sortedContours = contours.sortedBy { Imgproc.boundingRect(it).x }
        val symbols = mutableListOf<String>()
        var lastX = -1

        for (contour in sortedContours) {
            val rect = Imgproc.boundingRect(contour)

            // Ignorar contornos muy pequeños
            if (rect.height < 5 || rect.width < 2) continue

            // Espacios
            if (lastX != -1) {
                val gap = rect.x - lastX
                if (gap >= config.wordSpaceMinGap) {
                    symbols.add(" / ")
                } else if (gap >= config.letterSpaceMinGap) {
                    symbols.add(" ")
                }
            }

            // Clasificación de símbolo
            val symbol = when {
                rect.width <= config.dotMaxWidth -> "."
                rect.width >= config.dashMinWidth -> "-"
                else -> continue
            }

            if (symbol != null) {
                symbols.add(symbol)
                lastX = rect.x + rect.width
            }
        }

        return symbols.joinToString("")
    }
}