import UIKit

extension Encodable {
    
    public func asDictionary() -> [String: Any] {
        do {
            let data = try JSONEncoder().encode(self)
            
            guard let dictionary = try JSONSerialization.jsonObject(with: data, options: .allowFragments) as? [String: Any] else {
                return [:]
            }
            return dictionary
            
        } catch {
            // print("AsDictionary hatası :  \(error)")
            return [:]
        }
    }
}

extension UIImage {
    
    public func toBase64() -> String? {
        guard let imageData = self.pngData() else { return nil }
        return imageData.base64EncodedString(options: Data.Base64EncodingOptions.lineLength64Characters)
    }
    
    func convert(toSize size:CGSize, scale:CGFloat) -> UIImage {
        let imgRect = CGRect(origin: CGPoint(x:0.0, y:0.0), size: size)
        UIGraphicsBeginImageContextWithOptions(size, false, scale)
        self.draw(in: imgRect)
        let copied = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return copied ?? UIImage()
    }
}

extension String {
    
    func removingWhitespaces() -> String {
        return components(separatedBy: .whitespaces).joined()
    }
    
    func toMrzDate() -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "dd.MM.yyyy"
        let date = dateFormatter.date(from: self)
        dateFormatter.dateFormat = "yyMMdd"
        return dateFormatter.string(from: date ?? Date())
    }
    
    func toNormalDate() -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyMMdd"
        let date = dateFormatter.date(from: self)
        dateFormatter.dateFormat = "dd.MM.yyyy"
        return dateFormatter.string(from: date ?? Date())
    }
}

extension String {
    subscript(i: Int) -> String {
        return String(self[index(startIndex, offsetBy: i)])
    }
}