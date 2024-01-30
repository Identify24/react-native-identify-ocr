package com.identifyocr.model.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UploadImageDto(
    var ident_id: String,
    var image: String,
    var type: String,
    var address: String? = null,
    var fullMrzKey: String? = null,
    var ocr: IdOcrDto? = null
)
