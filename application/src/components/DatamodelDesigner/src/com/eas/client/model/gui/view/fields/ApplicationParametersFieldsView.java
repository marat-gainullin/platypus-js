/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.view.fields;

import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.client.model.gui.selectors.TablesSelectorCallback;
import com.eas.client.model.store.XmlDom2ApplicationModel;
import org.w3c.dom.Document;

/**
 *
 * @author mg
 */
public class ApplicationParametersFieldsView extends FieldsView<ApplicationDbEntity, ApplicationDbModel> {

    public ApplicationParametersFieldsView(TablesSelectorCallback aSelectorCallback) throws Exception {
        super(aSelectorCallback);
    }

    @Override
    public ApplicationDbModel newModel() {
        return new ApplicationDbModel();
    }

    @Override
    protected ApplicationDbModel document2Model(Document aDoc) throws Exception {
        ApplicationDbModel lmodel = new ApplicationDbModel(model.getClient());
        lmodel.accept(new XmlDom2ApplicationModel<ApplicationDbEntity>(aDoc));
        return lmodel;
    }
}
