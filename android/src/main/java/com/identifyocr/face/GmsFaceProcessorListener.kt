package com.identifyocr.face

import com.google.mlkit.vision.face.Face

interface GmsFaceProcessorListener {
    fun success(face: Face)
    fun error()
}
