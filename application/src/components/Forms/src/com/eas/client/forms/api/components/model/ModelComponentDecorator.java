/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components.model;

import com.bearsoft.rowset.RowsetConverter;
import com.eas.client.forms.api.components.HasValue;
import com.eas.client.forms.components.VProgressBar;
import com.eas.client.forms.components.VSlider;
import com.eas.controls.events.ControlEventsIProxy;
import com.eas.dbcontrols.CellRenderEvent;
import com.eas.dbcontrols.DbControl;
import com.eas.dbcontrols.DbControlEditingListener;
import com.eas.dbcontrols.DbControlRowsetListener;
import com.eas.dbcontrols.DbControlsUtils;
import com.eas.dbcontrols.IconCache;
import com.eas.dbcontrols.InitializingMethod;
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
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
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
import javax.swing.border.Border;
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
 === editable processing ? ===
 */
/**
 *
 * @author mg
 * @param <D>
 * @param <V>
 */
public abstract class ModelComponentDecorator<D extends JComponent, V> extends JComponent implements ScalarModelWidget<V> {

    protected static RowsetConverter converter = new RowsetConverter();
    protected JTextField prefSizeCalculator = new JTextField();// DON't move to design!!!
    protected JLabel iconLabel = new JLabel(" ");
    protected JToolBar extraTools = new JToolBar();
    protected Set<CellEditorListener> editorListeners = new HashSet<>();
    protected Set<ActionListener> actionListeners = new HashSet<>();
    protected boolean borderless = true;
    protected D decorated;
    protected int align = SwingConstants.LEFT;
    protected Icon icon;
    protected boolean selectOnly;
    private boolean design;
    // Model interacting
    protected DbControlRowsetListener rowsetListener;
    protected JSObject published;
    protected static final Color WDIGETS_BORDER_COLOR = controlsBorderColor();
    public static final int WIDGETS_DEFAULT_WIDTH = 100;
    public static final int DB_CONTROLS_DEFAULT_HEIGHT = 100;

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

    protected class ModelWidgetFocusAdapter extends FocusAdapter {

        @Override
        public void focusGained(FocusEvent e) {
            super.focusGained(e);
            applyFocus();
            repaint();
        }

        @Override
        public void focusLost(FocusEvent e) {
            super.focusLost(e);
            repaint();
        }
    }

    public D getDecorated() {
        return decorated;
    }

    protected PropertyChangeListener valueListener = (PropertyChangeEvent evt) -> {
        if (ModelComponentDecorator.this.standalone) {
            ModelComponentDecorator.this.firePropertyChange(VALUE_PROP_NAME, evt.getOldValue(), evt.getNewValue());
        } else {
            ModelComponentDecorator.this.fireCellEditingCompleted();
        }
    };

    public void setDecorated(D aComponent) {
        if (decorated != null) {
            decorated.removePropertyChangeListener(VALUE_PROP_NAME, valueListener);
        }
        decorated = aComponent;
        if (decorated != null) {
            decorated.addPropertyChangeListener(VALUE_PROP_NAME, valueListener);
        }
    }

    @Override
    public void injectPublished(JSObject aValue) {
        published = aValue;
    }

    public void initializeBorder() {
        if (standalone) {
            super.setBorder(new LineBorder(WDIGETS_BORDER_COLOR));
        } else {
            setBorder(null);
        }
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
        borderless = false;
        initializeBorder();
    }

    @Override
    public Dimension getMaximumSize() {
        if (design) {
            return getPreferredSize();
        } else {
            return super.getMaximumSize();
        }
    }

    protected class FocusCommitter extends FocusAdapter {

        public FocusCommitter() {
            super();
        }

        @Override
        public void focusLost(FocusEvent e) {
            try {
                if (standalone) {
                }
            } catch (Exception ex) {
                Logger.getLogger(DbControlPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    protected void initializeFocusListener() {
        if (standalone) {
            Component focusTarget = getFocusTargetComponent();
            if (focusTarget != null) {
                focusTarget.addFocusListener(new FocusCommitter());
            }
        }
    }

    public ModelComponentDecorator() {
        super();
        extraTools.setBorder(null);
        extraTools.setBorderPainted(false);
        extraTools.setFloatable(false);
        addFocusListener(new ModelWidgetFocusAdapter());
        setOpaque(true);
        iconLabel.setOpaque(true);
        initializeDesign();
    }

    public void fireCellEditingCompleted() {
        editorListeners.stream().forEach((l) -> {
            if (l instanceof DbControlEditingListener) {
                ((DbControlEditingListener) l).cellEditingCompleted();
            }
        });
    }

    protected void checkEvents(Component aComp) {
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
                            if (!(fl instanceof ModelComponentDecorator.ModelWidgetFocusAdapter)) {
                                fl.focusGained(e);
                            }
                        }
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    FocusListener[] fls = getFocusListeners();
                    if (fls != null) {
                        e.setSource(ModelComponentDecorator.this);
                        for (FocusListener fl : fls) {
                            if (!(fl instanceof ModelComponentDecorator.ModelWidgetFocusAdapter)) {
                                fl.focusLost(e);
                            }
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
        initializeBorder();
    }

    public void applyStyle(CascadedStyle aStyle) {
        if (aStyle != null) {
            setFont(DbControlsUtils.toNativeFont(aStyle.getFont()));
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
        applyAlign();
    }

    protected void applyAlign() {
        if (align == SwingConstants.RIGHT) {
            add(new JLabel(" "), BorderLayout.EAST);
        }
    }

    protected abstract void applyFont();

    public abstract JComponent getFocusTargetComponent();

    protected void applyFocus() {
        Component comp = getFocusTargetComponent();
        if (comp != null) {
            comp.requestFocus();
        }
    }

    protected void applyFocusable() {
        Component comp = getFocusTargetComponent();
        if (comp != null) {
            comp.setFocusable(isFocusable());
        }
    }

    protected void applyEnabled() {
        for (int i = 0; i < extraTools.getComponentCount(); i++) {
            Component comp = extraTools.getComponent(i);
            comp.setEnabled(isEnabled());
        }
    }

    @Override
    public void setFocusable(boolean aValue) {
        super.setFocusable(aValue);
        applyFocusable();
    }

    @Override
    public void setToolTipText(String text) {
        super.setToolTipText(text);
        applyTooltip(text);
    }

    protected abstract void applyTooltip(String aText);

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
    public void setBackground(Color aColor) {
        super.setBackground(aColor);
        applyBackground();
        if (iconLabel != null) {
            iconLabel.setBackground(aColor);
        }
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        applyForeground();
    }

    @Override
    public void setCursor(Cursor cursor) {
        super.setCursor(cursor);
        applyCursor();
    }

    protected abstract void applyBackground();

    protected abstract void applyForeground();

    protected abstract void applyCursor();

    protected abstract void applyOpaque();

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
    public void setBorder(Border border) {
        if (borderless) {
            super.setBorder(null);
        } else {
            super.setBorder(border);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        applyEnabled();
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
            configure();
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
    public void setOnSelect(JSObject aValue) throws Exception {
        onSelect = aValue;
        createFieldExtraEditingControls(onSelect, true);
        revalidate();
        repaint();
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

    @ScriptFunction(jsDoc = VALUE_JSDOC)
    @Override
    public V getValue() {
        if (decorated instanceof HasValue<?>) {
            return ((HasValue<V>) decorated).getValue();
        } else if (decorated instanceof VProgressBar) {
            return (V) Integer.valueOf(((VProgressBar) decorated).getValue());
        } else if (decorated instanceof VSlider) {
            return (V) Integer.valueOf(((VSlider) decorated).getValue());
        }
        return null;
    }

    @Override
    public void setValue(V aValue) {
        if (decorated instanceof HasValue<?>) {
            ((HasValue<V>) decorated).setValue(aValue);
        } else if (decorated instanceof VProgressBar) {
            ((VProgressBar) decorated).setValue(aValue instanceof Number ? ((Number) aValue).intValue() : 0);
        } else if (decorated instanceof VSlider) {
            ((VSlider) decorated).setValue(aValue instanceof Number ? ((Number) aValue).intValue() : 0);
        }
    }

    @Override
    public void addValueChangeListener(PropertyChangeListener listener) {
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

    private static final String REDRAW_JSDOC = ""
            + "/**\n"
            + "* Redraw the component.\n"
            + "*/";

    @ScriptFunction(jsDoc = REDRAW_JSDOC)
    public void redraw() {
        revalidate();
        repaint();
    }

    @Override
    public void setOpaque(boolean isOpaque) {
        if (standalone) {
            super.setOpaque(isOpaque);
            applyOpaque();
        } else {
            super.setOpaque(false);
        }
    }

    // table or tree cell editing
    protected InitializingMethod kind = InitializingMethod.UNDEFINED;

    protected abstract void initializeRenderer();

    protected abstract void setupRenderer(JTable table, int row, int column, boolean isSelected);

    protected void initializeEditor() {
        design = kind == InitializingMethod.UNDEFINED;
        assert extraTools != null;
        extraTools.setVisible(false);
        add(extraTools, BorderLayout.EAST);
        iconLabel.setText("");
        iconLabel.setInheritsPopupMenu(true);
        applyBackground();
        applyForeground();
        applyFont();
        applyAlign();
        applyTooltip(getToolTipText());
        applyEnabled();
        applyFocusable();
        applyCursor();
        if (standalone) {
            applyOpaque();
        }
    }

    protected void setupCellBorder(boolean hasFocus) {
        assert !standalone;
        if (hasFocus) {
            setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
        } else {
            super.setBorder(null);
        }
    }

    protected void setupEditor(JTable table) {
        if (!standalone) {
            extraTools.setVisible(false);
            EventQueue.invokeLater(this::invokingLaterProcessControls);
        }
    }

    protected void invokingLaterProcessControls() {
        assert !standalone;
        extraTools.setVisible(true);
    }

    protected void invokingLaterUnprocessControls() {
        assert !standalone;
        extraTools.setVisible(false);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        try {
            initializeRenderer();
            setValue((V) value);
            setupRenderer(table, row, column, isSelected);
            setupCellBorder(hasFocus);
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setOpaque(true);
            } else {
                setBackground(table.getBackground());
                setOpaque(false);
            }
            return this;
        } catch (Exception ex) {
            Logger.getLogger(DbControlPanel.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return this;
        }
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        try {
            initializeEditor();
            setValue((V) value);
            setupEditor(table);
            return this;
        } catch (Exception ex) {
            Logger.getLogger(DbControlPanel.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
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
        invokingLaterUnprocessControls();
        return true;
    }

    @Override
    public void cancelCellEditing() {
        fireEditingCancelled();
        invokingLaterUnprocessControls();
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
        applyFont();
        Font f = getFont();
        if (f != null && prefSizeCalculator != null) {
            prefSizeCalculator.setFont(f);
        }
    }

    public void setBorderless(boolean aBorderless) {
        borderless = aBorderless;
        if (borderless) {
            setBorder(null);
        }
    }

    @Override
    public void setStandalone(boolean aStandalone) {
        standalone = aStandalone;
    }

    @Override
    public boolean isFieldContentModified() {
        return data == null;
    }

    protected boolean standalone = true;
    protected JSObject onSelect;
    protected JSObject onRender;

    protected void cleanup() {
        removeAll();
    }

    protected void configure() throws Exception {
        cleanup();
        if (standalone) {
            kind = InitializingMethod.UNDEFINED;
            createFieldExtraEditingControls(onSelect, true);
            initializeEditor();
            initializeBorder();
            initializeFocusListener();
        }
    }

    protected void createFieldExtraEditingControls(JSObject aSelectFunction, boolean nullable) throws Exception {
        extraTools.removeAll();
        if (aSelectFunction != null) {
            JButton btnSelectingField = new JButton();
            btnSelectingField.setAction(new ModelComponentDecorator.ValueSelectorAction(aSelectFunction));
            btnSelectingField.setPreferredSize(new Dimension(DbControl.EXTRA_BUTTON_WIDTH, DbControl.EXTRA_BUTTON_WIDTH));
            btnSelectingField.setFocusable(false);
            extraTools.add(btnSelectingField);
        }
        if (nullable) {
            JButton btnNullingField = new JButton();
            btnNullingField.setAction(new NullerAction());
            btnNullingField.setPreferredSize(new Dimension(DbControl.EXTRA_BUTTON_WIDTH, DbControl.EXTRA_BUTTON_WIDTH));
            btnNullingField.setFocusable(false);
            extraTools.add(btnNullingField);
        }
    }

    public boolean haveNullerAction() {
        Component[] comps = extraTools.getComponents();
        for (Component comp : comps) {
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
            putValue(Action.SHORT_DESCRIPTION, DbControlsUtils.getLocalizedString(NullerAction.class.getSimpleName()));
            putValue(Action.SMALL_ICON, IconCache.getIcon("16x16/delete.png"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                setValue(null);
            } catch (Exception ex) {
                Logger.getLogger(DbControlPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private class ValueSelectorAction extends AbstractAction {

        protected JSObject selector;

        ValueSelectorAction(JSObject aSelector) {
            super();
            putValue(Action.NAME, "...");
            putValue(Action.SHORT_DESCRIPTION, DbControlsUtils.getLocalizedString(ValueSelectorAction.class.getSimpleName()));
            selector = aSelector;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (selector != null && published != null) {
                try {
                    selector.call(published, new Object[]{published});
                } catch (Exception ex) {
                    Logger.getLogger(DbControlPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Override
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (kind == InitializingMethod.EDITOR) {
            super.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    @Override
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        if (kind == InitializingMethod.EDITOR) {
            super.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    /**
     * Overridden for performance reasons. See the <a
     * href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
        if (kind == InitializingMethod.EDITOR) {
            super.repaint(tm, x, y, width, height);
        }
    }

    /**
     * Overridden for performance reasons. See the <a
     * href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void repaint(Rectangle r) {
        if (kind == InitializingMethod.EDITOR) {
            super.repaint(r);
        }
    }

    /**
     * Overridden for performance reasons. See the <a
     * href="#override">Implementation Note</a> for more information.
     *
     * @since 1.5
     */
    @Override
    public void repaint() {
        if (kind == InitializingMethod.EDITOR) {
            super.repaint();
        }
    }
}
