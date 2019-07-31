//
//  BaseTransformer.swift
//  DataInjector Pod
//
//  Library transformer: the base class
//  Classes derived from this base class can be used as a data transformer
//

import Foundation

/// A base transformer class which can be used as a generic interface to execute a transform operation
open class BaseTransformer {
    
    // ---
    // MARK: Initialization
    // ---
    
    public init() {
    }
    
    
    // ---
    // MARK: Function to implement
    // ---
    
    open func appliedTransformation(sourceData: Any?) -> InjectorResult {
        return InjectorResult(withModifiedObject: sourceData)
    }

}
