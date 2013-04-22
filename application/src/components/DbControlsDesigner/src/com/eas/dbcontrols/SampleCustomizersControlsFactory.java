/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols;

import com.eas.dbcontrols.DbControl;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class SampleCustomizersControlsFactory {

    protected Map<Class<?>, java.beans.Customizer> customizers = new HashMap<>();

    public java.beans.Customizer createCustomizer(Class<?> aControlClass) {
        return achiveCustomizer(aControlClass);
    }

    public DbControl createSampleBean(Class<?> aControlClass) {
        try {
            Object bean = aControlClass.newInstance();
            if (bean != null && bean instanceof DbControl) {
                return (DbControl) bean;
            }
        } catch (InstantiationException ex) {
            Logger.getLogger(SampleCustomizersControlsFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(SampleCustomizersControlsFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private DbControlCustomizer achiveCustomizer(Class<?> aControlClass) {
        if (aControlClass != null) {
            try {
                Class<?> beanInfoClass = Class.forName(aControlClass.getName() + "BeanInfo");
                if (beanInfoClass != null) {
                    try {
                        Object oBi = beanInfoClass.newInstance();
                        if (oBi != null && oBi instanceof BeanInfo) {
                            BeanInfo bi = (BeanInfo) oBi;
                            BeanDescriptor bd = bi.getBeanDescriptor();
                            if (bd != null) {
                                Class<?> custClass = bd.getCustomizerClass();
                                if (custClass != null) {
                                    Object oCust = custClass.newInstance();
                                    if (oCust != null && oCust instanceof DbControlCustomizer) {
                                        return (DbControlCustomizer) oCust;
                                    }
                                }
                            }
                        }
                    } catch (InstantiationException ex) {
                        Logger.getLogger(SampleCustomizersControlsFactory.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(SampleCustomizersControlsFactory.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(SampleCustomizersControlsFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
}
