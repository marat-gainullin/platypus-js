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

import com.bearsoft.gui.grid.header.GridColumnsNode;
import com.bearsoft.org.netbeans.modules.form.bound.RADColumnView;
import com.bearsoft.org.netbeans.modules.form.bound.RADModelGrid;
import com.bearsoft.org.netbeans.modules.form.bound.RADModelGridColumn;
import com.bearsoft.org.netbeans.modules.form.bound.RADModelScalarComponent;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.*;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.MarginLayoutSupport;
import com.eas.client.forms.components.Button;
import com.eas.client.forms.components.CheckBox;
import com.eas.client.forms.components.DesktopPane;
import com.eas.client.forms.components.DropDownButton;
import com.eas.client.forms.components.FormattedField;
import com.eas.client.forms.components.HtmlArea;
import com.eas.client.forms.components.Label;
import com.eas.client.forms.components.PasswordField;
import com.eas.client.forms.components.ProgressBar;
import com.eas.client.forms.components.RadioButton;
import com.eas.client.forms.components.Slider;
import com.eas.client.forms.components.TextArea;
import com.eas.client.forms.components.TextField;
import com.eas.client.forms.components.ToggleButton;
import com.eas.client.forms.components.model.ModelCheckBox;
import com.eas.client.forms.components.model.ModelComponentDecorator;
import com.eas.client.forms.components.model.ModelFormattedField;
import com.eas.client.forms.components.model.ModelTextArea;
import com.eas.client.forms.components.model.ModelWidget;
import com.eas.client.forms.components.model.grid.ModelGrid;
import com.eas.client.forms.containers.ButtonGroup;
import com.eas.client.forms.containers.ScrollPane;
import com.eas.client.forms.containers.ToolBar;
import com.eas.client.forms.menu.CheckMenuItem;
import com.eas.client.forms.menu.Menu;
import com.eas.client.forms.menu.MenuBar;
import com.eas.client.forms.menu.MenuItem;
import com.eas.client.forms.menu.MenuSeparator;
import com.eas.client.forms.menu.PopupMenu;
import com.eas.client.forms.menu.RadioMenuItem;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.util.*;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import org.openide.*;

/**
 * This class represents an access point for adding new components to FormModel.
 * Its responsibility is to create new meta components (from provided bean
 * classes) and add them to the FormModel. In some cases, no new component is
 * created, just modified (e.g. when a border is applied). This class is
 * intended to process user actions, so all errors are caught and reported here.
 *
 * @author Tomas Pavek
 */
public class RADComponentCreator {

    private enum TargetType {

        LAYOUT, VISUAL, OTHER
    }

    private enum ComponentType {

        NON_VISUAL, VISUAL
    }

    private static class TargetInfo {

        private TargetType targetType; // the way of adding/applying to the target component
        private ComponentType componentType; // type of radcomponent to be added/applied
        private RADComponent<?> targetComponent; // actual target component (after adjustments)
    }
    private final FormModel formModel;
    private RADVisualComponent<?> preRadComp;

    RADComponentCreator(FormModel model) {
        formModel = model;
    }

    /**
     * Creates and adds a new radcomponent to FormModel. The new component is
     * added to target component (if it is ComponentContainer).
     *
     * @param classSource ClassSource describing the component class
     * @param aConstraints constraints object (for visual components only)
     * @param targetComp component into which the new component is added
     * @return the radcomponent if it was successfully created and added (all
     * errors are reported immediately)
     */
    public RADComponent<?> createComponent(String classSource,
            RADComponent<?> targetComp,
            LayoutConstraints<?> aConstraints) {
        return createComponent(classSource, targetComp, aConstraints, true);
    }

    RADComponent<?> createComponent(String classSource,
            RADComponent<?> targetComp,
            LayoutConstraints<?> aConstraints,
            boolean exactTargetMatch) {
        Class<?> compClass = prepareClass(classSource);
        if (compClass != null) {
            RADComponent<?> radComp = createAndAddComponent(compClass, targetComp, aConstraints, exactTargetMatch);
            return radComp;
        } else {
            return null; // class loading failed
        }
    }

    /**
     * Creates a copy of a radcomponent and adds it to FormModel. The new
     * component is added or applied to the specified target component.
     *
     * @param sourceComp component to be copied
     * @param targetComp target component (where the new component is added)
     * @return the component if it was successfully created and added (all
     * errors are reported immediately)
     */
    public RADComponent<?> copyComponent(final RADComponent<?> sourceComp,
            final RADComponent<?> targetComp) {
        final TargetInfo target = getTargetInfo(sourceComp.getBeanClass(), targetComp,
                false, false);
        if (target == null) {
            return null;
        }

        try { // Look&Feel UI defaults remapping needed
            return FormLAF.<RADComponent<?>>executeWithLookAndFeel(formModel, () -> copyComponent2(sourceComp, null, target));
        } catch (Exception ex) { // should not happen
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
            return null;
        }
    }

    public boolean moveComponent(RADComponent<?> radComp, RADComponent<?> targetComp) throws Exception {
        TargetInfo target = getTargetInfo(radComp.getBeanClass(), targetComp, false, false);
        if (target != null) {
            formModel.removeComponent(radComp, false);
            if (targetComp instanceof RADVisualContainer<?>) {
                RADVisualContainer<?> targetCont = (RADVisualContainer<?>) targetComp;
                if (radComp instanceof RADVisualComponent<?> && targetCont.getLayoutSupport() != null && targetCont.getLayoutSupport().getLayoutDelegate() != null) {
                    RADVisualComponent<?> radCont = (RADVisualComponent<?>) radComp;
                    LayoutSupportDelegate lsd = targetCont.getLayoutSupport().getLayoutDelegate();
                    LayoutConstraints<?>[] newConstraints = new LayoutConstraints<?>[]{null};
                    lsd.convertConstraints(new LayoutConstraints<?>[]{null}, newConstraints, new Component[]{radCont.getBeanInstance()});
                    LayoutConstraints constraints = newConstraints[0];
                    radCont.setLayoutConstraints(lsd.getClass(), constraints);
                }
            }
            return copyComponent2(radComp, radComp, target) != null;
        } else {
            return false;
        }
    }

    public boolean addComponents(Collection<? extends RADComponent<?>> components, RADComponent<?> targetComp) throws Exception {
        for (RADComponent<?> radComp : components) {
            TargetInfo target = getTargetInfo(radComp.getBeanClass(), targetComp, false, false);
            if (target == null) {
                return false;
            }
            copyComponent2(radComp, radComp, target);
        }
        return true;
    }

    public static boolean canAddComponent(Class<?> beanClass, RADComponent<?> targetComp) {
        TargetInfo target = getTargetInfo(beanClass, targetComp, false, false);
        return target != null
                && (target.targetType == TargetType.OTHER
                || target.targetType == TargetType.VISUAL);
    }

    public static boolean canApplyComponent(Class<?> beanClass, RADComponent<?> targetComp) {
        TargetInfo target = getTargetInfo(beanClass, targetComp, false, false);
        return target != null && target.targetType == TargetType.LAYOUT;
    }

    // --------
    // Visual component can be precreated before added to form to provide for
    // better visual feedback when being added. The precreated component may
    // end up as added or canceled. If it is added to the form (by the user),
    // addPrecreatedComponent methods gets called. If adding is canceled for
    // whatever reason, releasePrecreatedComponent is called.
    public RADVisualComponent<?> precreateVisualComponent(final String classSource) {
        final Class<?> compClass = prepareClass(classSource);

        // no preview component if this is a window, applet, or not visual
        if (compClass == null
                // JPopupMenu can't be used as a visual component (added to a container)
                || javax.swing.JPopupMenu.class.isAssignableFrom(compClass)
                || !FormUtils.isVisualizableClass(compClass)) {
            return null;
        }

        if (preRadComp != null) {
            releasePrecreatedComponent();
        }

        try { // Look&Feel UI defaults remapping needed
            FormLAF.<RADVisualComponent<?>>executeWithLookAndFeel(formModel, () -> {
                preRadComp = createVisualComponent(compClass);
                return preRadComp;
            });
            return preRadComp;
        } catch (Exception ex) { // should not happen
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
            return null;
        }
    }

    public RADVisualComponent<?> getPrecreatedRADComponent() {
        return preRadComp;
    }

    static boolean shouldBeLayoutContainer(RADComponent<?> radComp) {
        return radComp instanceof RADVisualContainer
                && ((RADVisualContainer<?>) radComp).getLayoutSupport() == null;
    }

    public boolean addPrecreatedComponent(RADComponent<?> targetComp, int aIndex,
            LayoutConstraints<?> aConstraints) throws Exception {
        if (preRadComp != null) {
            TargetInfo target = getTargetInfo(preRadComp.getBeanClass(), targetComp, true, true);
            if (target != null
                    && (target.targetType == TargetType.VISUAL
                    || target.targetType == TargetType.OTHER)) {
                addVisualComponent2(preRadComp, target.targetComponent, aIndex, aConstraints, true);
            }
            releasePrecreatedComponent();
            return true;
        } else {
            return false;
        }
    }

    void releasePrecreatedComponent() {
        if (preRadComp != null) {
            preRadComp = null;
        }
    }

    // --------
    private RADComponent<?> createAndAddComponent(final Class<?> compClass,
            final RADComponent<?> targetComp,
            final LayoutConstraints<?> aConstraints,
            boolean exactTargetMatch) {
        // check adding form class to itself

        final TargetInfo target = getTargetInfo(compClass, targetComp,
                !exactTargetMatch, !exactTargetMatch);
        if (target != null) {

            try { // Look&Feel UI defaults remapping needed
                return FormLAF.<RADComponent<?>>executeWithLookAndFeel(formModel, () -> createAndAddComponent2(compClass, target, aConstraints));
            } catch (Exception ex) { // should not happen
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
                return null;
            }
        } else {
            return null;
        }
    }

    private RADComponent<?> createAndAddComponent2(Class<?> compClass,
            TargetInfo target,
            LayoutConstraints<?> aConstraints) throws Exception {
        RADComponent<?> targetComp = target.targetComponent;
        if (target.targetType == TargetType.LAYOUT) {
            return setContainerLayout((Class<LayoutManager>) compClass, targetComp);
        } else {
            RADComponent<?> newRadComp = null;
            if (target.componentType == ComponentType.VISUAL) {
                newRadComp = addVisualComponent((Class<? extends Component>) compClass, targetComp, -1, aConstraints);
            } else {
                if (ButtonGroup.class.isAssignableFrom(compClass)) {
                    newRadComp = addButtonGroup((Class<ButtonGroup>) compClass, targetComp);
                } else if (GridColumnsNode.class.isAssignableFrom(compClass)) {
                    newRadComp = addGridColumn((Class<GridColumnsNode>) compClass, targetComp);
                }
            }
            return newRadComp;
        }
    }

    private RADComponent<?> copyComponent2(RADComponent<?> sourceComp,
            RADComponent<?> copiedComp,
            TargetInfo target) throws Exception {
        RADComponent<?> targetComp = target.targetComponent;
        // if layout or border is to be copied from a meta component, we just
        // apply the cloned instance, but don't copy the meta component
        if (target.targetType == TargetType.LAYOUT) {
            return copyAndApplyLayout(sourceComp, targetComp);
        } else {
            // in other cases we need a copy of the source radcomponent
            if (sourceComp instanceof RADVisualComponent<?>) {
                LayoutSupportManager.storeConstraints((RADVisualComponent<?>) sourceComp);
            }
            boolean newlyAdded;
            if (copiedComp == null) { // copy the source radcomponent
                copiedComp = makeCopy(sourceComp);
                if (copiedComp == null) { // copying failed (for a mystic reason)
                    return null;
                }
                newlyAdded = true;
            } else {
                newlyAdded = false;
            }
            if (target.targetType == TargetType.VISUAL) {
                RADVisualComponent<?> newVisual = (RADVisualComponent<?>) copiedComp;
                LayoutConstraints constraints;
                if (targetComp != null) {
                    RADVisualContainer<?> targetCont = (RADVisualContainer<?>) targetComp;
                    LayoutSupportManager layoutSupport = targetCont.getLayoutSupport();
                    if (layoutSupport == null) {
                        constraints = null;
                    } else {
                        constraints = layoutSupport.getStoredConstraints(newVisual);
                    }
                } else {
                    constraints = null;
                }
                copiedComp = addVisualComponent2(newVisual, targetComp, targetComp instanceof ComponentContainer ? ((ComponentContainer) targetComp).getSubBeansCount() : 0, constraints, newlyAdded);
                // might be null if layout support did not accept the component
            } else if (target.targetType == TargetType.OTHER) {
                if (copiedComp instanceof RADButtonGroup) {
                    addButtonGroup((RADButtonGroup) copiedComp, targetComp, newlyAdded);
                } else if (copiedComp instanceof RADModelGridColumn) {
                    addGridColumn((RADModelGridColumn) copiedComp, targetComp, newlyAdded);
                }
            }

            return copiedComp;
        }
    }

    /**
     * This is a central place for deciding whether a bean can be added or
     * applied to given target component. It returns a TargetInfo object
     * representing the target operation and type of radcomponent to be created,
     * or null if the bean can't be used. Determining the target placement is
     * more strict for copy/cut/paste (paramaters canUseParent and
     * defaultToOthers set to false), and less strict for visual (drag&drop)
     * operations (canUseParent and defaultToOthers set to true). In the latter
     * case the actual target component can be different - it is returned in the
     * targetComponent field of TargetInfo.
     */
    private static TargetInfo getTargetInfo(Class<?> beanClass,
            RADComponent<?> targetComp,
            boolean canUseParent,
            boolean defaultToOthers) {
        TargetInfo target = new TargetInfo();

        if (targetComp != null) {
            if (LayoutSupportDelegate.class.isAssignableFrom(beanClass)
                    || LayoutManager.class.isAssignableFrom(beanClass)) {   // layout manager
                RADVisualContainer<?> targetCont = getVisualContainer(targetComp, canUseParent);
                if (targetCont != null && !targetCont.hasDedicatedLayoutSupport()) {
                    target.targetType = TargetType.LAYOUT;
                } else {
                    return null;
                }
            } else if (FormUtils.isVisualizableClass(beanClass)) {
                // visual component
                if (java.awt.Window.class.isAssignableFrom(beanClass)
                        || java.applet.Applet.class.isAssignableFrom(beanClass)
                        || !java.awt.Component.class.isAssignableFrom(beanClass)) {
                    // visual component that cna't have a parent
                    if (defaultToOthers) {
                        targetComp = null; // will go to Other Components
                    } else {
                        return null;
                    }
                }

                RADVisualContainer<?> targetCont = getVisualContainer(targetComp, canUseParent);
                while (targetCont != null) {
                    if (targetCont.canAddComponent(beanClass)) {
                        target.targetType = TargetType.VISUAL;
                        targetComp = targetCont;
                        break;
                    } else if (canUseParent) {
                        targetCont = targetCont.getParentComponent();
                    } else {
                        targetCont = null;
                    }
                }
                if (targetCont == null) {
                    if (defaultToOthers) {
                        targetComp = null; // will go to Other Components
                    } else {
                        return null;
                    }
                }
            }
        }
        if (targetComp == null) {
            target.targetType = TargetType.OTHER;
        } else {
            if (targetComp instanceof RADModelGrid) {
                target.targetType = TargetType.OTHER;
            } else if (targetComp instanceof RADModelGridColumn) {
                target.targetType = TargetType.OTHER;
            }
        }
        target.targetComponent = targetComp;

        if (FormUtils.isVisualizableClass(beanClass)) {
            target.componentType = ComponentType.VISUAL;
        } else {
            target.componentType = ComponentType.NON_VISUAL;
        }

        return target;
    }

    private static RADVisualContainer<?> getVisualContainer(RADComponent<?> targetComp, boolean canUseParent) {
        if (targetComp instanceof RADVisualContainer<?>) {
            return (RADVisualContainer<?>) targetComp;
        } else if (canUseParent && targetComp instanceof RADVisualComponent<?>) {
            return (RADVisualContainer<?>) targetComp.getParentComponent();
        } else {
            return null;
        }
    }

    static boolean isTransparentLayoutComponent(RADComponent<?> radComp) {
        return radComp != null
                && radComp.getBeanClass() == ScrollPane.class; // NOI18N
    }

    // ---------
    private RADComponent<?> makeCopy(RADComponent<?> sourceComp) throws Exception {
        RADComponent<?> newComp = null;

        if (sourceComp instanceof RADVisualContainer<?>) {
            newComp = new RADVisualContainer<>();
        } else if (sourceComp instanceof RADVisualComponent<?>) {
            if (sourceComp instanceof RADModelScalarComponent) {
                if (sourceComp instanceof RADColumnView<?>) {
                    newComp = new RADColumnView<>();
                } else {
                    newComp = new RADModelScalarComponent<>();
                }
            } else if (sourceComp instanceof RADModelGrid) {
                newComp = new RADModelGrid();
            } else {
                newComp = new RADVisualComponent<>();
            }
        } else {
            if (sourceComp instanceof RADModelGridColumn) {
                newComp = new RADModelGridColumn();
            } else if (sourceComp instanceof RADButtonGroup) {
                newComp = new RADButtonGroup();
            }
        }

        newComp.initialize(formModel);
        if (sourceComp != sourceComp.getFormModel().getTopRADComponent()) {
            String newName = (sourceComp.getName() != null && !sourceComp.getName().isEmpty()) ? formModel.findFreeComponentName(sourceComp.getName()) : formModel.findFreeComponentName(sourceComp.getBeanClass());
            newComp.setStoredName(newName);
        }

        try {
            newComp.initInstance(sourceComp.getBeanClass());
            newComp.setInModel(true);
        } catch (Exception ex) { // this is rather unlikely to fail
            ErrorManager em = ErrorManager.getDefault();
            em.annotate(ex, FormUtils.getBundleString("MSG_ERR_CannotCopyInstance")); // NOI18N
            em.notify(ex);
            return null;
        }

        // 1st - copy subcomponents
        if (sourceComp instanceof ComponentContainer) {
            RADComponent<?>[] sourceSubs = ((ComponentContainer) sourceComp).getSubBeans();
            RADComponent<?>[] newSubs = new RADComponent<?>[sourceSubs.length];

            for (int i = 0; i < sourceSubs.length; i++) {
                RADComponent<?> newSubComp = makeCopy(sourceSubs[i]);
                if (newSubComp == null) {
                    return null;
                }
                newSubs[i] = newSubComp;
            }

            ((ComponentContainer) newComp).initSubComponents(newSubs);

            // 2nd - clone layout support
            if (sourceComp instanceof RADVisualContainer<?>) {
                RADVisualComponent<?>[] newComps
                        = new RADVisualComponent<?>[newSubs.length];
                System.arraycopy(newSubs, 0, newComps, 0, newSubs.length);

                LayoutSupportManager sourceLayout
                        = ((RADVisualContainer<?>) sourceComp).getLayoutSupport();

                if (sourceLayout != null) {
                    RADVisualContainer<?> newCont = (RADVisualContainer<?>) newComp;
                    newCont.checkLayoutSupport();
                    newCont.getLayoutSupport().copyLayoutDelegateFrom(sourceLayout, newComps);
                }
            }
            // 3rd - clone column view
            if (sourceComp instanceof RADModelGridColumn) {
                assert newComp instanceof RADModelGridColumn;
                RADModelGridColumn sourceColumn = (RADModelGridColumn) sourceComp;
                RADModelGridColumn newColumn = (RADModelGridColumn) newComp;
                RADColumnView<? super ModelComponentDecorator> newColumnView = (RADColumnView<? super ModelComponentDecorator>) makeCopy(sourceColumn.getViewControl());
                // Let's revoke some work, obsolete for column view component
                newColumnView.setInModel(false);
                if (newColumnView.getConstraints() != null) {
                    newColumnView.getConstraints().clear();
                }
                // Set resulting view component to new column
                newColumn.setViewControl(newColumnView);
            }
        }

        // 4th - copy changed properties, except the name property
        int copyMode = FormUtils.DISABLE_CHANGE_FIRING;
        if (formModel == sourceComp.getFormModel()) {
            copyMode |= FormUtils.PASS_DESIGN_VALUES;
        }
        java.util.List<RADProperty<?>> filtered = new ArrayList<>();
        for (RADProperty<?> prop : sourceComp.getBeanProperties()) {
            if (!prop.isDefaultValue()) {
                filtered.add(prop);
            }
        }
        java.util.List<String> filteredNames = new ArrayList<>();
        filtered.stream().forEach((prop) -> {
            filteredNames.add(prop.getName());
        });
        RADProperty<?>[] sourceProps = filtered.toArray(new RADProperty<?>[]{});
        RADProperty<?>[] newProps = newComp.getBeanProperties(filteredNames.toArray(new String[]{}));
        assert sourceProps.length == newProps.length;

        FormUtils.copyProperties(sourceProps, newProps, copyMode);

        // 5th - copy layout constraints
        if (sourceComp instanceof RADVisualComponent<?>
                && newComp instanceof RADVisualComponent<?>) {
            Map<String, LayoutConstraints<?>> constraints = ((RADVisualComponent<?>) sourceComp).getConstraints();
            Map<String, LayoutConstraints<?>> newConstraints = new HashMap<>();

            for (Map.Entry<String, LayoutConstraints<?>> entry : constraints.entrySet()) {
                String layoutClassName = entry.getKey();
                LayoutConstraints<?> clonedConstr = entry.getValue().cloneConstraints();
                if (clonedConstr instanceof MarginLayoutSupport.MarginLayoutConstraints) {
                    // We assume, that component has a parent and it is not the root container
                    // because it has a constraints
                    MarginLayoutSupport.MarginLayoutConstraints mlc = (MarginLayoutSupport.MarginLayoutConstraints) clonedConstr;
                    Container targetContainer = sourceComp.getParentComponent().getBeanInstance();
                    Component sourceBean = (Component) sourceComp.getBeanInstance();
                    MarginLayoutSupport.mutate(mlc.getConstraintsObject(), targetContainer.getWidth(), targetContainer.getHeight(), sourceBean.getLocation().x + 10, sourceBean.getLocation().y + 10, sourceBean.getWidth(), sourceBean.getHeight());
                }
                newConstraints.put(layoutClassName, clonedConstr);
            }
            ((RADVisualComponent<?>) newComp).getConstraints().putAll(newConstraints);
        }
        return newComp;
    }

    // --------
    private RADComponent<?> addVisualComponent(Class<? extends Component> compClass,
            RADComponent<?> targetComp,
            int aIndex,
            LayoutConstraints<?> aConstraints) throws Exception {
        RADVisualComponent<?> newRadComp = createVisualComponent(compClass);

        if (java.awt.Window.class.isAssignableFrom(compClass)
                || java.applet.Applet.class.isAssignableFrom(compClass)) {
            targetComp = null;
        }

        return addVisualComponent2(newRadComp, targetComp, aIndex, aConstraints, true);
    }

    private RADVisualComponent<?> createVisualComponent(Class<?> compClass) {
        RADVisualComponent<?> newRadComp = null;
        RADVisualContainer<?> newRadCont = FormUtils.isContainer(compClass) ? new RADVisualContainer<>() : null;
        // initialize radcomponent and its bean instance
        if (ModelGrid.class.isAssignableFrom(compClass)) {
            newRadComp = new RADModelGrid();
        } else if (ModelWidget.class.isAssignableFrom(compClass)) {
            newRadComp = new RADModelScalarComponent<>();
        } else {
            newRadComp = newRadCont == null ? new RADVisualComponent<>() : newRadCont;
        }

        newRadComp.initialize(formModel);
        if (initComponentInstance(newRadComp, compClass)) {
            prepareDefaultLayoutSize(newRadComp.getBeanInstance(), newRadCont != null);
            if (newRadCont != null) {
                // prepare layout support (the new component is a container)
                boolean knownLayout = false;
                Throwable layoutEx = null;
                try {
                    newRadCont.checkLayoutSupport();
                    LayoutSupportManager laysup = newRadCont.getLayoutSupport();
                    knownLayout = laysup.prepareLayoutDelegate(false);
                } catch (RuntimeException ex) { // silently ignore, try again as non-container
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
                    return null;
                } catch (Exception ex) {
                    layoutEx = ex;
                } catch (LinkageError ex) {
                    layoutEx = ex;
                }

                if (!knownLayout) {
                    if (layoutEx == null) {
                        // no LayoutSupportDelegate found for the container
                        System.err.println("[WARNING] No layout support found for " + compClass.getName()); // NOI18N
                        System.err.println("          Just a limited basic support will be used."); // NOI18N
                    } else { // layout support initialization failed
                        ErrorManager em = ErrorManager.getDefault();
                        em.annotate(
                                layoutEx,
                                FormUtils.getBundleString("MSG_ERR_LayoutInitFailed2")); // NOI18N
                        em.notify(layoutEx);
                    }

                    newRadCont.getLayoutSupport().setUnknownLayoutDelegate();
                }
            }
            newRadComp.setStoredName(formModel.findFreeComponentName(compClass));
            // for some components, we initialize their properties with some
            // non-default values e.g. a label on buttons, checkboxes
            return (RADVisualComponent<?>) defaultVisualComponentInit(newRadComp);
        } else {
            return null; // failure (reported)
        }
    }

    private RADVisualComponent<?> addVisualComponent2(RADVisualComponent<?> newRadComp,
            RADComponent<?> targetComp,
            int aIndex,
            LayoutConstraints<?> aConstraints,
            boolean newlyAdded) throws Exception {
        // Issue 65254: beware of nested JScrollPanes
        if ((targetComp != null) && ScrollPane.class.isAssignableFrom(targetComp.getBeanClass())) {
            Object bean = newRadComp.getBeanInstance();
            if (bean instanceof ScrollPane) {
                RADVisualContainer<?> radCont = (RADVisualContainer<?>) newRadComp;
                newRadComp = radCont.getSubComponent(0);
            }
        }

        // get parent container into which the new component will be added
        RADVisualContainer<?> parentCont;
        if (targetComp != null) {
            parentCont = targetComp instanceof RADVisualContainer
                    ? (RADVisualContainer<?>) targetComp
                    : (RADVisualContainer<?>) targetComp.getParentComponent();
        } else {
            parentCont = null;
        }

        defaultTargetInit(newRadComp, parentCont);

        // add the new radcomponent to the model
        if (parentCont != null) {
            try {
                formModel.addVisualComponent(newRadComp, parentCont, aIndex, aConstraints, newlyAdded);
            } catch (RuntimeException ex) {
                // LayoutSupportDelegate may not accept the component
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
                return null;
            }
        } else {
            formModel.addComponent(newRadComp, null, newlyAdded);
        }
        return newRadComp;
    }

    private RADButtonGroup addButtonGroup(Class<ButtonGroup> compClass,
            RADComponent<?> targetComp) throws Exception {
        assert ButtonGroup.class.isAssignableFrom(compClass);
        RADButtonGroup newRadComp = new RADButtonGroup();
        newRadComp.initialize(formModel);
        if (!initComponentInstance(newRadComp, compClass)) {
            return null;
        }
        addButtonGroup(newRadComp, targetComp, true);
        return newRadComp;
    }

    private void addButtonGroup(RADButtonGroup newRadComp,
            RADComponent<?> targetComp,
            boolean newlyAdded) {
        ComponentContainer targetCont
                = targetComp instanceof ComponentContainer
                && !(targetComp instanceof RADVisualContainer<?>)
                        ? (ComponentContainer) targetComp : null;
        if (newlyAdded) {
            newRadComp.setStoredName(formModel.findFreeComponentName(ButtonGroup.class));
        }
        formModel.addComponent(newRadComp, targetCont, newlyAdded);
    }

    private RADModelGridColumn addGridColumn(Class<GridColumnsNode> compClass,
            RADComponent<?> targetComp) throws Exception {
        assert GridColumnsNode.class.isAssignableFrom(compClass);
        RADModelGridColumn newRadComp = new RADModelGridColumn();
        newRadComp.initialize(formModel);
        if (initComponentInstance(newRadComp, compClass)) {
            addGridColumn(newRadComp, targetComp, true);
            return newRadComp;
        } else {
            return null;
        }
    }

    private void addGridColumn(RADModelGridColumn newColumn,
            RADComponent<?> targetComp,
            boolean newlyAdded) {
        if (newlyAdded && newColumn.getName() == null) {
            newColumn.setStoredName("column");
        }
        formModel.addComponent(newColumn, targetComp instanceof RADModelGrid
                || targetComp instanceof RADModelGridColumn
                        ? (ComponentContainer) targetComp : null, newlyAdded);
    }

    private RADComponent<?> setContainerLayout(Class<LayoutManager> layoutClass,
            RADComponent<?> targetComp) {
        // get container on which the layout is to be set
        RADVisualContainer<?> radCont;
        if (targetComp instanceof RADVisualContainer<?>) {
            radCont = (RADVisualContainer<?>) targetComp;
        } else {
            radCont = (RADVisualContainer<?>) targetComp.getParentComponent();
            if (radCont == null) {
                return null;
            }
        }

        LayoutSupportDelegate layoutDelegate = null;
        Throwable t = null;
        try {
            if (LayoutManager.class.isAssignableFrom(layoutClass)) {
                // LayoutManager -> find LayoutSupportDelegate for it
                layoutDelegate = LayoutSupportRegistry.createSupportForLayout(layoutClass);
            } else if (LayoutSupportDelegate.class.isAssignableFrom(layoutClass)) {
                // LayoutSupportDelegate -> use it directly
                layoutDelegate = (LayoutSupportDelegate) layoutClass.newInstance();
            }
        } catch (Exception | LinkageError ex) {
            t = ex;
        }
        if (t != null) {
            String msg = FormUtils.getFormattedBundleString(
                    "FMT_ERR_LayoutInit", // NOI18N
                    new Object[]{layoutClass.getName()});

            ErrorManager em = ErrorManager.getDefault();
            em.annotate(t, msg);
            em.notify(t);
            return null;
        }

        if (layoutDelegate == null) {
            DialogDisplayer.getDefault().notify(
                    new NotifyDescriptor.Message(
                            FormUtils.getFormattedBundleString(
                                    "FMT_ERR_LayoutNotFound", // NOI18N
                                    new Object[]{layoutClass.getName()}),
                            NotifyDescriptor.WARNING_MESSAGE));

            return null;
        }

        try {
            formModel.setContainerLayout(radCont, layoutDelegate);
        } catch (Exception | LinkageError ex) {
            t = ex;
        }
        if (t != null) {
            String msg = FormUtils.getFormattedBundleString(
                    "FMT_ERR_LayoutInit", // NOI18N
                    new Object[]{layoutClass.getName()});

            ErrorManager em = ErrorManager.getDefault();
            em.annotate(t, msg);
            em.notify(t);
            return null;
        }
        return radCont;
    }

    private RADComponent<?> copyAndApplyLayout(RADComponent<?> sourceComp,
            RADComponent<?> targetComp) {
        try {
            RADVisualContainer<?> targetCont = (RADVisualContainer<?>) setContainerLayout((Class<LayoutManager>) sourceComp.getBeanClass(), targetComp);

            // copy properties additionally to handle design values
            FormProperty<?>[] sourceProps = sourceComp.getBeanProperties();
            FormProperty<?>[] targetProps
                    = targetCont.getLayoutSupport().getAllProperties();
            int copyMode = FormUtils.CHANGED_ONLY
                    | FormUtils.DISABLE_CHANGE_FIRING;
            if (formModel == sourceComp.getFormModel()) {
                copyMode |= FormUtils.PASS_DESIGN_VALUES;
            }

            FormUtils.copyProperties(sourceProps, targetProps, copyMode);
        } catch (Exception | LinkageError ex) { // ignore
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
        }

        return targetComp;
    }

    // --------
    Class<?> prepareClass(final String classSource) {
        return prepareClass0(classSource);
    }

    private Class<?> prepareClass0(String classSource) {
        Throwable error = null;
        String className = classSource;
        Class<?> loadedClass = null;
        try {
            if (FormLAF.inLAFBlock()) {
                // Force update to new class loader
                FormLAF.setUseDesignerDefaults(null);
                FormLAF.setUseDesignerDefaults(formModel);
            }
            loadedClass = FormUtils.loadSystemClass(className);
        } catch (Exception | LinkageError ex) {
            error = ex;
        }

        if (loadedClass == null) {
            showClassLoadingErrorMessage(error, classSource);
        }

        return loadedClass;
    }

    private static void showClassLoadingErrorMessage(Throwable ex,
            String classSource) {
        ErrorManager em = ErrorManager.getDefault();
        String msg = FormUtils.getFormattedBundleString(
                "FMT_ERR_CannotLoadClass4", // NOI18N
                new Object[]{classSource});
        em.annotate(ex, msg);
        em.notify(ErrorManager.USER, ex); // Issue 65116 - don't show the exception to the user
        em.notify(ErrorManager.INFORMATIONAL, ex); // Make sure the exception is in the console and log file
    }

    private boolean initComponentInstance(RADComponent<?> radComp, Class<?> compClass) {
        try {
            radComp.initInstance(compClass);
        } catch (Exception | LinkageError ex) {
            showInstErrorMessage(ex);
            return false;
        }
        return true;
    }

    private static void showInstErrorMessage(Throwable ex) {
        ErrorManager em = ErrorManager.getDefault();
        em.annotate(ex,
                FormUtils.getBundleString("MSG_ERR_CannotInstantiate")); // NOI18N
        em.notify(ex);
    }

    // --------
    // default component initialization
    private RADComponent<?> defaultVisualComponentInit(RADVisualComponent<?> newRadComp) {
        Object comp = newRadComp.getBeanInstance();
        String varName = newRadComp.getName();
        // Map of propertyNames -> propertyValues
        Map<String, Object> changes = new HashMap<>();

        changes.put("name", varName);
        if (comp instanceof MenuItem) {
            changes.put("text", varName); // NOI18N
        } else if (comp instanceof CheckMenuItem) {
            changes.put("text", varName); // NOI18N
            changes.put("selected", Boolean.TRUE); // NOI18N
        } else if (comp instanceof RadioMenuItem) {
            changes.put("text", varName); // NOI18N
            changes.put("selected", Boolean.TRUE); // NOI18N
        } else if (comp instanceof Menu) {
            changes.put("text", varName); // NOI18N
        } else if (comp instanceof Label
                || comp instanceof Button
                || comp instanceof ToggleButton
                || comp instanceof DropDownButton
                || comp instanceof RadioButton
                || comp instanceof CheckBox
                || comp instanceof ModelCheckBox
                || comp instanceof Label
                || comp instanceof TextField
                || comp instanceof PasswordField
                || comp instanceof FormattedField
                || comp instanceof TextArea
                || comp instanceof HtmlArea
                || comp instanceof ModelTextArea
                || comp instanceof ModelFormattedField) {
            changes.put("text", varName); // NOI18N
        } else if (comp instanceof FormUtils.Panel) {
            changes.put("background", Color.white); // NOI18N
        }

        for (Map.Entry<String, Object> change : changes.entrySet()) {
            String propName = change.getKey();
            Object propValue = change.getValue();
            RADProperty<Object> prop = newRadComp.<RADProperty<Object>>getProperty(propName);
            if (prop != null) {
                try {
                    prop.setChangeFiring(false);
                    prop.setValue(propValue);
                    prop.setChangeFiring(true);
                } catch (Exception e) {
                    // never mind, ignore
                }
            }
        }

        // more initial modifications...
        if (shouldEncloseByScrollPane(newRadComp.getBeanInstance())) {
            // hack: automatically enclose some components into scroll pane
            // [PENDING check for undo/redo!]
            RADVisualContainer<?> radScroll = (RADVisualContainer<?>) createVisualComponent(ScrollPane.class);
            // Mark this scroll pane as automatically created.
            // Some action (e.g. delete) behave differently on
            // components in such scroll panes.
            radScroll.add(newRadComp);
            Container scroll = (Container) radScroll.getBeanInstance();
            Component inScroll = (Component) newRadComp.getBeanInstance();
            radScroll.getLayoutSupport().addComponentsToContainer(
                    scroll, scroll, new Component[]{inScroll}, 0);
            newRadComp = radScroll;
        } else if (newRadComp instanceof RADVisualContainer<?> && newRadComp.getBeanInstance() instanceof MenuBar) {
            // for menubars create initial menu [temporary?]
            RADVisualContainer<?> menuCont = (RADVisualContainer<?>) newRadComp;
            Container menuBar = (Container) menuCont.getBeanInstance();
            RADVisualComponent<?> menuComp = createVisualComponent(Menu.class);
            String menuFileName = formModel.findFreeComponentName("mnuFile");
            menuComp.setStoredName(menuFileName);
            menuComp.getBeanInstance().setName(menuFileName);
            try {
                (menuComp.<RADProperty<String>>getProperty("text")) // NOI18N
                        .setValue(FormUtils.getBundleString("CTL_DefaultFileMenu")); // NOI18N
            } catch (Exception ex) {
                // nevermind, ignore
            }
            Component menu = (Component) menuComp.getBeanInstance();
            menuCont.add(menuComp);
            menuCont.getLayoutSupport().addComponentsToContainer(
                    menuBar, menuBar, new Component[]{menu}, 0);

            menuComp = createVisualComponent(Menu.class);
            String mnuEditName = formModel.findFreeComponentName("mnuEdit");
            menuComp.setStoredName(mnuEditName);
            menuComp.getBeanInstance().setName(mnuEditName);
            try {
                (menuComp.<RADProperty<String>>getProperty("text")) // NOI18N
                        .setValue(FormUtils.getBundleString("CTL_DefaultEditMenu")); // NOI18N
            } catch (Exception ex) {
                // never mind, ignore
            }
            menu = (Component) menuComp.getBeanInstance();
            menuCont.add(menuComp);
            menuCont.getLayoutSupport().addComponentsToContainer(
                    menuBar, menuBar, new Component[]{menu}, 1);
        }
        return newRadComp;
    }

    private static boolean shouldEncloseByScrollPane(Object bean) {
//        return bean instanceof TextArea || bean instanceof HtmlArea || bean instanceof ModelTextArea;
        return bean instanceof TextArea || bean instanceof ModelTextArea;
    }

    /**
     * Initial setting for components that can't be done until knowing where
     * they are to be added to (type of target container). E.g. button
     * properties are adjusted when added to a toolbar.
     */
    private static void defaultTargetInit(RADComponent<?> radComp, RADComponent<?> target) {
        Object targetComp = target != null ? target.getBeanInstance() : null;

        if (radComp.getBeanClass().equals(MenuSeparator.class)) {
            if (targetComp instanceof Menu || targetComp instanceof PopupMenu) {
                try {
                    radComp.initInstance(MenuSeparator.class);
                } catch (Exception ex) {
                } // should not fail with JDK class
                return;

            }
        }

        Object comp = radComp.getBeanInstance();
        Map<String, Object> changes = null;

        if (comp instanceof AbstractButton && targetComp instanceof ToolBar) {
            if (changes == null) {
                changes = new HashMap<>();
            }
            changes.put("focusable", false); // NOI18N
            changes.put("horizontalTextPosition", SwingConstants.CENTER); // NOI18N
            changes.put("verticalTextPosition", SwingConstants.BOTTOM); // NOI18N
        }

        if (changes != null) {
            for (Map.Entry<String, Object> e : changes.entrySet()) {
                RADProperty<Object> prop = radComp.<RADProperty<Object>>getProperty(e.getKey());
                if (prop != null) {
                    try {
                        prop.setChangeFiring(false);
                        prop.setValue(e.getValue());
                        prop.setChangeFiring(true);
                    } catch (Exception ex) {
                        // never mind, ignore
                    }
                }
            }
        }
    }

    public static Dimension prepareDefaultLayoutSize(Component comp, boolean isContainer) {
        int width = -1;
        int height = -1;
        if (comp instanceof Label || comp instanceof AbstractButton) {
            width = 100;
            height = 30;
        } else if (comp instanceof ToolBar || comp instanceof MenuBar) {
            width = 150;
            height = 30;
        } else if (comp instanceof ProgressBar || comp instanceof Slider) {
            width = 250;
            height = 30;
        } else if (isContainer) {
            width = 100;
            height = 100;
        } else if (comp instanceof TextArea
                || comp instanceof ModelTextArea
                || comp instanceof HtmlArea
                || comp instanceof ModelGrid
                || comp instanceof DesktopPane) {
            width = 100;
            height = 100;
        }

        if (width >= 0 && height >= 0) {
            Dimension size = new Dimension(width, height);
            if (comp instanceof JComponent) {
                ((JComponent) comp).setPreferredSize(size);
            }
            return size;
        } else {
            return null;
        }
    }
}
