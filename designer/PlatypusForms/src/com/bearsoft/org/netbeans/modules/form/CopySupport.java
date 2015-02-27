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

import com.bearsoft.org.netbeans.modules.form.bound.RADModelGridColumn;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.openide.ErrorManager;
import org.openide.nodes.*;
import org.openide.util.Mutex;
import org.openide.util.MutexException;
import org.openide.util.datatransfer.ExTransferable;
import org.openide.util.datatransfer.MultiTransferObject;
import org.openide.util.datatransfer.PasteType;

/**
 * Support class for copy/cut/paste operations in form editor.
 *
 * @author Tomas Pavek
 */
class CopySupport {

    private static final String flavorMimeType
            = "application/x-form-radcomponent;class=java.lang.Object"; // NOI18N
    private static DataFlavor copyFlavor;
    private static DataFlavor cutFlavor;

    static DataFlavor getComponentCopyFlavor() {
        if (copyFlavor == null) {
            copyFlavor = new DataFlavor(flavorMimeType,
                    "COMPONENT_COPY_FLAVOR"); // NOI18N
        }
        return copyFlavor;
    }

    static DataFlavor getComponentCutFlavor() {
        if (cutFlavor == null) {
            cutFlavor = new DataFlavor(flavorMimeType,
                    "COMPONENT_CUT_FLAVOR"); // NOI18N
        }
        return cutFlavor;
    }

    // -----------
    static class RADTransferable implements Transferable {

        private RADComponent<?> radComponent;
        private DataFlavor[] flavors;

        RADTransferable(DataFlavor flavor, RADComponent<?> radComponent) {
            this(new DataFlavor[]{flavor}, radComponent);
        }

        RADTransferable(DataFlavor[] flavors, RADComponent<?> radComponent) {
            this.flavors = flavors;
            this.radComponent = radComponent;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            for (int i = 0; i < flavors.length; i++) {
                if (flavors[i] == flavor) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException, IOException {
            if ("x-form-radcomponent".equals(flavor.getSubType())) // NOI18N
            {
                return radComponent;
            }

            throw new UnsupportedFlavorException(flavor);
        }
    }

    // -----------
    /**
     * Checks whether a component can be moved to a container (the component
     * cannot be pasted to its own sub-container or even to itself).
     */
    public static boolean canPasteCut(RADComponent<?> sourceComponent,
            FormModel targetForm,
            RADComponent<?> targetComponent) {
        if (!sourceComponent.isInModel()) {
            return false;
        }
        if (sourceComponent.getFormModel() != targetForm) {
            return true;
        }

        if (targetComponent == null) {
            return !(sourceComponent instanceof RADModelGridColumn) && targetForm.getModelContainer().getIndexOf(sourceComponent) < 0;
        }

        return sourceComponent != targetComponent
                && sourceComponent.getParentComponent() != targetComponent
                && !sourceComponent.isParentComponent(targetComponent);
    }

    // -----------
    static void createPasteTypes(Transferable trans, java.util.List<PasteType> s,
            FormModel aFormModel, RADComponent<?> targetComponent) {
        if (!aFormModel.isReadOnly()) {
            Transferable[] allTrans;
            if (trans.isDataFlavorSupported(ExTransferable.multiFlavor)) {
                try {
                    MultiTransferObject transObj = (MultiTransferObject) trans.getTransferData(ExTransferable.multiFlavor);
                    allTrans = new Transferable[transObj.getCount()];
                    for (int i = 0; i < allTrans.length; i++) {
                        allTrans[i] = transObj.getTransferableAt(i);
                    }
                } catch (UnsupportedFlavorException | IOException ex) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
                    return;
                }
            } else {
                allTrans = new Transferable[]{trans};
            }

            boolean canPaste = false;
            boolean cut = false; // true - cut, false - copy
            List<RADComponent<?>> sourceComponents = null;

            for (int i = 0; i < allTrans.length; i++) {
                Transferable t = allTrans[i];
                boolean radCompTransfer;
                if (t.isDataFlavorSupported(getComponentCopyFlavor())) {
                    assert !cut;
                    radCompTransfer = true;
                } else if (t.isDataFlavorSupported(getComponentCutFlavor())) {
                    assert cut || sourceComponents == null;
                    radCompTransfer = true;
                    cut = true;
                } else {
                    radCompTransfer = false;
                }
                if (radCompTransfer) {
                    RADComponent<?> transComp = null;
                    try {
                        Object data = t.getTransferData(t.getTransferDataFlavors()[0]);
                        if (data instanceof RADComponent<?>) {
                            transComp = (RADComponent<?>) data;
                        }
                    } catch (UnsupportedFlavorException | java.io.IOException e) {
                    } // should not happen

                    if (transComp != null
                            // only cut to another container
                            && (!cut || canPasteCut(transComp, aFormModel, targetComponent))
                            // must be a valid source/target combination
                            && (RADComponentCreator.canAddComponent(transComp.getBeanClass(), targetComponent)
                            || (!cut && RADComponentCreator.canApplyComponent(transComp.getBeanClass(), targetComponent)))) {   // pasting this meta component is allowed
                        RADComponent<?> copied = getComponentToCopy(transComp, targetComponent, cut);
                        if (copied != targetComponent) {
                            if (sourceComponents == null) {
                                sourceComponents = new ArrayList<>();
                            }
                            sourceComponents.add(copied);
                            canPaste = true;
                        }
                    }
                }
            }

            if (sourceComponents != null) {
                s.add(new RADPaste(sourceComponents, aFormModel, targetComponent, cut));
            }

            if (!canPaste && targetComponent != null
//                    && (!(targetComponent instanceof ComponentContainer) || RADComponentCreator.isTransparentLayoutComponent(targetComponent))
                    && targetComponent.getParentComponent() != null) {
                // allow paste on non-container component - try its parent
                createPasteTypes(trans, s, aFormModel, targetComponent.getParentComponent());
            }
        }
    }

    private static RADComponent<?> getComponentToCopy(RADComponent<?> radComp, RADComponent<?> targetComp, boolean cut) {
        RADComponent<?> parent = radComp.getParentComponent();
        if (RADComponentCreator.isTransparentLayoutComponent(parent)
                && (!cut || parent.getParentComponent() != targetComp)) {
            return parent;
        }
        return radComp;
    }

    /**
     * Paste type for meta components.
     */
    private static class RADPaste extends PasteType implements Mutex.ExceptionAction<Transferable> {

        private List<RADComponent<?>> sourceComponents;
        private FormModel targetForm;
        private RADComponent<?> targetComponent;
        private boolean fromCut;

        RADPaste(List<RADComponent<?>> sourceComponents,
                FormModel targetForm, RADComponent<?> targetComponent,
                boolean cut) {
            this.sourceComponents = sourceComponents;
            this.targetForm = targetForm;
            this.targetComponent = targetComponent;
            this.fromCut = cut;
        }

        @Override
        public String getName() {
            return FormUtils.getBundleString(fromCut ? "CTL_CutPaste" : "CTL_CopyPaste"); // NOI18N
        }

        @Override
        public Transferable paste() throws IOException {
            if (java.awt.EventQueue.isDispatchThread()) {
                try {
                    return doPaste();
                } catch (Exception ex) {
                    throw new IOException(ex);
                }
            } else { // reinvoke synchronously in AWT thread
                try {
                    return Mutex.EVENT.readAccess(this);
                } catch (MutexException ex) {
                    Exception e = ex.getException();
                    if (e instanceof IOException) {
                        throw (IOException) e;
                    } else { // should not happen, ignore
                        ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                        return ExTransferable.EMPTY;
                    }
                }
            }
        }

        @Override
        public Transferable run() throws Exception {
            return doPaste();
        }

        private Transferable doPaste() throws IOException, Exception {
            if (sourceComponents != null && !sourceComponents.isEmpty()) {
                FormModel sourceForm = sourceComponents.get(0).getFormModel();
                boolean move = fromCut && sourceForm == targetForm;
                boolean autoUndo = true; // in case of unexpected error, for robustness
                List<RADComponent<?>> copiedComponents = null; // only for new-to-new layout copy
                try {
                    // copy or move the components
                    for (RADComponent<?> sourceComp : sourceComponents) {
                        if (!move) {
                            RADComponent<?> copiedComp = targetForm.getComponentCreator().copyComponent(sourceComp, targetComponent);
                            if (copiedComp == null) {
                                return null; // copy failed...
                            }
                        } else { // move within the same form
                            targetForm.getComponentCreator().moveComponent(sourceComp, targetComponent);
                        }
                    }
                    autoUndo = false;
                } finally {
                    if (autoUndo) {
                        targetForm.forceUndoOfCompoundEdit();
                        // [don't expect problems in source form...]
                    }
                }

                // remove components if cut from another form (the components have been copied)
                if (fromCut && sourceForm != targetForm) {
                    for (RADComponent<?> sourceComp : sourceComponents) {
                        Node sourceNode = sourceComp.getNodeReference();
                        if (sourceNode != null) {
                            sourceNode.destroy();
                        }
                    }
                }

                // return Transferable object for the next paste operation
                if (fromCut) { // cut - can't be pasted again
                    return ExTransferable.EMPTY;
                } else if (copiedComponents != null) {
                    // make the newly copied components the source for the next paste
                    if (copiedComponents.size() == 1) {
                        return new RADTransferable(getComponentCopyFlavor(), copiedComponents.get(0));
                    } else {
                        Transferable[] trans = new Transferable[copiedComponents.size()];
                        int i = 0;
                        for (RADComponent<?> comp : copiedComponents) {
                            trans[i++] = new RADTransferable(getComponentCopyFlavor(), comp);
                        }
                        return new ExTransferable.Multi(trans);
                    }
                } else { // keep the original clipboard content
                    return null;
                }
                // TODO: menu components edge cases
            } else {
                return null;
            }
        }
    }
}
