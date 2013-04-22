/*
 * Copyright (c) 2004 JETA Software, Inc.  All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of JETA Software nor the names of its contributors may 
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jeta.forms.gui.form;

import java.awt.Image;
import java.awt.Window;
import java.awt.event.WindowListener;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.beans.BeanInfo;
import java.beans.BeanDescriptor;
import java.beans.EventSetDescriptor;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 * Defines the BeanInfo for a GridView.  When the user selects a form 
 * in the designer, the properties pane shows several properties for that 
 * form.  Forms use GridViews as their underlying Java bean component. So,
 * the designer is really updating properties for a GridView object.
 * The allows the user to set standard properties such as border and 
 * background color for forms.
 * A custom BeanInfo is provided instead of the standard JPanel BeanInfo
 * because we don't want nor need all of the JPanel properties.
 * 
 * @author Jeff Tassin
 */
public class GridViewBeanInfo implements BeanInfo
{

    private PropertyDescriptor[] m_props = new PropertyDescriptor[0];
    private static HashSet<String> m_supportedProps = new HashSet<String>();


    static
    {
        m_supportedProps.add("name");
        m_supportedProps.add("opaque");
    }

    /**
     * ctor
     */
    public GridViewBeanInfo()
    {
        try
        {
            ArrayList<PropertyDescriptor> props = new ArrayList<PropertyDescriptor>();

            // we don't need every property from JPanel, just name, sizes and opaque
            BeanInfo info = Introspector.getBeanInfo(JPanel.class);
            PropertyDescriptor[] pds = info.getPropertyDescriptors();
            for (int index = 0; index < pds.length; index++)
            {
                PropertyDescriptor pd = pds[index];
                if (m_supportedProps.contains(pd.getName()))
                {
                    props.add(pd);
                }
            }

            props.add(new PropertyDescriptor("title", GridView.class, "getTitle", "setTitle"));
            props.add(new PropertyDescriptor("icon", GridView.class, "getIcon", "setIcon"));
            props.add(new PropertyDescriptor("defaultCloseOperation", GridView.class, "getDefaultCloseOperation", "setDefaultCloseOperation"));
            props.add(new PropertyDescriptor("undecorated", GridView.class, "isUndecorated", "setUndecorated"));
            m_props = props.toArray(new PropertyDescriptor[props.size()]);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public BeanInfo[] getAdditionalBeanInfo()
    {
        return new BeanInfo[0];
    }

    @Override
    public BeanDescriptor getBeanDescriptor()
    {
        return null;
    }

    @Override
    public int getDefaultEventIndex()
    {
        return 0;
    }

    @Override
    public int getDefaultPropertyIndex()
    {
        return 0;
    }

    @Override
    public EventSetDescriptor[] getEventSetDescriptors()
    {
        try
        {
            BeanInfo bi = Introspector.getBeanInfo(Window.class);
            if (bi != null)
            {
                EventSetDescriptor[] evSets = bi.getEventSetDescriptors();
                if (evSets != null)
                {
                    for (int i = 0; i < evSets.length; i++)
                    {
                        if (evSets[i] != null && WindowListener.class.isAssignableFrom(evSets[i].getListenerType()))
                        {
                            return new EventSetDescriptor[]
                                    {
                                        evSets[i]
                                    };
                        }
                    }
                }
            }
        }
        catch (IntrospectionException ex)
        {
            Logger.getLogger(GridViewBeanInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new EventSetDescriptor[0];
    }

    @Override
    public Image getIcon(int i)
    {
        return null;
    }

    @Override
    public MethodDescriptor[] getMethodDescriptors()
    {
        return new MethodDescriptor[0];
    }

    /**
     * @return a collection of JETAPropertyDescriptor objects
     */
    @Override
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        return m_props;
    }
}