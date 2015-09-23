/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.editors;

import com.bearsoft.org.netbeans.modules.form.FormCookie;
import com.bearsoft.org.netbeans.modules.form.FormModel;
import com.bearsoft.org.netbeans.modules.form.FormProperty;
import com.bearsoft.org.netbeans.modules.form.PlatypusFormDataObject;
import com.eas.designer.application.module.EntityJSObject;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.client.model.gui.ModelElementRef;
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
public class EntityJSObjectEditor extends PropertyEditorSupport implements ExPropertyEditor {

    protected int selectionSubject = ModelElementSelector.DATASOURCE_SELECTION_SUBJECT;
    protected String dialogTitle = NbBundle.getMessage(EntityJSObjectFieldEditor.class, "CTL_SelectField");
    protected FormModel formModel;
    protected FormProperty<Object> property;
    protected IconsListCellRenderer renderer;

    public EntityJSObjectEditor() {
        super();
        dialogTitle = NbBundle.getMessage(EntityJSObjectEditor.class, "CTL_SelectEntity");
    }

    // Elipsis button section
    @Override
    public boolean supportsCustomEditor() {
        return formModel!= null && formModel.getDataObject() instanceof PlatypusFormDataObject;
    }

    @Override
    public Component getCustomEditor() {
        try {
            ApplicationDbModel model = formModel != null ? ((PlatypusFormDataObject) formModel.getDataObject()).getModel() : null;
            if (model != null) {
                ModelElementRef oldRef = new ModelElementRef();
                EntityJSObject oldModelObject = (EntityJSObject) getValue();
                if(oldModelObject != null)
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
                property = (FormProperty<Object>) aEnv.getFeatureDescriptor();
            }
        }
    }

    @Override
    public String getAsText() {
        Object oObject = getValue();
        if (oObject instanceof EntityJSObject) {
            EntityJSObject obj = (EntityJSObject) oObject;
            return obj.getName();
        } else {
            return super.getAsText();
        }
    }

    @Override
    public void setAsText(String aEntityName) throws IllegalArgumentException {
        if (aEntityName != null && !aEntityName.isEmpty()) {
            if (formModel != null && formModel.getDataObject() instanceof PlatypusFormDataObject) {
                try {
                    ApplicationDbModel model = ((PlatypusFormDataObject) formModel.getDataObject()).getModel();
                    model.getEntities().values().stream().filter((ApplicationDbEntity aEntity) -> {
                        return aEntityName.equals(aEntity.getName());
                    }).findAny().ifPresent((ApplicationDbEntity aEntity) -> {
                        setValue(aEntity.getPublished());
                    });
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            } else {
                setValue(new EntityJSObject(aEntityName));
            }
        } else {
            setValue(null);
        }
    }

    @Override
    public String[] getTags() {
        if (formModel != null && formModel.getDataObject() instanceof PlatypusFormDataObject) {
            try {
                List<String> tags = new ArrayList<>();
                tags.add("");
                ApplicationDbModel model = ((PlatypusFormDataObject) formModel.getDataObject()).getModel();
                model.getEntities().values().stream().forEach((ApplicationDbEntity aEntity) -> {
                    tags.add(aEntity.getName());
                });
                return tags.toArray(new String[]{});
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
                return null;
            }
        } else {
            return null;
        }
    }
}
