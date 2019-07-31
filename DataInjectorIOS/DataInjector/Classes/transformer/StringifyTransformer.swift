//
//  StringifyTransformer.swift
//  DataInjector Pod
//
//  Library transformer: transform data to a string
//  Convert data to a string, including nested structures
//

import Foundation

/// A transformer converting simple data or nested structures to a string
open class StringifyTransformer: BaseTransformer {
    
    // --
    // MARK: Members
    // --
    
    public var sourceDataPath: InjectorPath?
    public var includeSpaces = false
    public var includeNewlines = false

    
    // --
    // MARK: Initialization
    // --
    
    public override init() {
    }

    
    // --
    // MARK: Manual transformation
    // --
    
    public static func stringify(fromData: Any?, includeSpaces: Bool = false, includeNewlines: Bool = false) -> InjectorResult {
        return InjectorResult(withModifiedObject: dataToString(data: fromData, includeSpaces: includeSpaces, includeNewlines: includeNewlines, level: 0))
    }

    
    // --
    // MARK: General injection
    // --
    
    open override func appliedTransformation(sourceData: Any?) -> InjectorResult {
        let useSourceData = DataInjector.get(from: sourceData, path: sourceDataPath ?? InjectorPath(path: ""))
        return StringifyTransformer.stringify(fromData: useSourceData, includeSpaces: includeSpaces, includeNewlines: includeNewlines)
    }


    // --
    // MARK: Internal conversion
    // --

    private static func dataToString(data: Any?, includeSpaces: Bool, includeNewlines: Bool, level: Int) -> String {
        if let dict = data as? [AnyHashable: Any?] {
            return dictToString(dict: dict, includeSpaces: includeSpaces, includeNewlines: includeNewlines, level: level)
        } else if let array = data as? [Any?] {
            return arrayToString(array: array, includeSpaces: includeSpaces, includeNewlines: includeNewlines, level: level)
        } else if data != nil {
            var result = InjectorConv.asString(value: data)
            if result == nil {
                result = data.debugDescription
            }
            if let stringResult = result {
                if data is String {
                    return "\"\(stringResult)\""
                }
                return stringResult
            }
        }
        return "null"
    }
    
    private static func dictToString(dict: [AnyHashable: Any?], includeSpaces: Bool, includeNewlines: Bool, level: Int) -> String {
        var result = "{"
        var firstElement = true
        if includeNewlines {
            result += "\n"
        }
        for key in dict.keys {
            let stringKey = dataToString(data: key, includeSpaces: includeSpaces, includeNewlines: includeNewlines, level: 0)
            let stringValue = dataToString(data: dict[key] ?? nil, includeSpaces: includeSpaces, includeNewlines: includeNewlines, level: level + 1)
            if !firstElement {
                result += ","
                if includeNewlines {
                    result += "\n"
                } else if includeSpaces {
                    result += " "
                }
            }
            if includeNewlines {
                for _ in 0..<level * 2 + 2 {
                    result += " "
                }
            }
            result += stringKey
            result += ":"
            if includeSpaces {
                result += " "
            }
            result += stringValue
            firstElement = false
        }
        if includeNewlines {
            if !firstElement {
                result += "\n"
            }
            for _ in 0..<level * 2 {
                result += " "
            }
        }
        result += "}"
        return result
    }
    
    private static func arrayToString(array: [Any?], includeSpaces: Bool, includeNewlines: Bool, level: Int) -> String {
        var result = "["
        var firstElement = true
        if includeNewlines {
            result += "\n"
        }
        for item in array {
            let stringItem = dataToString(data: item, includeSpaces: includeSpaces, includeNewlines: includeNewlines, level: level + 1)
            if !firstElement {
                result += ","
                if includeNewlines {
                    result += "\n"
                } else if includeSpaces {
                    result += " "
                }
            }
            if includeNewlines {
                for _ in 0..<level * 2 + 2 {
                    result += " "
                }
            }
            result += stringItem
            firstElement = false
        }
        if includeNewlines {
            if !firstElement {
                result += "\n"
            }
            for _ in 0..<level * 2 {
                result += " "
            }
        }
        result += "]"
        return result
    }

}
