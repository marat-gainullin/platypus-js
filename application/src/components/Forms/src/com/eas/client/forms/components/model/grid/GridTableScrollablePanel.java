/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model.grid;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.Scrollable;

/**
 *
 * @author mg
 */
public class GridTableScrollablePanel extends JPanel implements Scrollable {

    protected GridTable table;

    public GridTableScrollablePanel(GridTable aTable) {
        super(new BorderLayout());
        table = aTable;
        add(table, BorderLayout.CENTER);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return table.getScrollableUnitIncrement(visibleRect, orientation, direction);
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return table.getScrollableBlockIncrement(visibleRect, orientation, direction);
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return getParent().getWidth() >= getPreferredSize().width;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return getParent().getHeight() >= getPreferredSize().height;
    }
}
