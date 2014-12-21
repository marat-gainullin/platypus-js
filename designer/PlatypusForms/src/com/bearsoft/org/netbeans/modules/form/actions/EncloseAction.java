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
package com.bearsoft.org.netbeans.modules.form.actions;

import com.bearsoft.org.netbeans.modules.form.*;
import com.bearsoft.org.netbeans.modules.form.palette.PaletteItem;
import com.bearsoft.org.netbeans.modules.form.palette.PaletteUtils;
import java.awt.EventQueue;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 * Action that encloses selected components in a given container.
 *
 * @author Tomas Pavek, Jan Stola
 */
public class EncloseAction extends NodeAction {

    @Override
    public String getName() {
        return NbBundle.getMessage(EncloseAction.class, "ACT_EncloseInContainer"); // NOI18N
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean enable(Node[] nodes) {
        List<RADVisualComponent<?>> comps = getComponents(nodes);
        return ((comps != null) && getContainer(comps) != null);
    }

    @Override
    protected void performAction(Node[] nodes) {
    }

    @Override
    public JMenuItem getMenuPresenter() {
        return getPopupPresenter();
    }

    @Override
    public JMenuItem getPopupPresenter() {
        JMenu menu = new ContainersMenu(getName(), getComponents(getActivatedNodes()));
        menu.setEnabled(isEnabled());
        HelpCtx.setHelpIDString(menu, EncloseAction.class.getName());
        return menu;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }

    private static List<RADVisualComponent<?>> getComponents(Node[] nodes) {
        return FormUtils.getSelectedLayoutComponents(nodes);
    }

    private static RADVisualContainer<?> getContainer(List<RADVisualComponent<?>> components) {
        RADVisualContainer<?> commonParent = null;
        for (RADVisualComponent<?> comp : components) {
            RADVisualContainer<?> parent = comp.getParentComponent();
            if (parent == null || (commonParent != null && parent != commonParent)) {
                return null;
            }
            if (commonParent == null) {
                commonParent = parent;
            }
        }
        return commonParent;
    }

    private static PaletteItem[] getAllContainers() {
        List<PaletteItem> list = new ArrayList<>();
        for (PaletteItem item : PaletteUtils.getAllItems()) {
            Class<?> cls = item.getComponentClass();
            if (cls != null
                    && JComponent.class.isAssignableFrom(cls)
                    && !MenuElement.class.isAssignableFrom(cls)
                    && FormUtils.isContainer(cls)) {
                list.add(item);
            }
        }
        return list.toArray(new PaletteItem[list.size()]);
    }

    private static class ContainersMenu extends JMenu {

        private boolean initialized = false;
        private List<RADVisualComponent<?>> components;

        private ContainersMenu(String name, List<RADVisualComponent<?>> aComponents) {
            super(name);
            components = aComponents;
        }

        @Override
        public JPopupMenu getPopupMenu() {
            final JPopupMenu popup = super.getPopupMenu();
            if (!initialized) {
                popup.removeAll();
                String waitTxt = NbBundle.getMessage(EncloseAction.class, "MSG_EncloseInPleaseWait"); // NOI18N
                JMenuItem waitItem = new JMenuItem(waitTxt);
                waitItem.setEnabled(false);
                popup.add(waitItem);
                // Find the containers outside EQ, see issue 123794
                FormUtils.getRequestProcessor().post(new Runnable() {
                    @Override
                    public void run() {
                        final PaletteItem[] items = getAllContainers();
                        EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                popup.removeAll();
                                for (PaletteItem item : items) {
                                    JMenuItem mi = new JMenuItem(item.getNode().getDisplayName());
                                    HelpCtx.setHelpIDString(mi, EncloseAction.class.getName());
                                    addSortedMenuItem(popup, mi);
                                    mi.addActionListener(new EncloseActionListener(item));
                                }
                                popup.pack();
                            }
                        });
                    }
                });
                initialized = true;
            }
            return popup;
        }

        private static void addSortedMenuItem(JPopupMenu menu, JMenuItem menuItem) {
            String text = menuItem.getText();
            for (int i = 0; i < menu.getComponentCount(); i++) {
                if (menu.getComponent(i) instanceof JMenuItem) {
                    String tx = ((JMenuItem) menu.getComponent(i)).getText();
                    if (text.compareTo(tx) < 0) {
                        menu.add(menuItem, i);
                        return;
                    }
                }
            }
            menu.add(menuItem);
        }

        private class EncloseActionListener implements ActionListener {

            private PaletteItem paletteItem;

            EncloseActionListener(PaletteItem paletteItem) {
                this.paletteItem = paletteItem;
            }

            @Override
            public void actionPerformed(ActionEvent evt) {
                RADVisualContainer<?> radCont = getContainer(components);
                if (radCont != null) {
                    FormModel formModel = radCont.getFormModel();
                    RADComponentCreator creator = formModel.getComponentCreator();
                    boolean autoUndo = true; // in case of unexpected error, for robustness
                    try {
                        // create and add the new container
                        RADComponent<?> newComp = creator.createComponent(paletteItem.getComponentClassSource(), radCont, radCont.getLayoutSupport().getConstraints(components.get(0)));
                        boolean success = (newComp instanceof RADVisualContainer<?>);
                        if (!success) {
                            String msg = NbBundle.getMessage(EncloseAction.class, "MSG_EncloseInNotEmpty"); // NOI18N
                            DialogDisplayer.getDefault().notifyLater(new NotifyDescriptor.Message(msg));
                        } else {
                            RADVisualContainer<?> newCont = (RADVisualContainer<?>) newComp;
                            for (RADComponent<?> radComp : components) {
                                formModel.removeComponent(radComp, false);
                            }
                            success = creator.addComponents(components, newCont); // this does not affect layout model
                            if (!success) {
                                String msg = NbBundle.getMessage(EncloseAction.class, "MSG_EncloseInFailed"); // NOI18N
                                DialogDisplayer.getDefault().notifyLater(new NotifyDescriptor.Message(msg));
                            }
                        }
                        autoUndo = !success;
                    } catch (Exception ex) {
                        ErrorManager.getDefault().notify(ex);
                    } finally {
                        if (autoUndo) {
                            formModel.forceUndoOfCompoundEdit();
                        }
                    }
                }
            }
        }
    }
}
