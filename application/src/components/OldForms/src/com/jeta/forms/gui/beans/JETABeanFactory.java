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
package com.jeta.forms.gui.beans;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import javax.swing.*;
import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.form.GridView;
import com.jeta.forms.gui.beans.factories.*;
import com.jeta.forms.components.border.*;
import com.jeta.forms.components.line.*;
import com.jeta.forms.components.image.*;
import com.jeta.forms.components.label.*;
import com.jeta.forms.components.panel.JFramePanel;
import com.jeta.forms.components.separator.*;
import com.jeta.forms.logger.FormsLogger;
import com.jeta.forms.gui.beans.factories.SplitPaneDynamicPropsFactory;

/**
 * This is a factory for creating a JETABean wrapper for a given Swing component
 * and its associated custom properties.  We need this because we want to support
 * dynamic properties for Swing components.  For example, we would like to have
 * a buttonGroup property for JRadioButtons.  This is easier for the user than having
 * to programmatically manage ButtonGroups.  The buttonGroup property will handle
 * this for the design and runtime systems. Each Swing component can have different
 * dynamic properties. For example, tabbed panes have tabs while JList and JTables have scrollBar
 * properties.  So, we use this factory to properly initialize a JETABean for a 
 * given Swing component.
 *
 * @author Jeff Tassin
 */
public class JETABeanFactory
{

    /**
     * m_factories<String,BeanFactory>  A map of component class names and their associated BeanFactories
     * This is for standard Swing Components such as JList, JButton, JLabel, etc.
     */
    private static LinkedHashMap<String, BeanFactory> m_factories = new LinkedHashMap<String, BeanFactory>();
    /**
     * m_custom_factories<String,BeanFactory>   A map of component class names and their
     * associated BeanFactories.  This is for custom, imported Java Beans.  If those beans
     * extend one of the standard swing components, then we want to use that
     * factory when creating the JETABean and its custom properties.
     */
    private static HashMap<String, JComponentBeanFactory> m_custom_factories = new HashMap<String, JComponentBeanFactory>();


    static
    {
        /** standard swing components */
        registerFactory("javax.swing.JButton", new ButtonBeanFactory());
        registerFactory("javax.swing.JToggleButton", new ToggleButtonFactory());
        registerFactory("javax.swing.JCheckBox", new CheckBoxBeanFactory());
        registerFactory("javax.swing.JComboBox", new ComboBoxBeanFactory());
        registerFactory("javax.swing.JList", new ListBeanFactory());
        registerFactory("javax.swing.JRadioButton", new RadioButtonBeanFactory());
        registerFactory("javax.swing.JTextField", new TextFieldBeanFactory());
        registerFactory("javax.swing.JPasswordField", new PasswordFieldBeanFactory());
        registerFactory("javax.swing.JTextArea", new TextAreaBeanFactory());
        registerFactory("javax.swing.JTable", new TableBeanFactory());
        registerFactory("javax.swing.JProgressBar", new ProgressBarFactory());
        registerFactory("javax.swing.JSlider", new SliderFactory());
        registerFactory("javax.swing.JTree", new TreeFactory());
        registerFactory("javax.swing.JEditorPane", new EditorPaneFactory());
        registerFactory("javax.swing.JTabbedPane", new TabbedPaneFactory());
        registerFactory("javax.swing.JFormattedTextField", new FormattedTextFieldFactory());
        registerFactory("javax.swing.JSplitPane", new SplitPaneDynamicPropsFactory());

        registerFactory("javax.swing.JToolBar", new ToolbarDynamicPropsFactory());
        registerFactory("javax.swing.JPopupMenu", new MenuDynamicPropsFactory());
        registerFactory("javax.swing.JMenuBar", new MenuBarDynamicPropsFactory());

        /* special case for JLabel since we have a custom JETALabel */
        LabelBeanFactory lbf = new LabelBeanFactory();
        lbf.setBeanClass(JLabel.class);
        registerFactory("javax.swing.JLabel", lbf);

        /* special case of JPanel since we use it as frame container*/
        registerFactory(JFramePanel.class.getName(), new FramePanelFactory());

        /** custom jetaware components */
        registerFactory(HorizontalLineComponent.class.getName(), new HorizontalLineComponentFactory());
        registerFactory(VerticalLineComponent.class.getName(), new VerticalLineComponentFactory());
        registerFactory(GridView.class.getName(), new GridViewBeanFactory());
        registerFactory(ImageComponent.class.getName(), new ImageBeanFactory());
        registerFactory(JETALabel.class.getName(), new LabelBeanFactory());
        registerFactory(TitledBorderLabel.class.getName(), new TitledBorderLabelFactory());
        registerFactory(TitledBorderSide.class.getName(), new TitledBorderSideFactory());
        registerFactory(TitledBorderBottom.class.getName(), new TitledBorderBottomFactory());
        registerFactory(TitledSeparator.class.getName(), new TitledSeparatorFactory());
    }

    /**
     * Registers a factory for the given component class
     */
    public static void registerFactory(String compClass, BeanFactory factory)
    {
        m_factories.put(compClass, factory);
    }

    /**
     * Removes all custom factories from the cache
     */
    public static void clearCustomFactories()
    {
        m_custom_factories.clear();
    }

    /**
     * Creates a JETABean wrapper for managing a JavaBean.
     * @param compClass the class name of the Java Bean to create (e.g. javax.swing.JButton, javax.swing.JRadioButton, etc.)
     * @param compName the name to assign to this component by calling Component.setName
     * @param instantiateBean set to true if the underlying Java Bean should be instantiated as well. During deserialization
     *  we don't want to do this because the XMLDecoder will create the JavaBean for us.
     * @param setDefaults sets default properties for the bean.  If false, no properties will be set (e.g. the text for a JButton)
     */
    public static JETABean createBean(String compClass, String compName, boolean instantiateBean, boolean setDefaults) throws FormException
    {
        if (compClass != null)
        {
            compClass = compClass.replace('/', '.');
            compClass = compClass.replace('\\', '.');
        }

        BeanFactory factory = lookupFactory(compClass);
        if (factory != null)
        {
            return factory.createBean(compName, instantiateBean, setDefaults);
        }
        else
        {
            return null;
        }
    }

    /**
     * Creates a bean info object for the given class.
     */
    public static DynamicBeanInfo getBeanInfo(Class compClass) throws FormException
    {
        DynamicBeanInfo beaninfo = JComponentBeanFactory.createBeanInfo(compClass);
        BeanFactory bf = lookupFactory(compClass.getName());
        /** todo fix this to eliminate instance check */
        if (bf instanceof JComponentBeanFactory)
        {
            BeanProperties default_props = new BeanProperties(beaninfo);
            ((JComponentBeanFactory) bf).defineProperties(default_props);
        }
        else if (bf instanceof GridViewBeanFactory)
        {
            BeanProperties default_props = new BeanProperties(beaninfo);
            ((GridViewBeanFactory) bf).defineProperties(default_props);
        }
        return beaninfo;
    }

    private static boolean isStandardFactory(JComponentBeanFactory jf)
    {
        return m_factories.containsKey(jf.getBeanClass().getName());
    }

    /**
     * Looks up a BeanFactory from either the standard factories or one of the custom component
     * factories.  Null is returned if a factory is not found.
     */
    private static BeanFactory lookupFactory(String compClass)
    {
        BeanFactory factory = m_factories.get(compClass);
        if (factory == null)
        {
            factory = (BeanFactory) m_custom_factories.get(compClass);
        }

        return factory;
    }

    /**
     * Locates a factory that is for a superclass of the given component class.
     * Iterates over all registered factories.  Almost all factories will have
     * the same superclass as a superclass of the given component class.  For example,
     * we automatically register a factory for both JRadioButton and JToggleButton.
     * If a user wants to register a custom component based on JRadioButton, then either
     * factory would work.  However, since the factory associated with JRadioButton is
     * a closer relationship, we need to choose that one.
     * @param compClass the class of the Java Bean whose factory we wish to locate.
     * @return a JComponentBeanFactory associated with the given bean.
     */
    private static JComponentBeanFactory lookupAssignableFactory(Class compClass)
    {
        /** count for keeping track of the factory with the closest relationship to the given compClass */
        int min_count = 0;
        JComponentBeanFactory result_factory = null;

        if (JComponent.class.isAssignableFrom(compClass))
        {
            Iterator iter = m_factories.values().iterator();
            while (iter.hasNext())
            {
                BeanFactory factory = (BeanFactory) iter.next();
                if (factory instanceof JComponentBeanFactory)
                {
                    JComponentBeanFactory jfactory = (JComponentBeanFactory) factory;
                    Class superclass = compClass;
                    int derive_count = 1;
                    while (superclass != JComponent.class && superclass != Object.class)
                    {
                        if (superclass == jfactory.getBeanClass())
                        {
                            if ((min_count > derive_count) || (min_count == 0))
                            {
                                result_factory = jfactory;
                                min_count = derive_count;
                            }
                            break;
                        }
                        superclass = superclass.getSuperclass();
                        derive_count++;
                    }
                }
            }
        }
        return result_factory;
    }

    /**
     * This is for custom, imported Java Beans.  If those beans
     * extend one of the standard swing components, then we want to register a factory
     * based on the standard factory so we can still use any custom properties.
     */
    public static void tryRegisterCustomFactory(String compClass, boolean scrollable) throws FormException
    {
        try
        {
            if (compClass == null)
            {
                return;
            }

            if (compClass != null)
            {
                compClass = compClass.replace('/', '.');
                compClass = compClass.replace('\\', '.');
            }

            Class c = Class.forName(compClass);
            tryRegisterCustomFactory(c, scrollable);
        }
        catch (Exception e)
        {
            FormsLogger.debug(e);
            if (e instanceof FormException)
            {
                throw (FormException) e;
            }
            else
            {
                throw new FormException(e);
            }
        }
    }

    /**
     * This is for custom, imported Java Beans.  If those beans
     * extend one of the standard swing components, then we want to register a factory
     * based on the standard factory so we can still use any custom properties.
     */
    public static void tryRegisterCustomFactory(Class compClass, boolean scrollable) throws FormException
    {
        try
        {
            if (compClass == null)
            {
                return;
            }

            /**
             * This is needed to support custom swing components that are scrollable.
             */
            BeanFactory bf = lookupFactory(compClass.getName());
            if (bf instanceof JComponentBeanFactory)
            {
                JComponentBeanFactory jf = (JComponentBeanFactory) bf;
                if (!isStandardFactory(jf))
                {
                    jf.setScrollable(scrollable);
                }
                return;
            }

            JComponentBeanFactory jfactory = lookupAssignableFactory(compClass);
            if (jfactory != null)
            {
                JComponentBeanFactory customfactory = (JComponentBeanFactory) jfactory.getClass().newInstance();
                customfactory.setBeanClass(compClass);
                customfactory.setScrollable(scrollable);
                m_custom_factories.put(compClass.getName(), customfactory);
                return;
            }

            /**
             * If we can't find a registered class, then just use the JComponent class
             */
            JComponentBeanFactory customfactory = new JComponentBeanFactory(compClass);
            customfactory.setScrollable(scrollable);
            m_custom_factories.put(compClass.getName(), customfactory);
        }
        catch (Exception e)
        {
            FormsLogger.debug(e);
            if (e instanceof FormException)
            {
                throw (FormException) e;
            }
            else
            {
                throw new FormException(e);
            }
        }
    }
    /** @link dependency */
    /*# JETABean lnkJETABean; */
}
