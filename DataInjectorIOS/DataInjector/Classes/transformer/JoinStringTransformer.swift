//
//  JoinStringTransformer.swift
//  DataInjector Pod
//
//  Library transformer: concatenate strings
//  Join multiple strings together with an optional delimiter, prefix or suffix
//

import Foundation

/// A transformer joining multiple strings together with an optional delimiter
open class JoinStringTransformer: BaseTransformer {
    
    // --
    // MARK: Members
    // --
    
    public var sourceDataPath: InjectorPath?
    public var fromItems = [String]()
    public var delimiter = ""
    public var prefix = ""
    public var suffix = ""

    
    // --
    // MARK: Initialization
    // --
    
    public override init() {
    }

    
    // --
    // MARK: Manual transformation
    // --
    
    public static func joinString(fromArray: [Any?], delimiter: String = "", prefix: String = "", suffix: String = "") -> InjectorResult {
        var stringItems = [String]()
        for value in fromArray {
            if let stringItem = value as? String {
                stringItems.append(stringItem)
            }
        }
        return InjectorResult(withModifiedObject: prefix + stringItems.joined(separator: delimiter) + suffix)
    }

    public static func joinString(fromDictionary: [String: Any?], fromItems: [String], delimiter: String = "", prefix: String = "", suffix: String = "") -> InjectorResult {
        var stringItems = [String]()
        for item in fromItems {
            if let stringItem = fromDictionary[item] as? String {
                stringItems.append(stringItem)
            }
        }
        return InjectorResult(withModifiedObject: prefix + stringItems.joined(separator: delimiter) + suffix)
    }
    
    
    // --
    // MARK: General injection
    // --
    
    open override func appliedTransformation(sourceData: Any?) -> InjectorResult {
        let useSourceData = DataInjector.get(from: sourceData, path: sourceDataPath ?? InjectorPath(path: ""))
        if let sourceDictionary = useSourceData as? [String: Any?] {
            return JoinStringTransformer.joinString(fromDictionary: sourceDictionary, fromItems: fromItems, delimiter: delimiter)
        } else if let sourceArray = useSourceData as? [Any?] {
            return JoinStringTransformer.joinString(fromArray: sourceArray, delimiter: delimiter)
        }
        return InjectorResult(withError: .sourceInvalid)
    }

}
