package com.identifyocr.model.entities

import android.os.Parcelable
import com.identifyocr.model.mrz.DocType
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class ScannedMrzInfo(
    var identificationSerialNumber: String?=null,
    var dateOfBirth : String?=null,
    var dateOfExpiry : String?= null,
    var docType : DocType
) : Parcelable
