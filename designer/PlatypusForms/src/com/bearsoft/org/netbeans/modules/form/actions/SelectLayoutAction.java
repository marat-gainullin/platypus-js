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
package com.bearsoft.org.netbeans.modules.form.actions;

import com.bearsoft.org.netbeans.modules.form.*;
import com.bearsoft.org.netbeans.modules.form.palette.PaletteItem;
import com.bearsoft.org.netbeans.modules.form.palette.PaletteUtils;
import com.eas.client.forms.layouts.MarginLayout;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.BeanInfo;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

/**
 * Action for setting layout on selected container(s). Presented only in
 * contextual menus within the Form Editor.
 */
public class SelectLayoutAction extends CallableSystemAction {

    private static String name;

    /**
     * Human presentable name of the action. This should be presented as an item
     * in a menu.
     *
     * @return the name of the action
     */
    @Override
    public String getName() {
        if (name == null) {
            name = NbBundle.getMessage(SelectLayoutAction.class, "ACT_SelectLayout"); // NOI18N
        }
        return name;
    }

    /**
     * Help context where to find more about the action.
     *
     * @return the help context for this action
     */
    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public boolean isEnabled() {
        Node[] nodes = getNodes();
        for (int i = 0; i < nodes.length; i++) {
            RADVisualContainer<?> container = getContainer(nodes[i]);
            if (container == null || container.hasDedicatedLayoutSupport()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public JMenuItem getMenuPresenter() {
        return getPopupPresenter();
    }

    @Override
    public JMenuItem getPopupPresenter() {
        JMenu layoutMenu = new LayoutMenu(getName());
        layoutMenu.setEnabled(isEnabled());
        HelpCtx.setHelpIDString(layoutMenu, SelectLayoutAction.class.getName());
        return layoutMenu;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }

    @Override
    public void performAction() {
    }

    // -------
    private static Node[] getNodes() {
        // using NodeAction and global activated nodes is not reliable
        // (activated nodes are set with a delay after selection in
        // FormInspector)
        return FormInspector.getInstance().getExplorerManager().getSelectedNodes();
    }

    private static RADVisualContainer<?> getContainer(Node node) {
        RADComponentCookie radCookie = node.getLookup().lookup(RADComponentCookie.class);
        if (radCookie != null) {
            RADComponent<?> radComp = radCookie.getRADComponent();
            if (radComp instanceof RADVisualContainer<?>) {
                return (RADVisualContainer<?>) radComp;
            }
        }
        return null;
    }

    private static PaletteItem[] getAllLayouts() {
        PaletteItem[] allItems = PaletteUtils.getAllItems();
        java.util.List<PaletteItem> layoutsList = new ArrayList<>();
        for (int i = 0; i < allItems.length; i++) {
            if (allItems[i].isLayout()) {
                layoutsList.add(allItems[i]);
            }
        }
        return layoutsList.toArray(new PaletteItem[]{});
    }

    private static class LayoutMenu extends JMenu {

        private boolean initialized = false;

        private LayoutMenu(String name) {
            super(name);
        }

        //Заполнение меню выбора LayoutManager'a
        @Override
        public JPopupMenu getPopupMenu() {
            JPopupMenu popup = super.getPopupMenu();
            Node[] nodes = getNodes();

            if (nodes.length != 0 && !initialized) {
                popup.removeAll();
                RADVisualContainer<?> container = getContainer(nodes[0]);
                //Назначение Margin Layout-а
                PaletteItem[] layouts = getAllLayouts();
                for (int i = 0; i < layouts.length; i++) {
                    if (MarginLayout.class.getName().equals(layouts[i].getComponentClassSource())) {
                        JMenuItem mi = new JMenuItem(layouts[i].getNode().getDisplayName());
                        mi.setIcon(new ImageIcon(layouts[i].getNode().getIcon(BeanInfo.ICON_COLOR_16x16)));
                        HelpCtx.setHelpIDString(mi, SelectLayoutAction.class.getName());
                        popup.add(mi);
                        mi.addActionListener(new LayoutActionListener(layouts[i]));
                        if (isContainersLayout(container, layouts[i])) {
                            setBoldFontForMenuText(mi);
                        }
                        popup.addSeparator();
                    }
                }
                //Назначение остальных Layout'ов
                for (int i = 0; i < layouts.length; i++) {
                    if (!GridBagLayout.class.getName().equals(layouts[i].getComponentClassSource())
                            && !MarginLayout.class.getName().equals(layouts[i].getComponentClassSource())) {
                        JMenuItem mi = new JMenuItem(layouts[i].getNode().getDisplayName());
                        mi.setIcon(new ImageIcon(layouts[i].getNode().getIcon(BeanInfo.ICON_COLOR_16x16)));
                        HelpCtx.setHelpIDString(mi, SelectLayoutAction.class.getName());
                        popup.add(mi);
                        mi.addActionListener(new LayoutActionListener(layouts[i]));
                        if (isContainersLayout(container, layouts[i])) {
                            setBoldFontForMenuText(mi);
                        }
                    }
                }
                initialized = true;
            }
            return popup;
        }

        private boolean isContainersLayout(RADVisualContainer<?> container, PaletteItem layout) {
            return container != null && container.getLayoutSupport() != null
                    && container.getLayoutSupport().getLayoutDelegate().getSupportedClass() == layout.getComponentClass();
        }

        private static void setBoldFontForMenuText(JMenuItem mi) {
            java.awt.Font font = mi.getFont();
            mi.setFont(font.deriveFont(font.getStyle() | java.awt.Font.BOLD));
        }
    }

    private static class LayoutActionListener implements ActionListener {

        private PaletteItem paletteItem;

        LayoutActionListener(PaletteItem aPaletteItem) {
            paletteItem = aPaletteItem;
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            Node[] nodes = getNodes();
            for (int i = 0; i < nodes.length; i++) {
                RADVisualContainer<?> container = getContainer(nodes[i]);
                if (container != null && paletteItem != null) {
                    // set the selected layout on the container
                    container.getFormModel().getComponentCreator().createComponent(
                            paletteItem.getComponentClassSource(), container, null);
                    container.getNodeReference().updateName();
                }
            }
        }
    }
}
