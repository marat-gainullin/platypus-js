/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.designer.application.utils;

import javax.swing.ComboBoxModel;
import org.netbeans.api.db.explorer.DatabaseConnection;

/**
 *
 * @author mg
 */
public class DatabaseConnectionComboBoxModel extends DatabaseConnectionListModel implements ComboBoxModel<DatabaseConnection>{

    protected DatabaseConnection selected;
    
    @Override
    public void setSelectedItem(Object anItem) {
        selected = (DatabaseConnection)anItem;
    }

    @Override
    public Object getSelectedItem() {
        return selected;
    }
    
}
