/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module;

import com.eas.designer.application.project.PlatypusProject;
import com.eas.designer.explorer.PlatypusDataObject;
import com.eas.util.ListenerRegistration;
import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.Node;

/**
 *
 * @author mg
 */
@MIMEResolver.NamespaceRegistration(checkedExtension = "model", elementName = "datamodel", displayName = "#LBL_Platypus_Model_file", mimeType = "text/model+xml")
public class PlatypusModelDataObject extends PlatypusDataObject {

    private transient ListenerRegistration queriesReg;
    protected transient PlatypusProject.QueriesChangeListener modelValidator = () -> {
        setModelValid(false);
        startModelValidating();
    };
    
    public PlatypusModelDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException {
        super(pf, loader);
        getCookieSet().add(new PlatypusModelSupport(this));
        PlatypusProject project = getProject();
        if (project != null) {
            queriesReg = project.addQueriesChangeListener(modelValidator);
        }
    }

    // Serialization
    static final long serialVersionUID = -975322023627853968L;

    private void readObject(java.io.ObjectInputStream is)
            throws java.io.IOException, ClassNotFoundException {
        is.defaultReadObject();
    }

    @Override
    protected void validateModel() throws Exception {
        if (getLookup().lookup(ModelProvider.class).getModel() != null) {
            getLookup().lookup(ModelProvider.class).getModel().validate();
        }
    }

    @Override
    protected Node createNodeDelegate() {
        return new ModelDataNode(this);
    }

    @Override
    protected void handleDelete() throws IOException {
        if (queriesReg != null) {
            queriesReg.remove();
            queriesReg = null;
        }
        super.handleDelete();
    }

}
