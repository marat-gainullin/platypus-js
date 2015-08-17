/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form;

import com.eas.designer.explorer.PlatypusDataObject;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiFileLoader;

/**
 *
 * @author mg
 */
@MIMEResolver.ExtensionRegistration(displayName = "#LBL_Form_Layout_Files", extension = "layout", mimeType = "text/layout+xml")
public class PlatypusLayoutDataObject extends PlatypusDataObject {

    public PlatypusLayoutDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException {
        super(pf, loader);
    }

    @Override
    protected void validateModel() throws Exception {
    }

}
