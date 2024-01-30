import UIKit

@objc(IdentifyOcr)
class IdentifyOcr: NSObject {

    var ocrManager: IdentifyOCRManager? = IdentifyOCRManager.shared

 

    @objc public func processImage(_ type: String ,base64Image: String,   resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        if let imageData = Data(base64Encoded: base64Image, options: .ignoreUnknownCharacters),
           let image = UIImage(data: imageData) {
            switch type {
            case "BackId":
                startBackIdOcr(frontImg: image) { idInfo, error in
                    if let error = error {
                        reject("OCR_ERROR", "Error during OCR: \(error)", nil)
                    } else {
                        resolve(idInfo.toDictionary())
                    }
                }
            case "PassportMrzKey":
                // Assuming cominData is available in React Native, you may need to adjust this part
                let cominData = FrontIdInfo() // Replace this with actual cominData
                startPassportMrzKey(frontImg: image, cominData: cominData) { idInfo, error in
                    if let error = error {
                        reject("OCR_ERROR", "Error during OCR: \(error)", nil)
                    } else {
                        resolve(idInfo.toDictionary())
                    }
                }
            case "FrontId":
                startFrontIdOcr(frontImg: image) { idInfo, error in
                    if let error = error {
                        reject("OCR_ERROR", "Error during OCR: \(error)", nil)
                    } else {
                        resolve(idInfo.toDictionary())
                    }
                }
            default:
                reject("INVALID_TYPE", "Invalid OCR type", nil)
            }
        } else {
            reject("INVALID_IMAGE", "Invalid base64 image data", nil)
        }
    }

    public func startBackIdOcr(frontImg: UIImage, callback: @escaping (BackIdInfo, SDKError?)->()) {
        ocrManager?.backScanner(identImg: frontImg) { [weak self] idInfo, error in
            guard let self = self else { return }
            if error != nil {
                callback(BackIdInfo(), error)
            } else {
                callback(idInfo, nil)
            }
        }
    }

    public func startPassportMrzKey(frontImg: UIImage, cominData: FrontIdInfo, callback: @escaping(FrontIdInfo, SDKError?) -> ()) {
        ocrManager?.startPassportScanner(identImg: frontImg, cominData: cominData) { [weak self] idInfo, error in
            guard let self = self else { return }
            if error != nil {
                callback(FrontIdInfo(), error)
            } else {
                callback(idInfo, nil)
            }
        }
    }

    public func startFrontIdOcr(frontImg: UIImage, callback: @escaping (FrontIdInfo, SDKError?)->()) {
        ocrManager?.frontScanner(identImg: frontImg) { [weak self] idInfo, error in
            guard let self = self else { return }
            if error != nil {
                callback(FrontIdInfo(), error)
            } else {
                if let tcNo = idInfo.idTcknOcr, tcNo == "" {
                    let newErr = SDKError(message: "TC No okunamadı, geçersiz kimlik")
                    callback(idInfo, newErr)
                    return
                } else {
                    callback(idInfo, nil)
                }
            }
        }
    }

}

extension BackIdInfo {
    func toDictionary() -> [String: Any] {
        return [
            "idSurnameMRZ": idSurnameMRZ ?? "",
            "idNameMRZ": idNameMRZ ?? "",
            "idMotherNameOcr": idMotherNameOcr ?? "",
            "idFatherNameOcr": idFatherNameOcr ?? "",
            "idIssuedByOcr": idIssuedByOcr ?? "",
            "fullMrzKey": fullMrzKey ?? "",
            "idTcknMRZ": idTcknMRZ ?? "",
            "idBirthDateMRZ": idBirthDateMRZ ?? "",
            "idDocumentNumberMRZ": idDocumentNumberMRZ ?? "",
            "idValidDateMRZ": idValidDateMRZ ?? "",
            "idTypeMRZ": idTypeMRZ ?? "",
            "idGenderMRZ": idGenderMRZ ?? "",
            "bulkData": bulkData ?? ""
        ]
    }
}

extension FrontIdInfo {
    func toDictionary() -> [String: Any] {
        return [
            "idTcknOcr": idTcknOcr ?? "",
            "idSurnameOcr": idSurnameOcr ?? "",
            "idNameOcr": idNameOcr ?? "",
            "idBirthDateOcr": idBirthDateOcr ?? "",
            "idSerialNoOcr": idSerialNoOcr ?? "",
            "idValidUntilOcr": idValidUntilOcr ?? "",
            "fullMrzKey": fullMrzKey ?? ""
        ]
    }
}
