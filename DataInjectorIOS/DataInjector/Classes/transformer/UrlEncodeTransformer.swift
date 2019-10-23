//
//  UrlEncodeTransformer.swift
//  DataInjector Pod
//
//  Library transformer: URL encode a string
//  Convert data to a URL encoded string
//

import Foundation

/// A transformer to URL encode a string
open class UrlEncodeTransformer: BaseTransformer {
    
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
    
    public static func encode(fromData: Any?) -> InjectorResult {
        if let dataString = InjectorConv.asString(value: fromData) {
            let characters = (CharacterSet.urlQueryAllowed as NSCharacterSet).mutableCopy() as! NSMutableCharacterSet
            characters.removeCharacters(in: "!*'();:@&=+$,/?%#[]")
            characters.addCharacters(in: " ")
            guard let encodedString = dataString.addingPercentEncoding(withAllowedCharacters: characters as CharacterSet) else {
                return InjectorResult(withModifiedObject: dataString)
            }
            return InjectorResult(withModifiedObject: encodedString.replacingOccurrences(of: " ", with: "+"))
        }
        return InjectorResult(withError: .sourceInvalid)
    }


    // --
    // MARK: General injection
    // --
    
    open override func appliedTransformation(sourceData: Any?) -> InjectorResult {
        let useSourceData = DataInjector.get(from: sourceData, path: sourceDataPath ?? InjectorPath(path: ""))
        return UrlEncodeTransformer.encode(fromData: useSourceData)
    }

}
