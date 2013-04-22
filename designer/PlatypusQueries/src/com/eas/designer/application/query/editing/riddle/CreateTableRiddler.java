package com.eas.designer.application.query.editing.riddle;

import java.util.Iterator;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.table.Index;

/**
 * A class to riddle (that is, remove from JSqlParser hierarchy some relations,
 * fields, parameters, and so on) a
 * {@link net.sf.jsqlparser.statement.create.table.CreateTable}
 */
public class CreateTableRiddler {

    protected RiddleTask riddleTask;

    public CreateTableRiddler(RiddleTask aTask) {
        riddleTask = aTask;
    }

    public void riddle(CreateTable createTable) {
        if (createTable.getColumnDefinitions() != null) {
            for (Iterator<ColumnDefinition> iter = createTable.getColumnDefinitions().iterator(); iter.hasNext();) {
                ColumnDefinition columnDefinition = iter.next();
                if (columnDefinition.getColDataType().getArgumentsStringList() != null) {
                    for (Iterator<String> iterator = columnDefinition.getColDataType().getArgumentsStringList().iterator(); iterator.hasNext();) {
                        iterator.next();
                    }
                }
                if (columnDefinition.getColumnSpecStrings() != null) {
                    for (Iterator<String> iterator = columnDefinition.getColumnSpecStrings().iterator(); iterator.hasNext();) {
                        iterator.next();
                    }
                }
            }

            for (Iterator<Index> iter = createTable.getIndexes().iterator(); iter.hasNext();) {
                Index index = iter.next();
                for (Iterator<String> iterator = index.getColumnsNames().iterator(); iterator.hasNext();) {
                    iterator.next();
                }
            }
        }
    }
}
