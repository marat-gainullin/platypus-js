/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.model.windows;

import com.eas.client.SqlQuery;
import com.eas.client.model.Entity;
import com.eas.client.model.gui.view.model.ModelView;
import java.awt.event.ActionEvent;
import java.util.Set;
import javax.swing.AbstractAction;

/**
 *
 * @author mg
 */
public class ShowEntityActionPerformer<E extends Entity<?, SqlQuery, E>, MV extends ModelView<E, ?>> extends AbstractAction {

    protected MV mView;

    public ShowEntityActionPerformer(MV aView) {
        super();
        mView = aView;
    }

    public MV getView() {
        return mView;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (mView != null) {
            Set<E> selected = mView.getSelectedEntities();
            if (!selected.isEmpty()) {
                mView.makeVisible(mView.getEntityView(selected.iterator().next()), false);
            }
        }
    }
}
