package com.eas.designer.application.query.editing.riddle;

import java.util.List;
import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.InverseExpression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NamedParameter;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.Matches;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.Connect;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SubSelect;

/**
 * A class to de-parse (that is, tranform from JSqlParser hierarchy into a
 * string) an {@link net.sf.jsqlparser.expression.Expression}
 */
public class ExpressionRiddler implements ExpressionVisitor, ItemsListVisitor {

    protected SelectVisitor selectVisitor;
    protected RiddleTask riddleTask;

    /**
     * @param selectVisitor a SelectVisitor to de-parse SubSelects. It has to
     * share the same<br> StringBuilder as this object in order to work, as:      <pre>
     * <code>
     * FinderTask myTask = new FinderTask();
     * MySelectRiddler selectRiddler = new  MySelectRiddler();
     * ExpressionRiddler expressionDeParser = new ExpressionRiddler(selectRiddler, myTask);
     * </code>
     * </pre>
     * @param buffer the buffer that will be filled with the expression
     */
    public ExpressionRiddler(SelectVisitor selectVisitor, RiddleTask riddleTask) {
        this.selectVisitor = selectVisitor;
        this.riddleTask = riddleTask;
    }

    public RiddleTask getRiddleTask() {
        return riddleTask;
    }

    public Expression checkAndRepairTopLevelBinaryExpression(BinaryExpression binaryExpression) {
        if (!riddleTask.markedAsDeleted(binaryExpression)) {
            boolean leftDeleted = riddleTask.markedAsDeleted(binaryExpression.getLeftExpression());
            boolean rightDeleted = riddleTask.markedAsDeleted(binaryExpression.getRightExpression());
            if (leftDeleted || rightDeleted) {
                riddleTask.markAsDeleted(binaryExpression);
                if (leftDeleted && !rightDeleted) {
                    return binaryExpression.getRightExpression();
                } else if (rightDeleted && !leftDeleted) {
                    return binaryExpression.getLeftExpression();
                }
            } else {
                return binaryExpression;
            }
        }
        return null;
    }

    protected void checkAndRepairBinaryExpression(BinaryExpression binaryExpression) {
        // Здесь надо проверить, а не побитые ли у этого оператора дочерние выражения
        // и, если это так, попробовать восстановить этот оператор с помощью одной из
        // частей побитого дочернего выражения.
        // Если же не осталось ни одной части, то удалить его совсем.
        if (!riddleTask.markedAsDeleted(binaryExpression.getLeftExpression())
                && !riddleTask.markedAsDeleted(binaryExpression.getRightExpression())) {
            Expression toExamine = binaryExpression.getLeftExpression();
            if (toExamine instanceof BinaryExpression) {
                BinaryExpression bToExamine = (BinaryExpression) toExamine;
                boolean leftDeleted = riddleTask.markedAsDeleted(bToExamine.getLeftExpression());
                boolean rightDeleted = riddleTask.markedAsDeleted(bToExamine.getRightExpression());
                if (leftDeleted || rightDeleted) {
                    riddleTask.markAsDeleted(bToExamine);
                    if (leftDeleted && !rightDeleted) {
                        binaryExpression.setLeftExpression(bToExamine.getRightExpression());
                    } else if (rightDeleted && !leftDeleted) {
                        binaryExpression.setLeftExpression(bToExamine.getLeftExpression());
                    }
                }
            }
            toExamine = binaryExpression.getRightExpression();
            if (toExamine instanceof BinaryExpression) {
                BinaryExpression bToExamine = (BinaryExpression) toExamine;
                boolean leftDeleted = riddleTask.markedAsDeleted(bToExamine.getLeftExpression());
                boolean rightDeleted = riddleTask.markedAsDeleted(bToExamine.getRightExpression());
                if (leftDeleted || rightDeleted) {
                    riddleTask.markAsDeleted(bToExamine);
                    if (leftDeleted && !rightDeleted) {
                        binaryExpression.setRightExpression(bToExamine.getRightExpression());
                    } else if (rightDeleted && !leftDeleted) {
                        binaryExpression.setRightExpression(bToExamine.getLeftExpression());
                    }
                }
            }
        }
    }

    @Override
    public void visit(Addition addition) {
        visitBinaryExpression(addition);
    }

    @Override
    public void visit(AndExpression andExpression) {
        visitBinaryExpression(andExpression);
    }

    @Override
    public void visit(Between between) {
        between.getLeftExpression().accept(this);
        between.getBetweenExpressionStart().accept(this);
        between.getBetweenExpressionEnd().accept(this);

        // left expression
        if (between.getLeftExpression() instanceof BinaryExpression) {
            between.setLeftExpression(checkAndRepairTopLevelBinaryExpression((BinaryExpression) between.getLeftExpression()));
        } else if (riddleTask.needToDelete(between.getLeftExpression()) || riddleTask.markedAsDeleted(between.getLeftExpression())) {
            riddleTask.markAsDeleted(between.getLeftExpression());
            between.setLeftExpression(null);
        }
        // start expression
        if (between.getBetweenExpressionStart() instanceof BinaryExpression) {
            between.setBetweenExpressionStart(checkAndRepairTopLevelBinaryExpression((BinaryExpression) between.getBetweenExpressionStart()));
        } else if (riddleTask.needToDelete(between.getBetweenExpressionStart()) || riddleTask.markedAsDeleted(between.getBetweenExpressionStart())) {
            riddleTask.markAsDeleted(between.getBetweenExpressionStart());
            between.setBetweenExpressionStart(null);
        }
        // end expression
        if (between.getBetweenExpressionEnd() instanceof BinaryExpression) {
            between.setBetweenExpressionEnd(checkAndRepairTopLevelBinaryExpression((BinaryExpression) between.getBetweenExpressionEnd()));
        } else if (riddleTask.needToDelete(between.getBetweenExpressionEnd()) || riddleTask.markedAsDeleted(between.getBetweenExpressionEnd())) {
            riddleTask.markAsDeleted(between.getBetweenExpressionEnd());
            between.setBetweenExpressionEnd(null);
        }

        if (between.getLeftExpression() == null
                || between.getBetweenExpressionStart() == null
                || between.getBetweenExpressionEnd() == null) {
            riddleTask.markAsDeleted(between);
        }
    }

    @Override
    public void visit(Division division) {
        visitBinaryExpression(division);
    }

    @Override
    public void visit(DoubleValue doubleValue) {
    }

    @Override
    public void visit(EqualsTo equalsTo) {
        visitBinaryExpression(equalsTo);
    }

    @Override
    public void visit(GreaterThan greaterThan) {
        visitBinaryExpression(greaterThan);
    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {
        visitBinaryExpression(greaterThanEquals);
    }

    @Override
    public void visit(InExpression inExpression) {
        inExpression.getLeftExpression().accept(this);
        inExpression.getItemsList().accept(this);

        if (inExpression.getLeftExpression() instanceof BinaryExpression) {
            inExpression.setLeftExpression(checkAndRepairTopLevelBinaryExpression((BinaryExpression) inExpression.getLeftExpression()));
        } else if (riddleTask.needToDelete(inExpression.getLeftExpression()) || riddleTask.markedAsDeleted(inExpression.getLeftExpression())) {
            riddleTask.markAsDeleted(inExpression.getLeftExpression());
            inExpression.setLeftExpression(null);
        }

        // check validity
        if (inExpression.getLeftExpression() == null) {
            riddleTask.markAsDeleted(inExpression);
        }
        if (inExpression.getItemsList() instanceof ExpressionList) {
            ExpressionList el = (ExpressionList) inExpression.getItemsList();
            if (el.getExpressions().isEmpty()) {
                riddleTask.markAsDeleted(inExpression);
            }
        }
    }

    @Override
    public void visit(InverseExpression inverseExpression) {
        inverseExpression.getExpression().accept(this);
        if (inverseExpression.getExpression() instanceof BinaryExpression) {
            inverseExpression.setExpression(checkAndRepairTopLevelBinaryExpression((BinaryExpression) inverseExpression.getExpression()));
        } else if (riddleTask.needToDelete(inverseExpression.getExpression()) || riddleTask.markedAsDeleted(inverseExpression.getExpression())) {
            riddleTask.markAsDeleted(inverseExpression.getExpression());
            inverseExpression.setExpression(null);
        }
        if (inverseExpression.getExpression() == null || riddleTask.needToDelete(inverseExpression)) {
            riddleTask.markAsDeleted(inverseExpression);
        }
    }

    @Override
    public void visit(IsNullExpression isNullExpression) {
        isNullExpression.getLeftExpression().accept(this);
        if (isNullExpression.getLeftExpression() instanceof BinaryExpression) {
            isNullExpression.setLeftExpression(checkAndRepairTopLevelBinaryExpression((BinaryExpression) isNullExpression.getLeftExpression()));
        } else if (riddleTask.needToDelete(isNullExpression.getLeftExpression()) || riddleTask.markedAsDeleted(isNullExpression.getLeftExpression())) {
            riddleTask.markAsDeleted(isNullExpression.getLeftExpression());
            isNullExpression.setLeftExpression(null);
        }
        if (isNullExpression.getLeftExpression() == null) {
            riddleTask.markAsDeleted(isNullExpression);
        }
    }

    @Override
    public void visit(JdbcParameter jdbcParameter) {
    }

    @Override
    public void visit(LikeExpression likeExpression) {
        visitBinaryExpression(likeExpression);
    }

    @Override
    public void visit(ExistsExpression existsExpression) {
        existsExpression.getRightExpression().accept(this);
        if (existsExpression.getRightExpression() instanceof BinaryExpression) {
            existsExpression.setRightExpression(checkAndRepairTopLevelBinaryExpression((BinaryExpression) existsExpression.getRightExpression()));
        } else if (riddleTask.needToDelete(existsExpression.getRightExpression()) || riddleTask.markedAsDeleted(existsExpression.getRightExpression())) {
            riddleTask.markAsDeleted(existsExpression.getRightExpression());
            existsExpression.setRightExpression(null);
        }
        if (existsExpression.getRightExpression() == null) {
            riddleTask.markAsDeleted(existsExpression);
        }
    }

    @Override
    public void visit(LongValue longValue) {
    }

    @Override
    public void visit(MinorThan minorThan) {
        visitBinaryExpression(minorThan);

    }

    @Override
    public void visit(MinorThanEquals minorThanEquals) {
        visitBinaryExpression(minorThanEquals);

    }

    @Override
    public void visit(Multiplication multiplication) {
        visitBinaryExpression(multiplication);
    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) {
        visitBinaryExpression(notEqualsTo);
    }

    @Override
    public void visit(NullValue nullValue) {
    }

    @Override
    public void visit(OrExpression orExpression) {
        visitBinaryExpression(orExpression);
    }

    @Override
    public void visit(Parenthesis parenthesis) {
        parenthesis.getExpression().accept(this);
        if (parenthesis.getExpression() instanceof BinaryExpression) {
            parenthesis.setExpression(checkAndRepairTopLevelBinaryExpression((BinaryExpression) parenthesis.getExpression()));
        } else if (riddleTask.needToDelete(parenthesis.getExpression()) || riddleTask.markedAsDeleted(parenthesis.getExpression())) {
            riddleTask.markAsDeleted(parenthesis.getExpression());
            parenthesis.setExpression(null);
        }
        if (parenthesis.getExpression() == null || riddleTask.needToDelete(parenthesis)) {
            riddleTask.markAsDeleted(parenthesis);
        }
    }

    @Override
    public void visit(StringValue stringValue) {
    }

    @Override
    public void visit(Subtraction subtraction) {
        visitBinaryExpression(subtraction);
    }

    private void visitBinaryExpression(BinaryExpression binaryExpression) {
        binaryExpression.getLeftExpression().accept(this);
        binaryExpression.getRightExpression().accept(this);
        if (riddleTask.needToDelete(binaryExpression)) {
            riddleTask.markAsDeleted(binaryExpression);
        } else {
            checkAndRepairBinaryExpression(binaryExpression);
        }
    }

    @Override
    public void visit(Column tableColumn) {
        if (riddleTask.needToDelete(tableColumn)) {
            riddleTask.markAsDeleted(tableColumn);
        }
    }

    @Override
    public void visit(NamedParameter namedParameter) {
        if (riddleTask.needToDelete(namedParameter)) {
            riddleTask.markAsDeleted(namedParameter);
        }
    }

    @Override
    public void visit(SubSelect subSelect) {
        subSelect.getSelectBody().accept(selectVisitor);
    }

    @Override
    public void visit(Function function) {
        if (!function.isAllColumns() && function.getParameters() != null) {
            visit(function.getParameters());
        }
    }

    @Override
    public void visit(ExpressionList expressionList) {
        for (int i = expressionList.getExpressions().size() - 1; i >= 0; i--) {
            Expression expression = expressionList.getExpressions().get(i);
            expression.accept(this);
            if (expression instanceof BinaryExpression) {
                expression = checkAndRepairTopLevelBinaryExpression((BinaryExpression) expression);
                if (expression != null) {
                    expressionList.getExpressions().set(i, expression);
                } else {
                    expressionList.getExpressions().remove(i);
                }
            } else if (riddleTask.needToDelete(expression) || riddleTask.markedAsDeleted(expression)) {
                riddleTask.markAsDeleted(expression);
                expressionList.getExpressions().remove(i);
            }
        }
    }

    public SelectVisitor getSelectVisitor() {
        return selectVisitor;
    }

    public void setSelectVisitor(SelectVisitor visitor) {
        selectVisitor = visitor;
    }

    @Override
    public void visit(DateValue dateValue) {
    }

    @Override
    public void visit(TimestampValue timestampValue) {
    }

    @Override
    public void visit(TimeValue timeValue) {
    }

    @Override
    public void visit(CaseExpression caseExpression) {
        Expression switchExp = caseExpression.getSwitchExpression();
        if (switchExp != null) {
            switchExp.accept(this);
            if (switchExp instanceof BinaryExpression) {
                caseExpression.setSwitchExpression(checkAndRepairTopLevelBinaryExpression((BinaryExpression) switchExp));
            } else if (riddleTask.needToDelete(switchExp) || riddleTask.markedAsDeleted(switchExp)) {
                riddleTask.markAsDeleted(switchExp);
                caseExpression.setSwitchExpression(null);
            }
        }

        List<WhenClause> clauses = caseExpression.getWhenClauses();
        for (int i = clauses.size() - 1; i >= 0; i--) {
            WhenClause exp = clauses.get(i);
            visit(exp);
            if (exp.getThenExpression() == null || exp.getWhenExpression() == null) {
                clauses.remove(i);
            }
        }

        Expression elseExp = caseExpression.getElseExpression();
        if (elseExp != null) {
            elseExp.accept(this);

            if (elseExp instanceof BinaryExpression) {
                caseExpression.setElseExpression(checkAndRepairTopLevelBinaryExpression((BinaryExpression) elseExp));
            } else if (riddleTask.needToDelete(elseExp) || riddleTask.markedAsDeleted(elseExp)) {
                riddleTask.markAsDeleted(elseExp);
                caseExpression.setElseExpression(null);
            }
        }

        if (caseExpression.getSwitchExpression() == null || caseExpression.getWhenClauses().isEmpty()) {
            riddleTask.markAsDeleted(caseExpression);
        }
    }

    @Override
    public void visit(WhenClause whenClause) {
        whenClause.getWhenExpression().accept(this);
        whenClause.getThenExpression().accept(this);

        if (whenClause.getWhenExpression() instanceof BinaryExpression) {
            whenClause.setWhenExpression(checkAndRepairTopLevelBinaryExpression((BinaryExpression) whenClause.getWhenExpression()));
        } else if (riddleTask.needToDelete(whenClause.getWhenExpression()) || riddleTask.markedAsDeleted(whenClause.getWhenExpression())) {
            riddleTask.markAsDeleted(whenClause.getWhenExpression());
            whenClause.setWhenExpression(null);
        }
        if (whenClause.getThenExpression() instanceof BinaryExpression) {
            whenClause.setThenExpression(checkAndRepairTopLevelBinaryExpression((BinaryExpression) whenClause.getThenExpression()));
        } else if (riddleTask.needToDelete(whenClause.getThenExpression()) || riddleTask.markedAsDeleted(whenClause.getThenExpression())) {
            riddleTask.markAsDeleted(whenClause.getThenExpression());
            whenClause.setThenExpression(null);
        }
        if (whenClause.getThenExpression() == null || whenClause.getWhenExpression() == null) {
            riddleTask.markAsDeleted(whenClause);
        }
    }

    @Override
    public void visit(AllComparisonExpression allComparisonExpression) {
        allComparisonExpression.GetSubSelect().accept((ExpressionVisitor) this);
    }

    @Override
    public void visit(AnyComparisonExpression anyComparisonExpression) {
        anyComparisonExpression.GetSubSelect().accept((ExpressionVisitor) this);
    }

    @Override
    public void visit(Concat concat) {
        visitBinaryExpression(concat);
    }

    @Override
    public void visit(Matches matches) {
        visitBinaryExpression(matches);
    }

    @Override
    public void visit(BitwiseAnd bitwiseAnd) {
        visitBinaryExpression(bitwiseAnd);
    }

    @Override
    public void visit(BitwiseOr bitwiseOr) {
        visitBinaryExpression(bitwiseOr);
    }

    @Override
    public void visit(BitwiseXor bitwiseXor) {
        visitBinaryExpression(bitwiseXor);
    }

    @Override
    public void visit(Connect aConnect) {
        aConnect.getStartWith().accept(this);
        aConnect.getConnectBy().accept(this);

        if (aConnect.getStartWith() instanceof BinaryExpression) {
            aConnect.setStartWith(checkAndRepairTopLevelBinaryExpression((BinaryExpression) aConnect.getStartWith()));
        } else if (riddleTask.needToDelete(aConnect.getStartWith()) || riddleTask.markedAsDeleted(aConnect.getStartWith())) {
            riddleTask.markAsDeleted(aConnect.getStartWith());
            aConnect.setStartWith(null);
        }
        if (aConnect.getConnectBy() instanceof BinaryExpression) {
            aConnect.setConnectBy(checkAndRepairTopLevelBinaryExpression((BinaryExpression) aConnect.getConnectBy()));
        } else if (riddleTask.needToDelete(aConnect.getConnectBy()) || riddleTask.markedAsDeleted(aConnect.getConnectBy())) {
            riddleTask.markAsDeleted(aConnect.getConnectBy());
            aConnect.setConnectBy(null);
        }
        if (aConnect.getConnectBy() == null) {
            riddleTask.markAsDeleted(aConnect);
        }
    }
}
