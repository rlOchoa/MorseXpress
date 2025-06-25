package com.aria.morsexpress.util

data class DecodedAudioData(
    val pcmData: ShortArray,
    val sampleRate: Int
)