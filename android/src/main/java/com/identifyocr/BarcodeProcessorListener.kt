package com.identifyocr

interface BarcodeProcessorListener {
    fun barcodeNumber(number : String?)
    fun failBarcode()
}
