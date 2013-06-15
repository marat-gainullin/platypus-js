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
package com.bearsoft.org.netbeans.modules.form.actions.menu;

import com.bearsoft.org.netbeans.modules.form.*;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.MarginLayoutOperations;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.MarginLayoutSupport;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import org.openide.ErrorManager;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.*;

/**
 * Action class providing popup menu presenter for setanchoring submenu.
 *
 * @author Martin Grebac
 */
public class SetAnchoringAction extends NodeAction {

    private JCheckBoxMenuItem[] items;

    @Override
    protected boolean enable(Node[] nodes) {
        List<RADVisualComponent<?>> comps = FormUtils.getSelectedLayoutComponents(nodes);
        return comps != null && comps.size() > 0;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(SetAnchoringAction.class, "ACT_SetAnchoring"); // NOI18N
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected void performAction(Node[] activatedNodes) {
    }

    @Override
    public JMenuItem getMenuPresenter() {
        return getPopupPresenter();
    }

    /**
     * Returns a JMenuItem that presents this action in a Popup Menu.
     *
     * @return the JMenuItem representation for the action
     */
    @Override
    public JMenuItem getPopupPresenter() {
        JMenu popupMenu = new JMenu(
                NbBundle.getMessage(SetAnchoringAction.class, "ACT_SetAnchoring")); // NOI18N
        //для MarginLayout
        Node[] nodes = getActivatedNodes();
        List<RADVisualComponent<?>> components = FormUtils.getSelectedLayoutComponents(nodes);
        if (components != null && components.size() > 0) {
            RADComponent<?> radComp = components.get(0);
            if (radComp != null) {
                if ((radComp instanceof RADVisualComponent<?>)) {
                    RADVisualComponent<?> visComp = (RADVisualComponent<?>) radComp;
                    RADVisualContainer<?> visCont = visComp.getParentComponent();
                    if ((visCont.getLayoutSupport() != null && visCont.getLayoutSupport().getLayoutDelegate() instanceof MarginLayoutSupport)) {
                        popupMenu = new JMenu(NbBundle.getMessage(SetAnchoringAction.class, "ACT_SetAnchoringMargin")); // NOI18N
                    }
                }
            }
        }
        popupMenu.setEnabled(isEnabled());
        HelpCtx.setHelpIDString(popupMenu, SetAnchoringAction.class.getName());

        popupMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                JMenu menu = (JMenu) e.getSource();
                createAnchoringSubmenu(menu);
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            }

            @Override
            public void menuCanceled(MenuEvent e) {
            }
        });
        return popupMenu;
    }

    private void createAnchoringSubmenu(JMenu menu) {
        Node[] nodes = getActivatedNodes();
        List<RADVisualComponent<?>> components = FormUtils.getSelectedLayoutComponents(nodes);
        if (components != null && !components.isEmpty()) {
            if (!(menu.getMenuComponentCount() > 0)) {
                ResourceBundle bundle = NbBundle.getBundle(SetAnchoringAction.class);

                JCheckBoxMenuItem leftItem = new AnchoringMenuItem(
                        bundle.getString("CTL_AnchorLeft"), // NOI18N
                        components,
                        0);
                JCheckBoxMenuItem topItem = new AnchoringMenuItem(
                        bundle.getString("CTL_AnchorTop"), // NOI18N
                        components,
                        2);
                JCheckBoxMenuItem bottomItem = new AnchoringMenuItem(
                        bundle.getString("CTL_AnchorBottom"), // NOI18N
                        components,
                        3);
                JCheckBoxMenuItem rightItem = new AnchoringMenuItem(
                        bundle.getString("CTL_AnchorRight"), // NOI18N
                        components,
                        1);
                items = new JCheckBoxMenuItem[]{leftItem, rightItem, topItem, bottomItem};
                for (int i = 0; i < 4; i++) {
                    items[i].addActionListener(getMenuItemListener());
                    HelpCtx.setHelpIDString(items[i], SetAnchoringAction.class.getName());
                    menu.add(items[i]);
                }
            }
            updateState(components);
        }
    }

    private void updateState(List<RADVisualComponent<?>> components) {
        /*
        if (components != null && !components.isEmpty()) {
            PlatypusFormLayoutView formDesigner = FormEditor.getFormDesigner(components.get(0).getFormModel());
            formDesigner.updateAnchorActions();
            for (int i = 0; i < 4; i++) {
                items[i].setEnabled(formDesigner.getAnchorButtons()[i].isEnabled());
                items[i].setSelected(formDesigner.getAnchorButtons()[i].isSelected());
            }
        }
        */ 
    }

    private ActionListener getMenuItemListener() {
        if (menuItemListener == null) {
            menuItemListener = new AnchoringMenuItemListener();
        }
        return menuItemListener;
    }

    // --------
    private static class AnchoringMenuItem extends JCheckBoxMenuItem {

        private int direction;
        private List<RADVisualComponent<?>> components;

        AnchoringMenuItem(String text, List<RADVisualComponent<?>> components, int direction) {
            super(text);
            this.components = components;
            this.direction = direction;
        }

        int getDirection() {
            return direction;
        }

        List<RADVisualComponent<?>> getRADComponents() {
            return components;
        }
    }

    private static class AnchoringMenuItemListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent evt) {
            Object source = evt.getSource();
            if (source instanceof AnchoringMenuItem) {
                AnchoringMenuItem mi = (AnchoringMenuItem) source;
                if (mi.isEnabled()) {
                    List<RADVisualComponent<?>> comps = mi.components;
                    if (comps != null && comps.size() > 0) {
                        MarginLayoutOperations mAlign = new MarginLayoutOperations(comps);
                        int index = mi.getDirection();
                        try {
                            if (!mi.isSelected()) {//опция сброшена значит сбрасываем
                                if (index == 0) {
                                    mAlign.clearAllLeft();
                                } else if (index == 1) {
                                    mAlign.clearAllRight();
                                } else if (index == 2) {
                                    mAlign.clearAllTop();
                                } else if (index == 3) {
                                    mAlign.clearAllBottom();
                                }
                            } else {//опция установлена значит устанавливаем
                                if (index == 0) {
                                    mAlign.setAllLeft();
                                } else if (index == 1) {
                                    mAlign.setAllRight();
                                } else if (index == 2) {
                                    mAlign.setAllTop();
                                } else if (index == 3) {
                                    mAlign.setAllBottom();
                                }
                            }
                        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                            ErrorManager.getDefault().notify(ex);
                        }
                    }
                }
            }
        }
    }
    private ActionListener menuItemListener;
}
