/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model;

/**
 *
 * @author mg
 * @param <E>
 */
public interface ModelEditingValidator<E extends Entity<?, ?, E>> {

    public boolean validateRelationAdding(Relation<E> aRelation);

    public boolean validateRelationRemoving(Relation<E> aRelation);

    public boolean validateEntityAdding(E aEntity);

    public boolean validateEntityRemoving(E aEntity);
}
