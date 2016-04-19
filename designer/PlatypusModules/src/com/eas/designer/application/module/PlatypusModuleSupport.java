/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module;

import com.eas.designer.datamodel.ModelUndoProvider;
import com.eas.designer.explorer.DataObjectProvider;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Position;
import javax.swing.text.StyledDocument;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;
import org.netbeans.core.spi.multiview.CloseOperationHandler;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewFactory;
import org.openide.ErrorManager;
import org.openide.awt.UndoRedo;
import org.openide.cookies.CloseCookie;
import org.openide.cookies.EditCookie;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.OpenCookie;
import org.openide.cookies.SaveCookie;
import org.openide.cookies.ViewCookie;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.loaders.MultiDataObject;
import org.openide.nodes.Node;
import org.openide.text.CloneableEditor;
import org.openide.text.CloneableEditorSupport;
import org.openide.text.DataEditorSupport;
import org.openide.text.PositionRef;
import org.openide.windows.CloneableOpenSupport;
import org.openide.windows.CloneableTopComponent;
import org.openide.windows.TopComponent;
import org.openide.xml.XMLUtil;

/**
 *
 * @author mg
 */
public class PlatypusModuleSupport extends DataEditorSupport implements OpenCookie,
        ViewCookie,
        EditCookie,
        EditorCookie.Observable,
        CloseCookie,
        SaveCookie,
        DocumentListener,
        DataObjectProvider,
        ModelUndoProvider,
        ModelModifiedProvider {

    protected PlatypusModuleDataObject dataObject;
    protected UndoRedo.Manager modelUndo;
    protected boolean modelModified;
    protected boolean sourceModified;

    public PlatypusModuleSupport(PlatypusModuleDataObject aObject) {
        super(aObject, new PlatypusScriptEnv(aObject));
        dataObject = aObject;
        modelUndo = new UndoRedo.Manager() {
            @Override
            public synchronized boolean addEdit(UndoableEdit anEdit) {
                try {
                    if (anEdit.isSignificant()) {
                        notifyModified();
                        modelModified = true;
                    }
                    return super.addEdit(anEdit);
                } catch (Exception ex) {
                    ErrorManager.getDefault().notify(ex);
                    return false;
                }
            }

            @Override
            public synchronized void undo() throws CannotUndoException {
                super.undo();
                notifyModified();
            }

            @Override
            public synchronized void redo() throws CannotRedoException {
                super.redo();
                getDataObject().setModified(true);
                notifyModified();
            }
        };
    }

    @Override
    public boolean isModelModified() {
        return modelModified;
    }

    @Override
    public void setModelModified(boolean aValue) {
        modelModified = aValue;
        // Undoable edits will mark whole dataobject as modified.
        // Only specific modified status is setted here.
        // Model's modified staus is the case.
    }

    /**
     * Opens editor at given position and ensures it is selected in the
     * multiview.
     *
     * @param pos position
     */
    public void openAt(PositionRef pos) {
        openCloneableTopComponent();
        openAt(pos, -1).getComponent().requestActive();
    }

    public void openAt(Position pos) {
        openAt(createPositionRef(pos.getOffset(), Position.Bias.Forward));
    }

    @Override
    protected void loadFromStreamToKit(StyledDocument doc, InputStream stream, EditorKit kit) throws IOException, BadLocationException {
        doc.putProperty(PlatypusModuleDataObject.DATAOBJECT_DOC_PROPERTY, dataObject);
        doc.addDocumentListener(this);
        super.loadFromStreamToKit(doc, stream, kit);
        sourceModified = false;
    }

    @Override
    protected void saveFromKitToStream(StyledDocument doc, EditorKit kit, OutputStream stream) throws IOException, BadLocationException {
        if (sourceModified) {
            super.saveFromKitToStream(doc, kit, stream);
            sourceModified = false;
        } else {
            stream.write(getDataObject().getPrimaryFile().asBytes());
            // NetBeans can't work properly if super.saveDocument() is called conditionally.
            // So we have to call it in despite of sourceModified flag.
            // If source is not modified, we should avoid file content overwriting.
            // There is no way to do so in NetBeans.
            // The only way is to provide original file content instead of document's content.
        }
    }

    @Override
    protected CloneableEditorSupport.Pane createPane() {
        PlatypusModuleSourceDescription sourceDesc = new PlatypusModuleSourceDescription(dataObject);
        PlatypusModuleDatamodelDescription modelDesc = new PlatypusModuleDatamodelDescription(dataObject);
        MultiViewDescription[] descs = new MultiViewDescription[]{sourceDesc, modelDesc};
        CloneableTopComponent mv = MultiViewFactory.createCloneableMultiView(descs, sourceDesc, new CloseHandler(dataObject));
        CloneableEditorSupport.Pane pane = (CloneableEditorSupport.Pane) mv;
        return pane;
    }

    @Override
    protected CloneableEditor createCloneableEditor() {
        return new PlatypusModuleSourceView(dataObject);
    }

    public UndoRedo.Manager getUndoRedoManager() {
        return super.getUndoRedo();
    }

    @Override
    public void saveDocument() throws IOException {
        boolean contentModified = sourceModified || modelModified;
        super.saveDocument();// sourceModified flag is taken into account in saveFromKitToStream() because of netbeans crazy CloneableEditorSupport
        saveModel();
        notifyUnmodified();
        if (contentModified) {
            notifyModuleChanged();
        }
    }
    
    protected void notifyModuleChanged(){
        /*
        try {
            dataObject.notifyChanged();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        */
    }

    @Override
    public void save() throws IOException {
        saveDocument();
    }

    protected void saveModel() throws IOException {
        if (modelModified) {
            dataObject.saveModel();
            modelModified = false;
        }
    }

    @Override
    public UndoRedo.Manager getModelUndo() {
        return modelUndo;
    }

    public List<CloneableTopComponent> getAllViews() {
        List<CloneableTopComponent> comps = new ArrayList<>();
        Enumeration<CloneableTopComponent> en = allEditors.getComponents();
        while (en.hasMoreElements()) {
            comps.add(en.nextElement());
        }
        return comps;
    }

    public void shrink() {
        List<CloneableTopComponent> views = getAllViews();
        if (views == null || views.isEmpty()) {
            notifyClosed();
            // Take care of memory consumption.
            dataObject.shrink();
            // Shrink has removed all parsed data from the data object.
            modelUndo.discardAllEdits();
            sourceModified = false;
            modelModified = false;
        }
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        sourceModified = true;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        sourceModified = true;
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        sourceModified = true;
    }

    @Override
    protected void notifyClosed() {
        Document doc = getDocument();
        if (doc != null) {
            doc.removeDocumentListener(this);
        }
        super.notifyClosed();
    }

    @Override
    public boolean notifyModified() {
        return super.notifyModified();
    }

    @Override
    protected boolean asynchronousOpen() {
        return false;
    }

    /**
     * Updates title (display name) of all multiviews for given form. Replans to
     * event queue thread if necessary.
     */
    private void updateMVTCDisplayName() {
        if (java.awt.EventQueue.isDispatchThread()) {
            updateMVTCDisplayNameInAWT();
        } else {
            java.awt.EventQueue.invokeLater(() -> {
                updateMVTCDisplayNameInAWT();
            });
        }
    }

    private void updateMVTCDisplayNameInAWT() {
        if (allEditors != null && dataObject.isValid()) {
            String[] titles = getMVTCDisplayName(dataObject);

            Enumeration<CloneableTopComponent> en = allEditors.getComponents();
            while (en.hasMoreElements()) {
                TopComponent tc = en.nextElement();
                tc.setDisplayName(titles[0]);
                tc.setHtmlDisplayName(titles[1]);
            }
        }
    }

    /**
     * Returns display name of the multiview top component. The first item of
     * the array is normal display name, the second item of the array is HTML
     * display name.
     *
     * @param formDataObject form data object representing the multiview tc.
     * @return display names of the MVTC. The second item can * be
     * <code>null</code>.
     */
    protected String[] getMVTCDisplayName(PlatypusModuleDataObject formDataObject) {
        Node node = formDataObject.getNodeDelegate();
        String title = node.getDisplayName();
        String htmlTitle = node.getHtmlDisplayName();
        if (htmlTitle == null) {
            try {
                htmlTitle = XMLUtil.toElementContent(title);
            } catch (CharConversionException x) {
                htmlTitle = "???";
            }
        }
        boolean modified = formDataObject.isModified();
        boolean readOnly = readOnly(formDataObject);
        return new String[]{
            DataEditorSupport.annotateName(title, false, modified, readOnly),
            DataEditorSupport.annotateName(htmlTitle, true, modified, readOnly)
        };
    }

    protected boolean readOnly(PlatypusModuleDataObject formDataObject) {
        return !formDataObject.getPrimaryFile().canWrite();
    }

    @Override
    public void updateTitles() {
        updateMVTCDisplayName();
    }

    /**
     * Implementation of CloseOperationHandler for multiview. Ensures both form
     * and java editor are correctly closed, data saved, etc. Holds a reference
     * to form DataObject only - to be serializable with the multiview
     * TopComponent without problems.
     */
    public static class CloseHandler implements CloseOperationHandler, Serializable {

        private static final long serialVersionUID = 3626344315421172415L;
        private PlatypusModuleDataObject dataObject;

        private CloseHandler() {// Serializing constructor.
            super();
        }

        public CloseHandler(PlatypusModuleDataObject aDataObject) {
            dataObject = aDataObject;
        }

        @Override
        public boolean resolveCloseOperation(CloseOperationState[] elements) {
            PlatypusModuleSupport support = dataObject.getLookup().lookup(PlatypusModuleSupport.class);
            return support.canClose();
        }
    }

    protected static class PlatypusScriptEnv extends DataEditorSupport.Env {

        private static final long serialVersionUID = 631344305421172415L;

        public PlatypusScriptEnv(PlatypusModuleDataObject aObject) {
            super(aObject);
            changeFile();
        }

        @Override
        public CloneableOpenSupport findCloneableOpenSupport() {
            return getDataObject().getLookup().lookup(PlatypusModuleSupport.class);
        }

        @Override
        protected FileObject getFile() {
            return getDataObject().getPrimaryFile();
        }

        @Override
        protected FileLock takeLock() throws IOException {
            return ((MultiDataObject) getDataObject()).getPrimaryEntry().takeLock();
        }

    }
}
