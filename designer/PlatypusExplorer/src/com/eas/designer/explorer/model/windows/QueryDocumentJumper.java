/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.model.windows;

import com.eas.client.model.Entity;
import com.eas.client.model.gui.view.EntityViewDoubleClickListener;
import com.eas.client.model.gui.view.entities.EntityView;
import com.eas.designer.application.indexer.IndexerQuery;
import com.eas.designer.explorer.utils.GuiUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

/**
 *
 * @author vv
 */
public class QueryDocumentJumper<E extends Entity<?, ?, E>> implements EntityViewDoubleClickListener<E> {

    @Override
    public void clicked(EntityView<E> aView, boolean fieldsClicked, boolean paramsClicked) {
        try {
            assert aView.getEntity() != null : "Entity in EntityView should not be null";
            String appElementId = aView.getEntity().getQueryId();
            if (appElementId != null) {
                FileObject appElementFileObject = IndexerQuery.appElementId2File(appElementId);
                if (appElementFileObject != null) {
                    GuiUtils.openDocument(DataObject.find(appElementFileObject));
                }
            }
        } catch (DataObjectNotFoundException ex) {
            Logger.getLogger(QueryDocumentJumper.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }
}
