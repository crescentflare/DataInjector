import UIKit
import XCTest
@testable import DataInjector

class InjectorUtilTests: XCTestCase {
    
    // ---
    // MARK: Test dictionary utilities
    // ---

    func testItemFromDictionary() {
        let dictionary: [String: Any] = [
            "first": "string",
            "second": 10,
            "third": [
                "nested": "value",
                "deeplynested": [
                    "value": 11.1
                ]
            ],
            "fourth": true
        ]
        XCTAssertEqual("string", InjectorUtil.itemFromDictionary(dictionary, path: "first") as? String)
        XCTAssertEqual(10, InjectorUtil.itemFromDictionary(dictionary, path: "second") as? Int)
        XCTAssertEqual("value", InjectorUtil.itemFromDictionary(dictionary, path: "third.nested") as? String)
        XCTAssertEqual(11.1, InjectorUtil.itemFromDictionary(dictionary, path: "third.deeplynested.value") as? Double)
        XCTAssertEqual(true, InjectorUtil.itemFromDictionary(dictionary, path: "fourth") as? Bool)
        XCTAssertNil(InjectorUtil.itemFromDictionary(dictionary, path: "invalidpath"))
    }
    
    
    // ---
    // MARK: Test array utilities
    // ---

    func testItemFromArray() {
        let array: [Any] = [
            "zero",
            "one",
            [ "a", "b", "c", "d" ],
            "three",
            [
                "test": "dictionary",
                "dictarray": [ "first", "second", "third" ]
            ]
        ]
        XCTAssertEqual("zero", InjectorUtil.itemFromArray(array, path: "0") as? String)
        XCTAssertEqual("one", InjectorUtil.itemFromArray(array, path: "1") as? String)
        XCTAssertEqual("b", InjectorUtil.itemFromArray(array, path: "2.1") as? String)
        XCTAssertEqual("three", InjectorUtil.itemFromArray(array, path: "3") as? String)
        XCTAssertEqual("dictionary", InjectorUtil.itemFromArray(array, path: "4.test") as? String)
        XCTAssertEqual("third", InjectorUtil.itemFromArray(array, path: "4.dictarray.2") as? String)
    }

}
