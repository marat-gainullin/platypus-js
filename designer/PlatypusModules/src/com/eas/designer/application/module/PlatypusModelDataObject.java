/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module;

import com.eas.designer.application.module.nodes.ApplicationModelNodeChildren;
import com.eas.designer.datamodel.nodes.ModelNode;
import com.eas.designer.explorer.PlatypusDataObject;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.Node;

/**
 *
 * @author mg
 */
@MIMEResolver.ExtensionRegistration(displayName = "#LBL_Platypus_Model_file", extension = "model", mimeType = "text/model+xml")
public class PlatypusModelDataObject extends PlatypusDataObject {

    public PlatypusModelDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException {
        super(pf, loader);
    }

    // Serialization
    static final long serialVersionUID = -975322023627853968L;
    
    private void readObject(java.io.ObjectInputStream is)
            throws java.io.IOException, ClassNotFoundException {
        is.defaultReadObject();
    }

    @Override
    protected void validateModel() throws Exception {
    }
/*
    @Override
    protected Node createNodeDelegate() {
        return new ModelNode<>(new ApplicationModelNodeChildren(model,
                getLookup().lookup(PlatypusModuleSupport.class).getModelUndo(),
                getLookup()), this);
    }
*/
}
