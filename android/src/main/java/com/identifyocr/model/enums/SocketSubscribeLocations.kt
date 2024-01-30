package com.identifyocr.model.enums

enum class SocketSubscribeLocations(val type : String) {
    NFC("Mrz & Nfc Screen"),
    CALL("Call Wait Screen"),
    CARD_PHOTO("Id Card"),
    SPEECH("Speech Recognition"),
    SELFIE("Selfie"),
    VITALITY("Liveness Detection"),
    VIDEO_RECORD("Video Recorder"),
    DIGITAL_SIGNATURE("Signature"),
    VALIDATE_ADDRESS("Address Confirm"),
    PREPARE("Prepare")
}
