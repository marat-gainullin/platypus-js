/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module;

import com.eas.client.cache.PlatypusFiles;
import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.FileEntry;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;

/**
 *
 * @author mg
 */
@DataObject.Registration(position = 200, displayName = "#LBL_PlatypusModule_loader_name", mimeType = "text/model+xml")
public class PlatypusModelDataLoader extends MultiFileLoader {

    public PlatypusModelDataLoader() {
        super(PlatypusModelDataObject.class.getName());
    }

    @Override
    protected FileObject findPrimaryFile(FileObject fo) {
        // never recognize folders.
        if (!fo.isFolder()) {
            String ext = fo.getExt();
            if (ext.equals(PlatypusFiles.MODEL_EXTENSION) && FileUtil.findBrother(fo, PlatypusFiles.JAVASCRIPT_EXTENSION) == null) {
                return fo;
            }
        }
        return null;
    }

    @Override
    protected MultiDataObject createMultiObject(FileObject primaryFile) throws DataObjectExistsException, IOException {
        return new PlatypusModelDataObject(primaryFile, this);
    }

    @Override
    protected MultiDataObject.Entry createPrimaryEntry(MultiDataObject obj, FileObject primaryFile) {
        return new FileEntry(obj, primaryFile);
    }

    @Override
    protected MultiDataObject.Entry createSecondaryEntry(MultiDataObject obj, FileObject secondaryFile) {
        return null;
    }

}
