package com.eas.designer.application.query.editing.riddle;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.update.Update;

/**
 * A class to de-parse (that is, tranform from JSqlParser hierarchy into a
 * string) an {@link net.sf.jsqlparser.statement.update.Update}
 */
public class UpdateRiddler {

    protected ExpressionRiddler expressionVisitor;

    /**
     * @param aVisitor a {@link ExpressionRiddler} to de-parse expressions. It
     * has to share the same<br> StringBuilder (buffer parameter) as this object
     * in order to work
     */
    public UpdateRiddler(ExpressionRiddler aVisitor) {
        expressionVisitor = aVisitor;
    }

    public void riddle(Update update) {
        for (int i = update.getColumns().size() - 1; i >= 0; i--) {
            Column column = update.getColumns().get(i);
            expressionVisitor.visit(column);

            Expression expression = update.getExpressions().get(i);
            expression.accept(expressionVisitor);

            boolean deleted = false;
            EqualsTo dummyRelation = new EqualsTo();
            dummyRelation.setLeftExpression(column);
            dummyRelation.setRightExpression(expression);
            if (expressionVisitor.getRiddleTask().needToDelete(dummyRelation)) {
                update.getColumns().remove(i);
                update.getExpressions().remove(i);
                deleted = true;
            }

            if (!deleted) {
                if (expression instanceof BinaryExpression) {
                    expression = expressionVisitor.checkAndRepairTopLevelBinaryExpression((BinaryExpression) expression);
                    if (expression == null) {
                        update.getExpressions().remove(i);
                        update.getColumns().remove(i);
                    } else {
                        update.getExpressions().set(i, expression);
                        if (expressionVisitor.getRiddleTask().needToDelete(column)
                                || expressionVisitor.getRiddleTask().markedAsDeleted(column)) {
                            expressionVisitor.getRiddleTask().markAsDeleted(expression);
                            expressionVisitor.getRiddleTask().markAsDeleted(column);
                            update.getExpressions().remove(i);
                            update.getColumns().remove(i);
                        }
                    }
                } else if (expressionVisitor.getRiddleTask().needToDelete(expression)
                        || expressionVisitor.getRiddleTask().markedAsDeleted(expression)
                        || expressionVisitor.getRiddleTask().needToDelete(column)
                        || expressionVisitor.getRiddleTask().markedAsDeleted(column)) {
                    expressionVisitor.getRiddleTask().markAsDeleted(expression);
                    expressionVisitor.getRiddleTask().markAsDeleted(column);
                    update.getExpressions().remove(i);
                    update.getColumns().remove(i);
                }
            }
        }

        if (update.getWhere() != null) {
            update.getWhere().accept(expressionVisitor);
            if (update.getWhere() instanceof BinaryExpression) {
                update.setWhere(expressionVisitor.checkAndRepairTopLevelBinaryExpression((BinaryExpression) update.getWhere()));
            } else if (expressionVisitor.getRiddleTask().needToDelete(update.getWhere()) || expressionVisitor.getRiddleTask().markedAsDeleted(update.getWhere())) {
                expressionVisitor.getRiddleTask().markAsDeleted(update.getWhere());
                update.setWhere(null);
            }
        }
    }

    public ExpressionRiddler getExpressionVisitor() {
        return expressionVisitor;
    }

    public void setExpressionVisitor(ExpressionRiddler visitor) {
        expressionVisitor = visitor;
    }
}
