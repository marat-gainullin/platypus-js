/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.editors;

import com.bearsoft.org.netbeans.modules.form.FormCookie;
import com.bearsoft.org.netbeans.modules.form.FormModel;
import com.bearsoft.org.netbeans.modules.form.FormProperty;
import com.bearsoft.org.netbeans.modules.form.RADComponent;
import com.bearsoft.org.netbeans.modules.form.RADComponentNode;
import com.bearsoft.org.netbeans.modules.form.RADProperty;
import com.bearsoft.org.netbeans.modules.form.bound.EntityJSObjectFieldProperty;
import com.bearsoft.org.netbeans.modules.form.bound.RADModelGridColumn;
import com.eas.client.forms.components.model.ModelCombo;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.model.Relation;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.client.model.application.ReferenceRelation;
import com.eas.client.model.gui.ModelElementRef;
import com.eas.client.model.gui.selectors.ModelElementSelector;
import com.eas.designer.application.module.EntityJSObject;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyEditorSupport;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author mg
 */
public class EntityJSObjectFieldEditor extends PropertyEditorSupport implements ExPropertyEditor {

    protected int selectionSubject = ModelElementSelector.FIELD_SELECTION_SUBJECT;
    protected String dialogTitle = NbBundle.getMessage(EntityJSObjectFieldEditor.class, "CTL_SelectField");
    protected FormModel formModel;
    protected EntityJSObjectFieldProperty property;
    protected RADComponent<?> comp;
    protected String prefix = "";

    public EntityJSObjectFieldEditor() {
        super();
    }

    public EntityJSObjectFieldEditor(String aPrefix) {
        this();
        prefix = aPrefix;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue("".equals(text) ? null : text);
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public String[] getTags() {
        ApplicationDbEntity dataEntity = lookupDataEntity();
        if (dataEntity != null) {
            List<String> tags = new ArrayList<>();
            tags.add("");
            Fields fields = dataEntity.getFields();
            if (fields != null) {
                fields.toCollection().stream().forEach((Field aField) -> {
                    tags.add(prefix + aField.getName());
                });
            }
            dataEntity.getModel().getReferenceRelationsByEntity(dataEntity).forEach((Relation<ApplicationDbEntity> aRelation) -> {
                if (aRelation instanceof ReferenceRelation<?>) {
                    ReferenceRelation<?> rr = (ReferenceRelation<?>) aRelation;
                    if (rr.getLeftEntity() == dataEntity) {
                        if (rr.getScalarPropertyName() != null && !rr.getScalarPropertyName().isEmpty()) {
                            tags.add(prefix + rr.getScalarPropertyName());
                        }
                        if (rr.getCollectionPropertyName() != null && !rr.getCollectionPropertyName().isEmpty()) {
                            tags.add(prefix + rr.getCollectionPropertyName());
                        }
                    }
                }
            });
            return tags.toArray(new String[]{});
        } else {
            return super.getTags();
        }
    }

    protected ApplicationDbEntity lookupDataEntity() {
        try {
            if (comp instanceof RADModelGridColumn) {
                RADModelGridColumn radCol = (RADModelGridColumn) comp;
                comp = radCol.lookupGrid();
            }
            RADProperty<EntityJSObject> prop = comp.getProperty(ModelCombo.class.isAssignableFrom(comp.getBeanClass()) && "displayField".equals(property.getName()) ? "displayList" : "data");
            EntityJSObject modelJsObject = prop != null ? prop.getValue() : null;
            ApplicationDbEntity dataEntity = modelJsObject != null ? modelJsObject.getEntity() : null;
            return dataEntity;
        } catch (IllegalAccessException | InvocationTargetException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    @Override
    public void setValue(Object value) {
        assert value == null || value instanceof String;
        super.setValue(value);
    }

    // Elipsis button section
    @Override
    public boolean supportsCustomEditor() {
        return true;
    }

    @Override
    public Component getCustomEditor() {
        try {
            ApplicationDbModel model = formModel.getDataObject().getModel();
            if (model != null) {
                ApplicationDbEntity dataEntity = lookupDataEntity();
                String oldValue = (String) getValue();
                Field oldField = oldValue != null && dataEntity != null && dataEntity.getFields() != null ? dataEntity.getFields().get(oldValue.startsWith(prefix) ? oldValue.substring(prefix.length()) : oldValue) : null;
                final ModelElementRef oldRef = oldField != null && dataEntity != null ? new ModelElementRef(oldField, true, dataEntity.getEntityId()) : new ModelElementRef();
                final ModelElementRef selectedRef = new ModelElementRef();
                return ModelElementSelector.prepareDialog(model,
                        dialogTitle,
                        selectedRef,
                        selectionSubject,
                        null,
                        oldRef, (ActionEvent e) -> {
                            if (dataEntity != null && dataEntity.getFields() != null && selectedRef.entityId != null
                            && selectedRef.entityId.equals(dataEntity.getEntityId())) {
                                Field newField = dataEntity.getFields().get(selectedRef.getFieldName());
                                setValue(prefix + newField.getName());
                            }
                        });
            } else {
                return super.getCustomEditor();
            }
        } catch (Exception ex) {
            Logger.getLogger(EntityJSObjectFieldEditor.class.getName()).log(Level.SEVERE, null, ex);
            return super.getCustomEditor();
        }
    }

    @Override
    public void attachEnv(PropertyEnv aEnv) {
        aEnv.getFeatureDescriptor().setValue("canEditAsText", Boolean.TRUE); // NOI18N
        Object bean = aEnv.getBeans()[0];
        if (bean instanceof Node) {
            Node node = (Node) bean;
            FormCookie formCookie = node.getLookup().lookup(FormCookie.class);
            if (formCookie != null && aEnv.getFeatureDescriptor() instanceof FormProperty<?>) {
                formModel = formCookie.getFormModel();
                property = (EntityJSObjectFieldProperty) aEnv.getFeatureDescriptor();
            }
            if (node instanceof RADComponentNode) {
                comp = ((RADComponentNode) node).getRADComponent();
            }
        }
    }

    public void setFormModel(FormModel aFormModel) {
        formModel = aFormModel;
    }

    public void setProperty(EntityJSObjectFieldProperty aProperty) {
        property = aProperty;
    }

    public void setComp(RADComponent<?> aComp) {
        comp = aComp;
    }
}
