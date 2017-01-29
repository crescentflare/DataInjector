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
    // MARK: Injection
    // ---

    override open func appliedInjection(targetData: Any, subTargetData: Any?, referencedData: Any? = nil, subReferencedData: Any? = nil) -> Any {
        if let dictItem = targetData as? [String: Any] {
            return processDict(dict: dictItem)
        } else if let arrayItem = targetData as? [Any] {
            return processArray(array: arrayItem)
        }
        return targetData
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
    
    private func processArray(array: [Any]) -> [Any] {
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
    
    private func processDict(dict: [String: Any]) -> [String: Any] {
        var modifiedDict: [String: Any] = [:]
        for (key, value) in dict {
            let keyItems = key.characters.split(separator: "_").map(String.init)
            var newKey = key
            if keyItems.count > 1 {
                newKey = keyItems[0]
                for i in 1..<keyItems.count {
                    newKey += keyItems[i].capitalized
                }
            }
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
