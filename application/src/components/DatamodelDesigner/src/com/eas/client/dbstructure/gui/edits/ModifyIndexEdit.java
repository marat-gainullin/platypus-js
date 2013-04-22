/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dbstructure.gui.edits;

import com.eas.client.dbstructure.SqlActionsController;
import com.eas.client.dbstructure.exceptions.DbActionException;
import com.eas.client.metadata.DbTableIndexColumnSpec;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.model.dbscheme.FieldsEntity;
import com.eas.client.model.gui.edits.NotSavable;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author vv
 */
public class ModifyIndexEdit extends DbStructureEdit implements NotSavable {

    protected DbTableIndexSpec index;
    protected DbTableIndexSpec oldIndexSpec;
    protected DbTableIndexSpec newIndexSpec;
    protected FieldsEntity entity;
    protected int indexPosition;
    
    public ModifyIndexEdit(SqlActionsController aSqlController, FieldsEntity aEntity, DbTableIndexSpec beforeContent, DbTableIndexSpec afterContent, DbTableIndexSpec anIndex, int anIndexPosition) {
        super(aSqlController);
        entity = aEntity;
        index = anIndex;
        oldIndexSpec = beforeContent;
        newIndexSpec = afterContent;
        indexPosition = anIndexPosition;
    }
    
    
    @Override
    protected void doRedoWork() throws Exception {  
        dropIndex(oldIndexSpec);
        createIndex(newIndexSpec);
        assign(index, newIndexSpec);
    }
    
    @Override
    protected void doUndoWork() throws Exception {
        dropIndex(newIndexSpec);
        createIndex(oldIndexSpec);
        assign(index, oldIndexSpec);
    }
        
    private void dropIndex(DbTableIndexSpec idx) throws DbActionException {
        SqlActionsController.DropIndexAction dropAction = sqlController.createDropIndexAction(entity.getTableName(), idx);
        if (!dropAction.execute()) {
            DbActionException ex = new DbActionException(dropAction.getErrorString());
            ex.setParam1(entity.getTableName());
            ex.setParam2(oldIndexSpec.getName());
            throw ex;
        }
    }
    
    private void createIndex(DbTableIndexSpec idx) throws DbActionException {
        SqlActionsController.CreateIndexAction createAction = sqlController.createCreateIndexAction(entity.getTableName(), idx);
        if (!createAction.execute()) {
            DbActionException ex = new DbActionException(createAction.getErrorString());
            ex.setParam1(entity.getTableName());
            ex.setParam2(newIndexSpec.getName());
            throw ex;
        }
    }    

    private void assign(DbTableIndexSpec index, DbTableIndexSpec newIndex) {
        if ((index.getName() == null && newIndex.getName() != null) || (index.getName() != null && !index.getName().equals(newIndex.getName()))) {
            index.setName(newIndex.getName());
        }  else if (index.isUnique() != newIndex.isUnique()) {
            index.setUnique(newIndex.isUnique());
        }  else if (index.isClustered() != newIndex.isClustered()) {
            index.setClustered(newIndex.isClustered());
        }  else if (index.isHashed() != newIndex.isHashed()) {
            index.setHashed(newIndex.isHashed());
        }
        List<DbTableIndexColumnSpec> oldColumns = index.copy().getColumns();
        boolean columnsChanged = false;
        if (index.getColumns().size() == newIndex.getColumns().size()) {
            for (DbTableIndexColumnSpec indexColumn : index.getColumns()) { 
                DbTableIndexColumnSpec newIndexColumn = newIndex.getColumn(indexColumn.getColumnName());
                if (indexColumn.getOrdinalPosition() != newIndexColumn.getOrdinalPosition()) {
                    indexColumn.setOrdinalPosition(newIndexColumn.getOrdinalPosition());
                    columnsChanged = true;
                }
            }
        }
        List<DbTableIndexColumnSpec> indexColumns = index.getColumns();
        Iterator<DbTableIndexColumnSpec> i = indexColumns.iterator();
        while(i.hasNext()) {
            DbTableIndexColumnSpec indexColumn = i.next();
            DbTableIndexColumnSpec newIndexColumn = newIndex.getColumn(indexColumn.getColumnName());
            if (newIndexColumn != null) {
                if (indexColumn.isAscending() != newIndexColumn.isAscending()) {
                    indexColumn.setAscending(newIndexColumn.isAscending());
                    columnsChanged = true;
                }      
            } else {
                i.remove();
                columnsChanged = true;
            }
        }
        for (DbTableIndexColumnSpec newIndexColumn : newIndex.getColumns()) {
            DbTableIndexColumnSpec indexColumn = index.getColumn(newIndexColumn.getColumnName());
            if (indexColumn == null) {
                index.addColumn(new DbTableIndexColumnSpec(newIndexColumn));
                columnsChanged = true;
            }
        }
        if (columnsChanged) {
            index.getChangeSupport().firePropertyChange(DbTableIndexSpec.COLUMNS_PROPERTY, oldColumns, newIndex.getColumns());
        }
    }
}
