/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.dbstructure.gui.edits;

import com.eas.client.dbstructure.SqlActionsController;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.model.dbscheme.FieldsEntity;

/**
 *
 * @author mg
 */
public class DropIndexEdit extends CreateIndexEdit {

    public DropIndexEdit(SqlActionsController aSqlController, FieldsEntity aEntity, DbTableIndexSpec aIndex, int aIndexPosition)
    {
        super(aSqlController, aEntity, aIndex, aIndexPosition);
    }

    @Override
    protected void doRedoWork() throws Exception {
        super.doUndoWork();
    }

    @Override
    protected void doUndoWork() throws Exception {
        super.doRedoWork();
    }

}
