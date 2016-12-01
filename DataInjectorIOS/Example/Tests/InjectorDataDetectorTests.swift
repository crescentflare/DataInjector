import UIKit
import XCTest
@testable import DataInjector

class InjectorDataDetectorTests: XCTestCase {
    
    // ---
    // MARK: Test dictionary utilities
    // ---

    func testDetectFromObject() {
        XCTAssertEqual(InjectorDataType.string, InjectorDataDetector.detectFromObject("test"))
        XCTAssertEqual(InjectorDataType.number, InjectorDataDetector.detectFromObject(11))
        XCTAssertEqual(InjectorDataType.decimalNumber, InjectorDataDetector.detectFromObject(Float(16.1)))
        XCTAssertEqual(InjectorDataType.decimalNumber, InjectorDataDetector.detectFromObject(231.12))
        XCTAssertEqual(InjectorDataType.boolean, InjectorDataDetector.detectFromObject(true))
    }

    func testDetectFromString() {
        XCTAssertEqual(InjectorDataType.string, InjectorDataDetector.detectFromObject("'23 items'"))
        XCTAssertEqual(InjectorDataType.string, InjectorDataDetector.detectFromObject("Freeform string"))
        XCTAssertEqual(InjectorDataType.number, InjectorDataDetector.detectFromObject("16"))
        XCTAssertEqual(InjectorDataType.decimalNumber, InjectorDataDetector.detectFromObject("231.12"))
        XCTAssertEqual(InjectorDataType.boolean, InjectorDataDetector.detectFromObject("true"))
        XCTAssertEqual(InjectorDataType.reference, InjectorDataDetector.detectFromObject("@reference"))
        XCTAssertEqual(InjectorDataType.subReference, InjectorDataDetector.detectFromObject("@.subReference"))
    }

    func testEndOfTypeString() {
        let stringCases: [String] = [
            "'Quoted string'",
            "Freeform string 16",
            "24 string",
            "19.9,16.2",
            "false or true",
            "@reference",
            "@subReference"
        ]
        XCTAssertEqual(stringCases[0].index(stringCases[0].startIndex, offsetBy: 15), InjectorDataDetector.endOfTypeString(type: .string, value: stringCases[0]))
        XCTAssertNil(InjectorDataDetector.endOfTypeString(type: .string, value: stringCases[1]))
        XCTAssertEqual(stringCases[2].index(stringCases[2].startIndex, offsetBy: 2), InjectorDataDetector.endOfTypeString(type: .number, value: stringCases[2]))
        XCTAssertEqual(stringCases[3].index(stringCases[3].startIndex, offsetBy: 4), InjectorDataDetector.endOfTypeString(type: .decimalNumber, value: stringCases[3]))
        XCTAssertEqual(stringCases[4].index(stringCases[4].startIndex, offsetBy: 5), InjectorDataDetector.endOfTypeString(type: .boolean, value: stringCases[4]))
        XCTAssertNil(InjectorDataDetector.endOfTypeString(type: .reference, value: stringCases[5]))
        XCTAssertNil(InjectorDataDetector.endOfTypeString(type: .subReference, value: stringCases[6]))
    }

}
