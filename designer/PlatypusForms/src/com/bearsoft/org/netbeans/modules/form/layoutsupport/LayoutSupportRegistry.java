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
package com.bearsoft.org.netbeans.modules.form.layoutsupport;

import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.BorderLayoutSupport;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.BoxLayoutSupport;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.CardLayoutSupport;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.FlowLayoutSupport;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.GridLayoutSupport;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.MarginLayoutSupport;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.MenuFakeSupport;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.ScrollPaneSupport;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.SplitPaneSupport;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.TabbedPaneSupport;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.ToolBarSupport;
import com.eas.client.forms.containers.ScrollPane;
import com.eas.client.forms.containers.SplitPane;
import com.eas.client.forms.containers.TabbedPane;
import com.eas.client.forms.containers.ToolBar;
import com.eas.client.forms.layouts.BoxLayout;
import com.eas.client.forms.layouts.CardLayout;
import com.eas.client.forms.layouts.MarginLayout;
import com.eas.client.forms.menu.Menu;
import com.eas.client.forms.menu.MenuBar;
import com.eas.client.forms.menu.PopupMenu;
import java.util.*;

/**
 * Registry and factory class for LayoutSupportDelegate implementations.
 *
 * @author Tomas Pavek
 */
public class LayoutSupportRegistry {

    private static final Map<Class<?>, Class<?>> containerToLayoutDelegate = new HashMap<>();
    private static final Map<Class<?>, Class<?>> layoutToLayoutDelegate = new HashMap<>();
    public static final String DEFAULT_SUPPORT = "<default>"; // NOI18N

    static {
        // fill in default containers
        containerToLayoutDelegate.put(
                ScrollPane.class, // NOI18N
                ScrollPaneSupport.class); // NOI18N
        containerToLayoutDelegate.put(
                SplitPane.class, // NOI18N
                SplitPaneSupport.class); // NOI18N
        containerToLayoutDelegate.put(
                TabbedPane.class, // NOI18N
                TabbedPaneSupport.class); // NOI18N
        containerToLayoutDelegate.put(
                ToolBar.class, // NOI18N
                ToolBarSupport.class); // NOI18N
        containerToLayoutDelegate.put(
                MenuBar.class, // NOI18N
                MenuFakeSupport.class); // NOI18N
        containerToLayoutDelegate.put(
                Menu.class, // NOI18N
                MenuFakeSupport.class); // NOI18N
        containerToLayoutDelegate.put(
                PopupMenu.class, // NOI18N
                MenuFakeSupport.class); // NOI18N
    }

    static {
        // fill in default layouts
        layoutToLayoutDelegate.put(
                java.awt.BorderLayout.class, // NOI18N
                BorderLayoutSupport.class); // NOI18N
        layoutToLayoutDelegate.put(
                java.awt.FlowLayout.class, // NOI18N
                FlowLayoutSupport.class); // NOI18N
        layoutToLayoutDelegate.put(
                BoxLayout.class, // NOI18N
                BoxLayoutSupport.class); // NOI18N
        layoutToLayoutDelegate.put(
                java.awt.GridLayout.class, // NOI18N
                GridLayoutSupport.class); // NOI18N
        layoutToLayoutDelegate.put(
                CardLayout.class, // NOI18N
                CardLayoutSupport.class); // NOI18N
        layoutToLayoutDelegate.put(
                MarginLayout.class, // NOI18N
                MarginLayoutSupport.class); // NOI18N
    }

    // --------------
    // get methods
    public static Class<?> getSupportClassForContainer(Class<?> containerClass) {
        return containerToLayoutDelegate.get(containerClass);
    }

    public Class<?> getSupportClassForLayout(Class<?> layoutClass) {
        return layoutToLayoutDelegate.get(layoutClass);
    }

    // ------------
    // creation methods
    public LayoutSupportDelegate createSupportForContainer(Class<?> containerClass)
            throws ClassNotFoundException,
            InstantiationException,
            IllegalAccessException {
        Class<?> delegateClass = getSupportClassForContainer(containerClass);
        if (delegateClass == null) {
            return null;
        }
        return (LayoutSupportDelegate) delegateClass.newInstance();
    }

    public static LayoutSupportDelegate createSupportForLayout(Class<?> layoutClass)
            throws ClassNotFoundException,
            InstantiationException,
            IllegalAccessException {
        Class<?> supportClass = layoutToLayoutDelegate.get(layoutClass);
        return (LayoutSupportDelegate) supportClass.newInstance();
    }
}
