import UIKit
import XCTest
@testable import DataInjector

class UrlEncodeTransformerTests: XCTestCase {
    
    // --
    // MARK: Test manual transformation
    // --

    func testUrlEncode() {
        let result = UrlEncodeTransformer.encode(fromData: "First / Second")
        XCTAssertEqual(result.modifiedObject as? String, "First+%2F+Second")
    }


    // --
    // MARK: Test generic transformation
    // --
    
    func testUrlEncodeGeneric() {
        // Set up data
        let sampleData = [
            "value": "First / Second"
        ]

        // Set up transformer and apply
        let transformer = UrlEncodeTransformer()
        transformer.sourceDataPath = InjectorPath(path: "value")
        let result = transformer.appliedTransformation(sourceData: sampleData)
        
        // Verify result
        XCTAssertEqual(result.modifiedObject as? String, "First+%2F+Second")
    }
    
}
