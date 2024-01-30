package com.identifyocr.model.facedetect

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RectModel(
    var left: Float,
    var top: Float,
    var right: Float,
    var bottom: Float
)
