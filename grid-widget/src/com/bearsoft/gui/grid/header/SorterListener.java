/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.header;

import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;

/**
 *
 * @author Gala
 */
public class SorterListener implements RowSorterListener {

    protected MultiLevelHeader header;

    public SorterListener(MultiLevelHeader aHeader) {
        super();
        header = aHeader;
    }

    public void sorterChanged(RowSorterEvent e) {
        header.repaint();
    }
}
