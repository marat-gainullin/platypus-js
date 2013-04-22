/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jeta.forms.gui.beans.factories;

import com.jeta.forms.gui.beans.BeanProperties;
import com.jeta.forms.store.properties.SplittedPaneLeftProperty;
import com.jeta.forms.store.properties.SplittedPaneRightProperty;
import com.jeta.forms.store.properties.TransformOptionsProperty;
import javax.swing.JSplitPane;
 
/*
 * @author Mg
 */
public class SplitPaneDynamicPropsFactory extends JComponentBeanFactory
{
    public static final transient String ORIENTATION_PROPERTY_NAME = "orientation";

    public SplitPaneDynamicPropsFactory()
    {
        super(JSplitPane.class);
    }

    /**
    * Override to set custom properties for your factory
    */
    @Override
   public void defineProperties( BeanProperties props )
   {
      super.defineProperties( props );
      props.register( new SplittedPaneLeftProperty() );
      props.register( new SplittedPaneRightProperty() );
      TransformOptionsProperty orientationProp = new TransformOptionsProperty(ORIENTATION_PROPERTY_NAME , "getOrientation", "setOrientation",
								       new Object[][] { {"VERTICAL", new Integer(JSplitPane.VERTICAL_SPLIT)},
											{"HORIZONTAL", new Integer(JSplitPane.HORIZONTAL_SPLIT)}});
      props.register(orientationProp);
      props.setPreferred(ORIENTATION_PROPERTY_NAME, true);
      props.setPreferred(SplittedPaneLeftProperty.PROPERTY_ID, false);
      props.setPreferred(SplittedPaneRightProperty.PROPERTY_ID, false);
   }
}
