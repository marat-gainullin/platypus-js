/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.dbcontrols.actions;

import com.eas.client.datamodel.ModelElementRef;
import com.eas.client.datamodel.gui.selectors.ModelElementValidator;
import com.eas.dbcontrols.*;
import javax.swing.Action;

/**
 *
 * @author mg
 */
public abstract class DmElementSelectAction extends DbControlSnapshotAction
{
    protected class FieldValidator implements ModelElementValidator
    {
        public FieldValidator()
        {
            super();
        }

        @Override
        public boolean validateDatamodelElementSelection(ModelElementRef aElement)
        {
            if(aElement != null && aElement.field != null)
                return DbControlsUtils.isDesignInfoCompatible2Type(designInfo.getClass(), aElement.field.getTypeInfo().getSqlType());
            return false;
        }

    }

    protected ModelElementValidator selectValidator = new FieldValidator();

    public DmElementSelectAction()
    {
        super();
        putValue(Action.SMALL_ICON, DesignIconCache.getIcon("16x16/ellipsis.gif"));
    }

    @Override
    public boolean isEnabled() {
        return designInfo instanceof DbControlDesignInfo;
    }
}
