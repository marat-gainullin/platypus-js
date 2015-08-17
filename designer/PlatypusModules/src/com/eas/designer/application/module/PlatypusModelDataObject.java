/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module;

import com.eas.designer.explorer.PlatypusDataObject;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiFileLoader;

/**
 *
 * @author mg
 */
@MIMEResolver.ExtensionRegistration(displayName = "#LBL_Platypus_Model_file", extension = "model", mimeType = "text/model+xml")
public class PlatypusModelDataObject extends PlatypusDataObject {

    public PlatypusModelDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException {
        super(pf, loader);
    }

    @Override
    protected void validateModel() throws Exception {
    }

}
