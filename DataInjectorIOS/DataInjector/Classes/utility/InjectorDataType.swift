//
//  InjectorDataType.swift
//  DataInjector Pod
//
//  Library utility: a data type
//  An enum to store the detected data types by the data detector utility
//

import Foundation

public enum InjectorDataType {
    
    case unknown
    case string
    case number
    case decimalNumber
    case boolean
    case reference
    case subReference

}
