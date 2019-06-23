//
//  DataInjector.swift
//  DataInjector Pod
//
//  Library injector: manually modify data
//  Search for an item in a nested data structure and return the modified result
//

import Foundation

/// An enum to determine the error that occurred during modification
public enum DataInjectorError {
    
    case unknown
    case noIndexedCollection
    case indexInvalid
    case custom
    
}

/// A class to hold the result of a modification
public class DataInjectorResult {
    
    public let modifiedObject: Any?
    public let error: DataInjectorError?
    public let customInfo: Any?

    public init(withModifiedObject: Any?) {
        modifiedObject = withModifiedObject
        error = nil
        customInfo = nil
    }

    public init(withError: DataInjectorError) {
        modifiedObject = nil
        error = withError
        customInfo = nil
    }

    public init(withCustomErrorInfo: Any) {
        modifiedObject = nil
        error = .custom
        customInfo = withCustomErrorInfo
    }
    
    public func hasError() -> Bool {
        return error != nil
    }

}

/// An injector duplicating an item depending on the given data source(s)
public class DataInjector {
    
    // ---
    // MARK: Initialization
    // ---
    
    private init() {
        // Private initializer, this is a static class
    }

    
    // ---
    // MARK: Modify data
    // ---

    public class func inject(into: Any?, path: String, modifyCallback: (_ originalData: Any?) -> DataInjectorResult) -> DataInjectorResult {
        return inject(into: into, path: InjectorPath(path: path), modifyCallback: modifyCallback)
    }
    
    public class func inject(into: Any?, path: InjectorPath, modifyCallback: (_ originalData: Any?) -> DataInjectorResult) -> DataInjectorResult {
        if path.hasElements() {
            if let intoDict = into as? [String: Any?] {
                if let dictIndex = path.firstElement() {
                    let originalData: Any? = intoDict[dictIndex] ?? nil
                    let result = inject(into: originalData, path: path.deeperPath(), modifyCallback: modifyCallback)
                    if result.hasError() {
                        return result
                    }
                    var modifiedDict = intoDict
                    modifiedDict[dictIndex] = result.modifiedObject
                    return DataInjectorResult(withModifiedObject: modifiedDict)
                }
                return DataInjectorResult(withError: .indexInvalid)
            } else if let intoDict = into as? [String: Any] {
                if let dictIndex = path.firstElement(), let originalData = intoDict[dictIndex] {
                    let result = inject(into: originalData, path: path.deeperPath(), modifyCallback: modifyCallback)
                    if result.hasError() {
                        return result
                    }
                    if let modifiedObject = result.modifiedObject {
                        var modifiedDict = intoDict
                        modifiedDict[dictIndex] = modifiedObject
                        return DataInjectorResult(withModifiedObject: modifiedDict)
                    }
                    var modifiedNullDict: [String: Any?] = intoDict
                    modifiedNullDict[dictIndex] = result.modifiedObject
                    return DataInjectorResult(withModifiedObject: modifiedNullDict)
                }
                return DataInjectorResult(withError: .indexInvalid)
            } else if let intoArray = into as? [Any?] {
                let index = InjectorConv.toInt(from: path.firstElement()) ?? -1
                if index >= 0 && index < intoArray.count {
                    let originalData: Any? = intoArray[index] ?? nil
                    let result = inject(into: originalData, path: path.deeperPath(), modifyCallback: modifyCallback)
                    if result.hasError() {
                        return result
                    }
                    var modifiedArray = intoArray
                    modifiedArray[index] = result.modifiedObject
                    return DataInjectorResult(withModifiedObject: modifiedArray)
                }
                return DataInjectorResult(withError: .indexInvalid)
            } else if let intoArray = into as? [Any] {
                let index = InjectorConv.toInt(from: path.firstElement()) ?? -1
                if index >= 0 && index < intoArray.count {
                    let originalData = intoArray[index]
                    let result = inject(into: originalData, path: path.deeperPath(), modifyCallback: modifyCallback)
                    if result.hasError() {
                        return result
                    }
                    if let modifiedObject = result.modifiedObject {
                        var modifiedArray = intoArray
                        modifiedArray[index] = modifiedObject
                        return DataInjectorResult(withModifiedObject: modifiedArray)
                    }
                    var modifiedNullArray: [Any?] = intoArray
                    modifiedNullArray[index] = result.modifiedObject
                    return DataInjectorResult(withModifiedObject: modifiedNullArray)
                }
                return DataInjectorResult(withError: .indexInvalid)
            } else {
                return DataInjectorResult(withError: .noIndexedCollection)
            }
        } else {
            return modifyCallback(into)
        }
    }

}
