//
//  DetailViewController.swift
//  DataInjector example
//
//  The detail view controller showing a list of products for a given customer in the example
//

import UIKit
import DataInjector

class DetailViewController: UIViewController, UITableViewDataSource {

    // --
    // MARK: Outlets
    // --
    
    @IBOutlet var tableView: UITableView!

    
    // --
    // MARK: Members
    // --
    
    var customerId: String?
    private var dependenciesOpen = false
    

    // --
    // MARK: Lifecycle
    // --

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.dataSource = self
        tableView.estimatedRowHeight = tableView.rowHeight
        tableView.rowHeight = UITableViewAutomaticDimension
        dependenciesOpen = InjectorDependencyManager.shared.getUnresolvedDependencies(checkDependencies: ["customers", "products"]).count > 0
    }
    
    override func viewDidAppear(_ animated: Bool) {
        InjectorDependencyManager.shared.addDataObserver(self, selector: #selector(dependenciesDidUpdate), name: InjectorDependencyManager.dependencyResolved)
        if dependenciesOpen {
            let dependenciesLeft = InjectorDependencyManager.shared.getUnresolvedDependencies(checkDependencies: ["customers", "products"])
            if dependenciesLeft.count > 0 {
                for dependency in dependenciesLeft {
                    InjectorDependencyManager.shared.resolveDependency(dependency: dependency)
                }
            } else {
                dependenciesOpen = false
                tableView.reloadData()
            }
        }
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        InjectorDependencyManager.shared.removeDataObserver(self, name: InjectorDependencyManager.dependencyResolved)
    }

    
    // --
    // MARK: Dependency handling
    // --

    func dependenciesDidUpdate() {
        if dependenciesOpen {
            let dependenciesLeft = InjectorDependencyManager.shared.getUnresolvedDependencies(checkDependencies: ["customers", "products"])
            if dependenciesLeft.count == 0 {
                dependenciesOpen = false
                tableView.reloadData()
            }
        }
    }
    

    // --
    // MARK: UITableViewDataSource
    // --

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if let productData = InjectorDependencyManager.shared.getDependency(name: "products")?.obtainInjectableData() as? [Any] {
            return productData.count
        }
        return 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let productData = InjectorDependencyManager.shared.getDependency(name: "products")?.obtainInjectableData() as? [[String: Any]] {
            if let cell = tableView.dequeueReusableCell(withIdentifier: "DetailCell") as? DetailCell {
                cell.title = InjectorConv.toString(from: productData[indexPath.row]["name"])
                cell.info = InjectorConv.toString(from: productData[indexPath.row]["description"])
                cell.value = InjectorConv.toString(from: productData[indexPath.row]["price"])
                return cell
            }
        }
        return UITableViewCell()
    }

}

