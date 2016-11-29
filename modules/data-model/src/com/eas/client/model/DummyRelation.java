/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model;

import com.eas.client.metadata.Field;


/**
 * Used to form a graph to control cycles into.
 *
 * @author mg
 * @param <RE>
 */
public class DummyRelation<RE extends Entity<?, ?, RE>> extends Relation<RE> {

    public DummyRelation(RE aLeftEntity, Field aLeftField, RE aRightEntity, Field aRightField) {
        super(aLeftEntity, aLeftField, aRightEntity, aRightField);
    }
}
