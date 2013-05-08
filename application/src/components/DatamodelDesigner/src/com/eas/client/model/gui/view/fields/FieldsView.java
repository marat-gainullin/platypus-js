/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ParametersView.java
 *
 * Created on 08.04.2009, 15:46:23
 */
package com.eas.client.model.gui.view.fields;

import com.bearsoft.rowset.metadata.*;
import com.bearsoft.rowset.utils.CollectionListener;
import com.bearsoft.rowset.utils.RowsetUtils;
import com.eas.client.DbClient;
import com.eas.client.DbMetadataCache;
import com.eas.client.SQLUtils;
import com.eas.client.metadata.TableRef;
import com.eas.client.model.*;
import com.eas.client.model.application.ApplicationParametersEntity;
import com.eas.client.model.dbscheme.DbSchemeModel;
import com.eas.client.model.dbscheme.FieldsEntity;
import com.eas.client.model.gui.DatamodelDesignUtils;
import com.eas.client.model.gui.DmAction;
import com.eas.client.model.gui.IconCache;
import com.eas.client.model.gui.edits.AccessibleCompoundEdit;
import com.eas.client.model.gui.edits.DeleteRelationEdit;
import com.eas.client.model.gui.edits.fields.ChangeFieldEdit;
import com.eas.client.model.gui.edits.fields.DeleteFieldEdit;
import com.eas.client.model.gui.edits.fields.MoveDownFieldEdit;
import com.eas.client.model.gui.edits.fields.MoveUpFieldEdit;
import com.eas.client.model.gui.edits.fields.NewFieldEdit;
import com.eas.client.model.gui.selectors.TablesSelectorCallback;
import com.eas.client.model.gui.view.FieldsListModel;
import com.eas.client.model.gui.view.FieldsParametersListCellRenderer;
import com.eas.client.model.gui.view.FieldsTypeIconsCache;
import com.eas.client.model.query.QueryModel;
import com.eas.client.model.query.QueryParametersEntity;
import com.eas.xml.dom.Source2XmlDom;
import com.eas.xml.dom.XmlDom2String;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.ParameterMetaData;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.UIResource;
import javax.swing.undo.UndoableEditSupport;
import org.w3c.dom.Document;

/**
 *
 * @author mg
 */
public abstract class FieldsView<E extends Entity<M, ?, E>, M extends Model<E, ?, DbClient, ?>> extends JPanel {

    public class ComboTypeRenderer implements ListCellRenderer<Integer> {

        private DefaultListCellRenderer typeRenderer = new DefaultListCellRenderer();

        @Override
        public Component getListCellRendererComponent(JList<? extends Integer> list, Integer value, int index, boolean isSelected, boolean cellHasFocus) {
            Component renderer = typeRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            Object oField = fieldsList.getSelectedValue();
            if (renderer instanceof DefaultListCellRenderer
                    && oField instanceof Field) {
                Field field = (Field) oField;
                DefaultListCellRenderer drenderer = (DefaultListCellRenderer) typeRenderer;
                drenderer.setIcon(null);
                drenderer.setText(null);
                if (value != null) {
                    Integer iValue = (Integer) value;
                    if (RowsetUtils.typesNames.containsKey(iValue)) {
                        String text = SQLUtils.getLocalizedTypeName(iValue);
                        Icon icon = FieldsTypeIconsCache.getIcon16(iValue);
                        if (index == -1
                                && ((iValue == java.sql.Types.STRUCT && field.getTypeInfo().getSqlType() == java.sql.Types.STRUCT)
                                || (iValue == java.sql.Types.OTHER && field.getTypeInfo().getSqlType() == java.sql.Types.OTHER))) {
                            String ltext = SQLUtils.getLocalizedTypeName(field.getTypeInfo().getSqlTypeName());
                            if (ltext != null && !ltext.isEmpty()) {
                                text = ltext;
                            }
                            String lTypeName = field.getTypeInfo().getSqlTypeName();
                            if (lTypeName != null) {
                                lTypeName = lTypeName.toUpperCase();
                            }
                            Icon licon = FieldsTypeIconsCache.getIcon16(lTypeName);
                            if (licon != null) {
                                icon = licon;
                            }
                        }
                        drenderer.setIcon(icon);
                        drenderer.setText(text);
                    } else {
                        drenderer.setText(DatamodelDesignUtils.getLocalizedString("UnknownType"));
                    }
                } else {
                    drenderer.setText(DatamodelDesignUtils.getLocalizedString("Unselected"));
                }
                return drenderer;
            } else {
                return typeRenderer;
            }
        }
    }

    protected class ParameterKindRenderer implements ListCellRenderer<Integer> {

        private DefaultListCellRenderer paramKindRenderer = new DefaultListCellRenderer();

        @Override
        public Component getListCellRendererComponent(JList<? extends Integer> list, Integer value, int index, boolean isSelected, boolean cellHasFocus) {
            String sValue;
            switch (value) {
                case ParameterMetaData.parameterModeIn:
                    sValue = DatamodelDesignUtils.getLocalizedString("in");
                    break;
                case ParameterMetaData.parameterModeOut:
                    sValue = DatamodelDesignUtils.getLocalizedString("out");
                    break;
                case ParameterMetaData.parameterModeInOut:
                    sValue = DatamodelDesignUtils.getLocalizedString("inOut");
                    break;
                default:
                    sValue = DatamodelDesignUtils.getLocalizedString("unknown");
            }
            return paramKindRenderer.getListCellRendererComponent(list, sValue, index, isSelected, cellHasFocus);
        }
    }

    protected void setTextSilent(JTextField aField, String aText) {
        Action action = aField.getAction();
        aField.setAction(null);
        try {
            aField.setText(aText);
        } finally {
            aField.setAction(action);
        }
    }

    protected void setValueToSpinSilent(JSpinner aSpin, Object aValue) {
        ChangeListener[] chListeners = aSpin.getChangeListeners();
        for (ChangeListener chl : chListeners) {
            if (chl instanceof FieldsView.ControlsFocusChangeListener) {
                aSpin.removeChangeListener(chl);
            }
        }
        try {
            aSpin.setValue(aValue);
        } finally {
            for (ChangeListener chl : chListeners) {
                if (chl instanceof FieldsView.ControlsFocusChangeListener) {
                    aSpin.addChangeListener(chl);
                }
            }
        }
    }

    public void updateFieldControls(Field field) throws Exception {
        ftextValue.setEnabled(false);
        if (field != null) {
            setTextSilent(ftextName, field.getName());
            setTextSilent(ftextDescription, field.getDescription());
            setTextSilent(ftextTypeName, field.getTypeInfo().getSqlTypeName());
            setValueToSpinSilent(ftextSize, field.getSize());
            setValueToSpinSilent(ftextScale, field.getScale());
            ftextSize.setEnabled(true);

            ftextScale.setEnabled(false);
            ActionMap am = getActionMap();
            if (am != null) {
                Action action = am.get(ChangeFieldScaleAction.class.getSimpleName());
                ftextScale.setEnabled(action.isEnabled());
                action = am.get(ChangeFieldSizeAction.class.getSimpleName());
                ftextSize.setEnabled(action.isEnabled());
            }

            Action action = comboType.getAction();
            comboType.setAction(null);
            try {
                comboType.setSelectedIndex(-1);
                comboType.setSelectedItem(field.getTypeInfo().getSqlType());
            } finally {
                comboType.setAction(action);
            }
            if (field instanceof Parameter) {
                comboParameterKind.setVisible(true);
                lblStoredProcedure.setVisible(true);
                action = comboParameterKind.getAction();
                comboParameterKind.setAction(null);
                try {
                    comboParameterKind.setSelectedItem(((Parameter) field).getMode());
                } finally {
                    comboParameterKind.setAction(action);
                }
            } else {
                comboParameterKind.setVisible(false);
                lblStoredProcedure.setVisible(false);
            }
            action = chkRequired.getAction();
            chkRequired.setAction(null);
            try {
                chkRequired.setSelected(!field.isNullable());
            } finally {
                chkRequired.setAction(action);
            }

            ftextFkTable.setText("");
            if (field.isFk()) {
                ForeignKeySpec fk = field.getFk();
                String dbId = null;
                if (model instanceof QueryModel) {
                    dbId = ((QueryModel) model).getDbId();
                }
                DbMetadataCache lmdCache = model.getClient().getDbMetadataCache(dbId);
                String tableName = fk.getReferee().getTable();
                String fieldName = fk.getReferee().getField();
                String fullTableName = tableName;
                if (fk.getReferee() != null && fk.getReferee().getSchema() != null && !fk.getReferee().getSchema().isEmpty()
                        && !fk.getReferee().getSchema().equalsIgnoreCase(lmdCache.getConnectionSchema())) {
                    fullTableName = fk.getReferee().getSchema() + "." + fullTableName;
                }
                try {
                    Fields tableFields = lmdCache.getTableMetadata(fullTableName);
                    if (tableFields != null) {
                        String tComment = tableFields.getTableDescription();
                        if (tComment != null && !tComment.isEmpty()) {
                            tableName = tComment;
                        }
                        String fComment = tableFields.get(fieldName).getDescription();
                        if (fComment != null && !fComment.isEmpty()) {
                            fieldName = fComment;
                        }
                    }
                } catch (Exception ex) {
                    Logger.getLogger(FieldsView.class.getName()).log(Level.SEVERE, null, ex);
                }
                fieldName = tableName + "." + fieldName;
                if (fk.getSchema() != null && !fk.getSchema().isEmpty()) {
                    fieldName = fk.getSchema() + "." + fieldName;
                }
                ftextFkTable.setText(fieldName);
            }
        } else {
            ftextFkTable.setText("");
            setTextSilent(ftextName, "");
            setTextSilent(ftextDescription, "");
            setTextSilent(ftextTypeName, "");
            setValueToSpinSilent(ftextSize, 0);
            setValueToSpinSilent(ftextScale, 0);
            ftextSize.setEnabled(false);
            ftextScale.setEnabled(false);

            Action action = comboType.getAction();
            comboType.setAction(null);
            try {
                comboType.setSelectedItem(null);
            } finally {
                comboType.setAction(action);
            }
            if (field instanceof Parameter) {
                comboParameterKind.setVisible(true);
                lblStoredProcedure.setVisible(true);
                action = comboParameterKind.getAction();
                comboParameterKind.setAction(null);
                try {
                    comboParameterKind.setSelectedItem(null);
                } finally {
                    comboParameterKind.setAction(action);
                }
            } else {
                comboParameterKind.setVisible(false);
                lblStoredProcedure.setVisible(false);
            }
            action = chkRequired.getAction();
            chkRequired.setAction(null);
            try {
                chkRequired.setSelected(false);
            } finally {
                chkRequired.setAction(action);
            }
        }
    }

    public class ParametersKindModel implements ComboBoxModel<Integer> {

        private Set<ListDataListener> listeners = new HashSet<>();
        private int[] paramTypes = new int[]{ParameterMetaData.parameterModeIn, ParameterMetaData.parameterModeOut, ParameterMetaData.parameterModeInOut, ParameterMetaData.parameterModeUnknown};
        private Integer selectedItem = ParameterMetaData.parameterModeUnknown;

        public ParametersKindModel() {
            super();
        }

        @Override
        public int getSize() {
            return paramTypes.length;
        }

        @Override
        public void addListDataListener(ListDataListener l) {
            if (!listeners.contains(l)) {
                listeners.add(l);
            }
        }

        @Override
        public void removeListDataListener(ListDataListener l) {
            listeners.remove(l);
        }

        @Override
        public void setSelectedItem(Object anItem) {
            if (anItem instanceof Integer) {
                selectedItem = (Integer) anItem;
            }
        }

        @Override
        public Integer getSelectedItem() {
            if (selectedItem != null) {
                return selectedItem;
            } else {
                return null;
            }
        }

        public void fireDataChanged() {
            for (ListDataListener l : listeners) {
                if (l != null) {
                    l.contentsChanged(new ListDataEvent(ParametersKindModel.this, ListDataEvent.CONTENTS_CHANGED, 0, getSize() - 1));
                }
            }
        }

        @Override
        public Integer getElementAt(int index) {
            if (index >= 0 && index < paramTypes.length) {
                return paramTypes[index];
            } else {
                return null;
            }
        }
    }

    public class AddField extends DmAction {

        public AddField() {
            super();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isEnabled()) {
                NewFieldEdit<E> edit = new NewFieldEdit<>(entity);
                Field fParam = edit.getField();
                if (fParam != null && fParam instanceof Parameter) {
                    Parameter param = (Parameter) fParam;
                    String lName = param.getName();
                    int lNameCounter = 0;
                    while (model.isNamePresent(lName, null, param)) {
                        lNameCounter++;
                        lName = param.getName() + String.valueOf(lNameCounter);
                    }
                    param.setName(lName);
                }
                edit.redo();
                undoSupport.postEdit(edit);
            }
            checkActions();
        }

        @Override
        public boolean isEnabled() {
            return entity != null;
        }

        @Override
        public String getDmActionText() {
            return DatamodelDesignUtils.getLocalizedString(AddField.class.getSimpleName());
        }

        @Override
        public String getDmActionHint() {
            return DatamodelDesignUtils.getLocalizedString(AddField.class.getSimpleName() + ".hint");
        }

        @Override
        public Icon getDmActionSmallIcon() {
            return IconCache.getIcon("new.png");
        }

        @Override
        public KeyStroke getDmActionAccelerator() {
            return KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0);
        }
    }

    public class MoveUpAction extends AbstractAction {

        public MoveUpAction() {
            super();
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.CTRL_DOWN_MASK));
            setEnabled(false);
        }

        @Override
        public boolean isEnabled() {
            return !fieldsList.isSelectionEmpty() && fieldsList.getSelectedIndices().length == 1
                    && fieldsList.getSelectedIndex() > 0;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isEnabled()) {
                Field fmd = (Field) fieldsList.getSelectedValue();
                Fields fields = entity.getFields();
                int fieldIdx = fields.find(fmd.getName());

                if (fieldIdx > 1) {
                    MoveUpFieldEdit<E> edit = new MoveUpFieldEdit<>(entity, fmd);
                    edit.redo();
                    undoSupport.postEdit(edit);
                    fieldsList.setSelectedIndex(fieldIdx - 2);
                    fieldsList.requestFocus();
                }
            }
        }
    }

    public class MoveDownAction extends AbstractAction {

        public MoveDownAction() {
            super();
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.CTRL_DOWN_MASK));
            setEnabled(false);
        }

        @Override
        public boolean isEnabled() {
            return !fieldsList.isSelectionEmpty() && fieldsList.getSelectedIndices().length == 1
                    && fieldsList.getSelectedIndex() < fieldsList.getModel().getSize() - 1;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isEnabled()) {
                Field fmd = (Field) fieldsList.getSelectedValue();
                Fields fields = entity.getFields();
                int fieldIdx = fields.find(fmd.getName());
                if (fieldIdx < fields.getFieldsCount()) {
                    MoveDownFieldEdit<E> edit = new MoveDownFieldEdit<>(entity, fmd);
                    edit.redo();
                    undoSupport.postEdit(edit);
                    fieldsList.setSelectedIndex(fieldIdx);
                    fieldsList.requestFocus();
                }
            }
        }
    }

    public abstract class ChangeFieldAction extends AbstractAction {

        protected Set<Relation<E>> toProcessRels;
        protected Field before;
        protected Field after;

        public ChangeFieldAction() {
            super();
            setEnabled(false);
        }

        @Override
        public boolean isEnabled() {
            boolean isSelection = !fieldsList.isSelectionEmpty() && fieldSelectionExists();
            return isSelection && entity != null && field != null;
        }

        protected abstract void doWork();

        protected void editFieldsInDiagram() {
            if (!before.equals(after)) {
                ChangeFieldEdit<E> edit = new ChangeFieldEdit<>(before, after, field, entity);
                edit.redo();
                undoSupport.postEdit(edit);
            }
        }

        protected boolean beforeEditingFields() {
            return !before.equals(after);
        }

        protected void afterEditingFields() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (field != null) {
                focusAndChangeToActionPerformer.disableFocus();
                try {
                    toProcessRels = new HashSet<>();
                    before = null;
                    after = null;
                    if (field instanceof Parameter) {
                        Parameter lparam = (Parameter) field;
                        before = new Parameter(lparam);
                        after = new Parameter(lparam);
                    } else {
                        before = new Field(field);
                        after = new Field(field);
                    }
                    toProcessRels = FieldsEntity.<E>getInOutRelationsByEntityField(entity, field);
                    doWork();
                    if (beforeEditingFields()) {
                        editFieldsInDiagram();
                        afterEditingFields();
                    }
                } finally {
                    focusAndChangeToActionPerformer.enableFocus();
                }
            }
        }

        protected void checkTypedLengthScale(Field after) {
            if (after != null) {
                if (SQLUtils.isSameTypeGroup(after.getTypeInfo().getSqlType(), java.sql.Types.VARCHAR)) {
                    if (after.getPrecision() <= 0 || after.getSize() <= 0) {
                        after.setPrecision(1);
                        after.setSize(100);
                    }
                } else if (SQLUtils.isSameTypeGroup(after.getTypeInfo().getSqlType(), java.sql.Types.NUMERIC)) {
                    if (after.getPrecision() <= 0 || after.getSize() <= 0) {
                        after.setPrecision(0);
                        after.setSize(0);
                    }
                    if (after.getPrecision() > 15 || after.getSize() > 15) {
                        after.setPrecision(0);
                        after.setSize(0);
                    }
                } else if (SQLUtils.isSameTypeGroup(after.getTypeInfo().getSqlType(), java.sql.Types.BOOLEAN)) {
                } else if (SQLUtils.isSameTypeGroup(after.getTypeInfo().getSqlType(), java.sql.Types.TIME)) {
                } else if (SQLUtils.isSameTypeGroup(after.getTypeInfo().getSqlType(), java.sql.Types.BLOB)) {
                }
            }
        }
    }

    public class ChangeFieldNameAction extends ChangeFieldAction {

        public ChangeFieldNameAction() {
            super();
        }

        @Override
        protected void doWork() {
            String newName = ftextName.getText();
            if (newName != null && !newName.isEmpty()
                    && !entity.getFields().isNameAlreadyPresent(newName, field)
                    && DatamodelDesignUtils.isLegalFieldName(newName)) {
                after.setName(newName);
            } else {
                if (newName == null || newName.isEmpty()) {
                    JOptionPane.showMessageDialog(splitParameters, DatamodelDesignUtils.getLocalizedString("NameIsReqired"), (entity instanceof ApplicationParametersEntity || entity instanceof QueryParametersEntity) ? DatamodelDesignUtils.getLocalizedString("Parameters") : DatamodelDesignUtils.getLocalizedString("Fields"), JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(splitParameters, (entity instanceof ApplicationParametersEntity || entity instanceof QueryParametersEntity) ? DatamodelDesignUtils.getLocalizedString("BadParameterName") : DatamodelDesignUtils.getLocalizedString("BadFieldName"), (entity instanceof ApplicationParametersEntity || entity instanceof QueryParametersEntity) ? DatamodelDesignUtils.getLocalizedString("Parameters") : DatamodelDesignUtils.getLocalizedString("Fields"), JOptionPane.WARNING_MESSAGE);
                }
                Action action = ftextName.getAction();
                ftextName.setAction(null);
                try {
                    ftextName.setText(field.getName());
                } finally {
                    ftextName.setAction(action);
                }
            }
        }

        @Override
        protected void editFieldsInDiagram() {
            if (!before.equals(after)) {
                ChangeFieldEdit<E> edit = new ChangeFieldEdit<>(before, after, field, entity); //o == ftextName ? entity : null);
                edit.redo();
                undoSupport.postEdit(edit);
            }
        }
    }

    public class ChangeFieldDescriptionAction extends ChangeFieldAction {

        @Override
        protected void doWork() {
            after.setDescription(ftextDescription.getText());
            if (after.getDescription() != null) {
                after.setDescription(after.getDescription().trim());
            }
        }
    }

    public class ChangeFieldSizeAction extends ChangeFieldAction {

        public ChangeFieldSizeAction() {
            super();
        }

        @Override
        public boolean isEnabled() {
            return super.isEnabled()
                    && !SQLUtils.isSameTypeGroup(field.getTypeInfo().getSqlType(), java.sql.Types.BLOB)
                    && !SQLUtils.isSameTypeGroup(field.getTypeInfo().getSqlType(), java.sql.Types.DATE)
                    && field.getTypeInfo().getSqlType() != java.sql.Types.STRUCT
                    && field.getTypeInfo().getSqlType() != java.sql.Types.OTHER
                    && field.getTypeInfo().getSqlType() != java.sql.Types.LONGVARCHAR
                    && field.getTypeInfo().getSqlType() != java.sql.Types.LONGNVARCHAR;
        }

        @Override
        protected void doWork() {
            Object ofValue = ftextSize.getValue();
            if (ofValue != null && ofValue instanceof Integer) {
                int lSize = (Integer) ofValue;
                if (SQLUtils.isSimpleTypesCompatible(after.getTypeInfo().getSqlType(), java.sql.Types.NUMERIC)
                        && lSize > 38) {
                    lSize = 38;
                }
                if (lSize < 0) {
                    lSize = 0;
                }
                after.setSize(lSize);
                after.setPrecision(lSize);
            } else {
                after.setSize(1);
                after.setPrecision(1);
            }
        }
    }

    public class ChangeFieldScaleAction extends ChangeFieldAction {

        public ChangeFieldScaleAction() {
            super();
        }

        @Override
        public boolean isEnabled() {
            return super.isEnabled() && SQLUtils.isSimpleTypesCompatible(field.getTypeInfo().getSqlType(), java.sql.Types.NUMERIC);
        }

        @Override
        protected void doWork() {
            Object ofValue = ftextScale.getValue();
            if (ofValue != null && ofValue instanceof Integer) {
                after.setScale((Integer) ofValue);
            }
        }
    }

    public class ChangeFieldValueAction extends ChangeFieldAction {

        public ChangeFieldValueAction() {
            super();
        }

        @Override
        public boolean isEnabled() {
            return super.isEnabled() && !fieldsList.isSelectionEmpty() && fieldSelectionExists();
        }

        @Override
        protected void doWork() {
        }
    }

    public class ChangeFieldWithRelsAction extends ChangeFieldAction {

        @Override
        protected void doWork() {
            Set<Relation<E>> rels = FieldsEntity.<E>getInOutRelationsByEntityField(entity, field);
            for (Relation<E> rel : rels) {
                Field lfield;
                Field rfield;
                if (rel.isLeftField()) {
                    lfield = rel.getLeftField();
                    if (rel.getLeftEntity() == entity
                            && before.getName().equals(lfield.getName())) {
                        lfield = after;
                    }
                } else {
                    lfield = rel.getLeftParameter();
                    if (rel.getLeftEntity() == entity && before.getName().equals(lfield.getName())) {
                        lfield = after;
                    }
                }

                if (rel.isRightField()) {
                    rfield = rel.getRightField();
                    if (rel.getRightEntity() == entity
                            && before.getName().equals(rfield.getName())) {
                        rfield = after;
                    }
                } else {
                    rfield = rel.getRightParameter();
                    if (rel.getRightEntity() == entity && before.getName().equals(rfield.getName())) {
                        rfield = after;
                    }
                }

                if ((lfield.isPk() || lfield.isFk())
                        && (rfield.isPk() || rfield.isFk())) {
                    if (!SQLUtils.isKeysCompatible(lfield, rfield)) {
                        toProcessRels.add(rel);
                    }
                } else if (!SQLUtils.isSimpleTypesCompatible(lfield.getTypeInfo().getSqlType(), rfield.getTypeInfo().getSqlType())) {
                    toProcessRels.add(rel);
                }
            }
            if (!toProcessRels.isEmpty()) {
                Action action = comboType.getAction();
                comboType.setAction(null);
                try {
                    if (JOptionPane.showConfirmDialog(splitParameters, (entity instanceof ApplicationParametersEntity || entity instanceof QueryParametersEntity) ? DatamodelDesignUtils.getLocalizedString("BadParameterType") : DatamodelDesignUtils.getLocalizedString("BadFieldType"), (entity instanceof ApplicationParametersEntity || entity instanceof QueryParametersEntity) ? DatamodelDesignUtils.getLocalizedString("Parameters") : DatamodelDesignUtils.getLocalizedString("Fields"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.CANCEL_OPTION) {
                        comboType.setSelectedItem(before.getTypeInfo().getSqlType());
                        after.getTypeInfo().setSqlType(before.getTypeInfo().getSqlType());
                    }
                } finally {
                    comboType.setAction(action);
                }
            }
        }

        @Override
        protected void editFieldsInDiagram() {
            if (!before.equals(after)) {
                AccessibleCompoundEdit section = new AccessibleCompoundEdit();
                for (Relation<E> rel : toProcessRels) {
                    DeleteRelationEdit<E> drEdit = new DeleteRelationEdit<>(rel);
                    drEdit.redo();
                    section.addEdit(drEdit);
                }
                ChangeFieldEdit<E> edit = new ChangeFieldEdit<>(before, after, field, entity);
                edit.redo();
                section.addEdit(edit);
                section.end();
                undoSupport.postEdit(section);
            }
        }
    }

    public class ChangeFieldTypeAction extends ChangeFieldWithRelsAction {

        public ChangeFieldTypeAction() {
            super();
        }

        @Override
        public boolean isEnabled() {
            return super.isEnabled() && !field.isPk() && !field.isFk();
        }

        @Override
        protected void doWork() {
            toProcessRels.clear();
            Object ofValue = comboType.getSelectedItem();
            if (ofValue != null && ofValue instanceof Integer) {
                int type = (Integer) ofValue;
                if (type != after.getTypeInfo().getSqlType()) {
                    after.getTypeInfo().setSqlType(type);
                    checkTypedLengthScale(after);
                    super.doWork();
                }
            }
        }
    }

    public class ChangeParameterKindAction extends ChangeFieldAction {

        public ChangeParameterKindAction() {
            super();
        }

        @Override
        public boolean isEnabled() {
            return field instanceof Parameter;
        }

        @Override
        protected void doWork() {
            Object oParameterKindValue = comboParameterKind.getSelectedItem();
            if (oParameterKindValue != null && oParameterKindValue instanceof Integer) {
                int paramKind = (Integer) oParameterKindValue;
                if (paramKind != ((Parameter) after).getMode()) {
                    ((Parameter) after).setMode(paramKind);
                }
            }
        }
    }

    public class ChangeFieldTypeNameAction extends ChangeFieldWithRelsAction {

        public ChangeFieldTypeNameAction() {
            super();
        }

        @Override
        public boolean isEnabled() {
            return super.isEnabled() && !field.isPk() && !field.isFk() && (SQLUtils.isSameTypeGroup(field.getTypeInfo().getSqlType(), java.sql.Types.STRUCT) || SQLUtils.isSameTypeGroup(field.getTypeInfo().getSqlType(), java.sql.Types.OTHER));
        }

        @Override
        protected void doWork() {
            String typeName = ftextTypeName.getText();
            if (typeName == null || typeName.isEmpty()) {
                typeName = "";
            }
            after.getTypeInfo().setSqlTypeName(typeName);
            super.doWork();
        }
    }

    public class ChangeFieldFkAction extends ChangeFieldWithRelsAction {

        public ChangeFieldFkAction() {
            super();
            putValue(Action.NAME, "...");
        }

        @Override
        public boolean isEnabled() {
            return super.isEnabled() && !(entity.getModel() instanceof DbSchemeModel);
        }

        @Override
        protected void doWork() {
            try {
                TableRef oldValue = new TableRef();
                oldValue.dbId = null;
                oldValue.schema = null;
                oldValue.tableName = null;
                TableRef[] selected = selectorCallback.selectTableRef(oldValue);
                if (selected != null) {
                    TableRef tr = selected[selected.length - 1];
                    String fullTableName = tr.tableName;
                    if (tr.schema != null && !tr.schema.isEmpty()) {
                        fullTableName = tr.schema + "." + fullTableName;
                    }
                    Fields refTableKeys = null;
                    try {
                        DbMetadataCache lmdCache = model.getClient().getDbMetadataCache(tr.dbId);
                        refTableKeys = lmdCache.getTableMetadata(fullTableName);
                    } catch (Exception ex) {
                        Logger.getLogger(FieldsView.class.getName()).log(Level.SEVERE, null, ex);
                        return;
                    }
                    if (refTableKeys != null) {
                        List<Field> lpks = refTableKeys.getPrimaryKeys();
                        if (lpks.size() == 1) {
                            Field lpkField = lpks.get(0);
                            ForeignKeySpec lpk = new ForeignKeySpec(field.getSchemaName(), field.getTableName(), field.getName(), "", ForeignKeySpec.ForeignKeyRule.CASCADE, ForeignKeySpec.ForeignKeyRule.CASCADE, false, tr.schema, tr.tableName, lpkField.getName(), null);
                            after.setFk(lpk);
                            after.getTypeInfo().setSqlType(lpkField.getTypeInfo().getSqlType());
                            String loldDesc = after.getDescription();
                            if (loldDesc == null || loldDesc.isEmpty()) {
                                after.setDescription(lpkField.getDescription());
                            }
                        } else if (lpks.size() > 1) {
                            JOptionPane.showMessageDialog(ftextName, DatamodelDesignUtils.getLocalizedString("MultipleKeydTablesNotSupported"), DatamodelDesignUtils.getLocalizedString("Parameters"), JOptionPane.INFORMATION_MESSAGE);
                            return;
                        } else {
                            JOptionPane.showMessageDialog(ftextName, DatamodelDesignUtils.getLocalizedString("TablesWithoutPkNotSupported"), DatamodelDesignUtils.getLocalizedString("Parameters"), JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }
                    }
                    super.doWork();
                }
            } catch (Exception ex) {
                Logger.getLogger(FieldsView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public class ClearFieldFkAction extends ChangeFieldWithRelsAction {

        public ClearFieldFkAction() {
            super();
            putValue(Action.NAME, "X");
        }

        @Override
        public boolean isEnabled() {
            return super.isEnabled() && !(entity.getModel() instanceof DbSchemeModel);
        }

        @Override
        protected void doWork() {
            try {
                after.setFk(null);
                super.doWork();
            } catch (Exception ex) {
                Logger.getLogger(FieldsView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public class ChangeFieldReqiredAction extends ChangeFieldAction {

        public ChangeFieldReqiredAction() {
            super();
            putValue(Action.NAME, DatamodelDesignUtils.getLocalizedString(ChangeFieldReqiredAction.class.getSimpleName()));
            putValue(Action.SHORT_DESCRIPTION, DatamodelDesignUtils.getLocalizedString(ChangeFieldReqiredAction.class.getSimpleName() + ".hint"));
        }

        @Override
        public boolean isEnabled() {
            return super.isEnabled() && (!field.isPk() || field instanceof Parameter);
        }

        @Override
        protected void doWork() {
            try {
                after.setNullable(!chkRequired.isSelected());
                updateFieldControls(field);
            } catch (Exception ex) {
                Logger.getLogger(FieldsView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public class Delete extends DmAction {

        protected boolean forceRelationsDelete = false;

        public Delete() {
            super();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isEnabled()) {
                Field[] selected = getSelectedFields();
                if (selected != null && selected.length > 0) {
                    if (!forceRelationsDelete) {
                        Set<Relation<E>> toConfirm = new HashSet<>();
                        for (int i = 0; i < selected.length; i++) {
                            Set<Relation<E>> toDel = FieldsEntity.<E>getInOutRelationsByEntityField(entity, selected[i]);
                            toConfirm.addAll(toDel);
                        }
                        if (!toConfirm.isEmpty()) {
                            if (JOptionPane.showConfirmDialog(FieldsView.this, DatamodelDesignUtils.getLocalizedString("ifDeleteRelationsReferences"), DatamodelDesignUtils.getLocalizedString("datamodel"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.CANCEL_OPTION) {
                                return;
                            }
                        }
                    }
                    int sIndex = fieldsList.getLeadSelectionIndex();
                    AccessibleCompoundEdit section = new AccessibleCompoundEdit();

                    for (int i = 0; i < selected.length; i++) {
                        Set<Relation<E>> toDel = FieldsEntity.<E>getInOutRelationsByEntityField(entity, selected[i]);
                        for (Relation<E> rel : toDel) {
                            DeleteRelationEdit<E> drEdit = new DeleteRelationEdit<>(rel);
                            drEdit.redo();
                            section.addEdit(drEdit);
                        }
                        DeleteFieldEdit<E> edit = new DeleteFieldEdit<>(entity, selected[i]);
                        edit.redo();
                        section.addEdit(edit);
                    }
                    section.end();
                    undoSupport.postEdit(section);
                    fieldsList.clearSelection();
                    int listModelSize = fieldsList.getModel().getSize();
                    if (listModelSize > 0) {
                        if (sIndex >= listModelSize) {
                            sIndex = listModelSize - 1;
                        }
                        fieldsList.setSelectedIndex(sIndex);
                        fieldsList.requestFocus();
                    }
                }
            }
        }

        @Override
        public boolean isEnabled() {
            return isAnyFieldSelected();
        }

        @Override
        public String getDmActionText() {
            return DatamodelDesignUtils.getLocalizedString(Delete.class.getSimpleName());
        }

        @Override
        public String getDmActionHint() {
            return DatamodelDesignUtils.getLocalizedString(Delete.class.getSimpleName() + ".hint");
        }

        @Override
        public Icon getDmActionSmallIcon() {
            return IconCache.getIcon("delete.png");
        }

        @Override
        public KeyStroke getDmActionAccelerator() {
            return KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        }
    }

    public class Cut extends DmAction {

        public Cut() {
            super();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isEnabled()) {
                Action copy = getActionMap().get(Copy.class.getSimpleName());
                Action delete = getActionMap().get(Delete.class.getSimpleName());
                if (copy != null && delete != null) {
                    ActionEvent ae = new ActionEvent(e.getSource(), 0, "");
                    copy.actionPerformed(ae);
                    delete.actionPerformed(ae);
                }
                checkActions();
            }
        }

        @Override
        public boolean isEnabled() {
            return isAnyFieldSelected();
        }

        @Override
        public String getDmActionText() {
            return DatamodelDesignUtils.getLocalizedString(Cut.class.getSimpleName());
        }

        @Override
        public String getDmActionHint() {
            return DatamodelDesignUtils.getLocalizedString(Cut.class.getSimpleName() + ".hint");
        }

        @Override
        public Icon getDmActionSmallIcon() {
            return IconCache.getIcon("cut.png");
        }

        @Override
        public KeyStroke getDmActionAccelerator() {
            return KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK);
        }
    }

    public abstract M newModel();

    public class Copy extends DmAction {

        public Copy() {
            super();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isEnabled()) {
                Field[] fields = getSelectedFields();
                if (fields != null && fields.length > 0) {
                    try {
                        M copiedModel = newModel();
                        copiedModel.setClient(model.getClient());
                        Parameters parameters = copiedModel.getParameters();
                        if (parameters != null) {
                            try {
                                for (int i = 0; i < fields.length; i++) {
                                    if (fields[i] != null) {
                                        // fields[i] may be Parameter or Field.
                                        // We need to copy strictly parameters.
                                        parameters.add(new Parameter(fields[i]));
                                    }
                                }
                                Document doc = copiedModel.toXML();
                                if (doc != null) {
                                    String sEntity = XmlDom2String.transform(doc);
                                    string2SystemClipboard(sEntity);
                                }
                            } finally {
                                parameters.clear();
                            }
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(FieldsView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        @Override
        public boolean isEnabled() {
            return isAnyFieldSelected();
        }

        @Override
        public String getDmActionText() {
            return DatamodelDesignUtils.getLocalizedString(Copy.class.getSimpleName());
        }

        @Override
        public String getDmActionHint() {
            return DatamodelDesignUtils.getLocalizedString(Copy.class.getSimpleName() + ".hint");
        }

        @Override
        public Icon getDmActionSmallIcon() {
            return IconCache.getIcon("copy.png");
        }

        @Override
        public KeyStroke getDmActionAccelerator() {
            return KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK);
        }

        protected void string2SystemClipboard(String sEntity) throws HeadlessException {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            if (sEntity != null && clipboard != null) {
                StringSelection ss = new StringSelection(sEntity);
                clipboard.setContents(ss, ss);
            }
        }
    }

    protected abstract M document2Model(Document aDoc) throws Exception;

    public class Paste extends DmAction {

        public Paste() {
            super();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isEnabled()) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                if (clipboard != null) {
                    Transferable tr = clipboard.getContents(null);
                    if (tr != null) {
                        try {
                            Object oData = tr.getTransferData(DataFlavor.stringFlavor);
                            if (oData != null && oData instanceof String) {
                                String sData = (String) oData;
                                Document doc = Source2XmlDom.transform(sData);
                                if (doc != null) {
                                    M pastedModel = document2Model(doc);
                                    if (pastedModel != null && pastedModel != null) {
                                        Parameters pastedParameters = pastedModel.getParameters();
                                        Parameters parameters = model.getParameters();
                                        if (pastedParameters != null && !pastedParameters.isEmpty()
                                                && parameters != null) {
                                            AccessibleCompoundEdit section = new AccessibleCompoundEdit();
                                            for (int i = 0; i < pastedParameters.getParametersCount(); i++) {
                                                Parameter pastedParam = pastedParameters.get(i + 1);
                                                if (parameters.get(pastedParam.getName()) != null) {
                                                    pastedParam.setName(parameters.generateNewName(pastedParam.getName()));
                                                }
                                                String lName = pastedParam.getName();
                                                int lNameCounter = 0;
                                                while (model.isNamePresent(lName, null, pastedParam)) {
                                                    lNameCounter++;
                                                    lName = pastedParam.getName() + String.valueOf(lNameCounter);
                                                }
                                                pastedParam.setName(lName);
                                                if (DatamodelDesignUtils.isLegalFieldName(pastedParam.getName())) {
                                                    NewFieldEdit<E> edit = new NewFieldEdit<>(model.getParametersEntity());
                                                    edit.redo();
                                                    section.addEdit(edit);
                                                    ChangeFieldEdit<E> cEdit = new ChangeFieldEdit<>(edit.getField(), pastedParam, edit.getField(), model.getParametersEntity());
                                                    cEdit.redo();
                                                    section.addEdit(cEdit);
                                                }
                                            }
                                            section.end();
                                            undoSupport.postEdit(section);
                                        }
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(FieldsView.this, DatamodelDesignUtils.getLocalizedString("BadClipboardData"), DatamodelDesignUtils.getLocalizedString("datamodel"), JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        } catch (UnsupportedFlavorException ex) {
                            JOptionPane.showMessageDialog(FieldsView.this, DatamodelDesignUtils.getLocalizedString("BadClipboardData"), DatamodelDesignUtils.getLocalizedString("datamodel"), JOptionPane.ERROR_MESSAGE);
                            Logger.getLogger(FieldsView.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(FieldsView.this, DatamodelDesignUtils.getLocalizedString("BadClipboardData"), DatamodelDesignUtils.getLocalizedString("datamodel"), JOptionPane.ERROR_MESSAGE);
                            Logger.getLogger(FieldsView.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                checkActions();
            }
        }

        @Override
        public boolean isEnabled() {
            if (Toolkit.getDefaultToolkit() != null) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                if (clipboard != null) {
                    return clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor);
                }
            }
            return false;
        }

        @Override
        public String getDmActionText() {
            return DatamodelDesignUtils.getLocalizedString(Paste.class.getSimpleName());
        }

        @Override
        public String getDmActionHint() {
            return DatamodelDesignUtils.getLocalizedString(Paste.class.getSimpleName() + ".hint");
        }

        @Override
        public Icon getDmActionSmallIcon() {
            return IconCache.getIcon("paste.png");
        }

        @Override
        public KeyStroke getDmActionAccelerator() {
            return KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK);
        }
    }

    public class ControlsFocusChangeListener implements FocusListener, ChangeListener {

        protected boolean focusEnabled = true;

        public ControlsFocusChangeListener() {
            super();
        }

        @Override
        public void focusGained(FocusEvent e) {
        }

        @Override
        public void focusLost(FocusEvent e) {
            if (focusEnabled) {
                Object oValue = fieldsList.getSelectedValue();
                if (oValue != null && oValue instanceof Field) {
                    Field field = (Field) oValue;
                    if (e.getSource() == ftextName) {
                        String cText = ftextName.getText();
                        if (cText == null) {
                            cText = "";
                        }
                        String dText = field.getName();
                        if (dText == null) {
                            dText = "";
                        }
                        if (!cText.equalsIgnoreCase(dText)) {
                            if (e.getSource() instanceof JTextField) {
                                Action lAction = ((JTextField) e.getSource()).getAction();
                                if (lAction != null) {
                                    lAction.actionPerformed(new ActionEvent(e.getSource(), 0, ""));
                                }
                            }
                        }
                    } else if (e.getSource() == ftextDescription) {

                        String cText = ftextDescription.getText();
                        if (cText == null) {
                            cText = "";
                        }
                        String dText = field.getDescription();
                        if (dText == null) {
                            dText = "";
                        }
                        if (!cText.equals(dText)) {
                            if (e.getSource() instanceof JTextField) {
                                Action lAction = ((JTextField) e.getSource()).getAction();
                                if (lAction != null) {
                                    lAction.actionPerformed(new ActionEvent(e.getSource(), 0, ""));
                                }
                            }
                        }
                    } else if (e.getSource() == ftextTypeName) {
                        String cText = ftextTypeName.getText();
                        if (cText == null) {
                            cText = "";
                        }
                        String dText = field.getTypeInfo().getSqlTypeName();
                        if (dText == null) {
                            dText = "";
                        }
                        if (!cText.equalsIgnoreCase(dText)) {
                            if (e.getSource() instanceof JTextField) {
                                Action lAction = ((JTextField) e.getSource()).getAction();
                                if (lAction != null) {
                                    lAction.actionPerformed(new ActionEvent(e.getSource(), 0, ""));
                                }
                            }
                        }
                    }
                }
            }
        }

        public void disableFocus() {
            focusEnabled = false;
        }

        public void enableFocus() {
            focusEnabled = true;
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            if (e.getSource() == ftextSize) {
                ActionMap am = getActionMap();
                if (am != null) {
                    Action action = am.get(ChangeFieldSizeAction.class.getSimpleName());
                    if (action.isEnabled()) {
                        action.actionPerformed(new ActionEvent(e.getSource(), 0, ""));
                    }
                }
            } else {
                if (e.getSource() == ftextScale) {
                    ActionMap am = getActionMap();
                    if (am != null) {
                        Action action = am.get(ChangeFieldScaleAction.class.getSimpleName());
                        if (action.isEnabled()) {
                            action.actionPerformed(new ActionEvent(e.getSource(), 0, ""));
                        }
                    }
                }
            }
            if (e.getSource() instanceof AbstractButton) {
                Action lAction = ((AbstractButton) e.getSource()).getAction();
                if (lAction != null) {
                    lAction.actionPerformed(new ActionEvent(e.getSource(), 0, ""));
                }
            } else if (e.getSource() instanceof JTextField) {
                Action lAction = ((JTextField) e.getSource()).getAction();
                if (lAction != null) {
                    lAction.actionPerformed(new ActionEvent(e.getSource(), 0, ""));
                }
            }
        }
    }
    // environment
    protected E entity;
    protected M model;
    // selected field
    protected Field field;
    // components
    private FieldsTypesModel typesModel;
    private ParametersKindModel parametersKindModel = new ParametersKindModel();
    private FieldsListModel<Field> fieldsModel;
    // interaction
    private ControlsFocusChangeListener focusAndChangeToActionPerformer = new ControlsFocusChangeListener();
    protected UndoableEditSupport undoSupport = new UndoableEditSupport();
    protected FieldsViewModelChangesListener fieldsChangeListener = new FieldsViewModelChangesListener();
    protected FieldChangeListener fieldChangeListener = new FieldChangeListener();
    protected FieldInFieldsListChangesReflector fieldInFieldsListChangesReflector = new FieldInFieldsListChangesReflector();
    protected TablesSelectorCallback selectorCallback;

    /**
     * Registers an
     * <code>UndoableEditListener</code>. The listener is notified whenever an
     * edit occurs which can be undone.
     *
     * @param l an <code>UndoableEditListener</code> object
     * @see #removeUndoableEditListener
     */
    public synchronized void addUndoableEditListener(UndoableEditListener l) {
        undoSupport.addUndoableEditListener(l);
    }

    /**
     * Removes an
     * <code>UndoableEditListener</code>.
     *
     * @param l the <code>UndoableEditListener</code> object to be removed
     * @see #addUndoableEditListener
     */
    public synchronized void removeUndoableEditListener(UndoableEditListener l) {
        undoSupport.removeUndoableEditListener(l);
    }

    public FieldsView(TablesSelectorCallback aSelectorCallback) throws Exception {
        super();
        selectorCallback = aSelectorCallback;

        fillActions();
        initComponents();

        fieldsModel = new FieldsListModel.FieldsModel();
        fieldsModel.addListDataListener(new FieldsListModelListener());
        fieldsList.setModel(fieldsModel);
        fieldsList.addListSelectionListener(new FieldsSelectionListener());
        fieldsList.setTransferHandler(null);

        ActionMap am = getActionMap();
        comboType.setEditable(false);
        comboType.setModel(typesModel);
        comboType.setRenderer(new ComboTypeRenderer());

        comboParameterKind.setEditable(false);
        comboParameterKind.setModel(parametersKindModel);
        comboParameterKind.setRenderer(new ParameterKindRenderer());

        ftextName.setAction(am.get(ChangeFieldNameAction.class.getSimpleName()));
        ftextName.addFocusListener(focusAndChangeToActionPerformer);
        comboType.setAction(am.get(ChangeFieldTypeAction.class.getSimpleName()));
        comboParameterKind.setAction(am.get(ChangeParameterKindAction.class.getSimpleName()));
        ftextTypeName.setAction(am.get(ChangeFieldTypeNameAction.class.getSimpleName()));
        ftextTypeName.addFocusListener(focusAndChangeToActionPerformer);
        ftextDescription.setAction(am.get(ChangeFieldDescriptionAction.class.getSimpleName()));
        ftextDescription.addFocusListener(focusAndChangeToActionPerformer);
        ftextSize.addChangeListener(focusAndChangeToActionPerformer);
        if (ftextSize.getEditor() instanceof JSpinner.NumberEditor) {
            JSpinner.NumberEditor leditor = (JSpinner.NumberEditor) ftextSize.getEditor();
            final JFormattedTextField ftf = leditor.getTextField();
            ftf.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String txt = ftf.getText();
                    if (txt != null && !txt.isEmpty()) {
                        try {
                            ftextSize.commitEdit();
                        } catch (ParseException ex) {
                            Logger.getLogger(FieldsView.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });
        }

        ftextScale.addChangeListener(focusAndChangeToActionPerformer);
        if (ftextScale.getEditor() instanceof JSpinner.NumberEditor) {
            JSpinner.NumberEditor leditor = (JSpinner.NumberEditor) ftextScale.getEditor();
            final JFormattedTextField ftf = leditor.getTextField();
            ftf.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String txt = ftf.getText();
                    if (txt != null && !txt.isEmpty()) {
                        try {
                            ftextScale.commitEdit();
                        } catch (ParseException ex) {
                            Logger.getLogger(FieldsView.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });
        }

        btnFkTable.setAction(am.get(ChangeFieldFkAction.class.getSimpleName()));
        btnFkDel.setAction(am.get(ClearFieldFkAction.class.getSimpleName()));

        chkRequired.setAction(am.get(ChangeFieldReqiredAction.class.getSimpleName()));

        InputMap im = DatamodelDesignUtils.fillInputMap(getActionMap());
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.SHIFT_DOWN_MASK), Cut.class.getSimpleName());
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, KeyEvent.CTRL_DOWN_MASK), Copy.class.getSimpleName());
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, KeyEvent.SHIFT_DOWN_MASK), Paste.class.getSimpleName());
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_CUT, 0), Cut.class.getSimpleName());
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_COPY, 0), Copy.class.getSimpleName());
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_PASTE, 0), Paste.class.getSimpleName());
        setInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, im);
        configureListInputMap();
    }

    protected void fillActions() {
        ActionMap am = getActionMap();

        am.put(AddField.class.getSimpleName(), new AddField());
        am.put(Delete.class.getSimpleName(), new Delete());
        am.put(Cut.class.getSimpleName(), new Cut());
        am.put(Copy.class.getSimpleName(), new Copy());
        am.put(Paste.class.getSimpleName(), new Paste());

        am.put(MoveUpAction.class.getSimpleName(), new MoveUpAction());
        am.put(MoveDownAction.class.getSimpleName(), new MoveDownAction());

        am.put(ChangeFieldNameAction.class.getSimpleName(), new ChangeFieldNameAction());
        am.put(ChangeFieldDescriptionAction.class.getSimpleName(), new ChangeFieldDescriptionAction());
        am.put(ChangeFieldSizeAction.class.getSimpleName(), new ChangeFieldSizeAction());
        am.put(ChangeFieldScaleAction.class.getSimpleName(), new ChangeFieldScaleAction());
        am.put(ChangeFieldValueAction.class.getSimpleName(), new ChangeFieldValueAction());
        am.put(ChangeFieldTypeAction.class.getSimpleName(), new ChangeFieldTypeAction());
        am.put(ChangeParameterKindAction.class.getSimpleName(), new ChangeParameterKindAction());
        am.put(ChangeFieldTypeNameAction.class.getSimpleName(), new ChangeFieldTypeNameAction());
        am.put(ChangeFieldFkAction.class.getSimpleName(), new ChangeFieldFkAction());
        am.put(ClearFieldFkAction.class.getSimpleName(), new ClearFieldFkAction());
        am.put(ChangeFieldReqiredAction.class.getSimpleName(), new ChangeFieldReqiredAction());
    }

    public void checkActions() {
        DatamodelDesignUtils.checkActions(this);
    }

    public void setSelectedField(Field aField) {
        fieldsList.setSelectedValue(aField, true);
    }

    public void configureListInputMap() {
        InputMap viewIm = getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        InputMap nativeListIm = fieldsList.getInputMap();
        if (nativeListIm != null) {
            for (KeyStroke ks : nativeListIm.allKeys()) {
                if (ks != null) {
                    if (viewIm.get(ks) != null) {
                        nativeListIm.remove(ks);
                    }
                }
            }
            if (nativeListIm.getParent() instanceof UIResource) {
                nativeListIm = nativeListIm.getParent();
                for (KeyStroke ks : nativeListIm.allKeys()) {
                    if (ks != null) {
                        if (viewIm.get(ks) != null) {
                            nativeListIm.remove(ks);
                        }
                    }
                }
            }
        }
    }

    public JList<? extends Field> getListParameters() {
        return fieldsList;
    }

    public boolean isAnyFieldSelected() {
        return fieldsList.getModel().getSize() > 0 && !fieldsList.isSelectionEmpty();
    }

    protected boolean fieldSelectionExists() {
        if (!fieldsList.isSelectionEmpty()) {
            Field f = fieldsList.getSelectedValue();
            ListModel<? extends Field> lm = fieldsList.getModel();
            for (int i = 0; i < lm.getSize(); i++) {
                if (f == lm.getElementAt(i)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Field getFirstSelectedField() {
        Object oSelected = fieldsList.getSelectedValue();
        if (oSelected != null && oSelected instanceof Field
                && fieldSelectionExists()) {
            return (Field) oSelected;
        }
        return null;
    }

    public Field[] getSelectedFields() {
        Object[] oSelected = fieldsList.getSelectedValues();
        if (oSelected != null) {
            Field[] fields = new Field[oSelected.length];
            for (int i = 0; i < fields.length; i++) {
                fields[i] = (Field) oSelected[i];
            }
            return fields;
        }
        return null;
    }

    protected void setField(Field aField) throws Exception {
        if (field != null) {
            field.getChangeSupport().removePropertyChangeListener(fieldChangeListener);
        }
        field = aField;
        if (field != null) {
            field.getChangeSupport().addPropertyChangeListener(fieldChangeListener);
        }
        updateFieldControls(field);
    }

    protected class FieldInFieldsListChangesReflector implements CollectionListener<Fields, Field>, PropertyChangeListener {

        protected Fields fields;

        public FieldInFieldsListChangesReflector() {
            super();
        }

        public void setFields(Fields aFields) {
            if (fields != aFields) {
                if (fields != null) {
                    fields.getCollectionSupport().removeListener(FieldInFieldsListChangesReflector.this);
                    for (Field field : fields.toCollection()) {
                        field.getChangeSupport().removePropertyChangeListener(FieldInFieldsListChangesReflector.this);
                    }
                }
                fields = aFields;
                if (fields != null) {
                    fields.getCollectionSupport().addListener(FieldInFieldsListChangesReflector.this);
                    for (Field field : fields.toCollection()) {
                        field.getChangeSupport().addPropertyChangeListener(FieldInFieldsListChangesReflector.this);
                    }
                }
            }
        }

        protected void fireRelationsChanged() {
            Set<Relation<E>> rels = entity.getInOutRelations();
            for (Relation<E> rel : rels) {
                model.fireRelationRemoved(rel);
                model.fireRelationAdded(rel);
            }
        }

        @Override
        public void added(Fields c, Field v) {
            v.getChangeSupport().addPropertyChangeListener(FieldInFieldsListChangesReflector.this);
            fireRelationsChanged();
        }

        @Override
        public void added(Fields c, Collection<Field> added) {
            for (Field v : added) {
                v.getChangeSupport().addPropertyChangeListener(FieldInFieldsListChangesReflector.this);
            }
            fireRelationsChanged();
        }

        @Override
        public void removed(Fields c, Field v) {
            v.getChangeSupport().removePropertyChangeListener(FieldInFieldsListChangesReflector.this);
            fireRelationsChanged();
        }

        @Override
        public void reodered(Fields c) {
        }

        @Override
        public void removed(Fields c, Collection<Field> removed) {
            for (Field v : removed) {
                v.getChangeSupport().removePropertyChangeListener(FieldInFieldsListChangesReflector.this);
            }
            fireRelationsChanged();
        }

        @Override
        public void cleared(Fields c) {
            fireRelationsChanged();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            assert evt.getSource() instanceof Field;
            fieldsList.setSelectedValue(evt.getSource(), true);
            fieldsList.repaint();
        }
    }

    protected class FieldChangeListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            assert evt.getSource() == field;
            fieldsModel.fireDataChanged();
            try {
                updateFieldControls(field);
            } catch (Exception ex) {
                Logger.getLogger(FieldsView.class.getName()).log(Level.SEVERE, null, ex);
            }
            checkActions();
        }
    }

    protected class FieldsListModelListener implements ListDataListener {

        @Override
        public void intervalAdded(ListDataEvent e) {
            fieldsList.clearSelection();
            fieldsList.setSelectedIndex(e.getIndex1());
        }

        @Override
        public void intervalRemoved(ListDataEvent e) {
            int oldSelectionIndex = fieldsList.getLeadSelectionIndex();
            fieldsList.clearSelection();
            if (fieldsList.getModel().getSize() > 0) {
                if (oldSelectionIndex >= fieldsList.getModel().getSize()) {
                    oldSelectionIndex = fieldsList.getModel().getSize() - 1;
                }
                fieldsList.setSelectedIndex(oldSelectionIndex);
            }
        }

        @Override
        public void contentsChanged(ListDataEvent e) {
            int oldSelectionIndex = fieldsList.getLeadSelectionIndex();
            fieldsList.clearSelection();
            if (fieldsList.getModel().getSize() > 0) {
                if (oldSelectionIndex >= fieldsList.getModel().getSize()) {
                    oldSelectionIndex = fieldsList.getModel().getSize() - 1;
                }
                fieldsList.setSelectedIndex(oldSelectionIndex);
            }
        }
    }

    protected class FieldsSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            try {
                Object oSelected = fieldsList.getSelectedValue();
                if (oSelected != null && oSelected instanceof Field) {
                    setField((Field) oSelected);
                } else {
                    setField(null);
                }
                checkActions();
            } catch (Exception ex) {
                Logger.getLogger(FieldsView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    protected class FieldsViewModelChangesListener implements ModelEditingListener<E> {

        @Override
        public void entityAdded(E added) {
        }

        @Override
        public void entityRemoved(E removed) {
            if (removed == entity) {
                try {
                    setEntity(null);
                } catch (Exception ex) {
                    Logger.getLogger(FieldsView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            checkActions();
        }

        @Override
        public void relationAdded(Relation<E> added) {
            fieldsModel.fireDataChanged();
        }

        @Override
        public void relationRemoved(Relation<E> removed) {
            fieldsModel.fireDataChanged();
        }

        @Override
        public void entityIndexesChanged(E e) {
        }
    }

    public M getModel() {
        return model;
    }

    public void setModel(M aModel) throws Exception {
        if (model != null) {
            model.removeEditingListener(fieldsChangeListener);
        }
        model = aModel;
        typesModel = new FieldsTypesModel(model);
        if (model != null) {
            model.addEditingListener(fieldsChangeListener);
        }
        if (model == null) {
            setEntity(null);
        }
    }

    public void setEntity(E aEntity) throws Exception {
        if (aEntity != null && aEntity.getModel() != null && model == null) {
            setModel(aEntity.getModel());
        }
        if (aEntity != null) {
            if (entity != aEntity) {
                entity = aEntity;
                if (entity != null) {
                    fieldsModel.setFields(entity.getFields());
                    fieldsList.setCellRenderer(new FieldsParametersListCellRenderer<>(DatamodelDesignUtils.getFieldsFont(), DatamodelDesignUtils.getBindedFieldsFont(), entity));
                }
            }
        } else {
            fieldsModel.setFields(null);
            fieldsList.setCellRenderer(null);
            entity = null;
            setField(null);
        }
        if (aEntity != null) {
            lblTableName.setText(entity.getFormattedTableNameAndTitle());
        } else {
            lblTableName.setText(""); //NO18IN
        }
        fieldInFieldsListChangesReflector.setFields(fieldsModel.getFields());
        ftextScale.setEnabled(entity != null && !fieldsList.isSelectionEmpty());
        ftextSize.setEnabled(entity != null && !fieldsList.isSelectionEmpty());
        ftextValue.setEnabled(entity != null && !fieldsList.isSelectionEmpty());
        if (fieldsList.getSelectedValue() != null
                && fieldsList.getSelectedValue() instanceof Field) {
            updateFieldControls((Field) fieldsList.getSelectedValue());
        }
        checkActions();
    }

    public E getEntity() {
        return entity;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        splitParameters = new javax.swing.JSplitPane();
        scrollCustomize = new javax.swing.JScrollPane();
        pnlCustomize = new javax.swing.JPanel();
        ftextName = new javax.swing.JTextField();
        ftextDescription = new javax.swing.JTextField();
        comboType = new javax.swing.JComboBox<Integer>();
        chkRequired = new javax.swing.JCheckBox();
        lblName = new javax.swing.JLabel();
        lblDescription = new javax.swing.JLabel();
        lblType = new javax.swing.JLabel();
        lblSize = new javax.swing.JLabel();
        lblPrecision = new javax.swing.JLabel();
        ftextValue = new javax.swing.JFormattedTextField();
        lblValue = new javax.swing.JLabel();
        lblFkTable = new javax.swing.JLabel();
        ftextSize = new javax.swing.JSpinner();
        ftextScale = new javax.swing.JSpinner();
        pnlFkTable = new javax.swing.JPanel();
        ftextFkTable = new javax.swing.JFormattedTextField();
        pnlButtons = new javax.swing.JPanel();
        btnFkTable = new javax.swing.JButton();
        btnFkDel = new javax.swing.JButton();
        lblTypeName = new javax.swing.JLabel();
        ftextTypeName = new javax.swing.JTextField();
        comboParameterKind = new javax.swing.JComboBox<Integer>();
        lblStoredProcedure = new javax.swing.JLabel();
        lblTableName = new javax.swing.JLabel();
        scrollParameters = new javax.swing.JScrollPane();
        fieldsList = fieldsList = new javax.swing.JList<>();

        splitParameters.setDividerLocation(180);
        splitParameters.setOneTouchExpandable(true);

        comboType.setPreferredSize(new java.awt.Dimension(60, 27));

        chkRequired.setText(DatamodelDesignUtils.getLocalizedString("chkNullableTitle")); // NOI18N

        lblName.setText(DatamodelDesignUtils.getLocalizedString("lblName")); // NOI18N

        lblDescription.setText(DatamodelDesignUtils.getLocalizedString("lblDescription")); // NOI18N

        lblType.setText(DatamodelDesignUtils.getLocalizedString("lblType")); // NOI18N

        lblSize.setText(DatamodelDesignUtils.getLocalizedString("lblSize")); // NOI18N

        lblPrecision.setText(DatamodelDesignUtils.getLocalizedString("lblPrecision")); // NOI18N

        lblValue.setText(DatamodelDesignUtils.getLocalizedString("lblValue")); // NOI18N

        lblFkTable.setText(DatamodelDesignUtils.getLocalizedString("lblFkTable")); // NOI18N

        ftextSize.setModel(new javax.swing.SpinnerNumberModel(100, 0, 10000, 1));
        ftextSize.setEditor(new javax.swing.JSpinner.NumberEditor(ftextSize, ""));

        ftextScale.setModel(new javax.swing.SpinnerNumberModel(0, 0, 10, 1));
        ftextScale.setEditor(new javax.swing.JSpinner.NumberEditor(ftextScale, ""));

        pnlFkTable.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlFkTable.setPreferredSize(new java.awt.Dimension(59, 27));
        pnlFkTable.setLayout(new java.awt.BorderLayout());

        ftextFkTable.setBorder(null);
        ftextFkTable.setEditable(false);
        ftextFkTable.setPreferredSize(new java.awt.Dimension(0, 27));
        ftextFkTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ftextFkTableActionPerformed(evt);
            }
        });
        pnlFkTable.add(ftextFkTable, java.awt.BorderLayout.CENTER);

        pnlButtons.setPreferredSize(new java.awt.Dimension(56, 18));
        pnlButtons.setLayout(new java.awt.BorderLayout());

        btnFkTable.setText("...");
        btnFkTable.setToolTipText("...");
        btnFkTable.setPreferredSize(new java.awt.Dimension(28, 18));
        pnlButtons.add(btnFkTable, java.awt.BorderLayout.CENTER);

        btnFkDel.setToolTipText("X");
        btnFkDel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFkDel.setIconTextGap(0);
        btnFkDel.setMinimumSize(new java.awt.Dimension(45, 23));
        btnFkDel.setPreferredSize(new java.awt.Dimension(28, 18));
        pnlButtons.add(btnFkDel, java.awt.BorderLayout.EAST);

        pnlFkTable.add(pnlButtons, java.awt.BorderLayout.EAST);

        lblTypeName.setText(DatamodelDesignUtils.getLocalizedString("lblTypeName")); // NOI18N

        comboParameterKind.setPreferredSize(new java.awt.Dimension(60, 27));

        lblStoredProcedure.setText(DatamodelDesignUtils.getLocalizedString("lblStoredProcedureParamKind")); // NOI18N

        lblTableName.setForeground(new java.awt.Color(0, 51, 255));
        lblTableName.setText("Table name");

        javax.swing.GroupLayout pnlCustomizeLayout = new javax.swing.GroupLayout(pnlCustomize);
        pnlCustomize.setLayout(pnlCustomizeLayout);
        pnlCustomizeLayout.setHorizontalGroup(
            pnlCustomizeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCustomizeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlCustomizeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(comboType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ftextDescription)
                    .addComponent(ftextName, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(ftextTypeName)
                    .addComponent(pnlFkTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ftextValue)
                    .addComponent(comboParameterKind, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlCustomizeLayout.createSequentialGroup()
                        .addGroup(pnlCustomizeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlCustomizeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(pnlCustomizeLayout.createSequentialGroup()
                                    .addComponent(lblFkTable, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
                                    .addGap(22, 22, 22))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlCustomizeLayout.createSequentialGroup()
                                    .addGap(1, 1, 1)
                                    .addComponent(ftextSize)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                            .addGroup(pnlCustomizeLayout.createSequentialGroup()
                                .addGroup(pnlCustomizeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblName)
                                    .addComponent(lblDescription)
                                    .addComponent(lblType)
                                    .addComponent(lblTypeName)
                                    .addComponent(lblSize)
                                    .addComponent(lblTableName))
                                .addGap(414, 414, 414)))
                        .addGroup(pnlCustomizeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlCustomizeLayout.createSequentialGroup()
                                .addComponent(ftextScale, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkRequired))
                            .addComponent(lblPrecision))
                        .addGap(6, 6, 6))
                    .addGroup(pnlCustomizeLayout.createSequentialGroup()
                        .addGroup(pnlCustomizeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblValue)
                            .addComponent(lblStoredProcedure))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlCustomizeLayout.setVerticalGroup(
            pnlCustomizeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCustomizeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTableName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ftextName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDescription)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ftextDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblType)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTypeName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ftextTypeName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCustomizeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSize)
                    .addComponent(lblPrecision))
                .addGap(5, 5, 5)
                .addGroup(pnlCustomizeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ftextSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ftextScale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkRequired, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblFkTable)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlFkTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblValue)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ftextValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStoredProcedure)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboParameterKind, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(91, 91, 91))
        );

        scrollCustomize.setViewportView(pnlCustomize);

        splitParameters.setRightComponent(scrollCustomize);

        scrollParameters.setViewportView(fieldsList);

        splitParameters.setLeftComponent(scrollParameters);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitParameters, javax.swing.GroupLayout.DEFAULT_SIZE, 836, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitParameters)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void ftextFkTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ftextFkTableActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ftextFkTableActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton btnFkDel;
    protected javax.swing.JButton btnFkTable;
    protected javax.swing.JCheckBox chkRequired;
    private javax.swing.JComboBox<Integer> comboParameterKind;
    protected javax.swing.JComboBox<Integer> comboType;
    protected javax.swing.JList<Field> fieldsList;
    protected javax.swing.JTextField ftextDescription;
    private javax.swing.JFormattedTextField ftextFkTable;
    protected javax.swing.JTextField ftextName;
    protected javax.swing.JSpinner ftextScale;
    protected javax.swing.JSpinner ftextSize;
    protected javax.swing.JTextField ftextTypeName;
    private javax.swing.JFormattedTextField ftextValue;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblFkTable;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPrecision;
    private javax.swing.JLabel lblSize;
    private javax.swing.JLabel lblStoredProcedure;
    private javax.swing.JLabel lblTableName;
    private javax.swing.JLabel lblType;
    private javax.swing.JLabel lblTypeName;
    private javax.swing.JLabel lblValue;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlCustomize;
    private javax.swing.JPanel pnlFkTable;
    private javax.swing.JScrollPane scrollCustomize;
    private javax.swing.JScrollPane scrollParameters;
    private javax.swing.JSplitPane splitParameters;
    // End of variables declaration//GEN-END:variables
}
