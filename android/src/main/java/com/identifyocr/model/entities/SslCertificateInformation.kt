package com.identifyocr.model.entities

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class SslCertificateInformation(
    val sha256FingerPrint: String,
    val domain : String
) : Parcelable
