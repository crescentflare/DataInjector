import UIKit
import XCTest
@testable import DataInjector

class JoinStringInjectorTests: XCTestCase {
    
    // --
    // MARK: Test manual joining string with an array
    // --

    func testJoinStringFromArray() {
        // Set up array
        let sampleArray = [ "Jack", "the", "Joker" ]
        
        // Apply manual injection to join the strings
        let result = JoinStringInjector.joinString(fromArray: sampleArray, delimiter: " ")
        
        // Verify the result
        XCTAssertEqual(result.modifiedObject as? String, "Jack the Joker")
    }


    // --
    // MARK: Test manual joining string with a dictionary
    // --

    func testJoinStringFromDictionary() {
        // Set up dictionary
        let sampleDict = [
            "firstName": "John",
            "middleName": nil,
            "lastName": "Doe"
        ]
        
        // Apply manual injection to join the strings
        let result = JoinStringInjector.joinString(fromDictionary: sampleDict, fromItems: ["firstName", "middleName", "lastName"], delimiter: " ")
        
        // Verify the result
        XCTAssertEqual(result.modifiedObject as? String, "John Doe")
    }
    

    // --
    // MARK: Test generic injection
    // --
    
    func testJoinStringGeneric() {
        // Set up dictionary for modification
        let sampleDict = [
            "firstName": "Jack",
            "middleName": "the",
            "lastName": "Joker",
            "address": "Injectorstreet 200",
            "city": "Washington"
        ]
        
        // Set up injector
        let injector = JoinStringInjector()
        injector.targetItemPath = InjectorPath(path: "fullName")
        injector.fromItems = [ "firstName", "middleName", "lastName" ]
        injector.delimiter = " "
        
        // Apply
        let result = injector.appliedInjection(targetData: sampleDict, sourceData: sampleDict)
        
        // Verify values
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: InjectorPath(path: "fullName")) as? String, "Jack the Joker")
    }
    
}
