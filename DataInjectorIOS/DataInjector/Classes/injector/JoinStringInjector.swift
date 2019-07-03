//
//  JoinStringInjector.swift
//  DataInjector Pod
//
//  Library injector: concatenate strings
//  Join multiple strings together with an optional delimiter
//

import Foundation

/// An injector joining multiple strings together with an optional delimiter
open class JoinStringInjector: BaseInjectorOld {
    
    // ---
    // MARK: Members
    // ---
    
    public var fromItems: [String] = []
    public var item: String?
    public var delimiter = ""
    public var removeOriginals = false

    
    // ---
    // MARK: Initialization
    // ---
    
    public override init() {
    }
    
    convenience public init(item: String, fromItems: [String], delimiter: String = "", removeOriginals: Bool = false) {
        self.init()
        self.item = item
        self.fromItems = fromItems
        self.delimiter = delimiter
        self.removeOriginals = removeOriginals
    }

    
    // ---
    // MARK: General injection
    // ---

    override open func appliedInjection(targetData: Any, subTargetData: Any?, referencedData: Any? = nil, subReferencedData: Any? = nil) -> Any {
        if item == nil {
            return targetData
        }
        var finalString = ""
        var firstItem = false
        for fromItem in fromItems {
            if let concatString = InjectorConv.toString(from: BaseInjectorOld.obtainValue(item: fromItem, targetData: targetData, subTargetData: nil, referencedData: referencedData, subReferencedData: subReferencedData)) {
                if !firstItem {
                    finalString += delimiter
                }
                finalString += concatString
                firstItem = false
            }
        }
        if var modifyDict = targetData as? [String: Any] {
            if removeOriginals {
                for fromItem in fromItems {
                    if !fromItem.hasPrefix("~.") && fromItem.hasPrefix("~") {
                        InjectorUtil.setItemOnDictionary(&modifyDict, path: fromItem.substring(from: fromItem.index(after: fromItem.startIndex)), value: nil)
                    }
                }
            }
            InjectorUtil.setItemOnDictionary(&modifyDict, path: item!, value: finalString)
            return modifyDict
        } else if var modifyArray = targetData as? [Any] {
            if removeOriginals {
                for fromItem in fromItems {
                    if !fromItem.hasPrefix("~.") && fromItem.hasPrefix("~") {
                        InjectorUtil.setItemOnArray(&modifyArray, path: fromItem.substring(from: fromItem.index(after: fromItem.startIndex)), value: nil)
                    }
                }
            }
            InjectorUtil.setItemOnArray(&modifyArray, path: item!, value: finalString)
            return modifyArray
        }
        return targetData
    }

}
