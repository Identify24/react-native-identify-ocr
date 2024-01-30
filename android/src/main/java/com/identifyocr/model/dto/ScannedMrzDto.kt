package com.identifyocr.model.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ScannedMrzDto(
    val identId: String?,
    val serialNo: String?,
    val expireDate: String?,
    val rawData: String?,
    val birthDate: String?
)
