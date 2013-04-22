/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.editors;

import com.eas.client.model.ModelElementRef;
import com.eas.client.model.ModelEntityRef;
import com.eas.client.model.gui.selectors.ModelElementSelector;
import org.openide.util.NbBundle;

/**
 *
 * @author mg
 */
public class ModelEntityRefEditor extends ModelElementRefEditor {

    public ModelEntityRefEditor() {
        super();
        selectionSubject = ModelElementSelector.DATASOURCE_SELECTION_SUBJECT;
        dialogTitle = NbBundle.getMessage(ModelEntityRefEditor.class, "CTL_SelectEntity");
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof ModelElementRef && !(value instanceof ModelEntityRef)) {
            ModelEntityRef entityRef = new ModelEntityRef();
            entityRef.setField(null);
            entityRef.setField(false);
            entityRef.setEntityId(((ModelElementRef) value).getEntityId());
            value = entityRef;
        }
        super.setValue(value);
    }

    @Override
    public String[] getTags() {
        return null;
    }
}
