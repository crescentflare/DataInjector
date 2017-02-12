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
        ReplaceNullInjector()
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
    
    private func jsonData(forFilename: String) -> Data? {
        let bundle = Bundle.main
        if let path = bundle.path(forResource: "data/" + forFilename, ofType: "json") {
            return try? NSData(contentsOfFile: path, options: .mappedIfSafe) as Data
        }
        return nil
    }
    
    private func processedJsonArray(forData: Data) -> [Any]? {
        if let json = try? JSONSerialization.jsonObject(with: forData, options: .allowFragments) {
            if let jsonArray = json as? [Any] {
                var processedJsonArray: [Any] = []
                for var jsonArrayItem in jsonArray {
                    for injector in self.injectors {
                        jsonArrayItem = injector.appliedInjection(targetData: jsonArrayItem)
                    }
                    processedJsonArray.append(jsonArrayItem)
                }
                return processedJsonArray
            }
        }
        return nil
    }
    
    override open func resolve(input: [String: String], completion: @escaping (_ success: Bool) -> Void) {
        if let jsonData = jsonData(forFilename: filename) {
            DispatchQueue.global(qos: .default).async {
                let processedJsonArray = self.processedJsonArray(forData: jsonData)
                DispatchQueue.main.async {
                    if processedJsonArray != nil {
                        self.storedJson = processedJsonArray
                    }
                    completion(processedJsonArray != nil)
                }
            }
            return
        }
        completion(false)
    }

}
