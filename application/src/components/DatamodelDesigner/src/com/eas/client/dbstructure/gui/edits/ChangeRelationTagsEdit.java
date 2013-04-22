/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dbstructure.gui.edits;

import com.bearsoft.rowset.metadata.ForeignKeySpec.ForeignKeyRule;
import com.eas.client.model.gui.edits.DatamodelEdit;
import com.eas.client.model.Relation;
import com.eas.client.model.dbscheme.FieldsEntity;

/**
 *
 * @author mg
 */
public class ChangeRelationTagsEdit extends DatamodelEdit {

    protected Relation<FieldsEntity> relation;
    protected ForeignKeyRule bUpdateRule;
    protected ForeignKeyRule bDeleteRule;
    protected String bCName;
    protected boolean bDeferrable;
    protected ForeignKeyRule aUpdateRule;
    protected ForeignKeyRule aDeleteRule;
    protected String aCName;
    protected boolean aDeferrable;

    public ChangeRelationTagsEdit(Relation<FieldsEntity> aRel) {
        super();
        relation = aRel;
    }

    @Override
    protected void redoWork() {
        relation.setFkUpdateRule(aUpdateRule);
        relation.setFkDeleteRule(aDeleteRule);
        relation.setFkName(aCName);
        relation.setFkDeferrable(aDeferrable);
    }

    @Override
    protected void undoWork() {
        relation.setFkUpdateRule(bUpdateRule);
        relation.setFkDeleteRule(bDeleteRule);
        relation.setFkName(bCName);
        relation.setFkDeferrable(bDeferrable);
    }

    public void recordBeforeState(Relation<FieldsEntity> aRelation) {
        bUpdateRule = aRelation.getFkUpdateRule();
        bDeleteRule = aRelation.getFkDeleteRule();
        bCName = aRelation.getFkName();
        bDeferrable = aRelation.isFkDeferrable();
    }

    public void recordAfterState(Relation<FieldsEntity> aRelation) {
        aUpdateRule = aRelation.getFkUpdateRule();
        aDeleteRule = aRelation.getFkDeleteRule();
        aCName = aRelation.getFkName();
        aDeferrable = aRelation.isFkDeferrable();
    }
}
