//
//  SnakeToCamelCaseInjector.swift
//  DataInjector Pod
//
//  Library injector: snake case conversion
//  Converts each entry key in the data set from snake case to camel case recursively
//

import Foundation

open class SnakeToCamelCaseInjector: DataInjector {
    
    // ---
    // MARK: Initialization
    // ---
    
    public override init() {
    }

    
    // ---
    // MARK: Data helpers
    // ---
    
    public static func camelCaseString(from snakeCaseString: String) -> String {
        let stringItems = snakeCaseString.characters.split(separator: "_").map(String.init)
        var resultString = snakeCaseString
        if stringItems.count > 1 {
            resultString = stringItems[0]
            for i in 1..<stringItems.count {
                resultString += stringItems[i].capitalized
            }
        }
        return resultString
    }
    

    // ---
    // MARK: Manual injection
    // ---
    
    public static func changedCase(onData targetData: Any) -> Any {
        if let dictItem = targetData as? [String: Any] {
            return processDict(dict: dictItem)
        } else if let arrayItem = targetData as? [Any] {
            return processArray(array: arrayItem)
        }
        return targetData
    }

    
    // ---
    // MARK: General injection
    // ---

    override open func appliedInjection(targetData: Any, subTargetData: Any?, referencedData: Any? = nil, subReferencedData: Any? = nil) -> Any {
        return SnakeToCamelCaseInjector.changedCase(onData: targetData)
    }
    

    // ---
    // MARK: Dependencies
    // ---

    override open func foundDependencies() -> [String] {
        return []
    }
    

    // ---
    // MARK: Helper
    // ---
    
    private static func processArray(array: [Any]) -> [Any] {
        var modifiedArray = array
        for i in 0..<modifiedArray.count {
            if let dictItem = modifiedArray[i] as? [String: Any] {
                modifiedArray[i] = processDict(dict: dictItem)
            } else if let arrayItem = modifiedArray[i] as? [Any] {
                modifiedArray[i] = processArray(array: arrayItem)
            }
        }
        return modifiedArray
    }
    
    private static func processDict(dict: [String: Any]) -> [String: Any] {
        var modifiedDict: [String: Any] = [:]
        for (key, value) in dict {
            var newKey = camelCaseString(from: key)
            if let dictItem = value as? [String: Any] {
                modifiedDict[newKey] = processDict(dict: dictItem)
            } else if let arrayItem = value as? [Any] {
                modifiedDict[newKey] = processArray(array: arrayItem)
            } else {
                modifiedDict[newKey] = value
            }
        }
        return modifiedDict
    }
    
}
