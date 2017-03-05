//
//  InjectorDependencyState.swift
//  DataInjector Pod
//
//  Library dependency utility: the state
//  Used to keep track of the dependency state
//

import Foundation

/// The state of the dependency using values which can be used as flags for bitwise comparisons
public enum InjectorDependencyState: Int {
    
    case idle = 0
    case loading = 1
    case resolved = 2
    case refreshing = 3
    case error = 4
    case refreshError = 6
    
    func isKindOfState(_ state: InjectorDependencyState) -> Bool {
        if state == .idle && self == .idle {
            return true
        }
        return (rawValue & state.rawValue) == state.rawValue
    }

}
