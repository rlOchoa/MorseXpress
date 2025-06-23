package com.aria.morsexpress.util

import android.content.Context
import android.util.Log
import org.opencv.android.OpenCVLoader

object OpenCVHelper {

    init {
        try {
            System.loadLibrary("opencv_java4")
        } catch (e: UnsatisfiedLinkError) {
            Log.e("OpenCVHelper", "Error cargando librería OpenCV: ${e.message}")
        }
    }

    fun initOpenCV(context: Context): Boolean {
        val success = OpenCVLoader.initDebug()
        if (success) {
            Log.d("OpenCVHelper", "OpenCV inicializado correctamente.")
        } else {
            Log.e("OpenCVHelper", "La inicialización de OpenCV falló.")
        }
        return success
    }
}