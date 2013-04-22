/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.scheme;

import com.eas.dbcontrols.DbControl;
import com.eas.dbcontrols.DbControlPanel;
import javax.swing.JComponent;
import javax.swing.JTable;

/**
 *
 * @author mg
 */
public class DbScheme extends DbControlPanel implements DbControl {

    public DbScheme() {
        super();
    }

    @Override
    public Object getCellEditorValue() {
        return editingValue;
    }

    @Override
    public void setEditingValue(Object aValue) {
    }

    @Override
    protected void initializeEditor() {
        super.initializeEditor();
    }

    @Override
    protected void setupEditor(JTable table) {
        super.setupEditor(table);
    }

    @Override
    protected void initializeRenderer() {
    }

    @Override
    protected void setupRenderer(JTable table, int row, int column, boolean isSelected) {
    }

    @Override
    protected void applyFont() {
    }

    @Override
    protected void applyAlign() {
    }

    @Override
    protected void applyTooltip(String aText) {
    }

    @Override
    protected void applyEditable2Field() {
    }

    @Override
    public JComponent getFocusTargetComponent() {
        return null;
    }

    @Override
    protected void applyEnabled() {
        super.applyEnabled();
    }

    @Override
    protected void applyCursor() {
    }
    
    @Override
    protected void applyOpaque() {
    }
    
    @Override
    protected void applyBackground() {
    }

    @Override
    protected void applyForeground() {
    }

    @Override
    protected void initializeDesign() {
        super.initializeDesign();
    }
}
