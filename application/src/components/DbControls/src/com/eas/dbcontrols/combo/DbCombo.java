/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.combo;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.events.RowChangeEvent;
import com.bearsoft.rowset.events.RowsetAdapter;
import com.bearsoft.rowset.events.RowsetDeleteEvent;
import com.bearsoft.rowset.events.RowsetFilterEvent;
import com.bearsoft.rowset.events.RowsetInsertEvent;
import com.bearsoft.rowset.events.RowsetNextPageEvent;
import com.bearsoft.rowset.events.RowsetRequeryEvent;
import com.bearsoft.rowset.events.RowsetRollbackEvent;
import com.bearsoft.rowset.events.RowsetSaveEvent;
import com.bearsoft.rowset.events.RowsetScrollEvent;
import com.bearsoft.rowset.events.RowsetSortEvent;
import com.bearsoft.rowset.locators.Locator;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.bearsoft.rowset.utils.RowsetUtils;
import com.eas.client.model.ModelElementRef;
import com.eas.client.model.application.ApplicationEntity;
import com.eas.dbcontrols.DbControl;
import com.eas.dbcontrols.DbControlPanel;
import com.eas.dbcontrols.InitializingMethod;
import com.eas.dbcontrols.combo.rt.DbComboBox;
import com.eas.dbcontrols.combo.rt.SemiBorderTextField;
import com.eas.design.Designable;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 *
 * @author mg
 */
public class DbCombo extends DbControlPanel implements DbControl {

    public DbCombo() {
        super();
    }

    @Override
    protected void initializeDesign() {
        if (kind == InitializingMethod.UNDEFINED
                && getComponentCount() == 0) {
            super.initializeDesign();
            JComboBox<Object> cb = new JComboBox<>();
            cb.setOpaque(false);
            add(cb, BorderLayout.CENTER);
        }
    }
    //
    protected boolean list = true;
    protected ModelElementRef valueField;
    protected ModelElementRef displayField;
    protected ApplicationEntity<?, ?, ?> valueRsEntity;
    protected int valueColIndex = 0;
    protected String valueParamName = null;
    protected Locator byValueLocator;
    protected ApplicationEntity<?, ?, ?> displayRsEntity;
    protected int displayColIndex = 0;
    protected Map<Object, Object> displayCache = new HashMap<>();

    public boolean isList() {
        return list;
    }

    public void setList(boolean aValue) {
        if (list != aValue) {
            list = aValue;
        }
    }

    @Designable(category = "model")
    public ModelElementRef getValueField() {
        return valueField;
    }

    public void setValueField(ModelElementRef aValue) throws Exception {
        if (valueField != aValue) {
            valueField = aValue;
        }
    }

    @Designable(category = "model")
    public ModelElementRef getDisplayField() {
        return displayField;
    }

    public void setDisplayField(ModelElementRef aValue) throws Exception {
        if (displayField != aValue) {
            displayField = aValue;
        }
    }

    public void clearDisplayCache() {
        displayCache.clear();
    }

    public void putInDisplayCache(Object key, Object value) {
        displayCache.put(key, value);
    }

    public Object getFromDisplayCache(Object key) {
        return displayCache.get(key);
    }

    private void setupCombo() {
        if (listEditor != null && listEditor.getModel() != null) {
            beginUpdate();
            try {
                listEditor.setSelectedItem(editingValue);
            } finally {
                endUpdate();
            }
        }
    }

    @Override
    public void applyEditable2Field() {
        if (standalone) {
            if (listEditor != null) {
                listEditor.setEnabled(editable);
            }
        }
    }

    @Override
    protected void invokingLaterProcessControls() {
        super.invokingLaterProcessControls();
        if (listEditor != null) {
            listEditor.setEnabled(true);
        }
    }

    @Override
    protected void invokingLaterUnprocessControls() {
        super.invokingLaterUnprocessControls();
        if (listEditor != null) {
            listEditor.setEnabled(false);
        }
    }

    public String getRendererText() {
        if (nonListRendererEditor != null) {
            return nonListRendererEditor.getText();
        }
        return null;
    }

    protected class ValueRowsetListener extends RowsetAdapter {

        @Override
        public void rowsetFiltered(RowsetFilterEvent event) {
            fireComboContentsChanged();
        }

        @Override
        public void rowsetRequeried(RowsetRequeryEvent event) {
            try {
                recreateLocator();
                fireComboContentsChanged();
            } catch (Exception ex) {
                Logger.getLogger(DbCombo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void rowsetRolledback(RowsetRollbackEvent event) {
            try {
                recreateLocator();
                fireComboContentsChanged();
            } catch (Exception ex) {
                Logger.getLogger(DbCombo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void rowsetNextPageFetched(RowsetNextPageEvent event) {
            try {
                recreateLocator();
                fireComboContentsChanged();
            } catch (Exception ex) {
                Logger.getLogger(DbCombo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void rowsetSaved(RowsetSaveEvent event) {
        }

        @Override
        public void rowsetScrolled(RowsetScrollEvent event) {
        }

        @Override
        public void rowsetSorted(RowsetSortEvent event) {
            fireComboContentsChanged();
        }

        @Override
        public void rowInserted(RowsetInsertEvent event) {
            fireComboContentsChanged();
        }

        @Override
        public void rowChanged(RowChangeEvent event) {
        }

        @Override
        public void rowDeleted(RowsetDeleteEvent event) {
            fireComboContentsChanged();
        }
    }

    protected class DbComboBoxModel extends DefaultComboBoxModel<Object> {

        public void fireContentsChanged() {
            fireContentsChanged(DbComboBoxModel.this, -1, -1);
        }

        @Override
        public void setSelectedItem(Object anItem) {
            if (editable) {
                if (!DbCombo.this.isUpdating()) {
                    try {
                        if (standalone) {
                            setValue(anItem);
                            fireContentsChanged(DbComboBoxModel.this, -1, -1);
                        } else {
                            setEditingValue(anItem);
                            fireContentsChanged(DbComboBoxModel.this, -1, -1);
                            fireCellEditingCompleted();
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(DbCombo.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            listEditor.repaint();
        }

        @Override
        public Object getSelectedItem() {
            return editingValue;
        }

        @Override
        public int getSize() {
            try {
                if (valueRsEntity != null && valueRsEntity.getRowset() != null) {
                    Rowset valueRowset = valueRsEntity.getRowset();
                    return valueRowset.size();
                }
            } catch (Exception ex) {
                Logger.getLogger(DbCombo.class.getName()).log(Level.SEVERE, null, ex);
            }
            return 0;
        }

        @Override
        public Object getElementAt(int index) {
            try {
                if (valueRsEntity != null && valueRsEntity.getRowset() != null) {
                    Rowset rowset = valueRsEntity.getRowset();
                    if (index >= 0 && index < rowset.size()) {
                        try {
                            return rowset.getCurrent().get(index).getColumnObject(valueColIndex);
                        } catch (Exception ex) {
                            Logger.getLogger(DbCombo.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(DbCombo.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }

    protected class DbComboBoxRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<? extends Object> list, Object aValue, int index, boolean isSelected, boolean cellHasFocus) {
            Object lValue = null;
            boolean lIsAjusting = model.isAjusting();
            if (!lIsAjusting) {
                model.beginAjusting();
                model.beginSavingCurrentRowIndexes();
            }
            try {
                try {
                    Object displayFromScript = null;
                    if (standalone && published != null
                            && getOnRender() != null) {
                        if (displayCache.containsKey(aValue)) {
                            displayFromScript = displayCache.get(aValue);
                        } else {
                            /*
                             CellData cd = new CellData(styleValue, aValue, achiveDisplayValue(aValue));
                             CellRenderEvent event = new CellRenderEvent(eventThis != null ? eventThis : scriptThis, null, null, cd, null);
                             Object retValue = ScriptUtils.toJava(getOnRender().call(cx, eventThis != null ? eventThis : scriptThis, eventThis != null ? eventThis : scriptThis, new Object[]{event.getPublished()}));
                             if (Boolean.TRUE.equals(retValue)) {
                             try {
                             aValue = ScriptUtils.js2Java(cd.data);
                             displayFromScript = ScriptUtils.js2Java(cd.display);
                             if (displayFromScript != null) {
                             displayCache.put(aValue, displayFromScript);
                             }
                             } catch (Exception ex) {
                             Logger.getLogger(DbControlPanel.class.getName()).log(Level.SEVERE, null, ex);
                             }
                             }
                             */
                        }
                    }
                    if (displayFromScript != null) {
                        lValue = displayFromScript;
                    } else {
                        lValue = achiveDisplayValue(aValue);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(DbCombo.class.getName()).log(Level.SEVERE, null, ex);
                }
            } finally {
                if (!lIsAjusting) {
                    model.restoreRowIndexes();
                    model.endAjusting();
                }
            }
            if (lValue == null) {
                lValue = "";
            }
            Component comp = super.getListCellRendererComponent(list, lValue, index, isSelected, cellHasFocus);
            if (comp != null && comp instanceof JComponent) {
                JComponent jComp = (JComponent) comp;
                jComp.setOpaque(isSelected);
                jComp.setBorder(null);
            }
            return comp;
        }
    }

    @Override
    protected void applyEnabled() {
        if (listEditor != null) {
            listEditor.setEnabled(isEnabled() && isEditable());
        }
        if (nonListRendererEditor != null) {
            nonListRendererEditor.setEnabled(isEnabled());
        }
        super.applyEnabled();
    }

    @Override
    protected void applyForeground() {
        if (kind != InitializingMethod.UNDEFINED) {
            if (isList() && kind == InitializingMethod.EDITOR) {
                if (listEditor != null) {
                    listEditor.setForeground(getForeground());
                    if (listEditor.getEditor() != null && listEditor.getEditor().getEditorComponent() != null
                            && listEditor.getEditor().getEditorComponent() instanceof JTextField) {
                        JTextField tf = (JTextField) listEditor.getEditor().getEditorComponent();
                        tf.setForeground(getForeground());
                    }
                }

            } else {
                if (nonListRendererEditor != null) {
                    nonListRendererEditor.setForeground(getForeground());
                }
            }
        }
    }

    @Override
    protected void applyBackground() {
        if (kind != InitializingMethod.UNDEFINED) {
            if (isList() && kind == InitializingMethod.EDITOR) {
                if (listEditor != null) {
                    listEditor.setBackground(getBackground());
                    if (listEditor.getEditor() != null && listEditor.getEditor().getEditorComponent() != null
                            && listEditor.getEditor().getEditorComponent() instanceof JTextField) {
                        JTextField tf = (JTextField) listEditor.getEditor().getEditorComponent();
                        tf.setBackground(getBackground());
                    }
                }
            } else {
                if (nonListRendererEditor != null) {
                    nonListRendererEditor.setBackground(getBackground());
                }
            }
        }
    }

    @Override
    protected void applyCursor() {
        if (kind != InitializingMethod.UNDEFINED) {
            if (isList() && kind == InitializingMethod.EDITOR) {
                if (listEditor != null) {
                    listEditor.setCursor(getCursor());
                    if (listEditor.getEditor() != null && listEditor.getEditor().getEditorComponent() != null
                            && listEditor.getEditor().getEditorComponent() instanceof JTextField) {
                        JTextField tf = (JTextField) listEditor.getEditor().getEditorComponent();
                        tf.setCursor(getCursor());
                    }
                }
            } else {
                if (nonListRendererEditor != null) {
                    nonListRendererEditor.setCursor(getCursor());
                }
            }
        }
    }

    @Override
    protected void applyOpaque() {
        if (kind != InitializingMethod.UNDEFINED) {
            if (isList() && kind == InitializingMethod.EDITOR) {
                if (listEditor != null) {
                    listEditor.setOpaque(isOpaque());
                    if (listEditor.getEditor() != null && listEditor.getEditor().getEditorComponent() != null
                            && listEditor.getEditor().getEditorComponent() instanceof JTextField) {
                        JTextField tf = (JTextField) listEditor.getEditor().getEditorComponent();
                        tf.setOpaque(isOpaque());
                    }
                }
            } else {
                if (nonListRendererEditor != null) {
                    nonListRendererEditor.setOpaque(isOpaque());
                }
            }
        }
    }

    @Override
    protected void applyAlign() {
        if (nonListRendererEditor != null) {
            applySwingAlign2TextAlign(nonListRendererEditor, align);
        }
        if (listEditor != null && listEditor.getEditor() != null
                && listEditor.getEditor().getEditorComponent() != null
                && listEditor.getEditor().getEditorComponent() instanceof JTextField) {
            JTextField tfEditor = (JTextField) listEditor.getEditor().getEditorComponent();
            applySwingAlign2TextAlign(tfEditor, align);
        }
        super.applyAlign();
    }

    @Override
    protected void applyFont() {
        if (nonListRendererEditor != null) {
            //nonListRendererEditor.setFont(checkDefFont(getFont()));
            nonListRendererEditor.setFont(getFont());
        }
        if (listEditor != null) {
            //listEditor.setFont(checkDefFont(getFont()));
            listEditor.setFont(getFont());
            if (listEditor.getRenderer() != null && listEditor.getRenderer() instanceof JComponent) {
                JComponent comp = (JComponent) listEditor.getRenderer();
                //comp.setFont(checkDefFont(getFont()));
                comp.setFont(getFont());
            }
        }
    }

    @Override
    protected void applyTooltip(String aText) {
        if (nonListRendererEditor != null) {
            nonListRendererEditor.setToolTipText(aText);
        }
        if (listEditor != null) {
            listEditor.setToolTipText(aText);
            if (listEditor.getRenderer() != null && listEditor.getRenderer() instanceof JComponent) {
                JComponent comp = (JComponent) listEditor.getRenderer();
                comp.setToolTipText(aText);
            }
        }
    }

    @Override
    public JComponent getFocusTargetComponent() {
        if (nonListRendererEditor != null) {
            return nonListRendererEditor;
        }
        if (listEditor != null) {
            return listEditor;
        }
        return null;
    }

    @Override
    public Object getCellEditorValue() {
        return editingValue;
    }

    @Override
    public void setEditingValue(Object aValue) {
        super.setEditingValue(aValue);
        initializeEditor();
        setupEditor(null);
    }

    public void fireComboContentsChanged() {
        if (listEditor != null && listEditor.getModel() instanceof DbComboBoxModel) {
            DbComboBoxModel model = (DbComboBoxModel) listEditor.getModel();
            model.fireContentsChanged();
        } else if (nonListRendererEditor != null) {
            setupEditor(null);
        }
    }

    public void recreateLocator() throws Exception {
        byValueLocator = null;
        createLocator();
    }

    protected void createLocator() throws Exception {
        if (byValueLocator == null && valueRsEntity != null) {
            Rowset valueRowset = valueRsEntity.getRowset();
            if (valueRowset != null) {
                byValueLocator = valueRowset.createLocator();
                byValueLocator.beginConstrainting();
                try {
                    byValueLocator.addConstraint(valueColIndex);
                } finally {
                    byValueLocator.endConstrainting();
                }
            }
        }
    }

    @Override
    public Object achiveDisplayValue(Object aValue) throws Exception {
        internalBind();
        Object displayValue = displayCache.get(aValue);
        if (displayValue != null) {
            return displayValue;
        } else {
            if (valueColIndex > 0) {
                if (byValueLocator == null) {
                    createLocator();
                }
                return achiveDisplayValueByLocatorWithKey(aValue);
            } else if (valueParamName != null && !valueParamName.isEmpty()) {
                return achiveDisplayValueByParameterWithKey(aValue);
            } else {
                return aValue;
            }
        }
    }

    protected Object achiveDisplayValueByLocatorWithKey(Object aKey) throws Exception {
        if (byValueLocator != null
                && valueRsEntity != null
                && displayRsEntity != null
                && displayColIndex > 0) {
            Rowset valueRs = valueRsEntity.getRowset();
            if (valueRs.isAfterLast() && valueRs.isBeforeFirst()) {
                return null;
            } else {
                if (byValueLocator.find(aKey)) {
                    // display and value rowsets are the same
                    if (displayRsEntity.getEntityId().equals(valueRsEntity.getEntityId())) {
                        Row br = byValueLocator.getRow(0);
                        return br.getColumnObject(displayColIndex);
                    } else {
                        Object alreadyKey = RowsetUtils.UNDEFINED_SQL_VALUE;
                        if (!valueRs.isAfterLast() && !valueRs.isBeforeFirst()) {
                            alreadyKey = valueRs.getObject(valueColIndex);
                        }
                        if (alreadyKey == RowsetUtils.UNDEFINED_SQL_VALUE
                                || (alreadyKey != null && aKey == null)
                                || (alreadyKey == null && aKey != null)
                                || (aKey != null && !aKey.equals(alreadyKey))) {
                            Rowset valueCrs = valueRsEntity.getRowset();
                            int valueCrsPos = valueCrs.getCursorPos();
                            boolean isBeforeFirst = valueRs.isBeforeFirst();
                            boolean isAfterLast = valueRs.isAfterLast();
                            try {
                                if (byValueLocator.first()) {
                                    Rowset displayRs = displayRsEntity.getRowset();
                                    return displayRs.getObject(displayColIndex);
                                }
                            } finally {
                                if (isBeforeFirst) {
                                    valueRs.beforeFirst();
                                } else if (isAfterLast) {
                                    valueRs.isAfterLast();
                                } else {
                                    valueCrs.absolute(valueCrsPos);
                                }
                            }
                        } else {
                            Rowset displayRs = displayRsEntity.getRowset();
                            return displayRs.getObject(displayColIndex);
                        }
                    }
                }
            }
        }
        return aKey;
    }

    protected Object achiveDisplayValueByParameterWithKey(Object aParamValue) throws Exception {
        if (valueRsEntity != null) {
            if (valueRsEntity.getQuery() != null) {
                Parameters pmdi = valueRsEntity.getQuery().getParameters();
                Parameter param = pmdi.get(valueParamName);
                param.setValue(aParamValue);
                valueRsEntity.setRowset(null);
                valueRsEntity.execute();
                if (displayRsEntity != null) {
                    Rowset displayRs = displayRsEntity.getRowset();
                    if (displayRs != null && displayRs.size() > 0) {
                        displayRs.first();
                        return displayRs.getObject(displayColIndex);
                    }
                } else {
                    return null;
                }
            }
        }
        return null;
    }
    protected SemiBorderTextField nonListRendererEditor;
    protected DbComboBox<Object> listEditor;

    protected void initCombo() {
        listEditor = new DbComboBox<>(model, new DbComboBoxModel(), borderless);
        listEditor.setEditable(false);
        listEditor.setMaximumRowCount(24);
        listEditor.setRenderer(new DbComboBoxRenderer());
        if (standalone) {
            checkEvents(listEditor);
        }
        listEditor.setEnabled(standalone);
        listEditor.setOpaque(false);
        //listEditor.setOpaque(standalone && ((DbControlDesignInfo)designInfo).isOpaque());
        listEditor.setInheritsPopupMenu(true);
        add(listEditor, BorderLayout.CENTER);
    }

    @Override
    protected void initializeRenderer() {
        try {
            internalBind();
            if (kind != InitializingMethod.RENDERER) {
                kind = InitializingMethod.RENDERER;
                createLocator();
                removeAll();
                setLayout(new BorderLayout());
                addIconLabel();
                nonListRendererEditor = new SemiBorderTextField(true);
                nonListRendererEditor.setFont(getFont());
                add(nonListRendererEditor, BorderLayout.CENTER);
                applyFont();
                applyAlign();
            }
        } catch (Exception ex) {
            Logger.getLogger(DbCombo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void setupRenderer(JTable table, int row, int column, boolean isSelected) {
        try {
            // cosmetics
            if (table != null) {
                if (isSelected) {
                    nonListRendererEditor.setBackground(table.getSelectionBackground());
                    nonListRendererEditor.setForeground(table.getSelectionForeground());
                } else {
                    nonListRendererEditor.setBackground(table.getBackground());
                    nonListRendererEditor.setForeground(table.getForeground());
                }
            }
            Object lValue = null;
            if (displayingValue != null) {
                lValue = displayingValue;
            } else {
                lValue = achiveDisplayValue(editingValue);
            }
            if (lValue != null) {
                nonListRendererEditor.setText(lValue.toString());
            } else {
                nonListRendererEditor.setText("");
            }
        } catch (Exception ex) {
            Logger.getLogger(DbCombo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void initializeEditor() {
        try {
            internalBind();
            if (kind != InitializingMethod.EDITOR) {
                kind = InitializingMethod.EDITOR;
                createLocator();
                removeAll();
                setLayout(new BorderLayout());
                addIconLabel();
                if (isList()) {
                    initCombo();
                } else {
                    nonListRendererEditor = new SemiBorderTextField(borderless);
                    if (borderless) {
                        setBorder(null);
                    }
                    add(nonListRendererEditor, BorderLayout.CENTER);
                    nonListRendererEditor.setEditable(false);
                    nonListRendererEditor.setOpaque(false);
                    nonListRendererEditor.setInheritsPopupMenu(true);
                    //nonListRendererEditor.setOpaque(standalone && cdi.isOpaque());
                    checkEvents(nonListRendererEditor);
                }
                super.initializeEditor();
            }
        } catch (Exception ex) {
            Logger.getLogger(DbCombo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void setupEditor(JTable table) {
        if (isList()) {
            setupCombo();
            if (listEditor != null && table != null && borderless) {
                listEditor.setBorder(BorderFactory.createEmptyBorder());
            }
        } else {
            if (table != null && !standalone) {
                nonListRendererEditor.setBackground(table.getBackground());
                nonListRendererEditor.setForeground(table.getForeground());
            }
            if (model != null) {
                Object lValue = null;
                if (displayingValue != null) {
                    lValue = displayingValue;
                } else {
                    boolean lIsAjusting = model.isAjusting();
                    if (!lIsAjusting && valueRsEntity != displayRsEntity) {
                        model.beginAjusting();
                        model.beginSavingCurrentRowIndexes();
                    }
                    try {
                        try {
                            lValue = achiveDisplayValue(editingValue);
                        } catch (Exception ex) {
                            Logger.getLogger(DbCombo.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } finally {
                        if (!lIsAjusting && valueRsEntity != displayRsEntity) {
                            model.restoreRowIndexes();
                            model.endAjusting();
                        }
                    }
                }
                if (nonListRendererEditor != null) {
                    if (lValue != null) {
                        nonListRendererEditor.setText(lValue.toString());
                    } else {
                        nonListRendererEditor.setText("");
                    }
                }
            }
        }
        super.setupEditor(table);
    }

    protected void internalBind() throws Exception {
        if (model != null) {
            if (valueRsEntity == null && displayRsEntity == null) {
                if (valueField != null) {
                    ApplicationEntity<?, ?, ?> entity = model.getEntityById(valueField.getEntityId());
                    if (entity != null && valueField.getFieldName() != null && !valueField.getFieldName().isEmpty()) {
                        Rowset rowset = entity.getRowset();
                        if (rowset != null) {
                            valueRsEntity = entity;
                            if (valueField.isField()) {
                                valueColIndex = rowset.getFields().find(valueField.getFieldName());
                                valueRsEntity.getRowset().addRowsetListener(new ValueRowsetListener());
                            } else {
                                valueParamName = valueField.getFieldName();
                            }
                        }
                    }
                }

                if (displayField != null) {
                    ApplicationEntity<?, ?, ?> entity = null;
                    entity = model.getEntityById(displayField.getEntityId());
                    if (entity != null && displayField.getFieldName() != null && !displayField.getFieldName().isEmpty()) {
                        Rowset rowset = entity.getRowset();
                        if (rowset != null) {
                            displayRsEntity = entity;
                            displayColIndex = rowset.getFields().find(displayField.getFieldName());
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void unbind() throws Exception {
        super.unbind();
        valueRsEntity = null;
        valueColIndex = 0;
        displayRsEntity = null;
        displayColIndex = 0;
    }

    @Override
    protected void bind() throws Exception {
        assert model != null;
        super.bind();
        internalBind();
    }

    @Override
    public boolean isFieldContentModified() {
        return !standalone;
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

    /*
     public static final long serialVersionUID = 2687354L;
    
     private void writeObject(java.io.ObjectOutputStream out) throws IOException {
     writeExternal(out);
     }
    
     private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
     readExternal(in);
     }
    
     private void readObjectNoData() throws ObjectStreamException {
     }
    
     public void writeExternal(ObjectOutput out) throws IOException {
     DbControlsUtils.writeControl(this, out);
     }
    
     public void readExternal(ObjectInput in) throws IOException {
     try {
     DbControlsUtils.readControl(this, in);
     } catch (Exception ex) {
     throw new IOException(ex);
     }
     }
     * 
     */
}
