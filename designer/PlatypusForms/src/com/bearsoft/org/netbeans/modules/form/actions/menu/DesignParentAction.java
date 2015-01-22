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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import javax.swing.*;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.*;

/**
 * Class used to represent the parent hierarchy of a single selected component
 * in the UI editor. This allows the user to quickly jump to editing a parent
 * without having to move their mouse to the inspector window and perform an
 * action.
 *
 * @author Wade Chandler
 */
public class DesignParentAction extends NodeAction {

    @Override
    protected boolean enable(Node[] nodes) {
        boolean ret = false;
        if (nodes != null && nodes.length == 1) {
            RADComponentCookie radCookie = nodes[0].getLookup().lookup(RADComponentCookie.class);
            RADComponent<?> comp = (radCookie != null) ? radCookie.getRADComponent() : null;
            if (comp != null && isParentEditableComponent(comp)) {
                    RADVisualComponent<?> designed = comp.getFormModel().getTopDesignComponent();
                    if (comp == designed
                            || (designed != null && designed.isParentComponent(comp)
                            && isParentEditableComponent(comp.getParentComponent()))) {
                        ret = true;
                        // the component must be in the designed tree and have
                        // some designable parent that is not designed at this moment
                    }
            }
        }
        return ret;
    }

    public static boolean isParentEditableComponent(RADComponent<?> comp) {
        RADComponent<?> parent = comp.getParentComponent();
        if (EditContainerAction.isEditableComponent(parent)) {
            return true;
        }
        // if not having parent, consider the top component
        if (parent == null) {
            RADComponent<?> topComp = comp.getFormModel().getTopRADComponent();
            return comp != topComp && EditContainerAction.isEditableComponent(topComp);
        }
        return false;
    }

    @Override
    protected void performAction(Node[] activatedNodes) {
    }

    static void reenable(Node[] nodes) {
        SystemAction.get(DesignParentAction.class).reenable0(nodes);
    }

    private void reenable0(Node[] nodes) {
        setEnabled(enable(nodes));
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(DesignParentAction.class, "ACT_DesignParentAction"); // NOI18N
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
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
                NbBundle.getMessage(DesignParentAction.class, "ACT_DesignParentAction")); // NOI18N

        popupMenu.setEnabled(isEnabled());
        HelpCtx.setHelpIDString(popupMenu, DesignParentAction.class.getName());
        createSubmenu(popupMenu);
        return popupMenu;
    }

    private void createSubmenu(JMenu menu) {
        Node[] nodes = getActivatedNodes();
        if (nodes != null && nodes.length == 1) {
            RADComponentCookie radCookie = nodes[0].getLookup().lookup(RADComponentCookie.class);
            RADComponent<?> comp = (radCookie != null) ? radCookie.getRADComponent() : null;
            if (comp != null) {
                List<JMenuItem> list = new ArrayList<>();
                RADComponent<?> topComp = comp.getFormModel().getTopRADComponent();
                boolean topCompIncluded = false;
                RADComponent<?> parent = comp.getParentComponent();
                while (parent != null) {
                    list.add(new DesignParentMenuItem(parent, getMenuItemListener()));
                    if (parent == topComp) {
                        topCompIncluded = true;
                    }
                    parent = parent.getParentComponent();
                }
                if (!topCompIncluded && topComp != null) {
                    list.add(new DesignParentMenuItem(topComp, getMenuItemListener()));
                }
                for (ListIterator<JMenuItem> it = list.listIterator(list.size()); it.hasPrevious();) {
                    menu.add(it.previous());
                }
            }
        }
    }

    private ActionListener getMenuItemListener() {
        if (menuItemListener == null) {
            menuItemListener = new DesignParentMenuItemListener();
        }
        return menuItemListener;
    }

    private static class DesignParentMenuItem extends JMenuItem {

        private final RADComponent<?> radc;

        public DesignParentMenuItem(RADComponent<?> c, ActionListener l) {
            super();
            radc = c;
            setText(c.getName());
            addActionListener(l);
            setEnabled(radc != null && radc != radc.getFormModel().getTopDesignComponent());
        }

        public RADComponent<?> getRADComponent() {
            return radc;
        }
    }

    private static class DesignParentMenuItemListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent evt) {
            Object source = evt.getSource();
            if (source != null && source instanceof DesignParentMenuItem) {
                DesignParentMenuItem mi = (DesignParentMenuItem) source;
                RADComponent<?> lc = mi.getRADComponent();
                if (lc instanceof RADVisualContainer<?>) {
                    lc.getFormModel().setTopDesignComponent((RADVisualContainer<?>) lc);
                    // NodeAction is quite unreliable in enabling, do it ourselves for sure
                    Node[] n = new Node[]{lc.getNodeReference()};
                    if (n[0] != null) {
                        EditContainerAction.reenable(n);
                        DesignParentAction.reenable(n);
                        EditFormAction.reenable(n);
                    }
                }
            }
        }
    }
    private ActionListener menuItemListener;
}
