package com.eas.designer.application.query.editing.riddle;

import java.util.List;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.OrderByVisitor;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.Union;

/**
 * A class to de-parse (that is, tranform from JSqlParser hierarchy into a
 * string) a {@link net.sf.jsqlparser.statement.select.Select}
 */
public class SelectRiddler implements SelectVisitor, OrderByVisitor, SelectItemVisitor, FromItemVisitor {

    protected ExpressionRiddler expressionVisitor;
    protected RiddleTask riddleTask;

    public SelectRiddler(RiddleTask riddleTask) {
        this.riddleTask = riddleTask;
    }

    /**
     * @param expressionVisitor a {@link ExpressionRiddler} to de-parse
     * expressions. It has to share the same<br> StringBuilder (buffer
     * parameter) as this object in order to work
     * @param buffer the buffer that will be filled with the select
     */
    public SelectRiddler(ExpressionRiddler expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
        this.riddleTask = expressionVisitor.getRiddleTask();
    }

    @Override
    public void visit(PlainSelect plainSelect) {
        if (plainSelect.getDistinct() != null) {
            if (plainSelect.getDistinct().getOnSelectItems() != null) {
                for (int i = plainSelect.getDistinct().getOnSelectItems().size() - 1; i >= 0; i--) {
                    SelectItem selectItem = (SelectItem) plainSelect.getDistinct().getOnSelectItems().get(i);
                    selectItem.accept(this);
                    if (selectItem instanceof SelectExpressionItem) {
                        SelectExpressionItem sei = (SelectExpressionItem) selectItem;
                        if (riddleTask.needToDelete(sei.getExpression()) || riddleTask.markedAsDeleted(sei.getExpression())) {
                            riddleTask.markAsDeleted(sei.getExpression());
                            plainSelect.getDistinct().getOnSelectItems().remove(i);
                        }
                    }
                }
            }

        }

        for (int i = plainSelect.getSelectItems().size() - 1; i >= 0; i--) {
            SelectItem selectItem = plainSelect.getSelectItems().get(i);
            selectItem.accept(this);
            if (selectItem instanceof SelectExpressionItem) {
                SelectExpressionItem sei = (SelectExpressionItem) selectItem;
                if (riddleTask.needToDelete(sei.getExpression()) || riddleTask.markedAsDeleted(sei.getExpression())) {
                    riddleTask.markAsDeleted(sei.getExpression());
                    plainSelect.getSelectItems().remove(i);
                }
            }
        }
// from item is the first table or subquery in from clause. Other from items are in joins section
        if (plainSelect.getFromItem() != null) {
            plainSelect.getFromItem().accept(this);
            if (plainSelect.getFromItem() instanceof Table) {
                Table table = (Table) plainSelect.getFromItem();
                if (riddleTask.needToDelete(table) || riddleTask.markedAsDeleted(table)) {
                    riddleTask.markAsDeleted(table);
                    plainSelect.setFromItem(null);
                }
            }
        }
// continuation of from clause. 
        if (plainSelect.getJoins() != null) {
            for (int i = plainSelect.getJoins().size() - 1; i >= 0; i--) {
                Join join = plainSelect.getJoins().get(i);
                riddleJoin(join);
                if (join.getRightItem() == null) {
                    plainSelect.getJoins().remove(i);
                }
            }
        }

        // Let's check if nothing is in from list
        if (plainSelect.getFromItem() == null
                && plainSelect.getJoins() != null
                && !plainSelect.getJoins().isEmpty()) {
            // If we find something in joins, then we have to try to repair from list.
            Join firstJoin = plainSelect.getJoins().get(0);
            plainSelect.setFromItem(firstJoin.getRightItem());
            plainSelect.getJoins().remove(0);
            if (plainSelect.getJoins().isEmpty()) {
                plainSelect.setJoins(null);
            }
        }

        if (plainSelect.getWhere() != null) {
            plainSelect.getWhere().accept(expressionVisitor);
            if (plainSelect.getWhere() instanceof BinaryExpression) {
                plainSelect.setWhere(expressionVisitor.checkAndRepairTopLevelBinaryExpression((BinaryExpression) plainSelect.getWhere()));
            } else if (riddleTask.needToDelete(plainSelect.getWhere()) || riddleTask.markedAsDeleted(plainSelect.getWhere())) {
                riddleTask.markAsDeleted(plainSelect.getWhere());
                plainSelect.setWhere(null);
            }
        }

        if (plainSelect.getConnect() != null) {
            plainSelect.getConnect().accept(expressionVisitor);
            // Start with is optional, so connect without start with clause is valid.
            // Connect without connect by cluase is invalid, so remove whole clause.
            if (plainSelect.getConnect().getConnectBy() == null) {
                plainSelect.setConnect(null);
            }
        }

        if (plainSelect.getGroupByColumnReferences() != null) {
            for (int i = plainSelect.getGroupByColumnReferences().size() - 1; i >= 0; i--) {
                Expression columnReference = plainSelect.getGroupByColumnReferences().get(i);
                columnReference.accept(expressionVisitor);
                if (columnReference instanceof BinaryExpression) {
                    columnReference = expressionVisitor.checkAndRepairTopLevelBinaryExpression((BinaryExpression) columnReference);
                    if (columnReference == null) {
                        plainSelect.getGroupByColumnReferences().remove(i);
                    } else {
                        plainSelect.getGroupByColumnReferences().set(i, columnReference);
                    }
                } else if (riddleTask.needToDelete(columnReference) || riddleTask.markedAsDeleted(columnReference)) {
                    riddleTask.markAsDeleted(columnReference);
                    plainSelect.getGroupByColumnReferences().remove(i);
                }
            }
        }

        if (plainSelect.getHaving() != null) {
            plainSelect.getHaving().accept(expressionVisitor);
            if (plainSelect.getHaving() instanceof BinaryExpression) {
                plainSelect.setHaving(expressionVisitor.checkAndRepairTopLevelBinaryExpression((BinaryExpression) plainSelect.getHaving()));
            } else if (riddleTask.needToDelete(plainSelect.getHaving()) || riddleTask.markedAsDeleted(plainSelect.getHaving())) {
                riddleTask.markAsDeleted(plainSelect.getHaving());
                plainSelect.setHaving(null);
            }
        }

        if (plainSelect.getOrderByElements() != null) {
            riddleOrderBy(plainSelect.getOrderByElements());
            if (plainSelect.getOrderByElements().isEmpty()) {
                plainSelect.setOrderByElements(null);
            }
        }

        if (plainSelect.getLimit() != null) {
            riddleLimit(plainSelect.getLimit());
        }
    }

    @Override
    public void visit(Union union) {
        for (int i = union.getPlainSelects().size() - 1; i >= 0; i--) {
            PlainSelect plainSelect = union.getPlainSelects().get(i);
            plainSelect.accept(this);
            if (plainSelect.getFromItem() == null && (plainSelect.getJoins() == null || plainSelect.getJoins().isEmpty())) {
                union.getPlainSelects().remove(i);
            }
        }

        if (union.getOrderByElements() != null) {
            riddleOrderBy(union.getOrderByElements());
            if (union.getOrderByElements().isEmpty()) {
                union.setOrderByElements(null);
            }
        }

        if (union.getLimit() != null) {
            riddleLimit(union.getLimit());
        }
    }

    @Override
    public void visit(OrderByElement orderBy) {
        orderBy.getExpression().accept(expressionVisitor);
        // Nothing to do. All done in riddleOrderBy
        // Since OrderByElement can't appear out of the order by clause
        // all work is done there.
    }

    public void visit(Column column) {
        expressionVisitor.visit(column);
        // Riddle will be processed in ExpressionRiddler
    }

    @Override
    public void visit(Table aTable) {
        if (riddleTask.needToDelete(aTable)) {
            riddleTask.markAsDeleted(aTable);
        }
    }

    @Override
    public void visit(AllColumns allColumns) {
    }

    @Override
    public void visit(AllTableColumns allTableColumns) {
    }

    @Override
    public void visit(SelectExpressionItem selectExpressionItem) {
        selectExpressionItem.getExpression().accept(expressionVisitor);
        // Nothing to do. All the work is performed in
        // visit(PlainSelect) method.
    }

    @Override
    public void visit(SubSelect subSelect) {
        subSelect.getSelectBody().accept(this);
    }

    public void riddleOrderBy(List<OrderByElement> orderByElements) {
        for (int i = orderByElements.size() - 1; i >= 0; i--) {
            OrderByElement orderByElement = orderByElements.get(i);
            orderByElement.accept(this);
            if (orderByElement.getExpression() instanceof BinaryExpression) {
                orderByElement.setExpression(expressionVisitor.checkAndRepairTopLevelBinaryExpression((BinaryExpression) orderByElement.getExpression()));
            } else if (riddleTask.needToDelete(orderByElement.getExpression()) || riddleTask.markedAsDeleted(orderByElement.getExpression())) {
                riddleTask.markAsDeleted(orderByElement.getExpression());
                orderByElement.setExpression(null);
            }
            if (orderByElement.getExpression() == null) {
                orderByElements.remove(i);
            }
        }
    }

    public void riddleLimit(Limit limit) {
    }

    public ExpressionVisitor getExpressionVisitor() {
        return expressionVisitor;
    }

    public void setExpressionVisitor(ExpressionRiddler visitor) {
        expressionVisitor = visitor;
        if (expressionVisitor != null) {
            riddleTask = expressionVisitor.getRiddleTask();
        }
    }

    @Override
    public void visit(SubJoin subjoin) {
        subjoin.getLeft().accept(this);
        riddleJoin(subjoin.getJoin());
        if (subjoin.getJoin().getRightItem() == null) {
            subjoin.setJoin(null);
        }
    }

    public void riddleJoin(Join join) {
        FromItem fromItem = join.getRightItem();
        fromItem.accept(this);

        if (join.getOnExpression() != null) {
            join.getOnExpression().accept(expressionVisitor);
            if (join.getOnExpression() instanceof BinaryExpression) {
                join.setOnExpression(expressionVisitor.checkAndRepairTopLevelBinaryExpression((BinaryExpression) join.getOnExpression()));
            } else if (riddleTask.needToDelete(join.getOnExpression()) || riddleTask.markedAsDeleted(join.getOnExpression())) {
                riddleTask.markAsDeleted(join.getOnExpression());
                join.setOnExpression(null);
            }
            if (join.getOnExpression() == null) {
                join.setFull(false);
                join.setInner(false);
                join.setLeft(false);
                join.setNatural(false);
                join.setOuter(false);
                join.setRight(false);
                join.setSimple(true);
            }
        }
        if (join.getUsingColumns() != null) {
            for (int i = join.getUsingColumns().size() - 1; i >= 0; i--) {
                Column column = join.getUsingColumns().get(i);
                expressionVisitor.visit(column);
                if (riddleTask.needToDelete(column) || riddleTask.markedAsDeleted(column)) {
                    riddleTask.markAsDeleted(column);
                    join.getUsingColumns().remove(i);
                }
            }
            if (join.getUsingColumns().isEmpty()) {
                join.setUsingColumns(null);
            }
        }

        if (fromItem instanceof Table) {
            Table table = (Table) fromItem;
            if (riddleTask.needToDelete(table) || riddleTask.markedAsDeleted(table)) {
                riddleTask.markAsDeleted(table);
                join.setRightItem(null);
            }
        }
    }
}
