/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.edits.fields;

import com.eas.client.metadata.Fields;
import com.eas.client.model.Entity;

/**
 *
 * @author mg
 */
public class ReorderFieldsEdit<E extends Entity<?, ?, E>> extends FieldsEdit<E> {

    protected int[] order;

    public ReorderFieldsEdit(E anEntity, int[] anOrder) {
        super(anEntity);
        order = new int[anOrder.length];
        for (int i = 0; i < order.length; i ++) {
           order[i] = anOrder[i] + 1;
        }
    }

    @Override
    protected void redoWork() {
        Fields f = entity.getFields();
        if (f != null) {
            f.reorder(order);
        }
    }

    @Override
    protected void undoWork() {
        redoWork();
    }
}
