package com.eas.designer.application.query.editing.riddle;

import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;

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
            createTable.getColumnDefinitions().stream().forEach((ColumnDefinition columnDefinition) -> {
                if (columnDefinition.getColDataType().getArgumentsStringList() != null) {
                    for (String argument : columnDefinition.getColDataType().getArgumentsStringList()) {
                        //
                    }
                }
                if (columnDefinition.getColumnSpecStrings() != null) {
                    for (String columnSpec : columnDefinition.getColumnSpecStrings()) {
                        //
                    }
                }
            });

            createTable.getIndexes().stream().forEach((index) -> {
                for (String columnName : index.getColumnsNames()) {
                    //
                }
            });
        }
    }
}
