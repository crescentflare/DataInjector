//
//  InjectorDependency.swift
//  DataInjector Pod
//
//  Library dependency utility: defines a dependency
//  Keeps track of a dependency item
//

import Foundation

/// A base class to define a dependency which can be resolved by various means,
/// like reading a file or executing an API call
open class InjectorDependency {
    
    // ---
    // MARK: Members
    // ---
    
    private let _name: String
    var lastUpdated: Int = 0
    var state = InjectorDependencyState.idle
    public var expiration: Int?
    public var requiresInput: [String] = []
    public var dependencies: [InjectorDependency] = []
    
    public var name: String {
        get { return _name }
    }

    
    // ---
    // MARK: Initialization
    // ---
    
    public init(name: String) {
        self._name = name
    }


    // ---
    // MARK: Data access
    // ---
    
    open func obtainInjectableData() -> Any? {
        return nil
    }
    

    // ---
    // MARK: State checking
    // ---
    
    public func resetExpiration() {
        lastUpdated = Int(Date().timeIntervalSince1970)
    }
    
    public func isExpired() -> Bool {
        if let expiresInSeconds = expiration {
            let currentTime = Int(Date().timeIntervalSince1970)
            return currentTime - lastUpdated >= expiresInSeconds
        }
        return false
    }
    
    public func isError() -> Bool {
        return state.isKindOfState(.error)
    }

    
    // ---
    // MARK: Resolving
    // ---
    
    open func resolve(input: [String: String], completion: @escaping (_ success: Bool) -> Void) {
        completion(false)
    }
    
}
