package com.identifyocr.model.entities

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class LivenessThresholds (
    val smilingProbability : Float = 0.6f,
    val eyesOpenProbability : Float = 0.5f,
    val rightSideHeadRotationAngle : Float? = -30f,
    val leftSideHeadRotationAngle : Float? = 30f,
) : Parcelable
