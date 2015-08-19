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

import com.eas.designer.explorer.PlatypusDataObject;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.UndoRedo;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;

/**
 * Form editor.
 *
 * @author Jan Stola
 */
public class FormEditor {

    public enum FormOperation {

        LOADING,
        SAVING
    }
    /**
     * The FormModel instance holding the form itself
     */
    private FormModel formModel;
    /**
     * The root node of form hierarchy presented in Component Inspector
     */
    private FormRootNode formRootNode;
    /**
     * List of exceptions occurred during the last persistence operation
     */
    private List<Throwable> persistenceErrors;
    /**
     * Persistence manager responsible for saving the form
     */
    private PersistenceManager persistenceManager = new PersistenceManager();
    /**
     * An indicator whether the form has been loaded (from the .form file)
     */
    private boolean formLoaded = false;
    /**
     * The DataObject of the form
     */
    private final PlatypusDataObject formDataObject;
    private PropertyChangeListener dataObjectListener;

    // -----
    FormEditor(PlatypusDataObject aDataObject) {
        formDataObject = aDataObject;
    }

    /**
     * @return root node representing the form (in pair with the class node)
     */
    public final FormRootNode getFormRootNode() {
        return formRootNode;
    }

    public final FormNode getOthersContainerNode() {
        FormNode othersNode = formRootNode.getOthersNode();
        return othersNode != null ? othersNode : formRootNode;
    }

    /**
     * @return the FormModel of this form, null if the form is not loaded
     */
    public final FormModel getFormModel() {
        return formModel;
    }

    public final PlatypusDataObject getFormDataObject() {
        return formDataObject;
    }

    /**
     * To be used just before loading a form to set a persistence manager that
     * already has the form recognized and superclass determined (i.e.
     * potentially long java parsing already done).
     */
    void setPersistenceManager(PersistenceManager aManager) {
        persistenceManager = aManager;
    }

    boolean isFormLoaded() {
        return formLoaded;
    }

    /**
     * This method performs the form data loading. All open/load methods go
     * through this one.
     *
     * @throws com.bearsoft.org.netbeans.modules.form.PersistenceException
     */
    public void loadForm() throws PersistenceException {
        if (!formLoaded) {
            resetPersistenceErrorLog(); // clear log of errors
            Logger.getLogger("TIMER").log(Level.FINE, "FormModel", new Object[]{formDataObject.getPrimaryFile(), formModel}); // NOI18N
            // load the form data (FormModel) and report errors
            try {
                formModel = persistenceManager.loadForm(formDataObject.getLookup().lookup(LayoutFileProvider.class).getLayoutFile(), formDataObject, persistenceErrors);
                formModel.setModified(false);
                // form is successfully loaded...
                formLoaded = true;
                formModel.fireFormLoaded();
                // create form nodes hierarchy and add it to SourceChildren
                formRootNode = new FormRootNode(formModel);
                formRootNode.getChildren().getNodes();
                formDataObject.getNodeDelegate().getChildren().add(new Node[]{formRootNode});

                attachDataObjectListener();
            } catch (Exception ex) {
                throw new PersistenceException(ex);
            }
        }
    }

    /**
     * Public method for saving form data to file. Does not save the source code
     * (document), does not report errors and does not throw any exceptions.
     *
     * @return whether there was not any fatal error during saving (true means
     * everything was ok); returns true even if nothing was saved because form
     * was not loaded or read-only, etc.
     */
    public boolean saveForm() {
        try {
            saveFormData();
            return true;
        } catch (PersistenceException ex) {
            logPersistenceError(ex, 0);
            return false;
        }
    }

    void saveFormData() throws PersistenceException {
        FileObject targetFile = formDataObject.getLookup().lookup(LayoutFileProvider.class).getLayoutFile();
        if (formLoaded && targetFile.canWrite() && !formModel.isReadOnly() && formModel.isModified()) {
            formModel.fireFormToBeSaved();
            resetPersistenceErrorLog();
            persistenceManager.saveForm(targetFile, this, persistenceErrors);
            formModel.setModified(false);
        }
    }

    private void resetPersistenceErrorLog() {
        if (persistenceErrors != null) {
            persistenceErrors.clear();
        } else {
            persistenceErrors = new ArrayList<>();
        }
    }

    private void logPersistenceError(Throwable t, int index) {
        if (persistenceErrors == null) {
            persistenceErrors = new ArrayList<>();
        }

        if (index < 0) {
            persistenceErrors.add(t);
        } else {
            persistenceErrors.add(index, t);
        }
    }

    boolean anyPersistenceError() {
        return persistenceErrors != null && !persistenceErrors.isEmpty();
    }

    /**
     * Reports errors occurred during loading or saving the form.
     *
     * @param operation operation being performed.
     */
    public void reportErrors(FormOperation operation) {
        if (anyPersistenceError()) {
            final ErrorManager errorManager = ErrorManager.getDefault();

            boolean checkLoadingErrors = operation == FormOperation.LOADING && formLoaded;
            boolean anyNonFatalLoadingError = false; // was there a real error?

            StringBuilder userErrorMsgs = new StringBuilder();

            for (Throwable t : persistenceErrors) {
                if (t instanceof PersistenceException) {
                    Throwable th = ((PersistenceException) t).getCause();
                    if (th != null) {
                        t = th;
                    }
                }

                if (checkLoadingErrors && !anyNonFatalLoadingError) {
                // was there a real loading error (not just warnings) causing
                    // some data not loaded?
                    ErrorManager.Annotation[] annotations
                            = errorManager.findAnnotations(t);
                    int severity = 0;
                    if ((annotations != null) && (annotations.length != 0)) {
                        for (int i = 0; i < annotations.length; i++) {
                            int s = annotations[i].getSeverity();
                            if (s == ErrorManager.UNKNOWN) {
                                s = ErrorManager.EXCEPTION;
                            }
                            if (s > severity) {
                                severity = s;
                            }
                        }
                    } else {
                        severity = ErrorManager.EXCEPTION;
                    }

                    if (severity > ErrorManager.WARNING) {
                        anyNonFatalLoadingError = true;
                    }
                }
                errorManager.notify(ErrorManager.INFORMATIONAL, t);
            }

            if (checkLoadingErrors && anyNonFatalLoadingError) {
            // the form was loaded with some non-fatal errors - some data
                // was not loaded - show a warning about possible data loss
                final String wholeMsg = userErrorMsgs.append(
                        FormUtils.getBundleString("MSG_FormLoadedWithErrors")).toString();  // NOI18N

                java.awt.EventQueue.invokeLater(() -> {
                // for some reason this would be displayed before the
                    // ErrorManager if not invoked later
                    if (isFormLoaded()) {// issue #164444
                        JButton viewOnly = new JButton(FormUtils.getBundleString("CTL_ViewOnly"));		// NOI18N
                        JButton allowEditing = new JButton(FormUtils.getBundleString("CTL_AllowEditing"));	// NOI18N

                        Object ret = DialogDisplayer.getDefault().notify(new NotifyDescriptor(
                                wholeMsg,
                                FormUtils.getBundleString("CTL_FormLoadedWithErrors"), // NOI18N
                                NotifyDescriptor.DEFAULT_OPTION,
                                NotifyDescriptor.WARNING_MESSAGE,
                                new Object[]{viewOnly, allowEditing, NotifyDescriptor.CANCEL_OPTION},
                                viewOnly));

                        if (ret == viewOnly) {
                            setFormReadOnly();
                        } else if (ret == allowEditing) {
                            destroyInvalidComponents();
                        } else { // close form
                            closeForm();
                        }
                    }
                });
            }
            resetPersistenceErrorLog();
        }
    }

    /**
     * Destroys all components from {@link #formModel} tageted as invalid
     */
    private void destroyInvalidComponents() {
        Collection<RADComponent<?>> allComps = formModel.getAllComponents();
        List<RADComponent<?>> invalidComponents = new ArrayList<>(allComps.size());
        // collect all invalid components
        for (RADComponent<?> comp : allComps) {
            if (!comp.isValid()) {
                invalidComponents.add(comp);
            }
        }
        // destroy all invalid components
        for (RADComponent<?> comp : invalidComponents) {
            try {
                RADComponentNode node = comp.getNodeReference();
                if (node != null) {
                    node.destroy();
                }
            } catch (java.io.IOException ex) { // should not happen
                ErrorManager.getDefault().notify(ex);
            }
        }
    }

    /**
     * Sets the FormEditor in Read-Only mode
     */
    private void setFormReadOnly() {
        formModel.setReadOnly(true);
        //getFormDataObject().getLookup().lookup(PlatypusFormSupport.class).updateTitles();
    }

    /**
     * Closes the form. Used when closing the form editor or reloading the form.
     */
    void closeForm() {
        if (formLoaded) {
            formModel.fireFormToBeClosed();

            //openForms.remove(formModel);
            formLoaded = false;

            // remove nodes hierarchy
            if (formDataObject.isValid()) {
                // Avoiding deadlock (issue 51796)
                java.awt.EventQueue.invokeLater(() -> {
                    if (formDataObject.isValid()) {
                        formDataObject.getNodeDelegate().getChildren().remove(new Node[]{formRootNode});
                    }
                    formRootNode = null;
                });
            }

            // remove listeners
            detachDataObjectListener();

            // reset references
            persistenceManager = null;
            persistenceErrors = null;
            formModel = null;
        }
    }

    private void attachDataObjectListener() {
        if (dataObjectListener == null) {
            dataObjectListener = (PropertyChangeEvent ev) -> {
                switch (ev.getPropertyName()) {
                    case DataObject.PROP_NAME:
                        // PlatypusFormDataObject's name has changed
                        String name = formDataObject.getName();
                        formModel.setName(name);
                        formRootNode.updateName(name);
                        // multiview updated by FormEditorSupport
                        // code regenerated by FormRefactoringUpdate
                        break;
                    case DataObject.PROP_COOKIE:
                        break;
                }
            };
            formDataObject.addPropertyChangeListener(dataObjectListener);
        }
    }

    private void detachDataObjectListener() {
        if (dataObjectListener != null) {
            formDataObject.removePropertyChangeListener(dataObjectListener);
            dataObjectListener = null;
        }
    }

    /**
     * Returns code editor pane for the specified form.
     *
     * @param formModel form model.
     * @return JEditorPane set up with the actuall forms java source
    public static JEditorPane createCodeEditorPane(FormModel formModel) {
        PlatypusFormDataObject dobj = formModel.getDataObject();
        JEditorPane codePane = new JEditorPane();
        FormUtils.setupEditorPane(codePane, dobj.getPrimaryFile(), 0);
        return codePane;
    }
     */

    UndoRedo.Manager getFormUndoRedoManager() {
        return formModel != null ? formModel.getUndoRedoManager() : null;
    }
}
