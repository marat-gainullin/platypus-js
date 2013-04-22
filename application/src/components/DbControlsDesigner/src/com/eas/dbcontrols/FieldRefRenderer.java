/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols;

import com.eas.client.datamodel.gui.view.FieldsListCellRenderer;
import java.awt.Dimension;

/**
 *
 * @author mg
 */
public class FieldRefRenderer extends FieldsListCellRenderer {

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(50, d.height);
    }

    @Override
    public Dimension getMinimumSize() {
        Dimension d = super.getMinimumSize();
        return new Dimension(50, d.height);
    }
}
