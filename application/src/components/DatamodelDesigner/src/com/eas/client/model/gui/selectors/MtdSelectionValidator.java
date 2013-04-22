/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.model.gui.selectors;

import com.eas.client.metadata.ApplicationElement;
import java.util.List;

/**
 *
 * @author mg
 */
public interface MtdSelectionValidator
{
    public boolean isEntityValid(ApplicationElement entitiy);
    public List<Integer> getAllowedTypes();
}
