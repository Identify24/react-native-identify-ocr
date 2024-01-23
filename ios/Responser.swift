
import Foundation
import UIKit


public class FirstRoom: Codable {
    public var id: String?
    public var status: String?
    public var form_uid: String?
    public var created_at: String?
    public var created_by: String?
    public var customer_id: String?
    public var customer_uid: String?
    public var language: String?
    public var sign_language: String?
    public var liveness: [Int]?
    public var modules: [String]?
    public var identification_type: String?
    public var project_id: String?
    
    init() {
        self.id = ""
        self.status = ""
        self.form_uid = ""
        self.created_at = ""
        self.created_by = ""
        self.customer_id = ""
        self.customer_uid = ""
        self.language = ""
        self.sign_language = ""
        self.liveness = []
        self.modules = []
        self.identification_type = ""
        self.project_id = ""
    }

    init(id: String?, status: String?, form_uid: String?, created_at: String?, created_by: String?, customer_id: String?, customer_uid: String?, language: String?, sign_language: String?, liveness: [Int]?, modules: [String]?, identification_type: String?, project_id: String?) {
        self.id = id
        self.status = status
        self.form_uid = form_uid
        self.created_at = created_at
        self.created_by = created_by
        self.customer_id = customer_id
        self.customer_uid = customer_uid
        self.language = language
        self.sign_language = sign_language
        self.liveness = liveness
        self.modules = modules
        self.identification_type = identification_type
        self.project_id = project_id
    }
    
}

public class RoomResponse: Codable {
    public var result: Bool?
    public var response_status: Int?
    public var messages: [String]?
    public var data: FirstRoom?
    public var allowed_content_types: String?
    
    init(result: Bool?, response_status: Int?, messages: [String]?, data: FirstRoom?, allowed_content_types: String?) {
        self.result = result
        self.response_status = response_status
        self.messages = messages
        self.data = data
        self.allowed_content_types = allowed_content_types
    }

    init() {
        self.result = false
        self.response_status = 0
        self.messages = [String]()
        self.data = FirstRoom()
        self.allowed_content_types = ""
    }
}

public class EmptyResponse: Codable {
    var result: Bool?
    var messages: [String]?
    var data: SMSData?

    init(result: Bool?, messages: [String]?, data: SMSData?) {
        self.result = result
        self.messages = messages
        self.data = data
    }
}

public class SMSData: Codable {
    var id: String?
    init(id: String?) {
        self.id = id
    }
}

public class SmsJson: Codable {
    var tid: String?
    var tan: String?

    init(tid: String?, tan: String?) {
        self.tid = tid
        self.tan = tan
    }
}

public class BoolResponse: Codable {
    public var result: Bool?
    public var msg: String?
    
    init(result: Bool?, msg: String?) {
        self.result = result
        self.msg = msg
    }
    public init() {}
}


public class PassportModel {

    public var documentImage: UIImage? = UIImage()
    public var documentType: String = ""
    public var countryCode: String = ""
    public var surnames: String = ""
    public var givenNames: String = ""
    public var documentNumber: String = ""
    public var nationality: String = ""
    public var birthDate: Date? = Date()
    public var sex: String = ""
    public var expiryDate: Date? = Date()
    public var personalNumber: String = ""

    public init() { }

    public init(documentNumber: String, birthDate: Date, expiryDate: Date) {
        self.documentNumber = documentNumber
        self.birthDate = birthDate
        self.expiryDate = expiryDate
    }

}

class Modules: Decodable {
    public var mName: String?
    public var mValue: SdkModules?
}

public class SDKWebError: Decodable {
    public var errorMessages: String?
    
    public init(message: String) {
        self.errorMessages = message
    }
}

public class SDKError: Decodable {
    public var errorMessages: String?
    
    public init(message: String) {
        self.errorMessages = message
    }
}

public class SDKKpsData: Decodable {
    public var birthDate, validDate, serialNo: String?
    
    public init(birthDate: String, validDate: String, serialNo: String) {
        self.birthDate = birthDate
        self.validDate = validDate
        self.serialNo = serialNo
    }
}