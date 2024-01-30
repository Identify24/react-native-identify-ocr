package com.identifyocr.model.enums

enum class IdentifyModuleTypes(val type: String) {
    AGENT_CALL("waitScreen"),SPEECH_TEST("speech"),IDENTIFICATION_INFORMATION_WITH_NFC("nfc"),LIVENESS_TEST("livenessDetection"),IDENTIFICATION_INFORMATION_WITH_CARD_PHOTO("idCard"),TAKE_SELFIE("selfie"),VIDEO_RECORD("videoRecord"),SIGNATURE("signature"),VALIDATE_ADDRESS("addressConf"), PREPARE("prepare")

}
