//
//  InjectorDependencyState.swift
//  DataInjector Pod
//
//  Library dependency utility: the state
//  Used to keep track of the dependency state
//

import Foundation

public enum InjectorDependencyState {
    
    case pending
    case obtaining
    case refreshing
    case resolved
    case obtainError
    case refreshError

}
