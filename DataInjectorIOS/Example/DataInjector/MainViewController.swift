//
//  MainViewController.swift
//  DataInjector example
//
//  The main view controller showing a list of customers in the example
//

import UIKit
import DataInjector

class MainViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {

    // --
    // MARK: Outlets
    // --
    
    @IBOutlet private weak var tableView: UITableView!

    
    // --
    // MARK: Members
    // --
    
    private var dependenciesOpen = false
    

    // --
    // MARK: Lifecycle
    // --

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.delegate = self
        tableView.dataSource = self
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
            let dependenciesLeft = InjectorDependencyManager.shared.getUnresolvedDependencies(checkDependencies: ["customers"])
            if dependenciesLeft.count == 0 {
                dependenciesOpen = false
                tableView.reloadData()
            }
        }
    }
    

    // --
    // MARK: UITableViewDelegate
    // --
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
    }
    

    // --
    // MARK: UITableViewDataSource
    // --

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if let customerData = InjectorDependencyManager.shared.getDependency(name: "customers")?.obtainInjectableData() as? [Any] {
            return customerData.count
        }
        return 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let customerData = InjectorDependencyManager.shared.getDependency(name: "customers")?.obtainInjectableData() as? [[String: Any]] {
            if let cell = tableView.dequeueReusableCell(withIdentifier: "SimpleCell") as? SimpleCell {
                cell.identifier = InjectorConv.toString(from: customerData[indexPath.row]["id"])
                cell.label = InjectorConv.toString(from: customerData[indexPath.row]["fullName"])
                return cell
            }
        }
        return UITableViewCell()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "customerDetail" {
            if let detailViewController = segue.destination as? DetailViewController {
                detailViewController.customerId = (sender as? SimpleCell)?.identifier
            }
        }
    }

}

