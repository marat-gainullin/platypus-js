/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.editors;

import com.bearsoft.org.netbeans.modules.form.FormAwareEditor;
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
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author mg
 */
public class ModelObjectEditor extends PropertyEditorSupport implements FormAwareEditor {

    protected int selectionSubject = ModelElementSelector.DATASOURCE_SELECTION_SUBJECT;
    protected String dialogTitle = NbBundle.getMessage(ModelObjectPropertyEditor.class, "CTL_SelectField");
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
            Logger.getLogger(ModelObjectPropertyEditor.class.getName()).log(Level.SEVERE, null, ex);
            return super.getCustomEditor();
        }
    }

    @Override
    public void setContext(FormModel aFormModel, FormProperty<?> aProperty) {
        formModel = aFormModel;
        property = (FormProperty<Object>) aProperty;
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
