import UIKit
import XCTest
@testable import DataInjector

class InjectorConvTests: XCTestCase {
    
    // ---
    // MARK: Test parse conversion
    // ---

    func testAsDate() {
        XCTAssertEqual(dateFrom(year: 2016, month: 8, day: 19), InjectorConv.toDate(from: "2016-08-19"))
        XCTAssertEqual(dateFrom(year: 2016, month: 5, day: 16, hour: 1, minute: 10, second: 28), InjectorConv.toDate(from: "2016-05-16T01:10:28"))
        XCTAssertEqual(utcDateFrom(year: 2016, month: 2, day: 27, hour: 12, minute: 24, second: 11), InjectorConv.toDate(from: "2016-02-27T12:24:11Z"))
        XCTAssertEqual(utcDateFrom(year: 2016, month: 2, day: 27, hour: 17, minute: 0, second: 0), InjectorConv.toDate(from: "2016-02-27T19:00:00+02:00"))
    }
    
    
    // ---
    // MARK: Test primitive type conversion
    // ---
    
    func testAsString() {
        XCTAssertEqual("test", InjectorConv.toString(from: "test"))
        XCTAssertEqual("12", InjectorConv.toString(from: 12))
        XCTAssertEqual("14.42", InjectorConv.toString(from: 14.42))
        XCTAssertEqual("true", InjectorConv.toString(from: true))
    }
    
    func testAsDouble() {
        XCTAssertEqual(89.213, InjectorConv.toDouble(from: "89.213"))
        XCTAssertEqual(31, InjectorConv.toDouble(from: 31))
    }
    
    func testAsFloat() {
        XCTAssertEqual(21.3, InjectorConv.toFloat(from: 21.3))
        XCTAssertEqual(1, InjectorConv.toFloat(from: true))
    }
    
    func testAsInt() {
        XCTAssertEqual(3, InjectorConv.toInt(from: "3"))
        XCTAssertEqual(45, InjectorConv.toInt(from: 45.75))
    }
    
    func testAsBool() {
        XCTAssertEqual(false, InjectorConv.toBool(from: "false"))
        XCTAssertEqual(true, InjectorConv.toBool(from: 2))
    }
    

    // ---
    // MARK: Helper
    // ---
    
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
