/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.visitors;

import com.bearsoft.rowset.metadata.Field;
import com.eas.client.model.Entity;
import com.eas.client.model.Relation;

/**
 *
 * @author mg
 */
public interface ModelVisitor<E extends Entity<?, ?, E>> {

    public void visit(Relation<E> aRelation);

    public void visit(Field aField);
}
