//
//  Bitlets.swift
//  DataInjector example
//
//  Example helper: the list of bitlets used in the app
//

import Foundation

class Bitlets {

    static let customers = MockBitlet(filename: "customer_list", cacheKey: "customers")
    static let products = MockBitlet(filename: "product_list", cacheKey: "products")

}
