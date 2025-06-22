package com.aria.morsexpress.util

object RobustMorseDetector {

    private val morseSymbolRegex = Regex("[.\\-–—•·]+")

    /**
     * Limpia el texto completo extraído por OCR y filtra posibles patrones de morse
     */
    fun extractMorseLines(text: String): String {
        val cleanedLines = text.lines().map { line ->
            line.trim()
                .replace("•", ".")
                .replace("·", ".")
                .replace("–", "-")
                .replace("—", "-")
                .replace("\\s+".toRegex(), "")
        }

        // Solo tomamos líneas que contengan patrones válidos
        return cleanedLines
            .filter { morseSymbolRegex.matches(it) || it.contains("/") }
            .joinToString(" ") // Espacio como separador de palabras morse
    }
}
