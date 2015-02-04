/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.selectors;

import com.eas.client.model.gui.view.model.SelectedField;
import com.eas.client.metadata.TableRef;
import com.eas.client.model.Entity;
import com.eas.client.model.Relation;
import com.eas.client.model.gui.view.ModelSelectionListener;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.swing.Action;

/**
 *
 * @author mg
 */
public class TableRefDmSelectionListener<E extends Entity<?, ?, E>> implements ModelSelectionListener<E> {

    protected TableRef tableRefTemplate = null;
    protected List<TableRef> selected;
    protected Action okAction = null;

    public TableRefDmSelectionListener(TableRef aTableRefTemplate, List<TableRef> aSelected, Action aOkAction) {
        super();
        tableRefTemplate = aTableRefTemplate;
        selected = aSelected;
        okAction = aOkAction;
    }

    @Override
    public void selectionChanged(List<SelectedField<E>> param, List<SelectedField<E>> field) {
    }

    @Override
    public void selectionChanged(Collection<Relation<E>> oldSelected, Collection<Relation<E>> newSelected) {
    }

    @Override
    public void selectionChanged(Set<E> oldSelected, Set<E> newSelected) {
        selected.clear();
        for (E entity : newSelected) {
            TableRef tr = new TableRef();
            tr.tableName = entity.getTableName();
            tr.datasourceName = tableRefTemplate.datasourceName;
            tr.schema = tableRefTemplate.schema;
            selected.add(tr);
        }
        okAction.setEnabled(newSelected != null && !newSelected.isEmpty());
    }
}
