/* Datamodel license.
 * Exclusive rights on this code in any form
 * are belong to it's author. This code was
 * developed for commercial purposes only. 
 * For any questions and any actions with this
 * code in any form you have to contact to it's
 * author.
 * All rights reserved.
 */

package com.eas.client.model.gui.view;

import com.eas.client.model.gui.view.model.ModelView;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 *
 * @author mg
 */
public class ModelViewDragHandler extends TransferHandler{

    protected ModelView<?, ?, ?> mView = null;
    
    public ModelViewDragHandler(ModelView<?, ?, ?> aModelView)
    {
        super();
        mView = aModelView;
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        super.exportDone(source, data, action);
        mView.cancelDragging(new MouseEvent(mView, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, 0, 0, 0, false) );
    }

    @Override
    public boolean canImport(TransferSupport support) {
        return false;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return MOVE | COPY;
    }

    @Override
    public boolean importData(TransferSupport support) {
        return false;
    }
}
