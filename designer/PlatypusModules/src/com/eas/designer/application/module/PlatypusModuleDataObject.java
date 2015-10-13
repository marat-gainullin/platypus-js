/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module;

import com.eas.client.SqlQuery;
import com.eas.client.cache.PlatypusFiles;
import com.eas.client.cache.PlatypusFilesSupport;
import com.eas.client.model.ModelEditingListener;
import com.eas.client.model.Relation;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.client.model.store.ApplicationModel2XmlDom;
import com.eas.client.model.store.XmlDom2ApplicationModel;
import com.eas.designer.application.PlatypusUtils;
import com.eas.designer.application.module.completion.ModuleCompletionContext;
import com.eas.designer.application.module.nodes.ApplicationModelNodeChildren;
import com.eas.designer.application.project.PlatypusProject;
import com.eas.designer.datamodel.nodes.ModelNode;
import com.eas.designer.explorer.PlatypusDataObject;
import com.eas.designer.explorer.files.wizard.NewApplicationElementWizardIterator;
import com.eas.script.Scripts;
import com.eas.util.ListenerRegistration;
import com.eas.xml.dom.Source2XmlDom;
import com.eas.xml.dom.XmlDom2String;
import java.io.IOException;
import java.io.OutputStream;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import jdk.nashorn.internal.ir.FunctionNode;
import jdk.nashorn.internal.parser.Token;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Cookie;
import org.openide.util.Exceptions;

public class PlatypusModuleDataObject extends PlatypusDataObject implements AstProvider, ModelProvider {

    public final static Object DATAOBJECT_DOC_PROPERTY = "dataObject";
    protected transient Entry modelEntry;
    protected transient ApplicationDbModel model;
    protected transient ModelNode<ApplicationDbEntity, ApplicationDbModel> modelNode;
    protected transient ModelEditingListener<ApplicationDbEntity> entitiesPublisher;
    private transient boolean astIsValid;
    private transient FunctionNode astRoot;
    private transient FunctionNode constructor;
    private transient ListenerRegistration queriesReg;
    protected transient PlatypusProject.QueriesChangeListener modelValidator = () -> {
        setModelValid(false);
        startModelValidating();
    };

    public PlatypusModuleDataObject(FileObject aJsFile, MultiFileLoader loader) throws Exception {
        super(aJsFile, loader);
        FileObject aModelFile = FileUtil.findBrother(aJsFile, PlatypusFiles.MODEL_EXTENSION);
        modelEntry = registerEntry(aModelFile);
        CookieSet cookies = getCookieSet();
        for (Cookie service : createServices()) {
            cookies.add(service);
        }
        PlatypusProject project = getProject();
        if (project != null) {
            queriesReg = project.addQueriesChangeListener(modelValidator);
        }
    }

    @Override
    public synchronized FunctionNode getAstRoot() {
        validateAst();
        return astRoot;
    }

    @Override
    public synchronized void setAstRoot(FunctionNode anAstRoot) {
        astRoot = anAstRoot;
        constructor = astRoot != null ? PlatypusFilesSupport.extractModuleConstructor(astRoot, getPrimaryFile().getPath()) : null;
        astIsValid = (astRoot != null);
    }

    @Override
    public FunctionNode getConstructor() {
        validateAst();
        return constructor;
    }

    public ModuleCompletionContext getCompletionContext() {
        return new ModuleCompletionContext(this);
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
                FunctionNode parseResult = null;
                try {
                    parseResult = Scripts.parseJs(doc.getText(0, doc.getLength()));
                } catch (BadLocationException ex) {
                    //no op
                }
                if (parseResult != null) {
                    astIsValid = true;
                    astRoot = parseResult;
                    constructor = PlatypusFilesSupport.extractModuleConstructor(astRoot, getPrimaryFile().getPath());
                } else {
                    astIsValid = false;
                }

            }
        }
    }

    public void invalidateAst() {
        astIsValid = false;
    }

    protected Cookie[] createServices() {
        return new Cookie[]{new PlatypusModuleSupport(this)};
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
        if (model != null) {
            model.removeEditingListener(entitiesPublisher);
            model = null;
        }
        modelNode = null;
        astIsValid = false;
        astRoot = null;
    }

    public FileObject getModelFile() {
        return modelEntry.getFile();
    }

    @Override
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
            model.getEntities().values().stream().forEach((ApplicationDbEntity aEntity) -> {
                aEntity.setPublished(new EntityJSObject(aEntity));
            });
            model.setPublished(new ModelJSObject(model));
            entitiesPublisher = new ModelEditingListener<ApplicationDbEntity>() {

                @Override
                public void entityAdded(ApplicationDbEntity aEntity) {
                    aEntity.setPublished(new EntityJSObject(aEntity));
                }

                @Override
                public void entityRemoved(ApplicationDbEntity e) {
                }

                @Override
                public void relationAdded(Relation<ApplicationDbEntity> rltn) {
                }

                @Override
                public void relationRemoved(Relation<ApplicationDbEntity> rltn) {
                }

                @Override
                public void entityIndexesChanged(ApplicationDbEntity e) {
                }
            };
            model.addEditingListener(entitiesPublisher);
            modelNode = createModelNode();
        }
    }

    protected ModelNode createModelNode() {
        return new ModelNode<>(new ApplicationModelNodeChildren(model,
                getLookup().lookup(PlatypusModuleSupport.class).getModelUndo(),
                getLookup()), this);
    }

    @Override
    public ModelNode<ApplicationDbEntity, ApplicationDbModel> getModelNode() throws Exception {
        checkModelRead();
        return modelNode;
    }

    protected ApplicationDbModel readModel() throws Exception {
        String modelContent = getModelFile().asText(PlatypusUtils.COMMON_ENCODING_NAME);
        org.w3c.dom.Document doc = Source2XmlDom.transform(modelContent);
        ApplicationDbModel modelRead = new ApplicationDbModel(getProject().getBasesProxy(), getProject().getQueries());
        modelRead.accept(new XmlDom2ApplicationModel<>(doc));
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
            org.w3c.dom.Document doc = ApplicationModel2XmlDom.transform(model);
            String modelContent = XmlDom2String.transform(doc);
            try (OutputStream out = getModelFile().getOutputStream()) {
                out.write(modelContent.getBytes(PlatypusUtils.COMMON_ENCODING_NAME));
                out.flush();
            }
        }
    }

    @Override
    protected void handleDelete() throws IOException {
        if (queriesReg != null) {
            queriesReg.remove();
            queriesReg = null;
        }
        FunctionNode constr = getConstructor();
        PlatypusProject project = getProject();
        if (project != null && constr != null && project.getQueries().getCachedQuery(constr.getName()) != null) {
            project.fireQueryChanged(constr.getName());
        }
        super.handleDelete();
    }

    public void notifyModuleQueryChanged() {
        FunctionNode constr = getConstructor();
        if (constr != null) {
            PlatypusProject project = getProject();
            if (project != null) {
                SqlQuery moduleQuery = project.getQueries().getCachedQuery(constr.getName());
                if (moduleQuery != null) {
                    if (queriesReg != null) {
                        queriesReg.remove();
                        queriesReg = null;
                    }
                    project.fireQueryChanged(constr.getName());
                    queriesReg = project.addQueriesChangeListener(modelValidator);
                }
            }
        }
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
        FunctionNode constructorFunc = PlatypusFilesSupport.extractModuleConstructor(Scripts.parseJs(aJsContent), getPrimaryFile().getPath());
        if (constructorFunc != null) {
            String oldName = constructorFunc.getName();
            String newName = NewApplicationElementWizardIterator.getNewValidAppElementName(project, oldName);
            int start = Token.descPosition(constructorFunc.getIdent().getToken());
            StringBuilder sb = new StringBuilder(aJsContent.substring(0, start));
            sb.append(newName);
            sb.append(aJsContent.substring(start + oldName.length()));
            return sb.toString();
        } else {
            return aJsContent;
        }
    }

}
