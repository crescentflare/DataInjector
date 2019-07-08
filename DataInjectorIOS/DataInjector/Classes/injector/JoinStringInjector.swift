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
    public var fromItems: [String]?
    public var delimiter = ""

    
    // --
    // MARK: Initialization
    // --
    
    public override init() {
    }

    
    // --
    // MARK: Manual injection
    // --
    
    public static func joinString(sourceData: Any?, delimiter: String = "") -> InjectorResult {
        if let dictItem = sourceData as? [String: Any?] {
            return joinString(sourceData: sourceData, fromItems: Array(dictItem.keys), delimiter: delimiter)
        }
        return joinString(sourceData: sourceData, fromItems: [], delimiter: delimiter)
    }
    
    public static func joinString(sourceData: Any?, fromItems: [String], delimiter: String = "") -> InjectorResult {
        if let dictItem = sourceData as? [String: Any?] {
            var stringItems = [String]()
            for item in fromItems {
                if let stringItem = dictItem[item] as? String {
                    stringItems.append(stringItem)
                }
            }
            return InjectorResult(withModifiedObject: stringItems.joined(separator: delimiter))
        } else if let arrayItem = sourceData as? [Any?] {
            var stringItems = [String]()
            for value in arrayItem {
                if let stringItem = value as? String {
                    stringItems.append(stringItem)
                }
            }
            return InjectorResult(withModifiedObject: stringItems.joined(separator: delimiter))
        }
        return InjectorResult(withError: .sourceInvalid)
    }
    
    
    // --
    // MARK: General injection
    // --
    
    open override func appliedInjection(targetData: Any?, sourceData: Any? = nil) -> InjectorResult {
        var useSourceData = overrideSourceData ?? sourceData
        useSourceData = DataInjector.get(from: useSourceData, path: sourceDataPath ?? InjectorPath(path: ""))
        return DataInjector.inject(into: targetData, path: targetItemPath ?? InjectorPath(path: ""), modifyCallback: { originalData in
            if let fromItems = fromItems {
                return JoinStringInjector.joinString(sourceData: useSourceData, fromItems: fromItems, delimiter: delimiter)
            }
            return JoinStringInjector.joinString(sourceData: useSourceData, delimiter: delimiter)
        })
    }

}
