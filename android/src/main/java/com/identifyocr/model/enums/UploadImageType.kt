package com.identifyocr.model.enums

enum class UploadImageType(val type : String) {
    SELFIE("selfie"),
    ID_FRONT("idFront"),
    ID_BACK("idBack"),
    PASSPORT("idFront"),
    SIGNATURE("signature"),
    ADDRESS("address"),
    BLINKING("blinking"),
    SMILING("smiling"),
    HEAD_TO_RIGHT("headToRight"),
    HEAD_TO_LEFT("headToLeft"),
    ID_PORTRAIT("idPortrait")
}
