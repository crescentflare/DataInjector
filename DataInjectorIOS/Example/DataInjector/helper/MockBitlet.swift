//
//  MockBitlet.swift
//  DataInjector example
//
//  Example helper: define a bitlet which can load an example mock JSON file and executes a number of data injectors
//

import Foundation
import DataInjector
import BitletSynchronizer

class JsonArray {
    
    var itemList = [Any]()
    
}

class MockBitlet: BitletHandler {

    // --
    // MARK: Bitlet data type
    // --

    typealias BitletData = JsonArray
    

    // --
    // MARK: Members
    // --
    
    private var injectors: [BaseInjectorOld] = [
        SnakeToCamelCaseInjectorOld(),
        ReplaceNullInjectorOld()
    ]
    private let filename: String
    let cacheKey: String
    

    // --
    // MARK: Initialization
    // --
    
    init(filename: String, cacheKey: String) {
        self.filename = filename
        self.cacheKey = cacheKey
        if cacheKey == "customers" {
            injectors.append(JoinStringInjector(item: "fullName", fromItems: [ "~firstName", "~middleName", "~lastName" ], delimiter: " ", removeOriginals: true))
        }
    }
    

    // --
    // MARK: Resolving
    // --
    
    func load(observer: BitletObserver<JsonArray>) {
        DispatchQueue.global(qos: .default).async {
            // First load and process the data
            var processedJsonArray: [Any]?
            var hash = "unknown"
            if let jsonData = self.jsonData(forFilename: self.filename) {
                processedJsonArray = self.processedJsonArray(forData: jsonData)
                hash = jsonData.md5()
            }

            // Then apply the changes on the main thread, wait for half a second to simulate a delay in network traffic
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.5, execute: {
                if let processedJsonArray = processedJsonArray {
                    let jsonArray = JsonArray()
                    jsonArray.itemList = processedJsonArray
                    observer.bitlet = jsonArray
                } else {
                    observer.error = NSError(domain: "bitlet", code: -1, userInfo: nil)
                }
                observer.bitletHash = hash
                observer.finish()
            })
        }
    }

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

}
