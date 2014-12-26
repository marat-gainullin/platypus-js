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

import com.bearsoft.org.netbeans.modules.form.*;
import java.awt.*;
import java.beans.*;
import java.util.*;
import org.openide.ErrorManager;
import org.openide.nodes.*;

/**
 * Main class of general layout support infrastructure. Connects form editor
 * metadata with specialized LayoutSupportDelegate implementations (layout
 * specific functionality is delegated to the right LayoutSupportDelegate).
 *
 * @author Tomas Pavek
 */
public final class LayoutSupportManager implements LayoutSupportContext {

    // possible component resizing directions (bit flag constants)
    public static final int RESIZE_UP = 1;
    public static final int RESIZE_DOWN = 2;
    public static final int RESIZE_LEFT = 4;
    public static final int RESIZE_RIGHT = 8;
    private LayoutSupportDelegate layoutDelegate;
    private boolean needInit;
    private boolean initializeFromInstance;
    //private boolean initializeFromCode;
    private Node.PropertySet[] propertySets;
    private LayoutListener layoutListener;
    private final RADVisualContainer<?> radContainer;
    private Container primaryContainer; // bean instance from radContainer
    private Container primaryContainerDelegate; // container delegate for it

    // ----------
    // initialization
    // initialization for a new container, layout delegate is set to null
    public LayoutSupportManager(RADVisualContainer<?> container) {
        radContainer = container;
    }

    /**
     * Creation and initialization of a layout delegate for a new container.
     *
     * @param initialize
     * @return false if suitable layout delegate is not found
     * @throws java.lang.Exception
     * @throw IllegalArgumentException if the container instance is not empty
     */
    public boolean prepareLayoutDelegate(boolean initialize) throws Exception {
        LayoutSupportDelegate delegate = null;
        LayoutManager lmInstance = null;

        // first try to find a dedicated layout delegate (for the container)
        Class<?> layoutDelegateClass = LayoutSupportRegistry.getSupportClassForContainer(
                radContainer.getBeanClass());

        if (layoutDelegateClass != null) {
            delegate = (LayoutSupportDelegate) layoutDelegateClass.newInstance();
            /*
            if (!delegate.checkEmptyContainer(getPrimaryContainer())) {
                RuntimeException ex = new IllegalArgumentException();
                org.openide.ErrorManager.getDefault().annotate(
                        ex, AbstractLayoutSupport.getBundle().getString(
                                "MSG_ERR_NonEmptyContainer")); // NOI18N
                throw ex;
            }
            */
        } else {
            Container contDel = getPrimaryContainerDelegate();
            //if (contDel.getComponentCount() == 0) {
                // we can still handle only empty containers ...
                lmInstance = contDel.getLayout();
                delegate = LayoutSupportRegistry.createSupportForLayout(lmInstance.getClass());
                /*
            } else {
                RuntimeException ex = new IllegalArgumentException();
                org.openide.ErrorManager.getDefault().annotate(
                        ex, AbstractLayoutSupport.getBundle().getString(
                                "MSG_ERR_NonEmptyContainer")); // NOI18N
                throw ex;
            }
                */
        }

        if (delegate != null) {
            if (initialize) {
                setLayoutDelegate(delegate);
            } else {
                layoutDelegate = delegate;
                needInit = true;
                initializeFromInstance = lmInstance != null;
            }
            return true;
        } else {
            return false;
        }
    }

    public void initializeLayoutDelegate() throws Exception {
        if (layoutDelegate != null && needInit) {
            LayoutManager lmInstance = initializeFromInstance
                    ? getPrimaryContainerDelegate().getLayout() : null;
            layoutDelegate.initialize(this, lmInstance);//, initializeFromCode);
            fillLayout(null);
            getPropertySets(); // force properties and listeners creation
            needInit = false;
        }
    }

    public void setLayoutDelegate(LayoutSupportDelegate newDelegate/*,
     boolean fromCode*/)
            throws Exception {
        LayoutConstraints<?>[] oldConstraints;
        LayoutSupportDelegate oldDelegate = layoutDelegate;

        if (layoutDelegate != null
                && (layoutDelegate != newDelegate/* || !fromCode*/)) {
            oldConstraints = removeLayoutDelegate(true);
        } else {
            oldConstraints = null;
        }

        layoutDelegate = newDelegate;
        propertySets = null;
        needInit = false;

        if (layoutDelegate != null) {
            try {
                layoutDelegate.initialize(this, null);
                fillLayout(oldConstraints);
                getPropertySets(); // force properties and listeners creation
            } catch (Exception ex) {
                removeLayoutDelegate(false);
                layoutDelegate = oldDelegate;
                if (layoutDelegate != null) {
                    fillLayout(null);
                }
                throw ex;
            }
        }
    }

    public LayoutSupportDelegate getLayoutDelegate() {
        return layoutDelegate;
    }

    public void setUnknownLayoutDelegate() {
        try {
            setLayoutDelegate(new UnknownLayoutSupport());
        } catch (Exception ex) { // nothing should happen, ignore
            ErrorManager.getDefault().notify(ex);
        }
    }

    public boolean isUnknownLayout() {
        return layoutDelegate == null
                || layoutDelegate instanceof UnknownLayoutSupport;
    }

    public boolean isSpecialLayout() {
        // Every standard layout manager has its own layout delegate.
        // Hence, the DefaultLayoutSupport is used by special layout managers only.
        return layoutDelegate instanceof DefaultLayoutSupport;
    }

    // copy layout delegate from another container
    public void copyLayoutDelegateFrom(
            LayoutSupportManager sourceLayoutSupport,
            RADVisualComponent<?>[] newRadComps) {
        LayoutSupportDelegate sourceDelegate
                = sourceLayoutSupport.getLayoutDelegate();

        int componentCount = sourceDelegate.getComponentCount();

        Container cont = getPrimaryContainer();
        Container contDel = getPrimaryContainerDelegate();

        if (layoutDelegate != null) {
            removeLayoutDelegate(false);
        }

        Component[] primaryComps = new Component[componentCount];

        for (int i = 0; i < componentCount; i++) {
            RADVisualComponent<?> radComp = newRadComps[i];
            primaryComps[i] = (Component) radComp.getBeanInstance();
        }

        LayoutSupportDelegate newDelegate
                = sourceDelegate.cloneLayoutSupport(this, newRadComps);

        newDelegate.setLayoutToContainer(cont, contDel);
        newDelegate.addComponentsToContainer(cont, contDel, primaryComps, 0);

        layoutDelegate = newDelegate;

        // Ensure correct propagation of copied properties (issue 50011, 72351)
        try {
            layoutDelegate.acceptContainerLayoutChange(null);
        } catch (PropertyVetoException pvex) {
            // should not happen
        }
    }

    public void clearPrimaryContainer() {
        layoutDelegate.clearContainer(getPrimaryContainer(),
                getPrimaryContainerDelegate());
    }

    public RADVisualContainer<?> getRadContainer() {
        return radContainer;
    }

    private LayoutConstraints<?>[] removeLayoutDelegate(
            boolean extractConstraints) {
        int componentCount = layoutDelegate.getComponentCount();
        LayoutConstraints<?>[] constraints = null;

        if (componentCount > 0) {
            RADVisualComponent<?>[] radComps = radContainer.getSubComponents();
            if (radComps.length == componentCount) { // robustness: might be called after failed layout initialization
                if (extractConstraints) {
                    constraints = new LayoutConstraints<?>[componentCount];
                }

                for (int i = 0; i < componentCount; i++) {
                    LayoutConstraints<?> constr = layoutDelegate.getConstraints(i);
                    if (extractConstraints) {
                        constraints[i] = constr;
                    }
                    if (constr != null) {
                        radComps[i].setLayoutConstraints(layoutDelegate.getClass(),
                                constr);
                    }
                }
            }
        }
        layoutDelegate.removeAll();
        layoutDelegate.clearContainer(getPrimaryContainer(),
                getPrimaryContainerDelegate());
        layoutDelegate = null;
        return constraints;
    }

    private void fillLayout(LayoutConstraints<?>[] oldConstraints) {
        RADVisualComponent<?>[] radComps = radContainer.getSubComponents();
        int componentCount = radComps.length;
        Component[] primaryComps = new Component[componentCount];
        LayoutConstraints<?>[] newConstraints = new LayoutConstraints<?>[componentCount];

        for (int i = 0; i < componentCount; i++) {
            primaryComps[i] = radComps[i].getBeanInstance();
            newConstraints[i] = radComps[i].getLayoutConstraints(layoutDelegate.getClass());
        }

        layoutDelegate.convertConstraints(oldConstraints,
                newConstraints,
                primaryComps);

        if (componentCount > 0) {
            layoutDelegate.acceptNewComponents(radComps, newConstraints, 0);
            layoutDelegate.addComponents(radComps, newConstraints, 0);

            for (int i = 0; i < componentCount; i++) {
                radComps[i].resetConstraintsProperties();
            }
        }

        // setup primary container
        Container cont = getPrimaryContainer();
        Container contDel = getPrimaryContainerDelegate();
        layoutDelegate.setLayoutToContainer(cont, contDel);
        if (componentCount > 0) {
            layoutDelegate.addComponentsToContainer(cont, contDel, primaryComps, 0);
        }
    }

    // ---------
    // public API delegated to LayoutSupportDelegate
    public boolean isDedicated() {
        return layoutDelegate.isDedicated();
    }

    public Class<?> getSupportedClass() {
        return layoutDelegate.getSupportedClass();
    }

    // node presentation
    public boolean shouldHaveNode() {
        return layoutDelegate.shouldHaveNode();
    }

    public String getDisplayName() {
        return layoutDelegate.getDisplayName();
    }

    public Image getIcon(int type) {
        return layoutDelegate.getIcon(type);
    }

    // properties and customizer
    public Node.PropertySet[] getPropertySets() {
        if (propertySets == null) {
            if (layoutDelegate == null) {
                return new Node.PropertySet[0]; // Issue 63916
            }
            propertySets = layoutDelegate.getPropertySets();

            for (Node.PropertySet propertySet : propertySets) {
                FormProperty<?>[] props = (FormProperty<?>[]) propertySet.getProperties();
                for (FormProperty<?> prop : props) {
                    prop.addVetoableChangeListener(getLayoutListener());
                    prop.addPropertyChangeListener(getLayoutListener());
                }
            }
        }
        return propertySets;
    }

    public FormProperty<?>[] getAllProperties() {
        if (layoutDelegate instanceof AbstractLayoutSupport) {
            return ((AbstractLayoutSupport) layoutDelegate).getAllProperties();
        }
        java.util.List<FormProperty<?>> allPropsList = new ArrayList<>();
        for (Node.PropertySet propertySet : propertySets) {
            FormProperty<?>[] props = (FormProperty<?>[]) propertySet.getProperties();
            allPropsList.addAll(Arrays.asList(props));
        }
        FormProperty<?>[] allProperties = new FormProperty<?>[allPropsList.size()];
        allPropsList.toArray(allProperties);
        return allProperties;
    }

    public FormProperty<?> getLayoutProperty(String name) {
        FormProperty<?> res = null;
        if (layoutDelegate instanceof AbstractLayoutSupport) {
            res = ((AbstractLayoutSupport) layoutDelegate).getProperty(name);
        }
        if (res == null) {
            FormProperty<?>[] properties = getAllProperties();
            for (int i = 0; i < properties.length; i++) {
                if (name.equals(properties[i].getName())) {
                    res = properties[i];
                }
            }
        }
        return res;
    }

    public Class<?> getCustomizerClass() {
        return layoutDelegate.getCustomizerClass();
    }

    public Component getSupportCustomizer() {
        return layoutDelegate.getSupportCustomizer();
    }

    public int getComponentCount() {
        return layoutDelegate.getComponentCount();
    }

    // data validation
    public void acceptNewComponents(RADVisualComponent<?>[] components,
            LayoutConstraints<?>[] constraints,
            int index) {
        layoutDelegate.acceptNewComponents(components, constraints, index);
    }

    // components adding/removing
    public void injectComponents(RADVisualComponent<?>[] components,
            LayoutConstraints<?>[] aConstraints,
            int index) {
        if (index <= -1) {
            index = layoutDelegate.getComponentCount();
        }
        layoutDelegate.addComponents(components, aConstraints, index);
        for (RADVisualComponent<?> component : components) {
            component.resetConstraintsProperties();
        }
    }

    public void addComponents(RADVisualComponent<?>[] components,
            LayoutConstraints<?>[] aConstraints,
            int index) {
        if (index <= -1) {
            index = layoutDelegate.getComponentCount();
        }
        injectComponents(components, aConstraints, index);
        
        Component[] comps = new Component[components.length];
        for (int i = 0; i < components.length; i++) {
            comps[i] = components[i].getBeanInstance();
        }
        layoutDelegate.addComponentsToContainer(getPrimaryContainer(),
                getPrimaryContainerDelegate(),
                comps, index);
    }

    public void removeComponent(RADVisualComponent<?> radComp, int index) {
        // first store constraints in the meta component
        LayoutConstraints<?> constr = layoutDelegate.getConstraints(index);
        if (constr != null) {
            radComp.setLayoutConstraints(layoutDelegate.getClass(), constr);
        }
        // remove the component from layout
        layoutDelegate.removeComponent(index);
        // remove the component instance from the primary container instance
        if (!layoutDelegate.removeComponentFromContainer(
                getPrimaryContainer(),
                getPrimaryContainerDelegate(),
                (Component) radComp.getBeanInstance())) {   // layout delegate does not support removing individual components,
            // so we clear the container and add the remaining components again
            layoutDelegate.clearContainer(getPrimaryContainer(),
                    getPrimaryContainerDelegate());

            RADVisualComponent<?>[] radComps = radContainer.getSubComponents();
            if (radComps.length > 1) {
                // we rely on that radcomp was not removed from the model yet
                Component[] comps = new Component[radComps.length - 1];
                for (int i = 0; i < radComps.length; i++) {
                    if (i != index) {
                        Component comp = (Component) radComps[i].getBeanInstance();
                        comps[i < index ? i : i - 1] = comp;
                    }
                }
                layoutDelegate.addComponentsToContainer(
                        getPrimaryContainer(),
                        getPrimaryContainerDelegate(),
                        comps,
                        0);
            }
        }
    }

    public void removeAll() {
        // first store constraints in meta components
        RADVisualComponent<?>[] components = radContainer.getSubComponents();
        for (int i = 0; i < components.length; i++) {
            LayoutConstraints<?> constr
                    = layoutDelegate.getConstraints(i);
            if (constr != null) {
                components[i].setLayoutConstraints(layoutDelegate.getClass(),
                        constr);
            }
        }
        // remove components from layout
        layoutDelegate.removeAll();
        // clear the primary container instance
        layoutDelegate.clearContainer(getPrimaryContainer(), getPrimaryContainerDelegate());
    }

    public boolean isLayoutChanged() {
        Container defaultContainer = (Container) BeanSupport.getDefaultInstance(radContainer.getBeanClass());
        Container defaultContDelegate
                = radContainer.getContainerDelegate(defaultContainer);

        return layoutDelegate.isLayoutChanged(defaultContainer, defaultContDelegate);
    }

    // managing constraints
    public LayoutConstraints<?> getConstraints(int index) {
        return layoutDelegate.getConstraints(index);
    }

    public LayoutConstraints<?> getConstraints(RADVisualComponent<?> radComp) {
        if (layoutDelegate != null) {
            int index = radComp.getComponentIndex();//radContainer.getIndexOf(radComp);
            return index >= 0 && index < layoutDelegate.getComponentCount()
                    ? layoutDelegate.getConstraints(index) : null;
        } else {
            return null;
        }
    }

    public static LayoutConstraints<?> storeConstraints(RADVisualComponent<?> radComp) {
        LayoutSupportManager layoutSupport = radComp.getParentLayoutSupport();
        if (layoutSupport != null) {
            LayoutConstraints<?> constr = layoutSupport.getConstraints(radComp);
            if (constr != null) {
                radComp.setLayoutConstraints(layoutSupport.getLayoutDelegate().getClass(), constr);
            }
            return constr;
        } else {
            return null;
        }
    }

    public LayoutConstraints<?> getStoredConstraints(RADVisualComponent<?> radComp) {
        return radComp.getLayoutConstraints(layoutDelegate.getClass());
    }

    // managing live components
    public void setLayoutToContainer(Container container,
            Container containerDelegate) {
        layoutDelegate.setLayoutToContainer(container, containerDelegate);
    }

    public void addComponentsToContainer(Container container,
            Container containerDelegate,
            Component[] components,
            int index) {
        layoutDelegate.addComponentsToContainer(container, containerDelegate,
                components, index);
    }

    public boolean removeComponentFromContainer(Container container,
            Container containerDelegate,
            Component component) {
        return layoutDelegate.removeComponentFromContainer(
                container, containerDelegate, component);
    }

    public boolean clearContainer(Container container,
            Container containerDelegate) {
        return layoutDelegate.clearContainer(container, containerDelegate);
    }

    // drag and drop support
    public LayoutConstraints<?> getNewConstraints(Container container,
            Container containerDelegate,
            Component component,
            int index,
            Point posInCont,
            Point posInComp) {

        LayoutConstraints<?> constraints = layoutDelegate.getNewConstraints(container, containerDelegate,
                component, index,
                posInCont, posInComp);
        String context = null;
        Object[] params = null;
        if (layoutDelegate instanceof AbstractLayoutSupport) {
            AbstractLayoutSupport support = (AbstractLayoutSupport) layoutDelegate;
            context = support.getAssistantContext();
            params = support.getAssistantParams();
        }
        context = (context == null) ? "generalPosition" : context; // NOI18N
        radContainer.getFormModel().getAssistantModel().setContext(context, params);
        return constraints;
    }

    public int getNewIndex(Container container,
            Container containerDelegate,
            Component component,
            int index,
            Point posInCont,
            Point posInComp) {
        return layoutDelegate.getNewIndex(container, containerDelegate,
                component, index,
                posInCont, posInComp);
    }

    public boolean paintDragFeedback(Container container,
            Container containerDelegate,
            Component component,
            LayoutConstraints<?> newConstraints,
            int newIndex,
            Graphics g) {
        return layoutDelegate.paintDragFeedback(container, containerDelegate,
                component,
                newConstraints, newIndex,
                g);
    }

    // resizing support
    public int getResizableDirections(Container container,
            Container containerDelegate,
            Component component,
            int index) {
        return layoutDelegate.getResizableDirections(container,
                containerDelegate,
                component, index);
    }

    public LayoutConstraints<?> getResizedConstraints(Container container,
            Container containerDelegate,
            Component component,
            int index,
            Rectangle originalBounds,
            Insets sizeChanges,
            Point posInCont) {
        return layoutDelegate.getResizedConstraints(container,
                containerDelegate,
                component, index,
                originalBounds,
                sizeChanges,
                posInCont);
    }

    // arranging support
    public void processMouseClick(Point p,
            Container cont,
            Container contDelegate) {
        layoutDelegate.processMouseClick(p, cont, contDelegate);
    }

    // arranging support
    public void selectComponent(int index) {
        layoutDelegate.selectComponent(index);
    }

    // arranging support
    public void arrangeContainer(Container container,
            Container containerDelegate) {
        layoutDelegate.arrangeContainer(container, containerDelegate);
    }

    // return container instance of meta container
    @Override
    public Container getPrimaryContainer() {
        return (Container) radContainer.getBeanInstance();
    }

    // return container delegate of container instance of meta container
    @Override
    public Container getPrimaryContainerDelegate() {
        Container defCont = (Container) radContainer.getBeanInstance();
        if (primaryContainerDelegate == null || primaryContainer != defCont) {
            primaryContainer = defCont;
            primaryContainerDelegate
                    = radContainer.getContainerDelegate(defCont);
        }
        return primaryContainerDelegate;
    }

    // return component instance of meta component
    @Override
    public Component getPrimaryComponent(int index) {
        return (Component) radContainer.getSubComponent(index).getBeanInstance();
    }

    @Override
    public void updatePrimaryContainer() {
        Container cont = getPrimaryContainer();
        Container contDel = getPrimaryContainerDelegate();

        layoutDelegate.clearContainer(cont, contDel);
        layoutDelegate.setLayoutToContainer(cont, contDel);

        RADVisualComponent<?>[] components = radContainer.getSubComponents();
        if (components.length > 0) {
            Component[] comps = new Component[components.length];
            for (int i = 0; i < components.length; i++) {
                comps[i] = (Component) components[i].getBeanInstance();
            }
            layoutDelegate.addComponentsToContainer(cont, contDel, comps, 0);
        }
    }

    @Override
    public void containerLayoutChanged(PropertyChangeEvent ev)
            throws PropertyVetoException {
        if (ev != null && ev.getPropertyName() != null) {
            layoutDelegate.acceptContainerLayoutChange(ev);

            FormModel formModel = radContainer.getFormModel();
            formModel.fireContainerLayoutChanged(radContainer,
                    ev.getPropertyName(),
                    ev.getOldValue(),
                    ev.getNewValue());
        } else {
            propertySets = null;
        }

        LayoutNode node = radContainer.getLayoutNodeReference();
        if (node != null) {
            // propagate the change to node
            if (ev != null && ev.getPropertyName() != null) {
                node.fireLayoutPropertiesChange();
            } else {
                node.fireLayoutPropertySetsChange();
            }
        }
    }

    @Override
    public void componentLayoutChanged(int index, PropertyChangeEvent ev)
            throws PropertyVetoException {
        RADVisualComponent<?> radComp = radContainer.getSubComponent(index);

        if (ev != null && ev.getPropertyName() != null) {
            layoutDelegate.acceptComponentLayoutChange(index, ev);

            FormModel formModel = radContainer.getFormModel();
            formModel.fireComponentLayoutChanged(radComp,
                    ev.getPropertyName(),
                    ev.getOldValue(),
                    ev.getNewValue());

            if (radComp.getNodeReference() != null) // propagate the change to node
            {
                radComp.getNodeReference().firePropertyChangeHelper(
                        ev.getPropertyName(),
                        ev.getOldValue(),
                        ev.getNewValue());
            }
        } else {
            if (radComp.getNodeReference() != null) // propagate the change to node
            {
                radComp.getNodeReference().fireComponentPropertySetsChange();
            }
            radComp.resetConstraintsProperties();
        }
    }

    // ---------
    private LayoutListener getLayoutListener() {
        if (layoutListener == null) {
            layoutListener = new LayoutListener();
        }
        return layoutListener;
    }

    private class LayoutListener implements VetoableChangeListener,
            PropertyChangeListener {

        @Override
        public void vetoableChange(PropertyChangeEvent ev)
                throws PropertyVetoException {
            Object source = ev.getSource();
            String eventName = ev.getPropertyName();
            if (source instanceof FormProperty<?>
                    && FormProperty.PROP_VALUE.equals(eventName)) {
                ev = new PropertyChangeEvent(layoutDelegate,
                        ((FormProperty<?>) source).getName(),
                        ev.getOldValue(),
                        ev.getNewValue());

                containerLayoutChanged(ev);
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent ev) {
            Object source = ev.getSource();
            String eventName = ev.getPropertyName();
            if (source instanceof FormProperty<?>
                    && FormProperty.PROP_VALUE.equals(eventName)) {
                ev = new PropertyChangeEvent(layoutDelegate,
                        ((FormProperty<?>) source).getName(),
                        ev.getOldValue(),
                        ev.getNewValue());

                try {
                    containerLayoutChanged(ev);
                } catch (PropertyVetoException ex) {
                    // should not happen
                }
            }
        }
    }
}
