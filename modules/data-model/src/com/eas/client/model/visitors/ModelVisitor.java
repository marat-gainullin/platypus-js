/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.visitors;

import com.eas.client.metadata.Field;
import com.eas.client.model.Entity;
import com.eas.client.model.Model;
import com.eas.client.model.Relation;

/**
 *
 * @author mg
 * @param <E>
 * @param <M>
 */
public interface ModelVisitor<E extends Entity<?, ?, E>, M extends Model<E, ?>> {

    public void visit(Relation<E> aRelation);

    public void visit(Field aField);

    public void visit(M aModel);

    public void visit(E aEntity);

}
