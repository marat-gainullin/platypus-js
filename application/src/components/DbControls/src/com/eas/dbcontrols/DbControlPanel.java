/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols;

import com.bearsoft.gui.grid.data.CellData;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.RowsetConverter;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.client.model.ModelElementRef;
import com.eas.client.model.application.ApplicationEntity;
import com.eas.client.model.application.ApplicationModel;
import com.eas.client.model.script.RowHostObject;
import com.eas.controls.DummyControlValue;
import com.eas.design.Designable;
import com.eas.design.Undesignable;
import com.eas.gui.CascadedStyle;
import com.eas.script.ScriptUtils;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author mg
 */
public abstract class DbControlPanel extends JPanel implements ScalarDbControl {

    protected static RowsetConverter converter = new RowsetConverter();
    protected JTextField prefSizeCalculator = new JTextField();// DON't move to design!!!
    protected JLabel iconLabel = new JLabel(" ");
    protected JToolBar extraTools = new JToolBar();
    protected Set<CellEditorListener> editorListeners = new HashSet<>();
    protected Object editingValue = null;
    protected int updateCounter = 0;
    protected boolean borderless = true;
    protected int align = SwingConstants.LEFT;
    protected Icon icon = null;
    protected boolean editable = true;
    protected boolean selectOnly;
    private boolean design;

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

    protected class DbControlFocusAdapter extends FocusAdapter {

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

    public void initializeBorder() {
        if (standalone) {
            super.setBorder(new LineBorder(DbControlsUtils.DB_CONTROLS_BORDER_COLOR));
        } else {
            setBorder(null);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        int width = super.getPreferredSize().width;
        int height = prefSizeCalculator.getPreferredSize().height;
        if (width < DbControlsUtils.DB_CONTROLS_DEFAULT_WIDTH) {
            width = DbControlsUtils.DB_CONTROLS_DEFAULT_WIDTH;
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

    protected class FocusCommitter extends FocusAdapter {

        public FocusCommitter() {
            super();
        }

        @Override
        public void focusLost(FocusEvent e) {
            try {
                if (standalone) {
                    if (!setValue2Rowset(editingValue)) {
                        // if the value has been rejected by rowset we must
                        // reflect rowset's value in the control.
                        setEditingValue(getValueFromRowset());
                    }
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

    public DbControlPanel() {
        super();
        addFocusListener(new DbControlFocusAdapter());
        setOpaque(true);
        iconLabel.setOpaque(true);
        initializeDesign();
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean aEditable) {
        editable = aEditable;
        applyEditable();
    }

    protected abstract void applyEditable2Field();

    protected void applyEditable2NonField() {
        if (standalone) {
            extraTools.setVisible(editable);
        }
    }

    protected void applyEditable() {
        boolean oldEditable = editable;
        try {
            editable = oldEditable && !isSelectOnly();
            applyEditable2Field();
        } finally {
            editable = oldEditable;
        }
        applyEditable2NonField();
    }

    @Override
    public void fireCellEditingCompleted() {
        for (CellEditorListener l : editorListeners) {
            if (l instanceof DbControlEditingListener) {
                ((DbControlEditingListener) l).cellEditingCompleted();
            }
        }
    }

    protected void checkEvents(Component aComp) {
        if (aComp != null && model != null) {
            aComp.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    MouseListener[] mls = getMouseListeners();
                    if (mls != null) {
                        for (int i = 0; i < mls.length; i++) {
                            mls[i].mouseClicked(e);
                        }
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    MouseListener[] mls = getMouseListeners();
                    if (mls != null) {
                        for (int i = 0; i < mls.length; i++) {
                            mls[i].mousePressed(e);
                        }
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    MouseListener[] mls = getMouseListeners();
                    if (mls != null) {
                        for (int i = 0; i < mls.length; i++) {
                            mls[i].mouseReleased(e);
                        }
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    MouseListener[] mls = getMouseListeners();
                    if (mls != null) {
                        for (int i = 0; i < mls.length; i++) {
                            mls[i].mouseEntered(e);
                        }
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    MouseListener[] mls = getMouseListeners();
                    if (mls != null) {
                        for (int i = 0; i < mls.length; i++) {
                            mls[i].mouseExited(e);
                        }
                    }
                }
            });
            aComp.addMouseMotionListener(new MouseMotionListener() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    MouseMotionListener[] mmls = getMouseMotionListeners();
                    if (mmls != null) {
                        for (int i = 0; i < mmls.length; i++) {
                            mmls[i].mouseDragged(e);
                        }
                    }
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    MouseMotionListener[] mmls = getMouseMotionListeners();
                    if (mmls != null) {
                        for (int i = 0; i < mmls.length; i++) {
                            mmls[i].mouseMoved(e);
                        }
                    }
                }
            });
            aComp.addMouseWheelListener(new MouseWheelListener() {
                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    MouseWheelListener[] mwls = getMouseWheelListeners();
                    if (mwls != null) {
                        for (int i = 0; i < mwls.length; i++) {
                            mwls[i].mouseWheelMoved(e);
                        }
                    }
                }
            });

            aComp.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    FocusListener[] fls = getFocusListeners();
                    if (fls != null) {
                        for (int i = 0; i < fls.length; i++) {
                            if (!(fls[i] instanceof DbControlFocusAdapter)) {
                                fls[i].focusGained(e);
                            }
                        }
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    FocusListener[] fls = getFocusListeners();
                    if (fls != null) {
                        for (int i = 0; i < fls.length; i++) {
                            if (!(fls[i] instanceof DbControlFocusAdapter)) {
                                fls[i].focusLost(e);
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
                        for (int i = 0; i < kls.length; i++) {
                            kls[i].keyTyped(e);
                        }
                    }
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    KeyListener[] kls = getKeyListeners();
                    if (kls != null) {
                        for (int i = 0; i < kls.length; i++) {
                            kls[i].keyPressed(e);
                        }
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    KeyListener[] kls = getKeyListeners();
                    if (kls != null) {
                        for (int i = 0; i < kls.length; i++) {
                            kls[i].keyReleased(e);
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

    @Override
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
        if (align == SwingConstants.RIGHT && kind != InitializingMethod.EDITOR) {
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

    @Undesignable
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
        applyEditable();
        applyFocusable();
        applyCursor();
        if (standalone) {
            applyOpaque();
        }
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

    @Override
    public void beginUpdate() {
        assert (updateCounter >= 0);
        updateCounter++;
    }

    @Override
    public boolean isUpdating() {
        return updateCounter > 0;
    }

    @Override
    public void endUpdate() {
        assert (updateCounter > 0);
        updateCounter--;
    }

    protected void setupEditor(JTable table) {
        if (!standalone) {
            if (!isUpdating()) {
                extraTools.setVisible(false);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        invokingLaterProcessControls();
                    }
                });
            }
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

    protected void setupCellBorder(boolean hasFocus) {
        assert !standalone;
        if (hasFocus) {
            setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
        } else {
            super.setBorder(null);
        }
    }
    protected CascadedStyle styleValue;
    protected Object displayingValue;

    @Override
    public Object achiveDisplayValue(Object aValue) throws Exception {
        return aValue;
    }

    protected void acceptCellValue(Object aValue) throws Exception {
        if (scriptScope == null && model != null) {
            scriptScope = model.getScriptScope();
        }
        if (standalone && scriptScope != null
                && handleFunction != null) {
            Object dataToProcess = aValue instanceof CellData ? ((CellData) aValue).data : aValue;
            CellData cd = new CellData(new CascadedStyle(), dataToProcess, achiveDisplayValue(dataToProcess));
            Context cx = Context.getCurrentContext();
            boolean wasContext = cx != null;
            if (!wasContext) {
                cx = ScriptUtils.enterContext();
            }
            try {
                Row row = null;
                if (rsEntity != null && !rsEntity.getRowset().isBeforeFirst() && !rsEntity.getRowset().isAfterLast()) {
                    row = rsEntity.getRowset().getCurrentRow();
                }
                Object[] rowIds = null;
                if (row != null) {
                    rowIds = row.getPKValues();
                }
                Object retValue = handleFunction.call(cx, eventThis != null ? eventThis : scriptScope, eventThis != null ? eventThis : scriptScope, new Object[]{new CellRenderEvent(eventThis != null ? eventThis : scriptScope, rowIds != null && rowIds.length > 0 ? (rowIds.length > 1 ? rowIds : rowIds[0]) : null, null, cd, row != null ? RowHostObject.publishRow(scriptScope, row) : null)});
                if (Boolean.TRUE.equals(retValue)) {
                    try {
                        cd.data = ScriptUtils.js2Java(cd.data);
                        cd.display = ScriptUtils.js2Java(cd.display);
                        aValue = cd;
                    } catch (Exception ex) {
                        Logger.getLogger(DbControlPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } finally {
                if (!wasContext) {
                    Context.exit();
                }
            }
        }
        if (aValue instanceof CellData) {
            CellData cd = (CellData) aValue;
            editingValue = cd.data;
            styleValue = cd.style;
            displayingValue = cd.display;
        } else {
            editingValue = aValue;
            styleValue = null;
            displayingValue = null;
        }
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        try {
            assert model != null;
            initializeRenderer();
            acceptCellValue(value);
            setupRenderer(table, row, column, isSelected);
            setupCellBorder(hasFocus);
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }
            applyStyle(styleValue);
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            }
            setOpaque(false);
            return this;
        } catch (Exception ex) {
            Logger.getLogger(DbControlPanel.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return this;
        }
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        try {
            assert model != null;
            initializeEditor();
            acceptCellValue(value);
            setupEditor(table);
            applyStyle(styleValue);
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
        for (CellEditorListener l : editorListeners) {
            if (l != null) {
                l.editingStopped(new ChangeEvent(this));
            }
        }
    }

    protected void fireEditingCancelled() {
        for (CellEditorListener l : editorListeners) {
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
    // datamodel interacting
    protected ModelElementRef datamodelElement = null;
    protected ApplicationModel<?, ?, ?, ?> model = null;
    protected ApplicationEntity<?, ?, ?> rsEntity;
    protected int colIndex = 0;
    protected DbControlRowsetListener rowsetListener;
    protected Scriptable scriptScope;
    protected Scriptable eventThis;

    protected void bind() throws Exception {
        assert model != null;
        if (datamodelElement != null) {
            ApplicationEntity<?, ?, ?> entity = model.getEntityById(datamodelElement.getEntityId());
            if (entity != null && datamodelElement.getFieldName() != null && !datamodelElement.getFieldName().isEmpty()) {
                Rowset rowset = entity.getRowset();
                if (rowset != null) {
                    rsEntity = entity;
                    colIndex = rowset.getFields().find(datamodelElement.getFieldName());
                    if (rowsetListener != null) {
                        rowset.removeRowsetListener(rowsetListener);
                    }
                    rowsetListener = new DbControlRowsetListener(this);
                    rowset.addRowsetListener(rowsetListener);
                } else {
                    entity.getChangeSupport().addPropertyChangeListener(this);
                }
            }
        }
    }

    @Override
    public boolean isSelectOnly() {
        if (kind != InitializingMethod.UNDEFINED) {
            return selectOnly;
        }
        return false;
    }

    @Override
    public void setSelectOnly(boolean aValue) {
        if (kind != InitializingMethod.UNDEFINED) {
            selectOnly = aValue;
        }
        applyEditable();
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

    @Override
    public ApplicationEntity<?, ?, ?> getBaseEntity() {
        return rsEntity;
    }

    ///// script interface /////
    public Object getValue() throws Exception {
        if (scriptScope != null) {
            return ScriptUtils.javaToJS(editingValue, scriptScope);
        } else {
            return null;
        }
    }

    public void setValue(Object aValue) throws Exception {
        if (scriptScope != null) {
            aValue = ScriptUtils.js2Java(aValue);
            if (standalone) {
                if (!setValue2Rowset(aValue)) {
                    // if the value has been rejected by rowset we must
                    // reflect rowset's value in the control.
                    setEditingValue(getValueFromRowset());
                }
            } else {
                setEditingValue(aValue);
                fireCellEditingCompleted();
            }
        }
    }
    //////////////////////////////

    @Override
    public void setEditingValue(Object aValue) {
        try {
            acceptCellValue(aValue);
            setupEditor(null);
            if (styleValue != null) {
                applyStyle(styleValue);
            }
            repaint();
        } catch (Exception ex) {
            Logger.getLogger(DbControlPanel.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    @Override
    public Object getValueFromRowset() throws Exception {
        if (rsEntity != null && rsEntity.getRowset() != null && colIndex > 0) {
            if (!rsEntity.getRowset().isBeforeFirst() && !rsEntity.getRowset().isAfterLast()) {
                Object value = rsEntity.getRowset().getObject(colIndex);
                if (value == null && datamodelElement != null) {
                    value = rsEntity.getSubstituteRowsetObject(datamodelElement.getFieldName());
                }
                return value;
            }
        }
        return null;
    }

    @Override
    public boolean setValue2Rowset(Object aValue) throws Exception {
        if (!(aValue instanceof DummyControlValue) && !isUpdating() && rsEntity != null
                && rsEntity.getRowset() != null && colIndex > 0) {
            if (!rsEntity.getRowset().isBeforeFirst() && !rsEntity.getRowset().isAfterLast()) {
                try {
                    return rsEntity.getRowset().updateObject(colIndex, aValue);
                } catch (Exception ex) {
                    setEditingValue(rsEntity.getRowset().getObject(colIndex));
                }
            }
        }
        return false;
    }

    @Override
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
        return model == null;
    }

    @Override
    public int getColIndex() {
        return colIndex;
    }

    @Override
    public ApplicationModel<?, ?, ?, ?> getModel() {
        return model;
    }

    @Override
    public void setModel(ApplicationModel<?, ?, ?, ?> aModel) throws Exception {
        ApplicationModel<?, ?, ?, ?> oldValue = model;
        if (model != aModel) {
            if (model != null) {
                unbind();
                model.getChangeSupport().removePropertyChangeListener(this);
            }
            model = aModel;
            if (model != null) {
                model.getChangeSupport().addPropertyChangeListener(this);
            }
            firePropertyChange("datamodel", oldValue, model);
        }
    }

    @Designable(displayName = "field", category = "model")
    @Override
    public ModelElementRef getDatamodelElement() {
        return datamodelElement;
    }

    @Override
    public void setDatamodelElement(ModelElementRef aValue) throws Exception {
        if (datamodelElement != aValue) {
            datamodelElement = aValue;
        }
    }
    protected boolean standalone = true;
    protected Function selectFunction;
    protected Function handleFunction;

    @Override
    public void cleanup() {
        removeAll();
    }

    @Override
    public void configure() throws Exception {
        if (isRuntime()) {
            cleanup();
            if (standalone) {
                if (model != null && datamodelElement != null) {
                    kind = InitializingMethod.UNDEFINED;
                    unbind();
                    bind();
                    createFieldExtraEditingControls();
                    removeAll();
                    initializeEditor();
                    extraTools.setVisible(editable);
                    initializeBorder();
                    initializeFocusListener();
                } else {
                    initializeDesign();
                }
            } else {
                // addExtraControls(createCellExtraEditingControls());
                // This work is moved to client code (grid or veer columns initializing)
            }
        }
    }

    protected void createFieldExtraEditingControls() throws Exception {
        extraTools.removeAll();
        if (rsEntity != null) {
            Fields fields = rsEntity.getFields();
            Field field = fields.get(colIndex);
            if (field != null) {
                extraTools.setBorder(null);
                extraTools.setFloatable(false);
                if (selectFunction != null) {
                    JButton btnSelectingField = new JButton();
                    btnSelectingField.setAction(new FieldValueSelectorAction());
                    btnSelectingField.setPreferredSize(new Dimension(DbControl.EXTRA_BUTTON_WIDTH, DbControl.EXTRA_BUTTON_WIDTH));
                    btnSelectingField.setFocusable(false);
                    extraTools.add(btnSelectingField);
                }
                if (field.isNullable()) {
                    JButton btnNullingField = new JButton();
                    btnNullingField.setAction(new FieldNullerAction());
                    btnNullingField.setPreferredSize(new Dimension(DbControl.EXTRA_BUTTON_WIDTH, DbControl.EXTRA_BUTTON_WIDTH));
                    btnNullingField.setFocusable(false);
                    extraTools.add(btnNullingField);
                }
            }
        }
    }

    @Override
    public void extraCellControls(Function aSelectFunction, boolean nullable) throws Exception {
        extraTools.removeAll();
        extraTools.setBorder(null);
        extraTools.setFloatable(false);
        if (aSelectFunction != null) {
            JButton btnSelectingField = new JButton();
            btnSelectingField.setAction(new CellValueSelectorAction(aSelectFunction));
            btnSelectingField.setPreferredSize(new Dimension(DbControl.EXTRA_BUTTON_WIDTH, DbControl.EXTRA_BUTTON_WIDTH));
            btnSelectingField.setFocusable(false);
            extraTools.add(btnSelectingField);
        }
        if (nullable) {
            JButton btnNullingField = new JButton();
            btnNullingField.setAction(new CellNullerAction());
            btnNullingField.setPreferredSize(new Dimension(DbControl.EXTRA_BUTTON_WIDTH, DbControl.EXTRA_BUTTON_WIDTH));
            btnNullingField.setFocusable(false);
            extraTools.add(btnNullingField);
        }
    }

    protected void unbind() throws Exception {
        if (rsEntity != null && rowsetListener != null) {
            rsEntity.getRowset().removeRowsetListener(rowsetListener);
        }
    }

    @Override
    public boolean haveNullerAction() {
        Component[] comps = extraTools.getComponents();
        for (Component comp : comps) {
            if (comp instanceof AbstractButton) {
                AbstractButton ab = (AbstractButton) comp;
                if (ab.getAction() instanceof NullerAction) {
                    return true;
                }
            }
        }
        return false;
    }

    private abstract class NullerAction extends AbstractAction {

        NullerAction() {
            super();
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0));
            putValue(Action.SHORT_DESCRIPTION, DbControlsUtils.getLocalizedString(NullerAction.class.getSimpleName()));
            putValue(Action.SMALL_ICON, IconCache.getIcon("16x16/delete.png"));
        }

        protected abstract void applyValue(Object aObject) throws Exception;

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                applyValue(null);
            } catch (Exception ex) {
                Logger.getLogger(DbControlPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private class FieldNullerAction extends NullerAction {

        @Override
        protected void applyValue(Object aObject) throws Exception {
            setValue2Rowset(null);
        }
    }

    private class CellNullerAction extends NullerAction {

        @Override
        protected void applyValue(Object aObject) throws Exception {
            setEditingValue(null);
            fireCellEditingCompleted();
        }
    }

    private abstract class ValueSelectorAction extends AbstractAction {

        ValueSelectorAction() {
            super();
            putValue(Action.NAME, "...");
            putValue(Action.SHORT_DESCRIPTION, DbControlsUtils.getLocalizedString(ValueSelectorAction.class.getSimpleName()));
        }

        protected abstract Function getSelectFunction();

        @Override
        public void actionPerformed(ActionEvent e) {
            Function selectFunction = getSelectFunction();
            if (selectFunction != null) {
                if (scriptScope == null && model != null) {
                    scriptScope = model.getScriptScope();
                }
                if (scriptScope != null) {
                    Context cx = Context.getCurrentContext();
                    boolean wasContext = cx != null;
                    if (!wasContext) {
                        cx = ScriptUtils.enterContext();
                    }
                    try {
                        Object oEditor = DbControlPanel.this.getClientProperty(ScriptUtils.WRAPPER_PROP_NAME);
                        if (oEditor == null) {
                            oEditor = DbControlPanel.this;
                        }
                        selectFunction.call(cx, eventThis != null ? eventThis : scriptScope, eventThis != null ? eventThis : scriptScope, new Object[]{oEditor});
                        /*
                         Component focusTarget = getFocusTargetComponent();
                         if (focusTarget != null) {
                         focusTarget.requestFocus();
                         }
                         */
                    } finally {
                        if (!wasContext) {
                            Context.exit();
                        }
                    }
                }
            }
        }
    }

    protected Function getHandler(String aHandlerName) {
        if (model != null && model.getScriptScope() != null) {
            Object oFunction = model.getScriptScope().get(aHandlerName, model.getScriptScope());
            if (oFunction instanceof Function) {
                return (Function) oFunction;
            }
        }
        return null;
    }

    private class FieldValueSelectorAction extends ValueSelectorAction {

        @Override
        protected Function getSelectFunction() {
            return selectFunction;
        }
    }

    private class CellValueSelectorAction extends ValueSelectorAction {

        protected Function selectFunction;

        public CellValueSelectorAction(Function aSelectFunction) {
            super();
            selectFunction = aSelectFunction;
        }

        @Override
        protected Function getSelectFunction() {
            return selectFunction;
        }
    }

    public Scriptable getScriptScope() {
        return scriptScope;
    }

    public void setScriptScope(Scriptable aScriptScope) {
        scriptScope = aScriptScope;
    }

    @Undesignable
    @Override
    public Scriptable getEventsThis() {
        return eventThis;
    }

    @Override
    public void setEventsThis(Scriptable aValue) {
        eventThis = aValue;
    }

    @Override
    public boolean isRuntime() {
        return model != null && model.isRuntime();
    }

    @Override
    public Function getOnSelect() {
        return selectFunction;
    }

    @Override
    public void setOnSelect(Function aHandler) {
        selectFunction = aHandler;
    }

    @Override
    public Function getOnRender() {
        return handleFunction;
    }

    @Override
    public void setOnRender(Function aHandler) {
        handleFunction = aHandler;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        try {
            if (evt != null) {
                ApplicationEntity<?, ?, ?> entity = datamodelElement != null && model != null ? model.getEntityById(datamodelElement.getEntityId()) : null;
                if (evt.getSource() == model
                        && "runtime".equals(evt.getPropertyName())) {
                    if (Boolean.FALSE.equals(evt.getOldValue())
                            && Boolean.TRUE.equals(evt.getNewValue())) {
                        if (entity != null && entity.getRowset() != null) {
                            configure();
                            beginUpdate();
                            try {
                                setEditingValue(getValueFromRowset());
                            } finally {
                                endUpdate();
                            }
                        } else {
                            bind();
                        }
                    } else if (Boolean.TRUE.equals(evt.getOldValue())
                            && Boolean.FALSE.equals(evt.getNewValue())) {
                        cleanup();
                    }
                } else if (evt.getSource() == entity
                        && "rowset".equals(evt.getPropertyName())
                        && evt.getNewValue() != null && evt.getOldValue() == null) {
                    entity.getChangeSupport().removePropertyChangeListener(this);
                    assert entity.getRowset() != null;
                    configure();
                    beginUpdate();
                    try {
                        setEditingValue(getValueFromRowset());
                    } finally {
                        endUpdate();
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(DbControlPanel.class.getName()).log(Level.SEVERE, null, ex);
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
