/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DbComboCustomizer.java
 *
 * Created on 18.05.2009, 10:12:07
 */
package com.eas.dbcontrols.combo;

import com.bearsoft.rowset.metadata.Parameter;
import com.eas.client.SQLUtils;
import com.eas.client.datamodel.Entity;
import com.eas.client.datamodel.ModelElementRef;
import com.eas.client.datamodel.Relation;
import com.eas.client.datamodel.gui.selectors.ModelElementSelector;
import com.eas.dbcontrols.DbControlCustomizer;
import com.eas.dbcontrols.DbControlDesignInfo;
import com.eas.dbcontrols.DbControlsDesignUtils;
import com.eas.dbcontrols.FieldRefRenderer;
import com.eas.dbcontrols.actions.DbControlSnapshotAction;
import com.eas.dbcontrols.actions.DmElementClearAction;
import com.eas.dbcontrols.actions.DmElementSelectAction;
import com.eas.dbcontrols.actions.FontClearAction;
import com.eas.dbcontrols.actions.FontSelectAction;
import com.eas.dbcontrols.fontchooser.FontChooser;
import java.awt.Font;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.border.EtchedBorder;

/**
 *
 * @author mg
 */
public class DbComboCustomizer extends DbControlCustomizer {

    protected final void fillActionMap() {
        getActionMap().put(DbComboFieldSelectAction.class.getSimpleName(), new DbComboFieldSelectAction());
        getActionMap().put(DbComboFieldClearAction.class.getSimpleName(), new DbComboFieldClearAction());
        getActionMap().put(DbComboDisplayFieldChangeAction.class.getSimpleName(), new DbComboDisplayFieldChangeAction());
        getActionMap().put(DbComboDisplayFieldClearAction.class.getSimpleName(), new DbComboDisplayFieldClearAction());
        getActionMap().put(DbComboValueFieldChangeAction.class.getSimpleName(), new DbComboValueFieldChangeAction());
        getActionMap().put(DbComboValueFieldClearAction.class.getSimpleName(), new DbComboValueFieldClearAction());
        getActionMap().put(DbComboFontChangeAction.class.getSimpleName(), new DbComboFontChangeAction());
        getActionMap().put(DbComboFontClearAction.class.getSimpleName(), new DbComboFontClearAction());
        getActionMap().put(DbComboListChangeAction.class.getSimpleName(), new DbComboListChangeAction());
    }

    @Override
    protected void designInfoPropertyChanged(String aPropertyName, Object oldValue, Object newValue) {
        switch (aPropertyName) {
            case DbComboDesignInfo.DISPLAYFIELD:
                updateDisplayFieldView();
                break;
            case DbComboDesignInfo.DATAMODELELEMENT:
                updateDatamodelElementView();
                break;
            case DbComboDesignInfo.VALUEFIELD:
                updateValueFieldView();
                break;
            case DbComboDesignInfo.LIST:
                updateIsListView();
                break;
            case DbComboDesignInfo.SELECTONLY:
                updateSelectOnlyView();
                break;
            case DbComboDesignInfo.SELECTFUNCTION:
            case DbComboDesignInfo.HANDLEFUNCTION:
                updateFunctionsView();
                break;
            default:
                updateView();
                break;
        }
        checkActionMap();
    }

    protected class DbComboFieldSelectAction extends DmElementSelectAction {

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            ModelElementRef old = after.getDatamodelElement();
            ModelElementRef newRef = ModelElementSelector.selectDatamodelElement(getDatamodel(), old, ModelElementSelector.FIELD_SELECTION_SUBJECT, selectValidator, DbComboCustomizer.this, DbControlsDesignUtils.getLocalizedString("selectField"));
            if (newRef != null) {
                after.setDatamodelElement(newRef);
                if (newRef.getField() != null && ((DbComboDesignInfo) after).getValueField() != null
                        && ((DbComboDesignInfo) after).getValueField().getField() != null) {
                    int valueFieldType = ((DbComboDesignInfo) after).getValueField().getField().getTypeInfo().getSqlType();
                    int destFieldType = newRef.getField().getTypeInfo().getSqlType();
                    if (!SQLUtils.isSimpleTypesCompatible(valueFieldType, destFieldType)) {
                        ((DbComboDesignInfo) after).setValueField(null);
                    }
                }
            }
        }
    }

    protected class DbComboFieldClearAction extends DmElementClearAction {

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            after.setDatamodelElement(null);
        }
    }

    protected class DbComboDisplayFieldChangeAction extends DmElementSelectAction {

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            if (after instanceof DbComboDesignInfo) {
                DbComboDesignInfo cafter = (DbComboDesignInfo) after;
                ModelElementRef old = cafter.getDisplayField();
                ModelElementRef newRef = ModelElementSelector.selectDatamodelElement(getDatamodel(), old, ModelElementSelector.FIELD_SELECTION_SUBJECT, selectValidator, DbComboCustomizer.this, DbControlsDesignUtils.getLocalizedString("selectField"));
                if (newRef != null) {
                    cafter.setDisplayField(newRef);
                }
            }
        }
    }

    protected class DbComboDisplayFieldClearAction extends DmElementClearAction {

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            if (after instanceof DbComboDesignInfo) {
                DbComboDesignInfo cafter = (DbComboDesignInfo) after;
                cafter.setDisplayField(null);
            }
        }

        @Override
        public boolean isEnabled() {
            if (designInfo instanceof DbComboDesignInfo) {
                DbComboDesignInfo info = (DbComboDesignInfo) designInfo;
                if (info != null) {
                    return info.getDisplayField() != null;
                }
            }
            return super.isEnabled();
        }
    }

    protected class DbComboValueFieldChangeAction extends DmElementSelectAction {

        protected class ComboValueFieldValidator extends FieldValidator {

            @Override
            public boolean validateDatamodelElementSelection(ModelElementRef aElement) {
                if (aElement != null && aElement.getField() != null
                        && super.validateDatamodelElementSelection(aElement)) {
                    if (aElement.getField() instanceof Parameter) {
                        Entity entity = getDatamodel().getEntityById(aElement.getEntityId());
                        if (entity != null) {
                            Set<Relation> rels = entity.getInRelations();
                            if (rels != null) {
                                for (Relation rel : rels) {
                                    if (rel.isRightParameter() && rel.getRightParameter() != null
                                            && rel.getRightParameter().equals(aElement.getFieldName())) {
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                    if (designInfo != null && designInfo instanceof DbComboDesignInfo) {
                        DbComboDesignInfo cInfo = (DbComboDesignInfo) designInfo;
                        if (cInfo.getDatamodelElement() != null
                                && cInfo.getDatamodelElement().getField() != null) {
                            int destType = cInfo.getDatamodelElement().getField().getTypeInfo().getSqlType();
                            return SQLUtils.isSimpleTypesCompatible(aElement.getField().getTypeInfo().getSqlType(), destType);
                        }
                    }
                }
                return false;
            }
        }
        ComboValueFieldValidator comboValueSelectValidator = new ComboValueFieldValidator();

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            if (after instanceof DbComboDesignInfo) {
                DbComboDesignInfo cafter = (DbComboDesignInfo) after;
                ModelElementRef old = cafter.getValueField();
                ModelElementRef newRef = ModelElementSelector.selectDatamodelElement(getDatamodel(), old, ModelElementSelector.FIELD_SELECTION_SUBJECT, comboValueSelectValidator, DbComboCustomizer.this, DbControlsDesignUtils.getLocalizedString("selectField"));
                if (newRef != null) {
                    cafter.setValueField(newRef);
                }
            }
        }
    }

    protected class DbComboValueFieldClearAction extends DmElementClearAction {

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            if (after instanceof DbComboDesignInfo) {
                DbComboDesignInfo cafter = (DbComboDesignInfo) after;
                cafter.setValueField(null);
            }
        }

        @Override
        public boolean isEnabled() {
            if (designInfo instanceof DbComboDesignInfo) {
                DbComboDesignInfo info = (DbComboDesignInfo) designInfo;
                if (info != null) {
                    return info.getValueField() != null;
                }
            }
            return super.isEnabled();
        }
    }

    protected class DbComboListChangeAction extends DbControlSnapshotAction {

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            if (after instanceof DbComboDesignInfo) {
                DbComboDesignInfo cafter = (DbComboDesignInfo) after;
                cafter.setList(chkList.isSelected());
            }
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

    protected class DbComboFontChangeAction extends FontSelectAction {

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            Font oldFont = after.getFont();
            Font lfont = FontChooser.chooseFont(DbComboCustomizer.this, oldFont, DbControlsDesignUtils.getLocalizedString("selectFont"));
            if (lfont != null) {
                after.setFont(lfont);
            }
        }
    }

    protected class DbComboFontClearAction extends FontClearAction {

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            after.setFont(null);
        }
    }

    /** Creates new customizer DbComboCustomizer */
    public DbComboCustomizer() {
        super();
        fillActionMap();
        initComponents();
        txtDatamodelField.setText(DbControlsDesignUtils.getLocalizedString("notSelected"));
        txtDatamodelDisplayingField.setText(DbControlsDesignUtils.getLocalizedString("notSelected"));
        txtDatamodelValueField.setText(DbControlsDesignUtils.getLocalizedString("notSelected"));
        displayFieldRenderer.setBorder(new EtchedBorder());
        valueFieldRenderer.setBorder(new EtchedBorder());
        //
        comboSelectFunction.setModel(selectFunctionModel);
        cmbSelectFunction = comboSelectFunction;
        chkSelectOnly = checkSelectOnly;
        //
        comboHandleFunction.setModel(handleFunctionModel);
        cmbHandleFunction = comboHandleFunction;
    }
    FieldRefRenderer displayFieldRenderer = new FieldRefRenderer();
    FieldRefRenderer valueFieldRenderer = new FieldRefRenderer();

    @Override
    protected void updateView() {
        updateDisplayFieldView();
        updateDatamodelElementView();
        updateValueFieldView();
        updateIsListView();
        updateSelectOnlyView();
        updateFunctionsView();
    }

    private void updateFunctionsView() {
        if (designInfo instanceof DbComboDesignInfo) {
            DbComboDesignInfo cinfo = (DbComboDesignInfo) designInfo;
            DbControlsDesignUtils.updateScriptItem(comboSelectFunction, cinfo.getSelectFunction());
            DbControlsDesignUtils.updateScriptItem(comboHandleFunction, cinfo.getHandleFunction());
        }
    }

    private void updateSelectOnlyView() {
        if (designInfo instanceof DbComboDesignInfo) {
            DbComboDesignInfo cinfo = (DbComboDesignInfo) designInfo;
            checkSelectOnly.setSelected(cinfo.isSelectOnly());
        }
    }

    private void updateIsListView() {
        if (designInfo instanceof DbComboDesignInfo) {
            DbComboDesignInfo cinfo = (DbComboDesignInfo) designInfo;
            chkList.setSelected(cinfo.isList());
        }
    }

    private void updateValueFieldView() {
        if (designInfo instanceof DbComboDesignInfo) {
            try {
                DbComboDesignInfo cinfo = (DbComboDesignInfo) designInfo;
                DbControlsDesignUtils.updateDmElement(getDatamodel(), cinfo.getValueField(), pnlDmValueField, valueFieldRenderer, txtDatamodelValueField, fieldsFont);
            } catch (Exception ex) {
                Logger.getLogger(DbComboCustomizer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void updateDatamodelElementView() {
        if (designInfo instanceof DbComboDesignInfo) {
            try {
                DbComboDesignInfo cinfo = (DbComboDesignInfo) designInfo;
                DbControlsDesignUtils.updateDmElement(getDatamodel(), cinfo.getDatamodelElement(), pnlDmField, fieldRenderer, txtDatamodelField, fieldsFont);
            } catch (Exception ex) {
                Logger.getLogger(DbComboCustomizer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void updateDisplayFieldView() {
        if (designInfo instanceof DbComboDesignInfo) {
            try {
                DbComboDesignInfo cinfo = (DbComboDesignInfo) designInfo;
                DbControlsDesignUtils.updateDmElement(getDatamodel(), cinfo.getDisplayField(), pnlDmDisplayingField, displayFieldRenderer, txtDatamodelDisplayingField, fieldsFont);
            } catch (Exception ex) {
                Logger.getLogger(DbComboCustomizer.class.getName()).log(Level.SEVERE, null, ex);
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
        pnlDmDisplayingField = new javax.swing.JPanel();
        txtDatamodelDisplayingField = new javax.swing.JTextField();
        pnlDisplayingFieldButtons = new javax.swing.JPanel();
        btnDelDisplayingField = new javax.swing.JButton();
        btnSelectDisplayingField = new javax.swing.JButton();
        lblDisplayingField = new javax.swing.JLabel();
        pnlDmValueField = new javax.swing.JPanel();
        txtDatamodelValueField = new javax.swing.JTextField();
        pnlValueFieldButtons = new javax.swing.JPanel();
        btnDelValueField = new javax.swing.JButton();
        btnSelectDisplayingField1 = new javax.swing.JButton();
        lblValueField = new javax.swing.JLabel();
        chkList = new javax.swing.JCheckBox();
        pnlDmField = new javax.swing.JPanel();
        txtDatamodelField = new javax.swing.JTextField();
        pnlFieldButtons = new javax.swing.JPanel();
        btnDelField = new javax.swing.JButton();
        btnSelectField = new javax.swing.JButton();
        lblDmField = new javax.swing.JLabel();
        pnlFunctionsCustomizer = new javax.swing.JPanel();
        lblValueSelectHandler = new javax.swing.JLabel();
        pnlSelectFunction = new javax.swing.JPanel();
        btnDelSelectFunction = new javax.swing.JButton();
        comboSelectFunction = new javax.swing.JComboBox();
        checkSelectOnly = new javax.swing.JCheckBox();
        pnlHandleFunction = new javax.swing.JPanel();
        btnDelHandleFunction = new javax.swing.JButton();
        comboHandleFunction = new javax.swing.JComboBox();
        lblValueHandler = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        pnlDmDisplayingField.setLayout(new java.awt.BorderLayout());

        txtDatamodelDisplayingField.setEditable(false);
        txtDatamodelDisplayingField.setToolTipText(DbControlsDesignUtils.getLocalizedString("selectDatamodelField")); // NOI18N
        pnlDmDisplayingField.add(txtDatamodelDisplayingField, java.awt.BorderLayout.CENTER);

        pnlDisplayingFieldButtons.setLayout(new java.awt.BorderLayout());

        btnDelDisplayingField.setAction(getActionMap().get(DbComboDisplayFieldClearAction.class.getSimpleName()) );
        btnDelDisplayingField.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlDisplayingFieldButtons.add(btnDelDisplayingField, java.awt.BorderLayout.CENTER);

        btnSelectDisplayingField.setAction(getActionMap().get(DbComboDisplayFieldChangeAction.class.getSimpleName()) );
        btnSelectDisplayingField.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlDisplayingFieldButtons.add(btnSelectDisplayingField, java.awt.BorderLayout.WEST);

        pnlDmDisplayingField.add(pnlDisplayingFieldButtons, java.awt.BorderLayout.EAST);

        lblDisplayingField.setText(DbControlsDesignUtils.getLocalizedString("lblDisplayingField")); // NOI18N
        pnlDmDisplayingField.add(lblDisplayingField, java.awt.BorderLayout.NORTH);

        pnlDmValueField.setLayout(new java.awt.BorderLayout());

        txtDatamodelValueField.setEditable(false);
        txtDatamodelValueField.setToolTipText(DbControlsDesignUtils.getLocalizedString("selectDatamodelField")); // NOI18N
        pnlDmValueField.add(txtDatamodelValueField, java.awt.BorderLayout.CENTER);

        pnlValueFieldButtons.setLayout(new java.awt.BorderLayout());

        btnDelValueField.setAction(getActionMap().get(DbComboValueFieldClearAction.class.getSimpleName()) );
        btnDelValueField.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlValueFieldButtons.add(btnDelValueField, java.awt.BorderLayout.CENTER);

        btnSelectDisplayingField1.setAction(getActionMap().get(DbComboValueFieldChangeAction.class.getSimpleName()) );
        btnSelectDisplayingField1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlValueFieldButtons.add(btnSelectDisplayingField1, java.awt.BorderLayout.WEST);

        pnlDmValueField.add(pnlValueFieldButtons, java.awt.BorderLayout.EAST);

        lblValueField.setText(DbControlsDesignUtils.getLocalizedString("lblValueField")); // NOI18N
        pnlDmValueField.add(lblValueField, java.awt.BorderLayout.NORTH);

        chkList.setAction(getActionMap().get(DbComboListChangeAction.class.getSimpleName()) );
        chkList.setText(DbControlsDesignUtils.getLocalizedString("lblList")); // NOI18N

        javax.swing.GroupLayout pnlControlLayout = new javax.swing.GroupLayout(pnlControl);
        pnlControl.setLayout(pnlControlLayout);
        pnlControlLayout.setHorizontalGroup(
            pnlControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlDmValueField, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
            .addComponent(pnlDmDisplayingField, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlControlLayout.createSequentialGroup()
                .addContainerGap(182, Short.MAX_VALUE)
                .addComponent(chkList)
                .addContainerGap())
        );
        pnlControlLayout.setVerticalGroup(
            pnlControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlControlLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlDmValueField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlDmDisplayingField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(chkList)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(pnlControl, java.awt.BorderLayout.CENTER);

        pnlDmField.setLayout(new java.awt.BorderLayout());

        txtDatamodelField.setEditable(false);
        txtDatamodelField.setToolTipText(DbControlsDesignUtils.getLocalizedString("selectDatamodelField")); // NOI18N
        pnlDmField.add(txtDatamodelField, java.awt.BorderLayout.CENTER);

        pnlFieldButtons.setLayout(new java.awt.BorderLayout());

        btnDelField.setAction(getActionMap().get(DbComboFieldClearAction.class.getSimpleName()) );
        btnDelField.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlFieldButtons.add(btnDelField, java.awt.BorderLayout.CENTER);

        btnSelectField.setAction(getActionMap().get(DbComboFieldSelectAction.class.getSimpleName()) );
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

        pnlHandleFunction.setLayout(new java.awt.BorderLayout());

        btnDelHandleFunction.setAction(handleFunctionClearAction);
        btnDelHandleFunction.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlHandleFunction.add(btnDelHandleFunction, java.awt.BorderLayout.EAST);

        comboHandleFunction.setEditable(true);
        comboHandleFunction.setAction(handleFunctionChangeAction);
        pnlHandleFunction.add(comboHandleFunction, java.awt.BorderLayout.CENTER);

        lblValueHandler.setText(DbControlsDesignUtils.getLocalizedString("lblValueHandler")); // NOI18N

        javax.swing.GroupLayout pnlFunctionsCustomizerLayout = new javax.swing.GroupLayout(pnlFunctionsCustomizer);
        pnlFunctionsCustomizer.setLayout(pnlFunctionsCustomizerLayout);
        pnlFunctionsCustomizerLayout.setHorizontalGroup(
            pnlFunctionsCustomizerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFunctionsCustomizerLayout.createSequentialGroup()
                .addComponent(lblValueSelectHandler)
                .addContainerGap(163, Short.MAX_VALUE))
            .addComponent(pnlSelectFunction, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
            .addGroup(pnlFunctionsCustomizerLayout.createSequentialGroup()
                .addComponent(checkSelectOnly)
                .addContainerGap())
            .addGroup(pnlFunctionsCustomizerLayout.createSequentialGroup()
                .addComponent(lblValueHandler)
                .addContainerGap())
            .addComponent(pnlHandleFunction, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
        );
        pnlFunctionsCustomizerLayout.setVerticalGroup(
            pnlFunctionsCustomizerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFunctionsCustomizerLayout.createSequentialGroup()
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

        pnlDmField.add(pnlFunctionsCustomizer, java.awt.BorderLayout.SOUTH);

        add(pnlDmField, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelDisplayingField;
    private javax.swing.JButton btnDelField;
    private javax.swing.JButton btnDelHandleFunction;
    private javax.swing.JButton btnDelSelectFunction;
    private javax.swing.JButton btnDelValueField;
    private javax.swing.JButton btnSelectDisplayingField;
    private javax.swing.JButton btnSelectDisplayingField1;
    private javax.swing.JButton btnSelectField;
    private javax.swing.JCheckBox checkSelectOnly;
    private javax.swing.JCheckBox chkList;
    private javax.swing.JComboBox comboHandleFunction;
    private javax.swing.JComboBox comboSelectFunction;
    private javax.swing.JLabel lblDisplayingField;
    private javax.swing.JLabel lblDmField;
    private javax.swing.JLabel lblValueField;
    private javax.swing.JLabel lblValueHandler;
    private javax.swing.JLabel lblValueSelectHandler;
    private javax.swing.JPanel pnlControl;
    private javax.swing.JPanel pnlDisplayingFieldButtons;
    private javax.swing.JPanel pnlDmDisplayingField;
    private javax.swing.JPanel pnlDmField;
    private javax.swing.JPanel pnlDmValueField;
    private javax.swing.JPanel pnlFieldButtons;
    private javax.swing.JPanel pnlFunctionsCustomizer;
    private javax.swing.JPanel pnlHandleFunction;
    private javax.swing.JPanel pnlSelectFunction;
    private javax.swing.JPanel pnlValueFieldButtons;
    private javax.swing.JTextField txtDatamodelDisplayingField;
    private javax.swing.JTextField txtDatamodelField;
    private javax.swing.JTextField txtDatamodelValueField;
    // End of variables declaration//GEN-END:variables
}
