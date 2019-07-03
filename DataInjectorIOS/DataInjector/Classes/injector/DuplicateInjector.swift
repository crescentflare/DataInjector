//
//  DuplicateInjector.swift
//  DataInjector Pod
//
//  Library injector: duplicate an item
//  Duplicate an item based on a data source
//

import Foundation

/// An enum to specify the sort method on the duplicate injector options
public enum DuplicateInjectorOptionsSortType: String {
    
    case none = ""
    case date = "date"
    case string = "string"
    
}

/// An extra set of injector options for the duplicate injector to specify sorting and limitations on the source data
public class DuplicateInjectorOptions {
    
    public var sortItemPath: InjectorPath?
    public var sortType: DuplicateInjectorOptionsSortType = .none
    public var sortDescending: Bool = false
    public var limit: Int?
    public var limitFromEnd: Bool = false

}

/// An injector duplicating an item depending on the given data source(s)
open class DuplicateInjector: BaseInjector {
    
    // ---
    // MARK: Members
    // ---

    public var subInjectors = [BaseInjector]()
    public var targetItemPath: InjectorPath?
    public var betweenItemPath: InjectorPath?
    public var emptyItemPath: InjectorPath?
    public var sourceDataPath: InjectorPath?
    public var count: Int?
    public var sortItemPath: InjectorPath?
    public var sortType = DuplicateInjectorOptionsSortType.none
    public var sortDescending = false
    public var limit: Int?
    public var limitFromEnd = false

    
    // ---
    // MARK: Initialization
    // ---
    
    public override init() {
    }

    
    // ---
    // MARK: Manual injection
    // ---

    public static func duplicate(targetData: Any?, targetItemPath: InjectorPath? = nil, betweenItemPath: InjectorPath? = nil, emptyItemPath: InjectorPath? = nil, count: Int, duplicateCallback: ((_ targetItem: Any?, _ sourceItem: Any?) -> InjectorResult)? = nil) -> InjectorResult {
        return InjectorResult(withModifiedObject: targetData)
    }

    public static func duplicate(targetData: Any?, targetItemPath: InjectorPath? = nil, betweenItemPath: InjectorPath? = nil, emptyItemPath: InjectorPath? = nil, sourceData: Any?, sourceDataPath: InjectorPath? = nil, options: DuplicateInjectorOptions? = nil, duplicateCallback: ((_ targetItem: Any?, _ sourceItem: Any?) -> InjectorResult)? = nil) -> InjectorResult {
        return InjectorResult(withModifiedObject: targetData)
    }

    
    // ---
    // MARK: General injection
    // ---

    override open func appliedInjection(targetData: Any?, sourceData: Any? = nil) -> InjectorResult {
        if sourceData == nil {
            return DuplicateInjector.duplicate(targetData: targetData, targetItemPath: targetItemPath, betweenItemPath: betweenItemPath, emptyItemPath: emptyItemPath, count: count ?? 0)
        }
        let options = DuplicateInjectorOptions()
        options.sortItemPath = sortItemPath
        options.sortType = sortType
        options.sortDescending = sortDescending
        options.limit = limit
        options.limitFromEnd = limitFromEnd
        return DuplicateInjector.duplicate(targetData: targetData, targetItemPath: targetItemPath, betweenItemPath: betweenItemPath, emptyItemPath: emptyItemPath, sourceData: sourceData, sourceDataPath: sourceDataPath, options: options, duplicateCallback: { targetItem, sourceItem in
            var modifiedTargetItem = targetItem
            for subInjector in self.subInjectors {
                let result = subInjector.appliedInjection(targetData: modifiedTargetItem, sourceData: sourceItem)
                if result.hasError() {
                    return result
                }
                modifiedTargetItem = result.modifiedObject
            }
            return InjectorResult(withModifiedObject: modifiedTargetItem)
        })
    }

}
