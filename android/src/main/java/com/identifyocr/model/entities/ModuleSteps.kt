package com.identifyocr.model.entities

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize


@Parcelize
@JsonClass(generateAdapter = true)
data class ModuleSteps(
        var nfc: Boolean = false,
        var liveness : Boolean = false,
        var idFront : Boolean = false,
        var idBack : Boolean = false,
        var video : Boolean = false,
        var signature : Boolean = false,
        var speech : Boolean = false,
        var selfie : Boolean = false,
        var verifyAddress : Boolean = false,
        var sign_language : Boolean ?= false,
        var prepare : Boolean = false,
        var language : String ?= ""
) : Parcelable
