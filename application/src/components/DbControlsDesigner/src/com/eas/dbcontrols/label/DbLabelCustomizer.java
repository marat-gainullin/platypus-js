/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DbLabelCustomizer.java
 *
 * Created on 18.05.2009, 10:19:59
 */
package com.eas.dbcontrols.label;

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
import java.awt.Font;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.text.MaskFormatter;

/**
 *
 * @author mg
 */
public class DbLabelCustomizer extends DbControlCustomizer {

    protected final void fillActionMap() {
        getActionMap().put(DbLabelFieldSelectAction.class.getSimpleName(), new DbLabelFieldSelectAction());
        getActionMap().put(DbLabelFieldClearAction.class.getSimpleName(), new DbLabelFieldClearAction());
        getActionMap().put(DbLabelFontChangeAction.class.getSimpleName(), new DbLabelFontChangeAction());
        getActionMap().put(DbLabelFontClearAction.class.getSimpleName(), new DbLabelFontClearAction());
        getActionMap().put(DbLabelFormatChangeAction.class.getSimpleName(), new DbLabelFormatChangeAction());
        getActionMap().put(DbLabelMultilineChangeAction.class.getSimpleName(), new DbLabelMultilineChangeAction());
    }

    @Override
    protected void designInfoPropertyChanged(String aPropertyName, Object oldValue, Object newValue) {
        switch (aPropertyName) {
            case DbLabelDesignInfo.DATAMODELELEMENT:
                updateDmElementView();
                break;
            case DbLabelDesignInfo.SELECTONLY:
                updateSelectOnlyView();
                break;
            case DbLabelDesignInfo.FORMATSTRING:
                updateFormatStringView();
                break;
            case DbLabelDesignInfo.MULTILINE:
                updateMultilineView();
                break;
            case DbLabelDesignInfo.HANDLEFUNCTION:
            case DbLabelDesignInfo.SELECTFUNCTION:
                updateFunctionsView();
                break;
            default:
                updateView();
                break;
        }
        checkActionMap();
    }

    protected class DbLabelFieldSelectAction extends DmElementSelectAction {

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            ModelElementRef old = after.getDatamodelElement();
            ModelElementRef newRef = ModelElementSelector.selectDatamodelElement(getDatamodel(), old, ModelElementSelector.FIELD_SELECTION_SUBJECT, selectValidator, DbLabelCustomizer.this, DbControlsDesignUtils.getLocalizedString("selectField"));
            if (newRef != null) {
                after.setDatamodelElement(newRef);
            }
        }
    }

    protected class DbLabelFieldClearAction extends DmElementClearAction {

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            after.setDatamodelElement(null);
        }
    }

    protected class DbLabelFontChangeAction extends FontSelectAction {

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            Font oldFont = after.getFont();
            Font lfont = FontChooser.chooseFont(pnlControl, oldFont, DbControlsDesignUtils.getLocalizedString("selectFont"));
            if (lfont != null) {
                after.setFont(lfont);
            }
        }
    }

    protected class DbLabelFontClearAction extends FontClearAction {

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            after.setFont(null);
        }
    }

    protected class DbLabelFormatChangeAction extends DbControlSnapshotAction {

        @Override
        public boolean isEnabled() {
            if (designInfo instanceof DbLabelDesignInfo) {
                DbLabelDesignInfo info = (DbLabelDesignInfo) designInfo;
                return !info.isMultiline();
            }
            return false;
        }
        protected MaskFormatter sampleFormatter = new MaskFormatter();

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            if (after instanceof DbLabelDesignInfo) {
                DbLabelDesignInfo cinfo = (DbLabelDesignInfo) after;
                try {
                    sampleFormatter.setMask(txtFormatString.getText());
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(DbLabelCustomizer.this, DbControlsDesignUtils.getLocalizedString("badFormatErrorMsg"), DbControlsDesignUtils.getLocalizedString(DbLabel.class.getSimpleName()), JOptionPane.ERROR_MESSAGE);
                    return;
                }
                cinfo.setFormatString(txtFormatString.getText());
            }
        }
    }

    protected class DbLabelMultilineChangeAction extends DbControlSnapshotAction {

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            if (after instanceof DbLabelDesignInfo) {
                DbLabelDesignInfo cinfo = (DbLabelDesignInfo) after;
                cinfo.setMultiline(chkMultiline.isSelected());
            }
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

    /** Creates new customizer DbLabelCustomizer */
    public DbLabelCustomizer() {
        super();
        fillActionMap();
        initComponents();
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
        updateSelectOnlyView();
        updateFormatStringView();
        updateMultilineView();
        updateFunctionsView();
    }

    private void updateFunctionsView() {
        if (designInfo instanceof DbLabelDesignInfo) {
            DbLabelDesignInfo cinfo = (DbLabelDesignInfo) designInfo;
            DbControlsDesignUtils.updateScriptItem(comboHandleFunction, cinfo.getHandleFunction());
            DbControlsDesignUtils.updateScriptItem(comboSelectFunction, cinfo.getSelectFunction());
        }
    }

    private void updateMultilineView() {
        if (designInfo instanceof DbLabelDesignInfo) {
            DbLabelDesignInfo cinfo = (DbLabelDesignInfo) designInfo;
            chkMultiline.setSelected(cinfo.isMultiline());
        }
    }

    private void updateFormatStringView() {
        if (designInfo instanceof DbLabelDesignInfo) {
            DbLabelDesignInfo cinfo = (DbLabelDesignInfo) designInfo;
            txtFormatString.setText(cinfo.getFormatString());
        }
    }

    private void updateSelectOnlyView() {
        if (designInfo instanceof DbLabelDesignInfo) {
            DbLabelDesignInfo cinfo = (DbLabelDesignInfo) designInfo;
            checkSelectOnly.setSelected(cinfo.isSelectOnly());
        }
    }

    private void updateDmElementView() {
        if (designInfo instanceof DbLabelDesignInfo) {
            try {
                DbLabelDesignInfo cinfo = (DbLabelDesignInfo) designInfo;
                DbControlsDesignUtils.updateDmElement(getDatamodel(), cinfo.getDatamodelElement(), pnlDmField, fieldRenderer, txtDatamodelField, fieldsFont);
            } catch (Exception ex) {
                Logger.getLogger(DbLabelCustomizer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
        lblFormatString = new javax.swing.JLabel();
        txtFormatString = new javax.swing.JTextField();
        chkMultiline = new javax.swing.JCheckBox();
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

        lblFormatString.setText(DbControlsDesignUtils.getLocalizedString("lblFormatString")); // NOI18N

        txtFormatString.setAction(getActionMap().get(DbLabelFormatChangeAction.class.getSimpleName()));

        chkMultiline.setAction(getActionMap().get(DbLabelMultilineChangeAction.class.getSimpleName()));
        chkMultiline.setText(DbControlsDesignUtils.getLocalizedString("chkMultiline")); // NOI18N

        javax.swing.GroupLayout pnlControlLayout = new javax.swing.GroupLayout(pnlControl);
        pnlControl.setLayout(pnlControlLayout);
        pnlControlLayout.setHorizontalGroup(
            pnlControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlControlLayout.createSequentialGroup()
                .addGroup(pnlControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlControlLayout.createSequentialGroup()
                        .addComponent(txtFormatString, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkMultiline))
                    .addComponent(lblFormatString))
                .addContainerGap())
        );
        pnlControlLayout.setVerticalGroup(
            pnlControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlControlLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblFormatString)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkMultiline)
                    .addComponent(txtFormatString, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(pnlControl, java.awt.BorderLayout.CENTER);

        pnlDmField.setLayout(new java.awt.BorderLayout());

        txtDatamodelField.setEditable(false);
        txtDatamodelField.setToolTipText(DbControlsDesignUtils.getLocalizedString("selectDatamodelField")); // NOI18N
        pnlDmField.add(txtDatamodelField, java.awt.BorderLayout.CENTER);

        pnlFieldButtons.setLayout(new java.awt.BorderLayout());

        btnDelField.setAction(getActionMap().get(DbLabelFieldClearAction.class.getSimpleName()));
        btnDelField.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlFieldButtons.add(btnDelField, java.awt.BorderLayout.CENTER);

        btnSelectField.setAction(getActionMap().get(DbLabelFieldSelectAction.class.getSimpleName()));
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
                .addContainerGap(192, Short.MAX_VALUE))
            .addComponent(pnlSelectFunction, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
            .addGroup(pnlSelectFunctionCustomizerLayout.createSequentialGroup()
                .addComponent(checkSelectOnly)
                .addContainerGap())
            .addGroup(pnlSelectFunctionCustomizerLayout.createSequentialGroup()
                .addComponent(lblValueHandler)
                .addContainerGap())
            .addComponent(pnlHandleFunction, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
        );
        pnlSelectFunctionCustomizerLayout.setVerticalGroup(
            pnlSelectFunctionCustomizerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSelectFunctionCustomizerLayout.createSequentialGroup()
                .addComponent(lblValueSelectHandler)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlSelectFunction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkSelectOnly)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
    private javax.swing.JCheckBox chkMultiline;
    private javax.swing.JComboBox comboHandleFunction;
    private javax.swing.JComboBox comboSelectFunction;
    private javax.swing.JLabel lblDmField;
    private javax.swing.JLabel lblFormatString;
    private javax.swing.JLabel lblValueHandler;
    private javax.swing.JLabel lblValueSelectHandler;
    private javax.swing.JPanel pnlControl;
    private javax.swing.JPanel pnlDmField;
    private javax.swing.JPanel pnlFieldButtons;
    private javax.swing.JPanel pnlHandleFunction;
    private javax.swing.JPanel pnlSelectFunction;
    private javax.swing.JPanel pnlSelectFunctionCustomizer;
    private javax.swing.JTextField txtDatamodelField;
    private javax.swing.JTextField txtFormatString;
    // End of variables declaration//GEN-END:variables
}
