//
//  SimpleCell.swift
//  DataInjector example
//
//  Table view cell: simple with only a label
//

import UIKit
import DataInjector

class SimpleCell: UITableViewCell {

    // --
    // MARK: Outlets
    // --
    
    @IBOutlet private weak var labelView: UILabel!

    
    // --
    // MARK: Storage
    // --
    
    var identifier: String?
    

    // --
    // MARK: Change content
    // --
    
    var label: String? {
        set {
            labelView.text = newValue
        }
        get { return labelView.text }
    }

}
