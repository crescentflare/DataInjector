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
    
    func testSetItemOnDictionary() {
        var dictionary: [String: Any] = [
            "string": "text",
            "array": [ 0, 1, 2, 3 ],
            "dictionary": [
                "first": "stringValue",
                "second": 123,
                "third": true
            ]
        ]
        InjectorUtil.setItemOnDictionary(&dictionary, path: "string", value: "modified text")
        InjectorUtil.setItemOnDictionary(&dictionary, path: "array.2", value: 77)
        InjectorUtil.setItemOnDictionary(&dictionary, path: "dictionary.first", value: "modifiedStringValue")
        InjectorUtil.setItemOnDictionary(&dictionary, path: "dictionary.second", value: 987)
        InjectorUtil.setItemOnDictionary(&dictionary, path: "dictionary.third", value: false)
        XCTAssertEqual("modified text", InjectorUtil.itemFromDictionary(dictionary, path: "string") as? String)
        XCTAssertEqual(77, InjectorUtil.itemFromDictionary(dictionary, path: "array.2") as? Int)
        XCTAssertEqual("modifiedStringValue", InjectorUtil.itemFromDictionary(dictionary, path: "dictionary.first") as? String)
        XCTAssertEqual(987, InjectorUtil.itemFromDictionary(dictionary, path: "dictionary.second") as? Int)
        XCTAssertEqual(false, InjectorUtil.itemFromDictionary(dictionary, path: "dictionary.third") as? Bool)
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
    
    func testSetItemOnArray() {
        var array: [Any] = [
            "string",
            [
                "first": "stringValue",
                "second": 123
            ]
        ]
        InjectorUtil.setItemOnArray(&array, path: "0", value: "modified string")
        InjectorUtil.setItemOnArray(&array, path: "1.first", value: "newString")
        InjectorUtil.setItemOnArray(&array, path: "1.second", value: 2022)
        XCTAssertEqual("modified string", InjectorUtil.itemFromArray(array, path: "0") as? String)
        XCTAssertEqual("newString", InjectorUtil.itemFromArray(array, path: "1.first") as? String)
        XCTAssertEqual(2022, InjectorUtil.itemFromArray(array, path: "1.second") as? Int)
    }

}
