package com.identifyocr

import android.graphics.Bitmap
import android.graphics.RectF
import android.util.Log
import android.util.Size
import androidx.core.graphics.toRect
import com.facebook.react.bridge.WritableMap
import com.facebook.react.bridge.WritableNativeMap
import com.identifyocr.model.entities.FaceEntity
import com.identifyocr.model.enums.CropType
import java.lang.Float.max


class BaseCropBackOfIdCard {


  private lateinit var idCardBitmap: Bitmap

  private var motherName: String = ""
  private var fatherName: String = ""
  private var issuedBy: String = ""
  private var mrzArea: String = ""
  private var mrzDocType: String? = null
  private var mrzNationality: String? = null
  private var mrzDocumentNum: String? = null
  private var mrzTcNumber: String? = null
  private var mrzBirthDate: String? = null
  private var mrzGender: String? = null
  private var mrzLastDate: String? = null
  private var mrzName: String? = null
  private var mrzSurname: String? = null
  private var allOcrData = ""
  private var exceptionCount = 0

  private lateinit var mrzBitmap: Bitmap
  private var rawMrz = ""
  private var fixedMrzText = ""
  private val resultData: WritableMap = WritableNativeMap()

  private val dayRegex = "[0-9]{6}".toRegex()
  private val idCardLineOneRegex = "([A|C|I]{1}[A-Z0-9<]{1})([A-Z]{3})([A-Z0-9<]{25})".toRegex()
  private val idCardLineTwoRegex =
    "([0-9]{6})([0-9]{1})([M|F|X|<]{1})([0-9]{6})([0-9]{1})([A-Z]{3})([A-Z0-9<]{11})([0-9]{1})".toRegex()
  private val documentNumberRegex = "([A-Z]{1})([0-9]{2})([A-Z]{1})([0-9]{5})".toRegex()
  private val mrzRegex =
    "([A|C|I]{1}[A-Z0-9<]{1})([A-Z]{3})([A-Z]{1})([0-9]{2})([A-Z]{1})([0-9]{6})([A-Z0-9<]{1})([0-9]{11})([A-Z0-9<]{1,3})([0-9]{6})([0-9]{1})([M|F]{1})([0-9]{6})([0-9]{1})([A-Z]{3})([A-Z0-9<]{7,42})".toRegex()


  private var mobileService: MobileServiceImpl? = null
  private var cropType = CropType.MOTHER_NAME

  private lateinit var bitmapMotherName: Bitmap
  private lateinit var bitmapFatherName: Bitmap
  private lateinit var bitmapIssuedBy: Bitmap
  private lateinit var bitmapMrzArea: Bitmap
  private var ocrResultCallback: OcrResultCallback? = null

  fun setOcrResultCallback(callback: OcrResultCallback) {
    this.ocrResultCallback = callback
  }

  fun startMobileServicesForBack(idCardBitmapData: Bitmap) {
    idCardBitmap = idCardBitmapData
    if (mobileService == null) {

      mobileService = MobileServiceImpl()
      mobileService?.getOcrFeatures(object : OcrProcessorListener {
        override fun scannedText(text: String) {
          processText(text)
          Log.e("texttexttexttext", text)

        }

      })
      getFaceFeatures()
    }

    scanTextBackOfId()

  }

  private fun getFaceFeatures() {
    mobileService?.getFaceFeatures(object : FaceCallback {
      override fun getFace(faceEntity: FaceEntity) {
        Log.e("getFace", "getFace: $faceEntity")
      }

      override fun faceNotFoundError() {
      }
    })
  }


  private fun adjustCropRect(rect: RectF, minWidth: Int, minHeight: Int): RectF {
    return RectF(
      rect.left,
      rect.top,
      max(rect.left + minWidth, rect.right),
      max(rect.top + minHeight, rect.bottom)
    )
  }

  private fun scanTextBackOfId() {
    idCardBitmap.let { bitmap ->
      val minCroppedWidth = 32
      val minCroppedHeight = 32


      val cropRectMotherName = RectF(
        bitmap.width / 3.6956f,
        bitmap.height / 4.0153f,
        bitmap.width / 1.2142f,
        bitmap.height / 3.0470f
      )


      val croppedBitmapMotherName =
        bitmap.cropImage(
          Size(bitmap.width, bitmap.height),
          adjustCropRect(cropRectMotherName, minCroppedWidth, minCroppedHeight).toRect()
        )
      Log.e("mother", Size(croppedBitmapMotherName.width, croppedBitmapMotherName.height).toString())


      val cropRectFatherName = RectF(
        bitmap.width / 3.6956f,
        bitmap.height / 2.8157f,
        bitmap.width / 1.2142f,
        bitmap.height / 2.3777f
      )

      val croppedBitmapFatherName =
        bitmap.cropImage(
          Size(bitmap.width, bitmap.height),
          adjustCropRect(cropRectFatherName, minCroppedWidth, minCroppedHeight).toRect()
        )


      val cropRectIssuedBy = RectF(
        bitmap.width / 3.6956f,
        bitmap.height / 2.14f,
        bitmap.width / 1.2142f,
        bitmap.height / 1.8135f
      )

      val croppedBitmapIssuedBy =
        bitmap.cropImage(
          Size(bitmap.width, bitmap.height),
          adjustCropRect(cropRectIssuedBy, minCroppedWidth, minCroppedHeight).toRect()
        )

      val cropRectMrz = RectF(
        0f,
        bitmap.height / 1.6718f,
        bitmap.width.toFloat(),
        bitmap.height.toFloat()
      )

      val croppedBitmapMrz =
        bitmap.cropImage(
          Size(bitmap.width, bitmap.height),
          adjustCropRect(cropRectMrz, minCroppedWidth, minCroppedHeight).toRect()
        )

      mrzBitmap = croppedBitmapMrz

      bitmapMotherName = croppedBitmapMotherName
      bitmapFatherName = croppedBitmapFatherName
      bitmapIssuedBy = croppedBitmapIssuedBy
      bitmapMrzArea = croppedBitmapMrz

      startOcrProcess()

      /*            bitmapToText(croppedBitmapMotherName, CropType.MOTHER_NAME)
                  bitmapToText(croppedBitmapFatherName, CropType.FATHER_NAME)
                  bitmapToText(croppedBitmapIssuedBy, CropType.ISSUED_BY)
                  bitmapToText(croppedBitmapMrz, CropType.MRZ_AREA)*/
    }
  }

  private fun startOcrProcess() {

    mobileService?.detectText(croppedBitmap = bitmapMotherName)
  }

  private fun processText(text: String) {

    allOcrData = allOcrData + "\n" + text

    var textWithoutSpace = text.replace("\n", "").replace("«", "<")
    when (cropType) {
      CropType.MOTHER_NAME -> {
        motherName = String(getCheckedLetters(textWithoutSpace))
        resultData.putString("idMotherNameOcr", motherName)
        cropType = CropType.FATHER_NAME
        mobileService?.detectText(croppedBitmap = bitmapFatherName)
      }

      CropType.FATHER_NAME -> {
        fatherName = String(getCheckedLetters(textWithoutSpace))
        resultData.putString("idFatherNameOcr", fatherName)
        cropType = CropType.ISSUED_BY
        mobileService?.detectText(croppedBitmap = bitmapIssuedBy)
      }

      CropType.ISSUED_BY -> {
        issuedBy = String(getCheckedLetters(textWithoutSpace))
        resultData.putString("idIssuedByOcr", issuedBy)
        cropType = CropType.MRZ_AREA
        mobileService?.detectText(croppedBitmap = bitmapMrzArea)
      }

      CropType.MRZ_AREA -> {
        textWithoutSpace = textWithoutSpace.replace(" ", "")

        rawMrz = textWithoutSpace


        val splitMrz = textWithoutSpace.split("<").filter { it != "" }

        if (splitMrz.size >= 6) {
          if (splitMrz[0].length == 1) {
            mrzDocType = splitMrz[0]
          }
          if (splitMrz[1].length == 13) {
            mrzNationality = splitMrz[1].substring(0, 3)
            mrzDocumentNum = splitMrz[1].substring(3, 12)
          }
          if (splitMrz[2].length == 11) {
            mrzTcNumber = splitMrz[2]
          }
          if (splitMrz[3].length == 18) {
            mrzBirthDate = splitMrz[3].substring(0, 6)
            mrzGender = splitMrz[3].substring(7, 8)
            mrzLastDate = splitMrz[3].substring(8, 14)
          }
          mrzSurname = splitMrz[4]
          mrzName = ""

          for (item in splitMrz.slice(5 until splitMrz.size)) {
            mrzName += item
          }

          val tempMrzDocType = mrzDocType
          val tempMrzNationality = mrzNationality
          val tempMrzDocumentNum = mrzDocumentNum
          val tempMrzTcNumber = mrzTcNumber
          val tempMrzBirthDate = mrzBirthDate
          val tempMrzGender = mrzGender
          val tempMrzLastDate = mrzLastDate
          val tempMrzSurname = mrzSurname
          val tempMrzName = mrzName

          mrzDocType = mrzDocType?.let { String(getCheckedLetters(it)) }
          mrzNationality =
            mrzNationality?.let { String(getCheckedLetters(it)) }
          mrzDocumentNum =
            mrzDocumentNum?.let { String(getFixedDocumentString(it)) }
          mrzTcNumber =
            mrzTcNumber?.let { String(getCheckedNumbers(it)) }
          mrzBirthDate =
            mrzBirthDate?.let { String(getCheckedNumbers(it)) }
          mrzGender = mrzGender?.let { String(getCheckedLetters(it)) }
          mrzLastDate =
            mrzLastDate?.let { String(getCheckedNumbers(it)) }
          mrzSurname = mrzSurname?.let { String(getCheckedLetters(it)) }
          mrzName = mrzName?.let { String(getCheckedLetters(it)) }

          mrzSurname?.let {
            if (isInteger(mrzSurname!![0].toString()))
              mrzSurname!!.removeRange(0, 1)
          }

          tempMrzDocType?.let { textWithoutSpace.replace(it, mrzDocType!!) }
          tempMrzNationality?.let { textWithoutSpace.replace(it, mrzNationality!!) }
          tempMrzDocumentNum?.let { textWithoutSpace.replace(it, mrzDocumentNum!!) }
          tempMrzTcNumber?.let { textWithoutSpace.replace(it, mrzTcNumber!!) }
          tempMrzBirthDate?.let { textWithoutSpace.replace(it, mrzBirthDate!!) }
          tempMrzGender?.let { textWithoutSpace.replace(it, mrzGender!!) }
          tempMrzLastDate?.let { textWithoutSpace.replace(it, mrzLastDate!!) }
          tempMrzSurname?.let { textWithoutSpace.replace(it, mrzSurname!!) }
          Log.e("lsdmvşsdmf", "processText: reulstttt$allOcrData")
          fixedMrzText = textWithoutSpace


          Log.e("mrzDocType", "processText:$tempMrzDocType ")
          Log.e("mrzNationality", "processText:$tempMrzNationality ")
          Log.e("mrzDocumentNum", "processText:$tempMrzDocumentNum ")
          Log.e("mrzTcNumber", "processText:$tempMrzTcNumber ")
          Log.e("mrzBirthDate", "processText:$tempMrzBirthDate ")
          Log.e("mrzGender", "processText:$tempMrzGender ")
          Log.e("mrzLastDate", "processText:$tempMrzLastDate ")
          Log.e("idSurnameMRZ", "processText:$tempMrzSurname ")


          resultData.putString("idTypeMRZ", tempMrzDocType)
          resultData.putString("idNationalityMRZ", tempMrzNationality)
          resultData.putString("idDocumentNumberMRZ", tempMrzDocumentNum)
          resultData.putString("idTcknMRZ", tempMrzTcNumber)
          resultData.putString("idBirthDateMRZ", tempMrzBirthDate)
          resultData.putString("idGenderMRZ", tempMrzGender)
          resultData.putString("idValidDateMRZ", tempMrzLastDate)
          resultData.putString("idSurnameMRZ", tempMrzSurname)
          resultData.putString("idNameMRZ", tempMrzName)

          mrzIssue(textWithoutSpace)
          resultData.putString("fullMrzKey", textWithoutSpace)
          ocrResultCallback?.onOcrResult(resultData)
        } else {

          //throw error
          ////throw error

        }
      }


      else -> {}
    }
    // Task completed successfully
    // ...
  }


  private fun mrzIssue(text: String) {

    mrzArea = text

    checkKpsData({
      val splitMrz = text.split("<").filter { it != "" }

      if (splitMrz.size >= 6) {
        if (splitMrz[0].length == 1) {
          mrzDocType = splitMrz[0]
        }
        if (splitMrz[1].length == 13) {
          mrzNationality = splitMrz[1].substring(0, 3)
          mrzDocumentNum = splitMrz[1].substring(3, 12)
        }
        if (splitMrz[2].length == 11) {
          mrzTcNumber = splitMrz[2]
        }
        if (splitMrz[3].length == 18) {
          mrzBirthDate = splitMrz[3].substring(0, 6)
          mrzGender = splitMrz[3].substring(7, 8)
          mrzLastDate = splitMrz[3].substring(8, 14)
        }

        /*                mrzDocType = splitMrz[0]
                        mrzDocumentNum = splitMrz[1].substring(3, 12)
                        mrzTcNumber = splitMrz[2]
                        mrzBirthDate = splitMrz[3].substring(0, 6)
                        mrzGender = splitMrz[3].substring(7, 8)
                        mrzLastDate = splitMrz[3].substring(8, 14)*/
        mrzSurname = splitMrz[4]
        mrzName = ""

        for (item in splitMrz.slice(5 until splitMrz.size)) {
          mrzName += item
        }
      } else
      //throw error

        mrzBirthDate =
          mrzBirthDate?.let { String(getCheckedNumbers(it)) }
      mrzLastDate =
        mrzLastDate?.let { String(getCheckedNumbers(it)) }

      mrzDocumentNum = mrzDocumentNum?.let { String(getFixedDocumentString(it)) }

      mrzSurname?.let {
        if (isInteger(mrzSurname!![0].toString()))
          mrzSurname = mrzSurname!!.removeRange(0, 1)
      }

      mrzDocumentNum?.let { dNum ->
        mrzLastDate?.let { lDay ->
          mrzBirthDate?.let { bDay ->
            if (documentNumberRegex.find(dNum) != null && dayRegex.find(
                bDay
              ) != null && dayRegex.find(lDay) != null
            ) {
              mrzDocumentNum =
                documentNumberRegex.find(dNum)?.value
              mrzLastDate = dayRegex.find(lDay)?.value
              mrzBirthDate = dayRegex.find(bDay)?.value
              if (!mrzBirthDate.isNullOrEmpty()
                && !mrzLastDate.isNullOrEmpty()
                && !mrzDocumentNum.isNullOrEmpty()
              ) {
                Log.e("flgefmerğpfl", "mrzIssue: ")
              } else {
                //throw error
                checkExceptionCount()
              }
            } else {
              //throw error
            }

          } ?: run {
            //throw error
          }
        } ?: run {
          //throw error
        }
      } ?: run {
        //throw error
      }
    }, {
      if (idCardLineOneRegex.find(text) != null && idCardLineTwoRegex.find(
          text
        ) != null
      ) {
        mrzDocumentNum = null
        mrzBirthDate = null
        mrzLastDate = null

      }
    })
  }

  private fun checkKpsData(noKps: () -> Unit, yesKps: () -> Unit) {
    if (true) {
      noKps()
    } else {
      yesKps()
    }
  }


  private fun checkExceptionCount() {
    exceptionCount++
    if (exceptionCount == 3) {
      //throw error
    } else {
      //clearImage()
    }


  }

  fun getDocumentNumberChars(documentNumber: String): CharArray {
    val documentNumberChars = documentNumber.toCharArray()
    for (i in documentNumberChars.indices) {
      val c = documentNumberChars[i]
      if ((i == 3 || i == 0) && c == "0"[0]) {
        documentNumberChars[i] = "O"[0]
      }
      if ((i == 3 || i == 0) && c == "1"[0]) {
        documentNumberChars[i] = "I"[0]
      }
      if ((i == 3 || i == 0) && c == "2"[0]) {
        documentNumberChars[i] = "Z"[0]
      }
      if ((i == 3 || i == 0) && c == "5"[0]) {
        documentNumberChars[i] = "S"[0]
      }
      if (i != 3 && i != 0 && c == "O"[0]) {
        documentNumberChars[i] = "0"[0]
      }
      if (i != 3 && i != 0 && c == "I"[0]) {
        documentNumberChars[i] = "1"[0]
      }
      if (i != 3 && i != 0 && c == "S"[0]) {
        documentNumberChars[i] = "5"[0]
      }
      if (i != 3 && i != 0 && c == "Z"[0]) {
        documentNumberChars[i] = "2"[0]
      }
    }
    return documentNumberChars
  }


  fun getDocumentNumberCharsForPassport(documentNumber: String): CharArray {
    val documentNumberChars = documentNumber.toCharArray()
    for (i in documentNumberChars.indices) {
      val c = documentNumberChars[i]
      if (i == 0 && c == "0"[0]) {
        documentNumberChars[i] = "O"[0]
      }
      if (i == 0 && c == "1"[0]) {
        documentNumberChars[i] = "I"[0]
      }
      if (i == 0 && c == "2"[0]) {
        documentNumberChars[i] = "Z"[0]
      }
      if (i == 0 && c == "5"[0]) {
        documentNumberChars[i] = "S"[0]
      }
      if (i != 0 && c == "O"[0]) {
        documentNumberChars[i] = "0"[0]
      }
      if (i != 0 && c == "I"[0]) {
        documentNumberChars[i] = "1"[0]
      }
      if (i != 0 && c == "S"[0]) {
        documentNumberChars[i] = "5"[0]
      }
      if (i != 0 && c == "Z"[0]) {
        documentNumberChars[i] = "2"[0]
      }
    }
    return documentNumberChars
  }


  fun isMrzValidForId(
    documentNumber: String?,
    dateOfBirth: String?,
    expiryDate: String?,
    tcNumber: String?
  ): Boolean {
    return !documentNumber.isNullOrEmpty() &&
      documentNumber.length == 9 &&
      !dateOfBirth.isNullOrEmpty() &&
      dateOfBirth.length == 6 &&
      !expiryDate.isNullOrEmpty() &&
      expiryDate.length == 6 &&
      !tcNumber.isNullOrEmpty() &&
      tcNumber.length == 11
  }

  fun isMrzValidForPassport(
    documentNumber: String?,
    dateOfBirth: String?,
    expiryDate: String?
  ): Boolean {
    return !documentNumber.isNullOrEmpty() &&
      documentNumber.length == 9 &&
      !dateOfBirth.isNullOrEmpty() &&
      dateOfBirth.length == 6 &&
      !expiryDate.isNullOrEmpty() &&
      expiryDate.length == 6
  }

  fun getFixedDocumentString(documentNumber: String): CharArray {
    val documentNumberChars = documentNumber.toCharArray()

    if (documentNumberChars.size == 9) {
      documentNumberChars[0] = getCheckedLetters(documentNumberChars[0].toString())[0]
      documentNumberChars[1] = getCheckedNumbers(documentNumberChars[1].toString())[0]
      documentNumberChars[2] = getCheckedNumbers(documentNumberChars[2].toString())[0]
      documentNumberChars[3] = getCheckedLetters(documentNumberChars[3].toString())[0]
      documentNumberChars[4] = getCheckedNumbers(documentNumberChars[4].toString())[0]
      documentNumberChars[5] = getCheckedNumbers(documentNumberChars[5].toString())[0]
      documentNumberChars[6] = getCheckedNumbers(documentNumberChars[6].toString())[0]
      documentNumberChars[7] = getCheckedNumbers(documentNumberChars[7].toString())[0]
      documentNumberChars[8] = getCheckedNumbers(documentNumberChars[8].toString())[0]
    }

    return documentNumberChars
  }

  fun isInteger(input: String): Boolean {
    val integerChars = '0'..'9'
    return input.all { it in integerChars }
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

    }
    return textChars
  }

}
