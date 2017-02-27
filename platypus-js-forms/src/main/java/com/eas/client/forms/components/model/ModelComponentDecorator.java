/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model;

import com.eas.client.forms.events.CellRenderEvent;
import com.eas.client.forms.ModelCellEditingListener;
import com.eas.client.forms.Forms;
import com.eas.client.forms.HasComponentEvents;
import com.eas.client.forms.HasOnValueChange;
import com.eas.client.forms.IconCache;
import com.eas.client.forms.Widget;
import com.eas.client.forms.components.rt.HasValue;
import com.eas.client.forms.components.rt.VSpinner;
import com.eas.client.forms.events.ComponentEvent;
import com.eas.client.forms.events.ValueChangeEvent;
import com.eas.client.forms.events.rt.ControlEventsIProxy;
import com.eas.client.forms.layouts.MarginLayout;
import com.eas.design.Designable;
import com.eas.design.Undesignable;
import com.eas.script.AlreadyPublishedException;
import com.eas.script.EventMethod;
import com.eas.script.ScriptFunction;
import com.eas.script.Scripts;
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
import javax.swing.JPopupMenu;
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
import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.JSType;

/**
 *
 * @author mg
 * @param <D>
 * @param <V>
 */
public abstract class ModelComponentDecorator<D extends JComponent, V> extends JPanel implements HasOnValueChange, Widget, ModelWidget<V>, HasComponentEvents {

    public static final int EXTRA_BUTTON_WIDTH = 18;

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

        public static final String COMMIT_ACTION_NAME = "notify-field-accept";
        protected JFormattedTextField field;

        public TextFieldsCommitAction(JFormattedTextField aField) {
            super(COMMIT_ACTION_NAME);
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
        if (decorated != aComponent) {
            if (decorated != null) {
                decorated.removePropertyChangeListener(VALUE_PROP_NAME, decoratedValueListener);
                remove(decorated);
            }
            decorated = aComponent;
            if (decorated != null) {
                decorated.setForeground(getForeground());
                decorated.setFont(getFont());
                decorated.setOpaque(false);
                decorated.setBorder(null);
                decorated.setInheritsPopupMenu(true);
                add(decorated, BorderLayout.CENTER);
                checkEvents(decorated);
                decorated.addPropertyChangeListener(VALUE_PROP_NAME, decoratedValueListener);
            }
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

    public ModelComponentDecorator() {
        super();
        setLayout(new BorderLayout());
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
        recreateExtraEditingControls();
        checkEvents(extraTools);
        add(extraTools, BorderLayout.EAST);
        //
        setBorder(new LineBorder(WDIGETS_BORDER_COLOR));
    }

    public void fireCellEditingCompleted() {
        for (CellEditorListener l : editorListeners.toArray(new CellEditorListener[]{})) {
            if (l instanceof ModelCellEditingListener) {
                ((ModelCellEditingListener) l).cellEditingCompleted();
            }
        };
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

    @ScriptFunction
    @Undesignable
    @Override
    public String getName() {
        return super.getName();
    }

    public int getAlign() {
        return align;
    }

    public void setAlign(int aAlign) {
        if (align != aAlign) {
            remove(gapLabel);
            align = aAlign;
            if (align == SwingConstants.RIGHT) {
                add(gapLabel, BorderLayout.EAST);
            }
        }
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

    @ScriptFunction(jsDoc = COMPONENT_POPUP_MENU_JSDOC)
    @Override
    public JPopupMenu getComponentPopupMenu() {
        return super.getComponentPopupMenu();
    }

    @ScriptFunction
    @Override
    public void setComponentPopupMenu(JPopupMenu aMenu) {
        super.setComponentPopupMenu(aMenu);
    }

    @ScriptFunction
    @Override
    public Cursor getCursor() {
        return super.getCursor();
    }

    @ScriptFunction
    @Override
    public void setCursor(Cursor cursor) {
        super.setCursor(cursor);
        decorated.setCursor(cursor);
        iconLabel.setCursor(cursor);
        gapLabel.setCursor(cursor);
        extraTools.setCursor(cursor);
    }

    @ScriptFunction
    @Override
    public boolean isFocusable() {
        return super.isFocusable() && isEnabled();
    }

    @ScriptFunction
    public Icon getIcon() {
        return icon;
    }

    @ScriptFunction
    public void setIcon(Icon aIcon) {
        icon = aIcon;
        iconLabel.setIcon(icon);
    }

    protected void addIconLabel() {
        add(iconLabel, BorderLayout.WEST);
    }

    @ScriptFunction(jsDoc = VISIBLE_JSDOC)
    @Override
    public boolean getVisible() {
        return super.isVisible();
    }

    @ScriptFunction
    @Override
    public void setVisible(boolean aValue) {
        super.setVisible(aValue);
    }

    @ScriptFunction(jsDoc = FOCUSABLE_JSDOC)
    @Override
    public boolean getFocusable() {
        return super.isFocusable();
    }

    @ScriptFunction
    @Override
    public void setFocusable(boolean aValue) {
        super.setFocusable(aValue);
        decorated.setFocusable(aValue);
    }

    @ScriptFunction(jsDoc = ENABLED_JSDOC)
    @Override
    public boolean getEnabled() {
        return super.isEnabled();
    }

    @ScriptFunction
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

    @ScriptFunction(jsDoc = TOOLTIP_TEXT_JSDOC)
    @Override
    public String getToolTipText() {
        return super.getToolTipText();
    }

    @ScriptFunction
    @Override
    public void setToolTipText(String text) {
        super.setToolTipText(text);
        gapLabel.setToolTipText(text);
        iconLabel.setToolTipText(text);
        decorated.setToolTipText(text);
        extraTools.setToolTipText(text);
    }

    @ScriptFunction(jsDoc = OPAQUE_TEXT_JSDOC)
    @Override
    public boolean getOpaque() {
        return super.isOpaque();
    }

    @ScriptFunction
    @Override
    public void setOpaque(boolean aValue) {
        super.setOpaque(aValue);
    }

    @ScriptFunction
    @Override
    public JComponent getNextFocusableComponent() {
        return (JComponent) super.getNextFocusableComponent();
    }

    @ScriptFunction
    @Override
    public void setNextFocusableComponent(JComponent aValue) {
        super.setNextFocusableComponent(aValue);
    }

    @ScriptFunction(jsDoc = LEFT_JSDOC)
    @Override
    public int getLeft() {
        return super.getLocation().x;
    }

    @ScriptFunction
    @Override
    public void setLeft(int aValue) {
        if (super.getParent() != null && super.getParent().getLayout() instanceof MarginLayout) {
            MarginLayout.ajustLeft(this, aValue);
        }
        super.setLocation(aValue, getTop());
    }

    @ScriptFunction(jsDoc = TOP_JSDOC)
    @Override
    public int getTop() {
        return super.getLocation().y;
    }

    @ScriptFunction
    @Override
    public void setTop(int aValue) {
        if (super.getParent() != null && super.getParent().getLayout() instanceof MarginLayout) {
            MarginLayout.ajustTop(this, aValue);
        }
        super.setLocation(getLeft(), aValue);
    }

    @ScriptFunction(jsDoc = WIDTH_JSDOC)
    @Override
    public int getWidth() {
        return super.getWidth();
    }

    @ScriptFunction
    @Override
    public void setWidth(int aValue) {
        Widget.setWidth(this, aValue);
    }

    @ScriptFunction(jsDoc = HEIGHT_JSDOC)
    @Override
    public int getHeight() {
        return super.getHeight();
    }

    @ScriptFunction
    @Override
    public void setHeight(int aValue) {
        Widget.setHeight(this, aValue);
    }

    @ScriptFunction(jsDoc = FOCUS_JSDOC)
    @Override
    public void focus() {
        super.requestFocus();
    }

    // Native API
    @ScriptFunction(jsDoc = NATIVE_COMPONENT_JSDOC)
    @Undesignable
    @Override
    public JComponent getComponent() {
        return this;
    }

    @ScriptFunction(jsDoc = NATIVE_ELEMENT_JSDOC)
    @Undesignable
    @Override
    public Object getElement() {
        return null;
    }

    // binding
    protected JSObject data;
    protected String field;
    protected JSObject boundToData;
    protected PropertyChangeListener boundToValue;

    private static final String FIELD_JSDOC = ""
            + "/**\n"
            + " * Model binding field.\n"
            + " */";

    @ScriptFunction(jsDoc = FIELD_JSDOC)
    @Designable(category = "model")
    @Override
    public String getField() {
        return field;
    }

    @ScriptFunction
    @Override
    public void setField(String aField) throws Exception {
        if (field == null ? aField != null : !field.equals(aField)) {
            unbind();
            field = aField;
            bind();
        }
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Object, bound to the widget.\n"
            + " */")
    @Designable(category = "model")
    @Override
    public JSObject getData() {
        return data;
    }

    @ScriptFunction
    @Override
    public void setData(JSObject aValue) {
        if (data != null ? !data.equals(aValue) : aValue != null) {
            unbind();
            data = aValue;
            bind();
        }
    }

    protected boolean settingValueFromJs;
    protected boolean settingValueToJs;

    protected void bind() {
        if (data != null && field != null && !field.isEmpty() && Scripts.isInitialized()) {
            boundToData = Scripts.getSpace().listen(data, field, new AbstractJSObject() {

                @Override
                public Object call(Object thiz, Object... args) {
                    rebind();
                    return null;
                }

            });
            Object oData = ModelWidget.getPathData(data, field);
            setJsValue(oData);
            boundToValue = (PropertyChangeEvent evt) -> {
                if (!settingValueFromJs) {
                    settingValueToJs = true;
                    try {
                        ModelWidget.setPathData(data, field, Scripts.getSpace().toJs(evt.getNewValue()));
                    } finally {
                        settingValueToJs = false;
                    }
                }
            };
            addValueChangeListener(boundToValue);
        } else {
            setJsValue(null);
        }
    }

    private void rebind() {
        if (!settingValueToJs) {
            settingValueFromJs = true;
            try {
                Object newValue = ModelWidget.getPathData(data, field);
                setJsValue(JSType.nullOrUndefined(newValue) ? null : newValue);
            } finally {
                settingValueFromJs = false;
            }
        }
    }

    protected void unbind() {
        boolean wasBoundToData = boundToData != null;
        if (boundToData != null) {
            Scripts.unlisten(boundToData);
            boundToData = null;
        }
        if (boundToValue != null) {
            removeValueChangeListener(boundToValue);
            boundToValue = null;
        }
        if (wasBoundToData) {
            setValue(null);
        }
    }

    private static final String ON_SELECT_JSDOC = ""
            + "/**\n"
            + "* Component's selection event handler function.\n"
            + "*/";

    @ScriptFunction(jsDoc = ON_SELECT_JSDOC)
    @Undesignable
    @Override
    public JSObject getOnSelect() {
        return onSelect;
    }

    @ScriptFunction
    @Override
    public void setOnSelect(JSObject aValue) {
        if (onSelect != null ? !onSelect.equals(aValue) : aValue != null) {
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
    @Undesignable
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

    @Undesignable
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

    @Override
    public Object getJsValue() {
        return Scripts.getSpace() != null ? Scripts.getSpace().toJs(getValue()) : getValue();
    }

    @Override
    public abstract void setJsValue(Object aValue);

    @Override
    public void addValueChangeListener(PropertyChangeListener listener) {
        super.removePropertyChangeListener(VALUE_PROP_NAME, listener);
        super.addPropertyChangeListener(VALUE_PROP_NAME, listener);
    }

    @Override
    public void removeValueChangeListener(PropertyChangeListener listener) {
        super.removePropertyChangeListener(VALUE_PROP_NAME, listener);
    }

    protected String error;
    protected JPanel errorPanel = new JPanel();

    @ScriptFunction(jsDoc = ERROR_JSDOC)
    @Override
    public String getError() {
        return error;
    }

    @ScriptFunction
    @Override
    public void setError(String aValue) {
        if (error == null ? aValue != null : !error.equals(aValue)) {
            error = aValue;
            remove(errorPanel);
            if (error != null) {
                errorPanel.setBackground(Color.red);
                errorPanel.setToolTipText(aValue);
                errorPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 2));
                add(errorPanel, BorderLayout.SOUTH);
            }
            revalidate();
            repaint();
        }
    }

    @ScriptFunction(jsDoc = BACKGROUND_JSDOC)
    @Override
    public Color getBackground() {
        return super.getBackground();
    }

    @ScriptFunction
    @Override
    public void setBackground(Color aValue) {
        super.setBackground(aValue);
    }

    @ScriptFunction(jsDoc = FOREGROUND_JSDOC)
    @Override
    public Color getForeground() {
        return super.getForeground();
    }

    @ScriptFunction
    @Override
    public void setForeground(Color aValue) {
        super.setForeground(aValue);
        if (gapLabel != null) {
            gapLabel.setForeground(aValue);
        }
        if (iconLabel != null) {
            iconLabel.setForeground(aValue);
        }
        if (extraTools != null) {
            extraTools.setForeground(aValue);
        }
        if (decorated != null) {
            decorated.setForeground(aValue);
        }
    }

    private static final String REDRAW_JSDOC = ""
            + "/**\n"
            + " * Redraw the component.\n"
            + " */";

    @ScriptFunction(jsDoc = REDRAW_JSDOC)
    public void redraw() {
        rebind();
        revalidate();
        repaint();
    }

    protected abstract void setupCellRenderer(JTable table, int row, int column, boolean isSelected);

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        try {
            setOpaque(true);
            silent = true;
            extraTools.setVisible(false);
            setJsValue((V) value);
            setupCellRenderer(table, row, column, isSelected);
            if (hasFocus) {
                setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
            } else {
                setBorder(null);
            }
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
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
            setJsValue((V) value);
            addValueChangeListener(cellEditingCompletedAlerter);
            extraTools.setVisible(false);
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
        fireEditingStopped();
        return true;
    }

    @Override
    public void cancelCellEditing() {
        fireEditingCancelled();
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

    @ScriptFunction
    @Override
    public boolean isSelectOnly() {
        return selectOnly;
    }

    @ScriptFunction
    @Override
    public void setSelectOnly(boolean aValue) {
        selectOnly = aValue;
    }

    @ScriptFunction
    @Override
    public Font getFont() {
        return super.getFont();
    }

    @ScriptFunction
    @Override
    public void setFont(Font aValue) {
        super.setFont(aValue);
        if (gapLabel != null) {
            gapLabel.setFont(aValue);
        }
        if (iconLabel != null) {
            iconLabel.setFont(aValue);
        }
        if (extraTools != null) {
            extraTools.setFont(aValue);
        }
        if (decorated != null) {
            decorated.setFont(aValue);
        }
        Font f = getFont();
        if (f != null && prefSizeCalculator != null) {
            prefSizeCalculator.setFont(f);
        }
    }

    protected JSObject onSelect;
    protected JSObject onRender;
    protected boolean nullable = true;

    @ScriptFunction
    @Override
    public boolean getNullable() {
        return nullable;
    }

    @ScriptFunction
    @Override
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
            //btnNullingField.setFocusable(false);
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
            putValue(Action.SHORT_DESCRIPTION, Forms.getLocalizedString(NullerAction.class.getSimpleName()));
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
            putValue(Action.SHORT_DESCRIPTION, Forms.getLocalizedString(ValueSelectorAction.class.getSimpleName()));
            selector = aSelector;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (selector != null && getPublished() != null) {
                try {
                    selector.call(getPublished(), new Object[]{getPublished()});
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

    public abstract JSObject getPublished();

    public void setPublished(JSObject aValue) {
        if (published != null) {
            throw new AlreadyPublishedException();
        }
        published = aValue;
    }

    protected ControlEventsIProxy eventsProxy = new ControlEventsIProxy(this);

    @ScriptFunction(jsDoc = ON_MOUSE_CLICKED_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.events.MouseEvent.class)
    @Undesignable
    @Override
    public JSObject getOnMouseClicked() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.mouseClicked);
    }

    @ScriptFunction
    @Override
    public void setOnMouseClicked(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.mouseClicked, aValue);
    }

    @ScriptFunction(jsDoc = ON_MOUSE_DRAGGED_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.events.MouseEvent.class)
    @Undesignable
    @Override
    public JSObject getOnMouseDragged() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.mouseDragged);
    }

    @ScriptFunction
    @Override
    public void setOnMouseDragged(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.mouseDragged, aValue);
    }

    @ScriptFunction(jsDoc = ON_MOUSE_ENTERED_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.events.MouseEvent.class)
    @Undesignable
    @Override
    public JSObject getOnMouseEntered() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.mouseEntered);
    }

    @ScriptFunction
    @Override
    public void setOnMouseEntered(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.mouseEntered, aValue);
    }

    @ScriptFunction(jsDoc = ON_MOUSE_EXITED_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.events.MouseEvent.class)
    @Undesignable
    @Override
    public JSObject getOnMouseExited() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.mouseExited);
    }

    @ScriptFunction
    @Override
    public void setOnMouseExited(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.mouseExited, aValue);
    }

    @ScriptFunction(jsDoc = ON_MOUSE_MOVED_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.events.MouseEvent.class)
    @Undesignable
    @Override
    public JSObject getOnMouseMoved() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.mouseMoved);
    }

    @ScriptFunction
    @Override
    public void setOnMouseMoved(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.mouseMoved, aValue);
    }

    @ScriptFunction(jsDoc = ON_MOUSE_PRESSED_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.events.MouseEvent.class)
    @Undesignable
    @Override
    public JSObject getOnMousePressed() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.mousePressed);
    }

    @ScriptFunction
    @Override
    public void setOnMousePressed(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.mousePressed, aValue);
    }

    @ScriptFunction(jsDoc = ON_MOUSE_RELEASED_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.events.MouseEvent.class)
    @Undesignable
    @Override
    public JSObject getOnMouseReleased() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.mouseReleased);
    }

    @ScriptFunction
    @Override
    public void setOnMouseReleased(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.mouseReleased, aValue);
    }

    @ScriptFunction(jsDoc = ON_MOUSE_WHEEL_MOVED_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.events.MouseEvent.class)
    @Undesignable
    @Override
    public JSObject getOnMouseWheelMoved() {
        return eventsProxy.getOnMouseWheelMoved();
    }

    @ScriptFunction
    @Override
    public void setOnMouseWheelMoved(JSObject aValue) {
        eventsProxy.setOnMouseWheelMoved(aValue);
    }

    @ScriptFunction(jsDoc = ON_ACTION_PERFORMED_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.events.ActionEvent.class)
    @Undesignable
    @Override
    public JSObject getOnActionPerformed() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.actionPerformed);
    }

    @ScriptFunction
    @Override
    public void setOnActionPerformed(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.actionPerformed, aValue);
    }

    @ScriptFunction(jsDoc = ON_COMPONENT_HIDDEN_JSDOC)
    @EventMethod(eventClass = ComponentEvent.class)
    @Undesignable
    @Override
    public JSObject getOnComponentHidden() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.componentHidden);
    }

    @ScriptFunction
    @Override
    public void setOnComponentHidden(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.componentHidden, aValue);
    }

    @ScriptFunction(jsDoc = ON_COMPONENT_MOVED_JSDOC)
    @EventMethod(eventClass = ComponentEvent.class)
    @Undesignable
    @Override
    public JSObject getOnComponentMoved() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.componentMoved);
    }

    @ScriptFunction
    @Override
    public void setOnComponentMoved(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.componentMoved, aValue);
    }

    @ScriptFunction(jsDoc = ON_COMPONENT_RESIZED_JSDOC)
    @EventMethod(eventClass = ComponentEvent.class)
    @Undesignable
    @Override
    public JSObject getOnComponentResized() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.componentResized);
    }

    @ScriptFunction
    @Override
    public void setOnComponentResized(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.componentResized, aValue);
    }

    @ScriptFunction(jsDoc = ON_COMPONENT_SHOWN_JSDOC)
    @EventMethod(eventClass = ComponentEvent.class)
    @Undesignable
    @Override
    public JSObject getOnComponentShown() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.componentShown);
    }

    @ScriptFunction
    @Override
    public void setOnComponentShown(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.componentShown, aValue);
    }

    @ScriptFunction(jsDoc = ON_FOCUS_GAINED_JSDOC)
    @EventMethod(eventClass = FocusEvent.class)
    @Undesignable
    @Override
    public JSObject getOnFocusGained() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.focusGained);
    }

    @ScriptFunction
    @Override
    public void setOnFocusGained(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.focusGained, aValue);
    }

    @ScriptFunction(jsDoc = ON_FOCUS_LOST_JSDOC)
    @EventMethod(eventClass = FocusEvent.class)
    @Undesignable
    @Override
    public JSObject getOnFocusLost() {
        return eventsProxy != null ? eventsProxy.getHandlers().get(ControlEventsIProxy.focusLost) : null;
    }

    @ScriptFunction
    @Override
    public void setOnFocusLost(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.focusLost, aValue);
    }

    @ScriptFunction(jsDoc = ON_KEY_PRESSED_JSDOC)
    @EventMethod(eventClass = KeyEvent.class)
    @Undesignable
    @Override
    public JSObject getOnKeyPressed() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.keyPressed);
    }

    @ScriptFunction
    @Override
    public void setOnKeyPressed(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.keyPressed, aValue);
    }

    @ScriptFunction(jsDoc = ON_KEY_RELEASED_JSDOC)
    @EventMethod(eventClass = KeyEvent.class)
    @Undesignable
    @Override
    public JSObject getOnKeyReleased() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.keyReleased);
    }

    @ScriptFunction
    @Override
    public void setOnKeyReleased(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.keyReleased, aValue);
    }

    @ScriptFunction(jsDoc = ON_KEY_TYPED_JSDOC)
    @EventMethod(eventClass = KeyEvent.class)
    @Undesignable
    @Override
    public JSObject getOnKeyTyped() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.keyTyped);
    }

    @ScriptFunction
    @Override
    public void setOnKeyTyped(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.keyTyped, aValue);
    }

    @ScriptFunction(jsDoc = ON_VALUE_CHANED_JSDOC)
    @EventMethod(eventClass = ValueChangeEvent.class)
    @Undesignable
    @Override
    public JSObject getOnValueChange() {
        return eventsProxy.getHandlers().get(ControlEventsIProxy.valueChanged);
    }

    @ScriptFunction
    @Override
    public void setOnValueChange(JSObject aValue) {
        eventsProxy.getHandlers().put(ControlEventsIProxy.valueChanged, aValue);
    }

    // published parent
    @ScriptFunction(name = "parent", jsDoc = PARENT_JSDOC)
    @Override
    public Widget getParentWidget() {
        return Forms.lookupPublishedParent(this);
    }
}
