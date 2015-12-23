/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.model.windows;

import com.eas.client.SqlQuery;
import com.eas.client.model.Entity;
import com.eas.client.model.gui.view.EntityViewDoubleClickListener;
import com.eas.client.model.gui.view.entities.EntityView;
import com.eas.designer.application.indexer.IndexerQuery;
import com.eas.designer.application.project.PlatypusProject;
import java.awt.EventQueue;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;

/**
 *
 * @author vv
 */
public class QueryDocumentJumper<E extends Entity<?, SqlQuery, E>> implements EntityViewDoubleClickListener<E> {

    private PlatypusProject project;

    public QueryDocumentJumper(PlatypusProject aProject) {
        project = aProject;
    }

    @Override
    public void clicked(EntityView<E> aView, boolean fieldsClicked, boolean paramsClicked) {
        try {
            assert aView.getEntity() != null : "Entity in EntityView should not be null";
            String appElementId = aView.getEntity().getQueryName();
            if (appElementId != null) {
                FileObject appElementFileObject = IndexerQuery.appElementId2File(project, appElementId);
                if (appElementFileObject != null) {
                    openDocument(DataObject.find(appElementFileObject));
                }
            }
        } catch (DataObjectNotFoundException ex) {
            Logger.getLogger(QueryDocumentJumper.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }

    public static boolean openDocument(final DataObject dataObject) {
        if (dataObject != null) {
            Callable<Boolean> doIt = () -> {
                OpenCookie oc = dataObject.getLookup().lookup(OpenCookie.class);
                if (oc != null) {
                    oc.open();
                    return true;
                } else {
                    return false;
                }
            };
            if (!EventQueue.isDispatchThread()) {
                EventQueue.invokeLater(() -> {
                    try {
                        doIt.call();
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }
                });
                return true; // not exactly accurate, but....
            } else {
                try {
                    return doIt.call();
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                    return false;
                }
            }
        } else {
            return false;
        }
    }
}
