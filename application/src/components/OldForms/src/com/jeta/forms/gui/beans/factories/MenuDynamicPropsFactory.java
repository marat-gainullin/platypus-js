/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jeta.forms.gui.beans.factories;

import com.jeta.forms.gui.beans.BeanProperties;
import com.jeta.forms.store.properties.MenuItemsProperty;
import javax.swing.JPopupMenu;

/**
 *
 * @author Mg
 */
public class MenuDynamicPropsFactory extends JComponentBeanFactory{
 
    public MenuDynamicPropsFactory()
    {
        super(JPopupMenu.class);
    }
    
    /**
    * Override to set custom properties for your factory
    */
    @Override
    public void defineProperties( BeanProperties props )
    {
       super.defineProperties( props );
       props.register( new MenuItemsProperty() );
    }

}
