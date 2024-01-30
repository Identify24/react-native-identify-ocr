package com.identifyocr

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions


class GmsOcrScanner {


  private val detector by lazy {
    TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
  }


  var ocrListener: OcrProcessorListener? = null

  var ocrStateListener: OcrProcessorWithStateListener? = null


  fun detectText(imageProxy: ImageProxy?, croppedImage: Bitmap, closeImage: Boolean? = true) {
    Log.e("fwfwfwbbb", "height:"+croppedImage.height.toString()+"___ width:"+croppedImage.width.toString() )
    croppedImage.let {
      recognizeText(InputImage.fromBitmap(it, 0)).addOnCompleteListener {
        imageProxy?.close()
      }
    }
  }

  fun detectTextWithState(croppedImage: Bitmap) {
    croppedImage.let {
      recognizeTextWithState(InputImage.fromBitmap(it, 0))
    }
  }


  private fun recognizeTextWithState(
    image: InputImage
  ): Task<Text> {
    // Pass image to an ML Kit Vision API
    return detector.process(image)
      .addOnSuccessListener { text ->
        // Task completed successfully
        if (text.text.isEmpty())
          ocrStateListener?.onError(null)
        else
          ocrStateListener?.scannedText(text.text)
      }
      .addOnFailureListener { exception ->
        // Task failed with an exception
        ocrStateListener?.onError(exception)
        println("""EROORR OCR dw= ${exception.message}""")
      }
  }


  private fun recognizeText(
    image: InputImage
  ): Task<Text> {
    // Pass image to an ML Kit Vision API
    Log.e("fdwdfwe", image.height.toString() + "___" + image.width.toString())
    return detector.process(image)
      .addOnSuccessListener { text ->
        // Task completed successfully
        ocrListener?.scannedText(text.text)
      }
      .addOnFailureListener { exception ->
        // Task failed with an exception
        println("""EROORR qOCR = ${exception.message}""")
      }
  }


  fun killGmsOcrService() {
    detector.close()
    ocrListener = null
  }

}
