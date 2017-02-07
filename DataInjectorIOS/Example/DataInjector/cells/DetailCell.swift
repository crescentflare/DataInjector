//
//  DetailCell.swift
//  DataInjector example
//
//  Table view cell: detailed with a title, info and a value (which can be colorized)
//

import UIKit
import DataInjector

class DetailCell: UITableViewCell {

    // --
    // MARK: Outlets
    // --
    
    @IBOutlet private weak var titleView: UILabel!
    @IBOutlet private weak var infoView: UILabel!
    @IBOutlet private weak var valueView: UILabel!

    
    // --
    // MARK: Change content
    // --
    
    var title: String? {
        set {
            titleView.text = newValue
        }
        get { return titleView.text }
    }

    var info: String? {
        set {
            infoView.text = newValue
        }
        get { return infoView.text }
    }

    var value: String? {
        set {
            valueView.text = newValue
        }
        get { return valueView.text }
    }
    
    var valueColor: UIColor {
        set {
            valueView.textColor = newValue
        }
        get { return valueView.textColor }
    }

}
