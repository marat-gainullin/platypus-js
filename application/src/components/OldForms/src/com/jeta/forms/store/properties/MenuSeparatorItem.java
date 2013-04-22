/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jeta.forms.store.properties;

import com.jeta.open.i18n.I18N;

/**
 *
 * @author Marat
 */
public class MenuSeparatorItem extends MenuItemsItem{

    
    @Override
    public String toString()
    {
         return I18N.getLocalizedMessage("JGoodies_Separator");
    }

}
