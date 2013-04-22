/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jeta.forms.gui.beans.factories;

import com.jeta.forms.components.panel.JFramePanel;
import com.jeta.forms.gui.beans.BeanProperties;
import com.jeta.forms.store.properties.FramePanelLinkProperty;

/**
 *
 * @author mg
 */
public class FramePanelFactory extends JComponentBeanFactory
{
    public FramePanelFactory()
    {
        super(JFramePanel.class);
    }

    /**
    * Override to set custom properties for your factory
    */
    @Override
   public void defineProperties( BeanProperties props )
   {
      super.defineProperties( props );
      props.register( new FramePanelLinkProperty() );
      props.removeProperty("foreground");
      props.removeProperty("background");
      props.removeProperty("popupMenu");
      props.removeProperty("toolTipText");
      props.removeProperty("font");
      props.removeProperty("enabled");
   }
}

