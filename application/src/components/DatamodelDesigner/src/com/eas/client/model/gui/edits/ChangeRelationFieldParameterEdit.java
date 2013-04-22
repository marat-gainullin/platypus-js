/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.edits;

import com.eas.client.model.Entity;
import com.eas.client.model.Relation;

/**
 *
 * @author mg
 */
public class ChangeRelationFieldParameterEdit<E extends Entity<?, ?, E>> extends DatamodelEdit {

    protected String beforeName = null;
    protected String afterName = null;
    protected Relation<E> toEdit = null;
    protected boolean parameter = false;
    protected boolean left = true;

    public ChangeRelationFieldParameterEdit(Relation<E> aRel, boolean isParam, boolean isLeft, String aBeforeName, String aAfterName) {
        super();
        toEdit = aRel;
        parameter = isParam;
        left = isLeft;
        beforeName = aBeforeName;
        afterName = aAfterName;
    }

    public String getAfterName() {
        return afterName;
    }

    public String getBeforeName() {
        return beforeName;
    }

    public boolean isLeft() {
        return left;
    }

    public boolean isParameter() {
        return parameter;
    }

    public Relation<E> getRelation() {
        return toEdit;
    }

    @Override
    protected void redoWork() {
        if (parameter) {
            if (left) {
                toEdit.setLeftParameter(afterName);
            } else {
                toEdit.setRightParameter(afterName);
            }
        } else {
            if (left) {
                toEdit.setLeftField(afterName);
            } else {
                toEdit.setRightField(afterName);
            }
        }
    }

    @Override
    protected void undoWork() {
        if (parameter) {
            if (left) {
                toEdit.setLeftParameter(beforeName);
            } else {
                toEdit.setRightParameter(beforeName);
            }
        } else {
            if (left) {
                toEdit.setLeftField(beforeName);
            } else {
                toEdit.setRightField(beforeName);
            }
        }
    }
}
