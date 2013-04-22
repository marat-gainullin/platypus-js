/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jeta.forms.store.properties;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.form.GridComponent;
import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class DynamicComponentRefProperty extends ComponentRefProperty {

    static final long serialVersionUID = -2205681173141194577L;
    /**
     * The current version number of this class
     */
    public static final int VERSION = 1;
    public static final String PROP_NAME_TAG_NAME = "propertyName";
    protected Class<? extends Component> m_compClass = Component.class;

    public DynamicComponentRefProperty() {
        super();
    }

    public DynamicComponentRefProperty(String aName, Class<? extends Component> aClass) {
        super(aClass);
        m_name = aName;
        m_compClass = aClass;
    }

    @Override
    public Class getNeededClass() {
        return m_compClass;
    }

    protected String getMethodNameByPropName(String aPropName) {
        if (aPropName != null && !aPropName.isEmpty()) {
            String lPropName = "set" + aPropName.substring(0, 1).toUpperCase();
            if (aPropName.length() > 1) {
                lPropName += aPropName.substring(1);
            }
            return lPropName;
        }
        return null;
    }

    @Override
    public void updateBean(JETABean jbean) {
        if (jbean != null && gridComponent != null) {
            setValue2Bean(jbean.getDelegate());
        }
    }

    @Override
    public void resolveReferences(JETABean jbean, HashMap<Long, GridComponent> aAllComps, String propName) {
        gridComponent = null;
        GridComponent gc = aAllComps.get(m_ComponentID);
        if (gc != null) {
            m_name = propName;
            setGridComponent(gc);
            updateBean(jbean);
        }
    }
    protected static final int MAX_SETTERS_CACHE_SIZE = 128;
    protected static final HashMap<String, Method> settersCache = new HashMap<String, Method>();

    protected String makeMethodKey(String targetClassName, String aPropName, String paramClassName) {
        StringBuilder sb = new StringBuilder();
        sb.append(targetClassName);
        sb.append(";");
        sb.append(aPropName);
        sb.append(";");
        sb.append(paramClassName);
        return sb.toString();
    }

    protected Method getCachedSetter(Class aCompClass) throws NoSuchMethodException {
        String methodKey = makeMethodKey(aCompClass.getName(), m_name, m_compClass.getName());
        Method method = settersCache.get(methodKey);
        if (method == null) {
            String mName = getMethodNameByPropName(m_name);
            if (mName != null && !mName.isEmpty()) {
                method = aCompClass.getMethod(mName, m_compClass);
                if (method != null) {
                    if (settersCache.size() >= MAX_SETTERS_CACHE_SIZE) {
                        settersCache.clear();
                    }
                    settersCache.put(methodKey, method);
                }
            }
        }
        return method;
    }

    protected void setValue2Bean(Component comp) {
        if (comp != null && gridComponent != null && gridComponent.getBeanDelegate() != null) {
            m_compClass = gridComponent.getBeanDelegate().getClass();
            try {
                Method method = getCachedSetter(comp.getClass());
                if (method != null) {
                    method.invoke(comp, gridComponent.getBeanDelegate());
                }
            } catch (IllegalAccessException ex) {
                Logger.getLogger(DynamicComponentRefProperty.class.getName()).log(Level.SEVERE, m_name, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(DynamicComponentRefProperty.class.getName()).log(Level.SEVERE, m_name, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(DynamicComponentRefProperty.class.getName()).log(Level.SEVERE, m_name, ex);
            } catch (NoSuchMethodException ex) {
                Logger.getLogger(DynamicComponentRefProperty.class.getName()).log(Level.SEVERE, m_name, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(DynamicComponentRefProperty.class.getName()).log(Level.SEVERE, m_name, ex);
            }
        }
    }
//    @Override
//    public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
//        super.read( in.getSuperClassInput() );
//        int version = in.readVersion();
//        m_PropertyName = in.readString(PROP_NAME_TAG_NAME);
//    }
//
//    @Override
//    public void write(JETAObjectOutput out) throws IOException {
//        super.write(out.getSuperClassOutput(JETAProperty.class));
//        out.writeVersion(VERSION);
//        out.writeString(PROP_NAME_TAG_NAME, m_PropertyName);
//    }
}
