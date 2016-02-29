/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module;

import com.eas.client.cache.PlatypusFiles;
import com.eas.client.model.ModelEditingListener;
import com.eas.client.model.Relation;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.client.model.store.ApplicationModel2XmlDom;
import com.eas.client.model.store.XmlDom2ApplicationModel;
import com.eas.designer.application.PlatypusUtils;
import com.eas.designer.application.module.nodes.ApplicationModelNodeChildren;
import com.eas.designer.application.project.PlatypusProject;
import com.eas.designer.datamodel.nodes.ModelNode;
import com.eas.designer.explorer.PlatypusDataObject;
import com.eas.util.ListenerRegistration;
import com.eas.xml.dom.Source2XmlDom;
import com.eas.xml.dom.XmlDom2String;
import java.io.IOException;
import java.io.OutputStream;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Cookie;

public class PlatypusModuleDataObject extends PlatypusDataObject implements ModelProvider {

    public final static Object DATAOBJECT_DOC_PROPERTY = "dataObject";
    protected transient Entry modelEntry;
    protected transient ApplicationDbModel model;
    protected transient ModelNode<ApplicationDbEntity, ApplicationDbModel> modelNode;
    protected transient ModelEditingListener<ApplicationDbEntity> entitiesPublisher;
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
    }

    public FileObject getModelFile() {
        return modelEntry.getFile();
    }

    public boolean isModelRead(){
        return model != null;
    }
    
    @Override
    public ApplicationDbModel getModel() throws Exception {
        checkModelRead();
        return model;
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
        super.handleDelete();
    }

    @Override
    protected DataObject handleCopy(DataFolder df) throws IOException {
        DataObject copied = super.handleCopy(df);
        return copied;
    }

}
