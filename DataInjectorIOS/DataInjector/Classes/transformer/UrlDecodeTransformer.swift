//
//  UrlDecodeTransformer.swift
//  DataInjector Pod
//
//  Library transformer: URL decode a string
//  Convert data to a URL decoded string
//

import Foundation

/// A transformer to URL decode a string
open class UrlDecodeTransformer: BaseTransformer {
    
    // --
    // MARK: Members
    // --
    
    public var sourceDataPath: InjectorPath?

    
    // --
    // MARK: Initialization
    // --
    
    public override init() {
    }

    
    // --
    // MARK: Manual transformation
    // --
    
    public static func decode(fromData: Any?) -> InjectorResult {
        if let dataString = InjectorConv.asString(value: fromData) {
            let result = dataString.replacingOccurrences(of: "+", with: " ")
            if let decodedString = result.removingPercentEncoding {
                return InjectorResult(withModifiedObject: decodedString)
            }
        }
        return InjectorResult(withError: .sourceInvalid)
    }


    // --
    // MARK: General injection
    // --
    
    open override func appliedTransformation(sourceData: Any?) -> InjectorResult {
        let useSourceData = DataInjector.get(from: sourceData, path: sourceDataPath ?? InjectorPath(path: ""))
        return UrlDecodeTransformer.decode(fromData: useSourceData)
    }

}
