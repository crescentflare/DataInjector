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
    
    private var dependencies: [InjectorDependency] = []
    

    // ---
    // MARK: Initialization
    // ---
    
    private init() {
    }


    // ---
    // MARK: Dependency management
    // ---
    
    public func addDependency(_ dependency: InjectorDependency) {
        dependencies.append(dependency)
    }
    
    public func dependency(forName: String) -> InjectorDependency? {
        for dependency in dependencies {
            if dependency.name == forName {
                return dependency
            }
        }
        return nil
    }

    public func dependencies(forNames: [String]) -> [InjectorDependency] {
        var dependencyList: [InjectorDependency] = []
        for dependency in dependencies {
            if forNames.contains(dependency.name) {
                dependencyList.append(dependency)
            }
        }
        return dependencyList
    }
    
    public func dependencyNameFrom(injectSource: String) -> String? {
        if !injectSource.hasPrefix("@.") && injectSource.hasPrefix("@") {
            let sourcePath = injectSource.characters.split(separator: ".").map(String.init)
            if sourcePath.count > 0 {
                return sourcePath[0].substring(from: sourcePath[0].index(after: sourcePath[0].startIndex))
            }
        }
        return nil
    }
    

    // ---
    // MARK: Filtering dependencies
    // ---

    public func filteredBaseDependencies(_ dependencies: [InjectorDependency]) -> [InjectorDependency] {
        var baseDependencies: [InjectorDependency] = []
        for dependency in dependencies {
            var foundBase = false
            for checkDependency in dependency.dependencies {
                if dependencies.filter({ el in el === checkDependency }).count == 0 {
                    foundBase = true
                    break
                }
            }
            if !foundBase {
                baseDependencies.append(dependency)
            }
        }
        return baseDependencies
    }
    
    public func filteredDependencies(_ dependencies: [InjectorDependency], forState: InjectorDependencyState, includeBase: Bool = true) -> [InjectorDependency] {
        var filteredList: [InjectorDependency] = []
        for dependency in dependencies {
            if dependency.state.isKindOfState(forState) {
                filteredList.append(dependency)
            }
            if includeBase {
                filteredList.append(contentsOf: filteredDependencies(dependency.dependencies, forState: forState))
            }
        }
        return removedDuplicateDependencies(filteredList)
    }
    
    public func filteredDependencies(_ dependencies: [InjectorDependency], excludingState: InjectorDependencyState, includeBase: Bool = true) -> [InjectorDependency] {
        var filteredList: [InjectorDependency] = []
        for dependency in dependencies {
            if !dependency.state.isKindOfState(excludingState) {
                filteredList.append(dependency)
            }
            if includeBase {
                filteredList.append(contentsOf: filteredDependencies(dependency.dependencies, excludingState: excludingState))
            }
        }
        return removedDuplicateDependencies(filteredList)
    }

    private func removedDuplicateDependencies(_ dependencies: [InjectorDependency]) -> [InjectorDependency] {
        var result: [InjectorDependency] = []
        for dependency in dependencies {
            var foundItem = false
            for checkDuplicate in result {
                if checkDuplicate === dependency {
                    foundItem = true
                    break
                }
            }
            if !foundItem {
                result.append(dependency)
            }
        }
        return result
    }


    // ---
    // MARK: Resolving dependencies
    // ---
    
    public func resolveDependency(dependency: InjectorDependency, forceRefresh: Bool = false, input: [String: String]? = nil) {
        // If the item is already busy loading or refreshing, bail out and wait for the existing resolution to be complete
        if dependency.state.isKindOfState(.loading) {
            return
        }
        
        // Return if the dependency is already up to date
        if dependency.state.isKindOfState(.resolved) && !dependency.isExpired() && !forceRefresh {
            NotificationCenter.default.post(name: Notification.Name(rawValue: InjectorDependencyManager.dependencyResolved), object: self, userInfo: ["dependency": dependency])
            return
        }
        
        // Check for input needed to resolve dependency
        var hasRequiredInput = true
        var sendInput: [String: String] = [:]
        for key in dependency.requiresInput {
            if let inputValue = input?[key] {
                sendInput[key] = inputValue
            } else {
                hasRequiredInput = false
                break
            }
        }
        
        // Try to resolve the dependency or fail without enough input
        if hasRequiredInput {
            dependency.state = dependency.state.isKindOfState(.resolved) ? .refreshing : .loading
            dependency.resolve(input: sendInput, completion: { success in
                if success {
                    dependency.resetExpiration()
                    dependency.state = .resolved
                    NotificationCenter.default.post(name: Notification.Name(rawValue: InjectorDependencyManager.dependencyResolved), object: self, userInfo: ["dependency": dependency])
                } else {
                    dependency.state = dependency.state.isKindOfState(.resolved) ? .refreshError : .error
                    NotificationCenter.default.post(name: Notification.Name(rawValue: InjectorDependencyManager.dependencyFailed), object: self, userInfo: ["dependency": dependency, "reason": "resolveError"])
                }
            })
        } else {
            NotificationCenter.default.post(name: Notification.Name(rawValue: InjectorDependencyManager.dependencyFailed), object: self, userInfo: ["dependency": dependency, "reason": "missingInput"])
        }
    }


    // ---
    // MARK: Handling data
    // ---

    public func generateInjectableData() -> [String: Any] {
        var data: [String: Any] = [:]
        for dependency in dependencies {
            if let injectionData = dependency.obtainInjectableData() {
                data[dependency.name] = injectionData
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
