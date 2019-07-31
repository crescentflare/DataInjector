//
//  DuplicateInjector.swift
//  DataInjector Pod
//
//  Library injector: duplicate an item
//  Duplicates an item in an array based on the amount of elements in a data source or an amount specified manually
//

import Foundation

/// An injector duplicating an item in an array depending on the given data source or specified manually
open class DuplicateInjector: BaseInjector {
    
    // --
    // MARK: Members
    // --

    public var subInjectors = [BaseInjector]()
    public var sourceTransformers = [BaseTransformer]()
    public var targetItemPath: InjectorPath?
    public var duplicateItemIndex: Int = 0
    public var betweenItemIndex: Int = -1
    public var emptyItemIndex: Int = -1
    public var sourceDataPath: InjectorPath?
    public var overrideSourceData: Any?
    public var count: Int?

    
    // --
    // MARK: Initialization
    // --
    
    public override init() {
    }

    
    // --
    // MARK: Manual injection
    // --
    
    public static func duplicateItem(inArray: Any?, count: Int = 2, duplicateItemIndex: Int = 0, betweenItemIndex: Int = -1, emptyItemIndex: Int = -1, duplicateCallback: ((_ duplicatedItem: Any?, _ duplicateIndex: Int) -> InjectorResult)? = nil) -> InjectorResult {
        if let inArray = inArray as? [Any?] {
            // Fetch items for duplication
            if duplicateItemIndex < 0 || duplicateItemIndex >= inArray.count {
                return InjectorResult(withError: .notFound)
            } else if betweenItemIndex >= 0 && betweenItemIndex >= inArray.count {
                return InjectorResult(withError: .notFound)
            } else if emptyItemIndex >= 0 && emptyItemIndex >= inArray.count {
                return InjectorResult(withError: .notFound)
            }
            let duplicateItem = inArray[duplicateItemIndex]
            let betweenItem = betweenItemIndex >= 0 && betweenItemIndex < inArray.count ? inArray[betweenItemIndex] : nil
            let emptyItem = emptyItemIndex >= 0 && emptyItemIndex < inArray.count ? inArray[emptyItemIndex] : nil
            
            // Prepare new array without duplicate items
            var modifiedArray = [Any?]()
            for index in inArray.indices {
                if index != duplicateItemIndex && index != betweenItemIndex && index != emptyItemIndex {
                    modifiedArray.append(inArray[index])
                }
            }
            
            // Duplicate items
            var insertIndex = duplicateItemIndex
            for i in 0..<count {
                if i > 0, let betweenItem = betweenItem {
                    modifiedArray.insert(betweenItem, at: insertIndex)
                    insertIndex += 1
                }
                if let duplicateCallback = duplicateCallback {
                    let result = duplicateCallback(duplicateItem, i)
                    if result.hasError() {
                        return result
                    }
                    modifiedArray.insert(result.modifiedObject, at: insertIndex)
                } else {
                    modifiedArray.insert(duplicateItem, at: insertIndex)
                }
                insertIndex += 1
            }
            if count == 0, let emptyItem = emptyItem {
                modifiedArray.insert(emptyItem, at: insertIndex)
            }
            return InjectorResult(withModifiedObject: modifiedArray)
        }
        return InjectorResult(withError: .targetInvalid)
    }

    public static func duplicateItem(inArray: Any?, sourceArray: Any?, duplicateItemIndex: Int = 0, betweenItemIndex: Int = -1, emptyItemIndex: Int = -1, duplicateCallback: ((_ duplicatedItem: Any?, _ sourceItem: Any?) -> InjectorResult)? = nil) -> InjectorResult {
        if let sourceArray = sourceArray as? [Any?] {
            return duplicateItem(inArray: inArray, count: sourceArray.count, duplicateItemIndex: duplicateItemIndex, betweenItemIndex: betweenItemIndex, emptyItemIndex: emptyItemIndex, duplicateCallback: { duplicatedItem, duplicateIndex in
                if let duplicateCallback = duplicateCallback {
                    return duplicateCallback(duplicatedItem, sourceArray[duplicateIndex])
                }
                return InjectorResult(withModifiedObject: duplicatedItem)
            })
        }
        return InjectorResult(withError: .sourceInvalid)
    }
    
    
    // --
    // MARK: General injection
    // --

    override open func appliedInjection(targetData: Any?, sourceData: Any? = nil) -> InjectorResult {
        // Use manual count when specified
        if let count = count {
            return DataInjector.inject(into: targetData, path: targetItemPath ?? InjectorPath(path: ""), modifyCallback: { originalData in
                return DuplicateInjector.duplicateItem(inArray: originalData, count: count, duplicateItemIndex: duplicateItemIndex, betweenItemIndex: betweenItemIndex, emptyItemIndex: emptyItemIndex)
            })
        }
        
        // Prepare source data with optional transformation
        var useSourceData = overrideSourceData ?? sourceData
        useSourceData = DataInjector.get(from: useSourceData, path: sourceDataPath ?? InjectorPath(path: ""))
        for transformer in sourceTransformers {
            let result = transformer.appliedTransformation(sourceData: useSourceData)
            if result.hasError() {
                return result
            }
            useSourceData = result.modifiedObject
        }

        // Duplicate based on source data
        return DataInjector.inject(into: targetData, path: targetItemPath ?? InjectorPath(path: ""), modifyCallback: { originalData in
            return DuplicateInjector.duplicateItem(inArray: originalData, sourceArray: useSourceData, duplicateItemIndex: duplicateItemIndex, betweenItemIndex: betweenItemIndex, emptyItemIndex: emptyItemIndex, duplicateCallback: { duplicatedItem, sourceItem in
                var modifiedDuplicatedItem = duplicatedItem
                for subInjector in self.subInjectors {
                    let result = subInjector.appliedInjection(targetData: modifiedDuplicatedItem, sourceData: sourceItem)
                    if result.hasError() {
                        return result
                    }
                    modifiedDuplicatedItem = result.modifiedObject
                }
                return InjectorResult(withModifiedObject: modifiedDuplicatedItem)
            })
        })
    }

}
