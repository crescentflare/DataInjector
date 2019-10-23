import UIKit
import XCTest
@testable import DataInjector

class InjectorConvTests: XCTestCase {
    
    // --
    // MARK: Test array conversion
    // --

    func testAsStringArray() {
        let mixedArray: [Any] = [
            "test",
            12,
            14.42,
            true
        ]
        let stringArray = InjectorConv.asStringArray(value: mixedArray)
        XCTAssertEqual("test", stringArray[0])
        XCTAssertEqual("12", stringArray[1])
        XCTAssertEqual("14.42", stringArray[2])
        XCTAssertEqual("true", stringArray[3])
    }
    
    func testAsDoubleArray() {
        let mixedArray: [Any] = [
            "89.213",
            31
        ]
        let doubleArray = InjectorConv.asDoubleArray(value: mixedArray)
        XCTAssertEqual(89.213, doubleArray[0])
        XCTAssertEqual(31, doubleArray[1])
    }
    
    func testAsFloatArray() {
        let mixedArray: [Any] = [
            21.3,
            true
        ]
        let floatArray = InjectorConv.asFloatArray(value: mixedArray)
        XCTAssertEqual(21.3, floatArray[0])
        XCTAssertEqual(1, floatArray[1])
    }
    
    func testAsIntArray() {
        let mixedArray: [Any] = [
            "3",
            45.75
        ]
        let intArray = InjectorConv.asIntArray(value: mixedArray)
        XCTAssertEqual(3, intArray[0])
        XCTAssertEqual(45, intArray[1])
    }
    
    func testAsBoolArray() {
        let mixedArray: [Any] = [
            "false",
            2
        ]
        let boolArray = InjectorConv.asBoolArray(value: mixedArray)
        XCTAssertEqual(false, boolArray[0])
        XCTAssertEqual(true, boolArray[1])
    }
    
    
    // --
    // MARK: Test date parse conversion
    // --
    
    func testAsDateArray() {
        let stringArray = [
            "2016-08-19",
            "2016-05-16T01:10:28",
            "2016-02-27T12:24:11Z",
            "2016-02-27T19:00:00+02:00"
        ]
        let dateArray = InjectorConv.asDateArray(value: stringArray)
        XCTAssertEqual(dateFrom(year: 2016, month: 8, day: 19), dateArray[0])
        XCTAssertEqual(dateFrom(year: 2016, month: 5, day: 16, hour: 1, minute: 10, second: 28), dateArray[1])
        XCTAssertEqual(utcDateFrom(year: 2016, month: 2, day: 27, hour: 12, minute: 24, second: 11), dateArray[2])
        XCTAssertEqual(utcDateFrom(year: 2016, month: 2, day: 27, hour: 17, minute: 0, second: 0), dateArray[3])
    }

    func testAsDate() {
        XCTAssertEqual(dateFrom(year: 2016, month: 8, day: 19), InjectorConv.asDate(value: "2016-08-19"))
        XCTAssertEqual(dateFrom(year: 2016, month: 5, day: 16, hour: 1, minute: 10, second: 28), InjectorConv.asDate(value: "2016-05-16T01:10:28"))
        XCTAssertEqual(utcDateFrom(year: 2016, month: 2, day: 27, hour: 12, minute: 24, second: 11), InjectorConv.asDate(value: "2016-02-27T12:24:11Z"))
        XCTAssertEqual(utcDateFrom(year: 2016, month: 2, day: 27, hour: 17, minute: 0, second: 0), InjectorConv.asDate(value: "2016-02-27T19:00:00+02:00"))
    }
    
    
    // --
    // MARK: Test primitive type conversion
    // --
    
    func testAsString() {
        XCTAssertEqual("test", InjectorConv.asString(value: "test"))
        XCTAssertEqual("12", InjectorConv.asString(value: 12))
        XCTAssertEqual("5", InjectorConv.asString(value: 5.0))
        XCTAssertEqual("14.42", InjectorConv.asString(value: 14.42))
        XCTAssertEqual("true", InjectorConv.asString(value: true))
    }
    
    func testAsDouble() {
        XCTAssertEqual(89.213, InjectorConv.asDouble(value: "89.213"))
        XCTAssertEqual(31, InjectorConv.asDouble(value: 31))
    }
    
    func testAsFloat() {
        XCTAssertEqual(21.3, InjectorConv.asFloat(value: 21.3))
        XCTAssertEqual(1, InjectorConv.asFloat(value: true))
    }
    
    func testAsInt() {
        XCTAssertEqual(3, InjectorConv.asInt(value: "3"))
        XCTAssertEqual(45, InjectorConv.asInt(value: 45.75))
    }
    
    func testAsBool() {
        XCTAssertEqual(false, InjectorConv.asBool(value: "false"))
        XCTAssertEqual(true, InjectorConv.asBool(value: 2))
    }
    

    // --
    // MARK: Helper
    // --
    
    func dateFrom(year: Int, month: Int, day: Int, hour: Int = 0, minute: Int = 0, second: Int = 0) -> Date {
        let calendar = Calendar(identifier: .gregorian)
        var components = DateComponents()
        components.year = year
        components.month = month
        components.day = day
        components.hour = hour
        components.minute = minute
        components.second = second
        return calendar.date(from: components)!
    }
    
    func utcDateFrom(year: Int, month: Int, day: Int, hour: Int = 0, minute: Int = 0, second: Int = 0) -> Date {
        var calendar = Calendar(identifier: .gregorian)
        var components = DateComponents()
        calendar.timeZone = TimeZone(identifier: "UTC")!
        components.year = year
        components.month = month
        components.day = day
        components.hour = hour
        components.minute = minute
        components.second = second
        return calendar.date(from: components)!
    }

}
