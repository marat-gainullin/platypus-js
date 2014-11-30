/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components.model;

import com.bearsoft.rowset.RowsetConverter;
import com.eas.client.forms.ModelCellEditingListener;
import com.eas.client.forms.Form;
import com.eas.client.forms.IconCache;
import com.eas.client.forms.components.HasValue;
import com.eas.client.forms.components.VSpinner;
import com.eas.client.forms.events.ControlEventsIProxy;
import com.eas.gui.CascadedStyle;
import com.eas.script.EventMethod;
import com.eas.script.ScriptFunction;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import jdk.nashorn.api.scripting.JSObject;

/*
 === from setValueToRowset ===
 if (rsEntity != null && rsEntity.getRowset() != null && colIndex != 0
 && !setValue2Rowset(value)) {
 // if the value has been rejected by rowset we must
 // reflect rowset's value in the control.
 setEditingValue(getValueFromRowset());
 }
 === from setValue ===
 if (aValue instanceof Number) {
 aValue = Double.valueOf(((Number) aValue).doubleValue());
 }
 === .style and .display processing ? ===
 */
/**
 *
 * @author mg
 * @param <D>
 * @param <V>
 */
public abstract class ModelComponentDecorator<D extends JComponent, V> extends JComponent implements ModelWidget<V> {

    public static final int EXTRA_BUTTON_WIDTH = 18;
    
    protected static RowsetConverter converter = new RowsetConverter();
    protected JTextField prefSizeCalculator = new JTextField();// DON't move to design!!!
    protected JLabel iconLabel = new JLabel(" ");
    protected JToolBar extraTools = new JToolBar();
    protected Set<CellEditorListener> editorListeners = new HashSet<>();
    protected Set<ActionListener> actionListeners = new HashSet<>();
    protected D decorated;
    protected int align = SwingConstants.LEFT;
    protected JLabel gapLabel = new JLabel(" ");
    protected Icon icon;
    protected boolean selectOnly;
    private boolean design;
    // Model interacting
    protected JSObject published;
    protected static final Color WDIGETS_BORDER_COLOR = controlsBorderColor();
    public static final int WIDGETS_DEFAULT_WIDTH = 100;
    public static final int WIDGETS_DEFAULT_HEIGHT = 100;

    private static Color controlsBorderColor() {
        Color lafGridColor = UIManager.getColor("Table.gridColor");
        if (lafGridColor != null) {
            return lafGridColor;
        } else {
            return new Color(103, 144, 185);
        }
    }

    /**
     * This class is intended to substitute standard action of "enter" in case
     * of in-table controls. There are cases when "enter" action commits edited
     * text and no table cell editing stop occurs. We need to get same results
     * on enter key in both cases.
     */
    protected class TextFieldsCommitAction extends AbstractAction {

        protected JFormattedTextField field;

        public TextFieldsCommitAction(JFormattedTextField aField, Object aName) {
            super(aName instanceof String ? (String) aName : "notify-field-accept");
            field = aField;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                field.commitEdit();
                fireEditingStopped();
            } catch (ParseException ex) {
                // no op
            }
        }
    }

    public D getDecorated() {
        return decorated;
    }

    protected PropertyChangeListener decoratedValueListener = (PropertyChangeEvent evt) -> {
        firePropertyChange(VALUE_PROP_NAME, evt.getOldValue(), evt.getNewValue());
    };

    public void setDecorated(D aComponent) {
        if (decorated != null) {
            decorated.removePropertyChangeListener(VALUE_PROP_NAME, decoratedValueListener);
        }
        decorated = aComponent;
        if (decorated != null) {
            decorated.setOpaque(true);
            decorated.setBorder(null);
            decorated.setInheritsPopupMenu(true);
            checkEvents(decorated);
            decorated.addPropertyChangeListener(VALUE_PROP_NAME, decoratedValueListener);
        }
    }

    @Override
    public void injectPublished(JSObject aValue) {
        published = aValue;
    }

    @Override
    public Dimension getPreferredSize() {
        int width = super.getPreferredSize().width;
        int height = prefSizeCalculator.getPreferredSize().height;
        if (width < WIDGETS_DEFAULT_WIDTH) {
            width = WIDGETS_DEFAULT_WIDTH;
        }
        return new Dimension(width, height);
    }

    protected void initializeDesign() {
        design = true;
        setAlignmentX(0.0f);
        setLayout(new BorderLayout());
        Font f = getFont();
        if (f != null && prefSizeCalculator != null) {
            prefSizeCalculator.setFont(f);
        }
    }

    @Override
    public Dimension getMaximumSize() {
        if (design) {
            return getPreferredSize();
        } else {
            return super.getMaximumSize();
        }
    }

    public ModelComponentDecorator() {
        super();
        setOpaque(true);
        iconLabel.setOpaque(false);
        iconLabel.setBorder(null);
        iconLabel.setFocusable(false);
        iconLabel.setText("");
        iconLabel.setInheritsPopupMenu(true);
        checkEvents(iconLabel);
        //
        gapLabel.setOpaque(false);
        gapLabel.setBorder(null);
        gapLabel.setFocusable(false);
        gapLabel.setText("");
        gapLabel.setInheritsPopupMenu(true);
        checkEvents(gapLabel);
        //
        extraTools.setBorder(null);
        extraTools.setBorderPainted(false);
        extraTools.setFloatable(false);
        extraTools.setOpaque(false);
        extraTools.setFocusable(false);
        extraTools.setInheritsPopupMenu(true);
        checkEvents(extraTools);
        add(extraTools, BorderLayout.EAST);
        //
        setBorder(new LineBorder(WDIGETS_BORDER_COLOR));
        //
        initializeDesign();
    }

    public void fireCellEditingCompleted() {
        editorListeners.stream().forEach((l) -> {
            if (l instanceof ModelCellEditingListener) {
                ((ModelCellEditingListener) l).cellEditingCompleted();
            }
        });
    }

    protected final void checkEvents(Component aComp) {
        if (aComp != null) {
            ControlEventsIProxy.reflectionInvokeARListener(aComp, "addActionListener", ActionListener.class, (ActionListener) (ActionEvent e) -> {
                fireActionPerformed(e);
            });
            aComp.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    MouseListener[] mls = getMouseListeners();
                    if (mls != null && e.getSource() instanceof Component) {
                        e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, ModelComponentDecorator.this);
                        for (MouseListener ml : mls) {
                            ml.mouseClicked(e);
                        }
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    MouseListener[] mls = getMouseListeners();
                    if (mls != null && e.getSource() instanceof Component) {
                        e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, ModelComponentDecorator.this);
                        for (MouseListener ml : mls) {
                            ml.mousePressed(e);
                        }
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    MouseListener[] mls = getMouseListeners();
                    if (mls != null && e.getSource() instanceof Component) {
                        e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, ModelComponentDecorator.this);
                        for (MouseListener ml : mls) {
                            ml.mouseReleased(e);
                        }
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    MouseListener[] mls = getMouseListeners();
                    if (mls != null && e.getSource() instanceof Component) {
                        e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, ModelComponentDecorator.this);
                        for (MouseListener ml : mls) {
                            ml.mouseEntered(e);
                        }
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    MouseListener[] mls = getMouseListeners();
                    if (mls != null && e.getSource() instanceof Component) {
                        e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, ModelComponentDecorator.this);
                        for (MouseListener ml : mls) {
                            ml.mouseExited(e);
                        }
                    }
                }
            });
            aComp.addMouseMotionListener(new MouseMotionListener() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    MouseMotionListener[] mls = getMouseMotionListeners();
                    if (mls != null && e.getSource() instanceof Component) {
                        e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, ModelComponentDecorator.this);
                        for (MouseMotionListener mml : mls) {
                            mml.mouseDragged(e);
                        }
                    }
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    MouseMotionListener[] mls = getMouseMotionListeners();
                    if (mls != null && e.getSource() instanceof Component) {
                        e = SwingUtilities.convertMouseEvent((Component) e.getSource(), e, ModelComponentDecorator.this);
                        for (MouseMotionListener mml : mls) {
                            mml.mouseMoved(e);
                        }
                    }
                }
            });
            aComp.addMouseWheelListener((MouseWheelEvent e) -> {
                MouseWheelListener[] mls = getMouseWheelListeners();
                if (mls != null && e.getSource() instanceof Component) {
                    e = (MouseWheelEvent) SwingUtilities.convertMouseEvent((Component) e.getSource(), e, ModelComponentDecorator.this);
                    for (MouseWheelListener mwl : mls) {
                        mwl.mouseWheelMoved(e);
                    }
                }
            });

            aComp.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    FocusListener[] fls = getFocusListeners();
                    if (fls != null) {
                        e.setSource(ModelComponentDecorator.this);
                        for (FocusListener fl : fls) {
                            fl.focusGained(e);
                        }
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    FocusListener[] fls = getFocusListeners();
                    if (fls != null) {
                        e.setSource(ModelComponentDecorator.this);
                        for (FocusListener fl : fls) {
                            fl.focusLost(e);
                        }
                    }
                }
            });

            aComp.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                    KeyListener[] kls = getKeyListeners();
                    if (kls != null) {
                        e.setSource(ModelComponentDecorator.this);
                        for (KeyListener kl : kls) {
                            kl.keyTyped(e);
                        }
                    }
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    KeyListener[] kls = getKeyListeners();
                    if (kls != null) {
                        e.setSource(ModelComponentDecorator.this);
                        for (KeyListener kl : kls) {
                            kl.keyPressed(e);
                        }
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    KeyListener[] kls = getKeyListeners();
                    if (kls != null) {
                        e.setSource(ModelComponentDecorator.this);
                        for (KeyListener kl : kls) {
                            kl.keyReleased(e);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void updateUI() {
        Color bkCol = getBackground();
        Color fgCol = getForeground();
        try {
            super.updateUI();
        } finally {
            setBackground(bkCol);
            setForeground(fgCol);
        }
    }

    public void applyStyle(CascadedStyle aStyle) {
        if (aStyle != null) {
            setFont(aStyle.getFont());
            setBackground(aStyle.getBackground());
            setForeground(aStyle.getForeground());
            setIcon(aStyle.getIcon());
            setAlign(aStyle.getAlign());
        }
    }

    public int getAlign() {
        return align;
    }

    public void setAlign(int aAlign) {
        align = aAlign;
        if (align == SwingConstants.RIGHT) {
            add(gapLabel, BorderLayout.EAST);
        } else {
            remove(gapLabel);
        }
    }

    @Override
    public void setFocusable(boolean aValue) {
        super.setFocusable(aValue);
        decorated.setFocusable(aValue);
    }

    @Override
    public void setToolTipText(String text) {
        super.setToolTipText(text);
        gapLabel.setToolTipText(text);
        iconLabel.setToolTipText(text);
        decorated.setToolTipText(text);
        extraTools.setToolTipText(text);
    }

    protected static void applySwingAlign2TextAlign(JTextField aTf, int aAlign) {
        switch (aAlign) {
            case SwingConstants.LEFT:
                aTf.setHorizontalAlignment(JTextField.LEFT);
                break;
            case SwingConstants.RIGHT:
                aTf.setHorizontalAlignment(JTextField.RIGHT);
                break;
            case SwingConstants.CENTER:
                aTf.setHorizontalAlignment(JTextField.CENTER);
                break;
        }
    }

    @Override
    public void setCursor(Cursor cursor) {
        super.setCursor(cursor);
        decorated.setCursor(cursor);
        iconLabel.setCursor(cursor);
        gapLabel.setCursor(cursor);
        extraTools.setCursor(cursor);
    }

    @Override
    public boolean isFocusable() {
        return super.isFocusable() && isEnabled();
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon aIcon) {
        icon = aIcon;
        iconLabel.setIcon(icon);
    }

    protected void addIconLabel() {
        add(iconLabel, BorderLayout.WEST);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        gapLabel.setEnabled(enabled);
        iconLabel.setEnabled(enabled);
        decorated.setEnabled(enabled);
        extraTools.setEnabled(enabled);
        for (Component comp : extraTools.getComponents()) {
            comp.setEnabled(enabled);
        }
    }

    // binding
    protected JSObject data;
    protected String field;
    private static final String FIELD_JSDOC = ""
            + "/**\n"
            + "* Model binding field.\n"
            + "*/";

    @ScriptFunction(jsDoc = FIELD_JSDOC)
    public String getField() {
        return field;
    }

    @ScriptFunction
    public void setField(String aField) throws Exception {
        if (field == null ? aField != null : !field.equals(aField)) {
            field = aField;
            revalidate();
            repaint();
        }
    }

    private static final String ON_SELECT_JSDOC = ""
            + "/**\n"
            + "* Component's selection event handler function.\n"
            + "*/";

    @ScriptFunction(jsDoc = ON_SELECT_JSDOC)
    @Override
    public JSObject getOnSelect() {
        return onSelect;
    }

    @ScriptFunction
    @Override
    public void setOnSelect(JSObject aValue) {
        if (onSelect != aValue) {
            onSelect = aValue;
            recreateExtraEditingControls();
            revalidate();
            repaint();
        }
    }

    private static final String ON_RENDER_JSDOC = ""
            + "/**\n"
            + "* Component's rendering event handler function.\n"
            + "*/";

    @ScriptFunction(jsDoc = ON_RENDER_JSDOC)
    @EventMethod(eventClass = CellRenderEvent.class)
    @Override
    public JSObject getOnRender() {
        return onRender;
    }

    @ScriptFunction
    @Override
    public void setOnRender(JSObject aValue) {
        onRender = aValue;
    }

    @Override
    public Object getCellEditorValue() {
        return getValue();
    }

    @ScriptFunction(jsDoc = VALUE_JSDOC)
    @Override
    public V getValue() {
        if (decorated instanceof HasValue<?>) {
            return ((HasValue<V>) decorated).getValue();
        } else if (decorated instanceof VSpinner) {
            return (V) ((VSpinner) decorated).getValue();
        }
        return null;
    }

    @Override
    public void setValue(V aValue) {
        if (decorated instanceof HasValue<?>) {
            ((HasValue<V>) decorated).setValue(aValue);
        } else if (decorated instanceof VSpinner) {
            ((VSpinner) decorated).setValue((Double) aValue);
        }
    }

    public void removeValueChangeListener(PropertyChangeListener listener) {
        super.removePropertyChangeListener(VALUE_PROP_NAME, listener);
    }

    @Override
    public void addValueChangeListener(PropertyChangeListener listener) {
        super.removePropertyChangeListener(VALUE_PROP_NAME, listener);
        super.addPropertyChangeListener(VALUE_PROP_NAME, listener);
    }

    private static final String VALUE_PROP_NAME = "value";

    protected String error;
    protected JPanel errorPanel = new JPanel();

    private static final String ERROR_JSDOC = ""
            + "/**\n"
            + "* Widget's error message.\n"
            + "*/";

    @ScriptFunction(jsDoc = ERROR_JSDOC)
    public String getError() {
        return error;
    }

    public void setError(String aValue) {
        if (error == null ? aValue != null : !error.equals(aValue)) {
            error = aValue;
            remove(errorPanel);
            if (aValue != null) {
                errorPanel.setBackground(Color.red);
                errorPanel.setToolTipText(aValue);
                errorPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 2));
                add(errorPanel, BorderLayout.SOUTH);
            }
            revalidate();
            repaint();
        }
    }

    private static final String REDRAW_JSDOC = ""
            + "/**\n"
            + "* Redraw the component.\n"
            + "*/";

    @ScriptFunction(jsDoc = REDRAW_JSDOC)
    public void redraw() {
        revalidate();
        repaint();
    }

    protected abstract void setupCellRenderer(JTable table, int row, int column, boolean isSelected);

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        try {
            silent = true;
            extraTools.setVisible(false);
            setValue((V) value);
            setupCellRenderer(table, row, column, isSelected);
            if (hasFocus) {
                setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
            } else {
                setBorder(null);
            }
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setOpaque(true);
            } else {
                setBackground(table.getBackground());
                setOpaque(false);
            }
            return this;
        } catch (Exception ex) {
            Logger.getLogger(ModelComponentDecorator.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return this;
        }
    }

    protected PropertyChangeListener cellEditingCompletedAlerter = (PropertyChangeEvent evt) -> {
        fireCellEditingCompleted();
    };

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        try {
            removeValueChangeListener(cellEditingCompletedAlerter);
            setValue((V) value);
            addValueChangeListener(cellEditingCompletedAlerter);
            EventQueue.invokeLater(() -> {
                extraTools.setVisible(true);
            });
            return this;
        } catch (Exception ex) {
            Logger.getLogger(ModelComponentDecorator.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return this;
        }
    }

    @Override
    public boolean isCellEditable(EventObject event) {
        return (event instanceof MouseEvent && ((MouseEvent) event).getClickCount() > 1)
                || (event instanceof KeyEvent && ((KeyEvent) event).getKeyCode() == KeyEvent.VK_F2)
                || (event instanceof ActionEvent);
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        return true;
    }

    @Override
    public boolean stopCellEditing() {
        if (isFieldContentModified()) {
            fireEditingStopped();
        } else {
            fireEditingCancelled();
        }
        EventQueue.invokeLater(() -> {
            extraTools.setVisible(false);
        });
        return true;
    }

    @Override
    public void cancelCellEditing() {
        fireEditingCancelled();
        EventQueue.invokeLater(() -> {
            extraTools.setVisible(false);
        });
    }

    protected void fireEditingStopped() {
        for (CellEditorListener l : editorListeners.toArray(new CellEditorListener[]{})) {
            if (l != null) {
                l.editingStopped(new ChangeEvent(this));
            }
        }
    }

    protected void fireEditingCancelled() {
        for (CellEditorListener l : editorListeners.toArray(new CellEditorListener[]{})) {
            if (l != null) {
                l.editingCanceled(new ChangeEvent(this));
            }
        }
    }

    @Override
    public void addCellEditorListener(CellEditorListener l) {
        editorListeners.add(l);
    }

    @Override
    public void removeCellEditorListener(CellEditorListener l) {
        editorListeners.remove(l);
    }

    public void addActionListener(ActionListener l) {
        actionListeners.add(l);
    }

    public void removeActionListener(ActionListener l) {
        actionListeners.add(l);
    }

    protected void fireActionPerformed(ActionEvent e) {
        e.setSource(this);
        actionListeners.stream().forEach((l) -> {
            l.actionPerformed(e);
        });
    }

    @Override
    public boolean isSelectOnly() {
        return selectOnly;
    }

    @Override
    public void setSelectOnly(boolean aValue) {
        selectOnly = aValue;
    }

    @Override
    public void setFont(Font aValue) {
        super.setFont(aValue);
        decorated.setFont(aValue);
        Font f = getFont();
        if (f != null && prefSizeCalculator != null) {
            prefSizeCalculator.setFont(f);
        }
    }

    protected JSObject onSelect;
    protected JSObject onRender;
    protected boolean nullable = true;

    public boolean getNullable() {
        return nullable;
    }

    public void setNullable(boolean aValue) {
        if (nullable != aValue) {
            nullable = aValue;
            recreateExtraEditingControls();
        }
    }

    protected void recreateExtraEditingControls() {
        extraTools.removeAll();
        if (onSelect != null) {
            JButton btnSelectingField = new JButton();
            btnSelectingField.setAction(new ModelComponentDecorator.ValueSelectorAction(onSelect));
            btnSelectingField.setPreferredSize(new Dimension(EXTRA_BUTTON_WIDTH, EXTRA_BUTTON_WIDTH));
            btnSelectingField.setFocusable(false);
            extraTools.add(btnSelectingField);
        }
        if (nullable) {
            JButton btnNullingField = new JButton();
            btnNullingField.setAction(new NullerAction());
            btnNullingField.setPreferredSize(new Dimension(EXTRA_BUTTON_WIDTH, EXTRA_BUTTON_WIDTH));
            btnNullingField.setFocusable(false);
            extraTools.add(btnNullingField);
        }
        for (Component comp : extraTools.getComponents()) {
            comp.setEnabled(isEnabled());
            checkEvents(comp);
        }
    }

    public boolean haveSelectorAction() {
        for (Component comp : extraTools.getComponents()) {
            if (comp instanceof AbstractButton) {
                AbstractButton ab = (AbstractButton) comp;
                if (ab.getAction() instanceof ModelComponentDecorator.ValueSelectorAction) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean haveNullerAction() {
        for (Component comp : extraTools.getComponents()) {
            if (comp instanceof AbstractButton) {
                AbstractButton ab = (AbstractButton) comp;
                if (ab.getAction() instanceof ModelComponentDecorator.NullerAction) {
                    return true;
                }
            }
        }
        return false;
    }

    private class NullerAction extends AbstractAction {

        NullerAction() {
            super();
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0));
            putValue(Action.SHORT_DESCRIPTION, Form.getLocalizedString(NullerAction.class.getSimpleName()));
            putValue(Action.SMALL_ICON, IconCache.getIcon("16x16/delete.png"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                setValue(null);
            } catch (Exception ex) {
                Logger.getLogger(ModelComponentDecorator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private class ValueSelectorAction extends AbstractAction {

        protected JSObject selector;

        ValueSelectorAction(JSObject aSelector) {
            super();
            putValue(Action.NAME, "...");
            putValue(Action.SHORT_DESCRIPTION, Form.getLocalizedString(ValueSelectorAction.class.getSimpleName()));
            selector = aSelector;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (selector != null && published != null) {
                try {
                    selector.call(published, new Object[]{published});
                } catch (Exception ex) {
                    Logger.getLogger(ModelComponentDecorator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    protected boolean silent;

    @Override
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (!silent) {
            super.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    @Override
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        if (!silent) {
            super.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    /**
     * Overridden for performance reasons. See the <a
     * href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
        if (!silent) {
            super.repaint(tm, x, y, width, height);
        }
    }
}
