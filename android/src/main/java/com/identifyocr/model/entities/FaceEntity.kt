package com.identifyocr.model.entities

import android.graphics.Rect
import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class FaceEntity (
    val smilingProbability : Float?,
    val leftEyeOpenProbability : Float?,
    val rightEyeOpenProbability : Float?,
    val headEulerAngleY : Float?
) : Parcelable
