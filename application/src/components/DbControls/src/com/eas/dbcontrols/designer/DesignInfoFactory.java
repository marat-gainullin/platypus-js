/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.designer;

import com.bearsoft.org.netbeans.modules.form.ComponentReference;
import com.bearsoft.org.netbeans.modules.form.FormProperty;
import com.bearsoft.org.netbeans.modules.form.RADComponent;
import com.bearsoft.org.netbeans.modules.form.RADVisualComponent;
import com.bearsoft.org.netbeans.modules.form.RADVisualFormContainer;
import com.bearsoft.org.netbeans.modules.form.bound.RADModelGrid;
import com.bearsoft.org.netbeans.modules.form.editors.AbstractFormatterFactoryEditor;
import com.bearsoft.org.netbeans.modules.form.editors.IconEditor.NbImageIcon;
import java.awt.*;
import java.lang.reflect.Method;
import java.util.Map;

/**
 *
 * @author mg
 */
public class DesignInfoFactory {

    public static void initWithComponent(DesignInfo designInfo, final RADComponent<?> aRadComp) throws Exception {
        // properties
        initWithProperties(designInfo, aRadComp.getAllBeanProperties());
        // form designed size
        if (designInfo instanceof FormDesignInfo && aRadComp instanceof RADVisualFormContainer) {
            RADVisualFormContainer radForm = (RADVisualFormContainer) aRadComp;
            FormDesignInfo fdi = (FormDesignInfo) designInfo;
            fdi.setDesignedPreferredSize(radForm.getDesignerSize());
        } else if (designInfo instanceof ControlDesignInfo) {
            ControlDesignInfo cdi = (ControlDesignInfo) designInfo;
            if (aRadComp instanceof RADVisualComponent<?>)// it may be button group, wich is not a visual component
            {
                Component comp = ((RADVisualComponent<?>) aRadComp).getBeanInstance();
                if (comp != null) {
                    cdi.setDesignedPreferredSize(comp.getSize());
                    if (aRadComp instanceof RADModelGrid) {
                        assert comp instanceof DbGrid;
                        assert cdi instanceof DbGridDesignInfo;
                        DbGridDesignInfo gridDi = (DbGridDesignInfo) cdi;
                        RADModelGrid radGrid = (RADModelGrid) aRadComp;
                        gridDi.getHeader().clear();
                        gridDi.getHeader().addAll(radGrid.getBeanInstance().getHeader());
                        initColumnsContainerWithEvents(radGrid);
                    } else if (aRadComp instanceof RADModelMap) {
                        assert comp instanceof DbMap;
                        assert cdi instanceof DbMapDesignInfo;
                        DbMapDesignInfo mapDi = (DbMapDesignInfo) cdi;
                        RADModelMap radMap = (RADModelMap) aRadComp;
                        mapDi.getFeatures().clear();
                        mapDi.getFeatures().addAll(radMap.getBeanInstance().getFeatures());
                    }
                }
            }
        }
        // name
        Map<String, Method> setters = getSetters(designInfo.getClass());
        Method nameSetter = setters.get("name");
        nameSetter.invoke(designInfo, aRadComp.getName());
    }

    public static void initWithProperties(DesignInfo di, FormProperty<?>[] aProperties) throws Exception {
        DesignInfo defaultInstance = di.getClass().newInstance();
        Map<String, Method> setters = getSetters(di.getClass());
        Map<String, Method> getters = getGetters(di.getClass());
        for (FormProperty<?> prop : aProperties) {
            Method setter = setters.get(prop.getName());
            if (setter != null) {
                Object propValue = prop.getValue();
                try {
                    if (prop.isDefaultValue()) {
                        Method defGetter = getters.get(prop.getName());
                        if (defGetter != null) {
                            propValue = defGetter.invoke(defaultInstance, new Object[]{});
                        } else {
                            propValue = null;
                        }
                    }
                    setter.invoke(di, propValue);
                } catch (IllegalArgumentException argex) {
                    if (propValue != null) {
                        if (propValue instanceof ComponentReference<?>
                                && setter.getParameterTypes() != null && setter.getParameterTypes().length == 1
                                && String.class.isAssignableFrom(setter.getParameterTypes()[0])) {
                            ComponentReference<?> compRef = (ComponentReference<?>) propValue;
                            if (compRef.getComponent() != null) {
                                setter.invoke(di, compRef.getComponent().getName());
                            }
                        } else if (propValue instanceof NbImageIcon
                                && setter.getParameterTypes() != null && setter.getParameterTypes().length == 1
                                && String.class.isAssignableFrom(setter.getParameterTypes()[0])) {
                            setter.invoke(di, ((NbImageIcon) propValue).getName());
                        } else if (propValue instanceof NbBorder
                                && setter.getParameterTypes() != null && setter.getParameterTypes().length == 1
                                && BorderDesignInfo.class.isAssignableFrom(setter.getParameterTypes()[0])) {
                            setter.invoke(di, convertNbBorderToBorderDesignInfo((NbBorder) propValue));
                        } else if (propValue instanceof java.awt.Cursor
                                && setter.getParameterTypes() != null && setter.getParameterTypes().length == 1) {
                            setter.invoke(di, ((java.awt.Cursor) propValue).getType());
                        }
                    }
                } catch (Exception ex) {
                    ex = null;
                }
            } else {
                if (RADComponent.FormatterFactoryProperty.FORMATTER_FACTORY_PROP_NAME.equals(prop.getName())) {
                    AbstractFormatterFactoryEditor.FormFormatter formatter = (AbstractFormatterFactoryEditor.FormFormatter) prop.getValue();
                    if (formatter != null) {
                        if (di instanceof FormattedFieldDesignInfo) {
                            ((FormattedFieldDesignInfo) di).setFormat(formatter.getFormat().getFormat());
                            ((FormattedFieldDesignInfo) di).setValueType(formatter.getFormat().getType());
                        }
                        if (di instanceof DbLabelDesignInfo) {
                            ((DbLabelDesignInfo) di).setFormat(formatter.getFormat().getFormat());
                            ((DbLabelDesignInfo) di).setValueType(formatter.getFormat().getType());
                        }
                    }
                }
            }
        }
    }
}
