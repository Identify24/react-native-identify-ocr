package com.identifyocr

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.ImageProxy

interface MobileService {
    fun getFaceFeatures(faceCallback : FaceCallback)
    fun getOcrFeatures(ocrCallBack: OcrProcessorListener)

    fun getOcrFeaturesWithState(ocrCallBack: OcrProcessorWithStateListener)
    fun getBarcodeFeatures(context: Context?=null,barcodeCallback: BarcodeProcessorListener)
    fun detectFace(imageProxy : ImageProxy?= null, bitmap : Bitmap?)

    fun detectTextWithState(croppedBitmap: Bitmap)
    fun detectBarcode(imageProxy : ImageProxy?= null, bitmap : Bitmap?)
    fun detectText(imageProxy : ImageProxy?= null,croppedBitmap : Bitmap)
    fun stopFaceService()
    fun stopBarcodeService()
    fun stopOcrService()
}
