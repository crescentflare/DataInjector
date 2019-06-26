//
//  DetailViewController.swift
//  DataInjector example
//
//  The detail view controller showing a list of products for a given customer in the example
//

import UIKit
import DataInjector
import BitletSynchronizer

class DetailViewController: UIViewController, UITableViewDataSource {

    // --
    // MARK: Constants
    // --
    
    private let dependencies = [Bitlets.customers, Bitlets.products]

    
    // --
    // MARK: Outlets
    // --
    
    @IBOutlet var tableView: UITableView!

    
    // --
    // MARK: Members
    // --
    
    var customerId: String?
    private var showCustomerProducts: [[String: Any]]?
    

    // --
    // MARK: Lifecycle
    // --

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.dataSource = self
        tableView.estimatedRowHeight = tableView.rowHeight
        tableView.rowHeight = UITableView.automaticDimension
    }
    
    override func viewDidAppear(_ animated: Bool) {
        let checkCaches = dependencies.map { $0.cacheKey }
        if !BitletSynchronizer.shared.anyCacheInState(.loadingOrRefreshing, forKeys: checkCaches) && !BitletSynchronizer.shared.anyCacheInState(.unavailable, forKeys: checkCaches) {
            refreshDisplayedData()
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
                    self.refreshDisplayedData()
                }
            })
        }
    }

    func refreshDisplayedData() {
        // Reset data set to display
        showCustomerProducts = nil
        
        // Look up dependency data
        let customers = (BitletSynchronizer.shared.cachedBitlet(forKey: Bitlets.customers.cacheKey) as? JsonArray)?.itemList as? [[String: Any]]
        let products = (BitletSynchronizer.shared.cachedBitlet(forKey: Bitlets.products.cacheKey) as? JsonArray)?.itemList as? [[String: Any]]
        
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
