import UIKit
import XCTest
@testable import DataInjector

class InjectorMapperTests: XCTestCase {
    
    func testObtainMapping() {
        // Test simple mappings
        XCTAssertEqual(100, InjectorMapper(mapping: "[ first -> 80 , second -> 100 , third -> 23 ]").obtainMapping(item: "second") as? Int)
        XCTAssertEqual(true, InjectorMapper(mapping: "['on'->true,'off'->false]").obtainMapping(item: "on") as? Bool)
        XCTAssertEqual("fork", InjectorMapper(mapping: "[1->'spoon',2->'fork',3->'knife']").obtainMapping(item: 2) as? String)
        XCTAssertEqual("other", InjectorMapper(mapping: "[0->zero,1->one,2->two,else->other]").obtainMapping(item: "fallback") as? String)

        // Test mappings with references
        let subDictionary: [String: Any] = [
            "twelve": 12,
            "thirteen": 13,
            "fourteen": 14
        ]
        let dictionary: [String: Any] = [
            "four": 4,
            "five": 5,
            "six": 6,
            "subDictionary": subDictionary
        ]
        XCTAssertEqual("dog", InjectorMapper(mapping: "[4->cat,5->dog,6->sheep,12->cow,13->goat,14->hamster,else->invalid]", fullRefData: dictionary).obtainMapping(item: "@five") as? String)
        XCTAssertEqual("cow", InjectorMapper(mapping: "[4->cat,5->dog,6->sheep,12->cow,13->goat,14->hamster,else->invalid]", fullRefData: dictionary).obtainMapping(item: "@subDictionary.twelve") as? String)
        XCTAssertEqual("hamster", InjectorMapper(mapping: "[4->cat,5->dog,6->sheep,12->cow,13->goat,14->hamster,else->invalid]", fullRefData: dictionary, subRefData: subDictionary).obtainMapping(item: "@.fourteen") as? String)
    }

}
