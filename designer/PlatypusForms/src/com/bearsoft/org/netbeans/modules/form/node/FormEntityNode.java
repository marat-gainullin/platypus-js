/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.node;

import com.bearsoft.org.netbeans.modules.form.FormModel;
import com.bearsoft.org.netbeans.modules.form.PlatypusFormSupport;
import com.bearsoft.org.netbeans.modules.form.RADComponent;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.designer.application.module.nodes.ApplicationEntityNode;
import org.openide.ErrorManager;
import org.openide.awt.UndoRedo;
import org.openide.util.Lookup;

/**
 *
 * @author vv
 */
public class FormEntityNode extends ApplicationEntityNode {

    public FormEntityNode(ApplicationDbEntity aEntity, UndoRedo.Manager aUndoReciever, Lookup aLookup) throws Exception {
        super(aEntity, aUndoReciever, aLookup);
    }

    @Override
    protected boolean isValidName(String name) {
        return super.isValidName(name) && isValidFormName(name);
    }

    public boolean isValidFormName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            try {
                PlatypusFormSupport support = getLookup().lookup(PlatypusFormSupport.class);
                support.loadForm();
                FormModel formModel = support.getFormModel();
                for (RADComponent<?> rc : formModel.getAllComponents()) {
                    if (name.equals(rc.getName())) {
                        return false;
                    }
                }
                return true;
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
        return false;
    }
}
