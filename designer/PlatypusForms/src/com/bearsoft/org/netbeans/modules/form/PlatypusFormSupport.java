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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

import com.eas.designer.application.module.PlatypusModuleDataObject;
import com.eas.designer.application.module.PlatypusModuleDatamodelDescription;
import com.eas.designer.application.module.PlatypusModuleSourceDescription;
import com.eas.designer.application.module.PlatypusModuleSupport;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.text.Document;
import javax.swing.text.Position;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.guards.GuardedSectionManager;
import org.netbeans.core.api.multiview.MultiViewHandler;
import org.netbeans.core.api.multiview.MultiViews;
import org.netbeans.core.spi.multiview.*;
import org.netbeans.spi.editor.guards.GuardedEditorSupport;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.awt.UndoRedo;
import org.openide.cookies.CloseCookie;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.OpenCookie;
import org.openide.cookies.PrintCookie;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.text.*;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.UserQuestionException;
import org.openide.windows.*;

/**
 *
 * @author Ian Formanek, Tomas Pavek
 */
public class PlatypusFormSupport extends PlatypusModuleSupport implements EditorCookie.Observable, CloseCookie, PrintCookie {

    /**
     * ID of the form designer (in the multiview)
     */
    private static final String MV_FORM_ID = "form"; //NOI18N
    private static final int JS_ELEMENT_INDEX = 0;
    private static final int FORM_ELEMENT_INDEX = 1;
    private static final int MODEL_ELEMENT_INDEX = 2;
    /**
     * Icon for the form editor multiview window
     */
    public static final String iconURL =
            "com/bearsoft/org/netbeans/modules/form/resources/form.gif"; // NOI18N
    private UndoRedo.Manager editorUndoManager;
    private FormEditor formEditor;

    // --------------
    // constructor
    public PlatypusFormSupport(PlatypusFormDataObject formDataObject) {
        super(formDataObject);
        setMIMEType("text/javascript"); // NOI18N
        dataObject = formDataObject;
    }

    // ----------
    // opening & saving interface methods
    /**
     * Main entry method. Called by OpenCookie implementation - opens the form.
     *
     * @param forceFormElement determines whether we should force switch to form
     * element.
     * @see OpenCookie#open
     */
    public void openFormEditor(boolean forceFormElement) {
        long ms = System.currentTimeMillis();
        try {
            showOpeningStatus("FMT_PreparingForm"); // NOI18N

            CloneableTopComponent multiviewTC = openCloneableTopComponent();
            multiviewTC.requestActive();

            if (forceFormElement) {
                MultiViewHandler handler = MultiViews.findMultiViewHandler(multiviewTC);
                handler.requestActive(handler.getPerspectives()[FORM_ELEMENT_INDEX]);
            }
        } finally {
            hideOpeningStatus();
        }
        Logger.getLogger(FormEditor.class.getName()).log(Level.FINER, "Opening form time 1: {0}ms", (System.currentTimeMillis() - ms)); // NOI18N
    }

    void showOpeningStatus(String fmtMessage) {
        JFrame mainWin = (JFrame) WindowManager.getDefault().getMainWindow();

        // set wait cursor
        mainWin.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        mainWin.getGlassPane().setVisible(true);

        // set status text like "Opening form: ..."
        StatusDisplayer.getDefault().setStatusText(
                FormUtils.getFormattedBundleString(
                fmtMessage, // NOI18N
                new Object[]{dataObject.getPrimaryFile().getName()}));
        javax.swing.RepaintManager.currentManager(mainWin).paintDirtyRegions();
    }

    void hideOpeningStatus() {
        // clear wait cursor
        JFrame mainWin = (JFrame) WindowManager.getDefault().getMainWindow();
        mainWin.getGlassPane().setVisible(false);
        mainWin.getGlassPane().setCursor(null);

        StatusDisplayer.getDefault().setStatusText(""); // NOI18N
    }

    @Override
    protected boolean asynchronousOpen() {
        return false;
    }

    /**
     * Overriden from JavaEditor - opens editor and ensures it is selected in
     * the multiview.
     */
    @Override
    public void open() {
        if (EventQueue.isDispatchThread()) {
            openInAWT();
        } else {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    openInAWT();
                }
            });
        }
    }

    private void openInAWT() {
        if (!dataObject.isValid()) {
            return;
        }
        if (Boolean.TRUE.equals(dataObject.getPrimaryFile().getAttribute("nonEditableTemplate"))) { // NOI18N
            String pattern = FormUtils.getBundleString("MSG_NonEditableTemplate"); // NOI18N
            String message = MessageFormat.format(pattern, new Object[]{dataObject.getNodeDelegate().getName()});
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(message));
            return;
        }
        super.open();
    }

    /**
     * Overriden from JavaEditor - opens editor at given position and ensures it
     * is selected in the multiview.
     *
     * @param pos position
     */
    @Override
    public void openAt(PositionRef pos) {
        CloneableTopComponent multiviewTC = openCloneableTopComponent();

        MultiViewHandler handler = MultiViews.findMultiViewHandler(multiviewTC);
        handler.requestActive(handler.getPerspectives()[JS_ELEMENT_INDEX]);

        try {
            openAt(pos, pos.getColumn()).getComponent().requestActive();
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }

    @Override
    public void openAt(Position pos) {
        openAt(createPositionRef(pos.getOffset(), Position.Bias.Forward));
    }

    /**
     * Public method for loading form data from file. Does not open the source
     * editor and designer, does not report errors and does not throw any
     * exceptions. Runs in AWT event dispatch thread, returns after the form is
     * loaded (even if not called from AWT thread).
     *
     * @return whether the form is loaded (true also if it already was)
     */
    public void loadForm() throws PersistenceException {
        // Ensure initialization of the formEditor
        getFormEditor(true);
        formEditor.loadForm();
    }

    /**
     * @return true if the form is opened, false otherwise
     */
    public boolean isOpened() {
        return (formEditor != null) && formEditor.isFormLoaded();
    }

    /**
     * Save the document in this thread and start reparsing it.
     *
     * @exception IOException on I/O error
     */
    @Override
    public void saveDocument() throws IOException {
        IOException ioEx = null;
        try {
            if (formEditor != null) {
                formEditor.saveFormData();
            }
            super.saveDocument();
        } catch (PersistenceException ex) {
            Throwable t = ex.getOriginalException();
            if (t instanceof IOException) {
                ioEx = (IOException) t;
            } else {
                ioEx = new IOException("Cannot save the form"); // NOI18N
                ErrorManager.getDefault().annotate(ioEx, t != null ? t : ex);
            }
        }
        if (formEditor != null) {
            formEditor.reportErrors(FormEditor.FormOperation.SAVING);
        }
        if (ioEx != null) {
            throw ioEx;
        }
    }

    void saveSourceOnly() throws IOException {
        super.saveDocument();
    }

    // ------------
    // other interface methods
    /**
     * @return data object representing the form
     */
    public final PlatypusFormDataObject getFormDataObject() {
        return (PlatypusFormDataObject) dataObject;
    }

    public FormModel getFormModel() {
        FormEditor fe = getFormEditor();
        return (fe == null) ? null : fe.getFormModel();
    }
    // END of PENDING

    public FormEditor getFormEditor() {
        return getFormEditor(false);
    }

    FormEditor getFormEditor(boolean initialize) {
        if (formEditor == null && initialize) {
            formEditor = new FormEditor((PlatypusFormDataObject) dataObject);
        }
        return formEditor;
    }

    /**
     * Marks the form as modified if it's not yet. Used if changes made in form
     * data don't affect the java source file (generated code).
     */
    void markFormModified() {
        if (formEditor != null && formEditor.isFormLoaded() && !dataObject.isModified()) {
            notifyModified();
        }
    }

    @Override
    protected UndoRedo.Manager createUndoRedoManager() {
        editorUndoManager = super.createUndoRedoManager();
        return editorUndoManager;
    }

    void discardEditorUndoableEdits() {
        if (editorUndoManager != null) {
            editorUndoManager.discardAllEdits();
        }
    }

    @Override
    public void shrink() {
        List<CloneableTopComponent> views = getAllViews();
        if (views == null || views.isEmpty()) {
            notifyClosed();
            // Take care of memory consumption.
            dataObject.shrink();
            // Shrink has removed all parsed data from the data object.
            modelUndo.discardAllEdits();
            if (formEditor != null) {
                formEditor.closeForm();
                formEditor = null;
            }
            discardEditorUndoableEdits();
            sourceModified = false;
            modelModified = false;
            // formEditor.formModel.modified will be unavailable, becaouse of formEditor.closeForm();
        }
    }

    @Override
    public boolean notifyModified() {
        boolean retVal = super.notifyModified();
        updateTitles();
        return retVal;
    }

    @Override
    protected void notifyUnmodified() {
        super.notifyUnmodified();
        updateTitles();
    }

    // -------
    // window system & multiview
    @Override
    protected CloneableEditorSupport.Pane createPane() {
        PlatypusModuleSourceDescription sourceDesc = new PlatypusModuleSourceDescription(dataObject);
        PlatypusModuleDatamodelDescription modelDesc = new PlatypusModuleDatamodelDescription(dataObject);
        PlatypusFormLayoutDescription layoutDesc = new PlatypusFormLayoutDescription(dataObject);
        MultiViewDescription[] descs = new MultiViewDescription[]{sourceDesc, layoutDesc, modelDesc};
        CloneableTopComponent mv = MultiViewFactory.createCloneableMultiView(descs, layoutDesc, new CloseHandler(dataObject));
        return (CloneableEditorSupport.Pane) mv;
    }

    @Override
    protected boolean readOnly(PlatypusModuleDataObject formDataObject) {
        if (!formDataObject.getPrimaryFile().canWrite()) {
            return true;
        }
        TopComponent active = TopComponent.getRegistry().getActivated();
        if (active != null && getSelectedElementType(active) == FORM_ELEMENT_INDEX) {
            PlatypusFormSupport fes = formDataObject.getLookup().lookup(PlatypusFormSupport.class);
            if (fes != null) {
                FormModel fm = fes.getFormModel();
                if (fm != null) {
                    return fm.isReadOnly();
                }
            }
        }
        return false;
    }

    /*
    private String getMVTCToolTipText(PlatypusFormDataObject formDataObject) {
        return DataEditorSupport.toolTip(formDataObject.getPrimaryFile(), formDataObject.isModified(), readOnly(formDataObject));
    }
*/
    /**
     * Updates tooltip of all multiviews for given form. Replans to even queue
     * thread if necessary.
     *
     * void updateMVTCToolTipText() { if
     * (java.awt.EventQueue.isDispatchThread()) { if (multiviewTC == null) {
     * return; }
     *
     * String tooltip = getMVTCToolTipText((PlatypusFormDataObject) dataObject);
     * Enumeration<CloneableTopComponent> en =
     * multiviewTC.getReference().getComponents(); while (en.hasMoreElements())
     * { TopComponent tc = en.nextElement(); tc.setToolTipText(tooltip); } }
     * else { java.awt.EventQueue.invokeLater(new Runnable() {
     *
     * @Override public void run() { if (multiviewTC == null) { return; }
     *
     * String tooltip = getMVTCToolTipText((PlatypusFormDataObject) dataObject);
     * Enumeration en = multiviewTC.getReference().getComponents(); while
     * (en.hasMoreElements()) { TopComponent tc = (TopComponent)
     * en.nextElement(); tc.setToolTipText(tooltip); } } }); } }
     */
    static boolean isLastView(TopComponent tc) {
        if (!(tc instanceof CloneableTopComponent)) {
            return false;
        }

        boolean oneOrLess = true;
        Enumeration<?> en = ((CloneableTopComponent) tc).getReference().getComponents();
        if (en.hasMoreElements()) {
            en.nextElement();
            if (en.hasMoreElements()) {
                oneOrLess = false;
            }
        }
        return oneOrLess;
    }

    /**
     * This is called by the multiview elements whenever they are created (and
     * given a observer knowing their multiview TopComponent). It is important
     * during deserialization and clonig the multiview - i.e. during the
     * operations we have no control over. But anytime a multiview is created,
     * this method gets called.
     *
     * void setTopComponent(TopComponent topComp) { multiviewTC =
     * (CloneableTopComponent) topComp; String[] titles =
     * getMVTCDisplayName((PlatypusFormDataObject) dataObject);
     * multiviewTC.setDisplayName(titles[0]);
     * multiviewTC.setHtmlDisplayName(titles[1]);
     * multiviewTC.setToolTipText(getMVTCToolTipText((PlatypusFormDataObject)
     * dataObject)); }
     */
    public static PlatypusFormSupport getFormEditor(TopComponent tc) {
        Object dobj = tc.getLookup().lookup(DataObject.class);
        return dobj instanceof PlatypusFormDataObject
                ? ((PlatypusFormDataObject) dobj).getLookup().lookup(PlatypusFormSupport.class) : null;
    }
    private static Boolean groupVisible = null;

    static void checkFormGroupVisibility() {
        // when active TopComponent changes, check if we should open or close
        // the form editor group of windows (Inspector, Palette, Properties)
        WindowManager wm = WindowManager.getDefault();
        final TopComponentGroup group = wm.findTopComponentGroup("jsform"); // NOI18N
        if (group != null) {
            boolean designerSelected = false;
            for (Mode mode : wm.getModes()) {
                TopComponent selected = mode.getSelectedTopComponent();
                if (getSelectedElementType(selected) == FORM_ELEMENT_INDEX) {
                    designerSelected = true;
                    break;
                }
            }

            if (designerSelected && !Boolean.TRUE.equals(groupVisible)) {
                // Bug 116008: calling group.open() first time may cause hiding the
                // PlatypusFormLayoutView (some winsys multiview initialization mess), calling
                // this method again and hiding the group. By setting the groupVisible
                // to false we make the re-entrant call effectively do nothing.
                groupVisible = Boolean.FALSE;
                try {
                    group.open();
                } finally {
                    groupVisible = Boolean.TRUE;
                }
                final TopComponentGroup paletteGroup = wm.findTopComponentGroup("commonpalette"); // NOI18N
                if (null != paletteGroup) {
                    paletteGroup.open();
                }
                FormInspector inspector = FormInspector.getInstance();
                inspector.open();
                if (!Boolean.TRUE.equals(inspector.getClientProperty("isSliding"))) { // NOI18N
                    inspector.requestVisible();
                }
            } else if (!designerSelected && !Boolean.FALSE.equals(groupVisible)) {
                group.close();
                groupVisible = Boolean.FALSE;
            }
        }
    }

    /**
     * @return 0 if java editor in form editor multiview is selected 1 if form
     * designer in form editor multiview is selected -1 if the given
     * TopComponent is not form editor multiview
     */
    static int getSelectedElementType(TopComponent tc) {
        if (tc != null) {
            MultiViewHandler handler = MultiViews.findMultiViewHandler(tc);
            if (handler != null) {
                String prefId = handler.getSelectedPerspective().preferredID();
                if (PlatypusModuleSourceDescription.MODULE_SOURCE_VIEW_NAME.equals(prefId)) {
                    return JS_ELEMENT_INDEX; // 0
                }
                if (MV_FORM_ID.equals(prefId)) {
                    return FORM_ELEMENT_INDEX; // 1
                }
                if(PlatypusModuleDatamodelDescription.MODULE_DATAMODEL_VIEW_NAME.equals(prefId)){
                    return MODEL_ELEMENT_INDEX; // 2
                }
            }
        }
        return -1;
    }

    private final class FormGEditor implements GuardedEditorSupport {

        StyledDocument doc = null;

        @Override
        public StyledDocument getDocument() {
            return FormGEditor.this.doc;
        }
    }

    /**
     * A descriptor for the PlatypusFormLayoutView element of multiview. Allows
     * lazy creation of the PlatypusFormLayoutView (and thus form loading).
     */
    private static class PlatypusFormLayoutDescription implements MultiViewDescription, Serializable {

        private static final long serialVersionUID = -3126744316624172415L;
        private DataObject dataObject;

        private PlatypusFormLayoutDescription() {
        }

        public PlatypusFormLayoutDescription(DataObject formDO) {
            dataObject = formDO;
        }

        private PlatypusFormSupport getFormEditor() {
            return dataObject != null && dataObject instanceof PlatypusFormDataObject
                    ? ((PlatypusFormDataObject) dataObject).getLookup().lookup(PlatypusFormSupport.class) : null;
        }

        @Override
        public MultiViewElement createElement() {
            PlatypusFormSupport formEditor = getFormEditor();
            return new PlatypusFormLayoutView((formEditor == null) ? null : formEditor.getFormEditor(true));
        }

        @Override
        public String getDisplayName() {
            return FormUtils.getBundleString("CTL_DesignTabCaption"); // NOI18N
        }

        @Override
        public org.openide.util.HelpCtx getHelpCtx() {
            return org.openide.util.HelpCtx.DEFAULT_HELP;
        }

        @Override
        public java.awt.Image getIcon() {
            return ImageUtilities.loadImage(iconURL);
        }

        @Override
        public int getPersistenceType() {
            return TopComponent.PERSISTENCE_NEVER;
        }

        @Override
        public String preferredID() {
            return MV_FORM_ID;
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(dataObject);
        }

        public void readExternal(ObjectInput in)
                throws IOException, ClassNotFoundException {
            Object firstObject = in.readObject();
            if (firstObject instanceof PlatypusFormDataObject) {
                dataObject = (DataObject) firstObject;
            }
        }
    }

    // -------
    /**
     * A descriptor for the javascript editor as an element in multiview.
     */
    private static class JsDesc implements MultiViewDescription, SourceViewMarker, Serializable {

        private static final long serialVersionUID = -3126744316624172415L;
        private DataObject dataObject;

        private JsDesc() {
        }

        public JsDesc(DataObject formDO) {
            dataObject = formDO;
        }

        private PlatypusFormSupport getJavaEditor() {
            return dataObject != null && dataObject instanceof PlatypusFormDataObject
                    ? ((PlatypusFormDataObject) dataObject).getLookup().lookup(PlatypusFormSupport.class) : null;
        }

        @Override
        public MultiViewElement createElement() {
            PlatypusFormSupport javaEditor = getJavaEditor();
            if (javaEditor != null) {
                javaEditor.prepareDocument();
                JsEditorTopComponent editor = new JsEditorTopComponent(dataObject.getLookup().lookup(PlatypusFormSupport.class));
                Node[] nodes = editor.getActivatedNodes();
                if ((nodes == null) || (nodes.length == 0)) {
                    editor.setActivatedNodes(new Node[]{dataObject.getNodeDelegate()});
                }
                return (MultiViewElement) editor;
            }
            return MultiViewFactory.BLANK_ELEMENT;
        }

        @Override
        public String getDisplayName() {
            return FormUtils.getBundleString("CTL_SourceTabCaption"); // NOI18N
        }

        @Override
        public org.openide.util.HelpCtx getHelpCtx() {
            return org.openide.util.HelpCtx.DEFAULT_HELP;
        }

        @Override
        public java.awt.Image getIcon() {
            return ImageUtilities.loadImage(iconURL);
        }

        @Override
        public int getPersistenceType() {
            return TopComponent.PERSISTENCE_ONLY_OPENED;
        }

        @Override
        public String preferredID() {
            return PlatypusModuleSourceDescription.MODULE_SOURCE_VIEW_NAME;
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(dataObject);
        }

        public void readExternal(ObjectInput in)
                throws IOException, ClassNotFoundException {
            Object firstObject = in.readObject();
            if (firstObject instanceof PlatypusFormDataObject) {
                dataObject = (DataObject) firstObject;
            }
        }
    }

    // --------
    private static class JsEditorTopComponent
            extends CloneableEditor
            implements MultiViewElement {

        private static final long serialVersionUID = -3126744316624172415L;
        private transient JComponent toolbar;
        private transient MultiViewElementCallback multiViewObserver;

        JsEditorTopComponent() {
            super();
        }

        JsEditorTopComponent(DataEditorSupport s) {
            super(s);
        }

        @Override
        public JComponent getToolbarRepresentation() {
            if (toolbar == null) {
                JEditorPane lpane = getEditorPane();
                if (lpane != null) {
                    Document doc = lpane.getDocument();
                    if (doc instanceof NbDocument.CustomToolbar) {
                        toolbar = ((NbDocument.CustomToolbar) doc).createToolbar(lpane);
                    }
                }
                if (toolbar == null) {
                    // attempt to create own toolbar??
                    toolbar = new JPanel();
                }
            }
            return toolbar;
        }

        @Override
        public JComponent getVisualRepresentation() {
            return this;
        }

        @Override
        public void componentDeactivated() {
            super.componentDeactivated();
        }

        @Override
        public void componentActivated() {
            super.componentActivated();
        }

        @Override
        public void setMultiViewCallback(MultiViewElementCallback callback) {
            multiViewObserver = callback;
        }

        @Override
        public void requestVisible() {
            if (multiViewObserver != null) {
                multiViewObserver.requestVisible();
            } else {
                super.requestVisible();
            }
        }

        @Override
        public void requestActive() {
            if (multiViewObserver != null) {
                multiViewObserver.requestActive();
            } else {
                super.requestActive();
            }
        }

        @Override
        public void componentClosed() {
            // Issue 52286 & 55818
            super.canClose(null, true);
            super.componentClosed();
        }

        @Override
        public void componentShowing() {
            super.componentShowing();
        }

        @Override
        public void componentHidden() {
            super.componentHidden();
        }

        @Override
        public void componentOpened() {
            super.componentOpened();
            DataObject dob = ((DataEditorSupport) cloneableEditorSupport()).getDataObject();
            if ((multiViewObserver != null) && !(dob instanceof PlatypusFormDataObject)) {
                multiViewObserver.getTopComponent().close(); // Issue 67879
                EditorCookie ec = dob.getLookup().lookup(EditorCookie.class);
                ec.open();
            }
        }

        @Override
        public void updateName() {
            super.updateName();
            if (multiViewObserver != null) {
                PlatypusFormDataObject formDataObject = (PlatypusFormDataObject) ((PlatypusFormSupport) cloneableEditorSupport()).getDataObject();
                String[] titles = ((PlatypusFormSupport) cloneableEditorSupport()).getMVTCDisplayName(formDataObject);
                setDisplayName(titles[0]);
                setHtmlDisplayName(titles[1]);
            }
        }

        @Override
        protected boolean closeLast() {
            return true;
        }

        @Override
        public CloseOperationState canCloseElement() {
            // if this is not the last cloned java editor component, closing is OK
            if (!PlatypusFormSupport.isLastView(multiViewObserver.getTopComponent())) {
                return CloseOperationState.STATE_OK;
            }

            // return a placeholder state - to be sure our CloseHandler is called
            return MultiViewFactory.createUnsafeCloseState(
                    "ID_JAVA_CLOSING", // dummy ID // NOI18N
                    MultiViewFactory.NOOP_CLOSE_ACTION,
                    MultiViewFactory.NOOP_CLOSE_ACTION);
        }

        protected boolean isActiveTC() {
            TopComponent selected = getRegistry().getActivated();

            if (selected == null) {
                return false;
            }
            if (selected == this) {
                return true;
            }

            MultiViewHandler handler = MultiViews.findMultiViewHandler(selected);
            if (handler != null
                    && PlatypusModuleSourceDescription.MODULE_SOURCE_VIEW_NAME.equals(handler.getSelectedPerspective().preferredID())) {
                return true;
            }

            return false;
        }
    }

    // ------
    /**
     * Implementation of CloseOperationHandler for multiview. Ensures both form
     * and java editor are correctly closed, data saved, etc. Holds a reference
     * to form DataObject only - to be serializable with the multiview
     * TopComponent without problems.
     */
    private static class CloseHandler implements CloseOperationHandler,
            Serializable {

        private static final long serialVersionUID = -3126744315424172415L;
        private DataObject dataObject;

        private CloseHandler() {
        }

        public CloseHandler(DataObject formDO) {
            dataObject = formDO;
        }

        private PlatypusFormSupport getFormEditor() {
            return dataObject != null && dataObject instanceof PlatypusFormDataObject
                    ? ((PlatypusFormDataObject) dataObject).getLookup().lookup(PlatypusFormSupport.class) : null;
        }

        @Override
        public boolean resolveCloseOperation(CloseOperationState[] elements) {
            PlatypusFormSupport formEditor = getFormEditor();
            return formEditor != null ? formEditor.canClose() : true;
        }
    }
}
