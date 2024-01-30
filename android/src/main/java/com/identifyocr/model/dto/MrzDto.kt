package com.identifyocr.model.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MrzDto(
    val authority: String?,
    val birthDate: String?,
    val docType: String?,
    val expireDate: String?,
    val gender: String?,
    val ident_id: String?,
    val name: String?,
    val nationality: String?,
    val personalNumber: String?,
    val serialNumber: String?,
    val surname: String?,
    val image: String?,
    val address: String?,
    val mrzInfo : String?,
    val activeAuth : Boolean?,
    val passiveAuth: Boolean?
)
