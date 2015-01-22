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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

import com.eas.client.forms.layouts.MarginLayout;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import javax.swing.*;
import org.openide.ErrorManager;

/**
 * Factory class for creating objects and some utility methods.
 *
 * @author Tomas Pavek
 */
public class CreationFactory {

    interface PropertyParameters<T> {

        public String getPropertyName();

        public String getJavaParametersString(FormProperty<? extends T> prop);

        public Object[] getPropertyParametersValues(FormProperty<? extends T> prop);

        public Class<?>[] getPropertyParametersTypes();
    }

    static class Property2ParametersMapper {

        private final String propertyName;
        private final Class<?>[] propertyType = new Class<?>[1];
        private PropertyParameters<Object> parameters;

        Property2ParametersMapper(Class<?> propertyClass, String propertyName) {
            this.propertyType[0] = propertyClass;
            this.propertyName = propertyName;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public Class<?>[] getPropertyTypes() {
            if (parameters != null) {
                return parameters.getPropertyParametersTypes();
            }
            return propertyType;
        }

        public String getJavaParametersString(FormProperty<? extends Object> prop) {
            if (parameters != null) {
                return parameters.getJavaParametersString(prop);
            }
            return prop.getJavaInitializationString();
        }

        public Object[] getPropertyParametersValues(FormProperty<? extends Object> prop) {
            if (parameters != null) {
                return parameters.getPropertyParametersValues(prop);
            }
            try {
                return new Object[]{prop.getValue()};
            } catch (InvocationTargetException | IllegalAccessException ite) {
                ErrorManager.getDefault().notify(ite);
            }
            return new Object[]{};
        }

        public void setPropertyParameters(PropertyParameters<Object> aParameters) {
            parameters = aParameters;
        }
    }

    private CreationFactory() {
    }

    // creation methods
    public static <C> C createDefaultInstance(final Class<?> cls) throws Exception {
        Object instance = cls.newInstance();
        initAfterCreation(instance);
        return (C) instance;
    }

    public static <C> C createInstance(Class<? extends C> cls)
            throws Exception {
        C instance = cls.newInstance();
        initAfterCreation(instance);
        return instance;
    }

    // additional initializations for some components - in fact hacks required
    // by using fake peers and remapping L&F...
    private static void initAfterCreation(Object instance) {
        if (instance instanceof javax.swing.border.TitledBorder) {
            ((javax.swing.border.TitledBorder) instance).setTitleFont(UIManager.getFont("TitledBorder.createDefaultInstancefont")); // NOI18N
        } else if (instance instanceof java.awt.Component
                && !(instance instanceof javax.swing.JComponent)
                && !(instance instanceof javax.swing.RootPaneContainer)) {
            ((Component) instance).setName(null);
            ((Component) instance).setFont(FormUtils.getDefaultAWTFont());
        } else if (instance.getClass() == JPanel.class) {
            ((JPanel) instance).setLayout(new MarginLayout());
        } else if (instance instanceof JComponent) {
            // smae alignment options are selected at runtime and used by all boxes
            ((JComponent) instance).setAlignmentX(1.0f);
            ((JComponent) instance).setAlignmentY(1.0f);
        }
    }

    static class InsetsPropertyParameters implements PropertyParameters<Insets> {

        private static Class<?>[] parameterTypes = new Class<?>[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE};
        private final String propertyName;

        public InsetsPropertyParameters(String aPropertyName) {
            propertyName = aPropertyName;
        }

        @Override
        public String getPropertyName() {
            return propertyName;
        }

        @Override
        public String getJavaParametersString(FormProperty<? extends Insets> prop) {
            try {
                Insets insets = prop.getValue();
                if (insets != null) {
                    return insets.top + ", " + insets.left + ", " + insets.bottom + ", " + insets.right;
                } else {
                    return "";
                }
            } catch (IllegalAccessException | InvocationTargetException ex) {
                return ex.getMessage();
            }
        }

        @Override
        public Object[] getPropertyParametersValues(FormProperty<? extends Insets> prop) {
            try {
                Insets insets = prop.getValue();
                if (insets != null) {
                    return new Object[]{new Integer(insets.top), new Integer(insets.left), new Integer(insets.bottom), new Integer(insets.right)};
                } else {
                    return new Object[]{};
                }
            } catch (IllegalAccessException | InvocationTargetException ex) {
                return new Object[]{ex.getMessage()};
            }
        }

        @Override
        public Class<?>[] getPropertyParametersTypes() {
            return parameterTypes;
        }
    }
}
