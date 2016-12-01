import UIKit
import XCTest
@testable import DataInjector

class InjectorConditionTests: XCTestCase {
    
    func testIsMet() {
        // Test value interpretation
        XCTAssertTrue(InjectorCondition(condition: "true").isMet())
        XCTAssertFalse(InjectorCondition(condition: "false").isMet())
        XCTAssertTrue(InjectorCondition(condition: "20").isMet())
        XCTAssertFalse(InjectorCondition(condition: "0").isMet())
        
        // Test simple comparisons
        XCTAssertTrue(InjectorCondition(condition: "'string' == 'string'").isMet())
        XCTAssertFalse(InjectorCondition(condition: "apple == pear").isMet())
        XCTAssertTrue(InjectorCondition(condition: "apple != pear").isMet())
        XCTAssertTrue(InjectorCondition(condition: "'20.5' > 10").isMet())
        XCTAssertTrue(InjectorCondition(condition: "16 < 215").isMet())
        XCTAssertTrue(InjectorCondition(condition: "14 >= '14'").isMet())
        
        // Test conditions with operators
        XCTAssertTrue(InjectorCondition(condition: "true != 'false' && 10 > 9").isMet())
        XCTAssertFalse(InjectorCondition(condition: "true == false || 4 <= 3").isMet())
        XCTAssertTrue(InjectorCondition(condition: "4 >= 4 && 'string' == string && 9 < 13 && true").isMet())

        // Test conditions with references
        let subDictionary: [String: Any] = [
            "one": 1,
            "two": 2,
            "three": 3,
            "four": 4
        ]
        let dictionary: [String: Any] = [
            "first": 1,
            "second": 2,
            "third": 3,
            "fourth": 4,
            "subDictionary": subDictionary
        ]
        XCTAssertTrue(InjectorCondition(condition: "@first < @.three && @subDictionary.four == @fourth", fullRefData: dictionary, subRefData: subDictionary).isMet())
    }

}
