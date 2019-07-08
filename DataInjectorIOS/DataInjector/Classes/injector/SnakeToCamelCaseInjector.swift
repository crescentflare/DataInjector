//
//  SnakeToCamelCaseInjector.swift
//  DataInjector Pod
//
//  Library injector: snake case conversion
//  Converts each entry key in the data set from snake case to camel case (with optional recursion)
//

import Foundation

/// An injector converting snake case into camel case for object keys
open class SnakeToCamelCaseInjector: BaseInjector {
    
    // --
    // MARK: Members
    // --
    
    public var targetItemPath: InjectorPath?
    public var recursive = false
    
    
    // --
    // MARK: Initialization
    // --
    
    public override init() {
    }

    
    // --
    // MARK: Manual injection
    // --
    
    public static func changeCase(onData: Any?, recursive: Bool = false) -> InjectorResult {
        if let dictItem = onData as? [String: Any?] {
            return processDict(dict: dictItem, recursive: recursive)
        } else if let arrayItem = onData as? [Any?] {
            if !recursive {
                return InjectorResult(withError: .targetInvalid)
            }
            return processArray(array: arrayItem)
        }
        return InjectorResult(withError: .targetInvalid)
    }
    
    
    // --
    // MARK: Data helpers
    // --
    
    public static func camelCaseString(from snakeCaseString: String) -> String {
        let stringItems = snakeCaseString.split(separator: "_").map(String.init)
        var resultString = snakeCaseString
        if stringItems.count > 1 {
            resultString = stringItems[0]
            for i in 1..<stringItems.count {
                resultString += stringItems[i].capitalized
            }
        }
        return resultString
    }
    

    // --
    // MARK: General injection
    // --
    
    open override func appliedInjection(targetData: Any?, sourceData: Any? = nil) -> InjectorResult {
        return DataInjector.inject(into: targetData, path: targetItemPath ?? InjectorPath(path: ""), modifyCallback: { originalData in
            return SnakeToCamelCaseInjector.changeCase(onData: originalData, recursive: recursive)
        })
    }


    // --
    // MARK: Helper
    // --
    
    private static func processArray(array: [Any?]) -> InjectorResult {
        var modifiedArray = [Any?]()
        for i in 0..<array.count {
            if let dictItem = array[i] as? [String: Any?] {
                let result = processDict(dict: dictItem, recursive: true)
                if result.hasError() {
                    return result
                }
                modifiedArray.append(result.modifiedObject)
            } else if let arrayItem = array[i] as? [Any?] {
                let result = processArray(array: arrayItem)
                if result.hasError() {
                    return result
                }
                modifiedArray.append(result.modifiedObject)
            } else {
                modifiedArray.append(array[i])
            }
        }
        return InjectorResult(withModifiedObject: modifiedArray)
    }
    
    private static func processDict(dict: [String: Any?], recursive: Bool) -> InjectorResult {
        var modifiedDict: [String: Any?] = [:]
        for (key, value) in dict {
            let newKey = camelCaseString(from: key)
            if recursive {
                if let dictItem = value as? [String: Any?] {
                    let result = processDict(dict: dictItem, recursive: recursive)
                    if result.hasError() {
                        return result
                    }
                    modifiedDict[newKey] = result.modifiedObject
                } else if let arrayItem = value as? [Any?] {
                    let result = processArray(array: arrayItem)
                    if result.hasError() {
                        return result
                    }
                    modifiedDict[newKey] = result.modifiedObject
                } else {
                    modifiedDict[newKey] = value
                }
            } else {
                modifiedDict[newKey] = value
            }
        }
        return InjectorResult(withModifiedObject: modifiedDict)
    }
    
}
