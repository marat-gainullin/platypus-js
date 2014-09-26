package com.eas.designer.application.query.editing.riddle;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.statement.delete.Delete;

/**
 * A class to de-parse (that is, tranform from JSqlParser hierarchy into a
 * string) a {@link net.sf.jsqlparser.statement.delete.Delete}
 */
public class DeleteRiddler {

    protected ExpressionRiddler expressionVisitor;

    /**
     * @param expressionVisitor a {@link ExpressionRiddler} to riddle
     * expressions.
     */
    public DeleteRiddler(ExpressionRiddler expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
    }

    public void riddle(Delete delete) {
        if (delete.getWhere() != null) {
            delete.getWhere().accept(expressionVisitor);
            if (delete.getWhere() instanceof BinaryExpression) {
                delete.setWhere(expressionVisitor.checkAndRepairTopLevelBinaryExpression((BinaryExpression) delete.getWhere()));
            } else if (expressionVisitor.getRiddleTask().needToDelete(delete.getWhere())
                    || expressionVisitor.getRiddleTask().markedAsDeleted(delete.getWhere())) {
                expressionVisitor.getRiddleTask().markAsDeleted(delete.getWhere());
                delete.setWhere(null);
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
