
import Foundation

public class IDCardRaw: Codable {
    var identId: String?
    var serialNo: String?
    var expireDate: String?
    var rawData: String?
    var birthDate: String?
    
    public init(identId: String?, serialNo: String?, expireDate: String?, rawData: String?, birthdate: String?) {
        self.identId = identId
        self.serialNo = serialNo
        self.expireDate = expireDate
        self.rawData = rawData
        self.birthDate = birthdate
    }
    
}

public class IdentifyCard: Codable {
    public var ident_id: String?
    public var name: String?
    public var surname: String?
    public var personalNumber: String?
    public var birthDate: String?
    public var expireDate: String?
    public var serialNumber: String?
    public var nationality: String?
    public var docType: String?
    public var authority: String?
    public var gender: String?
    public var image: String?
    public var mrzInfo: String?
    public var activeAuth: Bool?
    public var passiveAuth: Bool?
    public var address: String?
    public var fullName: String?
    
    public init(ident_id: String?, name: String?, surname: String?, personalNumber: String?, birthdate: String?, expireDate: String?, serialNumber: String?, nationality: String?, docType: String?, authority: String?, gender: String?, image: String?, mrzInfo: String?, activeAuth: Bool?, passiveAuth: Bool?, address: String?, fullName: String?) {
        self.ident_id = ident_id
        self.name = name
        self.surname = surname
        self.personalNumber = personalNumber
        self.birthDate = birthdate
        self.expireDate = expireDate
        self.serialNumber = serialNumber
        self.nationality = nationality
        self.docType = docType
        self.authority = authority
        self.gender = gender
        self.image = image
        self.mrzInfo = mrzInfo
        self.activeAuth = activeAuth
        self.passiveAuth = passiveAuth
        self.address = address
        self.fullName = fullName
        
    }
    
    public init() {
        self.ident_id = nil
        self.name = nil
        self.surname = nil
        self.personalNumber = nil
        self.birthDate = nil
        self.expireDate = nil
        self.serialNumber = nil
        self.nationality = nil
        self.docType = nil
        self.authority = nil
        self.gender = nil
        self.image = nil
        self.mrzInfo = nil
        self.activeAuth = nil
        self.passiveAuth = nil
        self.address = nil
        self.fullName = nil
    }
    
}

public class FrontIdInfo: Codable {
    
    public var idTcknOcr, idSurnameOcr, idNameOcr, idBirthDateOcr, idSerialNoOcr, idValidUntilOcr, fullMrzKey: String?
    
    public init() {
        self.idTcknOcr = ""
        self.idSurnameOcr = ""
        self.idNameOcr = ""
        self.idBirthDateOcr = ""
        self.idSerialNoOcr = ""
        self.idValidUntilOcr = ""
        self.fullMrzKey = ""
    }
    
    deinit {
        self.idTcknOcr = nil
        self.idSurnameOcr = nil
        self.idNameOcr = nil
        self.idBirthDateOcr = nil
        self.idSerialNoOcr = nil
        self.idValidUntilOcr = nil
        self.fullMrzKey = nil
    }
    
}

public class BackIdInfo: Codable {
    
    public var idSurnameMRZ, idNameMRZ, idMotherNameOcr, idFatherNameOcr, idIssuedByOcr, fullMrzKey, idTcknMRZ, idDocumentNumberMRZ, idBirthDateMRZ, idValidDateMRZ, idTypeMRZ, idGenderMRZ, bulkData: String?
    
    public init() {
        self.idSurnameMRZ = nil
        self.idNameMRZ = nil
        self.idMotherNameOcr = nil
        self.idFatherNameOcr = nil
        self.idIssuedByOcr = nil
        self.fullMrzKey = nil
        self.idTcknMRZ = nil
        self.idBirthDateMRZ = nil
        self.idDocumentNumberMRZ = nil
        self.idValidDateMRZ = nil
        self.idTypeMRZ = nil
        self.idGenderMRZ = nil
        self.bulkData = nil
    }
}


public class FinalIdCard: Codable {
    public var frontCard: FrontIdInfo?
    public var backCard: BackIdInfo?
    public var nfcIdCard: IdentifyCard?
    
    public init() {
        self.frontCard = nil
        self.backCard = nil
        self.nfcIdCard = nil
    }
}

public class AutoIdentStatus: Codable {
    public var identCompleteStatus: Bool?
    public var errorMessages: [String]?
    
    public init() {
        self.identCompleteStatus = nil
        self.errorMessages = nil
    }
}