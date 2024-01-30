package com.identifyocr

import com.facebook.react.bridge.WritableMap


interface OcrResultCallback {
  fun onOcrResult(ocrData: WritableMap)
}
