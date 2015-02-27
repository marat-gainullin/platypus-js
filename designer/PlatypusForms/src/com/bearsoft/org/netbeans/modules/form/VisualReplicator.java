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

import com.bearsoft.org.netbeans.modules.form.bound.RADModelGrid;
import com.bearsoft.org.netbeans.modules.form.bound.RADModelGridColumn;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.*;
import com.eas.client.forms.components.model.grid.ModelGrid;
import com.eas.client.forms.containers.ButtonGroup;
import com.eas.client.forms.components.rt.HasGroup;
import com.eas.client.forms.components.rt.HtmlContentEditorKit;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.openide.ErrorManager;

/**
 * This class replicates (clones) the reference instances from meta-components
 * of a form. This way an equal and independent hierarchy of real instances is
 * built. Components cloned this way are used in the ComponentLayer presenting
 * the form in designer, or by the TestAction. Thanks to mapping from meta
 * components to clones (and viceversa), effective incremental updates of
 * changes from metadata are possible. Note: After updating replicated
 * components, revalidate() and repaint() should be called on the top component.
 *
 * @author Tomas Pavek
 */
public class VisualReplicator {

    //private RADComponent<?> topDesignComponent;
    private final FormEditor formEditor;
    private final Map<String, Object> nameToClone = new HashMap<>();
    private final Map<Object, String> cloneToName = new HashMap<>();
    protected RADComponent<?> topDesignComponent;
    private final boolean designRestrictions;

    // ---------
    public VisualReplicator(FormEditor aFormEditor, boolean aDesignRestrictions) {
        super();
        formEditor = aFormEditor;
        designRestrictions = aDesignRestrictions;
        topDesignComponent = formEditor.getFormModel().getTopDesignComponent();
    }

    // ---------
    // mapping
    public Object getClonedComponent(RADComponent<?> radComp) {
        return radComp != null ? nameToClone.get(radComp.getName()) : null;
    }

    public Object getClonedComponent(String aName) {
        return nameToClone.get(aName);
    }

    public String getClonedComponentId(Object component) {
        return cloneToName.get(component);
    }

    public Map<String, Object> getMapToClones() {
        return Collections.unmodifiableMap(nameToClone);
    }

    // getters & setters
    public RADComponent<?> getTopDesignComponent() {
        return topDesignComponent;
    }

    public void setTopDesignComponent(RADComponent<?> aRadComponent) {
        topDesignComponent = aRadComponent;
        nameToClone.clear();
        cloneToName.clear();
    }

    public boolean getDesignRestrictions() {
        return designRestrictions;
    }

    // --------
    // executive public methods
    public Object createClone() {
        return createClone(topDesignComponent);
    }

    public Object createClone(RADComponent<?> radComp) {
        if (radComp != null) {
            Object clone;
            java.util.List<RADProperty<?>> relativeProperties = new ArrayList<>();
            try {
                // clone the whole visual hierarchy recursively 
                clone = cloneComponent(radComp, relativeProperties);
                // set relative properties additionally
                if (!relativeProperties.isEmpty()) {
                    copyRelativeProperties(relativeProperties);
                }
                Map<String, Object> mapToClones = new HashMap<>(getMapToClones());
                FormModel formModel = formEditor.getFormModel();
                Set<Entry<String, Object>> entries = mapToClones.entrySet();
                for (Map.Entry<String, Object> entry : entries) {
                    String id = entry.getKey();
                    Object comp = entry.getValue();
                    RADComponent<?> rc = formModel.getRADComponent(id);
                    if (rc != null && (comp == null || !rc.getBeanClass().isAssignableFrom(comp.getClass()))) {
                        // was converted
                        entry.setValue(rc.getBeanInstance());
                    }
                }
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
                clone = null;
            }
            return clone;
        } else {
            return null;
        }
    }

    public void reorderComponents(ComponentContainer radCont) throws Exception {
        if (radCont instanceof RADVisualContainer<?>) {
            updateContainerLayout((RADVisualContainer<?>) radCont);
        } else {
            checkModelGridOperation(radCont);
        }
    }

    public void updateContainerLayout(RADVisualContainer<?> radCont) {
        Container cont = (Container) getClonedComponent(radCont);
        if (cont != null) // cont == null => The container is not cloned by the replicator. See issue 63654
        {
            Container contDelegate = radCont.getContainerDelegate(cont);
            LayoutSupportManager laysup = radCont.getLayoutSupport();

            // clear the container first before setting/changing the layout
            if (laysup != null) { // layout support
                laysup.clearContainer(cont, contDelegate);
            }

            // update visual components
            RADVisualComponent<?>[] radComps = radCont.getSubComponents();
            Component[] comps = new Component[radComps.length];
            for (int i = 0; i < radComps.length; i++) {
                RADVisualComponent<? extends Component> radComp = radComps[i];
                Component comp = (Component) getClonedComponent(radComp);
                if (comp == null) {
                    comp = (Component) createClone(radComp);
                } else if (comp.getParent() != null) {
                    comp.getParent().remove(comp);
                }
                comp.setVisible(true);
                comps[i] = comp;
            }

            // set the layout and re-add the components
            if (laysup != null) { // layout support
                laysup.setLayoutToContainer(cont, contDelegate);
                if (comps.length > 0) {
                    laysup.addComponentsToContainer(cont, contDelegate, comps, 0);
                }
                laysup.arrangeContainer(cont, contDelegate);
            }
        }
    }

    public void updateAddedComponents(ComponentContainer radCont) throws Exception {
        Container container = null;
        if (radCont instanceof RADComponent<?>) {
            Object contClone = getClonedComponent((RADComponent) radCont);
            if (contClone instanceof Container) {
                if (radCont instanceof RADVisualContainer<?>) {
                    RADVisualContainer<?> visualRadCont = (RADVisualContainer<?>) radCont;
                    if (visualRadCont.getLayoutSupport() == null) {
                        // don't try incremental update with new layout support
                        updateContainerLayout(visualRadCont);
                        // layout is built, but we continue to also add e.g. menu bar
                    }
                    container = visualRadCont.getContainerDelegate((Container) contClone);
                } else {
                    container = (Container) contClone;
                }
                RADComponent<?>[] subComps = radCont.getSubBeans();
                for (int i = 0; i < subComps.length; i++) {
                    Object compClone = getClonedComponent(subComps[i]);
                    if (compClone == null) {
                        addComponent(subComps[i]);
                    } else if (compClone instanceof Component) {
                        Container cloneCont = ((Component) compClone).getParent();
                        if (cloneCont != container && cloneToName.get(cloneCont) != null) {
                            return; // The clone is placed in another container in
                            //replicator, there's going to be another update
                        }
                    }
                }
            }
        }
    }

    public void renameComponent(String aOldName, String aNewName){
        Object renamed = nameToClone.remove(aOldName);
        if(renamed != null){
            nameToClone.put(aNewName, renamed);
            String oldName = cloneToName.remove(renamed);
            cloneToName.put(renamed, aNewName);
            if(renamed instanceof Component){
                ((Component)renamed).setName(aNewName);
            }
        }
    }
    
    // for adding just one component, for adding more components use
    // updateAddedComponents
    public void addComponent(RADComponent<?> radComp) throws Exception {
        if (radComp != null && getClonedComponent(radComp) == null) {
            if (radComp instanceof RADVisualComponent<?>) {
                Object clone = createClone(radComp);
                if (clone instanceof Component) {
                    RADVisualContainer<?> radCnt = (RADVisualContainer<?>) radComp.getParentComponent();
                    if (radCnt != null) {
                        Container cont = (Container) getClonedComponent(radCnt);
                        if (radCnt.isMenuTypeComponent()) {
                            addToMenu(cont, clone);
                        } else {
                            LayoutSupportManager laysup = radCnt.getLayoutSupport();
                            if (laysup != null && cont != null) { // layout support
                                Container contDelegate = radCnt.getContainerDelegate(cont);
                                laysup.addComponentsToContainer(
                                        cont,
                                        contDelegate,
                                        new Component[]{(Component) clone},
                                        ((RADVisualComponent<?>) radComp).getComponentIndex());
                                laysup.arrangeContainer(cont, contDelegate);
                            }
                        }
                    }
                }
            } else if (radComp instanceof RADModelGridColumn) {
                checkModelGridOperation(radComp.getParent());
            }
        }
    }

    protected void checkModelGridOperation(ComponentContainer columnContainer) throws Exception {
        while (columnContainer instanceof RADComponent<?> && !(columnContainer instanceof RADModelGrid)) {
            columnContainer = ((RADComponent<?>) columnContainer).getParent();
        }
        if (columnContainer instanceof RADModelGrid) {
            RADModelGrid grid = (RADModelGrid) columnContainer;
            ModelGrid clonedGrid = (ModelGrid) getClonedComponent(grid);
            clonedGrid.setColumns(grid.getBeanInstance().getColumns());
            clonedGrid.setHeader(grid.getBeanInstance().getHeader());
        }
    }

    public void removeComponent(RADComponent<?> radComp, ComponentContainer radCont) throws Exception {
        if (radComp != null) {
            Object clone = getClonedComponent(radComp);
            if (clone != null) {
                if (clone instanceof Component) { // visual meta component was removed
                    Component comp = (Component) clone;
                    // do we know the parent container of the removed meta component?
                    RADVisualContainer<?> parentCont
                            = radCont instanceof RADVisualContainer
                                    ? (RADVisualContainer<?>) radCont : null;
                    Container cont = parentCont != null
                            ? (Container) getClonedComponent(parentCont) : null;

                    if (cont == null) {
                        // we don't know the meta container (layout support), so will
                        // just simply remove the component from its parent
                        if (comp.getParent() != null) {
                            comp.getParent().remove(comp);
                        }
                    } else { // let the layout support remove the visual component
                        Container contDelegate = parentCont.getContainerDelegate(cont);
                        LayoutSupportManager laysup = parentCont.getLayoutSupport();
                        if (laysup != null) { // layout support
                            if (!laysup.removeComponentFromContainer(
                                    cont, contDelegate, comp)) {   // layout delegate cannot remove individual components,
                                // we must clear the container and add the components again
                                laysup.clearContainer(cont, contDelegate);

                                RADVisualComponent<?>[] radComps = parentCont.getSubComponents();
                                if (radComps.length > 0) {
                                    // we assume the radcomponent is already removed
                                    Component[] comps = new Component[radComps.length];
                                    for (int i = 0; i < radComps.length; i++) {
                                        comp = (Component) getClonedComponent(radComps[i]);
                                        comps[i] = comp;
                                    }
                                    laysup.addComponentsToContainer(cont, contDelegate, comps, 0);
                                }
                            }
                        }
                    }
                    // fallback - workaround for issue 118019, for example
                    if (comp.getParent() != null) {
                        comp.getParent().remove(comp);
                    }
                } else if (clone instanceof MenuComponent) { // AWT menu
                    MenuComponent menuComp = (MenuComponent) clone;
                    MenuContainer menuCont = menuComp.getParent();
                    if (menuCont != null) {
                        menuCont.remove(menuComp);
                    } else {
                        return;
                    }
                }
                removeMapping(radComp);
            } else if (radComp instanceof RADModelGridColumn) {
                checkModelGridOperation(radComp.getParent());
            }
        }
    }

    public void updateComponentProperty(RADProperty<?> property) throws Exception {
        if (property != null) {
            RADComponent<?> radComp = property.getRADComponent();
            // target component of the property
            Object targetComp = getClonedComponent(radComp);
            if (targetComp != null) {
                // Scrollbar hack - to change some properties of AWT Scrollbar we
                // must create a new instance of Scrollbar (peer must be recreated)
                // [maybe this should be done for all AWT components]
                if (!(targetComp instanceof java.awt.Scrollbar)) {
                    boolean buttonGroup = ("buttonGroup".equals(property.getName()) && (targetComp instanceof AbstractButton)); // NOI18N
                    Method writeMethod = FormUtils.getPropertyWriteMethod(property, targetComp.getClass());
                    if (writeMethod != null || buttonGroup) {
                        try {
                            Object clonedValue;
                            if (property.getValue() instanceof ComponentReference && ((ComponentReference) property.getValue()).getComponent() != null) {
                                RADComponent<?> valueComp = ((ComponentReference) property.getValue()).getComponent();
                                // the value is another component (relative property)
                                Object clonedComp = getClonedComponent(valueComp);
                                if (clonedComp == null) { // there's no cloned instance yet
                                    clonedComp = createClone(valueComp);
                                }
                                clonedValue = clonedComp;
                            } else { // this is not a relative property (another component)
                                // There are cases when properties values are not applicable to native swing components properties
                                // So we have to get value to clone directly from Swing Component
                                Object realValue = property.getPropertyDescriptor().getReadMethod().invoke(radComp.getBeanInstance(), new Object[]{});
                                clonedValue = FormUtils.cloneObject(realValue);
                            }

                            if (buttonGroup) {
                                // special case - add button to button group
                                AbstractButton button = (AbstractButton) targetComp;
                                // remove from the old group
                                if (button instanceof HasGroup) {
                                    ((HasGroup) button).setButtonGroup(null);
                                }
                                // add to the new group
                                if (clonedValue instanceof ButtonGroup) {
                                    ((ButtonGroup) clonedValue).add(button);
                                }
                            } else {
                                if (!"visible".equals(property.getName())) {
                                    writeMethod.invoke(targetComp, new Object[]{clonedValue});
                                }
                            }
                            if (targetComp instanceof Component) {
                                ((Component) targetComp).invalidate();
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(VisualReplicator.class.getName()).log(Level.INFO, null, ex); // NOI18N
                        }
                    }
                } else {
                    // remove the component and add a new clone
                    removeComponent(radComp, null);
                    addComponent(radComp);
                }
            }
        }
    }

    // recursive method
    private Object cloneComponent(RADComponent<?> radComp,
            java.util.List<RADProperty<?>> relativeProperties)
            throws Exception {
        Object compClone = null; // clone of the component itself, might be "inside"
        // the returned clone - e.g. JPanel enclosed in JFrame by a converter

        Object oClone = radComp.cloneBeanInstance(relativeProperties);
        if (oClone == null) {
            return null;
        }

        if (compClone == null) {
            compClone = oClone;
        }

        nameToClone.put(radComp.getName(), compClone);
        cloneToName.put(compClone, radComp.getName());

        if (compClone instanceof java.beans.DesignMode) {
            ((java.beans.DesignMode) compClone).setDesignTime(getDesignRestrictions());
        }

        if (radComp instanceof RADVisualContainer<?>) {
            RADVisualContainer<?> radCont = (RADVisualContainer<?>) radComp;
            final Container cont = (Container) compClone;
            final Container contDelegate = radCont.getContainerDelegate(cont);

            // clone subcomponents
            RADVisualComponent<?>[] radComps = radCont.getSubComponents();
            final Component[] comps = new Component[radComps.length];
            for (int i = 0; i < radComps.length; i++) {
                RADComponent<?> sub = radComps[i];
                Component subClone = (Component) getClonedComponent(sub);
                if (subClone == null) {
                    subClone = (Component) cloneComponent(sub, relativeProperties);
                }
                comps[i] = subClone;
            }

            if (radCont.isMenuTypeComponent()) {
                for (Component comp : comps) {
                    addToMenu(cont, comp);
                }
            } else { // set layout
                final LayoutSupportManager laysup = radCont.getLayoutSupport();
                if (laysup != null) { // layout support
                    laysup.setLayoutToContainer(cont, contDelegate);
                    if (comps.length > 0) { // add cloned subcomponents to container
                        laysup.addComponentsToContainer(cont, contDelegate, comps, 0);
                    }
                    laysup.arrangeContainer(cont, contDelegate);
                }
            }
        }

        if (oClone instanceof Component && getDesignRestrictions()) {
            assert radComp.getBeanInstance() instanceof Component;
            final Component originalComp = (Component) radComp.getBeanInstance();
            final Component clonedComp = (Component) oClone;
            clonedComp.setLocation(originalComp.getLocation());
            clonedComp.setSize(originalComp.getSize());
            clonedComp.setVisible(true);
            if (clonedComp instanceof JComponent) {
                // turn off double buffering for JComponent in fake peer container
                if (hasAwtParent(radComp)) {
                    setDoubleBufferedRecursively((JComponent) clonedComp, false);
                }
                // make sure debug graphics options is turned off
                ((JComponent) clonedComp).setDebugGraphicsOptions(DebugGraphics.NONE_OPTION);
                if (clonedComp instanceof JTextPane) {
                    ((JTextPane) clonedComp).setContentType("text/plain");
                } else if (clonedComp instanceof JEditorPane) {
                    String text = ((JEditorPane) clonedComp).getText();
                    ((JEditorPane) clonedComp).setEditorKitForContentType("text/html", new HtmlContentEditorKit());
                    ((JEditorPane) clonedComp).setContentType("text/html");
                    ((JEditorPane) clonedComp).setText(text);
                }
            }
            disableFocusing(clonedComp);

            // patch for JDK 1.4 - hide glass pane of JInternalFrame
            if (clonedComp instanceof JInternalFrame) {
                ((JInternalFrame) oClone).getGlassPane().setVisible(false);
            }
            clonedComp.addHierarchyListener((HierarchyEvent e) -> {
                originalComp.setLocation(clonedComp.getLocation());
                originalComp.setSize(clonedComp.getSize());
            });
            clonedComp.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentMoved(ComponentEvent e) {
                    originalComp.setLocation(clonedComp.getLocation());
                }

                @Override
                public void componentResized(ComponentEvent e) {
                    originalComp.setSize(clonedComp.getSize());
                }
            });
        }
        return oClone;
    }

    private static void disableFocusing(Component comp) {
        comp.setFocusable(false);
        if (comp instanceof Container) {
            Container cont = (Container) comp;
            for (int i = 0, n = cont.getComponentCount(); i < n; i++) {
                disableFocusing(cont.getComponent(i));
            }
        }
    }

    private static void addToMenu(Object menu, Object menuItem) {
        if (menu instanceof JMenuBar) {
            ((JMenuBar) menu).add((JMenu) menuItem);
        } else if (menu instanceof JMenu) {
            if (menuItem instanceof JMenuItem) {
                ((JMenu) menu).add((JMenuItem) menuItem);
            } else {
                ((JMenu) menu).addSeparator();
            }
        }
    }

    private static boolean hasAwtParent(RADComponent<?> radComp) {
        RADComponent<?> parent = radComp.getParentComponent();
        while (parent != null) {
            Class<?> beanClass = parent.getBeanClass();
            if (Component.class.isAssignableFrom(beanClass)
                    && !JComponent.class.isAssignableFrom(beanClass)
                    && !RootPaneContainer.class.isAssignableFrom(beanClass)) {   // this is AWT component
                return true;
            }

            parent = parent.getParentComponent();
        }
        return false;
    }

    private static void setDoubleBufferedRecursively(JComponent component,
            boolean value) {
        component.setDoubleBuffered(value);
        Component[] subcomps = component.getComponents();
        for (int i = 0; i < subcomps.length; i++) {
            if (subcomps[i] instanceof JComponent) {
                setDoubleBufferedRecursively((JComponent) subcomps[i], value);
            }
        }
    }

    // -------
    // method for setting "relative" component properties additionaly
    private void copyRelativeProperties(java.util.List<RADProperty<?>> relativeProperties) {
        for (int i = 0; i < relativeProperties.size(); i++) {
            RADProperty<?> property = relativeProperties.get(i);
            try {
                Object value = property.getValue();
                RADComponent<?> valueComp;
                if (value instanceof ComponentReference) {
                    valueComp = ((ComponentReference) value).getComponent();
                } else {
                    valueComp = null;
                }

                if (valueComp != null) {
                    // the value is another component (relative property)
                    Object clonedComp = getClonedComponent(valueComp);
                    if (clonedComp == null) { // there's no cloned instance yet
                        clonedComp = cloneComponent(valueComp, relativeProperties);
                    }
                    Object clonedValue = clonedComp;

                    // target component of the property
                    Object targetComp
                            = getClonedComponent(property.getRADComponent());

                    Method writeMethod = FormUtils.getPropertyWriteMethod(property, targetComp.getClass());
                    if (writeMethod != null) {
                        writeMethod.invoke(targetComp,
                                new Object[]{clonedValue});
                    } else if (clonedValue instanceof ButtonGroup
                            && targetComp instanceof AbstractButton) {   // special case - add button to button group
                        ((ButtonGroup) clonedValue).remove((AbstractButton) targetComp);
                        ((ButtonGroup) clonedValue).add((AbstractButton) targetComp);
                    }
                }
            } catch (Exception ex) {
            } // should not happen, ignore
        }
    }

    private void removeMapping(RADComponent<?> radComp) {
        Object comp = nameToClone.remove(radComp.getName());
        if (comp != null) {
            cloneToName.remove(comp);
        }
        if (radComp instanceof ComponentContainer) {
            RADComponent<?>[] subcomps = ((ComponentContainer) radComp).getSubBeans();
            for (int i = 0; i < subcomps.length; i++) {
                removeMapping(subcomps[i]);
            }
        }
    }
}
