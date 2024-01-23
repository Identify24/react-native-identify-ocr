import Foundation

// uploadIdFront , uploadIdBack,  uploadSelfie, uploadSignature
public enum UploadImgType: String, Codable {
    case uploadIdFront = "uploadIdFront"
    case uploadIdBack = "uploadIdBack"
    case uploadSelfie = "uploadSelfie"
    case uploadSignature = "uploadSignature"
    case validateAddress = "validateAddress"
}

public enum OCRType: String, Codable {
    case frontId = "idFront"
    case backId = "idBack"
    case passport = "passport"
    case document = "document"
    case selfie = "selfie"
    case signature = "signature"
    case blinking = "blinking"
    case smiling = "smiling"
    case headToRight = "headToRight"
    case headToLeft = "headToLeft"
    case idPortrait = "idPortrait"
}

public enum AppType: String, Codable {
    case onlySDK = "SDK"
    case demoApp = "Demo App"
}

public enum SDKType: Int, Codable {
    case fullProcess    = 0
    case withoutCall    = 1
    case onlyCall       = 2
}

public enum HostType: String, Codable {
    case identifyTr = "Identify Tr"
    case custom = "Custom"
}


public enum SdkModules: String, Codable {
    case login          = "Login Screen"
    case nfc            = "Mrz & Nfc Screen"
    case livenessDetection       = "Liveness Detection"
    case waitScreen     = "Call Wait Screen"
    case selfie         = "Selfie"
    case videoRecord    = "Video Recorder"
    case idCard         = "Id Card"
    case signature      = "Signature"
    case speech         = "Speech Recognition"
    case addressConf    = "Address Confirm"
    case thankU         = "Thank You"
    case prepare        = "Prepare"
    
    var desc: String {
        switch self {
            case .login:
                return "login"
            case .nfc:
                return "nfc"
            case .livenessDetection:
                return "livenessDetection"
            case .waitScreen:
                return "waitScreen"
            case .selfie:
                return "selfie"
            case .videoRecord:
                return "videoRecord"
            case .idCard:
                return "idCard"
            case .signature:
                return "signature"
            case .speech:
                return "speech"
            case .addressConf:
                return "addressConf"
            case .thankU:
                return "thankU"
            case .prepare:
                return "prepare"
            }
        }
    
}

public enum SelfieTypes: String, Codable {
    case selfie         = "selfie"
    case oldPhoneFace   = "oldPhoneFace"
    case video          = "video"
    case backId         = "idBack"
    case frontId        = "idFront"
    case signature      = "signature"
}

public enum AppQuitType: String {
    case restartModules
    case onlyCall
}

public enum SDKLogLevel: String {
    case noLog
    case all
}

public enum VerificationCardType: String {
    case onlyIdCard
    case all
}

public enum SDKLang: String {
    case de
    case eng
    case tr
}

public enum CardType: String {
    case idCard
    case passport
    case oldSchool
}

public enum LivenessTestStep: String {
    case turnLeft
    case turnRight
    case blinkEyes
    case smile
    case completed
}

public enum SDKCallActions {
    case incomingCall
    case endCall
    case comingSms
    case approveSms(Bool)
    case openWarningCircle
    case closeWarningCircle
    case openCardCircle
    case closeCardCircle
    case terminateCall
    case imOffline
    case updateQueue(String, String)
    case photoTaken(String)
    case subrejectedDismiss(String)
    case subscribed
    case openNfcRemote(String, String, String)
    case startTransfer
    case networkQuality(String)
    case missedCall
    case editNfcProcess
    case connectionErr
}

 
