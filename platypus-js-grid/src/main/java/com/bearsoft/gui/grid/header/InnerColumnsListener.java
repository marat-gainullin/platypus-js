/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.header;

import java.awt.Dimension;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;

/**
 *
 * @author mg
 */
public class InnerColumnsListener implements TableColumnModelListener {

    protected MultiLevelHeader header;
    protected boolean eventsEnabled = true;

    public InnerColumnsListener(MultiLevelHeader aHeader) {
        super();
        header = aHeader;
    }

    @Override
    public void columnAdded(TableColumnModelEvent e) {
        if (eventsEnabled) {
            header.regenerate();
        }
    }

    @Override
    public void columnRemoved(TableColumnModelEvent e) {
        if (eventsEnabled) {
            header.regenerate();
        }
    }

    @Override
    public void columnMoved(TableColumnModelEvent e) {
        if (eventsEnabled) {
            header.regenerate();
        }
    }

    @Override
    public void columnMarginChanged(ChangeEvent e) {
        ajustHeaderSize();
    }

    private void ajustHeaderSize() {
        header.setSize(header.getPreferredSize());
        header.validate();
        Dimension s = header.getSize();
        Dimension ps = header.getPreferredSize();
        header.setSize(s.width, ps.height);
        header.validate();
    }

    @Override
    public void columnSelectionChanged(ListSelectionEvent e) {
    }

    public boolean isEventsEnabled() {
        return eventsEnabled;
    }

    public void setEventsEnabled(boolean aValue) {
        eventsEnabled = aValue;
    }
}
