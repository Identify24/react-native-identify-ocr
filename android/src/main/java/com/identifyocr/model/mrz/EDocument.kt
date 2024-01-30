package com.identifyocr.model.mrz

import com.squareup.moshi.JsonClass
import java.security.PublicKey

@JsonClass(generateAdapter = true)
data class EDocument (
    var docType: DocType? = null,
    var personDetails: PersonDetails? = null,
    var additionalPersonDetails: AdditionalPersonDetails? = null,
    var docPublicKey: PublicKey? = null
)
