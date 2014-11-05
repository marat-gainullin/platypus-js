/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.model.windows;

import com.eas.client.cache.PlatypusFiles;
import com.eas.client.model.gui.view.ModelViewDragHandler;
import com.eas.client.model.gui.view.model.ModelView;
import com.eas.client.model.gui.view.model.QueryModelView;
import com.eas.designer.application.indexer.IndexerQuery;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import org.openide.ErrorManager;
import org.openide.loaders.DataObject;

/**
 *
 * @author mg
 */
public class QueriesDragHandler extends TransferHandler {

    protected ModelViewDragHandler delegate;
    protected ModelView<?, ?> modelView;

    public QueriesDragHandler(ModelViewDragHandler aDelegate, ModelView<?, ?> aModelView) {
        super();
        delegate = aDelegate;
        modelView = aModelView;
    }

    protected DataObject extractDataObject(TransferSupport support) throws Exception {
        DataFlavor[] flavors = support.getDataFlavors();
        if (flavors != null && flavors.length > 0) {
            for (int i = 0; i < flavors.length; i++) {
                Object oData = support.getTransferable().getTransferData(flavors[i]);
                if (oData instanceof DataObject) {
                    return (DataObject) oData;
                }
            }
        }
        return null;
    }

    @Override
    public boolean canImport(TransferSupport support) {
        try {
            DataObject dObject = extractDataObject(support);
            if (dObject != null) {
                return PlatypusFiles.SQL_EXTENSION.equalsIgnoreCase(dObject.getPrimaryFile().getExt())
                        || (PlatypusFiles.JAVASCRIPT_EXTENSION.equalsIgnoreCase(dObject.getPrimaryFile().getExt()) && !(modelView instanceof QueryModelView));
            } else {
                return delegate.canImport(support);
            }
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
            return false;
        }
    }

    @Override
    public boolean importData(TransferSupport support) {
        try {
            DataObject dObject = extractDataObject(support);
            if (dObject != null) {
                boolean isModule = PlatypusFiles.JAVASCRIPT_EXTENSION.equalsIgnoreCase(dObject.getPrimaryFile().getExt());
                boolean isQuery = PlatypusFiles.SQL_EXTENSION.equalsIgnoreCase(dObject.getPrimaryFile().getExt());
                if (isQuery || isModule) {
                    String queryId = IndexerQuery.file2AppElementId(dObject.getPrimaryFile());
                    if (queryId != null) {
                        Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
                        Window w = SwingUtilities.getWindowAncestor(modelView);
                        Point topLocation = w.getLocation();
                        mouseLoc.x -= topLocation.x;
                        mouseLoc.y -= topLocation.y;
                        mouseLoc = SwingUtilities.convertPoint(null, mouseLoc, modelView);
                        modelView.doAddQuery(queryId, mouseLoc.x, mouseLoc.y);
                        return true;
                    }
                }
                return false;
            } else {
                return delegate.importData(support);
            }
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
            return false;
        }
    }
}
