package com.aria.morsexpress.util

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import java.nio.ByteBuffer
import kotlin.math.min

object AudioDecoder {

    fun decodeWavToPCM(context: Context, uri: Uri): DecodedAudioData? {
        try {
            val extractor = MediaExtractor()
            extractor.setDataSource(context, uri, null)
            val format = extractor.getTrackFormat(0)
            val sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
            extractor.selectTrack(0)

            val pcmBuffer = ByteArray(1024 * 1024)
            val byteBuffer = ByteBuffer.allocate(4096)
            var totalRead = 0

            while (true) {
                val sampleSize = extractor.readSampleData(byteBuffer, 0)
                if (sampleSize < 0) break
                byteBuffer.get(pcmBuffer, totalRead, sampleSize)
                totalRead += sampleSize
                byteBuffer.clear()
                extractor.advance()
            }

            val shortArray = ShortArray(totalRead / 2)
            for (i in 0 until totalRead step 2) {
                shortArray[i / 2] = ((pcmBuffer[i + 1].toInt() shl 8) or (pcmBuffer[i].toInt() and 0xFF)).toShort()
            }

            extractor.release()
            return DecodedAudioData(shortArray, sampleRate)
        } catch (e: Exception) {
            return null
        }
    }
}