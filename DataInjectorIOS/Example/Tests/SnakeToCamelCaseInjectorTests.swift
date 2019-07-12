import UIKit
import XCTest
@testable import DataInjector

class SnakeCaseToCamelCaseInjectorTests: XCTestCase {
    
    // --
    // MARK: Test manual injection
    // --

    func testChangeCase() {
        // Set up dictionary
        let nestedDictionary = [
            "first_set": [
                "snake_case_key": "converted",
                "nocase": 10
            ],
            "second_set": [
                "another_case": nil,
                "alreadyCamelCase": true
            ]
        ]
        
        // Apply manual injection to convert the case of the dictionary keys
        let result = SnakeToCamelCaseInjector.changeCase(onData: nestedDictionary, recursive: true)
        
        // Verify the change
        let secondDict = DataInjector.get(from: result.modifiedObject, path: InjectorPath(path: "secondSet")) as! [String: Any?]
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: InjectorPath(path: "firstSet.snakeCaseKey")) as? String, "converted")
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: InjectorPath(path: "firstSet.nocase")) as? Int, 10)
        XCTAssertTrue(secondDict.keys.contains("anotherCase"))
        XCTAssertTrue(DataInjector.get(from: result.modifiedObject, path: InjectorPath(path: "secondSet.alreadyCamelCase")) as? Bool ?? false)
    }


    // --
    // MARK: Test generic injection
    // --
    
    func testSnakeCaseToCamelCaseGeneric() {
        // Set up dictionary for modification
        let sampleDict = [
            "dont_touch_this_key": [
                "snake_case_key": "First",
                "another_key": "Second"
            ]
        ]
        
        // Set up injector
        let injector = SnakeToCamelCaseInjector()
        injector.recursive = true
        injector.targetItemPath = InjectorPath(path: "dont_touch_this_key")
        
        // Apply
        let result = injector.appliedInjection(targetData: sampleDict)
        
        // Verify values
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: InjectorPath(path: "dont_touch_this_key.snakeCaseKey")) as? String, "First")
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: InjectorPath(path: "dont_touch_this_key.anotherKey")) as? String, "Second")
    }
    
}
