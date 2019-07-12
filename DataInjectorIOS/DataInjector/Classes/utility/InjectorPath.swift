//
//  InjectorPath.swift
//  DataInjector Pod
//
//  Library utility: a target for data injection
//  Use a path to point to a nested data item to be read or modified
//

import Foundation

/// A path to easily manage or determine a location to inject or fetch data
public class InjectorPath {
    
    // --
    // MARK: Members
    // --

    private var pathComponents = [String]()
    

    // --
    // MARK: Initialization
    // --

    public init() {
        // No implementation
    }
    
    public init(path: String, separator: Character = ".") {
        if !path.isEmpty {
            pathComponents = path.split(separator: separator).map(String.init)
        }
    }
    
    public init(pathComponents: [String]) {
        self.pathComponents = pathComponents
    }
    

    // --
    // MARK: Access elements
    // --

    public func firstElement() -> String? {
        return pathComponents.first
    }
    
    public func nextElement() -> String? {
        return pathComponents.count > 1 ? pathComponents[1] : nil
    }
    
    public func hasElements() -> Bool {
        return pathComponents.count > 0
    }
    
    public func hasNextElement() -> Bool {
        return pathComponents.count > 1
    }
    

    // --
    // MARK: Traversal
    // --

    public func deeperPath() -> InjectorPath {
        if pathComponents.count > 0 {
            return InjectorPath(pathComponents: Array(pathComponents.dropFirst()))
        }
        return InjectorPath()
    }
    
    public class func seekPathForMap(data: Any?, markerKey: String, value: String) -> InjectorPath? {
        if let dataDict = data as? [String: Any?] {
            if let testString = dataDict[markerKey] as? String {
                if testString == value {
                    return InjectorPath()
                }
            }
            for (key, dictValue) in dataDict {
                if let result = seekPathForMap(data: dictValue, markerKey: markerKey, value: value) {
                    return InjectorPath(path: key + "." + result.asStringPath())
                }
            }
        } else if let dataDict = data as? [String: Any] {
            if let testString = dataDict[markerKey] as? String {
                if testString == value {
                    return InjectorPath()
                }
            }
            for (key, dictValue) in dataDict {
                if let result = seekPathForMap(data: dictValue, markerKey: markerKey, value: value) {
                    return InjectorPath(path: key + "." + result.asStringPath())
                }
            }
        } else if let dataArray = data as? [Any?] {
            for i in dataArray.indices {
                if let result = seekPathForMap(data: dataArray[i], markerKey: markerKey, value: value) {
                    return InjectorPath(path: "\(i)." + result.asStringPath())
                }
            }
        } else if let dataArray = data as? [Any] {
            for i in dataArray.indices {
                if let result = seekPathForMap(data: dataArray[i], markerKey: markerKey, value: value) {
                    return InjectorPath(path: "\(i)." + result.asStringPath())
                }
            }
        }
        return nil
    }


    // --
    // MARK: Conversion
    // --
    
    public func asStringPath(separator: Character = ".") -> String {
        return pathComponents.joined(separator: "\(separator)")
    }

}
