package com.identifyocr.model.enums

enum class ToggleType(val type : String) {
    TOOGLE_CAMERA("toggleCamera"),
    TOOGLE_FLASH("toggleFlash"),
    TOOGLE_FACE_GUIDE("faceGuide"),
    TOOGLE_ID_GUIDE("idGuide"),
    IS_SMILING("isSmiling"),
    IS_NFC("NFCStatus"),
    IS_SPEECH("isSpeech"),
    IS_PREPARE("isPrepare"),
    UPLOAD_VIDEO("uploadVideo"),
    UPLOAD_SELFIE("uploadSelfie"),
    VALIDATE_ADDRESS("validateAddress"),
    UPLOAD_SIGNATURE("uploadSignature"),
    UPLOAD_ID_CARD_FRONT("uploadIdFront"),
    UPLOAD_ID_CARD_BACK("uploadIdBack"),
    NETWORK_QUALITY("networkQuality"),
    AMBIENT_LIGHT_PERCENT("ambientLightPercent")


}
