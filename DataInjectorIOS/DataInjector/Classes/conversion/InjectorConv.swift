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
    
    // ---
    // MARK: Initialization
    // ---
    
    private init() {
    }
    
    
    // ---
    // MARK: Parsing
    // ---

    public static func toDate(from: Any?) -> Date? {
        if let stringDate = from as? String {
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
    

    // ---
    // MARK: Primitive types
    // ---
    
    public static func toString(from: Any?) -> String? {
        if from is String {
            return from as? String
        } else if let doubleValue = from as? Double {
            return String(doubleValue)
        } else if let floatValue = from as? Float {
            return String(floatValue)
        } else if let intValue = from as? Int {
            return String(intValue)
        } else if let boolValue = from as? Bool {
            return boolValue ? "true" : "false"
        }
        return nil
    }
    
    public static func toDouble(from: Any?) -> Double? {
        if let stringValue = from as? String {
            return Double(stringValue)
        } else if from is Double {
            return from as? Double
        } else if let floatValue = from as? Float {
            return Double(floatValue)
        } else if let intValue = from as? Int {
            return Double(intValue)
        } else if let boolValue = from as? Bool {
            return boolValue ? 1 : 0
        }
        return nil
    }
    
    public static func toFloat(from: Any?) -> Float? {
        if let stringValue = from as? String {
            return Float(stringValue)
        } else if let doubleValue = from as? Double {
            return Float(doubleValue)
        } else if from is Float {
            return from as? Float
        } else if let intValue = from as? Int {
            return Float(intValue)
        } else if let boolValue = from as? Bool {
            return boolValue ? 1 : 0
        }
        return nil
    }
    
    public static func toInt(from: Any?) -> Int? {
        if let stringValue = from as? String {
            if let intValue = Int(stringValue) {
                return intValue
            }
            if let doubleValue = Double(stringValue) {
                return Int(doubleValue)
            }
        } else if let doubleValue = from as? Double {
            return Int(doubleValue)
        } else if let floatValue = from as? Float {
            return Int(floatValue)
        } else if from is Int {
            return from as? Int
        } else if let boolValue = from as? Bool {
            return boolValue ? 1 : 0
        }
        return nil
    }
    
    public static func toBool(from: Any?) -> Bool? {
        if let stringValue = from as? String {
            return Bool(stringValue)
        } else if let doubleValue = from as? Double {
            return doubleValue > 0
        } else if let floatValue = from as? Float {
            return floatValue > 0
        } else if let intValue = from as? Int {
            return intValue > 0
        } else if from is Bool {
            return from as? Bool
        }
        return nil
    }
    
}
