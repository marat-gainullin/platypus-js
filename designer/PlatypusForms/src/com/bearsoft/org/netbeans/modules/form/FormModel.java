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

import com.bearsoft.org.netbeans.modules.form.assistant.AssistantModel;
import com.bearsoft.org.netbeans.modules.form.bound.RADColumnView;
import com.bearsoft.org.netbeans.modules.form.bound.RADModelGridColumn;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.LayoutConstraints;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.LayoutSupportDelegate;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.LayoutSupportManager;
import com.eas.client.forms.Form;
import com.eas.client.forms.components.model.ModelComponentDecorator;
import com.eas.designer.explorer.PlatypusDataObject;
import java.beans.Introspector;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import org.openide.ErrorManager;
import org.openide.awt.UndoRedo;
import org.openide.filesystems.FileObject;
import org.openide.util.Mutex;
import org.openide.util.MutexException;

/**
 * Holds all data of a form.
 *
 * @author Tran Duc Trung, Tomas Pavek
 */
public class FormModel {

    // name of the form is name of the DataObject
    private final PlatypusDataObject dataObject;
    private final Form form;
    private RADVisualContainer<?> topDesignComponent;
    private final AssistantModel assistantModel = new AssistantModel();
    private String formName;
    private boolean readOnly;

    public FormModel(PlatypusDataObject aDataObject, Form aForm) {
        super();
        dataObject = aDataObject;
        form = aForm;
        setName(aDataObject.getName());
        FileObject targetFile = dataObject.getLookup().lookup(LayoutFileProvider.class).getLayoutFile();
        setReadOnly(!targetFile.canWrite());
    }

    public Form getForm() {
        return form;
    }

    public PlatypusDataObject getDataObject() {
        return dataObject;
    }

    public AssistantModel getAssistantModel() {
        return assistantModel;
    }

    public RADVisualContainer<?> getTopDesignComponent() {
        return topDesignComponent;
    }

    public void setTopDesignComponent(RADVisualContainer<?> aComponent) {
        RADVisualContainer<?> oldValue = topDesignComponent;
        topDesignComponent = aComponent;
        fireTopDesignComponentChanged(oldValue, topDesignComponent);
    }

    public String findFreeComponentName(Class<?> compClass) {
        return findFreeComponentName(compClass.getSimpleName());
    }

    public String findFreeComponentName(String baseName) {
        baseName = Introspector.decapitalize(baseName);
        RADComponent<?> comp = getRADComponent(baseName);
        int counter = 0;
        String generatedName = baseName;
        while (comp != null) {
            counter++;
            generatedName = baseName + counter;
            comp = getRADComponent(generatedName);
        }
        return generatedName;
    }
    // the top radcomponent of the form (null if form is based on Object)
    private RADVisualContainer<?> topRADComponent;
    // other components - out of the main hierarchy under topRADComponent
    private final List<RADComponent<?>> otherComponents = new ArrayList<>(10);
    // holds both topRADComponent and otherComponents
    private ModelContainer modelContainer;
    private final Map<String, RADComponent<?>> namesToComponents = new HashMap<>();
    private boolean formLoaded;
    private UndoRedo.Manager undoRedoManager;
    private boolean undoRedoRecording;
    private CompoundEdit compoundEdit;
    private boolean undoCompoundEdit;
    private boolean modified;
    // list of listeners registered on FormModel
    private List<FormModelListener> listeners;
    private List<FormModelEvent> eventList;
    private boolean firing;
    private RADComponentCreator metaCreator;
    private final FormSettings settings = new FormSettings();

    final void setName(String name) {
        formName = name;
    }

    final void setReadOnly(boolean aValue) {
        readOnly = aValue;
    }

    // -----------
    // getters
    public final String getName() {
        return formName;
    }

    public final boolean isReadOnly() {
        return readOnly;
    }

    public final boolean isFormLoaded() {
        return formLoaded;
    }

    public final RADVisualContainer<?> getTopRADComponent() {
        return topRADComponent;
    }

    public ModelContainer getModelContainer() {
        if (modelContainer == null) {
            modelContainer = new ModelContainer();
        }
        return modelContainer;
    }

    public Collection<RADComponent<?>> getOtherComponents() {
        return Collections.unmodifiableCollection(otherComponents);
    }

    public final RADComponent<?> getRADComponent(String aName) {
        return namesToComponents.get(aName);
    }

    /**
     * Returns list of all components in the model. A new List instance is
     * created. The order of the components is random.
     *
     * @return list of components in the model.
     */
    public java.util.List<RADComponent<?>> getComponentList() {
        return new ArrayList<>(namesToComponents.values());
    }

    /**
     * Returns list of all components in the model. A new instance of list is
     * created and the components are added to the list in the traversal order
     * (used e.g. by code generator or persistence manager).
     *
     * @return list of components in the model.
     */
    public java.util.List<RADComponent<?>> getOrderedComponentList() {
        java.util.List<RADComponent<?>> list = new ArrayList<>(namesToComponents.size());
        collectRadComponents(getModelContainer(), list);
        list.add(getTopRADComponent());
        collectRadComponents(getTopRADComponent(), list);
        return list;
    }

    /**
     * Returns an unmodifiable collection of all components in the model in
     * random order.
     *
     * @return list of components in the model.
     */
    public Collection<RADComponent<?>> getAllComponents() {
        return Collections.unmodifiableCollection(namesToComponents.values());
    }

    public List<RADComponent<?>> getVisualComponents() {
        List<RADComponent<?>> list = new ArrayList<>(namesToComponents.size());
        for (Map.Entry<String, RADComponent<?>> e : namesToComponents.entrySet()) {
            RADComponent<?> radComp = e.getValue();
            if (radComp instanceof RADVisualComponent<?>) {
                list.add(radComp);
            }
        }
        return list;
    }

    private static void collectRadComponents(ComponentContainer cont,
            java.util.List<RADComponent<?>> list) {
        RADComponent<?>[] comps = cont.getSubBeans();
        for (int i = 0; i < comps.length; i++) {
            RADComponent<?> comp = comps[i];
            list.add(comp);
            if (comp instanceof ComponentContainer) {
                collectRadComponents((ComponentContainer) comp, list);
            }
        }
    }

    public FormSettings getSettings() {
        return settings;
    }

    // -----------
    // adding/deleting components, setting layout, etc
    /**
     * @return RADComponentCreator responsible for creating new components and
     * adding them to the model.
     */
    public RADComponentCreator getComponentCreator() {
        if (metaCreator == null) {
            metaCreator = new RADComponentCreator(this);
        }
        return metaCreator;
    }

    /**
     * Adds a new component to given (non-visual) container in the model. If the
     * container is not specified, the component is added to the "other
     * components".
     *
     * @param radComp component to add.
     * @param parentContainer parent of the added component.
     * @param newlyAdded is newly added?
     */
    public void addComponent(RADComponent<?> radComp,
            ComponentContainer parentContainer,
            boolean newlyAdded) {
        if (newlyAdded || !radComp.isInModel()) {
            setInModelRecursively(radComp, true);
            newlyAdded = true;
        }
        if (parentContainer != null) {
            radComp.setParent(parentContainer);
            parentContainer.add(radComp);
        } else {
            radComp.setParent(null);
            otherComponents.add(radComp);
        }
        fireComponentAdded(radComp, newlyAdded);
    }

    /**
     * Adds a new visual component to given container managed by the layout
     * support.
     *
     * @param radComp component to add.
     * @param parentContainer parent of the added component.
     * @param aIndex
     * @param aConstraints layout constraints.
     * @param newlyAdded is newly added?
     */
    public void addVisualComponent(RADVisualComponent<?> radComp,
            RADVisualContainer<?> parentContainer,
            int aIndex,
            LayoutConstraints<?> aConstraints,
            boolean newlyAdded) {
        LayoutSupportManager layoutSupport = parentContainer.getLayoutSupport();
        if (layoutSupport != null) {
            RADVisualComponent<?>[] compArray = new RADVisualComponent<?>[]{radComp};
            //LayoutConstraints<?> c = aConstraints instanceof LayoutConstraints<?>
            //        ? (LayoutConstraints<?>) aConstraints : null;
            LayoutConstraints<?>[] constrArray = new LayoutConstraints<?>[]{aConstraints};

            //int index = constraints instanceof Integer ? ((Integer) constraints).intValue() : -1;
            // constraints here may be of type Integer.
            // It comes from layout support delegates to
            // force us place a component in predefined position
            // like in toolbars while dragging new components in them.
            // component needs to be "in model" (have code expression) before added to layout
            if (newlyAdded || !radComp.isInModel()) {
                setInModelRecursively(radComp, true);
                newlyAdded = true;
            }
            if (newlyAdded) {
                try {
                    layoutSupport.acceptNewComponents(compArray, constrArray, aIndex);
                } catch (RuntimeException ex) {
                    // LayoutSupportDelegate may not accept the component
                    if (newlyAdded) {
                        setInModelRecursively(radComp, false);
                    }
                    throw ex;
                }
            }
            parentContainer.add(radComp, aIndex);
            layoutSupport.addComponents(compArray, constrArray, aIndex);
        } else {
            if (newlyAdded || !radComp.isInModel()) {
                setInModelRecursively(radComp, true);
                newlyAdded = true;
            }
            if (!newlyAdded) {
                radComp.resetConstraintsProperties();
            }
            parentContainer.add(radComp);
        }
        fireComponentAdded(radComp, newlyAdded);
    }

    void setContainerLayoutImpl(RADVisualContainer<?> radCont,
            LayoutSupportDelegate layoutDelegate)
            throws Exception {
        LayoutSupportManager currentLS = radCont.getLayoutSupport();
        LayoutSupportDelegate currentDel
                = currentLS != null ? currentLS.getLayoutDelegate() : null;

        if (currentLS == null) { // switching to layout support
            radCont.checkLayoutSupport();
        }
        try {
            radCont.setLayoutSupportDelegate(layoutDelegate);
        } catch (Exception ex) {
            throw ex;
        }

        fireContainerLayoutExchanged(radCont, currentDel, layoutDelegate);
    }

    public void setContainerLayout(RADVisualContainer<?> radCont,
            LayoutSupportDelegate layoutDelegate)
            throws Exception {
        setContainerLayoutImpl(radCont, layoutDelegate);
    }

    public void removeComponent(RADComponent<?> radComp, boolean fromModel) {
        removeComponentImpl(radComp, fromModel);
    }

    void removeComponentImpl(RADComponent<?> radComp, boolean fromModel) {
        if (fromModel) {
            setInModelRecursively(radComp, false);
        }
        ComponentContainer parent = radComp.getParent();
        if (parent != null)// parented components
        {
            int index = parent.getIndexOf(radComp);
            parent.remove(radComp);
            fireComponentRemoved(radComp, parent, index, fromModel);
        } else {// parentless components
            int index = modelContainer.getIndexOf(radComp);
            modelContainer.remove(radComp);
            fireComponentRemoved(radComp, modelContainer, index, fromModel);
        }
    }

    public void updateMapping(RADComponent<?> radComp, boolean register) {
        if (!(radComp instanceof RADModelGridColumn)) {
            if (register) {
                namesToComponents.put(radComp.getName(), radComp);
            } else {
                namesToComponents.remove(radComp.getName());
            }
        }
    }

    static void setInModelRecursively(RADComponent<?> radComp, boolean inModel) {
        if (radComp instanceof ComponentContainer) {
            RADComponent<?>[] comps = ((ComponentContainer) radComp).getSubBeans();
            for (RADComponent<?> comp : comps) {
                setInModelRecursively(comp, inModel);
            }
        }
        radComp.setInModel(inModel);
    }

    // ----------
    // undo and redo
    public void setUndoRedoRecording(boolean record) {
        t("turning undo/redo recording " + (record ? "on" : "off")); // NOI18N
        undoRedoRecording = record;
    }

    public boolean isUndoRedoRecording() {
        return undoRedoRecording;
    }

    private void startCompoundEdit() {
        if (compoundEdit == null) {
            t("starting compound edit"); // NOI18N
            compoundEdit = new CompoundEdit();
        }
    }
    private static boolean formModifiedLogged = false;

    public CompoundEdit endCompoundEdit(boolean commit) {
        if (compoundEdit != null) {
            t("ending compound edit: " + commit); // NOI18N
            compoundEdit.end();
            if (commit && undoRedoRecording && compoundEdit.isSignificant()) {
                if (!formModifiedLogged) {
                    Logger logger = Logger.getLogger("org.netbeans.ui.metrics.form"); // NOI18N
                    LogRecord rec = new LogRecord(Level.INFO, "USG_FORM_MODIFIED"); // NOI18N
                    rec.setLoggerName(logger.getName());
                    logger.log(rec);
                    formModifiedLogged = true;
                }
                getUndoRedoManager().undoableEditHappened(
                        new UndoableEditEvent(this, compoundEdit));
            }
            CompoundEdit edit = compoundEdit;
            compoundEdit = null;
            return edit;
        }
        return null;
    }

    public void forceUndoOfCompoundEdit() {
        if (compoundEdit != null) {
            undoCompoundEdit = true;
        }
    }

    public boolean isCompoundEditInProgress() {
        return compoundEdit != null; // && compoundEdit.isInProgress();
    }

    public void addUndoableEdit(UndoableEdit edit) {
        t("adding undoable edit"); // NOI18N
        if (!isCompoundEditInProgress()) {
            startCompoundEdit();
        }
        compoundEdit.addEdit(edit);
    }

    public UndoRedo.Manager getUndoRedoManager() {
        return undoRedoManager;
    }

    public void setColumnViewImpl(RADModelGridColumn aColumn, RADColumnView<? super ModelComponentDecorator> aView) {
        if (aColumn.getViewControl() != null) {
            RADColumnView<? super ModelComponentDecorator> oldView = aColumn.getViewControl();
            aColumn.setViewControl(aView);
            fireColumnViewExchanged(aColumn, oldView, aView);
        }
    }

    void initFormComponent(RADVisualFormContainer formComp) {
        topRADComponent = formComp;
        topDesignComponent = formComp;
    }

    // [Undo manager performing undo/redo in AWT event thread should not be
    //  probably implemented here - in FormModel - but seperately.]
    class UndoRedoManager extends UndoRedo.Manager {

        private final Mutex.ExceptionAction<Object> runUndo = () -> {
            super.undo();
            return null;
        };
        private final Mutex.ExceptionAction<Object> runRedo = () -> {
            super.redo();
            return null;
        };

        @Override
        public void undo() throws CannotUndoException {
            if (java.awt.EventQueue.isDispatchThread()) {
                super.undo();
            } else {
                try {
                    Mutex.EVENT.readAccess(runUndo);
                } catch (MutexException ex) {
                    Exception e = ex.getException();
                    if (e instanceof CannotUndoException) {
                        throw (CannotUndoException) e;
                    } else // should not happen, ignore
                    {
                        ErrorManager.getDefault().notify(e);
                    }
                }
            }
        }

        @Override
        public void redo() throws CannotRedoException {
            if (java.awt.EventQueue.isDispatchThread()) {
                super.redo();
            } else {
                try {
                    Mutex.EVENT.readAccess(runRedo);
                } catch (MutexException ex) {
                    Exception e = ex.getException();
                    if (e instanceof CannotRedoException) {
                        throw (CannotRedoException) e;
                    } else // should not happen, ignore
                    {
                        ErrorManager.getDefault().notify(e);
                    }
                }
            }
        }
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean aValue) {
        modified = aValue;
    }

    // ----------
    // listeners registration, firing methods
    public synchronized void addFormModelListener(FormModelListener l) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        listeners.add(l);
    }

    public synchronized void removeFormModelListener(FormModelListener l) {
        if (listeners != null) {
            listeners.remove(l);
        }
    }

    /**
     * Fires an event informing about that the form has been just loaded.
     */
    public void fireFormLoaded() {
        t("firing form loaded"); // NOI18N
        formLoaded = true;
        if (!readOnly) { // NOI18N
            undoRedoManager = new UndoRedoManager();
            undoRedoManager.setLimit(50);
            setUndoRedoRecording(true);
        }
        sendEventLater(new FormModelEvent(this, FormModelEvent.FORM_LOADED));
    }

    /**
     * Fires an event informing about that the form is just about to be saved.
     */
    public void fireFormToBeSaved() {
        t("firing form to be saved"); // NOI18N

        sendEventImmediately(
                new FormModelEvent(this, FormModelEvent.FORM_TO_BE_SAVED));
    }

    /**
     * Fires an event informing about that the form is just about to be closed.
     */
    public void fireFormToBeClosed() {
        t("firing form to be closed"); // NOI18N

        if (undoRedoManager != null) {
            undoRedoManager.discardAllEdits();
        }

        sendEventImmediately(
                new FormModelEvent(this, FormModelEvent.FORM_TO_BE_CLOSED));
    }

    /**
     * Fires an event informing about changing layout manager of a container. An
     * undoable edit is created and registered automatically.
     *
     * @param radCont container whose layout has been changed.
     * @param oldLayout old layout.
     * @param newLayout new layout.
     * @return event that has been fired.
     */
    public FormModelEvent fireContainerLayoutExchanged(
            RADVisualContainer<?> radCont,
            LayoutSupportDelegate oldLayout,
            LayoutSupportDelegate newLayout) {
        t("firing container layout exchange, container: " // NOI18N
                + (radCont != null ? radCont.getName() : "null")); // NOI18N

        FormModelEvent ev = new FormModelEvent(this, FormModelEvent.CONTAINER_LAYOUT_EXCHANGED);
        ev.setLayout(radCont, oldLayout, newLayout);
        sendEvent(ev);

        if (undoRedoRecording && radCont != null && oldLayout != newLayout) {
            addUndoableEdit(ev.getUndoableEdit());
        }

        return ev;
    }

    /**
     * Fires an event informing about changing a property of container layout.
     * An undoable edit is created and registered automatically.
     *
     * @param radCont container whose layout has been changed.
     * @param propName name of the layout property.
     * @param oldValue old value of the property.
     * @param newValue new value of the property.
     * @return event that has been fired.
     */
    public FormModelEvent fireContainerLayoutChanged(
            RADVisualContainer<?> radCont,
            String propName,
            Object oldValue,
            Object newValue) {
        t("firing container layout change, container: " // NOI18N
                + (radCont != null ? radCont.getName() : "null") // NOI18N
                + ", property: " + propName); // NOI18N

        FormModelEvent ev = new FormModelEvent(this, FormModelEvent.CONTAINER_LAYOUT_CHANGED);
        ev.setComponentAndContainer(radCont, radCont);
        ev.setProperty(propName, oldValue, newValue);
        sendEvent(ev);

        if (undoRedoRecording
                && radCont != null && (propName == null || oldValue != newValue)) {
            addUndoableEdit(ev.getUndoableEdit());
        }

        return ev;
    }

    /**
     * Fires an event informing about changing a property of component layout
     * constraints. An undoable edit is created and registered automatically.
     *
     * @param radComp component whose layout property has been changed.
     * @param propName name of the layout property.
     * @param oldValue old value of the property.
     * @param newValue new value of the property.
     * @return event that has been fired.
     */
    public FormModelEvent fireComponentLayoutChanged(
            RADVisualComponent<?> radComp,
            String propName,
            Object oldValue,
            Object newValue) {
        t("firing component layout change: " // NOI18N
                + (radComp != null ? radComp.getName() : "null")); // NOI18N

        FormModelEvent ev = new FormModelEvent(this, FormModelEvent.COMPONENT_LAYOUT_CHANGED);
        ev.setComponentAndContainer(radComp, null);
        ev.setProperty(propName, oldValue, newValue);
        sendEvent(ev);

        if (undoRedoRecording
                && radComp != null && propName != null && oldValue != newValue) {
            addUndoableEdit(ev.getUndoableEdit());
        }

        return ev;
    }

    /**
     * Fires an event informing about changing view of a column. An undoable
     * edit is created and registered automatically.
     *
     * @param aRadColumn column whose view has been changed.
     * @param oldView old view.
     * @param newView new view.
     * @return event that has been fired.
     */
    public FormModelEvent fireColumnViewExchanged(
            RADModelGridColumn aRadColumn,
            RADColumnView<? super ModelComponentDecorator> oldView,
            RADColumnView<? super ModelComponentDecorator> newView) {
        t("firing column view exchange, column: " // NOI18N
                + (aRadColumn != null ? aRadColumn.getName() : "null")); // NOI18N

        FormModelEvent ev = new FormModelEvent(this, FormModelEvent.COLUMN_VIEW_EXCHANGED);
        ev.setColumnView(aRadColumn, oldView, newView);
        sendEvent(ev);

        if (undoRedoRecording && aRadColumn != null && oldView != newView) {
            addUndoableEdit(ev.getUndoableEdit());
        }
        return ev;
    }

    /**
     * Fires an event informing about adding a component to the form. An
     * undoable edit is created and registered automatically.
     *
     * @param radComp component that has been added.
     * @param addedNew is newly added?
     * @return event that has been fired.
     */
    public FormModelEvent fireComponentAdded(RADComponent<?> radComp,
            boolean addedNew) {
        t("firing component added: " // NOI18N
                + (radComp != null ? radComp.getName() : "null")); // NOI18N

        if (radComp != null) {
            FormModelEvent ev = new FormModelEvent(this, FormModelEvent.COMPONENT_ADDED);
            ev.setAddData(radComp, radComp.getParent(), addedNew);
            sendEvent(ev);

            if (undoRedoRecording) {
                addUndoableEdit(ev.getUndoableEdit());
            }
            return ev;
        } else {
            return null;
        }
    }

    /**
     * Fires an event informing about removing a component from the form. An
     * undoable edit is created and registered automatically.
     *
     * @param radComp component that has been removed.
     * @param radCont container from which the component was removed.
     * @param index index of the component in the container.
     * @param removedFromModel determines whether the component has been removed
     * from the model.
     * @return event that has been fired.
     */
    public FormModelEvent fireComponentRemoved(RADComponent<?> radComp,
            ComponentContainer radCont,
            int index,
            boolean removedFromModel) {
        t("firing component removed: " // NOI18N
                + (radComp != null ? radComp.getName() : "null")); // NOI18N

        FormModelEvent ev = new FormModelEvent(this, FormModelEvent.COMPONENT_REMOVED);
        ev.setRemoveData(radComp, radCont, index, removedFromModel);
        sendEvent(ev);

        if (undoRedoRecording && radComp != null && radCont != null) {
            addUndoableEdit(ev.getUndoableEdit());
        }

        return ev;
    }

    /**
     * Fires an event informing about reordering components in a container. An
     * undoable edit is created and registered automatically.
     *
     * @param radCont container whose subcomponents has been reordered.
     * @param perm permutation describing the change in order.
     * @return event that has been fired.
     */
    public FormModelEvent fireComponentsReordered(ComponentContainer radCont,
            int[] perm) {
        t("firing components reorder in container: " // NOI18N
                + (radCont instanceof RADComponent
                        ? ((RADComponent) radCont).getName() : "<top>")); // NOI18N

        FormModelEvent ev = new FormModelEvent(this, FormModelEvent.COMPONENTS_REORDERED);
        ev.setComponentAndContainer(null, radCont);
        ev.setReordering(perm);
        sendEvent(ev);

        if (undoRedoRecording && radCont != null) {
            addUndoableEdit(ev.getUndoableEdit());
        }

        return ev;
    }

    /**
     * Fires an event informing about changing a property of a component. An
     * undoable edit is created and registered automatically.
     *
     * @param radComp component whose property has been changed.
     * @param propName name of the changed property.
     * @param oldValue old value of the property.
     * @param newValue new value of the property.
     * @return event that has been fired.
     */
    public FormModelEvent fireComponentPropertyChanged(RADComponent<?> radComp,
            String propName,
            Object oldValue,
            Object newValue) {
        t("firing component property change, component: " // NOI18N
                + (radComp != null ? radComp.getName() : "<null component>") // NOI18N
                + ", property: " + propName); // NOI18N

        FormModelEvent ev = new FormModelEvent(this, FormModelEvent.COMPONENT_PROPERTY_CHANGED);
        ev.setComponentAndContainer(radComp, null);
        ev.setProperty(propName, oldValue, newValue);
        sendEvent(ev);

        if (undoRedoRecording
                && radComp != null && propName != null && oldValue != newValue) {
            addUndoableEdit(ev.getUndoableEdit());
        }

        return ev;
    }

    /**
     * Fires an event informing about changing a property of a form. An undoable
     * edit is created and registered automatically.
     *
     * @param aFormRootNode
     * @param propName name of the changed property.
     * @param oldValue old value of the property.
     * @param newValue new value of the property.
     * @return event that has been fired.
     */
    public FormModelEvent fireFormPropertyChanged(FormRootNode aFormRootNode,
            String propName,
            Object oldValue,
            Object newValue) {
        t("firing form property change, property: " + propName); // NOI18N

        FormModelEvent ev = new FormModelEvent(this, FormModelEvent.FORM_PROPERTY_CHANGED);
        ev.setFormRootNode(aFormRootNode);
        ev.setProperty(propName, oldValue, newValue);
        sendEvent(ev);

        if (undoRedoRecording && propName != null && oldValue != newValue) {
            addUndoableEdit(ev.getUndoableEdit());
        }

        return ev;
    }

    /**
     * Fires an event informing about changing a synthetic property of a
     * component. An undoable edit is created and registered automatically.
     *
     * @param radComp component whose synthetic property has been changed.
     * @param propName name of the synthetic property that has been changed.
     * @param oldValue old value of the property.
     * @param newValue new value of the property.
     * @return event that has been fired.
     */
    public FormModelEvent fireSyntheticPropertyChanged(RADComponent<?> radComp,
            String propName,
            Object oldValue,
            Object newValue) {
        t("firing synthetic property change, component: " // NOI18N
                + (radComp != null ? radComp.getName() : "null") // NOI18N
                + ", property: " + propName); // NOI18N

        FormModelEvent ev = new FormModelEvent(this, FormModelEvent.SYNTHETIC_PROPERTY_CHANGED);
        ev.setComponentAndContainer(radComp, null);
        ev.setProperty(propName, oldValue, newValue);
        sendEvent(ev);

        if (undoRedoRecording && propName != null && oldValue != newValue) {
            addUndoableEdit(ev.getUndoableEdit());
        }

        return ev;
    }

    /**
     * Fires an event informing about renaming an event handler. An undoable
     * edit is created and registered automatically.
     *
     * @param aOldComponent
     * @param aNewComponent
     * @return event that has been fired.
     */
    public FormModelEvent fireTopDesignComponentChanged(RADVisualContainer aOldComponent, RADVisualContainer aNewComponent) {
        t("top design component changed: " + aOldComponent.getName() + " to " + aNewComponent.getName()); // NOI18N

        FormModelEvent ev = new FormModelEvent(this, FormModelEvent.TOP_DESIGN_COMPONENT_CHANGED);
        ev.setComponentAndContainer(aNewComponent, topRADComponent);
        ev.setProperty("topDesignComponent", aOldComponent, aNewComponent);
        sendEvent(ev);

        if (undoRedoRecording && aOldComponent != aNewComponent) {
            addUndoableEdit(ev.getUndoableEdit());
        }

        return ev;
    }

    /**
     * Fires an event informing about general form change.
     *
     * @param immediately determines whether the change should be fire
     * immediately.
     * @return event that has been fired.
     */
    public FormModelEvent fireFormChanged(boolean immediately) {
        t("firing form change"); // NOI18N

        FormModelEvent ev = new FormModelEvent(this, FormModelEvent.OTHER_CHANGE);
        if (immediately) {
            sendEventImmediately(ev);
        } else {
            sendEvent(ev);
        }

        return ev;
    }

    // ---------
    // firing methods for batch event processing
    private void sendEvent(FormModelEvent ev) {
        if (formLoaded) {
            if (eventList != null || ev.isModifying()) {
                sendEventLater(ev);
            } else {
                sendEventImmediately(ev);
            }
        } else {
            fireEvents(ev);
        }
    }

    private synchronized void sendEventLater(FormModelEvent ev) {
        // works properly only if called from AWT event dispatch thread
        if (!java.awt.EventQueue.isDispatchThread()) {
            sendEventImmediately(ev);
            return;
        }

        if (eventList == null) {
            eventList = new ArrayList<>();
            java.awt.EventQueue.invokeLater(() -> {
                firePendingEvents();
            });
        }
        eventList.add(ev);
    }

    private synchronized void sendEventImmediately(FormModelEvent ev) {
        if (eventList == null) {
            eventList = new ArrayList<>();
        }
        eventList.add(ev);
        firePendingEvents();
    }

    private void firePendingEvents() {
        List<FormModelEvent> list = pickUpEvents();
        if (list != null && !list.isEmpty()) {
            FormModelEvent[] events = new FormModelEvent[list.size()];
            list.toArray(events);
            fireEventBatch(events);
        }
    }

    private synchronized List<FormModelEvent> pickUpEvents() {
        List<FormModelEvent> list = eventList;
        eventList = null;
        return list;
    }

    boolean hasPendingEvents() {
        return eventList != null;
    }

    /**
     * This method fires events collected from all changes done during the last
     * round of AWT event queue. After all fired successfully (no error
     * occurred), all the changes are placed as one UndoableEdit into undo/redo
     * queue. When the fired events are being processed, some more changes may
     * happen (they are included in the same UndoableEdit). These changes are
     * typically fired immediately causing this method is re-entered while
     * previous firing is not finished yet. Additionally - for robustness, if
     * some unhandled error happens before or during firing the events, all the
     * changes done so far are undone: If an operation failed before firing, the
     * undoCompoundEdit field is set and then no events are fired at all (the
     * changes were defective), and the changes done before the failure are
     * undone. All the changes are undone also if the failure happens during
     * processing the events (e.g. the layout can't be built).
     */
    private void fireEventBatch(FormModelEvent... events) {
        if (!firing) {
            boolean firingFailed = false;
            try {
                firing = true;
                if (!undoCompoundEdit) {
                    firingFailed = true;
                    fireEvents(events);
                    firingFailed = false;
                }
            } finally {
                firing = false;
                boolean revert = undoCompoundEdit || firingFailed;
                undoCompoundEdit = false;
                CompoundEdit edit = endCompoundEdit(!revert);
                if (edit != null && revert) {
                    edit.undo();
                }
            }
        } else { // re-entrant call
            fireEvents(events);
        }
    }

    void fireEvents(FormModelEvent... events) {
        java.util.List<FormModelListener> targets = new ArrayList<>();
        synchronized (this) {
            if (listeners != null) {
                targets.addAll(listeners);
            }
        }
        for (FormModelEvent event : events) {
            if (event.getChangeType() != FormModelEvent.FORM_LOADED && event.getChangeType() != FormModelEvent.FORM_TO_BE_CLOSED && event.getChangeType() != FormModelEvent.FORM_TO_BE_SAVED) {
                modified = true;
            }
        }
        for (FormModelListener l : targets) {
            l.formChanged(events);
        }
    }

    // ---------------
    // ModelContainer inner class
    public final class ModelContainer implements ComponentContainer {

        @Override
        public RADComponent<?>[] getSubBeans() {
            return otherComponents.toArray(new RADComponent<?>[]{});
        }

        @Override
        public int getSubBeansCount() {
            return otherComponents.size();
        }

        @Override
        public void initSubComponents(RADComponent<?>[] initComponents) {
            otherComponents.clear();
            for (RADComponent<?> initComponent : initComponents) {
                add(initComponent);
            }
        }

        @Override
        public void reorderSubComponents(int[] perm) {
            RADComponent<?>[] components = new RADComponent<?>[otherComponents.size()];
            for (int i = 0; i < perm.length; i++) {
                components[perm[i]] = otherComponents.get(i);
            }
            otherComponents.clear();
            otherComponents.addAll(Arrays.asList(components));
        }

        @Override
        public void add(RADComponent<?> comp) {
            comp.setParent(null);
            otherComponents.add(comp);
        }

        @Override
        public void remove(RADComponent<?> comp) {
            if (otherComponents.remove(comp)) {
                comp.setParent(null);
            }
        }

        @Override
        public int getIndexOf(RADComponent<?> comp) {
            return otherComponents.indexOf(comp);
        }
    }
    // ---------------
    /**
     * For debugging purposes only.
     */
    static private int traceCount = 0;
    /**
     * For debugging purposes only.
     */
    static private final boolean TRACE = false;

    /**
     * For debugging purposes only.
     */
    static void t(String str) {
        if (TRACE) {
            if (str != null) {
                System.out.println("FormModel " + (++traceCount) + ": " + str); // NOI18N
            } else {
                System.out.println(""); // NOI18N
            }
        }
    }
}
