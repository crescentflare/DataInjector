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
            if path.count < 2 {
                if checkObject is NSNull {
                    return nil
                }
                return checkObject
            }
            if let checkDictionary = checkObject as? [String: Any] {
                return itemFromDictionary(checkDictionary, path: Array(path[1..<path.count]))
            } else if let checkArray = checkObject as? [Any] {
                return itemFromArray(checkArray, path: Array(path[1..<path.count]))
            }
        }
        return nil
    }

    public static func setItemOnDictionary(_ dictionary: inout [String: Any], path: String?, separator: Character = ".", value: Any?) {
        if let splittedPath = path?.characters.split(separator: separator).map(String.init) {
            setItemOnDictionary(&dictionary, path: splittedPath, value: value)
        }
    }
    
    public static func setItemOnDictionary(_ dictionary: inout [String: Any], path: [String], value: Any?) {
        if path.count > 0 {
            let key = path[0]
            if path.count > 1 {
                let nextPath = Array(path[1..<path.count])
                if var modifyDictionary = dictionary[key] as? [String: Any] {
                    setItemOnDictionary(&modifyDictionary, path: nextPath, value: value)
                    dictionary[key] = modifyDictionary
                } else if var modifyArray = dictionary[key] as? [Any] {
                    setItemOnArray(&modifyArray, path: nextPath, value: value)
                    dictionary[key] = modifyArray
                }
            } else {
                if value == nil {
                    dictionary.removeValue(forKey: key)
                } else {
                    dictionary[key] = value
                }
            }
        }
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
                if path.count < 2 {
                    if checkObject is NSNull {
                        return nil
                    }
                    return checkObject
                }
                if let checkDictionary = checkObject as? [String: Any] {
                    return itemFromDictionary(checkDictionary, path: Array(path[1..<path.count]))
                } else if let checkArray = checkObject as? [Any] {
                    return itemFromArray(checkArray, path: Array(path[1..<path.count]))
                }
            }
        }
        return nil
    }

    public static func setItemOnArray(_ array: inout [Any], path: String?, separator: Character = ".", value: Any?) {
        if let splittedPath = path?.characters.split(separator: separator).map(String.init) {
            setItemOnArray(&array, path: splittedPath, value: value)
        }
    }
    
    public static func setItemOnArray(_ array: inout [Any], path: [String], value: Any?) {
        if path.count > 0 {
            if let index = Int(path[0]) {
                if path.count > 1 {
                    let nextPath = Array(path[1..<path.count])
                    if var modifyDictionary = array[index] as? [String: Any] {
                        setItemOnDictionary(&modifyDictionary, path: nextPath, value: value)
                        array[index] = modifyDictionary
                    } else if var modifyArray = array[index] as? [Any] {
                        setItemOnArray(&modifyArray, path: nextPath, value: value)
                        array[index] = modifyArray
                    }
                } else {
                    if value == nil {
                        array.remove(at: index)
                    } else {
                        array[index] = value
                    }
                }
            }
        }
    }

}
