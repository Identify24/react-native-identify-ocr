package com.identifyocr.model.entities

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class NfcDependency(
    var identificationSerialNumber: String="",
    var dateOfBirth : String="",
    var dateOfExpiry : String = ""
) : Parcelable
