package com.identifyocr.model.entities

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class DeviceInfo(
    var osVersion: String,
    var deviceModel : String,
    var deviceBrand : String,
    var platform : String = "Android"
) : Parcelable
