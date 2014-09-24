/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.selectors;

import java.util.List;
import org.openide.loaders.DataObject;

/**
 *
 * @author mg
 */
public interface MtdSelectionValidator {

    public boolean isEntityValid(DataObject aDataObject);

    public List<String> getAllowedTypes();
}
