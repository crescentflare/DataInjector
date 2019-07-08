import UIKit
import XCTest
@testable import DataInjector

class LinkDataInjectorTests: XCTestCase {
    
    // --
    // MARK: Test manual injection on a single item
    // --

    func testLinkDataItem() {
        // Set up dictionary and lookup array
        let targetDict = [
            "name": "John Doe",
            "statusId": "1"
        ]
        let sourceArray = [
            [
                "statusId": "0",
                "status": "ready"
            ],
            [
                "statusId": "1",
                "status": "progress"
            ],
            [
                "statusId": "2",
                "status": "done"
            ]
        ]
        
        // Apply manual injection to link one of the statuses
        let result = LinkDataInjector.linkData(inDictionary: targetDict, fromArray: sourceArray, usingKey: "statusId")
        
        // Verify the linked status text
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: "status") as? String, "progress")
    }


    // --
    // MARK: Test manual injection on an array of items
    // --
    
    func testDuplicateItemFromData() {
        // Set up data and lookup arrays
        let targetArray = [
            [
                "name": "John Doe",
                "statusId": "1"
            ],
            [
                "name": "Jack the Joker",
                "statusId": "0"
            ],
            [
                "name": "Mary-Anne Adams",
                "statusId": "2"
            ]
        ]
        let sourceArray = [
            [
                "statusId": "0",
                "status": "ready"
            ],
            [
                "statusId": "1",
                "status": "progress"
            ],
            [
                "statusId": "2",
                "status": "done"
            ]
        ]

        // Apply manual injection to link the statuses
        let result = LinkDataInjector.linkData(onArray: targetArray, fromArray: sourceArray, usingKey: "statusId")

        // Verify status texts
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: "0.status") as? String, "progress")
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: "1.status") as? String, "ready")
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: "2.status") as? String, "done")
    }
    

    // --
    // MARK: Test generic injection
    // --
    
    func testLinkDataGeneric() {
        // Set up array for modification
        let originalArray = [
            [
                "name": "John Doe",
                "statusId": "1"
            ],
            [
                "name": "Jack the Joker",
                "statusId": "0"
            ],
            [
                "name": "Mary-Anne Adams",
                "statusId": "2"
            ]
        ]
        
        // Set up array for the data source
        let dataSource = [
            [
                "statusId": "0",
                "status": "ready"
            ],
            [
                "statusId": "1",
                "status": "progress"
            ],
            [
                "statusId": "2",
                "status": "done"
            ]
        ]
        
        // Set up injector
        let injector = LinkDataInjector()
        injector.linkKey = "statusId"
        
        // Apply
        let result = injector.appliedInjection(targetData: originalArray, sourceData: dataSource)
        
        // Verify status texts
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: "0.status") as? String, "progress")
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: "1.status") as? String, "ready")
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: "2.status") as? String, "done")
    }
    
}
