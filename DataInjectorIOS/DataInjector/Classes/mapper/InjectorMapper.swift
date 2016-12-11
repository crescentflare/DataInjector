//
//  InjectorMapper.swift
//  DataInjector Pod
//
//  Library utility: a mapping utility
//  Contains a list of mapping values and matches one of them based on a given value
//

import Foundation

public class InjectorMapper {
    
    // ---
    // MARK: Members
    // ---
    
    private var mappingIndices: [Any] = []
    private var mappingValues: [Any] = []
    private let fullRefData: [String: Any]?
    private let subRefData: [String: Any]?
    private var initFail = false


    // ---
    // MARK: Initialization
    // ---
    
    public init(mapping: String, fullRefData: [String: Any]? = nil, subRefData: [String: Any]? = nil) {
        // Parse away brackets if needed
        var checkMapping = mapping
        self.fullRefData = fullRefData
        self.subRefData = subRefData
        if checkMapping.hasPrefix("[") {
            if let endBracket = checkMapping.range(of: "]", options: .backwards)?.lowerBound {
                checkMapping = checkMapping.substring(with: checkMapping.index(after: checkMapping.startIndex)..<endBracket)
            } else {
                initFail = true
                return
            }
        }
        
        // Loop through mapping and find items
        var curPos: String.CharacterView.Index? = checkMapping.startIndex
        var findingData = true
        while findingData {
            // Reset state
            findingData = false
            curPos = findNonSpace(checkMapping, start: curPos)
            
            // Find the item type for the mapping key
            let keyType = InjectorDataDetector.detectFromString(checkMapping, start: curPos)
            var endPos: String.CharacterView.Index? = InjectorDataDetector.endOfTypeString(type: keyType, value: checkMapping, start: curPos)
            if endPos == nil {
                endPos = findAssignmentSeparator(checkMapping, start: curPos)
            }
            if endPos == nil {
                break
            }
            var keyObject = obtainObject(type: keyType, mapping: checkMapping, start: curPos!, end: endPos!)

            // Find the item type for the mapping value
            curPos = findAssignmentSeparator(checkMapping, start: endPos)
            if curPos != nil {
                curPos = checkMapping.index(curPos!, offsetBy: 2)
            }
            curPos = findNonSpace(checkMapping, start: curPos)
            let valueType = InjectorDataDetector.detectFromString(checkMapping, start: curPos)
            endPos = InjectorDataDetector.endOfTypeString(type: valueType, value: checkMapping, start: curPos)
            if endPos == nil {
                endPos = findDividerSeparator(checkMapping, start: curPos)
            }
            if endPos == nil {
                break
            }
            var valueObject = obtainObject(type: valueType, mapping: checkMapping, start: curPos!, end: endPos!)

            // Add the value
            if keyObject != nil && valueObject != nil {
                mappingIndices.append(keyObject)
                mappingValues.append(valueObject)
                endPos = findDividerSeparator(checkMapping, start: endPos)
                if endPos != nil && endPos != checkMapping.endIndex {
                    curPos = checkMapping.index(after: endPos!)
                    findingData = true
                }
            } else {
                initFail = true
                return
            }
        }
        if mappingIndices.count == 0 {
            initFail = true
        }
    }
    
    
    // ---
    // MARK: Fetch mapped value
    // ---
    
    public func obtainMapping(item: Any) -> Any? {
        if !initFail {
            var checkItem: Any? = item
            let itemType = InjectorDataDetector.detectFromObject(item)
            if itemType == .reference || itemType == .subReference {
                checkItem = obtainConvertedObject(type: itemType, item: InjectorConv.toString(from: checkItem) ?? "")
                if checkItem == nil {
                    return nil
                }
            }
            for i in 0..<mappingIndices.count {
                let key = mappingIndices[i]
                if let stringKey = key as? String {
                    if stringKey == "else" {
                        continue
                    }
                    if stringKey == InjectorConv.toString(from: checkItem) {
                        return mappingValues[i]
                    }
                } else if let doubleKey = key as? Double {
                    if doubleKey == InjectorConv.toDouble(from: checkItem) {
                        return mappingValues[i]
                    }
                } else if let integerKey = key as? Int {
                    if integerKey == InjectorConv.toInt(from: checkItem) {
                        return mappingValues[i]
                    }
                } else if let boolKey = key as? Bool {
                    if boolKey == InjectorConv.toBool(from: checkItem) {
                        return mappingValues[i]
                    }
                }
            }
            for i in 0..<mappingIndices.count {
                let key = mappingIndices[i]
                if let stringKey = key as? String {
                    if stringKey == "else" {
                        return mappingValues[i]
                    }
                }
            }
        }
        return nil
    }


    // ---
    // MARK: Helper
    // ---
    
    private func obtainObject(type: InjectorDataType, mapping: String, start: String.CharacterView.Index, end: String.CharacterView.Index) -> Any? {
        var item = mapping.substring(with: start..<end).trimmingCharacters(in: CharacterSet.whitespacesAndNewlines)
        if type == .string {
            var quoteChr = item[item.startIndex]
            if quoteChr == "'" || quoteChr == "\"" {
                if item.hasSuffix("\(quoteChr)") {
                    item = item.substring(with: item.index(after: item.startIndex)..<item.index(before: item.endIndex))
                }
            }
        }
        return obtainConvertedObject(type: type, item: item)
    }
    
    private func obtainConvertedObject(type: InjectorDataType, item: String) -> Any? {
        switch type {
        case .string:
            return item
        case .number:
            return InjectorConv.toInt(from: item)
        case .decimalNumber:
            return InjectorConv.toDouble(from: item)
        case .boolean:
            return InjectorConv.toBool(from: item)
        case .empty:
            return nil
        case .reference:
            if fullRefData != nil {
                return InjectorUtil.itemFromDictionary(fullRefData!, path: item.substring(from: item.index(after: item.startIndex)))
            }
            break
        case .subReference:
            if subRefData != nil {
                return InjectorUtil.itemFromDictionary(subRefData!, path: item.substring(from: item.index(item.startIndex, offsetBy: 2)))
            }
            break
        default:
            return nil
        }
        return nil
    }
    
    private func findNonSpace(_ string: String, start: String.CharacterView.Index?) -> String.CharacterView.Index? {
        if start == nil {
            return nil
        }
        for index in string.characters.indices[start!..<string.endIndex] {
            if string.characters[index] != " " {
                return index
            }
        }
        return nil
    }
    
    private func findAssignmentSeparator(_ string: String, start: String.CharacterView.Index?) -> String.CharacterView.Index? {
        if start == nil {
            return nil
        }
        var foundMinus = false
        for index in string.characters.indices[start!..<string.endIndex] {
            if string[index] == ">" && foundMinus {
                return string.index(before: index)
            }
            foundMinus = string[index] == "-"
        }
        return nil
    }
    
    private func findDividerSeparator(_ string: String, start: String.CharacterView.Index?) -> String.CharacterView.Index? {
        if start == nil {
            return nil
        }
        for index in string.characters.indices[start!..<string.endIndex] {
            if string.characters[index] == "," {
                return index
            }
        }
        return string.endIndex
    }

}
