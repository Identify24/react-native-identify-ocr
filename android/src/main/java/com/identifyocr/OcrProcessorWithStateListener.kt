package com.identifyocr

interface OcrProcessorWithStateListener {

    fun scannedText(text: String)

    fun onError(e:Exception?)
}
