import UIKit
import XCTest
@testable import DataInjector

class ReplaceNilInjectorTests: XCTestCase {
    
    // --
    // MARK: Test manual nil filtering
    // --

    func testFilterNil() {
        // Set up dictionary
        let nestedDictionary = [
            "Netherlands": [
                "Amsterdam": [
                    "centralLocation": "52.3791283, 4.8980833",
                    "harborLocation": nil
                ],
                "Rotterdam": [
                    "centralLocation": "51.9231934, 4.4676489",
                    "harborLocation": "51.9496008, 4.1430743"
                ]
            ],
            "Germany": [
                "Düsseldorf": [
                    "centralLocation": "51.2226277, 6.7866488",
                    "harborLocation": nil
                ],
                "Berlin": [
                    "centralLocation": "52.511654, 13.2737445",
                    "harborLocation": nil
                ]
            ]
        ]
        
        // Apply manual injection to filter out the nil values
        let result = ReplaceNilInjector.filterNil(onData: nestedDictionary, recursive: true)
        
        // Verify the change
        let amsterdamDict = DataInjector.get(from: result.modifiedObject, path: InjectorPath(path: "Netherlands.Amsterdam")) as! [String: Any?]
        let rotterdamDict = DataInjector.get(from: result.modifiedObject, path: InjectorPath(path: "Netherlands.Rotterdam")) as! [String: Any?]
        XCTAssertTrue(amsterdamDict.keys.contains("centralLocation"))
        XCTAssertFalse(amsterdamDict.keys.contains("harborLocation"))
        XCTAssertTrue(rotterdamDict.keys.contains("centralLocation"))
        XCTAssertTrue(rotterdamDict.keys.contains("harborLocation"))
    }


    // --
    // MARK: Test manual nil replacement
    // --

    func testReplaceNil() {
        // Set up dictionary
        let nestedDictionary = [
            "Vegetables": [
                [
                    "name": "Broccoli",
                    "description": "A healthy vegetable containing many nutrients"
                ],
                [
                    "name": "Carrots",
                    "description": nil
                ]
            ],
            "Meat": [
                [
                    "name": "Burger"
                ]
            ],
            "Deserts": [ nil ]
        ]

        // Set up defaults
        let defaults = [
            "Vegetables": [
                [
                    "name": "Untitled",
                    "description": "No description given"
                ]
            ],
            "Meat": [
                [
                    "name": "Untitled",
                    "description": "No description given"
                ]
            ],
            "Deserts": [
                [
                    "name": "Untitled",
                    "description": "No description given"
                ]
            ]
        ]
        
        // Apply manual injection to replace the nil values
        let result = ReplaceNilInjector.replaceNil(onData: nestedDictionary, replaceData: defaults, recursive: true, ignoreNotExisting: false)
        
        // Verify the change
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: InjectorPath(path: "Vegetables.0.description")) as? String, "A healthy vegetable containing many nutrients")
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: InjectorPath(path: "Vegetables.1.description")) as? String, "No description given")
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: InjectorPath(path: "Meat.0.description")) as? String, "No description given")
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: InjectorPath(path: "Deserts.0.name")) as? String, "Untitled")
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: InjectorPath(path: "Deserts.0.description")) as? String, "No description given")
    }
    

    // --
    // MARK: Test generic injection
    // --
    
    func testReplaceNilGeneric() {
        // Set up dictionary for modification
        let sampleDict = [
            "title": "Data Injector",
            "text": "A library to easily manipulate data",
            "description": nil
        ]
        
        // Set up dictionary for the data source
        let dataSource = [
            "title": "Untitled",
            "text": "...",
            "description": "No description given",
            "status": "Unknown"
        ]
        
        // Set up injector
        let injector = ReplaceNilInjector()
        injector.recursive = true
        injector.ignoreNotExisting = true
        
        // Apply
        let result = injector.appliedInjection(targetData: sampleDict, sourceData: dataSource)
        
        // Verify values
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: InjectorPath(path: "title")) as? String, sampleDict["title"])
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: InjectorPath(path: "text")) as? String, sampleDict["text"])
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: InjectorPath(path: "description")) as? String, dataSource["description"])
        XCTAssertNil(DataInjector.get(from: result.modifiedObject, path: InjectorPath(path: "status")) as? String)
    }
    
}
