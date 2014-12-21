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
package com.bearsoft.org.netbeans.modules.form.palette;

import com.bearsoft.org.netbeans.modules.form.FormUtils;
import java.awt.Image;
import java.beans.*;
import org.openide.ErrorManager;
import org.openide.nodes.Node;

/**
 * PaletteItem holds important information about one component (item) in the
 * palette.
 *
 * @author Tomas Pavek
 */
public final class PaletteItem implements Node.Cookie {

    private PaletteItemDataObject itemDataObject;
    // raw data (as read from the item file - to be resolved lazily)
    String componentClassSource;
    String componentType_explicit;
    Image icon;
    // resolved data (derived from the raw data)
    private Class<?> componentClass;
    private Throwable lastError; // error occurred when loading component class
    private int componentType = -1;
    // type of component constants
    private static final int LAYOUT = 1;
    private static final int VISUAL = 4; // bit flag
    private static final int MENU = 8; // bit flag
    private static final int TYPE_MASK = 15;

    // -------
    PaletteItem(PaletteItemDataObject dobj) {
        itemDataObject = dobj;
    }

    public PaletteItem(String aComponentClassSource, Class<?> aComponentClass) {
        super();
        componentClassSource = aComponentClassSource;
        componentClass = aComponentClass;
    }

    public void setComponentClassSource(String cs) {
        componentClass = null;
        lastError = null;
        componentType = -1;
        componentClassSource = cs;
    }

    void setComponentExplicitType(String type) {
        componentType_explicit = type;
    }

    /**
     * @return a node visually representing this palette item
     */
    public Node getNode() {
        return ((itemDataObject == null) || !itemDataObject.isValid()) ? null : itemDataObject.getNodeDelegate();
    }

    /**
     * @return a String identifying this palette item
     */
    public String getId() {
        return getComponentClassName();
    }

    public String getComponentClassName() {
        return componentClassSource;//.getClassName();
    }

    public String getComponentClassSource() {
        return componentClassSource;
    }

    /**
     * @return the class of the component represented by this pallete item. May
     * return null - if class loading fails.
     */
    public Class<?> getComponentClass() {
        if (componentClass == null && lastError == null) {
            componentClass = loadComponentClass();
        }
        return componentClass;
    }

    /**
     * @return the exception occurred when trying to resolve the component class
     * of this pallette item
     */
    public Throwable getError() {
        return lastError;
    }

    /**
     * @return type of the component as String, e.g. "visual", "menu", "layout",
     * border
     */
    public String getExplicitComponentType() {
        return componentType_explicit;
    }

    /**
     * @return whether the component of this palette item is a visual component
     * (java.awt.Component subclass)
     */
    public boolean isVisual() {
        if (componentType == -1) {
            resolveComponentType();
        }
        return (componentType & VISUAL) != 0;
    }

    /**
     * @return whether the component of this palette item is a menu component
     */
    public boolean isMenu() {
        if (componentType == -1) {
            resolveComponentType();
        }
        return (componentType & MENU) != 0;
    }

    /**
     * @return whether the component of this palette item is a layout manager
     * (java.awt.LayoutManager implementation)
     */
    public boolean isLayout() {
        if (componentType == -1) {
            resolveComponentType();
        }
        return (componentType & TYPE_MASK) == LAYOUT;
    }

    @Override
    public String toString() {
        return PaletteUtils.getItemComponentDescription(this);
    }

    String getDisplayName() {
        BeanDescriptor bd = getBeanDescriptor();
        return bd != null ? bd.getDisplayName() : null;
    }

    String getTooltip() {
        BeanDescriptor bd = getBeanDescriptor();
        return bd != null ? bd.getShortDescription() : null;
    }

    public Image getIcon(int type) {
        if (icon != null) {
            return icon;
        }
        BeanInfo bi = getBeanInfo();
        return bi != null ? bi.getIcon(type) : null;
    }

    public void setIcon(Image icon) {
        this.icon = icon;
    }

    void reset() {
        componentClass = null;
        lastError = null;
        componentType = -1;

        itemDataObject.displayName = null;
        itemDataObject.tooltip = null;
        itemDataObject.icon16 = null;
        itemDataObject.icon32 = null;
    }

    // -------
    private Class<?> loadComponentClass() {
        try {
            return FormUtils.loadSystemClass(getComponentClassSource());
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
            lastError = ex;
        } catch (LinkageError ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
            lastError = ex;
        }
        return null;
    }

    private BeanInfo getBeanInfo() {
        Class<?> compClass = getComponentClass();
        if (compClass != null) {
            try {
                return FormUtils.getBeanInfo(compClass);
            } catch (Exception ex) {
            } // ignore failure
            //catch (LinkageError ex) {}
            catch (Error er) {
            } // Issue 74002
        }
        return null;
    }

    private BeanDescriptor getBeanDescriptor() {
        Class<?> compClass = getComponentClass();
        if (compClass != null) {
            try {
                return FormUtils.getBeanInfo(compClass).getBeanDescriptor();
            } catch (Throwable ex) {
                // Issue 74002
            } // ignore failure
        }
        return null;
    }

    private void resolveComponentType() {
        if (componentType_explicit == null) {
            componentType = 0;

            Class<?> compClass = getComponentClass();
            if (compClass == null) {
                return;
            }
            if (java.awt.LayoutManager.class.isAssignableFrom(compClass)) {
                // PENDING LayoutSupportDelegate - should have special entry in pallette item file?
                componentType = LAYOUT;
                return;
            }
            if (java.awt.Component.class.isAssignableFrom(compClass)) {
                componentType |= VISUAL;
            }
            if (java.awt.MenuComponent.class.isAssignableFrom(compClass)
                    || javax.swing.JMenuItem.class.isAssignableFrom(compClass)
                    || javax.swing.JMenuBar.class.isAssignableFrom(compClass)
                    || javax.swing.JPopupMenu.class.isAssignableFrom(compClass)) {
                componentType |= MENU;
            }
        } else if ("visual".equalsIgnoreCase(componentType_explicit)) {
            componentType = VISUAL;
        } else if ("layout".equalsIgnoreCase(componentType_explicit)) {
            componentType = LAYOUT;
        } else if ("menu".equalsIgnoreCase(componentType_explicit)) {
            componentType = MENU | VISUAL;
        } else {
            componentType = 0;
        }
    }
}
