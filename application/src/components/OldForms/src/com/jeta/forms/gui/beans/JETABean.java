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

import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JPanel;
import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.logger.FormsLogger;
import com.jeta.forms.store.JETAPersistable;
import com.jeta.forms.store.bean.BeanDeserializer;
import com.jeta.forms.store.bean.BeanSerializer;
import com.jeta.forms.store.bean.BeanSerializerFactory;
import com.jeta.forms.store.bean.DummyObjectInput;
import com.jeta.forms.store.bean.DummyObjectOutput;
import com.jeta.forms.store.properties.JETAProperty;
import com.jeta.forms.store.properties.TransformProperty;
import com.jeta.forms.store.memento.BeanMemento;
import com.jeta.forms.store.memento.PropertiesMemento;
import com.jeta.forms.store.memento.StateRequest;
import com.jeta.forms.store.properties.EventsProperty;
import com.jeta.open.i18n.I18N;
import com.jeta.open.registry.JETARegistry;
import com.jeta.open.support.EmptyCollection;
import java.awt.Container;
import java.io.Externalizable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.debug.Debugger;

/**
 * A <code>JETABean</code> is a container and a proxy for a regular JavaBean. However, a 
 * <code>JETABean</code> also supports the notion of dynamic properties.  These are properties
 * that can be added at design time to the delegate bean.  For example, components such as
 * JList, JTable, JTextArea, etc. are normally contained within a scroll pane.  Instead
 * of requiring the user to create a JScrollPane and adding the child component, we
 * create a scroll property and attach at design-runtime to the list, table, and text areas.
 * This is much easier for the user to work with.
 *
 * All Java Bean components are contained within a JETABean instance in this architecture.
 *
 * @author Jeff Tassin
 */
public class JETABean extends JPanel {

    /**
     * This is the Java Bean component.
     * @directed
     * @supplierCardinality 1
     */
    private Component m_delegate;
    /**
     * The Bean Information for the component. This includes standard and custom properties.
     * @undirected
     */
    private DynamicBeanInfo m_beaninfo;
    /**
     * A map of custom property *values* associated with this bean.
     * m_custom_properties<String,Object>
     *   where String is the custom property name
     *         Object is the custom property value.
     */
    private HashMap<String, JETAProperty> m_custom_properties = new HashMap<String, JETAProperty>();
    private EventsProperty m_events = null;
    private EventsIProxy m_eventsImplementor = null;

    /**
     * Creates a <code>JETABean</code> instance.
     */
    public JETABean() {
        setOpaque(false);
        m_eventsImplementor = new EventsIProxy(this);
    }

    /**
     * Creates a <code>JETABean</code> instance with the specified Java Bean delegate and
     * dynamic properties.
     * @param delegate the Swing component that we are a proxy for
     * @param customProps a collection of default JETAProperty objects that we wish to add to this bean.
     *       These are the dynamic properties for the bean (if any).
     */
    public JETABean(Component delegate, BeanProperties customProperties) throws FormException {
        setOpaque(false);
        m_eventsImplementor = new EventsIProxy(this);
        try {
            m_delegate = delegate;
            m_eventsImplementor.setHandlee(m_delegate);

            if (customProperties != null) {
                m_beaninfo = customProperties.getBeanInfo();

                Iterator iter = customProperties.getPropertyValues().iterator();
                while (iter.hasNext()) {
                    JETAProperty prop = (JETAProperty) iter.next();
                    m_custom_properties.put(prop.getName(), prop);
                }
            }

            /**
             * the delegate can be null when we are deserializing
             */
            if (delegate != null) {
                initialize();
            }
        } catch (Exception e) {
            FormsLogger.severe(e);
        }
    }

    /**
     * Creates a <code>JETABean</code> instance with the specified Java Bean delegate and
     * no custom properties.
     * @param delegate the Swing component that we are a wrapper for
     */
    public JETABean(Component delegate) throws FormException {
        try {
            setOpaque(false);
            m_eventsImplementor = new EventsIProxy(this);

            m_delegate = delegate;
            m_eventsImplementor.setHandlee(m_delegate);
            initialize();
        } catch (Exception e) {
            System.out.println(">>>>>> JETABean initialized failed for delegate: " + m_delegate);
            e.printStackTrace();
        }
    }

    public void setEvents(EventsProperty aEvents) {
        m_events = aEvents;
    }

    /**
     * Returns the Bean information for the Java Bean. Normally, the BeanFactory provides
     * the Bean Information.  However, if this is not the case, then we generate a default
     * BeanInfo using the Introspector.
     * @return the BeanInfo object that describes the properties for the delegate
     */
    public DynamicBeanInfo getBeanInfo() {
        if (m_beaninfo == null) {
            try {
                if (m_delegate != null) {
                    BeanInfo info = Introspector.getBeanInfo(m_delegate.getClass());
                    m_beaninfo = new DynamicBeanInfo(info, null);
                }
            } catch (Exception e) {
                FormsLogger.severe(e);
            }
        }
        return m_beaninfo;
    }

    public String getEventHandler(String event) {
        if (m_events != null) {
            return m_events.getEventHandler(event);
        } else {
            return "";
        }
    }

    public void setEventHandler(String event, String handler) {
        if (m_events == null && m_custom_properties != null) {
            Object o = m_custom_properties.get(EventsProperty.EVENTS_PROPERTY_NAME);
            if (o instanceof EventsProperty) {
                m_events = (EventsProperty) o;
            }
        }
        if (m_events != null) {
            m_events.setEventHandler(event, handler);
        }
    }

    /**
     * Return the custom property associated with this bean.
     * @param propName the name of the property to get.
     * @return the custom property associated with this bean.  Null is returned if
     * the property is not found
     */
    public JETAProperty getCustomProperty(String propName) {
        return m_custom_properties.get(propName);
    }

    /**
     * Returns the underlying Java Bean component. This is the actual Swing component that
     * will be visible on the form.
     * @return the underlying Java Bean component.
     */
    public Component getDelegate() {
        return m_delegate;
    }

    /**
     * This returns the child component of this bean. Normally, this is the same
     * object as the m_delegate. However, in some cases it can be different.  For example,
     * if the delegate is contained in a scrollpane.
     */
    public Component getBeanChildComponent() {
        if (getComponentCount() > 0) {
            return getComponent(0);
        } else {
            return null;
        }
    }

    /**
     * Returns the name of the Java Bean.  This is the same as the 'name' property.
     * @return the name of the delegate
     */
    public String getBeanName() {
        if (m_delegate == null) {
            return null;
        } else {
            return m_delegate.getName();
        }
    }

    /**
     * Return the property descriptors associated with this bean.  This includes
     * all standard and custom properties.
     * @return a collection of JETAPropertyDescriptor objects
     */
    public Collection getPropertyDescriptors() {
        DynamicBeanInfo binfo = getBeanInfo();
        if (binfo != null) {
            return binfo.getPropertyDescriptors();
        } else {
            return EmptyCollection.getInstance();
        }
    }

    /**
     * Stores this bean's state into the given memento object.
     * @param memento the object used to hold the bean state. This includes all property values.
     * @param sr a request object that gives hints on how the state should be stored.
     */
    public void getState(BeanMemento memento, StateRequest sr) throws FormException {
        try {
            memento.setJETABeanClass(getClass().getName());
            if (m_delegate != null && m_delegate.getClass() != null) {
                memento.setBeanClass(m_delegate.getClass().getName());
                BeanSerializerFactory fac = (BeanSerializerFactory) JETARegistry.lookup(BeanSerializerFactory.COMPONENT_ID);
                if (fac != null) {
                    /** store the bean properties */
                    BeanSerializer bs = fac.createSerializer();
                    memento.setProperties(bs.writeBean(this));
                    if (m_delegate instanceof Externalizable) {
                        Externalizable ebean = (Externalizable) m_delegate;
                        DummyObjectOutput out = new DummyObjectOutput();
                        ebean.writeExternal(out);
                        memento.setBeanCustomContent(out.getData());
                    }
                } else {
                    FormUtils.safeAssert(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void executeEvent(String aname, Object ev) {
        if ((!FormUtils.isDesignMode() || FormUtils.isRuntime() || FormUtils.isSemiRuntime() /*|| (getTopLevelAncestor() instanceof RunFrame)*/)
                && m_events != null && isAnyEvents()) {
            try {
                String Handler = m_events.getEventHandler(aname);
                if (Handler != null && !Handler.equals("")) {
                    FormComponent fc = FormUtils.getParentLinkedForm(this);
                    if (fc != null && fc.getScriptScope() != null) {
                        ContextFactory cf = ContextFactory.getGlobal();
                        Context cx = cf.enterContext();
                        try {
                            ScriptableObject evScope = cx.initStandardObjects();
                            evScope.defineProperty("ev", Context.javaToJS(ev, evScope), 0);
                            ScriptableObject scope = (ScriptableObject) fc.getScriptScope();
                            evScope.setParentScope(scope);
                            Debugger ldbg = cx.getDebugger();
                            Object ldbgCtxData = cx.getDebuggerContextData();
                            cx.setDebugger(null, null);
                            Script lev = cx.compileString(Handler, aname, 0, null);
                            cx.setDebugger(ldbg, ldbgCtxData);
                            lev.exec(cx, evScope);
                        } finally {
                            Context.exit();
                        }
                    }
                }
            } catch (Exception ex) {
                if (!(ex instanceof IllegalStateException) || ex.getMessage() == null || !ex.getMessage().equals("break")) {
                    if (ex.getMessage() != null) {
                        if (ex instanceof JavaScriptException) {
                            Logger.getLogger(JETABean.class.getName()).log(Level.SEVERE, " while executing \"{0}\" event handler. {1}", new Object[]{aname, ex.getMessage()});
                        } else {
                            Logger.getLogger(JETABean.class.getName()).log(Level.SEVERE, " while executing \"" + aname + "\" event handler.", ex);
                        }
                        Container c = getTopLevelAncestor();
                        if (c != null) {
                            JOptionPane.showMessageDialog(c, ex.getMessage(), I18N.getLocalizedMessage("Error"), JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        }
    }

    void executeSource(String Handler, Object ev) {
        try {
            if (Handler != null && !Handler.equals("")) {
                FormComponent fc = FormUtils.getParentLinkedForm(this);
                if (fc != null && fc.isRuntime() && fc.getScriptScope() != null) {
                    ContextFactory cf = ContextFactory.getGlobal();
                    Context cx = cf.enterContext();
                    try {
                        ScriptableObject evScope = cx.initStandardObjects();
                        evScope.defineProperty("ev", Context.javaToJS(ev, evScope), 0);
                        ScriptableObject scope = (ScriptableObject) fc.getScriptScope();
                        evScope.setParentScope(scope);
                        Script lev = null;
                        Debugger ldbg = cx.getDebugger();
                        Object ldbgCtxData = cx.getDebuggerContextData();
                        cx.setDebugger(null, null);
                        try {
                            lev = cx.compileString(Handler, FormUtils.SCRIPT_ACTION_NAME, 0, null);
                        } finally {
                            cx.setDebugger(ldbg, ldbgCtxData);
                        }
                        if (lev != null) {
                            lev.exec(cx, evScope);
                        }
                    } finally {
                        Context.exit();
                    }
                }
            }
        } catch (Exception ex) {
            if (ex instanceof JavaScriptException) {
                Logger.getLogger(JETABean.class.getName()).log(Level.SEVERE, " while executing \"{0}\" event handler. {1}", new Object[]{FormUtils.SCRIPT_ACTION_NAME, ex.getMessage()});
            } else {
                Logger.getLogger(JETABean.class.getName()).log(Level.SEVERE, " while executing \"" + FormUtils.SCRIPT_ACTION_NAME + "\" event handler.", ex);
            }
            Container c = getTopLevelAncestor();
            if (c != null) {
                JOptionPane.showMessageDialog(c, ex.getMessage(), I18N.getLocalizedMessage("Error"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    boolean isAnyEvents() {
        return (m_events != null && m_events.getEvents() != null
                && !m_events.getEvents().isEmpty());
    }

    /**
     * Called after instantiation or deserialization.  Initializes the bean as well as the custom
     * properties associated with the bean.
     */
    private void initialize() throws FormException {
        try {
            removeAll();
            if (m_delegate != null) {
                setLayout(new BorderLayout());
                add(m_delegate, BorderLayout.CENTER);

                /** tell each custom property to updateBean */
                Collection props = m_custom_properties.values();
                Iterator iter = props.iterator();
                while (iter.hasNext()) {
                    JETAProperty jprop = (JETAProperty) iter.next();
                    jprop.updateBean(this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public EventsIProxy getEventsHandler() {
        return m_eventsImplementor;
    }

    /**
     * Sets a custom property associated with this bean.
     */
    public void setCustomProperty(String propName, JETAProperty prop) {
        m_custom_properties.put(propName, prop);
    }

    /**
     * This should never be called.
     */
    @Override
    public void setName(String name) {
        FormUtils.safeAssert(false);
    }
    private static final String CUSTOM_CONTENT_READ_KEY = "CustomConentRead";

    /**
     * Sets this bean's state using the given memento object.
     * @param memento the bean state.  This was created by calling getState.
     *
     * @see #getState
     */
    public void setState(BeanMemento memento) throws FormException {
        try {
            PropertiesMemento props_memento = memento.getProperties();
            if (props_memento != null) {
                setState(props_memento);
            } else {
                setStateOld(memento);
            }
            if (m_delegate != null) {
                if (m_delegate instanceof Externalizable) {
                    Externalizable ebean = (Externalizable) m_delegate;
                    DummyObjectInput in = new DummyObjectInput();
                    if (memento.getBeanCustomContentRead() != null) {
                        in.setData(memento.getBeanCustomContentRead());
                        ebean.readExternal(in);
                    } else {
                        in.setData(memento.getBeanCustomContent());
                        ebean.readExternal(in);
                        if (m_delegate instanceof JComponent) {
                            Object customContentRead = ((JComponent) m_delegate).getClientProperty(CUSTOM_CONTENT_READ_KEY);
                            if (customContentRead != null) {
                                memento.setBeanCustomContentRead(customContentRead);
                            }
                            ((JComponent) m_delegate).putClientProperty(CUSTOM_CONTENT_READ_KEY, null);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets this bean's state using the given properties memento object.
     * @param memento the bean state.
     */
    public void setState(PropertiesMemento props_memento) throws FormException {
        try {
            if (props_memento != null) {
                removeAll();
                setLayout(new BorderLayout());
                m_delegate = null;

                BeanSerializerFactory fac = (BeanSerializerFactory) JETARegistry.lookup(BeanSerializerFactory.COMPONENT_ID);
                if (fac != null) {
                    BeanDeserializer bds = fac.createDeserializer(props_memento);
                    m_delegate = bds.createBean();
                    if (m_delegate != null) {
                        add(m_delegate, BorderLayout.CENTER);
                        /** tell each custom property to updateBean */
                        Collection props = m_custom_properties.values();
                        Iterator iter = props.iterator();
                        while (iter.hasNext()) {
                            JETAProperty jprop = (JETAProperty) iter.next();
                            if (jprop instanceof TransformProperty) {
                                ((TransformProperty) jprop).setBean(this);
                            }
                        }
                        bds.initializeBean(this);
                        m_eventsImplementor.setHandlee(m_delegate);
                    }
                } else {
                    FormUtils.safeAssert(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the bean state using deprecated file format.
     */
    private void setStateOld(BeanMemento memento) {
//      try
//      {
//	 /** this is a deprecated format that we still support */
//	 byte[] xml_data = memento.getBeanXML();
//	 byte[] xml = memento.getBeanXML();
//	 if ( xml == null )
//	    return;
//	 
//	 XMLDecoder d = new XMLDecoder( new ByteArrayInputStream( xml ) );
//	 m_delegate = (Component)d.readObject();
//	 if ( m_delegate == null )
//	    return;
//         m_eventsImplementor.setHandlee(m_delegate);   
//	 // special handling for JList because we need to set the list model to DefaultListModel
//	 // this is for the custom ItemsProperty
//	 if ( m_delegate instanceof JList )
//	 {
//	    ((JList)m_delegate).setModel( new javax.swing.DefaultListModel() );
//	 }
//	 Collection custom_props = memento.getCustomProperties();
//	 if ( custom_props != null )
//	 {
//	    Iterator iter = custom_props.iterator();
//	    while( iter.hasNext() )
//	    {
//	       /**
//		* Here we are changing the default custom properties to any saved
//		* values.
//		*/
//	       JETAProperty prop = (JETAProperty)iter.next();
//	       JETAProperty default_prop = getCustomProperty( prop.getName() );
//	       if ( default_prop != null )
//	       {
//		  if ( !(default_prop instanceof TransformProperty) )
//		  {
//		     default_prop.setValue( prop );
//		  }
//	       }
//	       else
//	       {
//		  if ( !(prop instanceof TransformProperty) )
//		  {
//		     FormsLogger.debug( "JETABean.setState getCustomProperty failed: " + prop.getName() + "  " + prop );
//		  }
//	       }
//	    }
//	 }
//	 initialize();
//         if(m_eventsImplementor != null && m_delegate != null)
//             m_eventsImplementor.setHandlee(m_delegate);
//      }
//      catch( Exception e )
//      {
//	 e.printStackTrace();
//      }
    }

    /**
     * PostInitialize is called once after all components in a form have been re-instantiated
     * at runtime (not design time).  This gives each property and component a chance to
     * do some last minute initializations that might depend on the top level parent.
     * An example of this is using ButtonGroups.  Groups for JRadioButtons are global to a FormPanel
     * and not specific to each FormComponent instance.
     */
    public void postInitialize(FormPanel panel) {
        Collection props = m_custom_properties.values();
        Iterator iter = props.iterator();
        while (iter.hasNext()) {
            JETAProperty jprop = (JETAProperty) iter.next();
            jprop.postInitialize(panel, this);
        }
    }

    public void resolveReferences(HashMap<Long, GridComponent> aAllComps) {
        if (m_beaninfo != null) {
            Collection<JETAPropertyDescriptor> jpds = m_beaninfo.getPropertyDescriptors();
            if (jpds != null) {
                Iterator<JETAPropertyDescriptor> jpdsIt = jpds.iterator();
                if (jpdsIt != null) {
                    while (jpdsIt.hasNext()) {
                        JETAPropertyDescriptor jpd = jpdsIt.next();
                        if (jpd != null) {
                            Object oprop = null;
                            try {
                                oprop = jpd.getPropertyValue(this);
                            } catch (Exception le) {
                                oprop = null;
                            }
                            if (oprop != null && oprop instanceof JETAPersistable) {
                                JETAPersistable jp = (JETAPersistable) oprop;
                                jp.resolveReferences(this, aAllComps, jpd.getName());
                            }
                        }
                    }
                }
            }
        }
        if (!FormUtils.isDesignMode()) {
            m_beaninfo = null;
            m_custom_properties.clear();
        }
    }
}
