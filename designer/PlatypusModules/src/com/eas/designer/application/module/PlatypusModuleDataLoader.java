/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module;

import com.eas.client.cache.PlatypusFiles;
import org.openide.ErrorManager;
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
@DataObject.Registrations(value = {
    @DataObject.Registration(position = 200, displayName = "#LBL_PlatypusModule_loader_name", mimeType = "text/javascript"),
    @DataObject.Registration(position = 200, displayName = "#LBL_PlatypusModule_loader_name", mimeType = "text/model+xml")})
public class PlatypusModuleDataLoader extends MultiFileLoader {

    static final long serialVersionUID = 4579146057404524013L;

    /**
     * Constructs a new PlatypusModuleDataLoader
     */
    public PlatypusModuleDataLoader() {
        super(PlatypusModuleDataObject.class.getName()); // NOI18N
    }

    @Override
    protected String actionsContext() {
        return "Loaders/text/javascript/Actions/"; // NOI18N
    }

    /**
     * For a given file finds a primary file.
     *
     * @param fo the file to find primary file for
     *
     * @return the primary file for the file or null if the file is not
     * recognized by this loader
     */
    @Override
    protected FileObject findPrimaryFile(FileObject fo) {
        return findPrimaryFileImpl(fo);
    }

    private static FileObject findPrimaryFileImpl(FileObject fo) {
        // never recognize folders.
        if (!fo.isFolder()) {
            FileObject jsBrother = FileUtil.findBrother(fo, PlatypusFiles.JAVASCRIPT_EXTENSION);
            FileObject modelBrother = FileUtil.findBrother(fo, PlatypusFiles.MODEL_EXTENSION);
            FileObject layoutBrother = FileUtil.findBrother(fo, PlatypusFiles.FORM_EXTENSION);
            FileObject reportBrother = FileUtil.findBrother(fo, PlatypusFiles.REPORT_LAYOUT_EXTENSION_X);
            FileObject reportXBrother = FileUtil.findBrother(fo, PlatypusFiles.REPORT_LAYOUT_EXTENSION);
            if ((layoutBrother == null || !"text/layout+xml".equals(layoutBrother.getMIMEType()))
                    && reportBrother == null
                    && reportXBrother == null) {
                String foMimeType = fo.getMIMEType();
                if ("text/model+xml".equals(foMimeType)) {
                    return jsBrother;
                } else if ("text/javascript".equals(foMimeType)
                        && modelBrother != null && "text/model+xml".equals(modelBrother.getMIMEType()) ) {
                    return fo;
                }
            }
        }
        return null;
    }

    /**
     * Creates the right data object for given primary file. It is guaranteed
     * that the provided file is realy primary file returned from the method
     * findPrimaryFile.
     *
     * @param primaryFile the primary file
     * @return the data object for this file
     * @exception DataObjectExistsException if the primary file already has data
     * object
     */
    @Override
    protected MultiDataObject createMultiObject(FileObject primaryFile) throws DataObjectExistsException {
        try {
            return new PlatypusModuleDataObject(primaryFile, this);
        } catch (Exception ex) {
            if (ex instanceof DataObjectExistsException) {
                throw (DataObjectExistsException) ex;
            } else {
                ErrorManager.getDefault().notify(ex);
                return null;
            }
        }
    }

    // [?] Probably needed in case FormDataObject is deserialized, then the
    // secondary entry is created additionally.
    @Override
    protected MultiDataObject.Entry createSecondaryEntry(MultiDataObject obj,
            FileObject secondaryFile) {
        assert PlatypusFiles.MODEL_EXTENSION.equals(secondaryFile.getExt());
        return new FileEntry(obj, secondaryFile);
    }

    @Override
    protected MultiDataObject.Entry createPrimaryEntry(MultiDataObject obj, FileObject primaryFile) {
        return new FileEntry(obj, primaryFile);
    }
}
