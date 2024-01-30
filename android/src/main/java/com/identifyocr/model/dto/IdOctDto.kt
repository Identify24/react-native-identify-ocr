package com.identifyocr.model.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class IdOcrDto(
    val idTcknOcr: String? = null,
    val idSurnameOcr: String? = null,
    val idNameOcr: String? = null,
    val idBirthDateOcr: String? = null,
    val idSerialNoOcr: String? = null,
    val idValidUntilOcr: String? = null,
    var fullMrzKey: String? = "",

    val idSurnameMRZ: String? = null,
    val idNameMRZ: String? = null,
    val idMotherNameOcr: String? = null,
    val idFatherNameOcr: String? = null,
    val idIssuedByOcr: String? = null,
    val idTcknMRZ: String? = null,
    val idBirthDateMRZ: String? = null,
    val idDocumentNumberMRZ: String? = null,
    val idValidDateMRZ: String? = null,
    val idTypeMRZ: String? = null,
    val idGenderMRZ: String? = null,
    val bulkData: String? = null
)
