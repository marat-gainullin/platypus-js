/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.model.gui.view;

import com.eas.client.metadata.Field;
import com.eas.client.metadata.Parameter;
import com.eas.client.model.gui.view.entities.EntityView;
import com.eas.client.model.Entity;
import java.util.List;


/**
 *
 * @author mg
 */
public interface FieldSelectionListener<E extends Entity<?, ?, E>> {

    public void selected(EntityView<E> aView, List<Parameter> aParameter, List<Field> aField);
}
