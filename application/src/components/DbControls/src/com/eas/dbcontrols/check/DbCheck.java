/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.check;

import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.eas.dbcontrols.DbControl;
import com.eas.dbcontrols.DbControlPanel;
import com.eas.dbcontrols.InitializingMethod;
import com.eas.dbcontrols.check.rt.NullableCheckBox;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.EventObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

/**
 *
 * @author mg
 */
public class DbCheck extends DbControlPanel implements DbControl {

    public DbCheck() {
        super();
    }
    protected JCheckBox designCheck;

    @Override
    protected void initializeDesign() {
        if (kind == InitializingMethod.UNDEFINED
                && getComponentCount() == 0) {
            super.initializeDesign();
            designCheck = new JCheckBox((check != null && check.getText() != null && !check.getText().isEmpty()) ? check.getText() : this.getClass().getSimpleName().replace("Db", "Model"));
            designCheck.setOpaque(false);
            add(designCheck, BorderLayout.CENTER);
            applyBackground();
            applyForeground();
            applyEnabled();
            applyFont();
        }
    }

    protected class CheckRowsetUpdater extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (standalone) {
                try {
                    // editingValue will be changed after rowset's change had been accepted,
                    // but if value in rowset is not changed, editingValue remains unchanged.
                    setValue(check.getValue());
                } catch (Exception ex) {
                    Logger.getLogger(DbCheck.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                setEditingValue(check.getValue());
                if (haveNullerAction()) {
                    fireCellEditingCompleted();
                } else {
                    fireEditingStopped();
                }
            }
        }

        /*
         private boolean updateRowset(Boolean b) throws Exception {
         if (!DbCheck.this.isUpdating() && colIndex > 0 && rsEntity != null) {
         Rowset rowset = rsEntity.getRowset();
         if (rowset != null && !rowset.isAfterLast() && !rowset.isBeforeFirst()) {
         Fields field = rowset.getFields();
         int sqlType = field.get(colIndex).getTypeInfo().getSqlType();
         Object lValue = rowset.getConverter().convert2RowsetCompatible(SQLUtils.generateBooleanValue4Type(sqlType, b), rowset.getFields().get(colIndex).getTypeInfo());
         return rowset.updateObject(colIndex, lValue);
         }
         }
         return false;
         }
         */
    }

    @Override
    protected void applyForeground() {
        if (kind != InitializingMethod.UNDEFINED) {
            if (check != null) {
                check.setForeground(getForeground());
            }
        } else if (designCheck != null) {
            designCheck.setForeground(getForeground());
        }
    }

    @Override
    protected void applyBackground() {
        if (kind != InitializingMethod.UNDEFINED) {
            if (check != null) {
                check.setBackground(getBackground());
            }
            if (leftExtraPanel != null) {
                leftExtraPanel.setBackground(getBackground());
            }
        }
    }

    @Override
    protected void applyCursor() {
        if (kind != InitializingMethod.UNDEFINED) {
            if (check != null) {
                check.setCursor(getCursor());
            }
            if (leftExtraPanel != null) {
                leftExtraPanel.setCursor(getCursor());
            }
        }
    }

    @Override
    protected void applyOpaque() {
        if (kind != InitializingMethod.UNDEFINED) {
            if (check != null) {
                check.setOpaque(isOpaque());
            }
            if (leftExtraPanel != null) {
                leftExtraPanel.setOpaque(isOpaque());
            }
        }
    }

    @Override
    public void applyFont() {
        if (check != null) {
            check.setFont(getFont());
        }
        if (designCheck != null) {
            designCheck.setFont(getFont());
        }
    }

    @Override
    public JComponent getFocusTargetComponent() {
        if (check != null) {
            return check;
        }
        return null;
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        return true;
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        return false;
    }

    @Override
    public Object getCellEditorValue() {
        return editingValue;
    }

    @Override
    protected void acceptCellValue(Object aValue) throws Exception {
        super.acceptCellValue(aValue);
        editingValue = converter.convert2RowsetCompatible(editingValue, DataTypeInfo.BOOLEAN);
    }
    JPanel leftExtraPanel = new JPanel();

    @Override
    protected void invokingLaterProcessControls() {
        super.invokingLaterProcessControls();
        if (getAlign() == SwingConstants.CENTER && extraTools.isVisible()) {
            Dimension extraControlsSize = extraTools.getPreferredSize();
            extraControlsSize.width += 3;
            leftExtraPanel.setPreferredSize(extraControlsSize);
            leftExtraPanel.setVisible(true);
        }
    }

    @Override
    protected void invokingLaterUnprocessControls() {
        super.invokingLaterUnprocessControls();
        leftExtraPanel.setVisible(extraTools.isVisible());
    }
    protected NullableCheckBox check = new NullableCheckBox();
    protected CheckRowsetUpdater chekRowSetUpdater = new CheckRowsetUpdater();

    @Override
    protected void initializeRenderer() {
        if (kind != InitializingMethod.RENDERER) {
            if (borderless) {
                check.setBorder(null);
            }
            // Warning!!!
            check.setOpaque(true);
            // Don't touch above line please! The thing is that checkbox is non-opaque by default
            // and we must make it opaque according to db controls structure (parent panel, etc).
            //
            removeAll();
            setLayout(new BorderLayout());
            addIconLabel();
            check.setHorizontalAlignment(SwingConstants.CENTER);
            add(check, BorderLayout.CENTER);
            applyFont();
            applyAlign();
            kind = InitializingMethod.RENDERER;
        }
    }

    @Override
    protected void setupRenderer(JTable table, int row, int column, boolean isSelected) {
        if (table != null) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }
            check.setBorderPaintedFlat(true);
            check.setValue((Boolean) editingValue);
        }
    }

    @Override
    protected void initializeEditor() {
        if (kind != InitializingMethod.EDITOR) {
            kind = InitializingMethod.EDITOR;
            removeAll();
            setLayout(new BorderLayout());
            addIconLabel();
            if (borderless) {
                check.setBorder(null);
            }
            add(check, BorderLayout.CENTER);
            check.setHorizontalAlignment(SwingConstants.CENTER);
            check.setInheritsPopupMenu(true);
            checkEvents(check);
            leftExtraPanel.setVisible(false);
            add(leftExtraPanel, BorderLayout.WEST);
            super.initializeEditor();
        }
    }

    @Override
    protected void applyEditable2Field() {
        if (check != null) {
            check.removeActionListener(chekRowSetUpdater);
            if (editable) {
                check.addActionListener(chekRowSetUpdater);
            }
            check.setEnabled(editable);
        }
    }

    @Override
    public void initializeBorder() {
        setBorder(null);
    }

    @Override
    protected void applyAlign() {
        if (check != null) {
            check.setHorizontalAlignment(align);
        }
        super.applyAlign();
    }

    @Override
    protected void setupEditor(JTable table) {
        if (table != null) {
            setBackground(table.getBackground());
        }
        check.setValue((Boolean) editingValue);
        super.setupEditor(table);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (borderless) {
            check.setBorder(null);
        }
    }

    @Override
    protected void applyEnabled() {
        if (check != null) {
            check.setEnabled(isEnabled());
        }
        if (designCheck != null) {
            designCheck.setEnabled(isEnabled());
        }
        super.applyEnabled();
    }

    public String getText() {
        if (check != null) {
            return check.getText();
        } else {
            return null;
        }
    }

    public void setText(String aText) {
        if (check != null) {
            check.setText(aText);
        }
        if (designCheck != null) {
            designCheck.setText(aText);
        }
    }

    @Override
    public String getToolTipText() {
        if (check != null) {
            return check.getToolTipText();
        }
        return null;
    }

    public boolean isSelected() {
        if (check != null) {
            return check.isSelected();
        }
        return false;
    }

    public void setSelected(boolean aSelected) {
        if (check != null) {
            check.setSelected(aSelected);
        }
    }

    @Override
    protected void applyTooltip(String aText) {
        if (check != null) {
            check.setToolTipText(aText);
        }
    }
}
