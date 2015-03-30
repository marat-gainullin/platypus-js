/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.datamodel.nodes;

import com.eas.client.metadata.Parameter;
import com.eas.client.model.gui.edits.fields.ChangeFieldEdit;
import com.eas.client.model.query.QueryParametersEntity;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import javax.swing.event.UndoableEditEvent;
import org.openide.nodes.Sheet;
import org.openide.nodes.Sheet.Set;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author vv
 */
public class QueryParameterNode extends FieldNode {

    public static final String MODE_PROP_NAME = "mode";//NOI18N

    public QueryParameterNode(Parameter aField, Lookup aLookup) {
        super(aField, aLookup);
    }

    @Override
    public boolean canChange() {
        return getEntity() instanceof QueryParametersEntity;
    }

    public Integer getMode() {
        return ((Parameter) field).getMode();
    }

    public void setMode(Integer val) {
        Parameter oldContent = new Parameter(field);
        Parameter content = new Parameter(field);
        content.setMode((int) val);
        ChangeFieldEdit edit = new ChangeFieldEdit(oldContent, content, field, getEntity());
        edit.redo();
        getUndo().undoableEditHappened(new UndoableEditEvent(this, edit));
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        Set pSet = sheet.get(Sheet.PROPERTIES);
        Property<Integer> modeProp = new ModeProperty();
        pSet.put(modeProp);
        return sheet;
    }

    protected class ModeProperty extends Property<Integer> {

        public ModeProperty() {
            super(Integer.class);
        }

        @Override
        public PropertyEditor getPropertyEditor() {
            return ParmeterModeEditor.getNewInstance();
        }

        @Override
        public String getName() {
            return MODE_PROP_NAME;
        }

        @Override
        public String getShortDescription() {
            return NbBundle.getMessage(QueryParameterNode.ModeProperty.class, "MSG_ModePropertyShortDescription"); //NOI18N
        }

        @Override
        public Integer getValue() throws IllegalAccessException, InvocationTargetException {
            return getMode();
        }

        @Override
        public void setValue(Integer val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            setMode(val);
        }

        @Override
        public boolean canRead() {
            return true;
        }

        @Override
        public boolean canWrite() {
            return canChange();
        }
    }
}