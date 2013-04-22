/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DbSpinCustomizer.java
 *
 * Created on 18.05.2009, 10:23:34
 */
package com.eas.dbcontrols.spin;

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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author mg
 */
public class DbSpinCustomizer extends DbControlCustomizer {

    public static void normalizeStep(DbSpinDesignInfo cinfo) {
        Double lStep = cinfo.getStep();
        if (lStep < 0) {
            lStep = -lStep;
        }
        if (lStep == 0) {
            lStep = 1.0;
        }
        if (cinfo.getMax() - cinfo.getMin() < lStep) {
            lStep = cinfo.getMax() - cinfo.getMin();
        }
        cinfo.setStep(lStep);
    }

    protected final void fillActionMap() {
        getActionMap().put(DbSpinFieldSelectAction.class.getSimpleName(), new DbSpinFieldSelectAction());
        getActionMap().put(DbSpinFieldClearAction.class.getSimpleName(), new DbSpinFieldClearAction());
        getActionMap().put(DbSpinFontChangeAction.class.getSimpleName(), new DbSpinFontChangeAction());
        getActionMap().put(DbSpinFontClearAction.class.getSimpleName(), new DbSpinFontClearAction());
        getActionMap().put(DbSpinIsMaxChangeAction.class.getSimpleName(), new DbSpinIsMaxChangeAction());
        getActionMap().put(DbSpinIsMinChangeAction.class.getSimpleName(), new DbSpinIsMinChangeAction());
        getActionMap().put(DbSpinMaxChangeAction.class.getSimpleName(), new DbSpinMaxChangeAction());
        getActionMap().put(DbSpinMinChangeAction.class.getSimpleName(), new DbSpinMinChangeAction());
        getActionMap().put(DbSpinStepChangeAction.class.getSimpleName(), new DbSpinStepChangeAction());
    }

    @Override
    protected void designInfoPropertyChanged(String aPropertyName, Object oldValue, Object newValue) {
        switch (aPropertyName) {
            case DbSpinDesignInfo.DATAMODELELEMENT:
                updateDatamodelElementView();
                break;
            case DbSpinDesignInfo.PROP_MIN:
                updateMinView();
                break;
            case DbSpinDesignInfo.PROP_MAX:
                updateMaxView();
                break;
            case DbSpinDesignInfo.PROP_STEP:
                updateStepView();
                break;
            case DbSpinDesignInfo.SELECTONLY:
                updateSelectOnlyView();
                break;
            case DbSpinDesignInfo.HANDLEFUNCTION:
            case DbSpinDesignInfo.SELECTFUNCTION:
                updateFunctionsView();
                break;
            default:
                updateView();
                break;
        }
        checkActionMap();
    }

    protected class DbSpinFieldSelectAction extends DmElementSelectAction {

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            ModelElementRef old = after.getDatamodelElement();
            ModelElementRef newRef = ModelElementSelector.selectDatamodelElement(getDatamodel(), old, ModelElementSelector.FIELD_SELECTION_SUBJECT, selectValidator, DbSpinCustomizer.this, DbControlsDesignUtils.getLocalizedString("selectField"));
            if (newRef != null) {
                after.setDatamodelElement(newRef);
            }
        }
    }

    protected class DbSpinFieldClearAction extends DmElementClearAction {

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            after.setDatamodelElement(null);
        }
    }

    protected class DbSpinFontChangeAction extends FontSelectAction {

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            Font oldFont = after.getFont();
            Font lfont = FontChooser.chooseFont(pnlControl, oldFont, DbControlsDesignUtils.getLocalizedString("selectFont"));
            if (lfont != null) {
                after.setFont(lfont);
            }
        }
    }

    protected class DbSpinFontClearAction extends FontClearAction {

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            after.setFont(null);
        }
    }

    protected class DbSpinMinChangeAction extends DbControlSnapshotAction implements ChangeListener {

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            if (after instanceof DbSpinDesignInfo) {
                Double lMin = (Double) spinMin.getValue();
                DbSpinDesignInfo cinfo = (DbSpinDesignInfo) after;
                if (lMin < cinfo.getMax()) {
                    cinfo.setMin(lMin);
                }
                normalizeStep(cinfo);
            }
        }

        @Override
        public boolean isEnabled() {
            return chkMin.isSelected();
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            if (!updatingView) {
                actionPerformed(null);
            }
        }
    }

    protected class DbSpinMaxChangeAction extends DbControlSnapshotAction implements ChangeListener {

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            if (after instanceof DbSpinDesignInfo) {
                Double lMax = (Double) spinMax.getValue();
                DbSpinDesignInfo cinfo = (DbSpinDesignInfo) after;
                if (lMax > cinfo.getMin()) {
                    cinfo.setMax(lMax);
                }
                normalizeStep(cinfo);
            }
        }

        @Override
        public boolean isEnabled() {
            return chkMax.isSelected();
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            if (!updatingView) {
                actionPerformed(null);
            }
        }
    }

    protected class DbSpinStepChangeAction extends DbControlSnapshotAction implements ChangeListener {

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            if (after instanceof DbSpinDesignInfo) {
                Double lStep = (Double) spinStep.getValue();
                DbSpinDesignInfo cinfo = (DbSpinDesignInfo) after;
                if (lStep > 0 && cinfo.getMax() - cinfo.getMin() >= lStep) {
                    cinfo.setStep(lStep);
                }
            }
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            if (!updatingView) {
                actionPerformed(null);
            }
        }
    }

    protected class DbSpinIsMinChangeAction extends DbControlSnapshotAction {

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            if (after instanceof DbSpinDesignInfo) {
                DbSpinDesignInfo cinfo = (DbSpinDesignInfo) after;
                if (chkMin.isSelected()) {
                    cinfo.setMin(Double.valueOf(0));
                } else {
                    cinfo.setMin(-Double.MAX_VALUE);
                }
                normalizeStep(cinfo);
                spinMin.setEnabled(chkMin.isSelected());
            }
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

    protected class DbSpinIsMaxChangeAction extends DbControlSnapshotAction {

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            if (after instanceof DbSpinDesignInfo) {
                DbSpinDesignInfo cinfo = (DbSpinDesignInfo) after;
                if (chkMax.isSelected()) {
                    cinfo.setMax(Short.MAX_VALUE);
                } else {
                    cinfo.setMax(Double.MAX_VALUE);
                }
                normalizeStep(cinfo);
                spinMax.setEnabled(chkMax.isSelected());
            }
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

    /** Creates new customizer DbSpinCustomizer */
    public DbSpinCustomizer() {
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
        //
        spinMin.getModel().setValue(Double.valueOf(0.0f));
        spinMax.getModel().setValue(Double.valueOf(0.0f));
        spinMin.getModel().addChangeListener((ChangeListener) getActionMap().get(DbSpinMinChangeAction.class.getSimpleName()));
        spinMax.getModel().addChangeListener((ChangeListener) getActionMap().get(DbSpinMaxChangeAction.class.getSimpleName()));
        spinStep.getModel().addChangeListener((ChangeListener) getActionMap().get(DbSpinStepChangeAction.class.getSimpleName()));
    }

    @Override
    protected void updateView() {
        updateDatamodelElementView();
        updateMinView();
        updateMaxView();
        updateStepView();
        updateSelectOnlyView();
        updateFunctionsView();
    }

    private void updateFunctionsView() {
        if (designInfo instanceof DbSpinDesignInfo) {
            DbSpinDesignInfo cinfo = (DbSpinDesignInfo) designInfo;
            DbControlsDesignUtils.updateScriptItem(comboSelectFunction, cinfo.getSelectFunction());
            DbControlsDesignUtils.updateScriptItem(comboHandleFunction, cinfo.getHandleFunction());
        }
    }

    private void updateSelectOnlyView() {
        if (designInfo instanceof DbSpinDesignInfo) {
            DbSpinDesignInfo cinfo = (DbSpinDesignInfo) designInfo;
            checkSelectOnly.setSelected(cinfo.isSelectOnly());
        }
    }

    private void updateStepView() {
        if (designInfo instanceof DbSpinDesignInfo) {
            DbSpinDesignInfo cinfo = (DbSpinDesignInfo) designInfo;
            updatingView = true;
            try {
                spinStep.setValue(cinfo.getStep());
            } finally {
                updatingView = false;
            }
        }
    }

    private void updateMaxView() {
        if (designInfo instanceof DbSpinDesignInfo) {
            try {
                DbSpinDesignInfo cinfo = (DbSpinDesignInfo) designInfo;
                updatingView = true;
                try {
                    boolean lMax = Math.abs(cinfo.getMax() - Double.MAX_VALUE) < 1e-32;
                    chkMax.setSelected(!lMax);
                    if (!lMax) {
                        spinMax.setValue(cinfo.getMax());
                    } else {
                        spinMax.setValue(Double.valueOf(0));
                    }
                    spinMax.setEnabled(!lMax);
                } finally {
                    updatingView = false;
                }
            } catch (Exception ex) {
                Logger.getLogger(DbSpinCustomizer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void updateMinView() {
        if (designInfo instanceof DbSpinDesignInfo) {
            try {
                DbSpinDesignInfo cinfo = (DbSpinDesignInfo) designInfo;
                updatingView = true;
                try {
                    boolean lMin = Math.abs(cinfo.getMin() - -Double.MAX_VALUE) < 1e-32;
                    chkMin.setSelected(!lMin);
                    if (!lMin) {
                        spinMin.setValue(cinfo.getMin());
                    } else {
                        spinMin.setValue(Double.valueOf(0));
                    }
                    spinMin.setEnabled(!lMin);
                } finally {
                    updatingView = false;
                }
            } catch (Exception ex) {
                Logger.getLogger(DbSpinCustomizer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void updateDatamodelElementView() {
        if (designInfo instanceof DbSpinDesignInfo) {
            try {
                DbSpinDesignInfo cinfo = (DbSpinDesignInfo) designInfo;
                DbControlsDesignUtils.updateDmElement(getDatamodel(), cinfo.getDatamodelElement(), pnlDmField, fieldRenderer, txtDatamodelField, fieldsFont);
            } catch (Exception ex) {
                Logger.getLogger(DbSpinCustomizer.class.getName()).log(Level.SEVERE, null, ex);
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
        spinMin = new javax.swing.JSpinner();
        spinMax = new javax.swing.JSpinner();
        spinStep = new javax.swing.JSpinner();
        chkMin = new javax.swing.JCheckBox();
        chkMax = new javax.swing.JCheckBox();
        lblStep = new javax.swing.JLabel();
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

        spinMin.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(0.0d), null, null, Double.valueOf(1.0d)));
        spinMin.setEnabled(false);

        spinMax.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(0.0d), null, null, Double.valueOf(1.0d)));
        spinMax.setEnabled(false);

        spinStep.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(1.0d), null, null, Double.valueOf(1.0d)));

        chkMin.setAction(getActionMap().get(DbSpinIsMinChangeAction.class.getSimpleName()));
        chkMin.setText(DbControlsDesignUtils.getLocalizedString("chkMin")); // NOI18N

        chkMax.setAction(getActionMap().get(DbSpinIsMaxChangeAction.class.getSimpleName()));
        chkMax.setText(DbControlsDesignUtils.getLocalizedString("chkMax")); // NOI18N

        lblStep.setText(DbControlsDesignUtils.getLocalizedString("lblStep")); // NOI18N

        javax.swing.GroupLayout pnlControlLayout = new javax.swing.GroupLayout(pnlControl);
        pnlControl.setLayout(pnlControlLayout);
        pnlControlLayout.setHorizontalGroup(
            pnlControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlControlLayout.createSequentialGroup()
                .addGroup(pnlControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkMin)
                    .addComponent(spinMin, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(spinMax)
                    .addComponent(chkMax, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblStep)
                    .addComponent(spinStep, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        pnlControlLayout.setVerticalGroup(
            pnlControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlControlLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(pnlControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStep)
                    .addComponent(chkMin)
                    .addComponent(chkMax))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spinMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spinMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spinStep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        add(pnlControl, java.awt.BorderLayout.CENTER);

        pnlDmField.setLayout(new java.awt.BorderLayout());

        txtDatamodelField.setEditable(false);
        txtDatamodelField.setToolTipText(DbControlsDesignUtils.getLocalizedString("selectDatamodelField")); // NOI18N
        pnlDmField.add(txtDatamodelField, java.awt.BorderLayout.CENTER);

        pnlFieldButtons.setLayout(new java.awt.BorderLayout());

        btnDelField.setAction(getActionMap().get(DbSpinFieldClearAction.class.getSimpleName()));
        btnDelField.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlFieldButtons.add(btnDelField, java.awt.BorderLayout.CENTER);

        btnSelectField.setAction(getActionMap().get(DbSpinFieldSelectAction.class.getSimpleName()));
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
                .addContainerGap(153, Short.MAX_VALUE))
            .addComponent(pnlSelectFunction, javax.swing.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
            .addGroup(pnlSelectFunctionCustomizerLayout.createSequentialGroup()
                .addComponent(checkSelectOnly)
                .addContainerGap())
            .addGroup(pnlSelectFunctionCustomizerLayout.createSequentialGroup()
                .addComponent(lblValueHandler)
                .addContainerGap())
            .addGroup(pnlSelectFunctionCustomizerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(pnlHandleFunction, javax.swing.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE))
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
                .addContainerGap(35, Short.MAX_VALUE))
            .addGroup(pnlSelectFunctionCustomizerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlSelectFunctionCustomizerLayout.createSequentialGroup()
                    .addGap(83, 83, 83)
                    .addComponent(pnlHandleFunction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
    private javax.swing.JCheckBox chkMax;
    private javax.swing.JCheckBox chkMin;
    private javax.swing.JComboBox comboHandleFunction;
    private javax.swing.JComboBox comboSelectFunction;
    private javax.swing.JLabel lblDmField;
    private javax.swing.JLabel lblStep;
    private javax.swing.JLabel lblValueHandler;
    private javax.swing.JLabel lblValueSelectHandler;
    private javax.swing.JPanel pnlControl;
    private javax.swing.JPanel pnlDmField;
    private javax.swing.JPanel pnlFieldButtons;
    private javax.swing.JPanel pnlHandleFunction;
    private javax.swing.JPanel pnlSelectFunction;
    private javax.swing.JPanel pnlSelectFunctionCustomizer;
    private javax.swing.JSpinner spinMax;
    private javax.swing.JSpinner spinMin;
    private javax.swing.JSpinner spinStep;
    private javax.swing.JTextField txtDatamodelField;
    // End of variables declaration//GEN-END:variables
}
