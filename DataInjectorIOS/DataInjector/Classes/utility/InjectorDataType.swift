//
//  InjectorDataType.swift
//  DataInjector Pod
//
//  Library utility: a data type
//  An enum to store the detected data types by the data detector utility
//

import Foundation

/// The type of data used by utilities which need string parsing
public enum InjectorDataType {
    
    case unknown
    case string
    case number
    case decimalNumber
    case boolean
    case empty
    case reference
    case subReference

}
