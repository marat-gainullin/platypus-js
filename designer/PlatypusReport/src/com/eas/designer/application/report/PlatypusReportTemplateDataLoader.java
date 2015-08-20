/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.report;

import com.eas.client.cache.PlatypusFiles;
import java.io.IOException;
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
@DataObject.Registration(position = 210, displayName = "#LBL_PlatypusReport_loader_name", mimeType = "application/ms-excel-x")
public class PlatypusReportTemplateDataLoader extends UniFileLoader {

    public PlatypusReportTemplateDataLoader(){
        super(PlatypusReportTemplateDataObject.class.getName());
    }

    @Override
    protected String actionsContext() {
        return "Loaders/application/ms-excel-x/Actions/"; 
    }
    
    @Override
    protected FileObject findPrimaryFile(FileObject fo) {
        // never recognize folders.
        if (!fo.isFolder()) {
            String ext = fo.getExt();
            if (ext.equals(PlatypusFiles.REPORT_LAYOUT_EXTENSION_X)
                    && FileUtil.findBrother(fo, PlatypusFiles.JAVASCRIPT_EXTENSION) == null) {
                return fo;
            }
        }
        return null;
    }
    
    @Override
    protected MultiDataObject createMultiObject(FileObject primaryFile) throws DataObjectExistsException, IOException {
        return new PlatypusReportTemplateDataObject(primaryFile, this);
    }
    
}
