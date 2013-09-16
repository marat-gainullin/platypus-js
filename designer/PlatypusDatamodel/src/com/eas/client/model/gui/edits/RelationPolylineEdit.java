/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.edits;

import com.eas.client.model.Relation;

/**
 *
 * @author mg
 */
public class RelationPolylineEdit extends DatamodelEdit {

    protected Relation relation;
    protected int[] oldxs;
    protected int[] oldys;
    protected int[] newxs;
    protected int[] newys;

    public RelationPolylineEdit(Relation aRelation, int[] aOldxs, int[] aOldys, int[] aNewxs, int[] aNewys) {
        super();
        relation = aRelation;
        oldxs = aOldxs;
        oldys = aOldys;
        newxs = aNewxs;
        newys = aNewys;
    }

    public int[] getNewxs() {
        return newxs;
    }

    public int[] getNewys() {
        return newys;
    }

    @Override
    protected void redoWork() {
        relation.setXYs(newxs, newys);
    }

    @Override
    protected void undoWork() {
        relation.setXYs(oldxs, oldys);
    }

    public void setNewXs(int[] newxs) {
        this.newxs = newxs;
    }

    public void setNewYs(int[] newys) {
        this.newys = newys;
    }

}
