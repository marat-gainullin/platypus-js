/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.selectors;

import com.eas.client.model.gui.view.ModelSelectionListener;
import com.eas.client.metadata.TableRef;
import com.eas.client.model.Entity;
import com.eas.client.model.Relation;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.swing.JButton;

/**
 *
 * @author mg
 */
public class ModelSelectionListener1<E extends Entity<?, ?, E>> implements ModelSelectionListener<E> {

    protected TableRef tr = null;
    protected JButton okBtn = null;

    public ModelSelectionListener1(TableRef aTr, JButton aOkBtn) {
        super();
        tr = aTr;
        okBtn = aOkBtn;
    }

    @Override
    public void selectionChanged(Set<E> oldSelected, Set<E> newSelected) {
        if (newSelected != null && !newSelected.isEmpty()) {
            E entity = newSelected.iterator().next();
            if (entity != null) {
                tr.tableName = entity.getTableName();
            } else {
                tr.tableName = null;
            }
            okBtn.setEnabled(tr.tableName != null);
        }
    }

    @Override
    public void selectionChanged(List<SelectedParameter<E>> param, List<SelectedField<E>> field) {
    }

    @Override
    public void selectionChanged(Collection<Relation<E>> oldSelected, Collection<Relation<E>> newSelected) {
    }
}
