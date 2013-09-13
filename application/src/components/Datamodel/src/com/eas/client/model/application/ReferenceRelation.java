/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.bearsoft.rowset.metadata.Field;
import com.eas.client.model.Entity;
import com.eas.client.model.Relation;
import com.eas.client.model.visitors.ModelVisitor;

/**
 * Relation for design puporses. It's not serialized and is used only in designer.
 * @author mg
 */
public class ReferenceRelation<E extends Entity<?, ?, E>> extends Relation<E> {

    public ReferenceRelation(E aLeftEntity, Field aLeftField, E aRightEntity, Field aRightField) {
        super(aLeftEntity, aLeftField, aRightEntity, aRightField);
    }

    @Override
    public void accept(ModelVisitor<E> visitor) {
        // Such relation is not serialized and is used only in designer.
    }
 
}
