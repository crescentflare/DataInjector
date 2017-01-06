//
//  InjectorDependencyManager.swift
//  DataInjector Pod
//
//  Library dependency utility: manage dependencies
//  Provide a central place to keep track of all dependencies within the application
//

import Foundation

public class InjectorDependencyManager {
    
    // ---
    // MARK: Singleton instance
    // ---
    
    public static let shared = InjectorDependencyManager()


    // ---
    // MARK: Members
    // ---
    
    private var dependencies: [String: InjectorDependency] = [:]
    

    // ---
    // MARK: Initialization
    // ---
    
    private init() {
    }


    // ---
    // MARK: Dependency management
    // ---
    
    public func addDependency(name: String, dependency: InjectorDependency) {
        dependencies[name] = dependency
    }
    
}
