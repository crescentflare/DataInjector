//
//  JoinStringInjector.swift
//  DataInjector Pod
//
//  Library injector: concatenate strings
//  Join multiple strings together with an optional delimiter
//

import Foundation

open class JoinStringInjector: DataInjector {
    
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
    // MARK: Injection
    // ---

    override open func appliedInjection(targetData: Any, referencedData: Any? = nil, subReferencedData: Any? = nil) -> Any {
        if item == nil {
            return targetData
        }
        var finalString = ""
        for fromItem in fromItems {
            if let concatString = InjectorConv.toString(from: obtainValue(item: fromItem, targetData: targetData, subTargetData: nil, referencedData: referencedData, subReferencedData: subReferencedData)) {
                if finalString.characters.count > 0 {
                    finalString += delimiter
                }
                finalString += concatString
            }
        }
        if var modifyDict = targetData as? [String: Any] {
            if removeOriginals {
                for fromItem in fromItems {
                    if !fromItem.hasPrefix("~.") && fromItem.hasPrefix("~") {
                        InjectorUtil.setItemOnDictionary(&modifyDict, path: fromItem.substring(from: fromItem.characters.index(after: fromItem.startIndex)), value: nil)
                    }
                }
            }
            InjectorUtil.setItemOnDictionary(&modifyDict, path: item!, value: finalString)
            return modifyDict
        } else if var modifyArray = targetData as? [Any] {
            if removeOriginals {
                for fromItem in fromItems {
                    if !fromItem.hasPrefix("~.") && fromItem.hasPrefix("~") {
                        InjectorUtil.setItemOnArray(&modifyArray, path: fromItem.substring(from: fromItem.characters.index(after: fromItem.startIndex)), value: nil)
                    }
                }
            }
            InjectorUtil.setItemOnArray(&modifyArray, path: item!, value: finalString)
            return modifyArray
        }
        return targetData
    }
    

    // ---
    // MARK: Dependencies
    // ---

    override open func foundDependencies() -> [String] {
        if let dependency = InjectorDependencyManager.shared.dependencyNameFrom(injectSource: item ?? "") {
            return [dependency]
        }
        return []
    }
    
}
