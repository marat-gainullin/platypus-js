/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form;

import com.eas.client.cache.PlatypusFiles;
import java.io.IOException;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.UniFileLoader;

/**
 *
 * @author mg
 */
@DataObject.Registration(position = 200, displayName = "com.bearsoft.org.netbeans.modules.form.resources.Bundle#Loaders/text/layout+xml/Factories/com-bearsoft-org-netbeans-modules-form-PlatypusFormDataLoader.instance", mimeType = "text/layout+xml")
public class PlatypusLayoutDataLoader extends UniFileLoader {
    
    public PlatypusLayoutDataLoader(){
        super(PlatypusLayoutDataObject.class.getName());
    }
    
    @Override
    protected FileObject findPrimaryFile(FileObject fo) {
        // never recognize folders.
        if (!fo.isFolder()) {
            String ext = fo.getExt();
            if (ext.equals(PlatypusFiles.FORM_EXTENSION) && (FileUtil.findBrother(fo, PlatypusFiles.JAVASCRIPT_EXTENSION) == null || FileUtil.findBrother(fo, PlatypusFiles.MODEL_EXTENSION) == null)) {
                return fo;
            }
        }
        return null;
    }

    @Override
    protected MultiDataObject createMultiObject(FileObject primaryFile) throws DataObjectExistsException, IOException {
        try {
            return new PlatypusLayoutDataObject(primaryFile, this);
        } catch (Exception ex) {
            if (ex instanceof DataObjectExistsException) {
                throw (DataObjectExistsException) ex;
            } else {
                ErrorManager.getDefault().notify(ex);
                return null;
            }
        }
    }
}
