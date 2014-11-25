/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.label;

import com.eas.client.SQLUtils;
import com.eas.dbcontrols.DbControl;
import com.eas.client.forms.api.components.model.DbControlPanel;
import com.eas.client.forms.IconCache;
import com.eas.dbcontrols.InitializingMethod;
import com.eas.dbcontrols.label.rt.ModifiableTextField;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;

/**
 *
 * @author mg
 */
public class DbLabel extends DbControlPanel implements DbControl {

    public String getRendererText() {
        if (singlelineText != null) {
            return singlelineText.getText();
        }
        return null;
    }

    protected class TextCommitAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (standalone) {
                try {
                    singlelineText.commitEdit();
                    setValue(singlelineText.getValue());
                } catch (Exception ex) {
                    Logger.getLogger(DbLabel.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                // Editing stopped event is used here, because otherwise
                // any grid unexpectedly jumps to next row just after editing stopped/completed.
                // If you whant to change back, you should check grid's interrows jumping.
                fireEditingStopped();
                //fireCellEditingCompleted();
            }
        }
    }

    public DbLabel() {
        super();
    }

    @Override
    protected void initializeDesign() {
        if (kind == InitializingMethod.UNDEFINED
                && getComponentCount() == 0) {
            super.initializeDesign();
            designLabel = new JLabel("ModelText", IconCache.getIcon("16x16/label.png"), SwingConstants.LEADING);
            designLabel.setOpaque(false);
            designLabel.setFont(getFont());
            add(designLabel, BorderLayout.CENTER);
        }
    }
    protected AbstractFormatterFactory formatterFactory;
    protected JLabel designLabel;
    protected ModifiableTextField singlelineText;
    protected TextCommitAction textCommitAction = new TextCommitAction();

    public AbstractFormatterFactory getFormatterFactory() {
        return formatterFactory;
    }

    public void setFormatterFactory(AbstractFormatterFactory aValue) throws Exception {
        if (formatterFactory != aValue) {
            formatterFactory = aValue;
        }
    }

    @Override
    protected void applyEnabled() {
        if (singlelineText != null) {
            singlelineText.setEnabled(isEnabled());
        }
        super.applyEnabled();
    }

    @Override
    public void applyEditable2Field() {
        if (singlelineText != null) {
            singlelineText.setEditable(editable);
        }
    }

    @Override
    public void applyForeground() {
        if (kind != InitializingMethod.UNDEFINED) {
            if (singlelineText != null) {
                singlelineText.setForeground(getForeground());
            }
        }
    }

    @Override
    public void applyBackground() {
        if (kind != InitializingMethod.UNDEFINED) {
            if (singlelineText != null) {
                singlelineText.setBackground(getBackground());
            }
        }
    }

    @Override
    public void applyCursor() {
        if (kind != InitializingMethod.UNDEFINED) {
            if (singlelineText != null) {
                singlelineText.setCursor(getCursor());
            }
        }
    }

    @Override
    public void applyOpaque() {
        if (kind != InitializingMethod.UNDEFINED) {
            if (singlelineText != null) {
                singlelineText.setOpaque(isOpaque());
            }
        }
    }

    @Override
    public Object getCellEditorValue() {
        if (kind == InitializingMethod.EDITOR) {
            try {
                singlelineText.commitEdit();
                editingValue = singlelineText.getValue();
            } catch (ParseException ex) {
                editingValue = null;
            }

            if (editingValue instanceof String && ((String) editingValue).isEmpty()) {
                editingValue = null;
            }
            return editingValue;
        }
        return null;
    }

    @Override
    protected void initializeRenderer() {
        if (kind != InitializingMethod.RENDERER) {
            kind = InitializingMethod.RENDERER;
            removeAll();
            setLayout(new BorderLayout());
            addIconLabel();
            singlelineText = new ModifiableTextField(kind, formatterFactory, borderless);
            singlelineText.setFont(getFont());
            add(singlelineText, BorderLayout.CENTER);
            applyAlign();
            applyFont();
        }
    }

    @Override
    protected void setupRenderer(JTable table, int row, int column, boolean isSelected) {
        if (kind == InitializingMethod.RENDERER) {
            //try {
                String sValue = displayingValue != null ? displayingValue.toString() : SQLUtils.convertJDBCObject2String(editingValue);
                if (table != null) {
                    if (isSelected) {
                        singlelineText.setBackground(table.getSelectionBackground());
                        singlelineText.setForeground(table.getSelectionForeground());
                    } else {
                        singlelineText.setBackground(table.getBackground());
                        singlelineText.setForeground(table.getForeground());
                    }
                }
                if (singlelineText.isTextValue()) {
                    singlelineText.setValue(sValue);
                } else {
                    singlelineText.setValue(editingValue);
                }
                //singlelineText.setText(singlelineText.getFormatter().valueToString(singlelineText.getValue()));
            //} catch (ParseException ex) {
            //    Logger.getLogger(DbLabel.class.getName()).log(Level.SEVERE, null, ex);
            //}
        }
    }

    @Override
    protected void initializeEditor() {
        if (kind != InitializingMethod.EDITOR) {
            kind = InitializingMethod.EDITOR;
            removeAll();
            setLayout(new BorderLayout());
            addIconLabel();
            singlelineText = new ModifiableTextField(kind, formatterFactory, borderless);
            if (borderless) {
                singlelineText.setBorder(null);
            }
            singlelineText.setAction(textCommitAction);
            checkEvents(singlelineText);
            singlelineText.setOpaque(false);
            singlelineText.setInheritsPopupMenu(true);
            //singlelineText.setOpaque(standalone && ldi.isOpaque());
            add(singlelineText, BorderLayout.CENTER);
            super.initializeEditor();
        }
    }

    @Override
    protected void setupEditor(JTable table) {
        if (kind == InitializingMethod.EDITOR) {
            String sValue = displayingValue != null ? displayingValue.toString() : SQLUtils.convertJDBCObject2String(editingValue);
            if (singlelineText.isTextValue()) {
                singlelineText.setValue(sValue);
            } else {
                singlelineText.setValue(editingValue);
            }
            singlelineText.setModified(false);
        }
        super.setupEditor(table);
    }

    @Override
    public void setEditingValue(Object aValue) {
        initializeEditor();
        super.setEditingValue(aValue);
    }

    @Override
    protected void applyFont() {
        if (singlelineText != null) {
            singlelineText.setFont(getFont());
        }
        if (designLabel != null) {
            designLabel.setFont(getFont());
        }
    }

    @Override
    public JComponent getFocusTargetComponent() {
        if (singlelineText != null) {
            return singlelineText;
        }
        return null;
    }

    @Override
    protected void applyAlign() {
        if (singlelineText != null) {
            applySwingAlign2TextAlign(singlelineText, align);
        }
        super.applyAlign();
    }

    @Override
    protected void applyTooltip(String aText) {
        if (singlelineText != null) {
            singlelineText.setToolTipText(aText);
        }
    }

    @Override
    public boolean isFieldContentModified() {
        if (kind == InitializingMethod.EDITOR && editable) {
            if (editingValue != null) {
                if (singlelineText != null) {
                    return singlelineText.isModified();
                }
            } else {
                return true;
            }
        }
        return false;
    }    
    
    protected String emptyText;
    
    public String getEmptyText() {
        return emptyText;
    }

    public void setEmptyText(String aValue) {
        String oldValue = emptyText;
        emptyText = aValue;
        firePropertyChange("emptyText", oldValue, emptyText);
    }

}
