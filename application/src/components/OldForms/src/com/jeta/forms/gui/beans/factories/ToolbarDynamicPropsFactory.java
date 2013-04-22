/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jeta.forms.gui.beans.factories;

import com.jeta.forms.gui.beans.BeanProperties;
import com.jeta.forms.store.properties.ToolButtonsProperty;
import javax.swing.JToolBar;

/**
 *
 * @author Mg
 */
public class ToolbarDynamicPropsFactory extends JComponentBeanFactory{

    public ToolbarDynamicPropsFactory()
    {
        super(JToolBar.class);
    }
    
    /**
    * Override to set custom properties for your factory
    */
    @Override
   public void defineProperties( BeanProperties props )
   {
      super.defineProperties( props );
      props.register( new ToolButtonsProperty() );
   }

}
