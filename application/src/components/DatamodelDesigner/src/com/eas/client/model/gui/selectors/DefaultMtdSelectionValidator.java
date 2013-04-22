/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.selectors;

import com.eas.client.metadata.ApplicationElement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mg
 */
public class DefaultMtdSelectionValidator implements MtdSelectionValidator {

    protected List<Integer> allowedTypes = new ArrayList<>();

    public DefaultMtdSelectionValidator(List<Integer> aAllowedTypes) {
        super();
        allowedTypes = aAllowedTypes;
    }

    public boolean isEntityValid(ApplicationElement umdo) {
        if (allowedTypes != null) {
            if (umdo != null) {
                return allowedTypes.contains(umdo.getType());
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public List<Integer> getAllowedTypes() {
        return allowedTypes;
    }
}
