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
    
    var lastUpdated: Int = 0
    var state = InjectorDependencyState.pending
    public var expiration: Int?
    public var requiresInput: [String] = []
    public var dependencies: [String] = []

    
    // ---
    // MARK: Initialization
    // ---
    
    public init() {
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
        return state == .obtainError || state == .refreshError
    }

    
    // ---
    // MARK: Resolving
    // ---
    
    open func resolve(input: [String: String], completion: @escaping (_ success: Bool) -> Void) {
        completion(false)
    }
    
}
