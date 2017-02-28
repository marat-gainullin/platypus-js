/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.rendering;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Renderer with capability of transparently adding some artifacts to the left or to the right of the main renderer.
 * This renderer delegates all significant work to the delegate. It renders inset components by itself.
 * @author Gala
 */
public class InsettedRenderer extends JPanel implements TableCellRenderer {

    protected TableCellRenderer delegate;
    protected Component leadingComponent;
    protected Component trailingComponent;

    /**
     * Creates a new instance of InsettedRenderer.
     * @param aDelegate TableCellRenderer instance we have delegate all significant work to.
     * @see InsettedRenderer
     * @see TableCellRenderer
     */
    public InsettedRenderer(TableCellRenderer aDelegate) {
        super();
        setLayout(new BorderLayout());
        delegate = aDelegate;
    }

    /**
     * Creates a new instance of InsettedRenderer.
     * @param aDelegate TableCellRenderer instance we have delegate all significant work to.
     * @param aLeadingComponent Component that will be rendered on the left side of table cell.
     * @see InsettedRenderer
     * @see TableCellRenderer
     */
    public InsettedRenderer(TableCellRenderer aDelegate, Component aLeadingComponent) {
        this(aDelegate);
        leadingComponent = aLeadingComponent;
        setupInsets();
    }

    /**
     * Creates a new instance of InsettedRenderer.
     * @param aDelegate TableCellRenderer instance we have delegate all significant work to.
     * @param aLeadingComponent Component that will be rendered on the left side of table cell.
     * @param aTrailingComponent  Component that will be rendered on the right side of table cell.
     * @see InsettedRenderer
     * @see TableCellRenderer
     */
    public InsettedRenderer(TableCellRenderer aDelegate, Component aLeadingComponent, Component aTrailingComponent) {
        this(aDelegate);
        leadingComponent = aLeadingComponent;
        trailingComponent = aTrailingComponent;
        setupInsets();
    }

    public TableCellRenderer unwrap() {
        return delegate;
    }

    /**
     * Returns the component rendered on the left side of the table cell.
     * @return Component rendered on the left side of the table cell.
     */
    public Component getLeadingComponent() {
        return leadingComponent;
    }

    /**
     * Sets the component rendered on the left side of the table cell.
     * @param aLeadingComponent Component that will be rendered on the left side of the table cell.
     */
    public void setLeadingComponent(Component aLeadingComponent) {
        leadingComponent = aLeadingComponent;
        setupInsets();
    }

    /**
     * Returns the component rendered on the right side of the table cell.
     * @return Component rendered on the right side of the table cell.
     */
    public Component getTrailingComponent() {
        return trailingComponent;
    }

    /**
     * Sets the component rendered on the right side of the table cell.
     * @param aTrailingComponent Component that will be rendered on the right side of the table cell.
     */
    public void setTrailingComponent(Component aTrailingComponent) {
        trailingComponent = aTrailingComponent;
        setupInsets();
    }

    /**
     * {@inheritDoc}
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setBorder(null);
        if (delegate != null) {
            Component content = delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            remove(content);
            add(content, BorderLayout.CENTER);
            setBackground(content.getBackground());
            setForeground(content.getForeground());
            if (content instanceof JComponent) {
                setBorder(((JComponent) content).getBorder());
                ((JComponent) content).setBorder(null);
            }
            leadingComponent.setBackground(getBackground());
        }
        return this;
    }

    private void setupInsets() {
        removeAll();
        if (leadingComponent != null) {
            add(leadingComponent, BorderLayout.WEST);
        }
        if (trailingComponent != null) {
            add(trailingComponent, BorderLayout.EAST);
        }
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    public boolean isOpaque() {
        //return false;
        Color back = getBackground();
        Component p = getParent();
        if (p != null) {
            p = p.getParent();
        }

        // p should now be the JTable.
        boolean colorMatch = (back != null) && (p != null)
                && back.equals(p.getBackground())
                && p.isOpaque();
        return !colorMatch && super.isOpaque();
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    public void repaint(Rectangle r) {
    }

    /**
     * Overridden for performance reasons.
     */
    @Override
    public void repaint() {
    }
    
    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        if(leadingComponent != null)
            leadingComponent.setBackground(bg);
        if(trailingComponent != null)
            trailingComponent.setBackground(bg);
        if(delegate instanceof Component)
            ((Component)delegate).setBackground(bg);
    }
}
