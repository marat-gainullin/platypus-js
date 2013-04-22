/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jeta.forms.store.xml.parser;

import com.jeta.forms.store.jml.dom.JMLAttributes;
import java.awt.Dimension;

/**
 *
 * @author Marat
 */
public class DimensionHandler extends ObjectHandler{
   
    @Override
    protected Object instantiateObject( JMLAttributes attribs ) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Dimension l_dim = new Dimension();
        try
        {
            String lWidth = attribs.getValue("width");
            String lHeight = attribs.getValue("height");
            l_dim.width = Math.round(Float.parseFloat(lWidth)); 
            l_dim.height = Math.round(Float.parseFloat(lHeight));
        }catch(Exception ex)
        {
            l_dim.width = 0;
            l_dim.height = 0;
        }
        return l_dim;
    }

}
