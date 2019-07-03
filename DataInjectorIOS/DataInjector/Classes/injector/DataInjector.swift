//
//  DataInjector.swift
//  DataInjector Pod
//
//  Library injector: manually modify data
//  Search for an item in a nested data structure and return the modified result
//

import Foundation

/// An injector duplicating an item depending on the given data source(s)
public class DataInjector {
    
    // ---
    // MARK: Initialization
    // ---
    
    private init() {
        // Private initializer, this is a static class
    }

    
    // ---
    // MARK: Obtain data
    // ---

    public class func get(from: Any?, path: String) -> Any? {
        return get(from: from, path: InjectorPath(path: path))
    }
    
    public class func get(from: Any?, path: InjectorPath) -> Any? {
        if path.hasElements() {
            if let fromDict = from as? [String: Any?] {
                if let dictIndex = path.firstElement() {
                    return get(from: fromDict[dictIndex] ?? nil, path: path.deeperPath())
                }
            } else if let fromDict = from as? [String: Any] {
                if let dictIndex = path.firstElement() {
                    return get(from: fromDict[dictIndex], path: path.deeperPath())
                }
            } else if let fromArray = from as? [Any?] {
                let index = InjectorConv.toInt(from: path.firstElement()) ?? -1
                if index >= 0 && index < fromArray.count {
                    return get(from: fromArray[index], path: path.deeperPath())
                }
            } else if let fromArray = from as? [Any] {
                let index = InjectorConv.toInt(from: path.firstElement()) ?? -1
                if index >= 0 && index < fromArray.count {
                    return get(from: fromArray[index], path: path.deeperPath())
                }
            }
        } else {
            return from
        }
        return nil
    }
    
    
    // ---
    // MARK: Modify data
    // ---

    public class func inject(into: Any?, path: String, modifyCallback: (_ originalData: Any?) -> InjectorResult) -> InjectorResult {
        return inject(into: into, path: InjectorPath(path: path), modifyCallback: modifyCallback)
    }
    
    public class func inject(into: Any?, path: InjectorPath, modifyCallback: (_ originalData: Any?) -> InjectorResult) -> InjectorResult {
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
                    return InjectorResult(withModifiedObject: modifiedDict)
                }
                return InjectorResult(withError: .indexInvalid)
            } else if let intoDict = into as? [String: Any] {
                if let dictIndex = path.firstElement(), let originalData = intoDict[dictIndex] {
                    let result = inject(into: originalData, path: path.deeperPath(), modifyCallback: modifyCallback)
                    if result.hasError() {
                        return result
                    }
                    if let modifiedObject = result.modifiedObject {
                        var modifiedDict = intoDict
                        modifiedDict[dictIndex] = modifiedObject
                        return InjectorResult(withModifiedObject: modifiedDict)
                    }
                    var modifiedNullDict: [String: Any?] = intoDict
                    modifiedNullDict[dictIndex] = result.modifiedObject
                    return InjectorResult(withModifiedObject: modifiedNullDict)
                }
                return InjectorResult(withError: .indexInvalid)
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
                    return InjectorResult(withModifiedObject: modifiedArray)
                }
                return InjectorResult(withError: .indexInvalid)
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
                        return InjectorResult(withModifiedObject: modifiedArray)
                    }
                    var modifiedNullArray: [Any?] = intoArray
                    modifiedNullArray[index] = result.modifiedObject
                    return InjectorResult(withModifiedObject: modifiedNullArray)
                }
                return InjectorResult(withError: .indexInvalid)
            } else {
                return InjectorResult(withError: .noIndexedCollection)
            }
        } else {
            return modifyCallback(into)
        }
    }

}
