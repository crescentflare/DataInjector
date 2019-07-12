//
//  LinkDataInjector.swift
//  DataInjector Pod
//
//  Library injector: link datasets
//  Inject one data set into another by a common field that links them together (such as ID)
//

import Foundation

/// An injector linking multiple datasets together by a common field (like ID)
open class LinkDataInjector: BaseInjector {
    
    // --
    // MARK: Members
    // --

    public var targetItemPath: InjectorPath?
    public var sourceDataPath: InjectorPath?
    public var overrideSourceData: Any?
    public var linkKey = "unknown"

    
    // --
    // MARK: Initialization
    // --
    
    public override init() {
    }

    
    // --
    // MARK: Manual injection
    // --
    
    public static func linkData(inDictionary: [String: Any?], fromArray: [Any?], usingKey: String) -> InjectorResult {
        if let foundItem = findDataItem(inArray: fromArray, forValue: inDictionary[usingKey] ?? nil, usingKey: usingKey) {
            var modifiedDictionary = [String: Any?]()
            for (key, value) in inDictionary {
                modifiedDictionary[key] = value
            }
            for (key, value) in foundItem {
                if key != usingKey {
                    modifiedDictionary[key] = value
                }
            }
            return InjectorResult(withModifiedObject: modifiedDictionary)
        }
        return InjectorResult(withModifiedObject: inDictionary)
    }
    
    public static func linkData(onArray: [Any?], fromArray: [Any?], usingKey: String) -> InjectorResult {
        var modifiedData = [[String: Any?]?]()
        for arrayItem in onArray {
            if let dictItem = arrayItem as? [String: Any?] {
                let result = LinkDataInjector.linkData(inDictionary: dictItem, fromArray: fromArray, usingKey: usingKey)
                if result.hasError() {
                    return result
                }
                modifiedData.append(result.modifiedObject as? [String: Any?])
            } else {
                return InjectorResult(withError: .targetInvalid)
            }
        }
        return InjectorResult(withModifiedObject: modifiedData)
    }
    

    // --
    // MARK: Data helpers
    // --
    
    public static func findDataItem(inArray: [Any?], forValue: Any?, usingKey: String) -> [String: Any?]? {
        if let searchValueString = InjectorConv.asString(value: forValue) {
            for arrayItem in inArray {
                if let dictItem = arrayItem as? [String: Any?] {
                    if let compareValueString = InjectorConv.asString(value: dictItem[usingKey] ?? nil) {
                        if compareValueString == searchValueString {
                            return dictItem
                        }
                    }
                }
            }
        }
        return nil
    }
    
    
    // --
    // MARK: General injection
    // --
    
    open override func appliedInjection(targetData: Any?, sourceData: Any? = nil) -> InjectorResult {
        var useSourceData = overrideSourceData ?? sourceData
        useSourceData = DataInjector.get(from: useSourceData, path: sourceDataPath ?? InjectorPath(path: ""))
        return DataInjector.inject(into: targetData, path: targetItemPath ?? InjectorPath(path: ""), modifyCallback: { originalData in
            if let sourceArray = useSourceData as? [Any?] {
                if let targetDict = originalData as? [String: Any?] {
                    return LinkDataInjector.linkData(inDictionary: targetDict, fromArray: sourceArray, usingKey: self.linkKey)
                } else if let targetArray = originalData as? [Any?] {
                    return LinkDataInjector.linkData(onArray: targetArray, fromArray: sourceArray, usingKey: self.linkKey)
                } else {
                    return InjectorResult(withError: .targetInvalid)
                }
            }
            return InjectorResult(withError: .sourceInvalid)
        })
    }

}
