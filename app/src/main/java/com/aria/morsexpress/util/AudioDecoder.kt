package com.aria.morsexpress.util

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import com.aria.morsexpress.util.DecodedAudioData
import java.nio.ByteBuffer
import java.nio.ByteOrder

object AudioDecoder {

    fun decodeWavToPCM(context: Context, uri: Uri): DecodedAudioData? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val header = ByteArray(44)
            inputStream.read(header)
            val sampleRate = ByteBuffer.wrap(header, 24, 4).order(ByteOrder.LITTLE_ENDIAN).int
            val pcmData = inputStream.readBytes().toShortArray()
            inputStream.close()
            DecodedAudioData(pcmData, sampleRate)
        } catch (e: Exception) {
            null
        }
    }

    private fun ByteArray.toShortArray(): ShortArray {
        val shortBuffer = ByteBuffer.wrap(this).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()
        return ShortArray(shortBuffer.remaining()).also { shortBuffer.get(it) }
    }
}
