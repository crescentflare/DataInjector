//
//  MockDependency.swift
//  DataInjector example
//
//  Example helper: define a dependency for the dependency manager which can resolve itself through the example mock JSON files
//

import Foundation
import DataInjector

class MockDependency: InjectorDependency {

    // --
    // MARK: Members
    // --
    
    private var injectors: [DataInjector] = [
        SnakeToCamelCaseInjector(),
        FilterNullInjector()
    ]
    private let filename: String
    private var storedJson: [Any]?
    

    // --
    // MARK: Initialization
    // --
    
    init(filename: String) {
        self.filename = filename
        if filename.hasPrefix("customer") {
            injectors.append(JoinStringInjector(item: "fullName", fromItems: [ "~firstName", "~middleName", "~lastName" ], delimiter: " ", removeOriginals: true))
        }
    }
    

    // ---
    // MARK: Data access
    // ---
    
    override open func obtainInjectableData() -> Any? {
        return storedJson
    }
    
    
    // --
    // MARK: Resolving
    // --
    
    override open func resolve(input: [String: String], completion: @escaping (_ success: Bool) -> Void) {
        let bundle = Bundle.main
        if let path = bundle.path(forResource: "data/" + filename, ofType: "json") {
            if let jsonData = try? NSData(contentsOfFile: path, options: .mappedIfSafe) as Data {
                if let json = try? JSONSerialization.jsonObject(with: jsonData, options: .allowFragments) {
                    if let jsonArray = json as? [Any] {
                        var processedJsonArray: [Any] = []
                        for var jsonArrayItem in jsonArray {
                            for injector in injectors {
                                jsonArrayItem = injector.appliedInjection(targetData: jsonArrayItem)
                            }
                            processedJsonArray.append(jsonArrayItem)
                        }
                        storedJson = processedJsonArray
                        completion(true)
                    }
                }
            }
        }
        completion(false)
    }

}
