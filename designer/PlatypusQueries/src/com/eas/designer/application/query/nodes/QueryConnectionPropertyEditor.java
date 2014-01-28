/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.nodes;

import com.eas.designer.application.query.PlatypusQueryDataObject;
import java.beans.PropertyEditorSupport;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;

/**
 *
 * @author mg
 */
public class QueryConnectionPropertyEditor extends PropertyEditorSupport {

    protected PlatypusQueryDataObject dataObject;

    public QueryConnectionPropertyEditor(PlatypusQueryDataObject aDataObject) {
        super();
        dataObject = aDataObject;
    }

    @Override
    public String[] getTags() {
        DatabaseConnection[] connections = ConnectionManager.getDefault().getConnections();
        String[] names = new String[connections.length + 1];
        names[0] = "";
        for (int i = 1; i < names.length; i++) {
            names[i] = connections[i - 1].getDisplayName();
        }
        return names;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {        
        super.setValue(text != null && text.isEmpty() ? null : text);
    }

}
