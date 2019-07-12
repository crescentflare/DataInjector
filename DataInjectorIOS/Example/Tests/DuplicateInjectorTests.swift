import UIKit
import XCTest
@testable import DataInjector

class DuplicateInjectorTests: XCTestCase {
    
    // --
    // MARK: Test manual injection using a specified amount
    // --

    func testDuplicateItemFromCount() {
        // Set up array
        let simpleArray = [
            [
                "key": "value",
                "counter": 0
            ]
        ]
        
        // Apply manual injection to duplicate the item and match the counter of the injected item with the index
        let duplicateCount = 3
        let result = DuplicateInjector.duplicateItem(inArray: simpleArray, count: duplicateCount, duplicateCallback: { duplicatedItem, duplicateIndex in
            return DataInjector.inject(into: duplicatedItem, path: InjectorPath(path: "counter"), modifyCallback: { originalData in
                return InjectorResult(withModifiedObject: duplicateIndex)
            })
        })
        
        // Verify amount of items
        let resultArray = result.modifiedObject as? [Any?]
        XCTAssertEqual(resultArray?.count ?? 0, duplicateCount)
        
        // Verify the counter
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: "0.counter") as? Int, 0)
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: "1.counter") as? Int, 1)
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: "2.counter") as? Int, 2)
    }


    // --
    // MARK: Test manual injection using a data source
    // --
    
    func testDuplicateItemFromData() {
        // Set up array for modification
        let templateArray = [
            [
                "type": "vehicle",
                "name": "$template"
            ]
        ]
        
        // Set up array for the data source
        let dataSource = [ "Car", "Van", "Bike", "Truck" ]
        
        // Apply manual injection to duplicate the item and match the name value to the source item
        let result = DuplicateInjector.duplicateItem(inArray: templateArray, sourceArray: dataSource, duplicateCallback: { duplicatedItem, sourceItem in
            return DataInjector.inject(into: duplicatedItem, path: InjectorPath(path: "name"), modifyCallback: { originalData in
                return InjectorResult(withModifiedObject: sourceItem)
            })
        })

        // Verify amount of items
        let resultArray = result.modifiedObject as? [Any?]
        XCTAssertEqual(resultArray?.count ?? 0, dataSource.count)
        
        // Verify name insertion
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: "0.name") as? String, dataSource[0])
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: "1.name") as? String, dataSource[1])
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: "2.name") as? String, dataSource[2])
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: "3.name") as? String, dataSource[3])
    }
    

    // --
    // MARK: Test generic injection with a sub injector
    // --
    
    func testDuplicateItemGeneric() {
        // Set up array for modification
        let templateArray = [
            [
                "title": "$template",
                "subItems": [
                    [
                        "type": "item",
                        "text": "$template"
                    ],
                    [
                        "type": "divider"
                    ],
                    [
                        "type": "empty",
                        "text": "There are no items"
                    ]
                ]
            ]
        ]
        
        // Set up array for the data source
        let dataSource = [
            [
                "section": "Colors",
                "values": [ "Red", "Green", "Blue", "Yellow", "Cyan", "Magenta" ]
            ],
            [
                "section": "Shapes",
                "values": [ "Square", "Circle" ]
            ],
            [
                "section": "Available",
                "values": []
            ]
        ]
        
        // Set up main data injector for sections, including a custom injector to modify the section title
        let injector = DuplicateInjector()
        let sectionValueInjector = CustomInjector(targetKey: "title", sourceKey: "section")

        // Set up sub injector for sub items, including a custom injector to modify the text
        let duplicateSubItemInjector = DuplicateInjector()
        let itemValueInjector = CustomInjector(targetKey: "text")
        duplicateSubItemInjector.targetItemPath = InjectorPath(path: "subItems")
        duplicateSubItemInjector.sourceDataPath = InjectorPath(path: "values")
        duplicateSubItemInjector.betweenItemIndex = 1
        duplicateSubItemInjector.emptyItemIndex = 2
        duplicateSubItemInjector.subInjectors = [itemValueInjector]
        
        // Link sub injectors and apply
        injector.subInjectors = [sectionValueInjector, duplicateSubItemInjector]
        let result = injector.appliedInjection(targetData: templateArray, sourceData: dataSource)
        
        // Verify amount of items and sub items
        let resultSectionArray = result.modifiedObject as? [Any?]
        XCTAssertEqual(resultSectionArray?.count ?? 0, dataSource.count)
        XCTAssertEqual((DataInjector.get(from: result.modifiedObject, path: InjectorPath(path: "0.subItems")) as? [Any?])?.count ?? -1, 6 * 2 - 1)
        XCTAssertEqual((DataInjector.get(from: result.modifiedObject, path: InjectorPath(path: "1.subItems")) as? [Any?])?.count ?? -1, 2 * 2 - 1)
        XCTAssertEqual((DataInjector.get(from: result.modifiedObject, path: InjectorPath(path: "2.subItems")) as? [Any?])?.count ?? -1, 1)
        
        // Verify some deep nested items
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: InjectorPath(path: "1.title")) as? String, "Shapes")
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: InjectorPath(path: "0.subItems.2.text")) as? String, "Green")
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: InjectorPath(path: "0.subItems.3.type")) as? String, "divider")
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: InjectorPath(path: "1.subItems.0.text")) as? String, "Square")
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: InjectorPath(path: "2.subItems.0.text")) as? String, "There are no items")
    }


    // --
    // MARK: Custom set value injector
    // --
    
    fileprivate class CustomInjector: BaseInjector {
        
        var targetKey = ""
        var sourceKey: String?
        
        init(targetKey: String, sourceKey: String? = nil) {
            self.targetKey = targetKey
            self.sourceKey = sourceKey
        }
        
        override func appliedInjection(targetData: Any?, sourceData: Any? = nil) -> InjectorResult {
            return DataInjector.inject(into: targetData, path: InjectorPath(path: targetKey), modifyCallback: { originalData in
                var modifiedData = sourceData
                if let sourceKey = sourceKey {
                    modifiedData = DataInjector.get(from: sourceData, path: InjectorPath(path: sourceKey))
                }
                return InjectorResult(withModifiedObject: modifiedData)
            })
        }
        
    }
    
}
