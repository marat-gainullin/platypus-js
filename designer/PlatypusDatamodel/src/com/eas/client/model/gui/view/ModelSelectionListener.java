/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.view;

import com.eas.client.model.gui.view.model.SelectedField;
import com.eas.client.model.Entity;
import com.eas.client.model.Relation;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 *
 * @author mg
 */
public interface ModelSelectionListener<E extends Entity<?, ?, E>> {

    public void selectionChanged(Set<E> oldSelected, Set<E> newSelected);

    public void selectionChanged(List<SelectedField<E>> aParameters, List<SelectedField<E>> aFields);

    public void selectionChanged(Collection<Relation<E>> oldSelected, Collection<Relation<E>> newSelected);
}
