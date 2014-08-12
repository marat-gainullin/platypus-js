/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package com.bearsoft.org.netbeans.modules.form;

import com.bearsoft.org.netbeans.modules.form.editors.EnumEditor;
import com.bearsoft.org.netbeans.modules.form.editors.MnemonicEditor;
import com.bearsoft.org.netbeans.modules.form.editors.PrimitiveTypeArrayEditor;
import java.beans.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.WindowConstants;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;

/**
 * Takes care of registration and finding property editors used by the Form
 * Editor.
 *
 * @author Ian Formanek, Tomas Pavek
 */
final public class FormPropertyEditorManager {

    /**
     * Maps property type to property editor class. There are 2 maps - one for
     * findings of the standard PropertyEditorManager, one for our specific
     * search path (org.netbeans.modules.form.editors2 etc).
     */
    private static Map<Class<?>, Class<PropertyEditor>>[] editorClassCache;
    /**
     * Maps property type to list of property editor classes. For externally
     * registered editors (like i18n).
     */
    private static Map<Class<?>, List<Class<? extends PropertyEditor>>> expliciteEditors;

    // -------
    public static synchronized PropertyEditor findEditor(FormProperty<?> property) {
        Class<?> type = property.getValueType();
        List<PropertyEditor> list = findEditors(type, false);
        return list.isEmpty() ? null : list.get(0);
    }

    public static synchronized PropertyEditor[] getAllEditors(FormProperty<?> property) {
        Class<?> type = property.getValueType();
        List<PropertyEditor> list = findEditors(type, true);
        if (property instanceof RADProperty<?>) {
            PropertyEditor wrapper = checkEnumWrapper((RADProperty<?>) property);
            if (wrapper != null) {
                list.add(0, wrapper);// hack. If a property has to be wrapped, it will be wrapped only if wrapper editor is first element in the list
            }
        }
        return list.toArray(new PropertyEditor[]{});
    }

    public static synchronized void registerEditor(Class<?> propertyType, Class<? extends PropertyEditor> editorClass) {
        List<Class<? extends PropertyEditor>> classList;
        if (expliciteEditors != null) {
            classList = expliciteEditors.get(propertyType);
        } else {
            classList = null;
            expliciteEditors = new HashMap<>();
        }
        if (classList == null) {
            classList = new ArrayList<>();
            classList.add(editorClass);
            expliciteEditors.put(propertyType, classList);
        } else if (!classList.contains(editorClass)) {
            classList.add(editorClass);
        }
    }

    private static List<Class<? extends PropertyEditor>> getRegisteredEditorClasses(Class<?> propertyType) {
        List<Class<? extends PropertyEditor>> classList = expliciteEditors != null ? expliciteEditors.get(propertyType) : null;
        return classList != null ? classList : Collections.<Class<? extends PropertyEditor>>emptyList();
    }

    // -------
    private static List<PropertyEditor> findEditors(Class<?> type, boolean all) {
        List<PropertyEditor> editorList = new ArrayList<>(5);

        // 1st - try standard way through PropertyEditorManager
        if (isEditorInCache(type, 0)) {
            createEditorFromCache(type, 0, editorList);
        } else {
            PropertyEditor editor = (type != Object.class) ? PropertyEditorManager.findEditor(type) : null;
            if (editor != null) {
                editorList.add(editor);
            }
            addEditorToCache(type, editor, 0); // also cache nonexistence
        }
        if (!all && !editorList.isEmpty()) {
            return editorList;
        }
        // 2nd - add editors registered using registerEditor(...)
        for (Class<? extends PropertyEditor> cls : getRegisteredEditorClasses(type)) {
            createEditorInstance(cls, editorList);
            if (!all) {
                return editorList;
            }
        }
        // 3rd - add ComponentChooserEditor
        if (editorList.isEmpty() && isComponentType(type)) {
            ComponentChooserEditor chooser = new ComponentChooserEditor(new Class<?>[]{type});
            editorList.add(chooser);
            if (!all) {
                return editorList;
            }
        }
        if (type.isEnum()) {
            editorList.add(createDefaultEnumEditor(type));
        } else if (type.isArray()) {
            editorList.add(new PrimitiveTypeArrayEditor());
        }
        return editorList;
    }

    private static boolean isEditorInCache(Class<?> propertyType, int level) {
        return getEditorClassCache(level).containsKey(propertyType);
        // the cache may also hold the information that there is no property editor for given type
    }

    private static void createEditorFromCache(Class<?> propertyType, int level, List<PropertyEditor> list) {
        Class<PropertyEditor> editorClass = getEditorClassCache(level).get(propertyType);
        if (editorClass != null) {
            createEditorInstance(editorClass, list);
        }
    }

    private static void addEditorToCache(Class<?> propertyType, PropertyEditor editor, int level) {
        if (editor == null) {
            addEditorClassToCache(propertyType, null, level);
        } else {
            // Caching the class for editor instance is a bit tricky - the instance
            // is created by PropertyEditorManager, but we may not be able to re-create
            // it just from a class. We assume it is possible if the class has a no-arg
            // public constructor. Otherwise we don't cache the property editor class.
            Class<PropertyEditor> editorClass = (Class<PropertyEditor>) editor.getClass();
            try {
                Constructor<?> ctor = editorClass.getConstructor();
                if (ctor != null && (ctor.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC) {
                    addEditorClassToCache(propertyType, editorClass, level);
                }
            } catch (NoSuchMethodException ex) {
            } // ignore
        }
    }

    private static void addEditorClassToCache(Class<?> propertyType, Class<PropertyEditor> editorClass, int level) {
        getEditorClassCache(level).put(propertyType, editorClass);
    }

    public static class EditorsClassCacheEntry extends WeakHashMap<Class<?>, Class<PropertyEditor>> {
    }

    private static Map<Class<?>, Class<PropertyEditor>> getEditorClassCache(int level) {
        if (editorClassCache == null) {
            editorClassCache = new EditorsClassCacheEntry[]{new EditorsClassCacheEntry(), new EditorsClassCacheEntry()};
            // the weakness of the map is for classes from user projects
            // (property types which we remember we have no property editor for)
        }
        return editorClassCache[level];
    }

    /**
     * Returns true if given type can be considered as "component type" - i.e.
     * can expect components of this type in the form, so it makes sense to have
     * ComponentChooserEditor as one of the property editors for this type.
     */
    private static boolean isComponentType(Class<?> type) {
        return !type.equals(Object.class) && !type.equals(String.class)
                && !type.isEnum() && !type.isPrimitive()
                && !Number.class.isAssignableFrom(type);
    }

    private static boolean createEditorInstance(Class<? extends PropertyEditor> cls, List<PropertyEditor> list) {
        try {
            list.add(cls.newInstance());
            return true;
        } catch (Exception ex) {
            log(ex, "Error instantiating property editor: " + cls.getName()); // NOI18N
        }
        return false;
    }
    private static final Logger logger = Logger.getLogger(FormPropertyEditorManager.class.getName());

    ;

    private static void log(Throwable ex, String msg) {
        logger.log(Level.INFO, msg, ex);
    }

    static PropertyEditor createDefaultEnumEditor(Class<?> enumClass) {
        try {
            Method method = enumClass.getMethod("values"); // NOI18N
            Enum<? extends Object>[] values = (Enum<? extends Object>[]) method.invoke(null);
            java.util.List<Object> list = new ArrayList<>(3 * values.length);
            for (Enum<? extends Object> value : values) {
                list.add(value.toString());
                list.add(value);
                list.add(enumClass.getName().replace('$', '.') + '.' + value.name());
            }
            // null value is always valid
            list.add(NbBundle.getMessage(FormPropertyEditorManager.class, "CTL_NullText"));
            list.add(null);
            list.add("null"); // NOI18N

            return new EnumEditor(list.toArray());
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
        }
        return null;
    }

    protected static PropertyEditor checkEnumWrapper(RADProperty<?> aProp) {
        RADComponent<?> component = aProp.getComponent();
        PropertyDescriptor descriptor = aProp.getPropertyDescriptor();
        if ("defaultCloseOperation".equals(descriptor.getName()) && (javax.swing.JDialog.class.isAssignableFrom(component.getBeanClass())
                || javax.swing.JInternalFrame.class.isAssignableFrom(component.getBeanClass()))) {
            // hack: enumeration definition is missing in standard Swing
            // for JDialog and JInternalFrame defaultCloseOperation property
            Object[] enumerationValues = new Object[]{
                "DISPOSE_ON_CLOSE", WindowConstants.DISPOSE_ON_CLOSE, "WindowConstants.DISPOSE_ON_CLOSE", 
                "DO_NOTHING_ON_CLOSE", WindowConstants.DO_NOTHING_ON_CLOSE, "WindowConstants.DO_NOTHING_ON_CLOSE", // NOI18N
                "HIDE_ON_CLOSE", WindowConstants.HIDE_ON_CLOSE, "WindowConstants.HIDE_ON_CLOSE"}; // NOI18N
            return new EnumEditor(enumerationValues, true);
        } else if ("mnemonic".equals(descriptor.getName())) {
            return new MnemonicEditor();
        } else {
            Object[] enumerationValues = (Object[]) descriptor.getValue(EnumEditor.ENUMERATION_VALUES_KEY);
            if (enumerationValues != null) {
                return new EnumEditor(enumerationValues);
            }
        }
        return null;
    }
}
