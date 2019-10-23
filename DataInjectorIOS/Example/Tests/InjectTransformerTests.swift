import UIKit
import XCTest
@testable import DataInjector

class InjectTransformerTests: XCTestCase {
    
    // --
    // MARK: Test manual transformation
    // --

    func testInject() {
        // Set up dictionary
        let sampleDict = [
            "firstName": "John",
            "middleName": nil,
            "lastName": "Doe"
        ]

        // Apply manual transformation to inject a value
        let valueInjector = ValueInjector()
        valueInjector.targetItemPath = InjectorPath(path: "fullName")
        valueInjector.value = "John Doe"
        let result = InjectTransformer.inject(intoData: sampleDict, injectors: [valueInjector])
        
        // Verify the result
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: InjectorPath(path: "fullName")) as? String, "John Doe")
    }


    // --
    // MARK: Test generic transformation
    // --
    
    func testInjectGeneric() {
        // Set up dictionary for modification
        let sampleDict = [
            "firstName": "John",
            "middleName": nil,
            "lastName": "Doe"
        ]
        
        // Set up injector with transformer
        let valueInjector = ValueInjector()
        let joinStringTransformer = JoinStringTransformer()
        valueInjector.targetItemPath = InjectorPath(path: "fullName")
        joinStringTransformer.fromItems = [ "firstName", "middleName", "lastName" ]
        joinStringTransformer.delimiter = " "
        valueInjector.sourceTransformers = [joinStringTransformer]

        // Set up transformer
        let transformer = InjectTransformer()
        transformer.injectors = [valueInjector]
        
        // Apply
        let result = transformer.appliedTransformation(sourceData: sampleDict)
        
        // Verify values
        XCTAssertEqual(DataInjector.get(from: result.modifiedObject, path: InjectorPath(path: "fullName")) as? String, "John Doe")
    }
    
}
