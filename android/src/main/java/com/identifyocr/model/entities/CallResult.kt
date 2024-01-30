package com.identifyocr.model.entities

import android.os.Parcelable
import com.identifyocr.model.dto.MrzDto
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
data class CallResult(
    var withRedirect: Boolean,
    var withCallNfc: Boolean,
    var mrzDto: MrzDto?
)
