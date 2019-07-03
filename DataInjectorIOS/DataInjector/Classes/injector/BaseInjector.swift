//
//  BaseInjector.swift
//  DataInjector Pod
//
//  Library injector: the base class
//  Classes derived from this base class can be used as a data injector
//

import Foundation

/// A base injector class which can be used as a generic interface to execute an injection operation
open class BaseInjector {
    
    // ---
    // MARK: Initialization
    // ---
    
    public init() {
    }
    
    
    // ---
    // MARK: Function to implement
    // ---
    
    open func appliedInjection(targetData: Any?, sourceData: Any? = nil) -> InjectorResult {
        return InjectorResult(withModifiedObject: targetData)
    }

}
