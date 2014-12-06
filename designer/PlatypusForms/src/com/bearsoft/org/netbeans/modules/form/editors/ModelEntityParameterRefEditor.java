/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.editors;

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.client.model.ModelElementRef;
import com.eas.client.model.ModelEntityParameterRef;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.client.model.gui.selectors.ModelElementSelector;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.NbBundle;

/**
 *
 * @author mg
 */
public class ModelEntityParameterRefEditor extends ModelElementRefEditor {

    public ModelEntityParameterRefEditor() {
        super();
        selectionSubject = ModelElementSelector.STRICT_DATASOURCE_PARAMETER_SELECTION_SUBJECT;
        dialogTitle = NbBundle.getMessage(ModelEntityParameterRefEditor.class, "CTL_SelectEntityParameter");
    }

    // Combo editing section
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        try {
            assert getValue() == null || getValue() instanceof ModelElementRef;
            ModelElementRef element = (ModelElementRef) getValue();
            element = lookupGridRowsSource(element);
            ApplicationDbModel model = formModel.getDataObject().getBasesProxy() != null ? formModel.getDataObject().getModel() : null;
            if (element != null && model != null) {
                ApplicationDbEntity elementEntity = model.getEntityById(element.getEntityId());
                if (elementEntity != null && elementEntity.getQuery() != null) {
                    Fields fields = elementEntity.getQuery().getParameters();
                    if (fields.contains(text)) {
                        ModelElementRef copied = element.copy();
                        copied.setField(false);
                        Field field = fields.get(text);
                        copied.setField(field);
                        setValue(copied);
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ModelElementRefEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String[] getTags() {
        try {
            assert getValue() == null || getValue() instanceof ModelElementRef;
            ModelElementRef element = (ModelElementRef) getValue();
            element = lookupGridRowsSource(element);
            ApplicationDbModel model = formModel.getDataObject().getBasesProxy() != null ? formModel.getDataObject().getModel() : null;
            if (element != null && model != null) {
                ApplicationDbEntity elementEntity = model.getEntityById(element.getEntityId());
                if (elementEntity != null && elementEntity.getQuery() != null) {
                    Fields fields = elementEntity.getQuery().getParameters();
                    List<String> tags = new ArrayList<>();
                    for (int i = 1; i <= fields.getFieldsCount(); i++) {
                        Field field = fields.get(i);
                        tags.add(field.getName());
                    }
                    return tags.toArray(new String[]{});
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception ex) {
            Logger.getLogger(ModelElementRefEditor.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    @Override
    public void setValue(Object value) {
        if (value instanceof ModelElementRef && !(value instanceof ModelEntityParameterRef)) {
            ModelEntityParameterRef entityRef = new ModelEntityParameterRef();
            entityRef.setField(((ModelElementRef) value).getField());
            entityRef.setField(false);
            entityRef.setEntityId(((ModelElementRef) value).getEntityId());
            value = entityRef;
        }
        super.setValue(value);
    }

}
