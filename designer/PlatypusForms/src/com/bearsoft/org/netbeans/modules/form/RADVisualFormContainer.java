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

import java.awt.Container;
import java.awt.Dimension;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * RADVisualFormContainer represents the top-level container of the form and the
 * form itself during design time.
 *
 * @author Ian Formanek
 */
public class RADVisualFormContainer extends RADVisualContainer<Container> {

    protected static Set<String> hiddenProps = new HashSet<>(Arrays.asList(new String[]{
        "visible",
        "left",
        "top"}));

    public RADVisualFormContainer() {
        super();
    }

    @Override
    protected RADProperty<?> createBeanProperty(PropertyDescriptor desc) {
        if (!hiddenProps.contains(desc.getName())) {
            return super.createBeanProperty(desc);
        } else {
            return null;
        }
    }

    // ---------
    // providing the difference of the whole frame/dialog size and the size
    // of the content pane
    private static Dimension windowContentDimensionDiff;

    public Dimension getWindowContentDimensionDiff() {
        boolean undecorated = true;
        Object beanInstance = getBeanInstance();
        if (beanInstance instanceof java.awt.Frame) {
            undecorated = ((java.awt.Frame) beanInstance).isUndecorated();
        } else if (beanInstance instanceof java.awt.Dialog) {
            undecorated = ((java.awt.Dialog) beanInstance).isUndecorated();
        }
        return undecorated ? new Dimension(0, 0) : getDecoratedWindowContentDimensionDiff();
    }

    public static Dimension getDecoratedWindowContentDimensionDiff() {
        if (windowContentDimensionDiff == null) {
            javax.swing.JFrame frame = new javax.swing.JFrame();
            frame.pack();
            Dimension d1 = frame.getSize();
            Dimension d2 = frame.getRootPane().getSize();
            windowContentDimensionDiff
                    = new Dimension(d1.width - d2.width, d1.height - d2.height);
        }
        return windowContentDimensionDiff;
    }
}
