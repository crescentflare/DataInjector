//
//  InjectorConv.swift
//  DataInjector Pod
//
//  Library conversion: data type conversion
//  Easily recognize and convert between data types
//

import Foundation

/// A utility class to easily convert between data types
public class InjectorConv {
    
    // --
    // MARK: Initialization
    // --
    
    private init() {
    }
    
    
    // --
    // MARK: Array conversion
    // --
    
    public static func asStringArray(value: Any?) -> [String] {
        var array = [String]()
        if let valueArray = value as? [Any] {
            for valueItem in valueArray {
                if let stringValue = asString(value: valueItem) {
                    array.append(stringValue)
                }
            }
        }
        return array
    }
    
    public static func asDoubleArray(value: Any?) -> [Double] {
        var array = [Double]()
        if let valueArray = value as? [Any] {
            for valueItem in valueArray {
                if let doubleValue = asDouble(value: valueItem) {
                    array.append(doubleValue)
                }
            }
        }
        return array
    }
    
    public static func asFloatArray(value: Any?) -> [Float] {
        var array = [Float]()
        if let valueArray = value as? [Any] {
            for valueItem in valueArray {
                if let floatValue = asFloat(value: valueItem) {
                    array.append(floatValue)
                }
            }
        }
        return array
    }
    
    public static func asIntArray(value: Any?) -> [Int] {
        var array = [Int]()
        if let valueArray = value as? [Any] {
            for valueItem in valueArray {
                if let intValue = asInt(value: valueItem) {
                    array.append(intValue)
                }
            }
        }
        return array
    }
    
    public static func asBoolArray(value: Any?) -> [Bool] {
        var array = [Bool]()
        if let valueArray = value as? [Any] {
            for valueItem in valueArray {
                if let boolValue = asBool(value: valueItem) {
                    array.append(boolValue)
                }
            }
        }
        return array
    }


    // --
    // MARK: Date parsing
    // --

    public static func asDateArray(value: Any?) -> [Date] {
        var array = [Date]()
        if let valueArray = value as? [Any] {
            for valueItem in valueArray {
                if let dateValue = asDate(value: valueItem) {
                    array.append(dateValue)
                }
            }
        }
        return array
    }

    public static func asDate(value: Any?) -> Date? {
        if let stringDate = value as? String {
            let formats = [
                "yyyy-MM-dd'T'HH:mm:ssZZZZ",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd"
            ]
            for format in formats {
                let dateFormatter = DateFormatter()
                if format.hasSuffix("Z") {
                    dateFormatter.timeZone = TimeZone(identifier: "UTC")
                }
                dateFormatter.dateFormat = format
                if let date = dateFormatter.date(from: stringDate) {
                    return date
                }
            }
        }
        return nil
    }
    

    // --
    // MARK: Primitive types
    // --
    
    public static func asString(value: Any?) -> String? {
        if value is String {
            return value as? String
        } else if let doubleValue = value as? Double {
            return String(doubleValue)
        } else if let floatValue = value as? Float {
            return String(floatValue)
        } else if let intValue = value as? Int {
            return String(intValue)
        } else if let boolValue = value as? Bool {
            return boolValue ? "true" : "false"
        }
        return nil
    }
    
    public static func asDouble(value: Any?) -> Double? {
        if let stringValue = value as? String {
            return Double(stringValue)
        } else if value is Double {
            return value as? Double
        } else if let floatValue = value as? Float {
            return Double(floatValue)
        } else if let intValue = value as? Int {
            return Double(intValue)
        } else if let boolValue = value as? Bool {
            return boolValue ? 1 : 0
        }
        return nil
    }
    
    public static func asFloat(value: Any?) -> Float? {
        if let stringValue = value as? String {
            return Float(stringValue)
        } else if let doubleValue = value as? Double {
            return Float(doubleValue)
        } else if value is Float {
            return value as? Float
        } else if let intValue = value as? Int {
            return Float(intValue)
        } else if let boolValue = value as? Bool {
            return boolValue ? 1 : 0
        }
        return nil
    }
    
    public static func asInt(value: Any?) -> Int? {
        if let stringValue = value as? String {
            if let intValue = Int(stringValue) {
                return intValue
            }
            if let doubleValue = Double(stringValue) {
                return Int(doubleValue)
            }
        } else if let doubleValue = value as? Double {
            return Int(doubleValue)
        } else if let floatValue = value as? Float {
            return Int(floatValue)
        } else if value is Int {
            return value as? Int
        } else if let boolValue = value as? Bool {
            return boolValue ? 1 : 0
        }
        return nil
    }
    
    public static func asBool(value: Any?) -> Bool? {
        if let stringValue = value as? String {
            return Bool(stringValue)
        } else if let doubleValue = value as? Double {
            return doubleValue > 0
        } else if let floatValue = value as? Float {
            return floatValue > 0
        } else if let intValue = value as? Int {
            return intValue > 0
        } else if value is Bool {
            return value as? Bool
        }
        return nil
    }
    
}
