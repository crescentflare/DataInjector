import UIKit
import XCTest
@testable import DataInjector

class DuplicateInjectorTests: XCTestCase {
    
    // --
    // MARK: Test manual injection using specified count
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
        let resultArray = result.modifiedObject as? [[String: Any?]]
        XCTAssertEqual(resultArray?.count ?? 0, duplicateCount)
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: "0.counter") as? Int, 0)
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: "1.counter") as? Int, 1)
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: "2.counter") as? Int, 2)
    }

}
