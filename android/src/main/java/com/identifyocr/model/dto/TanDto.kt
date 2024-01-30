package com.identifyocr.model.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TanDto(
    var tid : String,
    var tan : String
)
