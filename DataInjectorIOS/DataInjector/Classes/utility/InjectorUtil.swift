//
//  InjectorUtil.swift
//  DataInjector Pod
//
//  Library utility: data injection utility
//  Utilities to access or change values within data sets (like dictionaries)
//

import Foundation

public class InjectorUtil {
    
    // ---
    // MARK: Initialization
    // ---
    
    private init() {
    }
    
    
    // ---
    // MARK: Dictionary
    // ---

    public static func itemFromDictionary(_ dictionary: [String: Any], path: String?, separator: Character = ".") -> Any? {
        if let splittedPath = path?.characters.split(separator: separator).map(String.init) {
            return itemFromDictionary(dictionary, path: splittedPath)
        }
        return nil
    }

    public static func itemFromDictionary(_ dictionary: [String: Any], path: [String]) -> Any? {
        if path.count > 0 {
            let checkObject = dictionary[path[0]]
            if let checkDictionary = checkObject as? [String: Any] {
                if path.count > 1 {
                    return itemFromDictionary(checkDictionary, path: Array(path[1..<path.count]))
                } else {
                    return checkDictionary
                }
            } else if let checkArray = checkObject as? [Any] {
                if path.count > 1 {
                    return itemFromArray(checkArray, path: Array(path[1..<path.count]))
                } else {
                    return checkArray
                }
            } else if path.count < 2 && !(checkObject is NSNull) {
                return checkObject
            }
        }
        return nil
    }

    
    // ---
    // MARK: Array
    // ---

    public static func itemFromArray(_ array: [Any], path: String?, separator: Character = ".") -> Any? {
        if let splittedPath = path?.characters.split(separator: separator).map(String.init) {
            return itemFromArray(array, path: splittedPath)
        }
        return nil
    }

    public static func itemFromArray(_ array: [Any], path: [String]) -> Any? {
        if path.count > 0 {
            if let index = Int(path[0]) {
                let checkObject = array[index]
                if let checkDictionary = checkObject as? [String: Any] {
                    if path.count > 1 {
                        return itemFromDictionary(checkDictionary, path: Array(path[1..<path.count]))
                    } else {
                        return checkDictionary
                    }
                } else if let checkArray = checkObject as? [Any] {
                    if path.count > 1 {
                        return itemFromArray(checkArray, path: Array(path[1..<path.count]))
                    } else {
                        return checkArray
                    }
                } else if path.count < 2 && !(checkObject is NSNull) {
                    return checkObject
                }
            }
        }
        return nil
    }

}
