import UIKit
import XCTest
@testable import DataInjector

class ValueInjectorTests: XCTestCase {
    
    // --
    // MARK: Test manual injection
    // --

    func testSetValue() {
        // Set up dictionary
        let nestedDictionary = [
            "San Francisco": [
                "country": "USA",
                "language": "English"
            ],
            "Nice": [
                "country": "France",
                "language": "French"
            ],
            "Madrid": [
                "country": "Spain",
                "language": "Spanish"
            ]
        ]
        
        // Apply manual injection to change the language of one of the cities
        let result = ValueInjector.setValue(inData: nestedDictionary, path: InjectorPath(path: "Madrid.language"), value: "German")
        
        // Verify the change
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: InjectorPath(path: "Madrid.language")) as? String, "German")
        
        // Nil value tests
        XCTAssertNil(ValueInjector.setValue(inData: nestedDictionary, path: InjectorPath(path: "Nice.language"), value: nil, allowNil: true).error)
        XCTAssertEqual(ValueInjector.setValue(inData: nestedDictionary, path: InjectorPath(path: "Nice.language"), value: nil, allowNil: false).error, InjectorError.nilNotAllowed)
    }


    // --
    // MARK: Test generic injection
    // --
    
    func testSetValueGeneric() {
        // Set up array for modification
        let templateArray = [
            [
                "title": "$template",
                "text": "$template"
            ],
            [
                "title": "$template",
                "text": "$template"
            ]
        ]
        
        // Set up array for the data source
        let dataSource = [
            [
                "product": "Apple",
                "description": "A juicy apple, freshly picked"
            ],
            [
                "product": "Strawberry",
                "description": "A set of tasty strawberries, available this week"
            ]
        ]
        
        // Set up the injectors for the 2 array items
        var injectors = [BaseInjector]()
        for productIndex in dataSource.indices {
            let titleInjector = ValueInjector()
            let textInjector = ValueInjector()
            titleInjector.targetItemPath = InjectorPath(path: "\(productIndex).title")
            titleInjector.sourceDataPath = InjectorPath(path: "\(productIndex).product")
            textInjector.targetItemPath = InjectorPath(path: "\(productIndex).text")
            textInjector.sourceDataPath = InjectorPath(path: "\(productIndex).description")
            injectors.append(titleInjector)
            injectors.append(textInjector)
        }
        
        // Apply
        var result = InjectorResult(withModifiedObject: templateArray)
        for injector in injectors {
            result = injector.appliedInjection(targetData: result.modifiedObject, sourceData: dataSource)
        }
        
        // Verify titles and descriptions
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: InjectorPath(path: "0.title")) as? String, "Apple")
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: InjectorPath(path: "0.text")) as? String, "A juicy apple, freshly picked")
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: InjectorPath(path: "1.title")) as? String, "Strawberry")
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: InjectorPath(path: "1.text")) as? String, "A set of tasty strawberries, available this week")
    }
    
}
