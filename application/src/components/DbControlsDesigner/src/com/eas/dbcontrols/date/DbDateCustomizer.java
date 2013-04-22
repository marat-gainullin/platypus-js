/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DbDateCustomizer.java
 *
 * Created on 18.05.2009, 10:16:10
 */
package com.eas.dbcontrols.date;

import com.eas.client.datamodel.ModelElementRef;
import com.eas.client.datamodel.gui.selectors.ModelElementSelector;
import com.eas.dbcontrols.DbControlCustomizer;
import com.eas.dbcontrols.DbControlDesignInfo;
import com.eas.dbcontrols.DbControlsDesignUtils;
import com.eas.dbcontrols.actions.DbControlSnapshotAction;
import com.eas.dbcontrols.actions.DmElementClearAction;
import com.eas.dbcontrols.actions.DmElementSelectAction;
import com.eas.dbcontrols.actions.FontClearAction;
import com.eas.dbcontrols.actions.FontSelectAction;
import com.eas.dbcontrols.fontchooser.FontChooser;
import java.awt.Component;
import java.awt.Font;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 *
 * @author mg
 */
public class DbDateCustomizer extends DbControlCustomizer {

    protected final void fillActionMap() {
        getActionMap().put(DbDateFieldSelectAction.class.getSimpleName(), new DbDateFieldSelectAction());
        getActionMap().put(DbDateFieldClearAction.class.getSimpleName(), new DbDateFieldClearAction());
        getActionMap().put(DbDateFontChangeAction.class.getSimpleName(), new DbDateFontChangeAction());
        getActionMap().put(DbDateFontClearAction.class.getSimpleName(), new DbDateFontClearAction());
        getActionMap().put(DbDateFormatChangeAction.class.getSimpleName(), new DbDateFormatChangeAction());
        getActionMap().put(DbDateExpandedChangeAction.class.getSimpleName(), new DbDateExpandedChangeAction());
    }

    @Override
    protected void designInfoPropertyChanged(String aPropertyName, Object oldValue, Object newValue) {
        switch (aPropertyName) {
            case DbDateDesignInfo.DATAMODELELEMENT:
                updateDmElementView();
                break;
            case DbDateDesignInfo.SELECTFUNCTION:
            case DbDateDesignInfo.HANDLEFUNCTION:
                updateFunctionsView();
                break;
            case DbDateDesignInfo.SELECTONLY:
                updateSelectOnlyView();
                break;
            case DbDateDesignInfo.EXPANDED:
                updateExpandedView();
                break;
            case DbDateDesignInfo.DATEFORMAT:
                updateDateFormatView();
                break;
            default:
                updateView();
                break;
        }
        checkActionMap();
    }

    protected class DbDateFieldSelectAction extends DmElementSelectAction {

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            ModelElementRef old = after.getDatamodelElement();
            ModelElementRef newRef = ModelElementSelector.selectDatamodelElement(getDatamodel(), old, ModelElementSelector.FIELD_SELECTION_SUBJECT, selectValidator, DbDateCustomizer.this, DbControlsDesignUtils.getLocalizedString("selectField"));
            if (newRef != null) {
                after.setDatamodelElement(newRef);
            }
        }
    }

    protected class DbDateFieldClearAction extends DmElementClearAction {

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            after.setDatamodelElement(null);
        }
    }

    protected class DbDateFontChangeAction extends FontSelectAction {

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            Font oldFont = after.getFont();
            Font lfont = FontChooser.chooseFont(pnlControl, oldFont, DbControlsDesignUtils.getLocalizedString("selectFont"));
            if (lfont != null) {
                after.setFont(lfont);
            }
        }

        @Override
        public boolean isEnabled() {
            if (designInfo != null && designInfo instanceof DbDateDesignInfo) {
                DbDateDesignInfo cinfo = (DbDateDesignInfo) designInfo;
                return !cinfo.isExpanded();
            }
            return super.isEnabled();
        }
    }

    protected class DbDateFontClearAction extends FontClearAction {

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            after.setFont(null);
        }

        @Override
        public boolean isEnabled() {
            if (designInfo != null && designInfo instanceof DbDateDesignInfo) {
                DbDateDesignInfo cinfo = (DbDateDesignInfo) designInfo;
                return !cinfo.isExpanded();
            }
            return super.isEnabled();
        }
    }

    protected class DbDateExpandedChangeAction extends DbControlSnapshotAction {

        public DbDateExpandedChangeAction() {
            super();
            putValue(Action.NAME, DbControlsDesignUtils.getLocalizedString("chkExpanded"));
        }

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            if (after != null && after instanceof DbDateDesignInfo) {
                DbDateDesignInfo cinfo = (DbDateDesignInfo) after;
                cinfo.setExpanded(chkExpanded.isSelected());
            }
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

    protected class DbDateFormatChangeAction extends DbControlSnapshotAction {

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            if (after != null && after instanceof DbDateDesignInfo) {
                DbDateDesignInfo cinfo = (DbDateDesignInfo) after;
                if (comboDateFormat.getModel() != null) {
                    Object oValue = comboDateFormat.getModel().getSelectedItem();
                    if (oValue != null && oValue instanceof String) {
                        cinfo.setDateFormat((String) oValue);
                    }
                }
            }
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

    /** Creates new customizer DbDateCustomizer */
    public DbDateCustomizer() {
        super();
        fillActionMap();
        initComponents();
        comboDateFormat.setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component lcomp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (lcomp != null && lcomp instanceof DefaultListCellRenderer
                        && value != null && value instanceof String) {
                    DefaultListCellRenderer dr = (DefaultListCellRenderer) lcomp;
                    dr.setText(DbControlsDesignUtils.getLocalizedString((String) value));
                }
                return lcomp;
            }
        });
        txtDatamodelField.setText(DbControlsDesignUtils.getLocalizedString("notSelected"));
        //
        comboSelectFunction.setModel(selectFunctionModel);
        cmbSelectFunction = comboSelectFunction;
        chkSelectOnly = checkSelectOnly;
        //
        comboHandleFunction.setModel(handleFunctionModel);
        cmbHandleFunction = comboHandleFunction;
    }

    @Override
    protected void updateView() {
        updateDmElementView();
        updateFunctionsView();
        updateSelectOnlyView();
        updateExpandedView();
        updateDateFormatView();
    }

    private void updateDmElementView() {
        if (designInfo instanceof DbDateDesignInfo) {
            try {
                DbDateDesignInfo cinfo = (DbDateDesignInfo) designInfo;
                DbControlsDesignUtils.updateDmElement(getDatamodel(), cinfo.getDatamodelElement(), pnlDmField, fieldRenderer, txtDatamodelField, fieldsFont);
            } catch (Exception ex) {
                Logger.getLogger(DbDateCustomizer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void updateSelectOnlyView() {
        DbDateDesignInfo cinfo = (DbDateDesignInfo) designInfo;
        checkSelectOnly.setSelected(cinfo.isSelectOnly());
    }

    private void updateFunctionsView() {
        DbDateDesignInfo cinfo = (DbDateDesignInfo) designInfo;
        DbControlsDesignUtils.updateScriptItem(comboSelectFunction, cinfo.getSelectFunction());
        DbControlsDesignUtils.updateScriptItem(comboHandleFunction, cinfo.getHandleFunction());
    }

    private void updateExpandedView() {
        DbDateDesignInfo cinfo = (DbDateDesignInfo) designInfo;
        chkExpanded.setSelected(cinfo.isExpanded());
    }

    private void updateDateFormatView() {
        DbDateDesignInfo cinfo = (DbDateDesignInfo) designInfo;
        DbControlsDesignUtils.setSelectedItem(comboDateFormat, cinfo.getDateFormat());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlControl = new javax.swing.JPanel();
        lblDateTimeFormat = new javax.swing.JLabel();
        comboDateFormat = new javax.swing.JComboBox();
        chkExpanded = new javax.swing.JCheckBox();
        pnlDmField = new javax.swing.JPanel();
        txtDatamodelField = new javax.swing.JTextField();
        pnlFieldButtons = new javax.swing.JPanel();
        btnDelField = new javax.swing.JButton();
        btnSelectField = new javax.swing.JButton();
        lblDmField = new javax.swing.JLabel();
        pnlSelectFunctionCustomizer = new javax.swing.JPanel();
        lblValueSelectHandler = new javax.swing.JLabel();
        pnlSelectFunction = new javax.swing.JPanel();
        btnDelSelectFunction = new javax.swing.JButton();
        comboSelectFunction = new javax.swing.JComboBox();
        checkSelectOnly = new javax.swing.JCheckBox();
        lblValueHandler = new javax.swing.JLabel();
        pnlHandleFunction = new javax.swing.JPanel();
        btnDelHandleFunction = new javax.swing.JButton();
        comboHandleFunction = new javax.swing.JComboBox();

        setLayout(new java.awt.BorderLayout());

        lblDateTimeFormat.setText(DbControlsDesignUtils.getLocalizedString("lblDateTimeFormat")); // NOI18N

        comboDateFormat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "DD_MM_YYYY", "DD_MM", "DD", "MM_YYYY", "MM", "YYYY", "DD_MM_YYYY_HH_MM_SS", "HH_MM_SS", "HH_MM", "MM_SS", " " }));
        comboDateFormat.setAction(getActionMap().get(DbDateFormatChangeAction.class.getSimpleName() ));

        chkExpanded.setAction(getActionMap().get(DbDateExpandedChangeAction.class.getSimpleName() ));

        javax.swing.GroupLayout pnlControlLayout = new javax.swing.GroupLayout(pnlControl);
        pnlControl.setLayout(pnlControlLayout);
        pnlControlLayout.setHorizontalGroup(
            pnlControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlControlLayout.createSequentialGroup()
                .addComponent(lblDateTimeFormat)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkExpanded, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
            .addComponent(comboDateFormat, 0, 209, Short.MAX_VALUE)
        );
        pnlControlLayout.setVerticalGroup(
            pnlControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlControlLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDateTimeFormat, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkExpanded, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboDateFormat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(pnlControl, java.awt.BorderLayout.CENTER);

        pnlDmField.setLayout(new java.awt.BorderLayout());

        txtDatamodelField.setEditable(false);
        txtDatamodelField.setToolTipText(DbControlsDesignUtils.getLocalizedString("selectDatamodelField")); // NOI18N
        pnlDmField.add(txtDatamodelField, java.awt.BorderLayout.CENTER);

        pnlFieldButtons.setLayout(new java.awt.BorderLayout());

        btnDelField.setAction(getActionMap().get(DbDateFieldClearAction.class.getSimpleName() ));
        btnDelField.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlFieldButtons.add(btnDelField, java.awt.BorderLayout.CENTER);

        btnSelectField.setAction(getActionMap().get(DbDateFieldSelectAction.class.getSimpleName() ));
        btnSelectField.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlFieldButtons.add(btnSelectField, java.awt.BorderLayout.WEST);

        pnlDmField.add(pnlFieldButtons, java.awt.BorderLayout.EAST);

        lblDmField.setText(DbControlsDesignUtils.getLocalizedString("lblDmField")); // NOI18N
        pnlDmField.add(lblDmField, java.awt.BorderLayout.NORTH);

        lblValueSelectHandler.setText(DbControlsDesignUtils.getLocalizedString("lblSelectHandler")); // NOI18N

        pnlSelectFunction.setLayout(new java.awt.BorderLayout());

        btnDelSelectFunction.setAction(selectFunctionClearAction);
        btnDelSelectFunction.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlSelectFunction.add(btnDelSelectFunction, java.awt.BorderLayout.EAST);

        comboSelectFunction.setEditable(true);
        comboSelectFunction.setAction(selectFunctionChangeAction);
        pnlSelectFunction.add(comboSelectFunction, java.awt.BorderLayout.CENTER);

        checkSelectOnly.setAction(selectOnlyChangeAction);

        lblValueHandler.setText(DbControlsDesignUtils.getLocalizedString("lblValueHandler")); // NOI18N

        pnlHandleFunction.setLayout(new java.awt.BorderLayout());

        btnDelHandleFunction.setAction(handleFunctionClearAction);
        btnDelHandleFunction.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlHandleFunction.add(btnDelHandleFunction, java.awt.BorderLayout.EAST);

        comboHandleFunction.setEditable(true);
        comboHandleFunction.setAction(handleFunctionChangeAction);
        pnlHandleFunction.add(comboHandleFunction, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout pnlSelectFunctionCustomizerLayout = new javax.swing.GroupLayout(pnlSelectFunctionCustomizer);
        pnlSelectFunctionCustomizer.setLayout(pnlSelectFunctionCustomizerLayout);
        pnlSelectFunctionCustomizerLayout.setHorizontalGroup(
            pnlSelectFunctionCustomizerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSelectFunctionCustomizerLayout.createSequentialGroup()
                .addComponent(lblValueSelectHandler)
                .addContainerGap(137, Short.MAX_VALUE))
            .addComponent(pnlSelectFunction, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
            .addGroup(pnlSelectFunctionCustomizerLayout.createSequentialGroup()
                .addComponent(checkSelectOnly)
                .addContainerGap())
            .addGroup(pnlSelectFunctionCustomizerLayout.createSequentialGroup()
                .addComponent(lblValueHandler)
                .addContainerGap())
            .addComponent(pnlHandleFunction, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
        );
        pnlSelectFunctionCustomizerLayout.setVerticalGroup(
            pnlSelectFunctionCustomizerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSelectFunctionCustomizerLayout.createSequentialGroup()
                .addComponent(lblValueSelectHandler)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlSelectFunction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkSelectOnly)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblValueHandler)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlHandleFunction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlDmField.add(pnlSelectFunctionCustomizer, java.awt.BorderLayout.SOUTH);

        add(pnlDmField, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelField;
    private javax.swing.JButton btnDelHandleFunction;
    private javax.swing.JButton btnDelSelectFunction;
    private javax.swing.JButton btnSelectField;
    private javax.swing.JCheckBox checkSelectOnly;
    private javax.swing.JCheckBox chkExpanded;
    private javax.swing.JComboBox comboDateFormat;
    private javax.swing.JComboBox comboHandleFunction;
    private javax.swing.JComboBox comboSelectFunction;
    private javax.swing.JLabel lblDateTimeFormat;
    private javax.swing.JLabel lblDmField;
    private javax.swing.JLabel lblValueHandler;
    private javax.swing.JLabel lblValueSelectHandler;
    private javax.swing.JPanel pnlControl;
    private javax.swing.JPanel pnlDmField;
    private javax.swing.JPanel pnlFieldButtons;
    private javax.swing.JPanel pnlHandleFunction;
    private javax.swing.JPanel pnlSelectFunction;
    private javax.swing.JPanel pnlSelectFunctionCustomizer;
    private javax.swing.JTextField txtDatamodelField;
    // End of variables declaration//GEN-END:variables
}
