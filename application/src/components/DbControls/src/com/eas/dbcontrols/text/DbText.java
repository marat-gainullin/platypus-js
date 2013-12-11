/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.text;

import com.eas.client.SQLUtils;
import com.eas.dbcontrols.DbControl;
import com.eas.dbcontrols.DbControlPanel;
import com.eas.dbcontrols.IconCache;
import com.eas.dbcontrols.InitializingMethod;
import com.eas.dbcontrols.label.rt.ModifiableTextArea;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.FocusEvent;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;

/**
 *
 * @author mg
 */
public class DbText extends DbControlPanel implements DbControl {

    protected JLabel designLabel;
    protected JTextComponent multilineText;

    public DbText() {
        super();
    }

    @Override
    public void initializeDesign() {
        if (kind == InitializingMethod.UNDEFINED
                && getComponentCount() == 0) {
            setLayout(new BorderLayout());
            designLabel = new JLabel("ModelTextArea", IconCache.getIcon("16x16/text.png"), SwingConstants.LEADING);
            designLabel.setOpaque(false);
            designLabel.setFont(getFont());
            add(designLabel, BorderLayout.CENTER);
        }
    }

    @Override
    protected void applyEnabled() {
        if (multilineText != null) {
            multilineText.setEnabled(isEnabled());
        }
        super.applyEnabled();
    }

    @Override
    public void applyEditable2Field() {
        if (multilineText != null) {
            multilineText.setEditable(editable);
        }
    }

    @Override
    public void applyForeground() {
        if (kind != InitializingMethod.UNDEFINED) {
            if (multilineText != null) {
                multilineText.setForeground(getForeground());
            }
        }
    }

    @Override
    public void applyBackground() {
        if (kind != InitializingMethod.UNDEFINED) {
            if (multilineText != null) {
                multilineText.setBackground(getBackground());
            }
        }
    }

    @Override
    public void applyCursor() {
        if (kind != InitializingMethod.UNDEFINED) {
            if (multilineText != null) {
                multilineText.setCursor(getCursor());
            }
        }
    }

    @Override
    public void applyOpaque() {
        if (kind != InitializingMethod.UNDEFINED) {
            if (multilineText != null) {
                multilineText.setOpaque(isOpaque());
            }
        }
    }

    @Override
    public Object getCellEditorValue() {
        if (kind == InitializingMethod.EDITOR) {
            editingValue = multilineText.getText();
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
            multilineText = createMultilineTextControl();
            multilineText.setFont(getFont());
            add(multilineText, BorderLayout.CENTER);
            applyAlign();
            applyFont();
        }
    }

    @Override
    protected void setupRenderer(JTable table, int row, int column, boolean isSelected) {
        if (kind == InitializingMethod.RENDERER) {
            String sValue = displayingValue != null ? displayingValue.toString() : SQLUtils.convertJDBCObject2String(editingValue);
            if (table != null) {
                if (isSelected) {
                    multilineText.setBackground(table.getSelectionBackground());
                    multilineText.setForeground(table.getSelectionForeground());
                } else {
                    multilineText.setBackground(table.getBackground());
                    multilineText.setForeground(table.getForeground());
                }
            }
            multilineText.setText(sValue);
        }
    }

    @Override
    protected void initializeEditor() {
        if (kind != InitializingMethod.EDITOR) {
            kind = InitializingMethod.EDITOR;
            removeAll();
            setLayout(new BorderLayout());
            addIconLabel();
            multilineText = createMultilineTextControl();
            JScrollPane scroll = new JScrollPane(multilineText);
            if (borderless) {
                scroll.setBorder(null);
            }
            checkEvents(multilineText);
            multilineText.setOpaque(false);
            multilineText.setInheritsPopupMenu(true);
            add(scroll, BorderLayout.CENTER);
            super.initializeEditor();
        }
    }

    protected class MultiLineFocusCommitter extends FocusCommitter {

        public MultiLineFocusCommitter() {
            super();
        }

        @Override
        public void focusLost(FocusEvent e) {
            assert multilineText != null;
            editingValue = multilineText.getText();
            super.focusLost(e);
        }
    }

    @Override
    protected void initializeFocusListener() {
        if (standalone) {
            Component focusTarget = getFocusTargetComponent();
            if (focusTarget != null) {
                focusTarget.addFocusListener(new MultiLineFocusCommitter());
            }
        } else {
            super.initializeFocusListener();
        }
    }

    @Override
    protected void setupEditor(JTable table) {
        if (kind == InitializingMethod.EDITOR) {
            String sValue = displayingValue != null ? displayingValue.toString() : SQLUtils.convertJDBCObject2String(editingValue);
            multilineText.setText(sValue);
        }
        super.setupEditor(table);
    }

    @Override
    protected void applyFont() {
        if (multilineText != null) {
            multilineText.setFont(getFont());
        }
        if (designLabel != null) {
            designLabel.setFont(getFont());
        }
    }

    @Override
    public JComponent getFocusTargetComponent() {
        if (multilineText != null) {
            return multilineText;
        }
        return null;
    }

    @Override
    protected void applyTooltip(String aText) {
        if (multilineText != null) {
            multilineText.setToolTipText(aText);
        }
    }

    @Override
    public boolean isFieldContentModified() {
        if (kind == InitializingMethod.EDITOR && editable) {
            if (editingValue != null) {
                if (multilineText != null) {
                    return isMultilineTextModified(multilineText);
                }
            } else {
                return true;
            }
        }
        return false;
    }

    protected boolean isMultilineTextModified(JTextComponent aMultilineText) {
        if (aMultilineText != null && editable) {
            return ((ModifiableTextArea) aMultilineText).isModified();
        }
        return false;
    }

    protected JTextComponent createMultilineTextControl() {
        return new ModifiableTextArea(kind, borderless);
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
