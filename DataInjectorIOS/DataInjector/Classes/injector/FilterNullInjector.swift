//
//  FilterNullInjector.swift
//  DataInjector Pod
//
//  Library injector: remove null values
//  Traverses a data set recursively removing null values
//

import Foundation

open class FilterNullInjector: DataInjector {
    
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
        var modifiedArray: [Any] = []
        for i in 0..<array.count {
            if let dictItem = array[i] as? [String: Any] {
                modifiedArray.append(processDict(dict: dictItem))
            } else if let arrayItem = array[i] as? [Any] {
                modifiedArray.append(processArray(array: arrayItem))
            } else if !(array[i] is NSNull) {
                modifiedArray.append(array[i])
            }
        }
        return modifiedArray
    }
    
    private func processDict(dict: [String: Any]) -> [String: Any] {
        var modifiedDict: [String: Any] = [:]
        for (key, value) in dict {
            if let dictItem = value as? [String: Any] {
                modifiedDict[key] = processDict(dict: dictItem)
            } else if let arrayItem = value as? [Any] {
                modifiedDict[key] = processArray(array: arrayItem)
            } else if !(value is NSNull) {
                modifiedDict[key] = value
            }
        }
        return modifiedDict
    }
    
}
