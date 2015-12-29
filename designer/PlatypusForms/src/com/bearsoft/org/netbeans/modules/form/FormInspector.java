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

import com.bearsoft.org.netbeans.modules.form.actions.TestAction;
import com.bearsoft.org.netbeans.modules.form.palette.PaletteUtils;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.beans.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.openide.*;
import org.openide.actions.CopyAction;
import org.openide.actions.CutAction;
import org.openide.actions.DeleteAction;
import org.openide.actions.PasteAction;
import org.openide.awt.UndoRedo;
import org.openide.explorer.*;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.*;
import org.openide.util.*;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.*;
import org.openide.windows.*;

/**
 * The FormInspector - special explorer for form editor.
 *
 * @author Tomas Pavek
 */
public class FormInspector extends TopComponent implements ExplorerManager.Provider {

    private final ExplorerManager explorerManager;
    private final TestAction testAction = SystemAction.findObject(TestAction.class, true);
    private final CopyCutActionPerformer copyActionPerformer = new CopyCutActionPerformer(true);
    private final CopyCutActionPerformer cutActionPerformer = new CopyCutActionPerformer(false);
    private final DeleteActionPerformer deleteActionPerformer = new DeleteActionPerformer();
    private final PasteActionPerformer pasteActionPerformer = new PasteActionPerformer();
    /**
     * Currently focused form or null if no form is opened/focused
     */
    private PlatypusFormLayoutView focusedFormView;
    private final EmptyInspectorNode emptyInspectorNode;
    private BeanTreeView treeView;
    /**
     * Default icon base for control panel.
     */
    private static final String EMPTY_INSPECTOR_ICON_BASE =
            "com/bearsoft/org/netbeans/modules/form/resources/emptyInspector.gif"; // NOI18N
    /**
     * The icon for FormInspector
     */
    private static final String iconURL =
            "com/bearsoft/org/netbeans/modules/form/resources/inspector.png"; // NOI18N
    private static FormInspector instance;

    // ------------
    // construction (FormInspector is a singleton)
    /**
     * Gets default instance. Don't use directly, it reserved for '.settings'
     * file only, i.e. deserialization routines, otherwise you can get
     * non-deserialized instance.
     *
     * @return FormInspector singleton.
     */
    public static synchronized FormInspector getDefault() {
        if (instance == null) {
            instance = new FormInspector();
        }
        return instance;
    }

    /**
     * Finds default instance. Use in client code instead of
     * {@link #getDefault()}.
     *
     * @return FormInspector singleton.
     */
    public static synchronized FormInspector getInstance() {
        if (instance == null) {
            TopComponent tc = WindowManager.getDefault().findTopComponent("PlatypusFormInspector"); // NOI18N
            if (instance == null) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, new IllegalStateException(
                        "Can not find PlatypusFormInspector component for its ID. Returned " + tc)); // NOI18N
                instance = new FormInspector();
            }
        }
        return instance;
    }

    static boolean exists() {
        return instance != null;
    }

    /**
     * Overriden to explicitely set persistence type of FormInspector to
     * PERSISTENCE_ALWAYS
     */
    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    private FormInspector() {
        explorerManager = new ExplorerManager();
        associateLookup(ExplorerUtils.createLookup(explorerManager, setupActionMap(getActionMap())));
        emptyInspectorNode = new EmptyInspectorNode();
        explorerManager.setRootContext(emptyInspectorNode);
        explorerManager.addPropertyChangeListener(new NodeSelectionListener());
        setLayout(new java.awt.BorderLayout());
        createComponents();
        setIcon(ImageUtilities.loadImage(iconURL));
        setName(FormUtils.getBundleString("CTL_InspectorTitle")); // NOI18N
        setToolTipText(FormUtils.getBundleString("HINT_ComponentInspector")); // NOI18N
    }

    final javax.swing.ActionMap setupActionMap(javax.swing.ActionMap map) {
        map.put(SystemAction.get(CopyAction.class).getActionMapKey(), copyActionPerformer);
        map.put(SystemAction.get(CutAction.class).getActionMapKey(), cutActionPerformer);
        map.put(SystemAction.get(DeleteAction.class).getActionMapKey(), deleteActionPerformer); // NOI18N
        map.put(SystemAction.get(PasteAction.class).getActionMapKey(), pasteActionPerformer);
        return map;
    }

    private void createComponents() {
        treeView = new BeanTreeView();
        treeView.setDragSource(true);
        treeView.setDropTarget(true);
        treeView.getAccessibleContext().setAccessibleName(
                FormUtils.getBundleString("ACS_ComponentTree")); // NOI18N
        treeView.getAccessibleContext().setAccessibleDescription(
                FormUtils.getBundleString("ACSD_ComponentTree")); // NOI18N
        add(java.awt.BorderLayout.CENTER, treeView);
    }

    // --------------
    // overriding superclasses, implementing interfaces
    // ExplorerManager.Provider
    @Override
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    @Override
    public UndoRedo getUndoRedo() {
        UndoRedo ur = focusedFormView != null
                ? focusedFormView.getUndoRedo() : null;
        return ur != null ? ur : super.getUndoRedo();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx("gui.component-inspector"); // NOI18N
    }

    /**
     * Replaces this in object stream.
     *
     * @return ResolvableHelper
     */
    @Override
    public Object writeReplace() {
        return new ResolvableHelper();
    }

    @Override
    protected void componentActivated() {
        attachActions();
    }

    @Override
    protected void componentDeactivated() {
        detachActions();
    }

    // ------------
    // activating and focusing
    synchronized void attachActions() {
        ExplorerUtils.activateActions(explorerManager, true);
    }

    synchronized void detachActions() {
        ExplorerUtils.activateActions(explorerManager, false);
    }

    /**
     * This method focuses the FormInspector on given form.
     *
     * @param form the form to focus on
     */
    public void focusForm(final PlatypusFormLayoutView form) {
        if (focusedFormView != form) {
            focusFormInAwtThread(form, 0);
        }
    }

    /**
     * This method focuses the FormInspector on given form.
     *
     * @param form the form to focus on
     * @param visible true to open inspector, false to close
     */
    public void focusForm(final PlatypusFormLayoutView form, boolean visible) {
        if (focusedFormView != form) {
            focusFormInAwtThread(form, visible ? 1 : -1);
        }
    }

    private void focusFormInAwtThread(final PlatypusFormLayoutView form,
            final int visibility) {
        if (java.awt.EventQueue.isDispatchThread()) {
            focusFormImpl(form, visibility);
        } else {
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    focusFormImpl(form, visibility);
                }
            });
        }
    }

    private void focusFormImpl(PlatypusFormLayoutView aFormView, int visibility) {
        focusedFormView = aFormView;

        if (aFormView == null) {
            testAction.setFormDesigner(null);
            PaletteUtils.setContext(null);

            // swing memory leak workaround
            removeAll();
            createComponents();
            revalidate();

            getExplorerManager().setRootContext(emptyInspectorNode);
        } else {
            Node[] selectedNodes = aFormView.getSelectedComponentNodes();

            testAction.setFormDesigner(aFormView);
            PaletteUtils.setContext(aFormView.getFormModel().getDataObject().getPrimaryFile());

            Node formNode = aFormView.getFormEditor().getFormRootNode();
            if (formNode == null) { // form not loaded yet, should not happen
                System.err.println("Warning: FormEditorSupport.getFormRootNode() returns null"); // NOI18N
                getExplorerManager().setRootContext(emptyInspectorNode);
            } else {
                getExplorerManager().setRootContext(formNode);
            }

            try {
                getExplorerManager().setSelectedNodes(selectedNodes);
            } catch (PropertyVetoException ex) {
                ErrorManager.getDefault().notify(ex);   // should not happen
            }
        }
        if (visibility > 0) {
            open();
        } else if (visibility < 0) {
            close();
        }
    }

    public PlatypusFormLayoutView getFocusedForm() {
        return focusedFormView;
    }

    /**
     * Called to synchronize with PlatypusFormLayoutView. Invokes
     * NodeSelectionListener.
     */
    void setSelectedNodes(Node[] nodes, PlatypusFormLayoutView form)
            throws PropertyVetoException {
        if (form == focusedFormView) {
            getExplorerManager().setSelectedNodes(nodes);
        }
    }

    public Node[] getSelectedNodes() {
        return getExplorerManager().getSelectedNodes();
    }

    private Node[] getSelectedRootNodes() {
        // exclude nodes that are under other selected nodes
        Node[] selected = getExplorerManager().getSelectedNodes();
        if (selected != null && selected.length > 1) {
            List<Node> list = new ArrayList<>(selected.length);
            for (int i = 0; i < selected.length; i++) {
                Node node = selected[i];
                boolean subcontained = false;
                for (int j = 0; j < selected.length; j++) {
                    if (j != i && isSubcontainedNode(node, selected[j])) {
                        subcontained = true;
                        break;
                    }
                }
                if (!subcontained) {
                    list.add(node);
                }
            }
            if (list.size() < selected.length) {
                selected = list.toArray(new Node[list.size()]);
            }
        }
        return selected;
    }

    private static boolean isSubcontainedNode(Node node, Node maybeParent) {
        RADComponentCookie cookie = node.getLookup().lookup(RADComponentCookie.class);
        RADComponent<?> comp = (cookie != null) ? cookie.getRADComponent() : null;
        if (comp != null) {
            cookie = maybeParent.getLookup().lookup(RADComponentCookie.class);
            RADComponent<?> parentComp = (cookie != null) ? cookie.getRADComponent() : null;
            if (parentComp != null && parentComp.isParentComponent(comp)) {
                return true;
            }
        }
        return false;
    }

    private Clipboard getClipboard() {
        Clipboard c = Lookup.getDefault().lookup(java.awt.datatransfer.Clipboard.class);
        if (c == null) {
            c = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
        }
        return c;
    }

    @Override
    protected String preferredID() {
        return getClass().getSimpleName();
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean requestFocusInWindow() {
        super.requestFocusInWindow();
        return treeView.requestFocusInWindow();
    }

    // ---------------
    // innerclasses
    // listener on nodes selection (ExplorerManager)
    private class NodeSelectionListener implements PropertyChangeListener {

        NodeSelectionListener() {
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName()) && focusedFormView != null) {
                Node[] selectedNodes = getExplorerManager().getSelectedNodes();
                if (evt.getSource() == FormInspector.this.getExplorerManager()) {   // the change comes from FormInspector => synchronize PlatypusFormLayoutView
                    focusedFormView.clearSelectionImpl();
                    for (int i = 0; i < selectedNodes.length; i++) {
                        FormCookie formCookie = selectedNodes[i].getLookup().lookup(FormCookie.class);
                        if (formCookie != null) {
                            FormNode node = formCookie.getOriginalNode();
                            if (node instanceof RADComponentNode) {
                                focusedFormView.addComponentToSelectionImpl(((RADComponentNode) node).getRADComponent());
                            }
                        }
                    }
                    focusedFormView.repaintSelection();
                    focusedFormView.setActivatedNodes(selectedNodes);
                }
                setActivatedNodes(selectedNodes);
            }
        }
    }

    private class PasteActionPerformer extends javax.swing.AbstractAction {

        protected PasteType[] gatherPasteTypes() {
            Node[] selected = getExplorerManager().getSelectedNodes();
            if (selected != null && selected.length >= 1) {
                // pasting considered only on the first selected node
                Clipboard clipboard = getClipboard();
                Transferable trans = clipboard.getContents(this); // [this??]
                if (trans != null) {
                    Node node = selected[0];
                    return node.getPasteTypes(trans);
                }
            }
            return new PasteType[]{};
        }

        @Override
        public boolean isEnabled() {
            PasteType[] pasteTypes = gatherPasteTypes();
            return pasteTypes != null && pasteTypes.length > 0;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            PasteType[] pasteTypes = gatherPasteTypes();
            for (PasteType pt : pasteTypes) {
                try {
                    pt.paste();
                } catch (IOException ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            }
        }
    }

    // performer for DeleteAction
    private class DeleteActionPerformer extends javax.swing.AbstractAction
            implements Mutex.Action<Object> {

        private Node[] nodesToDestroy;

        @Override
        public void actionPerformed(ActionEvent e) {
            Node[] selected = getSelectedRootNodes();

            if (selected == null || selected.length == 0) {
                return;
            }

            for (int i = 0; i < selected.length; i++) {
                if (!selected[i].canDestroy()) {
                    return;
                }
            }

            try { // clear nodes selection first
                getExplorerManager().setSelectedNodes(new Node[0]);
            } catch (PropertyVetoException ex) {
                // cannot be vetoed
            }

            nodesToDestroy = selected;
            if (java.awt.EventQueue.isDispatchThread()) {
                doDelete();
            } else // reinvoke synchronously in AWT thread
            {
                Mutex.EVENT.readAccess(this);
            }
        }

        @Override
        public Object run() {
            doDelete();
            return null;
        }

        private void doDelete() {
            if (nodesToDestroy != null) {
                for (int i = 0; i < nodesToDestroy.length; i++) {
                    try {
                        nodesToDestroy[i].destroy();
                    } catch (java.io.IOException ex) { // should not happen
                        ErrorManager.getDefault().notify(ex);
                    }
                }
                nodesToDestroy = null;
            }
        }
    }

    // performer for CopyAction and CutAction
    private class CopyCutActionPerformer extends javax.swing.AbstractAction {

        private boolean copy;

        public CopyCutActionPerformer(boolean isCopy) {
            super();
            copy = isCopy;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Transferable trans;
            Node[] selected = getSelectedRootNodes();

            if (selected == null || selected.length == 0) {
                trans = null;
            } else if (selected.length == 1) {
                trans = getTransferableOwner(selected[0]);
            } else {
                Transferable[] transArray = new Transferable[selected.length];
                for (int i = 0; i < selected.length; i++) {
                    if ((transArray[i] = getTransferableOwner(selected[i]))
                            == null) {
                        return;
                    }
                }

                trans = new ExTransferable.Multi(transArray);
            }

            if (trans != null) {
                Clipboard clipboard = getClipboard();
                clipboard.setContents(trans, new StringSelection("")); // NOI18N
            }
        }

        private Transferable getTransferableOwner(Node node) {
            try {
                return copy ? node.clipboardCopy() : node.clipboardCut();
            } catch (java.io.IOException e) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                return null;
            }
        }
    }
    /*
     // paste type used for ExTransferable.Multi
     private static class MultiPasteType extends PasteType implements Mutex.ExceptionAction<Transferable> {

     private Transferable[] transIn;
     private PasteType[] pasteTypes;

     MultiPasteType(Transferable[] t, PasteType[] p) {
     transIn = t;
     pasteTypes = p;
     }

     // performs the paste action
     @Override
     public Transferable paste() throws java.io.IOException {
     if (java.awt.EventQueue.isDispatchThread()) {
     return doPaste();
     } else { // reinvoke synchronously in AWT thread
     try {
     return Mutex.EVENT.readAccess(this);
     } catch (MutexException ex) {
     Exception e = ex.getException();
     if (e instanceof java.io.IOException) {
     throw (java.io.IOException) e;
     } else { // should not happen, ignore
     ErrorManager.getDefault().notify(e);
     return ExTransferable.EMPTY;
     }
     }
     }
     }

     @Override
     public Transferable run() throws Exception {
     return doPaste();
     }

     private Transferable doPaste() throws java.io.IOException {
     Transferable[] transOut = new Transferable[transIn.length];
     for (int i = 0; i < pasteTypes.length; i++) {
     Transferable newTrans = pasteTypes[i].paste();
     transOut[i] = newTrans != null ? newTrans : transIn[i];
     }
     return new ExTransferable.Multi(transOut);
     }
     }
     */
    // -----------
    // node for empty FormInspector

    private static class EmptyInspectorNode extends AbstractNode {

        public EmptyInspectorNode() {
            super(Children.LEAF);
            setIconBaseWithExtension(EMPTY_INSPECTOR_ICON_BASE);
        }

        @Override
        public boolean canRename() {
            return false;
        }
    }

    final public static class ResolvableHelper implements java.io.Serializable {

        static final long serialVersionUID = 7424646018839457544L;

        public Object readResolve() {
            return FormInspector.getDefault();
        }
    }
}
