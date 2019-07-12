import UIKit
import XCTest
@testable import DataInjector

class InjectorPathTests: XCTestCase {
    
    // --
    // MARK: Test creation
    // --

    func testCreate() {
        // Create and test through string
        XCTAssertEqual(InjectorPath(path: "").asStringPath(), "")
        XCTAssertEqual(InjectorPath(path: "item.subItem").asStringPath(separator: "."), "item.subItem")
        XCTAssertEqual(InjectorPath(path: "item/subItem", separator: "/").asStringPath(separator: "/"), "item/subItem")

        // Create and test through components
        XCTAssertEqual(InjectorPath(pathComponents: ["item", "subItem"]).asStringPath(separator: "."), "item.subItem")
    }
    
    
    // --
    // MARK: Test data access
    // --
    
    func testElements() {
        // Case with a simple nested path
        var path = InjectorPath(path: "item.subItem")
        XCTAssertTrue(path.hasElements())
        XCTAssertEqual(path.firstElement(), "item")
        XCTAssertTrue(path.hasNextElement())
        XCTAssertEqual(path.nextElement(), "subItem")
    
        // Case with only a single path
        path = InjectorPath(path: "item")
        XCTAssertTrue(path.hasElements())
        XCTAssertEqual(path.firstElement(), "item")
        XCTAssertFalse(path.hasNextElement())
        XCTAssertNil(path.nextElement())
    
        // Case with an empty path
        path = InjectorPath()
        XCTAssertFalse(path.hasElements())
        XCTAssertNil(path.firstElement())
        XCTAssertFalse(path.hasNextElement())
        XCTAssertNil(path.nextElement())
    
        // Case with a deeper path
        path = InjectorPath(path: "item.subItem.deeperItem")
        XCTAssertTrue(path.hasElements())
        XCTAssertEqual(path.firstElement(), "item")
        XCTAssertTrue(path.hasNextElement())
        XCTAssertEqual(path.nextElement(), "subItem")
    }


    // --
    // MARK: Test traversal
    // --
    
    func testTraversal() {
        let path = InjectorPath(path: "item.subItem.deeperItem")
        let deeperPath = path.deeperPath()
        XCTAssertEqual(deeperPath.asStringPath(), "subItem.deeperItem")
    }
    
    func testSeekPathForMap() {
        // Set up mixed structure
        let mixedStructure = [
            "furniture": [
                [
                    "$marker": "chair",
                    "type": "chair",
                    "price": "51.95"
                ],
                [
                    "type": "table",
                    "price": "132.95"
                ]
            ],
            "lighting": [
                [
                    "type": "lightBulb",
                    "price": "9.95"
                ],
                [
                    "$marker": "sensor",
                    "type": "sensor",
                    "price": "19.95"
                ]
            ]
        ]

        // Check if the path can be indexed to the marker items
        XCTAssertEqual(InjectorPath.seekPathForMap(data: mixedStructure, markerKey: "$marker", value: "chair")?.asStringPath(), "furniture.0");
        XCTAssertEqual(InjectorPath.seekPathForMap(data: mixedStructure, markerKey: "$marker", value: "sensor")?.asStringPath(), "lighting.1");
    }

}
