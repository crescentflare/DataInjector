import UIKit
import XCTest
@testable import DataInjector

class DataInjectorTests: XCTestCase {
    
    // --
    // MARK: Test injecting in a nested dictionary
    // --

    func testInjectDicts() {
        // Set up dictionary
        var inventoryDict = [
            "tools": [
                "hand": [
                    "hammer": "9.95",
                    "screwDriver": "5.45"
                ],
                "machine": [
                    "drill": "59.95"
                ]
            ],
            "clothing": [
                "shirts": [
                    "small": "9.95",
                    "medium": "10.95",
                    "large": "11.95"
                ],
                "trousers": [
                    "small": "48.95",
                    "medium": "49.95",
                    "large": "50.95"
                ],
                "jackets": [
                    "small": "74.95",
                    "medium": "79.95",
                    "large": "84.95"
                ]
            ]
        ]
        
        // Apply manual injection to change the price
        let result = DataInjector.inject(into: inventoryDict, path: "clothing.shirts.small", modifyCallback: { originalData in
            return InjectorResult(withModifiedObject: "8.95")
        })

        // Check that only the injection target has been modified
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: "clothing.shirts.small") as! String, "8.95")
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: "clothing.shirts.large") as! String, "11.95")

        // The original dictionary structure shouldn't be modified
        XCTAssertEqual(inventoryDict["clothing"]!["shirts"]!["small"], "9.95")

        // When changing the original data, the modified result should not change
        inventoryDict["clothing"]!["trousers"]!["medium"] = "39.95"
        XCTAssertEqual(inventoryDict["clothing"]!["trousers"]!["medium"], "39.95")
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: "clothing.trousers.medium") as! String, "49.95")
    }
    
    
    // --
    // MARK: Test injecting in a nested array
    // --
    
    func testInjectArrays() {
        // Set up array
        var nestedNumbers = [
            [ 0, 2, 4, 6, 8 ],
            [ 1, 3, 6, 7, 9 ] // Intentionally put a wrong number here
        ]

        // Apply manual injection to change the number
        let result = DataInjector.inject(into: nestedNumbers, path: "1.2", modifyCallback: { originalData in
            return InjectorResult(withModifiedObject: 5)
        })

        // Check that only the injection target has been modified
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: "1.2") as! Int, 5)
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: "1.3") as! Int, 7)

        // The original array structure shouldn't be modified
        XCTAssertEqual(nestedNumbers[1][2], 6)
        
        // When changing the original data, the modified result should not change
        nestedNumbers[0][4] = 10
        XCTAssertEqual(nestedNumbers[0][4], 10)
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: "0.4") as! Int, 8)
    }
    
    
    // --
    // MARK: Test injecting in a mixed structure
    // --
    
    func testInjectMixed() {
        // Set up structure
        let randomItems: [Any] = [
            [ 0, 1, 2, 3, 5, 8 ],
            [
                "first": "1st",
                "second": "4th",
                "third": "3rd"
            ]
        ]

        // Apply manual injection to change the dictionary
        let result = DataInjector.inject(into: randomItems, path: "1.second", modifyCallback: { originalData in
            return InjectorResult(withModifiedObject: "2nd")
        })
        
        // Check the result
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: "1.second") as! String, "2nd")
    }

}
