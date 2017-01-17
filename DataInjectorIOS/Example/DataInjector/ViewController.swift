//
//  ViewController.swift
//  DataInjector example
//
//  The main view controller
//

import UIKit
import DataInjector

class ViewController: UIViewController {

    // --
    // MARK: Members
    // --
    
    private var dependenciesOpen = false
    

    // --
    // MARK: Lifecycle
    // --

    override func viewDidLoad() {
        super.viewDidLoad()
        dependenciesOpen = InjectorDependencyManager.shared.getUnresolvedDependencies(checkDependencies: ["customers"]).count > 0
    }
    
    override func viewDidAppear(_ animated: Bool) {
        InjectorDependencyManager.shared.addDataObserver(self, selector: #selector(dependenciesDidUpdate), name: InjectorDependencyManager.dependencyResolved)
        if dependenciesOpen {
            let dependenciesLeft = InjectorDependencyManager.shared.getUnresolvedDependencies(checkDependencies: ["customers"])
            if dependenciesLeft.count > 0 {
                for dependency in dependenciesLeft {
                    InjectorDependencyManager.shared.resolveDependency(dependency: dependency)
                }
            } else {
                updateViews()
            }
        }
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        InjectorDependencyManager.shared.removeDataObserver(self, name: InjectorDependencyManager.dependencyResolved)
    }

    
    // --
    // MARK: View creation
    // --
    
    func updateViews() {
        // Nothing yet...
    }
    

    // --
    // MARK: Dependency handling
    // --

    func dependenciesDidUpdate() {
        if dependenciesOpen {
            let dependenciesLeft = InjectorDependencyManager.shared.getUnresolvedDependencies(checkDependencies: ["customers"])
            if dependenciesLeft.count == 0 {
                updateViews()
                dependenciesOpen = false
            }
        }
    }
    
}

