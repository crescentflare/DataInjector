//
//  InjectorResult.swift
//  DataInjector Pod
//
//  Library utility: a result after data injection
//  Stores the result of a data injection which can be a modified object or an error
//

import Foundation

/// An enum to determine the error that occurred during injection
public enum InjectorError {
    
    case unknown
    case noIndexedCollection
    case indexInvalid
    case targetInvalid
    case sourceInvalid
    case notFound
    case nilNotAllowed
    case custom
    
}

/// A class to hold the result of an injection
public class InjectorResult {
    
    // --
    // MARK: Members
    // --

    public let modifiedObject: Any?
    public let error: InjectorError?
    public let customInfo: Any?
    

    // --
    // MARK: Initialization
    // --

    public init(withModifiedObject: Any?) {
        modifiedObject = withModifiedObject
        error = nil
        customInfo = nil
    }
    
    public init(withError: InjectorError) {
        modifiedObject = nil
        error = withError
        customInfo = nil
    }
    
    public init(withCustomErrorInfo: Any) {
        modifiedObject = nil
        error = .custom
        customInfo = withCustomErrorInfo
    }
    

    // --
    // MARK: Get values
    // --

    public func hasError() -> Bool {
        return error != nil
    }

}
