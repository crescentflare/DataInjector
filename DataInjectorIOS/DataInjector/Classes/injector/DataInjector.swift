//
//  DataInjector.swift
//  DataInjector Pod
//
//  Library injector: the base class
//  Classes derived from this base class can be used as a data injector
//

import Foundation

open class DataInjector {
    
    // ---
    // MARK: Initialization
    // ---

    public init() {
    }


    // ---
    // MARK: Functions to implement
    // ---
    
    open func appliedInjection(targetData: Any, referencedData: Any? = nil, subReferencedData: Any? = nil) -> Any {
        return targetData
    }
    
    open func foundDependencies() -> [String] {
        return []
    }
    

    // ---
    // MARK: Helper
    // ---

    func obtainValue(item: String, targetData: Any?, subTargetData: Any?, referencedData: Any?, subReferencedData: Any?) -> Any? {
        if item.hasPrefix("#.") {
            return InjectorUtil.itemFromObject(subReferencedData, path: item.substring(from: item.characters.index(item.startIndex, offsetBy: 2)))
        } else if item.hasPrefix("#") {
            return InjectorUtil.itemFromObject(referencedData, path: item.substring(from: item.characters.index(after: item.startIndex)))
        } else if item.hasPrefix("~.") {
            return InjectorUtil.itemFromObject(subTargetData, path: item.substring(from: item.characters.index(item.startIndex, offsetBy: 2)))
        } else if item.hasPrefix("~") {
            return InjectorUtil.itemFromObject(targetData, path: item.substring(from: item.characters.index(after: item.startIndex)))
        }
        return item
    }
}
