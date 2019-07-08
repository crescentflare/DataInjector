import UIKit
import XCTest
@testable import DataInjector

class JoinStringInjectorTests: XCTestCase {
    
    // --
    // MARK: Test manual joining string with all items
    // --

    func testJoinStringWithAllItems() {
        // Set up dictionary and array
        let sampleArray = [ "Jack", "the", "Joker" ]
        let sampleDict = [
            "firstName": "John",
            "middleName": nil,
            "lastName": "Doe"
        ]
        
        // Apply manual injection to join the strings
        let arrayResult = JoinStringInjector.joinString(sourceData: sampleArray, delimiter: " ")
        let dictResult = JoinStringInjector.joinString(sourceData: sampleDict, delimiter: " ")
        
        // Verify the result
        XCTAssertEqual(arrayResult.modifiedObject as? String, "Jack the Joker")
        XCTAssertEqual(dictResult.modifiedObject as? String, "John Doe")
    }


    // --
    // MARK: Test manual joining string with a specific set of items
    // --

    func testJoinStringWithGivenItems() {
        // Set up dictionary
        let sampleDict = [
            "firstName": "John",
            "middleName": nil,
            "lastName": "Doe",
            "address": "Injectorstreet 200",
            "city": "Washington"
        ]

        // Apply manual injection to merge the name into a full name
        let result = JoinStringInjector.joinString(sourceData: sampleDict, fromItems: ["firstName", "middleName", "lastName"], delimiter: " ")
        
        // Verify the change
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
