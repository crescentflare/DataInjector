import UIKit
import XCTest
@testable import DataInjector

class StringifyTransformerTests: XCTestCase {
    
    // --
    // MARK: Test manual transformation
    // --

    func testStringify() {
        // Set up data
        let sampleData: [String: Any] = [
            "name": "MacBook Pro",
            "type": "laptop",
            "brand": "Apple",
            "features": ["Fingerprint", "Touchbar", "Camera"],
            "price": [
                "currency": "USD",
                "value": 1299
            ]
        ]
        
        // Test default string conversion (full string checking is not possible due to the random order of the map elements)
        var result = StringifyTransformer.stringify(fromData: sampleData)
        XCTAssertTrue((result.modifiedObject as? String)?.contains("\"name\":\"MacBook Pro\"") ?? false)
        XCTAssertTrue((result.modifiedObject as? String)?.contains("\"features\":[\"Fingerprint\",\"Touchbar\",\"Camera\"]") ?? false)
        XCTAssertTrue((result.modifiedObject as? String)?.contains("\"value\":1299") ?? false)

        // Test conversion with spaces
        result = StringifyTransformer.stringify(fromData: sampleData, includeSpaces: true)
        XCTAssertTrue((result.modifiedObject as? String)?.contains("\"type\": \"laptop\"") ?? false)
        XCTAssertTrue((result.modifiedObject as? String)?.contains("\"features\": [\"Fingerprint\", \"Touchbar\", \"Camera\"]") ?? false)
        XCTAssertTrue((result.modifiedObject as? String)?.contains("\"currency\": \"USD\"") ?? false)

        // Test conversion with newlines
        result = StringifyTransformer.stringify(fromData: sampleData, includeSpaces: true, includeNewlines: true)
        XCTAssertTrue((result.modifiedObject as? String)?.contains("\"brand\": \"Apple\"") ?? false)
        XCTAssertTrue((result.modifiedObject as? String)?.contains("{\n  ") ?? false)
        XCTAssertTrue((result.modifiedObject as? String)?.contains("  \"price\": {\n    ") ?? false)
        XCTAssertTrue((result.modifiedObject as? String)?.contains("    \"value\": 1299") ?? false)
        XCTAssertTrue((result.modifiedObject as? String)?.contains("  \"features\": [\n    \"Fingerprint\",\n    \"Touchbar\",\n    \"Camera\"\n  ]") ?? false)
    }


    // --
    // MARK: Test generic transformation
    // --
    
    func testStringifyGeneric() {
        // Set up data
        let sampleData = [
            [ "type": "desktop" ],
            [ "type": "laptop" ],
            [ "type": "tablet" ]
        ]

        // Set up transformer
        let transformer = StringifyTransformer()
        transformer.includeSpaces = true
        transformer.includeNewlines = true

        // Apply
        let result = transformer.appliedTransformation(sourceData: sampleData)
        
        // Verify result
        XCTAssertEqual(result.modifiedObject as? String, "[\n  {\n    \"type\": \"desktop\"\n  },\n  {\n    \"type\": \"laptop\"\n  },\n  {\n    \"type\": \"tablet\"\n  }\n]")
    }
    
}
