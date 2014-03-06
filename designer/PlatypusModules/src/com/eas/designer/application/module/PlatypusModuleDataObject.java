/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module;

import com.eas.client.cache.PlatypusFiles;
import com.eas.client.cache.PlatypusFilesSupport;
import com.eas.client.model.ModelEditingListener;
import com.eas.client.model.Relation;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.client.model.store.XmlDom2ApplicationModel;
import com.eas.client.scripts.ScriptRunner;
import com.eas.designer.application.PlatypusUtils;
import com.eas.designer.application.indexer.IndexerQuery;
import com.eas.designer.application.module.completion.ModuleCompletionContext;
import com.eas.designer.application.module.events.ApplicationModuleEvents;
import com.eas.designer.application.module.nodes.ApplicationModelNodeChildren;
import com.eas.script.JsParser;
import com.eas.designer.application.project.PlatypusProject;
import com.eas.designer.explorer.PlatypusDataObject;
import com.eas.designer.datamodel.nodes.ModelNode;
import com.eas.designer.explorer.files.wizard.NewApplicationElementWizardIterator;
import com.eas.xml.dom.Source2XmlDom;
import com.eas.xml.dom.XmlDom2String;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.OutputStream;
import javax.swing.text.Document;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Cookie;
import org.openide.util.Exceptions;

@MIMEResolver.ExtensionRegistration(displayName = "#LBL_Platypus_Model_file", extension = "model", mimeType = "text/model+xml")
public class PlatypusModuleDataObject extends PlatypusDataObject implements AstProvider {

    protected class ApplicationDbModelModifiedObserver implements ModelEditingListener<ApplicationDbEntity>, PropertyChangeListener {

        @Override
        public void entityAdded(ApplicationDbEntity e) {
            setModelModified();
            e.getChangeSupport().addPropertyChangeListener(this);
        }

        @Override
        public void entityRemoved(ApplicationDbEntity e) {
            setModelModified();
            e.getChangeSupport().removePropertyChangeListener(this);
        }

        @Override
        public void relationAdded(Relation<ApplicationDbEntity> rel) {
            setModelModified();
            rel.getChangeSupport().addPropertyChangeListener(this);
        }

        @Override
        public void relationRemoved(Relation<ApplicationDbEntity> rel) {
            setModelModified();
            rel.getChangeSupport().removePropertyChangeListener(this);
        }

        @Override
        public void entityIndexesChanged(ApplicationDbEntity e) {
            setModelModified();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            setModelModified();
        }

        private void setModelModified() {
            getLookup().lookup(PlatypusModuleSupport.class).setModelModified(true);
        }
    }

    public final static Object DATAOBJECT_DOC_PROPERTY = "dataObject";
    protected transient Entry modelEntry;
    protected transient ApplicationDbModel model;
    protected transient ModelNode<ApplicationDbEntity, ApplicationDbModel> modelNode;
    protected transient JsCodeGenerator codeGenerator;
    private transient boolean astIsValid;
    private transient AstRoot ast;

    public PlatypusModuleDataObject(FileObject aJsFile, MultiFileLoader loader) throws Exception {
        super(aJsFile, loader);
        FileObject aModelFile = FileUtil.findBrother(aJsFile, PlatypusFiles.MODEL_EXTENSION);
        modelEntry = registerEntry(aModelFile);
        CookieSet cookies = getCookieSet();
        for (Cookie service : createServices()) {
            cookies.add(service);
        }
        codeGenerator = new JsCodeGenerator(this);
    }

    @Override
    public synchronized AstRoot getAst() {
        validateAst();
        return ast;
    }

    @Override
    public synchronized void setAst(AstRoot anAstRoot) {
        ast = anAstRoot;
        astIsValid = (ast != null);
    }

    public ModuleCompletionContext getCompletionContext() {
        return new ModuleCompletionContext(this, ScriptRunner.class);
    }

    protected void validateAst() {
        if (!astIsValid) {
            Document doc = null;
            try {
                doc = getDocument();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            if (doc != null) {
                try {
                    AstRoot parseResult = JsParser.parse(doc.getText(0, doc.getLength()));
                    if (parseResult != null) {
                        astIsValid = true;
                        ast = parseResult;
                    } else {
                        astIsValid = false;
                    }
                } catch (Exception ex) {
                    // no op
                }
            }
        }
    }

    public void invalidateAst() {
        astIsValid = false;
    }

    protected Cookie[] createServices() {
        return new Cookie[]{new PlatypusModuleSupport(this), new ApplicationModuleEvents(this)};
    }

    @Override
    protected Node createNodeDelegate() {
        Node node = super.createNodeDelegate();
        if (node instanceof AbstractNode) {
            ((AbstractNode) node).setIconBaseWithExtension(PlatypusModuleDataObject.class.getPackage().getName().replace('.', '/') + "/module.png");
        }
        return node;
    }

    public void shrink() {
        model = null;
        modelNode = null;
        astIsValid = false;
        ast = null;
        getLookup().lookup(ApplicationModuleEvents.class).clear();
    }

    public FileObject getModelFile() {
        return modelEntry.getFile();
    }

    public ApplicationDbModel getModel() throws Exception {
        checkModelRead();
        return model;
    }

    private Document getDocument() throws IOException {
        EditorCookie ec = getLookup().lookup(EditorCookie.class);
        if (ec == null) {
            return null;
        }
        Document doc = ec.getDocument();
        if (doc == null) {
            doc = ec.openDocument();
        }
        return doc;
    }

    private void checkModelRead() throws Exception {
        PlatypusProject project = getProject();
        assert project != null : "Project is null.";
        if (model == null) {
            model = readModel();
            modelNode = createModelNode();
            ApplicationDbModelModifiedObserver changesObserver = new ApplicationDbModelModifiedObserver();
            model.addEditingListener(changesObserver);
            model.getParametersEntity().getChangeSupport().addPropertyChangeListener(changesObserver);
            for (ApplicationDbEntity entity : model.getEntities().values()) {
                entity.getChangeSupport().addPropertyChangeListener(changesObserver);
            }
            for (Relation<ApplicationDbEntity> rel : model.getRelations()) {
                rel.getChangeSupport().addPropertyChangeListener(changesObserver);
            }
        }
    }

    protected ModelNode createModelNode() {
        return new ModelNode<>(new ApplicationModelNodeChildren(model,
                getLookup().lookup(ApplicationModuleEvents.class),
                getLookup().lookup(PlatypusModuleSupport.class).getModelUndo(),
                getLookup()), this);
    }

    public ModelNode<ApplicationDbEntity, ApplicationDbModel> getModelNode() throws Exception {
        checkModelRead();
        return modelNode;
    }

    protected ApplicationDbModel readModel() throws Exception {
        String modelContent = getModelFile().asText(PlatypusUtils.COMMON_ENCODING_NAME);
        org.w3c.dom.Document doc = Source2XmlDom.transform(modelContent);
        ApplicationDbModel modelRead = new ApplicationDbModel(getClient());
        modelRead.accept(new XmlDom2ApplicationModel<ApplicationDbEntity>(doc));
        return modelRead;
    }

    @Override
    protected void validateModel() throws Exception {
        if (getModel() != null) {
            getModel().validate();
        }
    }

    public void saveModel() throws IOException {
        if (model != null) {
            org.w3c.dom.Document doc = model.toXML();
            String modelContent = XmlDom2String.transform(doc);
            try (OutputStream out = getModelFile().getOutputStream()) {
                out.write(modelContent.getBytes(PlatypusUtils.COMMON_ENCODING_NAME));
                out.flush();
            }
        }
    }

    @Override
    protected void handleDelete() throws IOException {
        String oldId = IndexerQuery.file2AppElementId(getPrimaryFile());
        super.handleDelete();
        if (getClient() != null) {
            try {
                getClient().appEntityChanged(oldId);
            } catch (Exception ex) {
                throw new IOException(ex);
            }
        }
    }

    public void notifyChanged() throws Exception {
        if (getClient() != null) {
            unsignFromQueries();
            try {
                getClient().appEntityChanged(IndexerQuery.file2AppElementId(getPrimaryFile()));
            } finally {
                resignOnQueries();
            }
        }
    }

    public JsCodeGenerator getCodeGenerator() {
        return codeGenerator;
    }

    @Override
    protected DataObject handleCopy(DataFolder df) throws IOException {
        DataObject copied = super.handleCopy(df);
        String content = copied.getPrimaryFile().asText(PlatypusFiles.DEFAULT_ENCODING);
        String newContent = getCopyModuleContent(FileOwnerQuery.getOwner(copied.getPrimaryFile()), content);
        try (OutputStream os = copied.getPrimaryFile().getOutputStream()) {
            os.write(newContent.getBytes(PlatypusFiles.DEFAULT_ENCODING));
            os.flush();
        }
        return copied;
    }

    private String getCopyModuleContent(Project project, String aJsContent) {
        AstRoot jsRoot = JsParser.parse(aJsContent);
        FunctionNode func = PlatypusFilesSupport.extractModuleConstructor(jsRoot);
        if (func != null) {
            Name consructorName = func.getFunctionName();
            String oldName = consructorName.getIdentifier();
            String newName = NewApplicationElementWizardIterator.getNewValidAppElementName(project, oldName);
            StringBuilder sb = new StringBuilder(aJsContent.substring(0, consructorName.getAbsolutePosition()));
            sb.append(newName);
            sb.append(aJsContent.substring(consructorName.getAbsolutePosition() + oldName.length()));
            return sb.toString();
        } else {
            return aJsContent;
        }
    }
}
