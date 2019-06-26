//
//  MainViewController.swift
//  DataInjector example
//
//  The main view controller showing a list of customers in the example
//

import UIKit
import DataInjector
import BitletSynchronizer

class MainViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {

    // --
    // MARK: Constants
    // --
    
    private let dependencies = [Bitlets.customers]
    
    
    // --
    // MARK: Outlets
    // --
    
    @IBOutlet private weak var tableView: UITableView!

    
    // --
    // MARK: Lifecycle
    // --

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.delegate = self
        tableView.dataSource = self
    }
    
    override func viewDidAppear(_ animated: Bool) {
        let checkCaches = dependencies.map { $0.cacheKey }
        if !BitletSynchronizer.shared.anyCacheInState(.loadingOrRefreshing, forKeys: checkCaches) && !BitletSynchronizer.shared.anyCacheInState(.unavailable, forKeys: checkCaches) {
            tableView.reloadData()
        }
        loadData(forced: false)
    }
    
    
    // --
    // MARK: Data loading
    // --

    private func loadData(forced: Bool) {
        let checkCaches = dependencies.map { $0.cacheKey }
        for dependency in dependencies {
            BitletSynchronizer.shared.loadBitlet(dependency, cacheKey: dependency.cacheKey, forced: forced, completion: { data, error in
                if !BitletSynchronizer.shared.anyCacheInState(.loadingOrRefreshing, forKeys: checkCaches) {
                    self.tableView.reloadData()
                }
            })
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
        if let customerData = BitletSynchronizer.shared.cachedBitlet(forKey: Bitlets.customers.cacheKey) as? JsonArray {
            return customerData.itemList.count
        }
        return 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let customerData = BitletSynchronizer.shared.cachedBitlet(forKey: Bitlets.customers.cacheKey) as? JsonArray {
            if let customerList = customerData.itemList as? [[String: Any]] {
                if let cell = tableView.dequeueReusableCell(withIdentifier: "SimpleCell") as? SimpleCell {
                    cell.identifier = InjectorConv.toString(from: customerList[indexPath.row]["id"])
                    cell.label = InjectorConv.toString(from: customerList[indexPath.row]["fullName"])
                    return cell
                }
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
