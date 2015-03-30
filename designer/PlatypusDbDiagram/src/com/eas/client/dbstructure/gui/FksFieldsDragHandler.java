/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dbstructure.gui;

import com.eas.client.dbstructure.DbStructureUtils;
import com.eas.client.dbstructure.SqlActionsController;
import com.eas.client.dbstructure.gui.edits.CreateFkEdit;
import com.eas.client.dbstructure.gui.edits.DbStructureUndoableEditSupport;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.ForeignKeySpec;
import com.eas.client.model.Relation;
import com.eas.client.model.dbscheme.DbSchemeModel;
import com.eas.client.model.dbscheme.FieldsEntity;
import com.eas.client.model.gui.view.RelationsFieldsDragHandler;
import com.eas.client.model.gui.view.entities.EntityView;
import com.eas.client.model.gui.view.model.ModelView;
import javax.swing.JOptionPane;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoableEditSupport;
import org.openide.util.NbBundle;

/**
 *
 * @author mg
 */
public class FksFieldsDragHandler extends RelationsFieldsDragHandler<FieldsEntity> {

    protected SqlActionsController sqlActionsController;

    public FksFieldsDragHandler(SqlActionsController aSqlActionsController, ModelView<FieldsEntity, DbSchemeModel> aModelView, EntityView<FieldsEntity> aEntityView) {
        super(aModelView, aEntityView);
        sqlActionsController = aSqlActionsController;
    }

    @Override
    protected void editModelField2FieldRelation(UndoableEditSupport aUndoSupport, Object aTransferrableData, Relation<FieldsEntity> alreadyInRelation, Relation<FieldsEntity> newRel) throws CannotRedoException {
        if (newRel.getLeftField() != null && newRel.getRightField() != null
                && (newRel.getLeftEntity() != newRel.getRightEntity() || newRel.getLeftField() != newRel.getRightField())) {
            Field field = newRel.getLeftField();
            ForeignKeySpec fkSpec = DbStructureUtils.constructFkSpecByRelation(newRel);
            CreateFkEdit cEdit;
            try {
                cEdit = new CreateFkEdit(sqlActionsController, fkSpec, field);
                cEdit.redo();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(modelView, ex.getLocalizedMessage(), NbBundle.getMessage(DbStructureUtils.class, "dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            newRel.setFkUpdateRule(ForeignKeySpec.ForeignKeyRule.CASCADE);
            newRel.setFkDeleteRule(ForeignKeySpec.ForeignKeyRule.CASCADE);
            newRel.setFkDeferrable(true);
            newRel.setFkName(fkSpec.getCName());
            try {
                assert aUndoSupport instanceof DbStructureUndoableEditSupport;
                ((DbStructureUndoableEditSupport) aUndoSupport).notSavable();
                aUndoSupport.beginUpdate();
                try {
                    aUndoSupport.postEdit(cEdit);
                    super.editModelField2FieldRelation(
                            aUndoSupport,
                            aTransferrableData,
                            null/* Application model can't have multiple field's value's sources.
                             But database structure diagram can refer to the same field multiple times
                             without any drawbacks.*/,
                            newRel);
                } finally {
                    aUndoSupport.endUpdate();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(modelView, ex.getLocalizedMessage(), NbBundle.getMessage(DbStructureUtils.class, "dbSchemeEditor"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
