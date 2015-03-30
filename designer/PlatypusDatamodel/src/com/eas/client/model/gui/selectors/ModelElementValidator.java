/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.model.gui.selectors;

import com.eas.client.model.gui.ModelElementRef;

/**
 *
 * @author mg
 */
public interface ModelElementValidator
{
    public boolean validateDatamodelElementSelection(ModelElementRef aDmElement);
}
