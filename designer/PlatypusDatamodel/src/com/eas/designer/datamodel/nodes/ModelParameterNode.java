/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.datamodel.nodes;

import com.bearsoft.rowset.metadata.Field;
import com.eas.client.model.Model;
import com.eas.designer.datamodel.nodes.FieldNode;
import com.eas.script.ScriptUtils;
import javax.swing.undo.UndoableEdit;
import org.openide.ErrorManager;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author vv
 */
public class ModelParameterNode extends FieldNode {

    public ModelParameterNode(Field aField, Lookup aLookup) {
        super(aField, aLookup);
    }

    @Override
    protected UndoableEdit editName(String val) {
        if (isValidName(getEntity().getModel(), val)) {
            return super.editName(val);
        } else {
            IllegalArgumentException t = new IllegalArgumentException();
            throw Exceptions.attachLocalizedMessage(t, String.format(NbBundle.getMessage(ModelParameterNode.class, "MSG_InvalidParameterName"), val)); //NOI18N
        }

    }

    public static boolean isValidName(Model model, String name) {
        try {
            model.getParameters().invalidateFieldsHash();
            return !name.isEmpty() && model.getParameters().get(name) == null && model.getEntityByName(name) == null && ScriptUtils.isValidJsIdentifier(name);
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
        return false;
    }
    
    @Override
    public boolean canChange() {
        return true;
    }
}
