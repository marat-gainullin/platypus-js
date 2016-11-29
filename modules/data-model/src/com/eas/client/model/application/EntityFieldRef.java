/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.eas.client.metadata.Field;
import com.eas.client.model.Entity;

/**
 *
 * @author mg
 * @param <E>
 */
public class EntityFieldRef<E extends Entity<?, ?, E>> {

    public E entity;
    public Field field;

    public EntityFieldRef(E aEntity, Field aField) {
        super();
        entity = aEntity;
        field = aField;
    }
}
