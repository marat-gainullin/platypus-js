package com.eas.designer.application.query.editing.riddle;

import net.sf.jsqlparser.expression.BinaryExpression;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SubSelect;

/**
 * A class to riddle (that is, tranform JSqlParser hierarchy into a new form according to
 * relation/parameter/table deletion).
 * an {@link net.sf.jsqlparser.statement.insert.Insert}
 */
public class InsertRiddler implements ItemsListVisitor {

    protected ExpressionRiddler expressionVisitor;
    protected SelectVisitor selectVisitor;
    protected RiddleTask riddleTask;

    /**
     * @param expressionVisitor a {@link ExpressionRiddler} to de-parse {@link net.sf.jsqlparser.expression.Expression}s. It has to share the same<br>
     * StringBuilder (buffer parameter) as this object in order to work
     * @param selectVisitor a {@link SelectVisitor} to de-parse {@link net.sf.jsqlparser.statement.select.Select}s.
     * It has to share the same<br>
     * StringBuilder (buffer parameter) as this object in order to work
     * @param buffer the buffer that will be filled with the insert
     */
    public InsertRiddler(ExpressionRiddler expressionVisitor, SelectVisitor selectVisitor) {
        this.expressionVisitor = expressionVisitor;
        this.selectVisitor = selectVisitor;
        this.riddleTask = expressionVisitor.getRiddleTask();
    }

    public void riddle(Insert insert) {
        if (insert.getColumns() != null) {
            if (insert.getItemsList() instanceof ExpressionList) {
                ExpressionList expressionList = (ExpressionList) insert.getItemsList();
                assert insert.getColumns().size() == expressionList.getExpressions().size();
                for (int i = insert.getColumns().size() - 1; i >= 0; i--) {
                    Column column = insert.getColumns().get(i);
                    Expression expression = expressionList.getExpressions().get(i);
                    expressionVisitor.visit(column);

                    EqualsTo dummyRelation = new EqualsTo();
                    dummyRelation.setLeftExpression(column);
                    dummyRelation.setRightExpression(expression);
                    if (riddleTask.needToDelete(dummyRelation)) {
                        riddleTask.markAsDeleted(column);
                        riddleTask.markAsDeleted(expression);
                        insert.getColumns().remove(i);
                        expressionList.getExpressions().remove(i);
                    }
                }
            } else {
                for (int i = insert.getColumns().size() - 1; i >= 0; i--) {
                    Column column = insert.getColumns().get(i);
                    expressionVisitor.visit(column);
                }
                insert.getItemsList().accept(this);
            }
        } else {
            insert.getItemsList().accept(this);
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
            } else if (riddleTask.needToDelete(expression) || riddleTask.markedAsDeleted(expression)) {
                riddleTask.markAsDeleted(expression);
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
        if (expressionVisitor != null) {
            riddleTask = expressionVisitor.getRiddleTask();
        }
    }

    public void setSelectVisitor(SelectVisitor visitor) {
        selectVisitor = visitor;
    }
}
