//
//  DuplicateInjector.swift
//  DataInjector Pod
//
//  Library injector: duplicate an item
//  Duplicate an item based on one or more data sources
//

import Foundation

/// An enum to specify the sort method on the duplicate injector options
public enum DuplicateInjectorOptionsSortType: String {
    
    case none = ""
    case date = "date"
    case string = "string"
    
}

/// An extra set of injector options for the duplicate injector to specify sorting and limitations on the data source
public class DuplicateInjectorOptions {
    
    public var sortItemPath: String?
    public var sortType: DuplicateInjectorOptionsSortType = .none
    public var sortDescending: Bool = false
    public var limitSourceItems: Int?
    public var limitFromEnd: Bool = false

}

/// An injector duplicating an item depending on the given data source(s)
open class DuplicateInjector: BaseInjector {
    
    // ---
    // MARK: Members
    // ---

    public var linkKey: String?

    
    // ---
    // MARK: Initialization
    // ---
    
    public override init() {
    }

    
    // ---
    // MARK: Manual injection
    // ---

    public static func duplicatedItem(onData targetData: [String: Any], for dataSource: [Any], duplicateItemPath: String?, betweenItemPath: String? = nil, emptyItemPath: String? = nil, options: DuplicateInjectorOptions? = nil, createDuplicateCallback: ((Any) -> Any)? = nil) -> [String: Any] {
        return targetData
    }

    public static func duplicatedItem(onData targetData: [String: Any], for dataSources: [[Any]], duplicateItemPath: String?, betweenItemPath: String? = nil, emptyItemPath: String? = nil, options: DuplicateInjectorOptions? = nil, createDuplicateCallback: ((Any) -> Any)? = nil) -> [String: Any] {
        var mergedDataSources: [Any] = []
        for dataSource in dataSources {
            mergedDataSources.append(contentsOf: dataSource)
        }
        return duplicatedItem(onData: targetData, for: mergedDataSources, duplicateItemPath: duplicateItemPath, betweenItemPath: betweenItemPath, emptyItemPath: emptyItemPath, options: options, createDuplicateCallback: createDuplicateCallback)
    }

    
    // ---
    // MARK: General injection
    // ---

    override open func appliedInjection(targetData: Any, subTargetData: Any?, referencedData: Any? = nil, subReferencedData: Any? = nil) -> Any {
        return targetData
    }
    

    // ---
    // MARK: Dependencies
    // ---

    override open func foundDependencies() -> [InjectorDependency] {
        return []
    }

}
