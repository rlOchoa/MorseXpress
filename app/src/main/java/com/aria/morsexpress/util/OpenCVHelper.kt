package com.aria.morsexpress.util

import android.content.Context
import org.opencv.android.OpenCVLoader
import org.opencv.core.Core

object OpenCVHelper {
    fun initOpenCV(context: Context): Boolean {
        return OpenCVLoader.initDebug().also {
            if (!it) {
                throw RuntimeException("OpenCV no se pudo inicializar")
            }
        }
    }
}