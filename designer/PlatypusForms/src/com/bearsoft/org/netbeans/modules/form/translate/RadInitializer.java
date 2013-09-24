/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.translate;

import com.bearsoft.org.netbeans.modules.form.*;
import com.bearsoft.org.netbeans.modules.form.editors.AbstractFormatterFactoryEditor;
import com.bearsoft.org.netbeans.modules.form.editors.IconEditor;
import com.bearsoft.org.netbeans.modules.form.editors.NbBorder;
import com.eas.controls.DesignInfo;
import com.eas.controls.borders.BorderDesignInfo;
import com.eas.controls.borders.CompoundBorderDesignInfo;
import com.eas.controls.borders.MatteBorderDesignInfo;
import com.eas.controls.borders.TitledBorderDesignInfo;
import com.eas.controls.plain.FormattedFieldDesignInfo;
import com.eas.controls.visitors.SwingBorderFactory;
import com.eas.dbcontrols.label.DbLabelDesignInfo;
import com.eas.store.Object2Dom;
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

    public void initializeEvents(DesignInfo di) {
        initializeEvents(component, di);
    }

    public static void initializeEvents(RADComponent<?> component, DesignInfo di) {
        component.getFormModel().getDataObject().getCodeGenerator().setCanPosition(false);
        try {
            Map<String, Method> getters = getGetters(di.getClass());
            for (Event event : component.getAllEvents()) {
                Method getter = getters.get(event.getName());
                if (getter != null) {
                    Object res = getter.invoke(di, EMPTY_ARGS);
                    try {
                        if (res != null && !"".equals(res)) {
                            component.getFormModel().getFormEvents().attachEvent(event, (String) res, null);
                        }
                    } catch (IllegalArgumentException argex) {
                    }
                }
            }
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        } finally {
            component.getFormModel().getDataObject().getCodeGenerator().setCanPosition(true);
        }
    }

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
                            } else if (res instanceof BorderDesignInfo) {
                                NbBorder converted = convertBorderDesignInfoToNbBorder(radProp, (BorderDesignInfo) res);
                                radProp.setValue(converted);
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

    private NbBorder convertBorderDesignInfoToNbBorder(FormProperty<?> aProp, BorderDesignInfo aInfo) throws Exception {
        if (aInfo != null) {
            SwingBorderFactory bFactory = new SwingBorderFactory(null);
            aInfo.accept(bFactory);
            NbBorder nbBorder = new NbBorder(bFactory.getBorder());
            nbBorder.setPropertyContext(new FormPropertyContext.SubProperty(aProp));
            IconEditor.NbImageIcon wasMatteIconValue = null;
            FormProperty<Color> matteColorProperty = null;
            FormProperty<IconEditor.NbImageIcon> matteIconProperty = null;
            if (aInfo instanceof MatteBorderDesignInfo) {
                MatteBorderDesignInfo mbdi = (MatteBorderDesignInfo) aInfo;
                matteColorProperty = nbBorder.<FormProperty<Color>>getPropertyOfName("matteColor");
                matteIconProperty = nbBorder.<FormProperty<IconEditor.NbImageIcon>>getPropertyOfName("tileIcon");
                if (mbdi.getTileIcon() != null && !mbdi.getTileIcon().isEmpty()) {
                    wasMatteIconValue = IconEditor.iconFromResourceName(component.getFormModel().getDataObject(), mbdi.getTileIcon());
                } else {
                    matteColorProperty.setValue(mbdi.getMatteColor());
                }
            } else if (aInfo instanceof CompoundBorderDesignInfo) {
                CompoundBorderDesignInfo cbdi = (CompoundBorderDesignInfo) aInfo;
                FormProperty<NbBorder> insideBorderProperty = nbBorder.<FormProperty<NbBorder>>getPropertyOfName("insideBorder");
                insideBorderProperty.setValue(convertBorderDesignInfoToNbBorder((FormProperty<?>) insideBorderProperty, cbdi.getInsideBorder()));
                FormProperty<NbBorder> outsideBorderProperty = nbBorder.<FormProperty<NbBorder>>getPropertyOfName("outsideBorder");
                outsideBorderProperty.setValue(convertBorderDesignInfoToNbBorder((FormProperty<?>) outsideBorderProperty, cbdi.getOutsideBorder()));
            } else if (aInfo instanceof TitledBorderDesignInfo) {
                TitledBorderDesignInfo tbdi = (TitledBorderDesignInfo) aInfo;
                FormProperty<NbBorder> borderProperty = nbBorder.<FormProperty<NbBorder>>getPropertyOfName("border");
                borderProperty.setValue(convertBorderDesignInfoToNbBorder((FormProperty<?>) borderProperty, tbdi.getBorder()));
            }
            for (FormProperty<?> prop : nbBorder.getProperties()) {
                prop.setChanged(true);
            }
            if (matteIconProperty != null) {
                if (wasMatteIconValue != null) {
                    matteColorProperty.setChanged(false);
                    matteIconProperty.setChanged(true);
                    matteIconProperty.setValue(wasMatteIconValue);
                } else {
                    matteIconProperty.setChanged(false);
                }
            }
            return nbBorder;
        } else {
            return null;
        }
    }
}
