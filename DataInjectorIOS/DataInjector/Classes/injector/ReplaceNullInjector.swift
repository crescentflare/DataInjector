//
//  ReplaceNullInjector.swift
//  DataInjector Pod
//
//  Library injector: remove or replace null values
//  Traverses a data set recursively removing null values or replacing them with the given defaults
//

import Foundation

/// An injector filtering out or replacing null values
open class ReplaceNullInjector: BaseInjector {
    
    // ---
    // MARK: Initialization
    // ---
    
    public override init() {
    }

    
    // ---
    // MARK: Manual injection
    // ---

    public static func filteredNull(onData targetData: Any) -> Any {
        return replacedNull(onData: targetData, with: nil)
    }

    public static func replacedNull(onData targetData: Any, with replaceData: Any?, ignoreNotExisting: Bool = false) -> Any {
        if let dictItem = targetData as? [String: Any] {
            return processDict(dict: dictItem, replaceDict: replaceData as? [String: Any], ignoreNotExisting: ignoreNotExisting)
        } else if let arrayItem = targetData as? [Any] {
            return processArray(array: arrayItem, replaceArray: replaceData as? [Any], ignoreNotExisting: ignoreNotExisting)
        }
        return targetData
    }
    
    
    // ---
    // MARK: General injection
    // ---

    override open func appliedInjection(targetData: Any, subTargetData: Any?, referencedData: Any? = nil, subReferencedData: Any? = nil) -> Any {
        return ReplaceNullInjector.replacedNull(onData: targetData, with: referencedData)
    }
    

    // ---
    // MARK: Internal data processing
    // ---
    
    private static func processArray(array: [Any], replaceArray: [Any]?, ignoreNotExisting: Bool) -> [Any] {
        var modifiedArray: [Any] = []
        for i in 0..<array.count {
            if let dictItem = array[i] as? [String: Any] {
                var supplyReplaceDict: [String: Any]?
                if replaceArray != nil && i < replaceArray!.count {
                    supplyReplaceDict = replaceArray?[i] as? [String: Any]
                }
                modifiedArray.append(processDict(dict: dictItem, replaceDict: supplyReplaceDict, ignoreNotExisting: ignoreNotExisting))
            } else if let arrayItem = array[i] as? [Any] {
                var supplyReplaceArray: [Any]?
                if replaceArray != nil && i < replaceArray!.count {
                    supplyReplaceArray = replaceArray?[i] as? [Any]
                }
                modifiedArray.append(processArray(array: arrayItem, replaceArray: supplyReplaceArray, ignoreNotExisting: ignoreNotExisting))
            } else if !(array[i] is NSNull) {
                modifiedArray.append(array[i])
            } else if replaceArray != nil && i < replaceArray!.count && !(replaceArray![i] is NSNull) {
                modifiedArray.append(replaceArray![i])
            }
        }
        if !ignoreNotExisting && replaceArray != nil && replaceArray!.count > array.count {
            for i in array.count..<replaceArray!.count {
                if !(replaceArray![i] is NSNull) {
                    modifiedArray.append(replaceArray![i])
                }
            }
        }
        return modifiedArray
    }
    
    private static func processDict(dict: [String: Any], replaceDict: [String: Any]?, ignoreNotExisting: Bool) -> [String: Any] {
        var modifiedDict: [String: Any] = [:]
        for (key, value) in dict {
            if let dictItem = value as? [String: Any] {
                modifiedDict[key] = processDict(dict: dictItem, replaceDict: replaceDict?[key] as? [String: Any], ignoreNotExisting: ignoreNotExisting)
            } else if let arrayItem = value as? [Any] {
                modifiedDict[key] = processArray(array: arrayItem, replaceArray: replaceDict?[key] as? [Any], ignoreNotExisting: ignoreNotExisting)
            } else if !(value is NSNull) {
                modifiedDict[key] = value
            } else if let replacingItem = replaceDict?[key] {
                if !(replacingItem is NSNull) {
                    modifiedDict[key] = replacingItem
                }
            }
        }
        if !ignoreNotExisting && replaceDict != nil {
            for (key, value) in replaceDict! {
                if modifiedDict[key] == nil && !(value is NSNull){
                    modifiedDict[key] = value
                }
            }
        }
        return modifiedDict
    }
    
}
