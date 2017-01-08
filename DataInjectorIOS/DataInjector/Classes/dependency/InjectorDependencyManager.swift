//
//  InjectorDependencyManager.swift
//  DataInjector Pod
//
//  Library dependency utility: manage dependencies
//  Provide a central place to keep track of all dependencies within the application
//

import Foundation

public class InjectorDependencyManager {
    
    // --
    // MARK: Notification center IDs
    // --
    
    public static let dependencyResolved = "InjectorDependencyManager.dependencyResolved"
    public static let dependencyFailed = "InjectorDependencyManager.dependencyFailed"

    
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
    
    public func getDependency(name: String) -> InjectorDependency? {
        return dependencies[name]
    }
    

    // ---
    // MARK: Resolving dependencies
    // ---
    
    public func getDependenciesInProgress(checkDependencies: [String], includeBase: Bool = true) -> [String] {
        var inProgress: [String] = []
        for checkDependency in checkDependencies {
            if let dependencyItem = dependencies[checkDependency] {
                if dependencyItem.state == .obtaining || dependencyItem.state == .refreshing {
                    inProgress.append(checkDependency)
                }
                if includeBase {
                    inProgress.append(contentsOf: getDependenciesInProgress(checkDependencies: dependencyItem.dependencies))
                }
            }
        }
        return Array(Set(inProgress))
    }

    public func getDependenciesWithError(checkDependencies: [String], includeBase: Bool = true) -> [String] {
        var withError: [String] = []
        for checkDependency in checkDependencies {
            if let dependencyItem = dependencies[checkDependency] {
                if dependencyItem.isError() {
                    withError.append(checkDependency)
                }
                if includeBase {
                    withError.append(contentsOf: getDependenciesWithError(checkDependencies: dependencyItem.dependencies))
                }
            }
        }
        return Array(Set(withError))
    }

    public func getUnresolvedDependencies(checkDependencies: [String], includeBase: Bool = true) -> [String] {
        var unresolvedDependencies: [String] = []
        for checkDependency in checkDependencies {
            if let dependencyItem = dependencies[checkDependency] {
                if dependencyItem.state != .resolved && dependencyItem.state != .refreshing && dependencyItem.state != .refreshError {
                    unresolvedDependencies.append(checkDependency)
                }
                if includeBase {
                    unresolvedDependencies.append(contentsOf: getUnresolvedDependencies(checkDependencies: dependencyItem.dependencies))
                }
            } else {
                unresolvedDependencies.append(checkDependency)
            }
        }
        return Array(Set(unresolvedDependencies))
    }
    
    public func getUnresolvedBaseDependencies(checkDependencies: [String]) -> [String] {
        var baseDependencies: [String] = []
        for checkDependency in checkDependencies {
            if let dependencyItem = dependencies[checkDependency] {
                var recursiveBaseDependencies = getUnresolvedBaseDependencies(checkDependencies: dependencyItem.dependencies)
                if recursiveBaseDependencies.count > 0 {
                    baseDependencies.append(contentsOf: recursiveBaseDependencies)
                } else {
                    baseDependencies.append(contentsOf: getUnresolvedDependencies(checkDependencies: dependencyItem.dependencies))
                }
            }
        }
        return Array(Set(baseDependencies))
    }

    public func resolveDependency(dependency: String, forceRefresh: Bool = false, input: [String: String]? = nil) {
        if let dependencyItem = dependencies[dependency] {
            // If the item is already busy resolving, bail out and wait for the existing resolution to be complete
            if dependencyItem.state == .obtaining || dependencyItem.state == .refreshing {
                return
            }
            
            // Return if the dependency is already up to date
            if dependencyItem.state == .resolved && !dependencyItem.isExpired() && !forceRefresh {
                NotificationCenter.default.post(name: Notification.Name(rawValue: InjectorDependencyManager.dependencyResolved), object: self, userInfo: ["dependency": dependency])
                return
            }
            
            // Check for input needed to resolve dependency
            var hasRequiredInput = true
            var sendInput: [String: String] = [:]
            for key in dependencyItem.requiresInput {
                if let inputValue = input?[key] {
                    sendInput[key] = inputValue
                } else {
                    hasRequiredInput = false
                    break
                }
            }
            
            // Try to resolve the dependency or fail without enough input
            if hasRequiredInput {
                dependencyItem.state = dependencyItem.state == .resolved || dependencyItem.state == .refreshError ? .refreshing : .obtaining
                dependencyItem.resolve(input: sendInput, completion: { success in
                    if success {
                        dependencyItem.resetExpiration()
                        dependencyItem.state = .resolved
                        NotificationCenter.default.post(name: Notification.Name(rawValue: InjectorDependencyManager.dependencyResolved), object: self, userInfo: ["dependency": dependency])
                    } else {
                        dependencyItem.state = dependencyItem.state == .refreshing ? .refreshError : .obtainError
                        NotificationCenter.default.post(name: Notification.Name(rawValue: InjectorDependencyManager.dependencyFailed), object: self, userInfo: ["dependency": dependency, "reason": "resolveError"])
                    }
                })
            } else {
                NotificationCenter.default.post(name: Notification.Name(rawValue: InjectorDependencyManager.dependencyFailed), object: self, userInfo: ["dependency": dependency, "reason": "missingInput"])
            }
        } else {
            NotificationCenter.default.post(name: Notification.Name(rawValue: InjectorDependencyManager.dependencyFailed), object: self, userInfo: ["dependency": dependency, "reason": "notExists"])
        }
    }


    // ---
    // MARK: Handling data
    // ---

    public func generateInjectableData() -> [String: Any] {
        var data: [String: Any] = [:]
        for (name, dependency) in dependencies {
            if let injectionData = dependency.obtainInjectableData() {
                data[name] = injectionData
            }
        }
        return data
    }
    
    
    // --
    // MARK: Observers
    // --
    
    public func addDataObserver(_ observer: NSObject, selector: Selector, name: String) {
        NotificationCenter.default.addObserver(observer, selector: selector, name: NSNotification.Name(rawValue: name), object: self)
    }
    
    public func removeDataObserver(_ observer: NSObject, name: String) {
        NotificationCenter.default.removeObserver(observer, name: NSNotification.Name(rawValue: name), object: self)
    }

}
