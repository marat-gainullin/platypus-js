/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.editors;

import com.bearsoft.org.netbeans.modules.form.FormCookie;
import com.bearsoft.org.netbeans.modules.form.FormModel;
import com.bearsoft.org.netbeans.modules.form.FormProperty;
import com.eas.designer.application.module.ModelJSObject;
import com.eas.client.model.ModelElementRef;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.client.model.gui.selectors.ModelElementSelector;
import com.eas.client.model.gui.view.IconsListCellRenderer;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyEditorSupport;
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
public class ModelObjectEditor extends PropertyEditorSupport implements ExPropertyEditor {

    protected int selectionSubject = ModelElementSelector.DATASOURCE_SELECTION_SUBJECT;
    protected String dialogTitle = NbBundle.getMessage(ModelObjectPropertyPropertyEditor.class, "CTL_SelectField");
    protected FormModel formModel;
    protected FormProperty<Object> property;
    protected IconsListCellRenderer renderer;

    public ModelObjectEditor() {
        super();
        dialogTitle = NbBundle.getMessage(ModelObjectEditor.class, "CTL_SelectEntity");
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
                ModelElementRef oldRef = new ModelElementRef();
                ModelJSObject oldModelObject = (ModelJSObject) getValue();
                oldRef.setEntityId(oldModelObject.getEntity().getEntityId());
                final ModelElementRef selected = new ModelElementRef();
                return ModelElementSelector.prepareDialog(model,
                        dialogTitle,
                        selected,
                        selectionSubject,
                        null,
                        oldRef, (ActionEvent e) -> {
                            ApplicationDbEntity entity = model.getEntityById(selected.entityId);
                            setValue(entity.getPublished());
                        });
            } else {
                return super.getCustomEditor();
            }
        } catch (Exception ex) {
            Logger.getLogger(ModelObjectPropertyPropertyEditor.class.getName()).log(Level.SEVERE, null, ex);
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
                property = (FormProperty<Object>) aEnv.getFeatureDescriptor();
            }
        }
    }

    @Override
    public String getAsText() {
        Object oObject = getValue();
        if (oObject instanceof ModelJSObject) {
            ModelJSObject obj = (ModelJSObject) oObject;
            return obj.getEntity().getName();
        } else {
            return super.getAsText();
        }
    }

    @Override
    public void setAsText(String aEntityName) throws IllegalArgumentException {
        if (aEntityName != null && !aEntityName.isEmpty()) {
            try {
                ApplicationDbModel model = formModel.getDataObject().getModel();
                model.getEntities().values().stream().filter((ApplicationDbEntity aEntity) -> {
                    return aEntityName.equals(aEntity.getName());
                }).findAny().ifPresent((ApplicationDbEntity aEntity) -> {
                    setValue(aEntity.getPublished());
                });
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            setValue(null);
        }
    }

    @Override
    public String[] getTags() {
        try {
            List<String> tags = new ArrayList<>();
            tags.add("");
            ApplicationDbModel model = formModel.getDataObject().getModel();
            model.getEntities().values().stream().forEach((ApplicationDbEntity aEntity) -> {
                tags.add(aEntity.getName());
            });
            return tags.toArray(new String[]{});
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }
}
