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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package com.bearsoft.org.netbeans.modules.form.menu;

import com.bearsoft.org.netbeans.modules.form.FormUtils;
import com.bearsoft.org.netbeans.modules.form.RADComponent;
import com.bearsoft.org.netbeans.modules.form.RADComponentCookie;
import com.bearsoft.org.netbeans.modules.form.palette.PaletteItem;
import com.bearsoft.org.netbeans.modules.form.palette.PaletteUtils;
import java.awt.EventQueue;
import javax.swing.JMenu;
import org.openide.ErrorManager;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 * An action which adds a JMenu to a JMenuBar
 *
 * @author Joshua Marinacci
 */
public class InsertMenuAction extends NodeAction {

    private static String name;

    @Override
    public String getName() {
        if (name == null) {
            name = NbBundle.getMessage(InsertMenuAction.class, "ACT_InsertMenu"); // NOI18N/
        }
        return name;
    }

    @Override
    protected void performAction(Node[] activatedNodes) {
        if (activatedNodes != null && activatedNodes.length == 1) {
            RADComponentCookie radCookie = activatedNodes[0].getLookup().lookup(RADComponentCookie.class);
            final RADComponent<?> radComp = radCookie == null ? null
                    : radCookie.getRADComponent();

            //find the first JMenuBar item in the palette
            PaletteItem[] items = PaletteUtils.getAllItems();
            for (PaletteItem item : items) {
                Class<?> clazz = item.getComponentClass();
                if (clazz != null && JMenu.class.isAssignableFrom(clazz)) {
                    final PaletteItem it = item;
                    EventQueue.invokeLater(() -> {
                        try {
                            FormUtils.addComponentToEndOfContainer(radComp, it);
                        } catch (Exception ex) {
                            ErrorManager.getDefault().notify(ex);
                        }
                    });
                    return;
                }
            }
        }
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        return true;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx("gui.containers.designing"); // NOI18N
    }
}
