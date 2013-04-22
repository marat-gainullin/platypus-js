/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jeta.forms.store.properties;

import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;
import com.jeta.forms.store.JETAPersistable;
import com.jeta.open.rules.JETARule;
import java.awt.Component;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;

/**
 *
 * @author Marat
 */
public class ComponentNativeRefProperty extends Component implements JETAPersistable {

    public ComponentRefProperty m_prop = new ComponentRefProperty();

    public ComponentNativeRefProperty() {
    }

    public Component getValue() {
        if (m_prop != null && m_prop.getGridComponent() != null) {
            return m_prop.getGridComponent().getBeanDelegate();
        }
        return null;
    }

    public void setPropertyValueBaseClass(Class<?> aClass) {
        if (m_prop != null) {
            m_prop.setPropertyValueBaseClass(aClass);
        }
    }

    public JETARule getNeededRule() {
        if (m_prop != null) {
            return m_prop.getNeededRule();
        }
        return null;
    }

    @Override
    public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
        m_prop.read(in);
    }

    @Override
    public void write(JETAObjectOutput out) throws IOException {
        m_prop.write(out);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        m_prop.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        m_prop.writeExternal(out);
    }

    @Override
    public void resolveReferences(JETABean jbean, HashMap<Long, GridComponent> aAllComps, String propName) {
        m_prop.resolveReferences(jbean, aAllComps, propName);
        // a-la UpdateBean
        if (jbean != null && jbean.getDelegate() != null
                && m_prop != null && m_prop.getGridComponent() != null) {
            GridComponent gc = m_prop.getGridComponent();
            if (gc.getBeanDelegate() instanceof JComponent
                    && jbean.getDelegate() instanceof JComponent) {
                JComponent ref = (JComponent) gc.getBeanDelegate();
                JComponent refee = (JComponent) jbean.getDelegate();
                if (ref != null && refee != null && propName != null && propName.length() > 0) {
                    try {
                        Method mtd = refee.getClass().getMethod("set" + propName.substring(0, 1).toUpperCase() + propName.substring(1), Component.class);
                        if (mtd != null) {
                            try {
                                mtd.invoke(refee, ref);
                                if (FormUtils.isDesignMode()) {
                                    mtd.invoke(refee, this);
                                }
                                // Prop need to be set by name through the reflection
                            } catch (IllegalAccessException ex) {
                                Logger.getLogger(ComponentNativeRefProperty.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IllegalArgumentException ex) {
                                Logger.getLogger(ComponentNativeRefProperty.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (InvocationTargetException ex) {
                                Logger.getLogger(ComponentNativeRefProperty.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } catch (NoSuchMethodException ex) {
                        Logger.getLogger(ComponentNativeRefProperty.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (SecurityException ex) {
                        Logger.getLogger(ComponentNativeRefProperty.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
}
