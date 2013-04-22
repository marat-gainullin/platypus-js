/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.spin;

import com.eas.dbcontrols.DbControl;
import com.eas.dbcontrols.DbControlPanel;
import com.eas.dbcontrols.InitializingMethod;
import com.eas.controls.AdaptiveDecimalFormatter;
import com.eas.dbcontrols.spin.rt.NullableSpinner;
import com.eas.dbcontrols.spin.rt.NullableSpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author mg
 */
public class DbSpin extends DbControlPanel implements DbControl {

    public DbSpin() {
        super();
    }

    @Override
    protected void initializeDesign() {
        if (getComponentCount() == 0) {
            super.initializeDesign();
            JSpinner cb = new JSpinner();
            cb.setOpaque(false);
            add(cb, BorderLayout.CENTER);
        }
    }
    protected Double min;
    protected Double max;
    protected double step = 1;
    protected NullableSpinner spin;
    protected NullableSpinnerNumberModel spinModel;
    private Double dummyNumber = 0.0;

    public Double getMin() {
        return min;
    }

    public void setMin(Double aValue) throws Exception {
        if (min != aValue) {
            min = aValue;
            if (spinModel != null) {
                spinModel.setMin(aValue);
            }
        }
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double aValue) throws Exception {
        if (max != aValue) {
            max = aValue;
            if (spinModel != null) {
                spinModel.setMax(aValue);
            }
        }
    }

    public double getStep() {
        return step;
    }

    public void setStep(double aValue) throws Exception {
        if (step != aValue) {
            step = aValue;
            if (spinModel != null) {
                spinModel.setStep(aValue);
            }
        }
    }

    @Override
    protected void initializeEditor() {
        if (kind != InitializingMethod.EDITOR) {
            kind = InitializingMethod.EDITOR;
            setLayout(new BorderLayout());
            removeAll();
            addIconLabel();
            spinModel = new NullableSpinnerNumberModel(dummyNumber, getMin(), getMax(), getStep());
            spin = new NullableSpinner(spinModel);
            spinModel.setValue(null);

            if (borderless) {
                spin.setBorder(null);
            }
            if (rowsetUpdater != null) {
                spinModel.removeValueChangeListener(rowsetUpdater);
            }
            rowsetUpdater = new SpinRowsetUpdater();
            spinModel.addValueChangeListener(rowsetUpdater);
            if (spin.getEditor() instanceof JSpinner.NumberEditor) {
                final JFormattedTextField tf = ((JSpinner.NumberEditor) spin.getEditor()).getTextField();
                if (tf != null) {
                    checkEvents(tf);
                    NumberFormatter nf = new AdaptiveDecimalFormatter();
                    nf.setAllowsInvalid(false);
                    tf.setFormatterFactory(new DefaultFormatterFactory(nf));
                    if (!standalone) {
                        Object actionKey = tf.getInputMap().get(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
                        if (actionKey != null) {
                            tf.getActionMap().put(actionKey, new TextFieldsCommitAction(tf, actionKey));
                        }
                    }
                }
            }
            spin.setFocusable(true);
            spin.setOpaque(false);
            spin.setInheritsPopupMenu(true);
            add(spin, BorderLayout.CENTER);
            super.initializeEditor();
        }
    }

    @Override
    protected void applyAlign() {
        if (spin != null && spin.getEditor() != null
                && spin.getEditor() instanceof JTextField) {
            JTextField tfEditor = (JTextField) spin.getEditor();
            applySwingAlign2TextAlign(tfEditor, align);
        }
        if (renTxtSingleLine != null) {
            renTxtSingleLine.setHorizontalAlignment(align);
        }
        super.applyAlign();
    }

    @Override
    protected void applyFont() {
        if (getFont() != null) {
            if (spin != null) {
                spin.setFont(getFont());
            }
            if (renTxtSingleLine != null) {
                renTxtSingleLine.setFont(getFont());
            }
        }
    }

    @Override
    protected void applyTooltip(String aText) {
        if (spin != null) {
            spin.setToolTipText(aText);
        }
        if (renTxtSingleLine != null) {
            renTxtSingleLine.setToolTipText(aText);
        }
    }

    @Override
    public JComponent getFocusTargetComponent() {
        if (spin != null) {
            if (spin.getEditor() instanceof JSpinner.DefaultEditor) {
                JFormattedTextField txtEditor = ((JSpinner.DefaultEditor) spin.getEditor()).getTextField();
                if (txtEditor != null) {
                    return txtEditor;
                }
            }
        }
        if (renTxtSingleLine != null) {
            return renTxtSingleLine;
        }
        return null;
    }

    @Override
    public void applyEditable2Field() {
        if (spinModel != null) {
            spinModel.setEditable(editable);
        }
        if (spin != null && spin.getEditor() instanceof JSpinner.NumberEditor) {
            JFormattedTextField tf = ((JSpinner.NumberEditor) spin.getEditor()).getTextField();
            if (tf != null) {
                tf.setEditable(editable);
            }
        }
    }

    @Override
    protected void setupEditor(JTable table) {
        if (editingValue instanceof BigInteger) {
            editingValue = new BigDecimal(((BigInteger) editingValue).longValue());
        }
        if (spinModel != null && (editingValue == null || editingValue instanceof Number)) {
            if (rowsetUpdater != null) {
                spinModel.removeValueChangeListener(rowsetUpdater);
            }
            try {
                spinModel.setValue(editingValue);
            } finally {
                if (rowsetUpdater != null) {
                    spinModel.addValueChangeListener(rowsetUpdater);
                }
            }
        }
        super.setupEditor(table);
    }

    @Override
    protected void applyEnabled() {
        if (renTxtSingleLine != null) {
            renTxtSingleLine.setEnabled(isEnabled());
        }
        if (spin != null) {
            spin.setEnabled(isEnabled());
        }
        super.applyEnabled();
    }

    @Override
    public void applyForeground() {
        if (kind != InitializingMethod.UNDEFINED) {
            if (spin != null) {
                spin.setForeground(getForeground());
                if (spin.getEditor() != null) {
                    JComponent comp = spin.getEditor();
                    comp.setBackground(getForeground());
                    for (int i = 0; i < comp.getComponentCount(); i++) {
                        if (comp.getComponent(i) != null) {
                            comp.getComponent(i).setForeground(getForeground());
                        }
                    }
                }
            }
            if (renTxtSingleLine != null) {
                renTxtSingleLine.setForeground(getForeground());
            }
        }
    }

    @Override
    public void applyBackground() {
        if (kind != InitializingMethod.UNDEFINED) {
            if (spin != null) {
                spin.setBackground(getBackground());
                if (spin.getEditor() != null) {
                    JComponent comp = spin.getEditor();
                    comp.setBackground(getBackground());
                    for (int i = 0; i < comp.getComponentCount(); i++) {
                        if (comp.getComponent(i) != null) {
                            comp.getComponent(i).setBackground(getBackground());
                        }
                    }
                }
            }
            if (renTxtSingleLine != null) {
                renTxtSingleLine.setBackground(getBackground());
            }
        }
    }

    @Override
    public void applyCursor() {
        if (kind != InitializingMethod.UNDEFINED) {
            if (spin != null) {
                spin.setCursor(getCursor());
                if (spin.getEditor() != null) {
                    JComponent comp = spin.getEditor();
                    comp.setCursor(getCursor());
                    for (int i = 0; i < comp.getComponentCount(); i++) {
                        if (comp.getComponent(i) != null) {
                            comp.getComponent(i).setCursor(getCursor());
                        }
                    }
                }
            }
            if (renTxtSingleLine != null) {
                renTxtSingleLine.setCursor(getCursor());
            }
        }
    }

    @Override
    public void applyOpaque() {
        if (kind != InitializingMethod.UNDEFINED) {
            if (spin != null) {
                spin.setOpaque(isOpaque());
                if (spin.getEditor() != null) {
                    JComponent comp = spin.getEditor();
                    comp.setOpaque(isOpaque());
                    for (int i = 0; i < comp.getComponentCount(); i++) {
                        if (comp.getComponent(i) instanceof JComponent) {
                            ((JComponent) comp.getComponent(i)).setOpaque(isOpaque());
                        }
                    }
                }
            }
            if (renTxtSingleLine != null) {
                renTxtSingleLine.setOpaque(isOpaque());
            }
        }
    }

    @Override
    public boolean isFieldContentModified() {
        return !standalone;
    }

    @Override
    public Object getCellEditorValue() {
        if (spinModel != null) {
            if (spin != null && spin.getEditor() instanceof JSpinner.NumberEditor) {
                JFormattedTextField fTxt = ((JSpinner.NumberEditor) spin.getEditor()).getTextField();
                if (fTxt != null) {
                    try {
                        fTxt.commitEdit();
                    } catch (ParseException ex) {
                        spinModel.setValue(null);
                    }
                }
            }
            return spinModel.getValue();
        } else {
            return null;
        }
    }
    // table or tree cell rendering
    private JLabel renTxtSingleLine;

    @Override
    protected void initializeRenderer() {
        if (kind != InitializingMethod.RENDERER) {
            setLayout(new BorderLayout());
            addIconLabel();
            renTxtSingleLine = new DefaultTableCellRenderer();
            renTxtSingleLine.setBorder(null);
            add(renTxtSingleLine, BorderLayout.CENTER);

            kind = InitializingMethod.RENDERER;
            applyFont();
            applyAlign();
        }
    }
    private DecimalFormat rendererDf = new DecimalFormat();

    @Override
    protected void setupRenderer(JTable table, int row, int column, boolean isSelected) {
        if (renTxtSingleLine != null) {
            String value;
            if (displayingValue != null) {
                value = displayingValue.toString();
            } else if (editingValue != null) {
                value = editingValue.toString();
                if (editingValue instanceof Float || editingValue instanceof Double || editingValue instanceof BigDecimal) {
                    value = rendererDf.format(editingValue);
                }
            } else {
                value = "";
            }
            ((DefaultTableCellRenderer) renTxtSingleLine).getTableCellRendererComponent(table, value, isSelected, false, row, column);
        }
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (renTxtSingleLine != null) {
            renTxtSingleLine.setBorder(null);
        }
        if (borderless && spin != null) {
            spin.setBorder(null);
        }
    }
    protected ChangeListener rowsetUpdater;

    public String getRendererText() {
        if (renTxtSingleLine != null) {
            return renTxtSingleLine.getText();
        }
        return null;
    }

    protected class SpinRowsetUpdater implements ChangeListener {

        SpinRowsetUpdater() {
            super();
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            if (!DbSpin.this.isUpdating()) {
                if (standalone) {
                    if (editable && rsEntity != null) {
                        try {
                            assert (spinModel != null && rsEntity != null && colIndex > 0);
                            setValue(spinModel.getValue());
                        } catch (Exception ex) {
                            Logger.getLogger(DbSpin.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else {
                    setEditingValue(spinModel.getValue());
                    fireCellEditingCompleted();
                }
            }
        }
    }
}
