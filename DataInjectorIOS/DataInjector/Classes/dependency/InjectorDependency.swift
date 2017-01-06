//
//  InjectorDependency.swift
//  DataInjector Pod
//
//  Library dependency utility: defines a dependency
//  Keeps track of a dependency item
//

import Foundation

open class InjectorDependency {
    
    // ---
    // MARK: Members
    // ---
    
    var state = InjectorDependencyState.pending
    

    // ---
    // MARK: Initialization
    // ---
    
    public init() {
    }


    // ---
    // MARK: Resolving
    // ---
    
    open func resolve(completion: @escaping (_ success: Bool) -> Void) {
        completion(false)
    }
    
}
