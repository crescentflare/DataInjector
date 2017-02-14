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
    // MARK: Constants
    // --
    
    private static let dependencies = ["customers", "products"]

    
    // --
    // MARK: Outlets
    // --
    
    @IBOutlet var tableView: UITableView!

    
    // --
    // MARK: Members
    // --
    
    var customerId: String?
    private var dependenciesOpen = false
    private var showCustomerProducts: [[String: Any]]?
    

    // --
    // MARK: Lifecycle
    // --

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.dataSource = self
        tableView.estimatedRowHeight = tableView.rowHeight
        tableView.rowHeight = UITableViewAutomaticDimension
        dependenciesOpen = InjectorDependencyManager.shared.getUnresolvedDependencies(checkDependencies: DetailViewController.dependencies).count > 0
        if !dependenciesOpen {
            refreshDisplayedData()
        }
    }
    
    override func viewDidAppear(_ animated: Bool) {
        InjectorDependencyManager.shared.addDataObserver(self, selector: #selector(dependenciesDidUpdate), name: InjectorDependencyManager.dependencyResolved)
        if dependenciesOpen {
            let dependenciesLeft = InjectorDependencyManager.shared.getUnresolvedDependencies(checkDependencies: DetailViewController.dependencies)
            if dependenciesLeft.count > 0 {
                for dependency in dependenciesLeft {
                    InjectorDependencyManager.shared.resolveDependency(dependency: dependency)
                }
            } else {
                dependenciesOpen = false
                refreshDisplayedData()
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
            let dependenciesLeft = InjectorDependencyManager.shared.getUnresolvedDependencies(checkDependencies: DetailViewController.dependencies)
            if dependenciesLeft.count == 0 {
                dependenciesOpen = false
                refreshDisplayedData()
            }
        }
    }
    
    func refreshDisplayedData() {
        // Reset data set to display
        showCustomerProducts = nil
        
        // Look up dependency data
        let customers = InjectorDependencyManager.shared.getDependency(name: "customers")?.obtainInjectableData() as? [[String: Any]]
        let products = InjectorDependencyManager.shared.getDependency(name: "products")?.obtainInjectableData() as? [[String: Any]]
        
        // Find the products of the given customer id
        let customer = LinkDataInjector.findDataItem(onDataArray: customers ?? [], forValue: customerId, usingKey: "id")
        let customerProducts = customer?["products"] as? [[String: Any]]
        
        // If everything is there, link the product details to the customer product list
        showCustomerProducts = LinkDataInjector.linkedDataArray(onData: customerProducts ?? [], with: products ?? [], linkBy: "id") as? [[String: Any]]
        tableView.reloadData()
    }
    

    // --
    // MARK: UITableViewDataSource
    // --

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return showCustomerProducts?.count ?? 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let productData = showCustomerProducts {
            if let cell = tableView.dequeueReusableCell(withIdentifier: "DetailCell") as? DetailCell {
                cell.title = InjectorConv.toString(from: productData[indexPath.row]["name"])
                cell.info = InjectorConv.toString(from: productData[indexPath.row]["description"])
                cell.value = InjectorConv.toString(from: productData[indexPath.row]["price"])
                cell.valueColor = (InjectorConv.toBool(from: productData[indexPath.row]["paid"]) ?? false) ? UIColor.black : UIColor.red
                return cell
            }
        }
        return UITableViewCell()
    }

}
