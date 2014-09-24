/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.selectors;

import java.util.ArrayList;
import java.util.List;
import org.openide.loaders.DataObject;

/**
 *
 * @author mg
 */
public class DefaultMtdSelectionValidator implements MtdSelectionValidator {

    protected List<String> allowedTypes = new ArrayList<>();

    public DefaultMtdSelectionValidator(List<String> aAllowedTypes) {
        super();
        allowedTypes = aAllowedTypes;
    }

    @Override
    public boolean isEntityValid(DataObject umdo) {
        if (allowedTypes != null) {
            if (umdo != null) {                
                return allowedTypes.contains(umdo.getPrimaryFile().getExt());
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public List<String> getAllowedTypes() {
        return allowedTypes;
    }
}
