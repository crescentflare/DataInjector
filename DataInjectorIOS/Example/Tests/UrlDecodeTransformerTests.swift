import UIKit
import XCTest
@testable import DataInjector

class UrlDecodeTransformerTests: XCTestCase {
    
    // --
    // MARK: Test manual transformation
    // --

    func testUrlDecode() {
        let result = UrlDecodeTransformer.decode(fromData: "First+%2F+Second")
        XCTAssertEqual(result.modifiedObject as? String, "First / Second")
    }


    // --
    // MARK: Test generic transformation
    // --
    
    func testUrlDecodeGeneric() {
        // Set up data
        let sampleData = [
            "value": "First+%2F+Second"
        ]

        // Set up transformer and apply
        let transformer = UrlDecodeTransformer()
        transformer.sourceDataPath = InjectorPath(path: "value")
        let result = transformer.appliedTransformation(sourceData: sampleData)
        
        // Verify result
        XCTAssertEqual(result.modifiedObject as? String, "First / Second")
    }
    
}
