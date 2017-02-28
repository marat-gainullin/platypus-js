/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.editing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.EventObject;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

/**
 * Editor with capability of transparently adding some artifacts to the left or
 * to the right of the main editor. This editor delegates all significant work
 * to the delegate. It renders inset components by itself.
 *
 * @author mg
 */
public class InsettedEditor extends JPanel implements TableCellEditor {

    protected TableCellEditor delegate;
    protected Component leadingComponent;
    protected Component trailingComponent;

    /**
     * Creates a new instance of InsettedEditor.
     *
     * @param aDelegate TableCellEditor instance we have delegate all
     * significant work to.
     * @see TableCellEditor
     */
    public InsettedEditor(TableCellEditor aDelegate) {
        super();
        delegate = aDelegate;
    }

    /**
     * Creates a new instance of InsettedEditor.
     *
     * @param aDelegate TableCellEditor instance we have delegate all
     * significant work to.
     * @param aLeadingComponent Component that will be rendered on the left side
     * of the table cell.
     * @see TableCellEditor
     */
    public InsettedEditor(TableCellEditor aDelegate, Component aLeadingComponent) {
        this(aDelegate);
        leadingComponent = aLeadingComponent;
        setupInsets();
    }

    /**
     * Creates a new instance of InsettedEditor.
     *
     * @param aDelegate TableCellEditor instance we have delegate all
     * significant work to.
     * @param aLeadingComponent Component that will be rendered on the left side
     * of table cell.
     * @param aTrailingComponent Component that will be rendered on the right
     * side of table cell.
     * @see TableCellEditor
     */
    public InsettedEditor(TableCellEditor aDelegate, Component aLeadingComponent, Component aTrailingComponent) {
        this(aDelegate);
        leadingComponent = aLeadingComponent;
        trailingComponent = aTrailingComponent;
        setupInsets();
    }

    /**
     * Returns the component rendered on the left side of the table cell.
     *
     * @return Component rendered on the left side of the table cell.
     */
    public Component getLeadingComponent() {
        return leadingComponent;
    }

    /**
     * Sets the component rendered on the left side of the table cell.
     *
     * @param aLeadingComponent Component that will be rendered on the left side
     * of the table cell.
     */
    public void setLeadingComponent(Component aLeadingComponent) {
        leadingComponent = aLeadingComponent;
        setupInsets();
    }

    /**
     * Returns the component rendered on the right side of the table cell.
     *
     * @return Component rendered on the right side of the table cell.
     */
    public Component getTrailingComponent() {
        return trailingComponent;
    }

    /**
     * Sets the component rendered on the right side of the table cell.
     *
     * @param aTrailingComponent Component that will be rendered on the right
     * side of the table cell.
     */
    public void setTrailingComponent(Component aTrailingComponent) {
        trailingComponent = aTrailingComponent;
        setupInsets();
    }

    private void setupInsets() {
        removeAll();
        setLayout(new BorderLayout());
        if (leadingComponent != null) {
            add(leadingComponent, BorderLayout.WEST);
        }
        if (trailingComponent != null) {
            add(trailingComponent, BorderLayout.EAST);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (delegate != null) {
            Component content = delegate.getTableCellEditorComponent(table, value, isSelected, row, column);
            remove(content);
            add(content, BorderLayout.CENTER);
            setBackground(content.getBackground());
            setForeground(content.getForeground());
            if (content instanceof JComponent) {
                setBorder(((JComponent) content).getBorder());
                ((JComponent) content).setBorder(null);
            }
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getCellEditorValue() {
        return delegate.getCellEditorValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCellEditable(EventObject anEvent) {
        if (delegate != null) {
            return delegate.isCellEditable(anEvent);
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        return delegate.shouldSelectCell(anEvent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean stopCellEditing() {
        return delegate.stopCellEditing();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancelCellEditing() {
        delegate.cancelCellEditing();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCellEditorListener(CellEditorListener l) {
        delegate.addCellEditorListener(l);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeCellEditorListener(CellEditorListener l) {
        delegate.removeCellEditorListener(l);
    }

    public TableCellEditor unwrap() {
        return delegate;
    }
}
