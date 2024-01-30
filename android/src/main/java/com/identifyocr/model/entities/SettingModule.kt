package com.identifyocr.model.entities

import android.os.Parcelable
import com.identifyocr.model.enums.IdentifyModuleTypes
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class SettingModule(
    val moduleName: String,
    var moduleTypes: IdentifyModuleTypes,
    var swiped : Boolean = false
) : Parcelable
