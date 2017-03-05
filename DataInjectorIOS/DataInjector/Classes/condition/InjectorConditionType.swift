//
//  InjectorConditionType.swift
//  DataInjector Pod
//
//  Library condition: condition type enum
//  Determines the type of the condition item in a sequence
//

import UIKit

/// A type of condition element used in the injector condition class
enum InjectorConditionType: String {

    // ---
    // MARK: Enum values
    // ---

    case unknown = ""
    case equals = "=="
    case notEquals = "!="
    case bigger = ">"
    case smaller = "<"
    case biggerOrEquals = ">="
    case smallerOrEquals = "<="
    case and = "&&"
    case or = "||"
    
    static func allValues() -> [InjectorConditionType] {
        return [equals, notEquals, bigger, smaller, biggerOrEquals, smallerOrEquals, and, or]
    }
    

    // ---
    // MARK: Helpers
    // ---
    
    func isComparison() -> Bool {
        return self == .equals || self == .notEquals || self == .bigger || self == .smaller || self == .biggerOrEquals || self == .smallerOrEquals
    }
    
    func isOperator() -> Bool {
        return self == .and || self == .or
    }

    static func isReservedCharacter(chr: Character) -> Bool {
        for value in allValues() {
            if value.rawValue.characters.count > 0 {
                let firstChr = value.rawValue.characters[value.rawValue.startIndex]
                if firstChr == chr {
                    return true
                }
            }
        }
        return false
    }

}
