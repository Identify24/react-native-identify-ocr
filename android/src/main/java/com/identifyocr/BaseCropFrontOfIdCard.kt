package com.identifyocr

import android.graphics.Bitmap
import android.graphics.RectF
import android.util.Size
import androidx.core.graphics.toRect
import com.facebook.react.bridge.WritableMap
import com.facebook.react.bridge.WritableNativeMap
import com.identifyocr.model.entities.FaceEntity
import com.identifyocr.model.enums.CropType
import java.lang.Float

class BaseCropFrontOfIdCard {

  private var allOcrData = ""
  private var tcNumber: String = ""
  private var name: String = ""
  private var surname: String = ""
  private var dateOfBirth: String = ""
  private var expiryDate: String = ""
  private var documentNumber: String = ""
  private var gender: String = ""
  private var nationality: String = ""
  private val resultData: WritableMap = WritableNativeMap()
  private lateinit var idCardBitmap: Bitmap
  private val wrongDocumentNoRegex = "([A-Z0-9]{9})".toRegex()

  private var mobileService: MobileServiceImpl? = null


  private lateinit var bitmapTcNo: Bitmap
  private lateinit var bitmapName: Bitmap
  private lateinit var bitmapLastName: Bitmap
  private lateinit var bitmapBirthDate: Bitmap
  private lateinit var bitmapDocumentNo: Bitmap
  private lateinit var bitmapValidUntil: Bitmap
  private lateinit var bitmapGender: Bitmap
  private lateinit var bitmapNationality: Bitmap
  private var cropType = CropType.ID_NUMBER

  private var ocrResultCallback: OcrResultCallback? = null

  fun setOcrResultCallback(callback: OcrResultCallback) {
    this.ocrResultCallback = callback
  }

  fun startMobileServicesForFront(idCardBitmapData: Bitmap) {
    idCardBitmap = idCardBitmapData

    if (mobileService == null) {
      mobileService = MobileServiceImpl()
      mobileService?.getOcrFeatures(object : OcrProcessorListener {
        override fun scannedText(text: String) {
          processText(text)
        }

      })
      mobileService?.getFaceFeatures(object : FaceCallback {
        override fun getFace(faceEntity: FaceEntity) {
          // add to json this area

          getFaceCropArea(idCardBitmap)
        }

        override fun faceNotFoundError() {
          //error ver
        }
      })
    }
    scanTextFrontOfId(idCardBitmap)
  }

  private fun adjustCropRect(rect: RectF, minWidth: Int, minHeight: Int): RectF {
    return RectF(
      rect.left,
      rect.top,
      Float.max(rect.left + minWidth, rect.right),
      Float.max(rect.top + minHeight, rect.bottom)
    )
  }

  private fun scanTextFrontOfId(bitmap: Bitmap) {
    val minCroppedWidth = 32
    val minCroppedHeight = 32


    val cropRectTcNo = RectF(
      bitmap.width / 17f,
      bitmap.height / 3.821f,
      bitmap.width / 3.148f,
      bitmap.height / 2.972f
    )
    val croppedBitmapTcNo =
      bitmap.cropImage(
        Size(bitmap.width, bitmap.height),
        adjustCropRect(cropRectTcNo, minCroppedWidth, minCroppedHeight).toRect()
      )

    val cropRectLastName = RectF(
      bitmap.width / 3.035f,
      bitmap.height / 2.431f,
      bitmap.width / 1.7f,
      bitmap.height / 2.057f
    )
    val croppedBitmapLastName =
      bitmap.cropImage(
        Size(bitmap.width, bitmap.height),
        adjustCropRect(cropRectLastName, minCroppedWidth, minCroppedHeight).toRect()
      )

    val cropRecName = RectF(
      bitmap.width / 3.035f,
      bitmap.height / 1.91f,
      bitmap.width / 1.7f,
      bitmap.height / 1.671f
    )
    val croppedBitmapName =
      bitmap.cropImage(
        Size(bitmap.width, bitmap.height),
        adjustCropRect(cropRecName, minCroppedWidth, minCroppedHeight).toRect()
      )

    val cropRecBirthDate = RectF(
      bitmap.width / 3.035f,
      bitmap.height / 1.573f,
      bitmap.width / 1.8888f,
      bitmap.height / 1.408f
    ) //1.40789
    val croppedBitmapBirthDate =
      bitmap.cropImage(
        Size(bitmap.width, bitmap.height),
        adjustCropRect(cropRecBirthDate, minCroppedWidth, minCroppedHeight).toRect()
      )

    val cropRecDocumentNo = RectF(
      bitmap.width / 3.035f,
      bitmap.height / 1.3375f,
      bitmap.width / 1.8888f,
      bitmap.height / 1.2298f
    ) // 1.2159
    val croppedBitmapDocumentNo =
      bitmap.cropImage(
        Size(bitmap.width, bitmap.height),
        adjustCropRect(cropRecDocumentNo, minCroppedWidth, minCroppedHeight).toRect()
      )

    val cropRecValidUntil = RectF(
      bitmap.width / 3.035f,
      bitmap.height / 1.1630f,
      bitmap.width / 1.8888f,
      bitmap.height / 1.07f
    ) // 1.2159
    val croppedBitmapValidUntil =
      bitmap.cropImage(
        Size(bitmap.width, bitmap.height),
        adjustCropRect(cropRecValidUntil, minCroppedWidth, minCroppedHeight).toRect()
      )

    val cropRecGender = RectF(
      bitmap.width / 1.7f,
      bitmap.height / 1.573f,
      bitmap.width / 1.416f,
      bitmap.height / 1.408f
    ) // 1.40789
    val croppedBitmapGender =
      bitmap.cropImage(
        Size(bitmap.width, bitmap.height),
        adjustCropRect(cropRecGender, minCroppedWidth, minCroppedHeight).toRect()
      )

    val cropRecNationality = RectF(
      bitmap.width / 1.7f,
      bitmap.height / 1.3375f,
      bitmap.width / 1.3077f,
      bitmap.height / 1.2159f
    ) // 1.40789
    val croppedBitmapNationality =
      bitmap.cropImage(
        Size(bitmap.width, bitmap.height),
        adjustCropRect(cropRecNationality, minCroppedWidth, minCroppedHeight).toRect()
      )

    // bitmapDetectFace(croppedBitmapFace)

    bitmapTcNo = croppedBitmapTcNo
    bitmapName = croppedBitmapName
    bitmapLastName = croppedBitmapLastName
    bitmapBirthDate = croppedBitmapBirthDate
    bitmapDocumentNo = croppedBitmapDocumentNo
    bitmapValidUntil = croppedBitmapValidUntil
    bitmapGender = croppedBitmapGender
    bitmapNationality = croppedBitmapNationality

    mobileService?.detectText(croppedBitmap = bitmapTcNo)
  }

  private fun processText(text: String) {
    allOcrData = allOcrData + "\n" + text

    val textWithoutSpace = text.replace("\n", "")
    when (cropType) {
      CropType.ID_NUMBER -> {
        tcNumber = String(getCheckedNumbers(textWithoutSpace))
        resultData.putString("idTcknOcr", tcNumber)
        cropType = CropType.NAME
        mobileService?.detectText(croppedBitmap = bitmapName)
      }

      CropType.NAME -> {
        name = String(getCheckedLetters(textWithoutSpace))
        resultData.putString("idNameOcr", name)

        cropType = CropType.SURNAME
        mobileService?.detectText(croppedBitmap = bitmapLastName)
      }

      CropType.SURNAME -> {
        surname = String(getCheckedLetters(textWithoutSpace))
        resultData.putString("idSurnameOcr", surname)

        cropType = CropType.DATE_OF_BIRTH
        mobileService?.detectText(croppedBitmap = bitmapBirthDate)
      }

      CropType.DATE_OF_BIRTH -> {
        val birthDayList = String(
          getCheckedNumbers(
            textWithoutSpace.replace(
              ",",
              "."
            )
          )
        ).split(".")
        if (birthDayList.size == 3) {
          dateOfBirth =
            birthDayList[0] + "." + birthDayList[1] + "." + birthDayList[2]
          //birthDayList[2].substring(2) + birthDayList[1] + birthDayList[0]
        } else {
          dateOfBirth =
            String(getCheckedNumbers(textWithoutSpace.replace(",", ".")))
        }
        resultData.putString("idBirthDateOcr", dateOfBirth)
        cropType = CropType.DOCUMENT_NO
        mobileService?.detectText(croppedBitmap = bitmapDocumentNo)
      }

      CropType.DOCUMENT_NO -> {
        checkKpsData({
          documentNumber = textWithoutSpace.replace(" ", "")

        }, {

        })
        resultData.putString("idSerialNoOcr", documentNumber)

        cropType = CropType.VALID_UNTIL
        mobileService?.detectText(croppedBitmap = bitmapValidUntil)
      }

      CropType.VALID_UNTIL -> {
        val validUntilList = String(
          getCheckedNumbers(
            textWithoutSpace.replace(
              ",",
              "."
            )
          )
        ).split(".")
        expiryDate = if (validUntilList.size == 3) {
          validUntilList[2].substring(2) + validUntilList[1] + validUntilList[0]
        } else
          String(getCheckedNumbers(textWithoutSpace.replace(",", ".")))
        resultData.putString("idValidUntilOcr", expiryDate)

        cropType = CropType.GENDER
        mobileService?.detectText(croppedBitmap = bitmapGender)
      }

      CropType.GENDER -> {
        gender = textWithoutSpace
        resultData.putString("idGenderOcr", gender)

        cropType = CropType.NATIONALITY
        mobileService?.detectText(croppedBitmap = bitmapNationality)
      }

      CropType.NATIONALITY -> {
        nationality = String(getCheckedLetters(textWithoutSpace))
        resultData.putString("idNationalityOcr", nationality)
        ocrResultCallback?.onOcrResult(resultData)
      }

      else -> {
        print("buradadadada")
      }
    }
    // Task completed successfully
    // ...
  }

  private fun checkKpsData(noKps: () -> Unit, yesKps: () -> Unit) {
    if (true) {
      noKps()
    } else {
      yesKps()
    }
  }

  private fun getFaceCropArea(fullIdCard: Bitmap): Bitmap {
    fullIdCard.let { bitmap ->
      val cropRectFace = RectF(
        bitmap.width / 14.16f,
        bitmap.height / 2.547f,
        bitmap.width / 3.115f,
        bitmap.height / 1.138f
      )
      return bitmap.cropImage(Size(bitmap.width, bitmap.height), cropRectFace.toRect())
    }
  }


  fun getCheckedNumbers(text: String): CharArray {
    val textChars = text.toCharArray()
    for (i in textChars.indices) {
      val c = textChars[i]
      if (c == "O"[0]) {
        textChars[i] = "0"[0]
      }
      if (c == "S"[0]) {
        textChars[i] = "5"[0]
      }
      if (c == "Z"[0]) {
        textChars[i] = "2"[0]
      }
      if (c == "I"[0]) {
        textChars[i] = "1"[0]
      }
      if (c == "D"[0]) {
        textChars[i] = "0"[0]
      }
    }
    return textChars
  }

  fun getCheckedLetters(text: String): CharArray {
    val textChars = text.toCharArray()
    for (i in textChars.indices) {
      val c = textChars[i]
      if (c == "0"[0]) {
        textChars[i] = "O"[0]
      }
      if (c == "5"[0]) {
        textChars[i] = "S"[0]
      }
      if (c == "2"[0]) {
        textChars[i] = "Z"[0]
      }
      if (c == "1"[0]) {
        textChars[i] = "I"[0]
      }
      /*            if (c == "0"[0]) {
                      textChars[i] = "D"[0]
                  }*/
    }
    return textChars
  }


}
