//
//  ReplaceNilInjector.swift
//  DataInjector Pod
//
//  Library injector: remove or replace nil values
//  Traverses a data set and remove nil values or replace them with the given defaults (with optional recursion)
//

import Foundation

/// An injector filtering out or replacing nil values
open class ReplaceNilInjector: BaseInjector {
    
    // --
    // MARK: Members
    // --
    
    public var sourceTransformers = [BaseTransformer]()
    public var targetItemPath: InjectorPath?
    public var sourceDataPath: InjectorPath?
    public var overrideSourceData: Any?
    public var recursive = false
    public var ignoreNotExisting = false


    // --
    // MARK: Initialization
    // --
    
    public override init() {
    }

    
    // --
    // MARK: Manual injection
    // --

    public static func filterNil(onData: Any?, recursive: Bool = false) -> InjectorResult {
        return replaceNil(onData: onData, replaceData: nil, recursive: recursive)
    }

    public static func replaceNil(onData: Any?, replaceData: Any?, recursive: Bool = false, ignoreNotExisting: Bool = false) -> InjectorResult {
        return processData(data: onData, replaceData: replaceData, recursive: recursive, ignoreNotExisting: ignoreNotExisting)
    }
    
    
    // --
    // MARK: General injection
    // --
    
    open override func appliedInjection(targetData: Any?, sourceData: Any? = nil) -> InjectorResult {
        // Prepare source data with optional transformation
        var useSourceData = overrideSourceData ?? sourceData
        useSourceData = DataInjector.get(from: useSourceData, path: sourceDataPath ?? InjectorPath(path: ""))
        for transformer in sourceTransformers {
            let result = transformer.appliedTransformation(sourceData: useSourceData)
            if result.hasError() {
                return result
            }
            useSourceData = result.modifiedObject
        }
        
        // Apply injection
        return DataInjector.inject(into: targetData, path: targetItemPath ?? InjectorPath(path: ""), modifyCallback: { originalData in
            return ReplaceNilInjector.replaceNil(onData: originalData, replaceData: useSourceData, recursive: recursive, ignoreNotExisting: ignoreNotExisting)
        })
    }


    // --
    // MARK: Internal data processing
    // --
    
    private static func processData(data: Any?, replaceData: Any?, recursive: Bool, ignoreNotExisting: Bool) -> InjectorResult {
        if data == nil {
            return replaceData != nil ? InjectorResult(withModifiedObject: replaceData) : InjectorResult(withError: .nilNotAllowed)
        } else if recursive {
            if let dictItem = data as? [String: Any?] {
                return processDict(dict: dictItem, replaceDict: replaceData as? [String: Any?], recursive: recursive, ignoreNotExisting: ignoreNotExisting)
            } else if let arrayItem = data as? [Any?] {
                return processArray(array: arrayItem, replaceArray: replaceData as? [Any?], recursive: recursive, ignoreNotExisting: ignoreNotExisting)
            }
        }
        return InjectorResult(withModifiedObject: data)
    }
    
    private static func processArray(array: [Any?], replaceArray: [Any?]?, recursive: Bool, ignoreNotExisting: Bool) -> InjectorResult {
        var modifiedArray = [Any]()
        for arrayItem in array {
            let addArrayItem = arrayItem == nil || arrayItem is NSNull ? replaceArray?.first : arrayItem
            if addArrayItem != nil && !(addArrayItem is NSNull) {
                if recursive {
                    let result = processData(data: addArrayItem ?? nil, replaceData: replaceArray?.first ?? nil, recursive: recursive, ignoreNotExisting: ignoreNotExisting)
                    if result.hasError() {
                        return result
                    }
                    if let validModifiedObject = result.modifiedObject {
                        modifiedArray.append(validModifiedObject)
                    } else {
                        return InjectorResult(withError: .nilNotAllowed)
                    }
                } else {
                    modifiedArray.append(addArrayItem as Any)
                }
            }
        }
        if modifiedArray.isEmpty && !ignoreNotExisting, let addItem = replaceArray?.first {
            if let addValidItem = addItem {
                modifiedArray.append(addValidItem)
            } else {
                return InjectorResult(withError: .sourceInvalid)
            }
        }
        return InjectorResult(withModifiedObject: modifiedArray)
    }
    
    private static func processDict(dict: [String: Any?], replaceDict: [String: Any?]?, recursive: Bool, ignoreNotExisting: Bool) -> InjectorResult {
        var modifiedDict = [String: Any]()
        for (key, value) in dict {
            let setValue = value == nil || value is NSNull ? replaceDict?[key] : value
            if setValue != nil && !(setValue is NSNull) {
                if recursive {
                    let result = processData(data: setValue ?? nil, replaceData: replaceDict?[key] ?? nil, recursive: recursive, ignoreNotExisting: ignoreNotExisting)
                    if result.hasError() {
                        return result
                    }
                    if let validModifiedObject = result.modifiedObject {
                        modifiedDict[key] = validModifiedObject
                    } else {
                        return InjectorResult(withError: .nilNotAllowed)
                    }
                } else {
                    modifiedDict[key] = setValue as Any
                }
            }
        }
        if !ignoreNotExisting, let replaceDict = replaceDict {
            for key in replaceDict.keys {
                if !modifiedDict.keys.contains(key) {
                    if let setValue = replaceDict[key] {
                        modifiedDict[key] = setValue
                    } else {
                        return InjectorResult(withError: .nilNotAllowed)
                    }
                }
            }
        }
        return InjectorResult(withModifiedObject: modifiedDict)
    }
    
}
