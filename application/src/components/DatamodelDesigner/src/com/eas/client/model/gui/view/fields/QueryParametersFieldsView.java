/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.view.fields;

import com.eas.client.model.gui.selectors.TablesSelectorCallback;
import com.eas.client.model.query.QueryEntity;
import com.eas.client.model.query.QueryModel;
import com.eas.client.model.store.XmlDom2QueryModel;
import org.w3c.dom.Document;

/**
 *
 * @author mg
 */
public class QueryParametersFieldsView extends FieldsView<QueryEntity, QueryModel> {

    public QueryParametersFieldsView(TablesSelectorCallback aSelectorCallback) throws Exception {
        super(aSelectorCallback);
    }

    @Override
    public QueryModel newModel() {
        return new QueryModel();
    }
    
    @Override
    protected QueryModel document2Model(Document aDoc) throws Exception {
        return XmlDom2QueryModel.transform(model.getClient(), aDoc);
    }
}
