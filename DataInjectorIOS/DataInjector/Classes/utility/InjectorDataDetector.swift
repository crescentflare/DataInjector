//
//  InjectorDataDetector.swift
//  DataInjector Pod
//
//  Library utility: detect data types
//  Determine a data type from an object or string
//

import Foundation

public class InjectorDataDetector {
    
    // ---
    // MARK: Initialization
    // ---
    
    private init() {
    }
    
    
    // ---
    // MARK: Detection
    // ---
    
    public static func detectFromObject(_ object: Any?) -> InjectorDataType {
        if object is String {
            return detectFromString(object as! String)
        } else if object is Float || object is Double {
            return .decimalNumber
        } else if object is Int {
            return .number
        } else if object is Bool {
            return .boolean
        }
        return .unknown
    }
    
    public static func detectFromString(_ value: String, start: String.CharacterView.Index? = nil) -> InjectorDataType {
        var offsetString = value
        if start != nil {
            offsetString = value.substring(from: start!)
        }
        if offsetString.hasPrefix("'") || offsetString.hasPrefix("\"") {
            return .string
        } else if offsetString.hasPrefix("@.") {
            return .subReference
        } else if offsetString.hasPrefix("@") {
            return .reference
        } else if offsetString.hasPrefix("true") || offsetString.hasPrefix("false") {
            return .boolean
        } else if offsetString.characters.count > 0 && (offsetString.characters[offsetString.startIndex] >= "0" && offsetString.characters[offsetString.startIndex] <= "9") {
            return containsDot(numberString: offsetString) ? .decimalNumber : .number
        } else if offsetString.characters.count > 1 && offsetString.characters[offsetString.startIndex] == "-" && (offsetString.characters[offsetString.index(after: offsetString.startIndex)] >= "0" && offsetString.characters[offsetString.index(after: offsetString.startIndex)] <= "9") {
            return containsDot(numberString: offsetString) ? .decimalNumber : .number
        }
        return .string
    }
    
    public static func endOfTypeString(type: InjectorDataType, value: String, start: String.CharacterView.Index? = nil) -> String.CharacterView.Index? {
        var checkStart = start ?? value.startIndex
        var offsetString = value.substring(from: checkStart)
        if type == .string && (offsetString.hasPrefix("'") || offsetString.hasPrefix("\"")) {
            let findEndChar = offsetString.characters[offsetString.startIndex]
            checkStart = value.index(after: checkStart)
            for index in value.characters.indices[checkStart..<value.endIndex] {
                if value.characters[index] == findEndChar {
                    return value.index(after: index)
                }
            }
        } else if type == .number || type == .decimalNumber {
            checkStart = value.index(after: checkStart)
            for index in value.characters.indices[checkStart..<value.endIndex] {
                if !(value.characters[index] >= "0" && value.characters[index] <= "9") && value.characters[index] != "." {
                    return index
                }
            }
        } else if type == .boolean {
            if offsetString.hasPrefix("true") {
                return value.index(checkStart, offsetBy: 4)
            } else if offsetString.hasPrefix("false") {
                return value.index(checkStart, offsetBy: 5)
            }
        }
        return nil
    }

    
    // ---
    // MARK: Helper
    // ---
    
    private static func containsDot(numberString: String) -> Bool {
        for index in numberString.characters.indices {
            let chr = numberString.characters[index]
            if chr == "." {
                return true
            }
            if !(chr >= "0" && chr <= "9") && chr != "-" {
                return false
            }
        }
        return false
    }

}
