package com.identifyocr

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.face.Face
import com.identifyocr.barcode.GmsBarcodeScanner

import com.identifyocr.face.GmsFaceScanner

import com.identifyocr.face.GmsFaceProcessorListener
import com.identifyocr.model.entities.FaceEntity

class MobileServiceImpl : MobileService {

    private var gmsFaceScanner : GmsFaceScanner?= null

    private var gmsOcrScanner : GmsOcrScanner ?= null


    private var gmsBarcodeScanner : GmsBarcodeScanner?= null



    override fun getFaceFeatures(faceCallback: FaceCallback) {
        gmsFaceScanner = GmsFaceScanner()
        gmsFaceScanner?.faceListener = object : GmsFaceProcessorListener {
           override fun success(face: Face) {
               faceCallback.getFace(FaceEntity(face.smilingProbability,face.leftEyeOpenProbability,face.rightEyeOpenProbability,face.headEulerAngleY))
           }
           override fun error() {
               faceCallback.faceNotFoundError()
           }
       }
    }

    override fun getOcrFeatures(ocrCallback: OcrProcessorListener) {
        gmsOcrScanner = GmsOcrScanner()
        gmsOcrScanner?.ocrListener = ocrCallback
    }

    override fun getOcrFeaturesWithState(ocrCallback: OcrProcessorWithStateListener) {
        gmsOcrScanner = GmsOcrScanner()
        gmsOcrScanner?.ocrStateListener = ocrCallback
    }

    override fun getBarcodeFeatures(context: Context?, barcodeCallback: BarcodeProcessorListener) {
        gmsBarcodeScanner = GmsBarcodeScanner()
        gmsBarcodeScanner?.barcodeListener = barcodeCallback
    }

    override fun detectFace(imageProxy: ImageProxy?, bitmap : Bitmap?) {
        gmsFaceScanner?.detectFace(imageProxy,bitmap) }

    override fun detectTextWithState(croppedBitmap: Bitmap) {
         gmsOcrScanner?.detectTextWithState(croppedBitmap)
    }

    override fun detectBarcode(imageProxy: ImageProxy?, bitmap: Bitmap?) { gmsBarcodeScanner?.detectBarcode(imageProxy,bitmap) }

    override fun detectText(imageProxy: ImageProxy?,croppedBitmap : Bitmap) { gmsOcrScanner?.detectText(imageProxy,croppedBitmap) }

    override fun stopFaceService() {
        gmsFaceScanner?.killGmsFaceService()
        gmsFaceScanner = null
    }

    override fun stopBarcodeService() {
       gmsBarcodeScanner?.killGmsBarcodeService()
        gmsBarcodeScanner = null
    }

    override fun stopOcrService() {
       gmsOcrScanner?.killGmsOcrService()
       gmsOcrScanner = null
    }




}
