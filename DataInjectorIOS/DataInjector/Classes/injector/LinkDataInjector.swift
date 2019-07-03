//
//  LinkDataInjector.swift
//  DataInjector Pod
//
//  Library injector: link datasets
//  Inject one data set into another by a common field that links them together (such as ID)
//

import Foundation

/// An injector linking multiple datasets together by a common field (like ID)
open class LinkDataInjector: BaseInjectorOld {
    
    // ---
    // MARK: Members
    // ---

    public var linkKey: String?

    
    // ---
    // MARK: Initialization
    // ---
    
    public override init() {
    }

    
    // ---
    // MARK: Data helpers
    // ---
    
    public static func findDataItem(onDataArray targetData: [[String: Any]], forValue: Any?, usingKey: String) -> [String: Any]? {
        if let searchValueString = InjectorConv.toString(from: forValue) {
            for dataItem in targetData {
                if let compareValueString = InjectorConv.toString(from: dataItem[usingKey]) {
                    if compareValueString == searchValueString {
                        return dataItem
                    }
                }
            }
        }
        return nil
    }
    

    // ---
    // MARK: Manual injection
    // ---

    public static func linkedData(onData targetData: [String: Any], with linkedData: [[String: Any]], linkBy key: String) -> [String: Any] {
        if let foundItem = findDataItem(onDataArray: linkedData, forValue: targetData[key], usingKey: key) {
            var modifiedData = targetData
            for (itemKey, itemValue) in foundItem {
                if itemKey != key {
                    modifiedData[itemKey] = itemValue
                }
            }
            return modifiedData
        }
        return targetData
    }

    public static func linkedDataArray(onData targetData: [[String: Any]], with linkedData: [[String: Any]], linkBy key: String) -> Any {
        var modifiedData: [[String: Any]] = []
        for targetDataItem in targetData {
            modifiedData.append(LinkDataInjector.linkedData(onData: targetDataItem, with: linkedData, linkBy: key))
        }
        return modifiedData
    }

    
    // ---
    // MARK: General injection
    // ---

    override open func appliedInjection(targetData: Any, subTargetData: Any?, referencedData: Any? = nil, subReferencedData: Any? = nil) -> Any {
        if let key = linkKey {
            if let linkedData = referencedData as? [[String: Any]] {
                if let targetDataArray = targetData as? [[String: Any]] {
                    return LinkDataInjector.linkedDataArray(onData: targetDataArray, with: linkedData, linkBy: key)
                } else if let targetDataItem = targetData as? [String: Any] {
                    return LinkDataInjector.linkedData(onData: targetDataItem, with: linkedData, linkBy: key)
                }
            }
        }
        return targetData
    }

}
