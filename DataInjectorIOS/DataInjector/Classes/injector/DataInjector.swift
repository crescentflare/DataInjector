//
//  DataInjector.swift
//  DataInjector Pod
//
//  Library injector: the base class
//  Classes derived from this base class can be used as a data injector
//

import Foundation

open class DataInjector {
    
    public init() {
    }

    open func appliedInjection(targetData: Any, referencedData: Any? = nil, subReferencedData: Any? = nil) -> Any {
        return targetData
    }
    
    open func foundDependencies() -> [String] {
        return []
    }
    
}
