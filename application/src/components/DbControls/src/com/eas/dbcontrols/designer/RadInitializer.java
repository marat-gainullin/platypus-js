/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.designer;

import com.bearsoft.org.netbeans.modules.form.*;
import com.bearsoft.org.netbeans.modules.form.editors.AbstractFormatterFactoryEditor;
import com.bearsoft.org.netbeans.modules.form.editors.IconEditor;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.beans.Introspector;
import java.beans.PropertyEditor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ButtonGroup;
import org.openide.ErrorManager;

/**
 *
 * @author mg
 */
public class RadInitializer {

    protected RADComponent<?> component;
    protected Map<String, RADComponent<?>> components;

    public RadInitializer(RADComponent<?> aComponent, Map<String, RADComponent<?>> aComponents) {
        super();
        component = aComponent;
        components = aComponents;
    }
    private static final Object[] EMPTY_ARGS = new Object[]{};

    public static void initializeProperties(DesignInfo di, FormProperty<?>[] aProperties) throws Exception {
        Map<String, Method> getters = getGetters(di.getClass());
        for (FormProperty<?> nProp : aProperties) {
            if (nProp instanceof FormProperty<?>) {
                FormProperty<Object> radProp = (FormProperty<Object>) nProp;
                Method getter = getters.get(radProp.getName());
                if (getter != null) {
                    Object res = getter.invoke(di, EMPTY_ARGS);
                    try {
                        if (res != null && !isValuesEqual(res, radProp.getDefaultValue())) {
                            radProp.setValue(res);
                        }
                    } catch (IllegalArgumentException argex) {
                    }
                }
            }
        }
    }

    public void initializeProperties(DesignInfo di) {
        try {
            Map<String, Method> getters = getGetters(di.getClass());
            for (RADProperty<Object> radProp : (RADProperty<Object>[]) component.getAllBeanProperties()) {
                Method getter = getters.get(radProp.getName());
                if (getter != null) {
                    Object res = getter.invoke(di, EMPTY_ARGS);
                    try {
                        // ignore null and default values
                        if (res != null && !isValuesEqual(res, radProp.getDefaultValue())) {
                            radProp.setValue(res);
                        }
                    } catch (Exception argex) {
                        try {
                            radProp.restoreDefaultValue();
                            if (res instanceof String) {
                                if (Component.class.isAssignableFrom(radProp.getValueType())
                                        || ButtonGroup.class.isAssignableFrom(radProp.getValueType())) {
                                    radProp.setValue(new ComponentChooserEditor.ComponentRef<>((String) res, component.getFormModel()));
                                } else if (javax.swing.Icon.class.isAssignableFrom(radProp.getValueType()) || java.awt.Image.class.isAssignableFrom(radProp.getValueType())) {
                                    String iconName = (String) res;
                                    radProp.setValue(IconEditor.iconFromResourceName(component.getFormModel().getDataObject(), iconName));
                                }
                            } else if (res instanceof Integer && "cursor".equals(radProp.getName())) {
                                radProp.setValue(Cursor.getPredefinedCursor((Integer) res));
                            }
                        } catch (Exception ex) {
                            ErrorManager.getDefault().notify(ex);
                        }
                    }
                    PropertyEditor radPEditor = radProp.getPropertyEditor();
                    if (radPEditor instanceof FormAwareEditor) {
                        ((FormAwareEditor) radPEditor).setContext(component.getFormModel(), radProp);
                    }
                } else {
                    if (RADComponent.FormatterFactoryProperty.FORMATTER_FACTORY_PROP_NAME.equals(radProp.getName())) {
                        if (di instanceof FormattedFieldDesignInfo) {
                            FormattedFieldDesignInfo fdi = (FormattedFieldDesignInfo) di;
                            if (fdi.getFormat() != null) {
                                radProp.setValue(AbstractFormatterFactoryEditor.FormFormatter.valueOf(fdi.getFormat(), fdi.getValueType()));
                            }
                        } else if (di instanceof DbLabelDesignInfo) {
                            DbLabelDesignInfo ldi = (DbLabelDesignInfo) di;
                            if (ldi.getFormat() != null) {
                                radProp.setValue(AbstractFormatterFactoryEditor.FormFormatter.valueOf(ldi.getFormat(), ldi.getValueType()));
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }

    public static boolean isValuesEqual(Object value1, Object value2) {
        if (value1 == null && value2 != null) {
            return false;
        }
        if (value2 == null && value1 != null) {
            return false;
        }
        if (value1 != null && value2 != null && !value1.equals(value2)) {
            return false;
        }
        return true;
    }
    protected static Map<String, Map<String, Method>> methodsCache = new HashMap<>();

    public static Map<String, Method> getGetters(Class<?> aClass) throws Exception {
        Map<String, Method> getters = methodsCache.get(aClass.getName());
        if (getters == null) {
            getters = new HashMap<>();
            methodsCache.put(aClass.getName(), getters);
            Method[] methods = aClass.getMethods();
            for (Method m : methods) {
                int modifiers = m.getModifiers();
                if (Modifier.isPublic(modifiers)
                        && !Modifier.isAbstract(modifiers)
                        && !Modifier.isStatic(modifiers)
                        && !m.isSynthetic() && !m.isVarArgs()
                        && (m.getParameterTypes() == null
                        || m.getParameterTypes().length == 0)
                        && ((m.getName().startsWith(Object2Dom.BEANY_GETTER_PREFIX) && m.getName().length() > Object2Dom.BEANY_GETTER_PREFIX.length())
                        || (m.getName().startsWith(Object2Dom.BEANY_IS_PREFIX) && m.getName().length() > Object2Dom.BEANY_IS_PREFIX.length()))) {
                    String propName = null;
                    if (m.getName().startsWith(Object2Dom.BEANY_GETTER_PREFIX)) {
                        propName = m.getName().substring(Object2Dom.BEANY_GETTER_PREFIX.length());
                    } else {
                        propName = m.getName().substring(Object2Dom.BEANY_IS_PREFIX.length());
                    }
                    getters.put(Introspector.decapitalize(propName), m);
                }
            }
        }
        return getters;
    }
}
