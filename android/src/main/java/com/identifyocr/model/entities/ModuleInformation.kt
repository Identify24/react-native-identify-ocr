package com.identifyocr.model.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.fragment.app.Fragment
import com.identifyocr.model.enums.IdentifyModuleTypes
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize
import java.io.Serializable


@JsonClass(generateAdapter = true)
data class ModuleInformation(
        val position: Int,
        var moduleTypes: String,
        var fragment: Fragment,
        var active : Boolean
) : Serializable
