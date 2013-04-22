/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols;

import com.eas.client.Client;
import com.eas.client.datamodel.ApplicationModel;
import com.eas.controls.DesignInfo;
import com.eas.dbcontrols.actions.DbControlSnapshotAction;
import java.awt.Component;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEditSupport;

/**
 *
 * @author mg
 */
public abstract class DbControlCustomizer extends JPanel implements java.beans.Customizer {

    protected CustomizerUpdater customizerUpdater = new CustomizerUpdater();
    protected UndoableEditSupport undoSupport = new UndoableEditSupport();
    protected UndoManager editor = null;
    protected DbControl bean = null;
    protected DesignInfo designInfo = null;
    protected FieldRefRenderer fieldRenderer = new FieldRefRenderer();
    protected Font fieldsFont = new Font(Font.DIALOG, 0, 12);
    protected boolean updatingView = false;
    //
    protected JComboBox cmbSelectFunction;
    protected SelectFunctionClearAction selectFunctionClearAction = new SelectFunctionClearAction();
    protected SelectFunctionChangeAction selectFunctionChangeAction = new SelectFunctionChangeAction();
    protected ScriptFunctionsComboBoxModel selectFunctionModel = new ScriptFunctionsComboBoxModel();
    protected JCheckBox chkSelectOnly;
    protected SelectOnlyChangeAction selectOnlyChangeAction = new SelectOnlyChangeAction();
    //
    protected JComboBox cmbHandleFunction;
    protected HandleFunctionClearAction handleFunctionClearAction = new HandleFunctionClearAction();
    protected HandleFunctionChangeAction handleFunctionChangeAction = new HandleFunctionChangeAction();
    protected ScriptFunctionsComboBoxModel handleFunctionModel = new ScriptFunctionsComboBoxModel();
    //
    protected ScriptEvents scriptHost = null;

    public void updateFunctions(String aSubjectName, Method aListenerMethod, String existentHandler, ScriptFunctionsComboBoxModel aModel) {
        if (scriptHost != null) {
            if (existentHandler == null || existentHandler.isEmpty()) {
                existentHandler = scriptHost.findFreeHandlerName(aSubjectName, aListenerMethod);
            }
            List<String> funcs = new ArrayList<>();
            funcs.addAll(Arrays.asList(scriptHost.getAllEventHandlers()));
            if (!funcs.contains(existentHandler)) {
                funcs.add(existentHandler);
            }
            aModel.setFunctions(funcs.toArray(new String[0]));
        }
    }

    /**
     * Enumerates action map actions and assigns them thia custormizer's undo
     * support and db control.
     */
    protected void assignDesignInfoSupport2Actions() {
        ActionMap am = getActionMap();
        if (am != null) {
            Object[] actionKeys = am.allKeys();
            if (actionKeys != null) {
                for (Object actionKey : actionKeys) {
                    Action action = am.get(actionKey);
                    if (action instanceof DbControlSnapshotAction) {
                        DbControlSnapshotAction cAction = (DbControlSnapshotAction) action;
                        cAction.setDesignInfo(designInfo);
                        cAction.setUndoSupport(undoSupport);
                    }
                }
            }
        }
        selectFunctionChangeAction.setDesignInfo(designInfo);
        selectFunctionClearAction.setDesignInfo(designInfo);
        selectOnlyChangeAction.setDesignInfo(designInfo);
        selectFunctionChangeAction.setUndoSupport(undoSupport);
        selectFunctionClearAction.setUndoSupport(undoSupport);
        selectOnlyChangeAction.setUndoSupport(undoSupport);

        handleFunctionChangeAction.setDesignInfo(designInfo);
        handleFunctionClearAction.setDesignInfo(designInfo);
        handleFunctionChangeAction.setUndoSupport(undoSupport);
        handleFunctionClearAction.setUndoSupport(undoSupport);
    }

    protected void checkActionMap() {
        ActionMap am = getActionMap();
        if (am != null) {
            Object[] actionKeys = am.allKeys();
            if (actionKeys != null) {
                for (Object actionKey : actionKeys) {
                    Action action = am.get(actionKey);
                    if (action != null) {
                        action.setEnabled(false);
                        action.setEnabled(action.isEnabled());
                    }
                }
            }
        }
        selectFunctionChangeAction.setEnabled(false);
        selectFunctionClearAction.setEnabled(false);
        selectOnlyChangeAction.setEnabled(false);
        handleFunctionChangeAction.setEnabled(false);
        handleFunctionClearAction.setEnabled(false);
        selectFunctionChangeAction.setEnabled(selectFunctionChangeAction.isEnabled());
        selectFunctionClearAction.setEnabled(selectFunctionClearAction.isEnabled());
        selectOnlyChangeAction.setEnabled(selectOnlyChangeAction.isEnabled());
        handleFunctionChangeAction.setEnabled(handleFunctionChangeAction.isEnabled());
        handleFunctionClearAction.setEnabled(handleFunctionClearAction.isEnabled());
    }

    protected void updateHandlers() {
        if (bean != null && scriptHost != null
                && bean instanceof Component) {
            DbControlDesignInfo cInfo = (DbControlDesignInfo) designInfo;
            updateFunctions(((Component) bean).getName(), DbControlChangeEdit.selectValueMethod, cInfo.getSelectFunction(), selectFunctionModel);
            updateFunctions(((Component) bean).getName(), DbControlChangeEdit.handleValueMethod, cInfo.getHandleFunction(), handleFunctionModel);
        }
    }

    protected class CustomizerUpdater implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            designInfoPropertyChanged(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
        }
    }

    public class ScriptFunctionsComboBoxModel implements ComboBoxModel {

        protected Set<ListDataListener> listeners = new HashSet<>();
        protected String selected = null;
        protected String[] functions = null;

        public void setFunctions(String[] aFunctions) {
            functions = aFunctions;
            fireDataChanged();
        }

        @Override
        public void setSelectedItem(Object anItem) {
            selected = (String) anItem;
        }

        @Override
        public Object getSelectedItem() {
            return selected;
        }

        @Override
        public int getSize() {
            if (functions != null) {
                return functions.length;
            }
            return 0;
        }

        @Override
        public Object getElementAt(int index) {
            if (index >= 0 && index < getSize()) {
                return functions[index];
            } else {
                return null;
            }
        }

        @Override
        public void addListDataListener(ListDataListener l) {
            listeners.add(l);
        }

        @Override
        public void removeListDataListener(ListDataListener l) {
            listeners.remove(l);
        }

        protected void fireDataChanged() {
            ListDataEvent event = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, getSize() - 1);
            for (ListDataListener l : listeners) {
                if (l instanceof JComboBox) {
                    JComboBox combo = (JComboBox) l;
                    Action action = combo.getAction();
                    combo.setAction(null);
                    try {
                        l.contentsChanged(event);
                    } finally {
                        combo.setAction(action);
                    }
                } else {
                    l.contentsChanged(event);
                }
            }
        }
    }

    protected class SelectFunctionChangeAction extends DbControlSnapshotAction {

        @Override
        public boolean isEnabled() {
            return designInfo instanceof DbControlDesignInfo;
        }

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            if (after != null) {
                String script = (String) cmbSelectFunction.getSelectedItem();
                if (script == null || script.isEmpty()) {
                    ComboBoxEditor cbe = cmbSelectFunction.getEditor();
                    if (cbe != null && cbe.getEditorComponent() != null) {
                        Component ec = cbe.getEditorComponent();
                        if (ec instanceof JTextComponent) {
                            JTextComponent tc = (JTextComponent) ec;
                            script = tc.getText();
                        }
                    }
                }
                after.setSelectFunction(script);
            }
        }
    }

    protected class SelectFunctionClearAction extends DbControlSnapshotAction {

        public SelectFunctionClearAction() {
            super();
            putValue(Action.SMALL_ICON, DesignIconCache.getIcon("16x16/delete.png"));
        }

        @Override
        public boolean isEnabled() {
            if (designInfo instanceof DbControlDesignInfo) {
                DbControlDesignInfo dInfo = (DbControlDesignInfo) designInfo;
                return dInfo.getSelectFunction() != null && !dInfo.getSelectFunction().isEmpty();
            }
            return false;
        }

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            if (after != null) {
                if (after.getSelectFunction() != null && !after.getSelectFunction().isEmpty()) {
                    scriptHost.decHandlerUse(after.getSelectFunction());
                }
                after.setSelectFunction(null);
                after.setSelectOnly(false);
            }
        }
    }

    protected class SelectOnlyChangeAction extends DbControlSnapshotAction {

        public SelectOnlyChangeAction() {
            super();
            putValue(Action.NAME, DbControlsDesignUtils.getLocalizedString("chkSelectOnly"));
        }

        @Override
        public boolean isEnabled() {
            if (designInfo instanceof DbControlDesignInfo) {
                DbControlDesignInfo cinfo = (DbControlDesignInfo) designInfo;
                return cinfo.getSelectFunction() != null && !cinfo.getSelectFunction().isEmpty();
            }
            return false;
        }

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            if (after != null) {
                after.setSelectOnly(chkSelectOnly.isSelected());
            }
        }
    }

    protected class HandleFunctionChangeAction extends DbControlSnapshotAction {

        @Override
        public boolean isEnabled() {
            return designInfo instanceof DbControlDesignInfo;
        }

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            if (after != null) {
                String handleFunction = (String) cmbHandleFunction.getSelectedItem();
                if (handleFunction == null || handleFunction.isEmpty()) {
                    ComboBoxEditor cbe = cmbHandleFunction.getEditor();
                    if (cbe != null && cbe.getEditorComponent() != null) {
                        Component ec = cbe.getEditorComponent();
                        if (ec instanceof JTextComponent) {
                            JTextComponent tc = (JTextComponent) ec;
                            handleFunction = tc.getText();
                        }
                    }
                }
                after.setHandleFunction(handleFunction);
            }
        }
    }

    protected class HandleFunctionClearAction extends DbControlSnapshotAction {

        public HandleFunctionClearAction() {
            super();
            putValue(Action.SMALL_ICON, DesignIconCache.getIcon("16x16/delete.png"));
        }

        @Override
        public boolean isEnabled() {
            if (designInfo instanceof DbControlDesignInfo) {
                DbControlDesignInfo dInfo = (DbControlDesignInfo) designInfo;
                return dInfo.getHandleFunction() != null && !dInfo.getHandleFunction().isEmpty();
            }
            return false;
        }

        @Override
        protected void processChangedDesignInfo(DbControlDesignInfo after) {
            if (after != null) {
                if (after.getHandleFunction() != null && !after.getHandleFunction().isEmpty()) {
                    scriptHost.decHandlerUse(after.getHandleFunction());
                }
                after.setHandleFunction(null);
            }
        }
    }

    public DbControlCustomizer() {
        super();
        selectFunctionChangeAction.setEnabled(false);
        selectFunctionClearAction.setEnabled(false);
        selectOnlyChangeAction.setEnabled(false);
        handleFunctionChangeAction.setEnabled(false);
        handleFunctionClearAction.setEnabled(false);
        fieldRenderer.setBorder(new EtchedBorder());
        undoSupport.addUndoableEditListener(new UndoableEditsSender());
    }

    @Override
    public void setObject(Object aObject) {
        if (aObject == null) {
            setUndoSupport(null);
            setScriptHost(null);
            setBean(null);
            editor = null;
        } else {
            if (aObject instanceof UndoableEditSupport) {
                setUndoSupport((UndoableEditSupport) aObject);
            } else if (aObject instanceof UndoManager) {
                editor = (UndoManager) aObject;
            }
            if (aObject instanceof ScriptEvents) {
                setScriptHost((ScriptEvents) aObject);
            }
            if (aObject instanceof DbControl) {
                setBean((DbControl) aObject);
            }
        }
    }

    public void setBean(DbControl aBean) {
        if (bean != aBean) {
            if (aBean != null) {
                if (aBean.getDesignInfo() == null) {
                    try {
                        Class<?> infoClass = DbControlsUtils.getDesignInfoClass(aBean.getClass());
                        if (infoClass != null) {
                            aBean.setDesignInfo((DesignInfo) infoClass.newInstance());
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(DbControlCustomizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            bean = aBean;
            setDesignInfo(aBean != null ? aBean.getDesignInfo() : null);
            updateHandlers();
        }
    }

    public void setDesignInfo(DesignInfo aInfo) {
        if (designInfo != aInfo) {
            if (designInfo != null) {
                bean.getDesignInfo().removePropertyChangeListener(customizerUpdater);
            }
            designInfo = aInfo;
            if (designInfo != null) {
                designInfo.addPropertyChangeListener(customizerUpdater);
            }
            assignDesignInfoSupport2Actions();
            updateView();
            checkActionMap();
        }
    }

    protected abstract void updateView();

    public DesignInfo getDesignInfo() {
        return designInfo;
    }

    public Object getBean() {
        return bean;
    }

    public ApplicationModel<? extends Client> getDatamodel() {
        return bean != null ? bean.getDatamodel() : null;
    }

    public ScriptEvents getScriptHost() {
        return scriptHost;
    }

    public void setScriptHost(ScriptEvents aScriptHost) {
        if (scriptHost != aScriptHost) {
            scriptHost = aScriptHost;
            selectFunctionClearAction.setScriptEvents(aScriptHost);
            selectFunctionChangeAction.setScriptEvents(aScriptHost);
            handleFunctionClearAction.setScriptEvents(aScriptHost);
            handleFunctionChangeAction.setScriptEvents(aScriptHost);
            updateHandlers();
        }
    }
    
    public boolean isEmbedded()
    {
        return scriptHost != null;
    }

    public UndoableEditSupport getUndoSupport() {
        return undoSupport;
    }

    public void setUndoSupport(UndoableEditSupport aSupport) {
        if (undoSupport != aSupport) {
            undoSupport = aSupport;
            assignDesignInfoSupport2Actions();
        }
    }

    protected class UndoableEditsSender implements UndoableEditListener {

        @Override
        public void undoableEditHappened(UndoableEditEvent e) {
            if (editor != null) {
                editor.addEdit(e.getEdit());
            }
        }
    }

    /**
     * Virtual method, invoked by standard design info property change listener.
     * Changes may be transparently propagated from undoable edits or simple
     * property setters.
     *
     * @param aPropertyName Name of the changed property
     * @param oldValue Old value of the changed property
     * @param newValue New value of the changed property
     */
    protected abstract void designInfoPropertyChanged(String aPropertyName, Object oldValue, Object newValue);
}
