import UIKit
import XCTest
@testable import DataInjector

class JoinStringTransformerTests: XCTestCase {
    
    // --
    // MARK: Test manual joining string with an array
    // --

    func testJoinStringFromArray() {
        // Set up array
        let sampleArray = [ "Jack", "the", "Joker" ]
        
        // Apply manual transformation to join the strings
        let result = JoinStringTransformer.joinString(fromArray: sampleArray, delimiter: " ")
        
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
        let result = JoinStringTransformer.joinString(fromDictionary: sampleDict, fromItems: ["firstName", "middleName", "lastName"], delimiter: " ")
        
        // Verify the result
        XCTAssertEqual(result.modifiedObject as? String, "John Doe")
    }
    

    // --
    // MARK: Test generic transformation
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
        let transformer = JoinStringTransformer()
        transformer.fromItems = [ "firstName", "middleName", "lastName" ]
        transformer.delimiter = " "
        
        // Apply
        let result = transformer.appliedTransformation(sourceData: sampleDict)
        
        // Verify values
        XCTAssertEqual(result.modifiedObject as? String, "Jack the Joker")
    }
    
}
