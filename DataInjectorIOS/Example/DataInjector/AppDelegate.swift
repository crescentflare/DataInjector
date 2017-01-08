//
//  AppDelegate.swift
//  DataInjector example
//
//  The application delegate, handling global events while the app is running
//

import UIKit
import DataInjector

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    // --
    // MARK: Window member (used to contain the navigation controller)
    // --

    var window: UIWindow?


    // --
    // MARK: Lifecycle callbacks
    // --

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {
        InjectorDependencyManager.shared.addDependency(name: "customers", dependency: MockDependency(filename: "customer_list"))
        InjectorDependencyManager.shared.addDependency(name: "products", dependency: MockDependency(filename: "product_list"))
        return true
    }

    func applicationWillResignActive(_ application: UIApplication) {
        // No implementation
    }

    func applicationDidEnterBackground(_ application: UIApplication) {
        // No implementation
    }

    func applicationWillEnterForeground(_ application: UIApplication) {
        // No implementation
    }

    func applicationDidBecomeActive(_ application: UIApplication) {
        // No implementation
    }

    func applicationWillTerminate(_ application: UIApplication) {
        // No implementation
    }

}
