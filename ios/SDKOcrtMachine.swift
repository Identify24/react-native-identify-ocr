//
//  SDKOcrtMachine.swift
//  react-native-identify-ocr
//
//  Created by Melih on 22.01.2024.
//

import UIKit
import Vision
import CoreImage
import CoreImage.CIFilterBuiltins

public class IdentifyOCRManager {
    
    static let shared = IdentifyOCRManager()
    var mrzLine1Comp = false
    var mrzLine2Comp = false
    var mrzLine3Comp = false
    var fullMrzKey = ""
    var mrzLineArr = [""]
    var sdkLogLevel: SDKLogLevel? = .noLog
    
    
    
    func sdkLog(logMsg: String? = "log msg not found") {
        switch self.sdkLogLevel {
        case .all:
            print("⚠️⚠️⚠️ Identify LOG: \(logMsg ?? "log msg not found") ⚠️⚠️⚠️")
        default:
            return
        }
    }
    
    
    public func detectHumanFace(comingPhoto: UIImage, callback: @escaping(Bool) -> ()) {
        let myImage = CIImage(image: comingPhoto)!
        let accuracy = [CIDetectorAccuracy: CIDetectorAccuracyHigh]
        let faceDetector = CIDetector(ofType: CIDetectorTypeFace, context: nil, options: accuracy)
        let faces = faceDetector?.features(in: myImage, options: [CIDetectorSmile:true])
        
        if faces?.count == 1 {
            callback(true)
        } else {
            callback(false)
        }
    }
    
    func searchPassportMrz(docText: [String], cominData: FrontIdInfo) -> FrontIdInfo {
        var mrzText = ""
        sdkLog(logMsg:"\(docText)")
        if docText.count == 0 {
            return cominData
        } else {
            let mrzArr = docText.suffix(2)
            
            for key in mrzArr {
                mrzText = mrzText + key
            }
            cominData.fullMrzKey = mrzText
        }
        return cominData
    }
    
    func searchValuesInArray(docText: [String]) -> FrontIdInfo {
        sdkLog(logMsg:"Front OCR: \(docText)")
        
        let infoClass = FrontIdInfo()

        if docText.count == 0 {
            sdkLog(logMsg: "Ön yüzde yazı algılanamadı")
            return FrontIdInfo()
        }
        
        var tcIndex = 0
        for (index, element) in docText.enumerated() {
            let range = NSRange(location: 0, length: element.count)
            
            let tcNoRegex = try! NSRegularExpression(pattern: "^[1-9]{1}[0-9]{9}[02468]{1}$")
            let dateRegex = try! NSRegularExpression(pattern: "^(0[1-9]|[12][0-9]|3[01])[.](0[1-9]|1[012])[.](19|20)[0-9]{2}$")
            
            // Tc no alalım
            if (tcNoRegex.firstMatch(in: element, options: [], range: range) != nil) {
                infoClass.idTcknOcr = element
                tcIndex = index
            }
            
            // Tarihleri alalım
            if (dateRegex.firstMatch(in: element, options: [], range: range) != nil) {
                let dateFormatter = DateFormatter()
                dateFormatter.dateFormat = "dd.MM.YYYY"
                let date = dateFormatter.date(from:element)
                let delta = date?.timeIntervalSince(Date()) ?? 0
                if delta < 0 {
                    infoClass.idBirthDateOcr = element
                } else {
                    infoClass.idValidUntilOcr = element
                }
            }
            
            // find doc no start
            let documentRegex = try! NSRegularExpression(pattern: "^([A-Z]{1}[0-9]{2}[A-Z]{1}[0-9]{5})$")
            let documentErrorRegex = try! NSRegularExpression(pattern: "^([A-Z]{2}[0-9]{1}[A-Z]{1}[0-9]{5})$")
            let documentNoAllErrorRegex = try! NSRegularExpression(pattern: "^([A-Z]{1}[0-9]{8})")
            
            if (documentRegex.firstMatch(in: element, options: [], range: range) != nil || documentRegex.firstMatch(in: element, options: [], range: range) != nil ) {
                infoClass.idSerialNoOcr = element
            } else if (documentErrorRegex.firstMatch(in: element, options: [], range: range) != nil) {
                var newText = ""
                newText = self.fixSerialNo(element: element)
//                if element[1] == "D" || element[1] == "O" {
//                    newText = replaceWrongChr(myString: element, 1, "0")
//                } else if element[1] == "Z" {
//                    newText = replaceWrongChr(myString: element, 1, "2")
//                } else if element[1] == "S" {
//                    newText = replaceWrongChr(myString: element, 1, "5")
//                } else if element[1] == "I" {
//                    newText = replaceWrongChr(myString: element, 1, "1")
//                }
                infoClass.idSerialNoOcr = newText
                
            } else if (documentNoAllErrorRegex.firstMatch(in: element, options: [], range: range) != nil) {
                var newText = ""
                newText = self.fixSerialNo(element: element)
//                if element[3] == "0" {
//                    newText = replaceWrongChr(myString: element, 3, "O")
//                } else if element[3] == "2" {
//                    newText = replaceWrongChr(myString: element, 3, "Z")
//                } else if element[3] == "1" {
//                    newText = replaceWrongChr(myString: element, 3, "I")
//                } else if element[3] == "5" {
//                    newText = replaceWrongChr(myString: element, 3, "S")
//                }
                infoClass.idSerialNoOcr = newText
            }
        }
        
        self.getNameAndSurnameAndDocNo(docText: docText, indexId: tcIndex, infoClass: infoClass) { [weak self] results in
            guard let self = self else { return }
            infoClass.idSurnameOcr = results.idSurnameOcr
            infoClass.idNameOcr = results.idNameOcr
        }
        return infoClass
    }
    
    func searchValuesInBackArray(docText: [String]) -> BackIdInfo {
        sdkLog(logMsg:"Back OCR: \(docText)")
        
        let infoClass = BackIdInfo()
        
        if docText.count == 0 {
            sdkLog(logMsg: "Arka yüzde yazı algılanamadı")
            return BackIdInfo()
        }
        
        var cominText:[String] = docText
        
        var backTcNo = ""
        var backBirthDate = ""
        var backValidDate = ""
        var docNo = ""
        
        
        let fullNameArr = cominText.last?.components(separatedBy: "<<")
        if fullNameArr?.count ?? 0 > 1 {
            let surname = fullNameArr?[0] ?? ""
            let firstName = fullNameArr?[1].replacingOccurrences(of: "<", with: " ", options: .literal, range: nil) ?? ""
            infoClass.idSurnameMRZ = surname
            infoClass.idNameMRZ = firstName
        }
        
        if cominText.count >= 8 {
            infoClass.idMotherNameOcr = cominText[3]
            infoClass.idFatherNameOcr = cominText[5]
            infoClass.idIssuedByOcr = cominText[7]
        }
        
        
        
//        infoClass.mrzLine = cominText[8] + cominText[9] + cominText[10]
        for (_, element) in cominText.enumerated() {
                        
            var x = element.replacingOccurrences(of: "≤", with: "<", options: .literal, range: nil)
            x = x.replacingOccurrences(of: "≤≤", with: "<", options: .literal, range: nil)
            
            var y = x.replacingOccurrences(of: "‹", with: "<", options: .literal, range: nil)
            y = y.replacingOccurrences(of: "«", with: "<")
            
            let range = NSRange(location: 0, length: y.utf16.count)
            
            let backTcRegex = try! NSRegularExpression(pattern: "[1-9]{1}[0-9]{9}[02468]{1}")
            let dateRegex = try! NSRegularExpression(pattern: "^[0-9]{7}[A-Z]{1}")
            
            let serialNoRegex = try! NSRegularExpression(pattern: "([A-Z]{1}[0-9]{2}[A-Z]{1}[0-9]{5})")
            let wronSeriNoRegex =  try! NSRegularExpression(pattern: "([A-Z]{1}[0-9]{3}[0-9]{5})")
            
            // Tc no alalım
            let containsTUR = "<TUR" // 1. satırı algılamak için mecburuz
            let containsStat = containsTUR.contains("<TUR")
            
            let contains2TUR = "\\bTUR<\\b"
            let contains2Stat = contains2TUR.contains("\\bTUR<\\b")
            
            let PENRegex = "^[PEN]{3}\\s[1-9]{6}"
            let havePen = PENRegex.contains(PENRegex)
            
            if (containsStat == true && backTcRegex.firstMatch(in: y, options: [], range: range) != nil) {
                
                if (serialNoRegex.firstMatch(in: y, range: range) != nil) {
                    let serialNo = y.matchForRegex("([A-Z]{1}[0-9]{2}[A-Z]{1}[0-9]{5})")
                    infoClass.idDocumentNumberMRZ = serialNo.first?.first
                    let tcNo = y.matchForRegex("[1-9]{1}[0-9]{9}[02468]{1}")
                    infoClass.idTcknMRZ = tcNo.first?.first ?? ""
                } else if (wronSeriNoRegex.firstMatch(in: y, range: range) != nil) {
                    let serialNo = y.matchForRegex("([A-Z]{1}[0-9]{3}[0-9]{5})")
                    let fixedDocNo = self.fixSerialNo(element: serialNo.first?.first ?? "")
                    infoClass.idDocumentNumberMRZ = fixedDocNo
                    let tcNo = y.matchForRegex("[1-9]{1}[0-9]{9}[02468]{1}")
                    infoClass.idTcknMRZ = tcNo.first?.first ?? ""
                }
            }
            
            let pattern = "TUR<"
            if let ranger = y.range(of: pattern, options: .regularExpression) {
                if y.count >= 13 {
                    for i in 0...5 {
                        backBirthDate += y[i]
                    }
                    infoClass.idBirthDateMRZ = backBirthDate
                    
                    if y.contains("M") || y.contains("F") {
                        for i in 8...13 {
                            backValidDate += y[i]
                        }
                        infoClass.idValidDateMRZ = backValidDate
                    } else {
                        for i in 7...12 {
                            backValidDate += y[i]
                        }
                        infoClass.idValidDateMRZ = backValidDate
                    }
                    
                }
            }
        }
        
        var mrzText = ""
        
        let mrzArr = cominText.suffix(3)
        for key in mrzArr {
            mrzText = mrzText + key
        }
        infoClass.fullMrzKey = mrzText
        
        let joined = cominText.joined(separator: ", ")
        infoClass.bulkData = joined

        return infoClass
    }
    
    func getNameAndSurnameAndDocNo(docText: [String], indexId: Int, infoClass: FrontIdInfo, callback: @escaping (FrontIdInfo)->()) {
        if docText.count >= indexId + 5 {
            let findName = docText[indexId + 4]
            let findSurName = docText[indexId + 2]
            infoClass.idSurnameOcr = findSurName
            infoClass.idNameOcr = findName
            callback(infoClass)
        } else {
            infoClass.idSurnameOcr = ""
            infoClass.idNameOcr = ""
            callback(infoClass)
        }
    }
    
    func startPassportScanner(identImg: UIImage, cominData: FrontIdInfo, callback: @escaping (FrontIdInfo, SDKError?)->()) {
        let image = identImg
        detectHumanFace(comingPhoto: image) { [weak self] isFaceFound in
            guard let self = self else { return }
            if isFaceFound {
                if let cgImage = image.cgImage {
                    let requestHandler = VNImageRequestHandler(cgImage: cgImage)
                    
                    let recognizeTextRequest = VNRecognizeTextRequest { (request, error) in
                        guard let observations = request.results as? [VNRecognizedTextObservation] else {
                            return
                        }
                        
                        let recognizedStrings = observations.compactMap { observation in
                            observation.topCandidates(1).first?.string
                        }
                        
                        DispatchQueue.main.async {
                            let idObject = self.searchPassportMrz(docText: recognizedStrings, cominData: cominData)
                            callback(idObject, nil)
                        }
                    }
                    
                    recognizeTextRequest.recognitionLanguages = ["tr-TR"]
                    recognizeTextRequest.recognitionLevel = .accurate
                    recognizeTextRequest.minimumTextHeight = 0.005
                    
                    DispatchQueue.global(qos: .userInitiated).async {
                        do {
                            try requestHandler.perform([recognizeTextRequest])
                        } catch {
                            let err = SDKError(message: error.localizedDescription)
                            callback(FrontIdInfo(), err)
                        }
                    }
                }
            } else {
                let err = SDKError(message: "Pasaport ön yüzü geçerli değil")
                callback(FrontIdInfo(), err)
            }
        }
    }
    
    func frontScanner(identImg: UIImage, callback: @escaping (FrontIdInfo, SDKError?)->()) {
        let image = identImg
//        let image = setNewPhotoForOCR(image: identImg)
        
        
        
//        let page = CIImage(image: identImg)
//
//        let filter = AdaptiveThresholdFilter()
//
//        filter.inputImage = page
//
//        let image = filter.outputImage
//
//        let uiFix = filter.convert(cmage: image!)
        
        
            detectHumanFace(comingPhoto: image) { [weak self] isFaceFound in
            guard let self = self else { return }
            if isFaceFound {
                if let cgImage = image.cgImage {
                    let requestHandler = VNImageRequestHandler(cgImage: cgImage)
                    
                    let recognizeTextRequest = VNRecognizeTextRequest { (request, error) in
                        guard let observations = request.results as? [VNRecognizedTextObservation] else {
                            return
                        }
                        
                        let recognizedStrings = observations.compactMap { observation in
                            observation.topCandidates(1).first?.string
                        }
                        
                        DispatchQueue.main.async {
                            let idObject = self.searchValuesInArray(docText: recognizedStrings)
                            callback(idObject, nil)
                        }
                    }
                    
                    recognizeTextRequest.recognitionLanguages = ["tr-TR"]
                    recognizeTextRequest.recognitionLevel = .accurate
                    recognizeTextRequest.minimumTextHeight = 0.005
                    
                    DispatchQueue.global(qos: .userInitiated).async {
                        do {
                            try requestHandler.perform([recognizeTextRequest])
                        } catch {
                            let err = SDKError(message: error.localizedDescription)
                            callback(FrontIdInfo(), err)
                        }
                    }
                }
            } else {
                let err = SDKError(message: "Kimlik ön yüzü geçerli değil")
                callback(FrontIdInfo(), err)
            }
        }
    }
    
    
    
    
    func backScanner(identImg: UIImage, callback: @escaping (BackIdInfo, SDKError?)->()) {
//        var isIdentCard = true // back
//        let image = identImg
        
//        let page = CIImage(image: identImg)
//
//        let filter = AdaptiveThresholdFilter()
//
//        filter.inputImage = page
//
//        let image = filter.outputImage
//
//        let uiFix = filter.convert(cmage: image!)
        
        detectHumanFace(comingPhoto: identImg) { [weak self] isFaceFound in
            guard let self = self else { return }
            if !isFaceFound {
                if let cgImage = identImg.cgImage {
                    let requestHandler = VNImageRequestHandler(cgImage: cgImage, orientation: .left)
                    
                    let recognizeTextRequest = VNRecognizeTextRequest { (request, error) in
                        guard let observations = request.results as? [VNRecognizedTextObservation] else {
                            return
                        }
                        
                        let recognizedStrings = observations.compactMap { observation in
                            observation.topCandidates(1).first?.string
                        }
                        
                        DispatchQueue.main.async {
                            let idObject = self.searchValuesInBackArray(docText: recognizedStrings)
                            callback(idObject, nil)
//                            if isIdentCard == false {
//                                let err = SDKError(message: "Kimliğinizdeki barkod okunamadı, lütfen tekrar deneyin")
//                                callback(BackIdInfo(), err)
//                            } else {
//                                let idObject = self.searchValuesInBackArray(docText: recognizedStrings)
//                                callback(idObject, nil)
//                            }
                        }
                    }
                    
                    recognizeTextRequest.recognitionLanguages = ["tr-TR"]
                    recognizeTextRequest.recognitionLevel = .accurate
                    recognizeTextRequest.minimumTextHeight = 0.005
                    
//                    let barcodeRequest = VNDetectBarcodesRequest { request, error in
//                        if let error = error as NSError? {
//                            isIdentCard = false
//                            return
//                        }
//                        else {
//                            guard let observ = request.results as? [VNDetectedObjectObservation] else { return }
//                            if observ.count >= 1 {
//                                isIdentCard = true
//                                return
//                            }
//                        }
//                    }
                    
                    DispatchQueue.global(qos: .userInitiated).async {
                        do {
                            try requestHandler.perform([recognizeTextRequest])
                        } catch {
                            let err = SDKError(message: error.localizedDescription)
                            callback(BackIdInfo(), err)
                        }
                    }
                    
                }
            } else {
                let err = SDKError(message: "Kimlik arka yüzü geçerli değil")
                callback(BackIdInfo(), err)
            }
        }
    }
    
    
    private func fixSerialNo(element: String) -> String {
        var newText = ""
        if element.count != 9 {
            return ""
        } else {
            if element[1] == "D" || element[1] == "O" {
                newText = replaceWrongChr(myString: element, 1, "0")
            } else if element[1] == "Z" {
                newText = replaceWrongChr(myString: element, 1, "2")
            } else if element[1] == "S" {
                newText = replaceWrongChr(myString: element, 1, "5")
            } else if element[1] == "I" {
                newText = replaceWrongChr(myString: element, 1, "1")
            } else if element[3] == "1" && element[4] == "1" {
                let x = element.dropLast()
                newText = addNewChar(myString: String(x), 3, "M")
            }  else if element[3] == "0" {
                newText = replaceWrongChr(myString: element, 3, "O")
            } else if element[3] == "2" {
                newText = replaceWrongChr(myString: element, 3, "Z")
            } else if element[3] == "1" {
                newText = replaceWrongChr(myString: element, 3, "I")
            } else if element[3] == "5" {
                newText = replaceWrongChr(myString: element, 3, "S")
            }
            return newText
        }
    }
    
    private func addNewChar(myString: String, _ index: Int, _ newChar: Character) -> String {
        var modifiedStr = myString
        let index = modifiedStr.index(modifiedStr.startIndex, offsetBy: index)
        modifiedStr.insert(newChar, at: index)
        return modifiedStr
    }
    
    private func replaceWrongChr(myString: String, _ index: Int, _ newChar: Character) -> String {
        var chars = Array(myString)
        chars[index] = newChar
        let modifiedString = String(chars)
        return modifiedString
    }
    
    private func readMrzLine1(text: String, range: NSRange, type: BackIdInfo) {
        let idRegexLine1 = try! NSRegularExpression(pattern: "(^[A|C|I][A-Z0-9<]{1})([A-Z]{3})([A-Z0-9<]{24})")
        if (idRegexLine1.firstMatch(in: text.trimmingCharacters(in: .whitespacesAndNewlines), options: [], range: range) != nil) {
            let mrzLine1Str = text
            sdkLog(logMsg: "MRZ LINE 1 : \(mrzLine1Str)")
            mrzLine1Comp = true
            fullMrzKey = mrzLine1Str + "\n"
        }
    }
    
    private func readMrzLine2(text: String, range: NSRange, type: BackIdInfo) {
//        let idRegexLine2 = try! NSRegularExpression(pattern:"^([0-9]{7})([M|F|X|<]{1})([0-9]{7})")
        let idRegexLine2 = try! NSRegularExpression(pattern:"([0-9]{6})([0-9]{1})([M|F|X|<]{1})([0-9]{6})([0-9]{1})([A-Z]{3})([A-Z0-9<]{11})([0-9]{1})")

        if (idRegexLine2.firstMatch(in: text.trimmingCharacters(in: .whitespacesAndNewlines), options: [], range: range) != nil) {
            let mrzLine2Str = text
            sdkLog(logMsg: "MRZ LINE 2 : \(mrzLine2Str)")
            if mrzLine1Comp {
                fullMrzKey = fullMrzKey + mrzLine2Str + "\n"
                mrzLine2Comp = true
                type.idGenderMRZ = mrzLine2Str[7]
            }
        } else {
            mrzLine2Comp = true
        }
    }
    
    private func readMrzLine3(text: String, range: NSRange) {
        let idRegexLine3 = try! NSRegularExpression(pattern:"([A-Z<]{25,30})")
        if (idRegexLine3.firstMatch(in: text.trimmingCharacters(in: .whitespacesAndNewlines), options: [], range: range) != nil) {
            let mrzLine3Str = text
            sdkLog(logMsg: "MRZ LINE 3 : \(mrzLine3Str)")
            if mrzLine1Comp && mrzLine2Comp {
                fullMrzKey = fullMrzKey + mrzLine3Str
                mrzLine3Comp = true
            }
        } else {
            mrzLine3Comp = true
        }
    }
    
    public func setNewPhotoForOCR(image: UIImage) -> UIImage {
        var modifiedPhoto = UIImage()
        if let bwImg = setBWPhoto(image: image) {
            if let invertedPhoto = invertColors(image: bwImg) {
                if let brightnessPhoto = adjustContrastAndBrightness(image: invertedPhoto, contrast: 100, brightness: -40) {
                    return brightnessPhoto
                }
            }
        }
        return image
    }
    
    func calculateBrightness(image: UIImage) -> Float? {
        guard let cgImage = image.cgImage else {
            return nil
        }

        let width = cgImage.width
        let height = cgImage.height
        var totalBrightness: Float = 0.0

        guard let dataProvider = cgImage.dataProvider,
            let data = dataProvider.data,
            let pointer = CFDataGetBytePtr(data) else {
                return nil
        }

        for y in 0..<height {
            for x in 0..<width {
                let pixelInfo: Int = ((width * y) + x) * 4
                let red = Float(pointer[pixelInfo]) / 255.0
                let green = Float(pointer[pixelInfo + 1]) / 255.0
                let blue = Float(pointer[pixelInfo + 2]) / 255.0
                let brightness = (red + green + blue) / 3.0
                totalBrightness += brightness
            }
        }

        let totalPixels = width * height
        let averageBrightness = totalBrightness / Float(totalPixels)
        return averageBrightness
    }
    
    private func setBWPhoto(image: UIImage) -> UIImage? {
        if let ciImage = CIImage(image: image) {
                // CIImage'i siyah-beyaza dönüştüren bir filtre oluşturun
                let filter = CIFilter(name: "CIColorControls")!
                filter.setValue(ciImage, forKey: kCIInputImageKey)
                filter.setValue(0.0, forKey: kCIInputSaturationKey) // Renk doygunluğunu sıfıra ayarla

                if let outputImage = filter.outputImage {
                    let context = CIContext()
                    if let cgImage = context.createCGImage(outputImage, from: outputImage.extent) {
                        let grayscaleImage = UIImage(cgImage: cgImage)
                        return grayscaleImage
                    }
                }
            }
        return UIImage()
    }
    
    private func invertColors(image: UIImage) -> UIImage? {
        if let ciImage = CIImage(image: image) {
            // CIImage'i renkleri tersine çeviren bir filtre oluşturun
            let filter = CIFilter(name: "CIColorInvert")!
            filter.setValue(ciImage, forKey: kCIInputImageKey)

            if let outputImage = filter.outputImage {
                let context = CIContext()
                if let cgImage = context.createCGImage(outputImage, from: outputImage.extent) {
                    let invertedImage = UIImage(cgImage: cgImage)
                    return invertedImage
                }
            }
        }
        
        return nil
    }
    
    func adjustContrastAndBrightness(image: UIImage, contrast: Float, brightness: Float) -> UIImage? {
        if let ciImage = CIImage(image: image) {
            // CIImage'i kontrast ve parlaklık ayarı ile düzenleyen bir filtre oluşturun
            let filter = CIFilter(name: "CIColorControls")!
            filter.setValue(ciImage, forKey: kCIInputImageKey)
            filter.setValue(contrast, forKey: kCIInputContrastKey) // Kontrast ayarı
            filter.setValue(brightness, forKey: kCIInputBrightnessKey) // Parlaklık ayarı

            if let outputImage = filter.outputImage {
                let context = CIContext()
                if let cgImage = context.createCGImage(outputImage, from: outputImage.extent) {
                    let adjustedImage = UIImage(cgImage: cgImage)
                    return adjustedImage
                }
            }
        }

        return nil
    }
        
}


extension String {
    func matchForRegex(_ regex: String) -> [[String]] {
        let nsString = self as NSString
        return (try? NSRegularExpression(pattern: regex, options: []))?.matches(in: self, options: [], range: NSMakeRange(0, nsString.length)).map { match in
            (0..<match.numberOfRanges).map { match.range(at: $0).location == NSNotFound ? "" : nsString.substring(with: match.range(at: $0)) }
        } ?? []
    }
}

class AdaptiveThresholdFilter: CIFilter
{
    var inputImage : CIImage?


    var thresholdKernel =  CIColorKernel(source:
    "kernel vec4 thresholdFilter(__sample image, __sample threshold)" +
    "{" +
    "   float imageLuma = dot(image.rgb, vec3(0.2126, 0.7152, 0.0722));" +
    "   float thresholdLuma = dot(threshold.rgb, vec3(0.2126, 0.7152, 0.0722));" +

    "   return vec4(vec3(step(imageLuma, thresholdLuma)), 1.0);" +
    "}"
    )


//    override var outputImage: CIImage! {
//        guard let inputImage = inputImage,
//              let thresholdKernel = thresholdKernel else
//        {
//            return nil
//        }
//
//        let blurred = inputImage.applyingFilter("CIBoxBlur",
//                                                parameters: [kCIInputRadiusKey: 9])
//
//        let extent = inputImage.extent
//        let arguments = [inputImage, blurred]
//
//        return thresholdKernel.apply(extent: extent, arguments: arguments)
//    }
    
    override var outputImage: CIImage! {
        guard let inputImage = inputImage,
              let thresholdKernel = thresholdKernel else
        {
            return nil
        }

        let blurred = inputImage.applyingFilter("CIBoxBlur",
                                                parameters: [kCIInputRadiusKey: 9])

        let extent = inputImage.extent
        let arguments = [inputImage, blurred]

        return thresholdKernel.apply(extent: extent, arguments: arguments)
    }
    
    
    
    func convert(cmage:CIImage) -> UIImage {
        let context:CIContext = CIContext.init(options: nil)
        let cgImage:CGImage = context.createCGImage(cmage, from: cmage.extent)!
        let image:UIImage = UIImage.init(cgImage: cgImage)
        return image
    }
}
