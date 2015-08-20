/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.report;

import com.eas.designer.explorer.PlatypusDataObject;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.Node;

/**
 *
 * @author mg
 */
public class PlatypusReportTemplateDataObject extends PlatypusDataObject{

    public PlatypusReportTemplateDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException {
        super(pf, loader);
    }

    @Override
    protected void validateModel() throws Exception {
    }
    
    @Override
    protected Node createNodeDelegate() {
        return new ReportTemplateDataNode(this);
    }
}
