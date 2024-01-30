package com.identifyocr

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.facebook.react.bridge.WritableMap

class IdentifyOcrModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  private val baseCropBackOfIdCard = BaseCropBackOfIdCard()
  private val baseCropFrontOfIdCard = BaseCropFrontOfIdCard()

  private fun base64ToBitmap(base64Data: String): Bitmap? {
    try {
      val decodedBytes = Base64.decode(base64Data, Base64.DEFAULT)
      return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
      e.printStackTrace()
      return null
    }
  }

  override fun getName(): String {
    return NAME
  }

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  fun processImage(type: String, base64Image: String, promise: Promise) {

    val bitmap = base64ToBitmap(base64Image)
    if (bitmap != null) {
      Log.e("12", "bitmaat-ppp not nall")

      when (type) {
        "FrontId" -> {
          Log.e("123FrontId", "123")


          baseCropFrontOfIdCard.setOcrResultCallback(object : OcrResultCallback {
            override fun onOcrResult(ocrData: WritableMap) {
              // Handle the OCR result here
              Log.e("OCR Result Front", ocrData.toString())

              promise.resolve(ocrData)
            }
          })

          baseCropFrontOfIdCard.startMobileServicesForFront(bitmap)
        }

        "BackId" -> {
          baseCropBackOfIdCard.setOcrResultCallback(object : OcrResultCallback {
            override fun onOcrResult(ocrData: WritableMap) {
              // Handle the OCR result here
              Log.e("OCR Result Back", ocrData.toString())
              promise.resolve(ocrData)
            }
          })

          baseCropBackOfIdCard.startMobileServicesForBack(bitmap)
        }

        "PassportMrzKey" -> {
          Log.e("PassportMrzKey", "PassportMrzKey")
          promise.resolve("PassportMrzKey Not Found")
        }
      }
    }
  }

  companion object {
    const val NAME = "IdentifyOcr"
  }
}
