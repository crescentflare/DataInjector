//
//  InjectorCondition.swift
//  DataInjector Pod
//
//  Library condition: a full condition
//  Contains condition items parsed from a string and can be checked if the condition is met
//

import UIKit

/// An item in an injector condition sequence
private class InjectorConditionItem {
    
    let conditionType: InjectorConditionType
    let valueType: InjectorDataType
    let value: Any?
    
    init(conditionType: InjectorConditionType, valueType: InjectorDataType, value: Any?) {
        self.conditionType = conditionType
        self.valueType = valueType
        self.value = value
    }
    
}

/// A utility which can be used to parse a condition string and check for its result
public class InjectorCondition {
    
    // ---
    // MARK: Members
    // ---
    
    private var items: [InjectorConditionItem] = []
    
    
    // ---
    // MARK: Initialization
    // ---
    
    public init(condition: String, fullRefData: [String: Any]? = nil, subRefData: [String: Any]? = nil) {
        if condition.characters.count == 0 {
            return
        }
        var curPos: String.CharacterView.Index? = condition.startIndex
        var findingData = true
        while findingData {
            // Reset state
            findingData = false
            curPos = findNonSpace(condition, start: curPos)
            
            // Check for condition operator or comparison type
            if curPos != nil && curPos != condition.endIndex {
                let curChr = condition.characters[curPos!]
                if InjectorConditionType.isReservedCharacter(chr: curChr) {
                    var foundType = InjectorConditionType.unknown
                    if let nextIndex = condition.index(curPos!, offsetBy: 2, limitedBy: condition.endIndex) {
                        let checkTypeString = condition.substring(with: curPos!..<nextIndex)
                        for checkType in InjectorConditionType.allValues() {
                            if checkType.rawValue.characters.count == 2 {
                                if checkTypeString.hasPrefix(checkType.rawValue) {
                                    foundType = checkType
                                    break
                                }
                            }
                        }
                        if foundType == .unknown {
                            for checkType in InjectorConditionType.allValues() {
                                if checkType.rawValue.characters.count == 1 {
                                    if checkTypeString.hasPrefix(checkType.rawValue) {
                                        foundType = checkType
                                        break
                                    }
                                }
                            }
                        }
                    }
                    if foundType != .unknown {
                        items.append(InjectorConditionItem(conditionType: foundType, valueType: .unknown, value: nil))
                        curPos = condition.index(curPos!, offsetBy: foundType.rawValue.characters.count)
                    } else {
                        break
                    }
                    if curPos != nil && curPos != condition.endIndex {
                        findingData = true
                        continue
                    } else {
                        break
                    }
                }
            }
            
            // Check for value type
            let valueType = InjectorDataDetector.detectFromString(condition, start: curPos)
            if valueType != .unknown {
                var endPos: String.CharacterView.Index? = InjectorDataDetector.endOfTypeString(type: valueType, value: condition, start: curPos)
                if endPos == nil {
                    endPos = findReservedCharacter(condition, start: curPos)
                }
                if endPos == nil {
                    break
                }
                var valueObject = obtainObject(type: valueType, condition: condition, start: curPos!, end: endPos!, fullRefData: fullRefData, subRefData: subRefData)
                items.append(InjectorConditionItem(conditionType: .unknown, valueType: InjectorDataDetector.detectFromObject(valueObject), value: valueObject))
                if endPos != nil && endPos != condition.endIndex {
                    curPos = endPos
                    findingData = true
                }
            }
        }
    }
    
    
    // ---
    // MARK: Condition checking
    // ---
    
    public func isMet() -> Bool {
        // First simplify the item list by changing comparisons into booleans
        var simplifiedItems: [InjectorConditionItem] = []
        var skipItems = 0
        for i in 0..<items.count {
            if skipItems > 0 {
                skipItems -= 1
                continue
            }
            let item = items[i]
            if i + 1 < items.count {
                let comparisonItem = items[i + 1]
                if comparisonItem.conditionType.isComparison() {
                    if i + 2 < items.count {
                        let compareItem = items[i + 2]
                        simplifiedItems.append(InjectorConditionItem(conditionType: .unknown, valueType: .boolean, value: compareItems(firstItem: item, secondItem: compareItem, type: comparisonItem.conditionType)))
                        skipItems = 2
                        continue
                    } else {
                        return false
                    }
                }
            }
            simplifiedItems.append(item)
        }
        
        // Finalize the comparison with operators
        var currentCondition = true
        var currentOperator = InjectorConditionType.and
        for item in simplifiedItems {
            if currentOperator != .unknown {
                if item.conditionType.isOperator() {
                    return false
                }
                let checkValue = InjectorConv.toBool(from: item.value) ?? false
                if currentOperator == .and {
                    currentCondition = currentCondition && checkValue
                } else {
                    currentCondition = currentCondition || checkValue
                }
                currentOperator = .unknown
            } else {
                currentOperator = item.conditionType
                if !currentOperator.isOperator() {
                    return false;
                }
            }
        }
        return currentCondition
    }
    
    private func compareItems(firstItem: InjectorConditionItem, secondItem: InjectorConditionItem, type: InjectorConditionType) -> Bool {
        switch type {
        case .equals:
            if secondItem.valueType == .string {
                return InjectorConv.toString(from: firstItem.value) == InjectorConv.toString(from: secondItem.value)
            } else if secondItem.valueType == .number {
                return InjectorConv.toInt(from: firstItem.value) == InjectorConv.toInt(from: secondItem.value)
            } else if secondItem.valueType == .decimalNumber {
                return InjectorConv.toDouble(from: firstItem.value) == InjectorConv.toDouble(from: secondItem.value)
            } else if secondItem.valueType == .boolean {
                return InjectorConv.toBool(from: firstItem.value) == InjectorConv.toBool(from: secondItem.value)
            } else if secondItem.valueType == .empty {
                return firstItem.valueType == .empty || (firstItem.valueType == .string && (InjectorConv.toString(from: firstItem.value)?.characters.count ?? 0) == 0)
            }
            return false
        case .notEquals:
            if secondItem.valueType == .string {
                return InjectorConv.toString(from: firstItem.value) != InjectorConv.toString(from: secondItem.value)
            } else if secondItem.valueType == .number {
                return InjectorConv.toInt(from: firstItem.value) != InjectorConv.toInt(from: secondItem.value)
            } else if secondItem.valueType == .decimalNumber {
                return InjectorConv.toDouble(from: firstItem.value) != InjectorConv.toDouble(from: secondItem.value)
            } else if secondItem.valueType == .boolean {
                return InjectorConv.toBool(from: firstItem.value) != InjectorConv.toBool(from: secondItem.value)
            } else if secondItem.valueType == .empty {
                return firstItem.valueType != .empty || (firstItem.valueType == .string && (InjectorConv.toString(from: firstItem.value)?.characters.count ?? 0) > 0)
            }
            return false
        case .bigger:
            if let firstDouble = InjectorConv.toDouble(from: firstItem.value) {
                if let secondDouble = InjectorConv.toDouble(from: secondItem.value) {
                    return firstDouble > secondDouble
                }
            }
            return false
        case .smaller:
            if let firstDouble = InjectorConv.toDouble(from: firstItem.value) {
                if let secondDouble = InjectorConv.toDouble(from: secondItem.value) {
                    return firstDouble < secondDouble
                }
            }
            return false
        case .biggerOrEquals:
            if let firstDouble = InjectorConv.toDouble(from: firstItem.value) {
                if let secondDouble = InjectorConv.toDouble(from: secondItem.value) {
                    return firstDouble >= secondDouble
                }
            }
            return false
        case .smallerOrEquals:
            if let firstDouble = InjectorConv.toDouble(from: firstItem.value) {
                if let secondDouble = InjectorConv.toDouble(from: secondItem.value) {
                    return firstDouble <= secondDouble
                }
            }
            return false
        default:
            return false
        }
    }
    
    
    // ---
    // MARK: Parse helpers
    // ---
    
    private func obtainObject(type: InjectorDataType, condition: String, start: String.CharacterView.Index, end: String.CharacterView.Index, fullRefData: [String: Any]?, subRefData: [String: Any]?) -> Any? {
        var item = condition.substring(with: start..<end).trimmingCharacters(in: CharacterSet.whitespacesAndNewlines)
        if type == .string {
            var quoteChr = item[item.startIndex]
            if quoteChr == "'" || quoteChr == "\"" {
                if item.hasSuffix("\(quoteChr)") {
                    item = item.substring(with: item.index(after: item.startIndex)..<item.index(before: item.endIndex))
                }
            }
        }
        return obtainConvertedObject(type: type, item: item, fullRefData: fullRefData, subRefData: subRefData)
    }
    
    private func obtainConvertedObject(type: InjectorDataType, item: String, fullRefData: [String: Any]?, subRefData: [String: Any]?) -> Any? {
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

    private func findReservedCharacter(_ string: String, start: String.CharacterView.Index?) -> String.CharacterView.Index? {
        if start == nil {
            return nil
        }
        for index in string.characters.indices[start!..<string.endIndex] {
            if InjectorConditionType.isReservedCharacter(chr: string.characters[index]) {
                return index
            }
        }
        return string.endIndex
    }
    
}
