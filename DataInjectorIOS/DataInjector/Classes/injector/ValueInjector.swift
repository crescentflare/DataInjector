//
//  ValueInjector.swift
//  DataInjector Pod
//
//  Library injector: set a value
//  A simple injector to set a value manually, or based on a data source
//

import Foundation

/// A simple injector inserting a value manually or from a data source
open class ValueInjector: BaseInjector {
    
    // --
    // MARK: Members
    // --

    public var sourceTransformers = [BaseTransformer]()
    public var targetItemPath: InjectorPath?
    public var sourceDataPath: InjectorPath?
    public var value: Any?
    public var allowNil = true

    
    // --
    // MARK: Initialization
    // --
    
    public override init() {
    }

    
    // --
    // MARK: Manual injection
    // --
    
    public static func setValue(inData: Any?, path: InjectorPath, value: Any?, allowNil: Bool = true) -> InjectorResult {
        if !allowNil && value == nil {
            return InjectorResult(withError: .nilNotAllowed)
        }
        return DataInjector.inject(into: inData, path: path, modifyCallback: { originalData in
            return InjectorResult(withModifiedObject: value)
        })
    }

    
    // --
    // MARK: General injection
    // --

    override open func appliedInjection(targetData: Any?, sourceData: Any? = nil) -> InjectorResult {
        // Use manual value when specified
        if let value = value {
            return ValueInjector.setValue(inData: targetData, path: targetItemPath ?? InjectorPath(path: ""), value: value, allowNil: allowNil)
        }
        
        // Prepare source data with optional transformation
        var useSourceData = DataInjector.get(from: sourceData, path: sourceDataPath ?? InjectorPath(path: ""))
        for transformer in sourceTransformers {
            let result = transformer.appliedTransformation(sourceData: useSourceData)
            if result.hasError() {
                return result
            }
            useSourceData = result.modifiedObject
        }

        // Set value based on source data
        return ValueInjector.setValue(inData: targetData, path: targetItemPath ?? InjectorPath(path: ""), value: useSourceData, allowNil: allowNil)
    }

}
