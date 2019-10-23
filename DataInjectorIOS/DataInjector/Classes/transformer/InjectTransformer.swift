//
//  UrlEncodeTransformer.swift
//  DataInjector Pod
//
//  Library transformer: inject data
//  Inject values into the source data
//

import Foundation

/// A transformer to inject values into the source data
open class InjectTransformer: BaseTransformer {
    
    // --
    // MARK: Members
    // --
    
    public var sourceDataPath: InjectorPath?
    public var injectors = [BaseInjector]()

    
    // --
    // MARK: Initialization
    // --
    
    public override init() {
    }

    
    // --
    // MARK: Manual transformation
    // --
    
    public static func inject(intoData: Any?, injectors: [BaseInjector]) -> InjectorResult {
        var result = intoData
        for injector in injectors {
            let injectorResult = injector.appliedInjection(targetData: result, sourceData: result)
            if injectorResult.hasError() {
                return injectorResult
            }
            result = injectorResult.modifiedObject
        }
        return InjectorResult(withModifiedObject: result)
    }


    // --
    // MARK: General injection
    // --
    
    open override func appliedTransformation(sourceData: Any?) -> InjectorResult {
        let useSourceData = DataInjector.get(from: sourceData, path: sourceDataPath ?? InjectorPath(path: ""))
        return InjectTransformer.inject(intoData: useSourceData, injectors: injectors)
    }

}
