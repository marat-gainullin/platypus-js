/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.dbdiagram;

import com.eas.client.model.dbscheme.DbSchemeModel;
import com.eas.client.model.dbscheme.FieldsEntity;
import com.eas.client.model.store.XmlDom2DbSchemeModel;
import com.eas.designer.application.PlatypusUtils;
import com.eas.designer.application.dbdiagram.nodes.TableEntityNode;
import com.eas.designer.explorer.PlatypusDataObject;
import com.eas.designer.explorer.model.nodes.EntityNode;
import com.eas.designer.explorer.model.nodes.ModelNode;
import com.eas.designer.explorer.model.nodes.ModelNodeChildren;
import com.eas.xml.dom.Source2XmlDom;
import com.eas.xml.dom.XmlDom2String;
import java.io.IOException;
import java.io.OutputStream;
import org.openide.awt.UndoRedo;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.CookieSet;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.w3c.dom.Document;

@MIMEResolver.ExtensionRegistration(displayName = "#LBL_PlatypusDbDiagram_loader_name", extension = "pd", mimeType = "text/dbdiagram+xml")
@DataObject.Registration(displayName = "#LBL_PlatypusDbDiagram_loader_name", iconBase = "com/eas/designer/application/dbdiagram/dbScheme.png", mimeType = "text/dbdiagram+xml")
public class PlatypusDbDiagramDataObject extends PlatypusDataObject {

    protected transient DbSchemeModel model;
    protected transient ModelNode<FieldsEntity, DbSchemeModel> modelNode;

    public PlatypusDbDiagramDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        CookieSet cookies = getCookieSet();
        cookies.add(new PlatypusDbDiagramSupport(this));
    }

    @Override
    protected void dispose() {
        getLookup().lookup(PlatypusDbDiagramSupport.class).closeAllViews();
        super.dispose();
    }

    @Override
    public Lookup getLookup() {
        return getCookieSet().getLookup();
    }

    @Override
    protected void clientChanged() {
        if (model != null) {
            model.setClient(getClient());
        }
    }

    public void readModel() throws Exception {
        FileObject pf = getPrimaryFile();
        model = XmlDom2DbSchemeModel.transform(getClient(), Source2XmlDom.transform(pf.asText(PlatypusUtils.COMMON_ENCODING_NAME)));
    }

    protected void checkModelRead() throws Exception {
        if (model == null) {
            readModel();
            modelNode = new ModelNode<>(new ModelNodeChildren<FieldsEntity, DbSchemeModel>(model, getUndoRedo(), getLookup()) {
                @Override
                protected EntityNode<FieldsEntity> createNode(FieldsEntity key) throws Exception {
                    return new TableEntityNode(key, new ProxyLookup(getLookup(), Lookups.fixed(key, getUndoRedo())));
                }
            }, this);
        }
    }
    
    private UndoRedo.Manager getUndoRedo() {
        return getLookup().lookup(PlatypusDbDiagramSupport.class).getModelUndo();
    }

    public DbSchemeModel getModel() throws Exception {
        checkModelRead();
        return model;
    }

    public ModelNode<FieldsEntity, DbSchemeModel> getModelNode() throws Exception {
        checkModelRead();
        return modelNode;
    }

    public void shrink() {
        model = null;
        modelNode = null;
    }

    public void saveModel() throws Exception {
        DbSchemeModel lModel = getModel();
        Document doc = lModel.toXML();
        String sData = XmlDom2String.transform(doc);
        FileObject fo = getPrimaryFile();
        try (OutputStream out = fo.getOutputStream()) {
            byte[] data = sData.getBytes(PlatypusUtils.COMMON_ENCODING_NAME);
            out.write(data);
            out.flush();
        }
    }

    @Override
    protected boolean needAnnotationRename(DataObject aDataObject) {
        return false;
    }
}
