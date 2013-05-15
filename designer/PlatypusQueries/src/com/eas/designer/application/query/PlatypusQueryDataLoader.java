/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query;

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
    @DataObject.Registration(position = 200, displayName = "#LBL_PlatypusQuery_loader_name", iconBase = "com/eas/designer/application/query/query.png", mimeType = "text/x-platypus-sql"),
    @DataObject.Registration(position = 200, displayName = "#LBL_PlatypusQuery_loader_name", iconBase = "com/eas/designer/application/query/query.png", mimeType = "text/model+xml"),
    @DataObject.Registration(position = 200, displayName = "#LBL_PlatypusQuery_loader_name", iconBase = "com/eas/designer/application/query/query.png", mimeType = "text/fields+xml")
})
public class PlatypusQueryDataLoader extends MultiFileLoader {

    static final long serialVersionUID = 4579146057404524013L;

    /**
     * Constructs a new PlatypusModuleDataLoader
     */
    public PlatypusQueryDataLoader() {
        super(PlatypusQueryDataObject.class.getName());
    }

    @Override
    protected String actionsContext() {
        return "Loaders/text/x-platypus-sql/Actions/"; // NOI18N
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
        // never recognize folders.
        if (!fo.isFolder()) {
            String ext = fo.getExt();
            FileObject sqlFile = FileUtil.findBrother(fo, PlatypusFiles.SQL_EXTENSION);
            FileObject modelFile = FileUtil.findBrother(fo, PlatypusFiles.MODEL_EXTENSION);
            FileObject dialectFile = FileUtil.findBrother(fo, PlatypusFiles.DIALECT_EXTENSION);
            FileObject outFile = FileUtil.findBrother(fo, PlatypusFiles.OUT_EXTENSION);
            if (ext.equals(PlatypusFiles.MODEL_EXTENSION)
                    && sqlFile != null
                    && dialectFile != null
                    && outFile != null) {
                return sqlFile;
            } else if (ext.equals(PlatypusFiles.DIALECT_EXTENSION)
                    && sqlFile != null
                    && modelFile != null
                    && outFile != null) {
                return sqlFile;
            } else if (ext.equals(PlatypusFiles.OUT_EXTENSION)
                    && sqlFile != null
                    && modelFile != null
                    && dialectFile != null) {
                return sqlFile;
            } else if (ext.equals(PlatypusFiles.SQL_EXTENSION)
                     && modelFile != null
                     && dialectFile != null
                     && outFile != null) {
                return fo;
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
            return new PlatypusQueryDataObject(primaryFile, this);
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
        return new FileEntry(obj, secondaryFile);
    }

    @Override
    protected MultiDataObject.Entry createPrimaryEntry(MultiDataObject obj, FileObject primaryFile) {
        return new FileEntry(obj, primaryFile);
    }
}
