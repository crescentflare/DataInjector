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
    
    private let filename: String
    private var storedJson: [Any]?
    

    // --
    // MARK: Initialization
    // --
    
    init(filename: String) {
        self.filename = filename
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
                    let correctedJson = SnakeToCamelCaseInjector().appliedInjection(targetData: json)
                    if let jsonArray = correctedJson as? [Any] {
                        storedJson = jsonArray
                        completion(true)
                    }
                }
            }
        }
        completion(false)
    }

}
