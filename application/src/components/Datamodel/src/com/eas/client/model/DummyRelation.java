/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.model;

/**
 * Used to form a graph to control cycles into.
 * @author mg
 */
public class DummyRelation<RE extends Entity<?, ?, RE>> extends Relation<RE>
{
    public DummyRelation(RE aLeftEntity, boolean isLeftField, String aLeftField, RE aRightEntity, boolean isRightField, String aRightField)
    {
        super(aLeftEntity, isLeftField, aLeftField, aRightEntity, isRightField, aRightField);
    }
}
