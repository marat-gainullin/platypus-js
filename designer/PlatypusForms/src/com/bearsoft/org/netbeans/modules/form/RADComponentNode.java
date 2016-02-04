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

import com.bearsoft.org.netbeans.modules.form.actions.*;
import com.bearsoft.org.netbeans.modules.form.actions.menu.DesignParentAction;
import com.bearsoft.org.netbeans.modules.form.actions.menu.EditContainerAction;
import com.bearsoft.org.netbeans.modules.form.bound.AddGridColumnAction;
import com.bearsoft.org.netbeans.modules.form.bound.RADColumnView;
import com.bearsoft.org.netbeans.modules.form.bound.RADModelGrid;
import com.bearsoft.org.netbeans.modules.form.bound.RADModelGridColumn;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.*;
import com.bearsoft.org.netbeans.modules.form.menu.AddSubItemAction;
import com.bearsoft.org.netbeans.modules.form.menu.InsertMenuAction;
import com.bearsoft.org.netbeans.modules.form.menu.MenuEditLayer;
import com.bearsoft.org.netbeans.modules.form.palette.PaletteUtils;
import com.eas.client.forms.components.model.grid.header.ModelGridColumn;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.datatransfer.*;
import java.text.MessageFormat;
import java.util.*;
import javax.swing.Action;
import org.openide.actions.*;
import org.openide.awt.AcceleratorBinding;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.*;
import org.openide.util.*;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.NewType;
import org.openide.util.datatransfer.PasteType;

public class RADComponentNode extends FormNode implements RADComponentCookie, FormPropertyCookie {

    private static final DefaultRADAction DEFAULT_ACTION = new DefaultRADAction();
    private static final MessageFormat nodeNameFormat
            = new MessageFormat(
                    FormUtils.getBundleString("FMT_ComponentNodeName")); // NOI18N
    private static final CopyAction copyAction = SystemAction.get(CopyAction.class);
    private static final CutAction cutAction = SystemAction.get(CutAction.class);

    static {
        AcceleratorBinding.setAccelerator(copyAction, FileUtil.getConfigFile("Actions/Edit/org-openide-actions-CopyAction.instance"));
        AcceleratorBinding.setAccelerator(cutAction, FileUtil.getConfigFile("Actions/Edit/org-openide-actions-CutAction.instance"));
    }

    private RADComponent<?> component;
    private boolean highlightDisplayName;
    private final Map<Integer, Image> img = new HashMap<>();

    public RADComponentNode(RADComponent<?> aComponent) {
        this(aComponent instanceof ComponentContainer ? new RADChildren((ComponentContainer) aComponent) : Children.LEAF, aComponent);
    }

    protected RADComponentNode(Children children, RADComponent<?> aComponent) {
        super(children, aComponent.getFormModel());
        component = aComponent;
        if (aComponent instanceof ComponentContainer) {
            getCookieSet().add(new ComponentsIndex());
        }
        updateName();
    }

    public final void updateName() {
        String compClassName = component.getBeanClass().getSimpleName();
        if (component instanceof RADVisualContainer<?> && FormUtils.Panel.class.getSimpleName().equals(compClassName)) {
            RADVisualContainer<?> radCont = (RADVisualContainer<?>) component;
            LayoutSupportManager layoutSupportManager = radCont.getLayoutSupport();
            if (layoutSupportManager != null && layoutSupportManager.getLayoutDelegate() != null) {
                Class<?> layoutedContainerClass = FormUtils.getPlatypusConainerClass(layoutSupportManager.getLayoutDelegate().getSupportedClass());
                compClassName = layoutedContainerClass.getSimpleName();
            }
        }
        setDisplayName(nodeNameFormat.format(new Object[]{getName(), compClassName}));
    }

    public void fireComponentPropertiesChange() {
        firePropertyChange(null, null, null);
    }

    public void fireComponentPropertySetsChange() {
        firePropertySetsChange(null, null);
    }

    public void fireChildrenChange() {
        ((RADComponentNode.FormNodeChildren) getChildren()).updateKeys();
    }
    private static boolean iconsInitialized;

    @Override
    public Image getIcon(final int iconType) {
        Image icon = img.get(iconType);
        if (icon != null) {
            return icon;
        }
        // try to get a special icon
        if (!iconsInitialized) {
            // getIconForClass invokes getNodes(true) which cannot be called in Mutex
            EventQueue.invokeLater(() -> {
                Image icon1 = PaletteUtils.getIconForClass(component.getBeanClass().getName(), iconType, true);
                iconsInitialized = true;
                if (icon1 != null) {
                    img.put(iconType, icon1);
                    fireIconChange();
                }
            });
        } else {
            icon = PaletteUtils.getIconForClass(component.getBeanClass().getName(), iconType, false);
        }

        if (icon == null) {
            // get icon from BeanInfo
            java.beans.BeanInfo bi = component.getBeanInfo();
            if (bi != null) {
                icon = bi.getIcon(iconType);
            }
            if (icon == null) {
                // use default icon
                icon = super.getIcon(iconType);
            }
        }
        img.put(iconType, icon);
        return icon;
    }

    @Override
    public Image getOpenedIcon(int iconType) {
        return getIcon(iconType);
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx("gui.components.editing"); // NOI18N
    }

    @Override
    public Node.PropertySet[] getPropertySets() {
        return component.getProperties();
    }

    /**
     * List new types that can be created in this node.
     *
     * @return new types
     */
    @Override
    public NewType[] getNewTypes() {
        return component.getNewTypes();
    }

    @Override
    public Action getPreferredAction() {
        return DEFAULT_ACTION;
    }

    @Override
    public Action[] getActions(boolean context) {
        if (actions == null) {
            List<Action> lactions = new ArrayList<>(20);
            RADVisualContainer<?> topComp = component.getFormModel().getTopRADComponent();

            if (component.isReadOnly()) {
                if (component == topComp) {
                    lactions.add(SystemAction.get(TestAction.class));
                    lactions.add(null);
                }
                lactions.add(SystemAction.get(CopyAction.class));
            } else if (!(component instanceof RADColumnView)) {
                if (SelectGridColumnViewAction.isEditableComponent(component)) {
                    lactions.add(SystemAction.get(SelectGridColumnViewAction.class));
                    addSeparator(lactions);
                }
                lactions.add(SystemAction.get(ChangeComponentNameAction.class));
                lactions.add(null);

                addSeparator(lactions);

                if (component instanceof ComponentContainer) {
                    addContainerActions(lactions);
                }
                addLayoutActions(lactions);
                if (component != topComp) {
                    lactions.add(SystemAction.get(MoveUpAction.class));
                    lactions.add(SystemAction.get(MoveDownAction.class));
                }
                if (component instanceof ComponentContainer) {
                    lactions.add(SystemAction.get(ReorderAction.class));
                }
                if (FillGridColumnsAction.isEditableComponent(component)) {
                    addSeparator(lactions);
                    lactions.add(SystemAction.get(FillGridColumnsAction.class));
                }
                addSeparator(lactions);

                if (component != topComp) {
                    lactions.add(cutAction);
                }
                lactions.add(copyAction);
                if (component instanceof ComponentContainer) {
                    lactions.add(SystemAction.get(PasteAction.class));
                }
                if (component != topComp) {
                    lactions.add(SystemAction.get(DeleteAction.class));
                }

            }
            lactions.add(null);

            javax.swing.Action[] superActions = super.getActions(context);
            if (superActions != null && superActions.length > 0) {
                lactions.addAll(Arrays.asList(superActions));
                lactions.add(null);
            }
            if (component == topComp) {
                lactions.add(SystemAction.get(TestAction.class));
            }
            this.actions = new Action[lactions.size()];
            lactions.toArray(this.actions);
        }

        return actions;
    }

    private void addLayoutActions(List<Action> actions) {
        if (component.getParentComponent() instanceof RADVisualContainer<?>) {
            // To uncomment folowing actions for the menu, you have to refactor theirs
            // updates (selected state and enabledness)
            //actions.add(SystemAction.get(AlignAction.class));
            //actions.add(SystemAction.get(SetAnchoringAction.class));
            //actions.add(SystemAction.get(SetResizabilityAction.class));
            actions.add(SystemAction.get(EncloseAction.class));
            actions.add(null);
        }
    }

    private void addContainerActions(List<Action> actions) {
        if (component instanceof RADVisualContainer<?> && !((RADVisualContainer<?>) component).hasDedicatedLayoutSupport()) {
            actions.add(SystemAction.get(SelectLayoutAction.class));
        }
        if (MenuEditLayer.isMenuBarContainer(component)) {
            actions.add(SystemAction.get(InsertMenuAction.class));
        } else if (MenuEditLayer.isMenuRelatedContainer(component)) {
            actions.add(SystemAction.get(AddSubItemAction.class));
        } else if (component instanceof RADModelGrid || component instanceof RADModelGridColumn) {
            actions.add(SystemAction.get(AddGridColumnAction.class));
        } else { // only use the AddAction for non-menu containers
            actions.add(SystemAction.get(AddAction.class));
        }
        if (getNewTypes().length != 0) {
            actions.add(null);
            actions.add(SystemAction.get(NewAction.class));
        }
        if (EditContainerAction.isEditableComponent(component)) {
            actions.add(SystemAction.get(EditContainerAction.class));
        }
        if (DesignParentAction.isParentEditableComponent(component)) {
            actions.add(SystemAction.get(DesignParentAction.class));
        }
        addSeparator(actions);
    }

    private static void addSeparator(List<Action> actions) {
        int n = actions.size();
        if (n > 0 && actions.get(n - 1) != null) {
            actions.add(null);
        }
    }

    @Override
    public String getName() {
        return component.getName();
    }

    /**
     * Set the system name. Fires a property change event. Also may change the
     * display name according to {@link #displayFormat}.
     *
     * @param aValue the new name
     */
    @Override
    public void setName(String aValue) {
        if (aValue != null && !aValue.isEmpty()) {
            component.setName(aValue);
        }
    }

    /**
     * Can this node be renamed?
     *
     * @return <code>false</code>
     */
    @Override
    public boolean canRename() {
        return !component.isReadOnly();
    }

    /**
     * Can this node be destroyed?
     *
     * @return <CODE>false</CODE>
     */
    @Override
    public boolean canDestroy() {
        return !component.isReadOnly()
                && component != component.getFormModel().getTopRADComponent();
    }

    /**
     * Remove the node from its parent and deletes it. The default
     * implementation obtains write access to the
     * {@link Children#MUTEX children's lock}, and removes the node from its
     * parent(if any). Also fires a property change.
     * <P>
     * This may be overridden by subclasses to do any additional cleanup.
     *
     * @exception java.io.IOException if something fails
     */
    @Override
    public void destroy() throws java.io.IOException {
        if (component.getNodeReference() == this) {
            if (RADComponentCreator.isTransparentLayoutComponent(component.getParentComponent())) {
                component = component.getParentComponent();
            }
            if (EventQueue.isDispatchThread()) {
                component.getFormModel().removeComponent(component, true);
            } else {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        component.getFormModel().removeComponent(component, true);
                    }
                });
            }
        } // otherwise the component was likely already removed with a parent component
        super.destroy();
    }

    /**
     * Test whether there is a customizer for this node. If true, the customizer
     * can be obtained via {@link #getCustomizer}.
     *
     * @return <CODE>true</CODE> if there is a customizer
     */
    @Override
    public boolean hasCustomizer() {
        return false;
    }

    /**
     * Creates the customizer component for the node.
     *
     * @return the component, or null if there is no customizer
     */
    @Override
    protected Component createCustomizer() {
        return null;
    }

    // -----------------------------------------------------------------------------------------
    // Clipboard operations
    /**
     * Test whether this node can be copied. The default implementation returns
     * <code>true</code>.
     *
     * @return <code>true</code> if it can
     */
    @Override
    public boolean canCopy() {
        return true;
    }

    /**
     * Test whether this node can be cut. The default implementation assumes it
     * can if this node is writeable.
     *
     * @return <code>true</code> if it can
     */
    @Override
    public boolean canCut() {
        return !component.isReadOnly()
                && component != component.getFormModel().getTopRADComponent();
    }

    /**
     * Copy this node to the clipboard.
     *
     * @return The transferable for RACComponentNode
     * @throws java.io.IOException if it could not copy
     */
    @Override
    public Transferable clipboardCopy() throws java.io.IOException {
        return new CopySupport.RADTransferable(
                CopySupport.getComponentCopyFlavor(),
                component);
    }

    /**
     * Cut this node to the clipboard.
     *
     * @return {@link Transferable} with one flavor, {@link RAD_COMPONENT_COPY_FLAVOR
     * }
     * @
     * throws java.io.IOException if it could not cut
     */
    @Override
    public Transferable clipboardCut() throws java.io.IOException {
        return new CopySupport.RADTransferable(
                CopySupport.getComponentCutFlavor(),
                component);
    }

    /**
     * Accumulate the paste types that this node can handle for a given
     * transferable.
     *
     * @param t a transferable containing clipboard data
     * @param s a list of {@link PasteType}s that will have added to it all
     * types valid for this node
     */
    @Override
    protected synchronized void createPasteTypes(Transferable t, java.util.List<PasteType> s) {
        super.createPasteTypes(t, s);
        CopySupport.createPasteTypes(t, s, component.getFormModel(), component);
    }

    // -----------------------------------------------------------------------------
    // RADComponentCookie implementation
    @Override
    public RADComponent<?> getRADComponent() {
        return component;
    }

    // -----------------------------------
    // FormPropertyCookie implementation
    @Override
    public FormProperty<?> getProperty(String name) {
        return component.getProperty(name);
    }

    // -----------------------------------------------------------------------------
    // Innerclasses
    public static class RADChildren extends FormNodeChildren {

        private final ComponentContainer container;
        private RADLayout keyLayout;

        public RADChildren(ComponentContainer aContainer) {
            super();
            container = aContainer;
            updateKeys();
        }

        // FormNodeChildren implementation
        @Override
        protected final void updateKeys() {
            List<RADComponent<?>> keys = new ArrayList<>();
            if (container instanceof RADVisualContainer<?> && ((RADVisualContainer<?>) container).shouldHaveLayoutNode()) {
                keyLayout = ((RADVisualContainer<?>) container).getLayoutSupport().getLayoutDelegate().getRadLayout();
                keys.add(keyLayout);
            }
            if (container instanceof RADModelGridColumn && ((RADModelGridColumn) container).getBeanInstance() instanceof ModelGridColumn) {
                RADModelGridColumn col = (RADModelGridColumn) container;
                keys.add(col.getViewControl());
            }
            keys.addAll(Arrays.asList(container.getSubBeans()));
            setKeys(keys);
        }

        @Override
        protected Node[] createNodes(RADComponent<?> key) {
            Node node;
            if (key == keyLayout) {
                node = new LayoutNode((RADVisualContainer<?>) container);
            } else {
                node = new RADComponentNode(key);
                key.setNodeReference((RADComponentNode) node);
                node.getChildren().getNodes(); // enforce subnodes creation
            }
            return new Node[]{node};
        }
    }

    private final class ComponentsIndex extends org.openide.nodes.Index.Support {

        @Override
        public Node[] getNodes() {
            RADComponent<?>[] comps;
            if (component instanceof RADVisualContainer<?>) {
                comps = ((RADVisualContainer<?>) component).getSubComponents();
            } else if (component instanceof ComponentContainer) {
                comps = ((ComponentContainer) component).getSubBeans();
            } else {
                comps = null;
            }

            Node[] nodes = new Node[comps != null ? comps.length : 0];
            for (int i = 0; i < nodes.length; i++) {
                nodes[i] = comps[i].getNodeReference();
            }

            return nodes;
        }

        @Override
        public int getNodesCount() {
            return getNodes().length;
        }

        @Override
        public void reorder(int[] perm) {
            if (component instanceof ComponentContainer) {
                ComponentContainer cont = (ComponentContainer) component;
                cont.reorderSubComponents(perm);
                component.getFormModel().fireComponentsReordered(cont, perm);
            }
        }
    }

    private static final class ChangeComponentNameAction extends RenameAction {

        @Override
        public String getName() {
            return NbBundle.getMessage(ChangeComponentNameAction.class, "ChangeVariableNameAction"); // NOI18N
        }
    }

    @Override
    public String getHtmlDisplayName() {
        if (highlightDisplayName) {
            return "<html><b>" + getDisplayName() + "</b></html>"; // NOI18N
        } else {
            return "<html>" + getDisplayName() + "</html>"; // NOI18N
        }
    }

    void highlightDisplayName(boolean highlight) {
        if (highlight != highlightDisplayName) {
            highlightDisplayName = highlight;
            fireDisplayNameChange(null, getDisplayName());
        }
    }
}
