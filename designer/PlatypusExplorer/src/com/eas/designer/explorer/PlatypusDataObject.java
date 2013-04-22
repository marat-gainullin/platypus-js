/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.DbClient;
import com.eas.client.cache.PlatypusFiles;
import com.eas.client.cache.PlatypusFilesSupport;
import com.eas.designer.application.HandlerRegistration;
import com.eas.designer.explorer.project.PlatypusProject;
import com.eas.util.StringUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;

/**
 * Base class for Platypus Data Objects.
 *
 * @author vv
 */
public abstract class PlatypusDataObject extends MultiDataObject {

    private Set<Runnable> clientListeners = new HashSet<>();
    protected HandlerRegistration projectClientListener;

    public PlatypusDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException {
        super(pf, loader);
        if (getProject() != null) {
            projectClientListener = getProject().addClientChangeListener(new Runnable() {
                @Override
                public void run() {
                    clientChanged();
                    fireClientChanged();
                }
            });
        }
    }

    protected abstract void clientChanged();

    @Override
    protected void dispose() {
        if (projectClientListener != null) {
            projectClientListener.remove();
        }
        super.dispose();
    }

    public HandlerRegistration addClientChangeListener(final Runnable onChange) {
        clientListeners.add(onChange);
        return new HandlerRegistration() {
            @Override
            public void remove() {
                clientListeners.remove(onChange);
            }
        };
    }

    private void fireClientChanged() {
        for (Runnable onChange : clientListeners) {
            onChange.run();
        }
    }

    public final PlatypusProject getProject() {
        Project pr = FileOwnerQuery.getOwner(getPrimaryFile());
        if (pr instanceof PlatypusProject) {
            return (PlatypusProject) pr;
        } else {
            return null;
        }
    }

    public DbClient getClient() {
        PlatypusProject project = getProject();
        if (project != null) {
            return project.getClient();
        }
        return null;
    }

    public FileObject getAppRoot() throws Exception {
        PlatypusProject project = getProject();
        if (project != null) {
            return project.getApplicationRoot();
        } else {
            return null;
        }
    }

    protected boolean needAnnotationRename(DataObject aDataObject) {
        return aDataObject != null && aDataObject.getPrimaryFile() != null;
    }

    @Override
    protected DataObject handleCopy(DataFolder df) throws IOException {
        DataObject dob = super.handleCopy(df);
        if (needAnnotationRename(dob)) {
            String content = dob.getPrimaryFile().asText(PlatypusFiles.DEFAULT_ENCODING);
            String oldPlatypusId = PlatypusFilesSupport.getAnnotationValue(content, PlatypusFilesSupport.APP_ELEMENT_NAME_ANNOTATION);
            String newPlatypusId = oldPlatypusId + String.valueOf(IDGenerator.genID());
            content = PlatypusFilesSupport.replaceAnnotationValue(content, PlatypusFilesSupport.APP_ELEMENT_NAME_ANNOTATION, newPlatypusId);
            try (OutputStream os = dob.getPrimaryFile().getOutputStream()) {
                os.write(content.getBytes(PlatypusFiles.DEFAULT_ENCODING));
                os.flush();
            }
        } else {
            Logger.getLogger(PlatypusDataObject.class.getName()).log(Level.WARNING, "Copy error. Couldn't get primary file.");
        }
        return dob;
    }
}
