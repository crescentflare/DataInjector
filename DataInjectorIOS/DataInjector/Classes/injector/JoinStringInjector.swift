//
//  JoinStringInjector.swift
//  DataInjector Pod
//
//  Library injector: concatenate strings
//  Join multiple strings together with an optional delimiter
//

import Foundation

/// An injector joining multiple strings together with an optional delimiter
open class JoinStringInjector: BaseInjector {
    
    // --
    // MARK: Members
    // --
    
    public var targetItemPath: InjectorPath?
    public var sourceDataPath: InjectorPath?
    public var overrideSourceData: Any?
    public var fromItems = [String]()
    public var delimiter = ""

    
    // --
    // MARK: Initialization
    // --
    
    public override init() {
    }

    
    // --
    // MARK: Manual injection
    // --
    
    public static func joinString(fromArray: [Any?], delimiter: String = "") -> InjectorResult {
        var stringItems = [String]()
        for value in fromArray {
            if let stringItem = value as? String {
                stringItems.append(stringItem)
            }
        }
        return InjectorResult(withModifiedObject: stringItems.joined(separator: delimiter))
    }
    
    public static func joinString(fromDictionary: [String: Any?], fromItems: [String], delimiter: String = "") -> InjectorResult {
        var stringItems = [String]()
        for item in fromItems {
            if let stringItem = fromDictionary[item] as? String {
                stringItems.append(stringItem)
            }
        }
        return InjectorResult(withModifiedObject: stringItems.joined(separator: delimiter))
    }
    
    
    // --
    // MARK: General injection
    // --
    
    open override func appliedInjection(targetData: Any?, sourceData: Any? = nil) -> InjectorResult {
        var useSourceData = overrideSourceData ?? sourceData
        useSourceData = DataInjector.get(from: useSourceData, path: sourceDataPath ?? InjectorPath(path: ""))
        return DataInjector.inject(into: targetData, path: targetItemPath ?? InjectorPath(path: ""), modifyCallback: { originalData in
            if let sourceDictionary = useSourceData as? [String: Any?] {
                return JoinStringInjector.joinString(fromDictionary: sourceDictionary, fromItems: fromItems, delimiter: delimiter)
            } else if let sourceArray = useSourceData as? [Any?] {
                return JoinStringInjector.joinString(fromArray: sourceArray, delimiter: delimiter)
            }
            return InjectorResult(withError: .sourceInvalid)
        })
    }

}
