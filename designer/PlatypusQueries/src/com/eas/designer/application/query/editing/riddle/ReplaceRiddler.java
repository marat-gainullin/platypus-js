package com.eas.designer.application.query.editing.riddle;

import net.sf.jsqlparser.expression.BinaryExpression;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SubSelect;

/**
 * A class to de-parse (that is, tranform from JSqlParser hierarchy into a string)
 * a {@link net.sf.jsqlparser.statement.replace.Replace}
 */
public class ReplaceRiddler implements ItemsListVisitor {

    protected ExpressionRiddler expressionVisitor;
    protected SelectVisitor selectVisitor;

    /**
     * @param expressionVisitor a {@link ExpressionVisitor} to de-parse expressions. It has to share the same<br>
     * StringBuilder (buffer parameter) as this object in order to work
     * @param selectVisitor a {@link SelectVisitor} to de-parse {@link net.sf.jsqlparser.statement.select.Select}s.
     * It has to share the same<br>
     * StringBuilder (buffer parameter) as this object in order to work
     */
    public ReplaceRiddler(ExpressionRiddler expressionVisitor, SelectVisitor selectVisitor) {
        this.expressionVisitor = expressionVisitor;
        this.selectVisitor = selectVisitor;
    }

    public void riddle(Replace replace) {
        if (replace.getItemsList() != null) {
            if (replace.getColumns() != null) {
                for (int i = 0; i < replace.getColumns().size(); i++) {
                    Column column = replace.getColumns().get(i);
                    expressionVisitor.visit(column);
                }
            }
        } else {
            for (int i = replace.getColumns().size() - 1; i >= 0; i--) {
                Column column = replace.getColumns().get(i);
                expressionVisitor.visit(column);
                Expression expression = replace.getExpressions().get(i);
                expression.accept(expressionVisitor);

                boolean deleted = false;
                EqualsTo dummyRelation = new EqualsTo();
                dummyRelation.setLeftExpression(column);
                dummyRelation.setRightExpression(expression);
                if (expressionVisitor.getRiddleTask().needToDelete(dummyRelation)) {
                    expressionVisitor.getRiddleTask().markAsDeleted(column);
                    expressionVisitor.getRiddleTask().markAsDeleted(expression);
                    replace.getColumns().remove(i);
                    replace.getExpressions().remove(i);
                    deleted = true;
                }

                if (!deleted) {
                    if (expression instanceof BinaryExpression) {
                        expression = expressionVisitor.checkAndRepairTopLevelBinaryExpression((BinaryExpression) expression);
                        if (expression == null) {
                            replace.getExpressions().remove(i);
                            replace.getColumns().remove(i);
                        } else {
                            replace.getExpressions().set(i, expression);
                            if (expressionVisitor.getRiddleTask().needToDelete(column)
                                    || expressionVisitor.getRiddleTask().markedAsDeleted(column)) {
                                expressionVisitor.getRiddleTask().markAsDeleted(expression);
                                expressionVisitor.getRiddleTask().markAsDeleted(column);
                                replace.getExpressions().remove(i);
                                replace.getColumns().remove(i);
                            }
                        }
                    } else if (expressionVisitor.getRiddleTask().needToDelete(expression)
                            || expressionVisitor.getRiddleTask().markedAsDeleted(expression)
                            || expressionVisitor.getRiddleTask().needToDelete(column)
                            || expressionVisitor.getRiddleTask().markedAsDeleted(column)) {
                        expressionVisitor.getRiddleTask().markAsDeleted(expression);
                        expressionVisitor.getRiddleTask().markAsDeleted(column);
                        replace.getExpressions().remove(i);
                        replace.getColumns().remove(i);
                    }
                }
            }
        }

    }

    @Override
    public void visit(ExpressionList expressionList) {
        for (int i = expressionList.getExpressions().size() - 1; i >= 0; i--) {
            Expression expression = expressionList.getExpressions().get(i);
            expression.accept(expressionVisitor);
            if (expression instanceof BinaryExpression) {
                expression = expressionVisitor.checkAndRepairTopLevelBinaryExpression((BinaryExpression) expression);
                if (expression == null) {
                    expressionList.getExpressions().remove(i);
                } else {
                    expressionList.getExpressions().set(i, expression);
                }
            } else if (expressionVisitor.getRiddleTask().needToDelete(expression)
                    || expressionVisitor.getRiddleTask().markedAsDeleted(expression)) {
                expressionVisitor.getRiddleTask().markAsDeleted(expression);
                expressionList.getExpressions().remove(i);
            }
        }
    }

    @Override
    public void visit(SubSelect subSelect) {
        subSelect.getSelectBody().accept(selectVisitor);
    }

    public ExpressionRiddler getExpressionVisitor() {
        return expressionVisitor;
    }

    public SelectVisitor getSelectVisitor() {
        return selectVisitor;
    }

    public void setExpressionVisitor(ExpressionRiddler visitor) {
        expressionVisitor = visitor;
    }

    public void setSelectVisitor(SelectVisitor visitor) {
        selectVisitor = visitor;
    }
}
