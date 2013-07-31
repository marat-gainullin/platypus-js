/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.nodes;

import com.bearsoft.rowset.metadata.Parameter;
import com.eas.client.model.Model;
import com.eas.designer.datamodel.nodes.QueryParameterNode;
import javax.swing.undo.UndoableEdit;
import org.openide.ErrorManager;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author vv
 */
public class QueryModelParametersNode extends QueryParameterNode {
    
    public QueryModelParametersNode(Parameter aField, Lookup aLookup) {
        super(aField, aLookup);
    }
    
    @Override
    protected UndoableEdit editName(String val) {
        if (isValidName(getEntity().getModel(), val)) {
            return super.editName(val);
        } else {
            throw Exceptions.attachLocalizedMessage(new IllegalArgumentException(), String.format(NbBundle.getMessage(QueryModelParametersNode.class, "MSG_InvalidParameterName"), val)); //NOI18N
        }

    }

    public static boolean isValidName(Model model, String name) {
        try {
            model.getParameters().invalidateFieldsHash();
            return !name.isEmpty() && model.getParameters().get(name) == null;
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
        return false;
    }
}
