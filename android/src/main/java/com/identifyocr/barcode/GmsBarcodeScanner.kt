package com.identifyocr.barcode

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.identifyocr.BarcodeProcessorListener

class GmsBarcodeScanner {

    var barcodeListener : BarcodeProcessorListener?= null

    private val scanner by lazy {
        BarcodeScanning.getClient()
    }



    fun detectBarcode(image: ImageProxy?, bitmap : Bitmap?) {
        bitmap?.let {
            val inputImage = InputImage.fromBitmap(bitmap,0)
            scanner.process(inputImage)
                .addOnSuccessListener { barcode ->
                    if (barcode.isNotEmpty() && barcode.size == 1) {
                        barcodeListener?.barcodeNumber(barcode.first().displayValue)
                    }else{
                        barcodeListener?.failBarcode()
                    }
                }.addOnCompleteListener {
                    image?.close()
                }
        }
    }


    fun killGmsBarcodeService(){
        barcodeListener  = null
        scanner.close()
    }
}
