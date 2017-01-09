package net.sf.jsqlparser.util.deparser;

import java.util.Iterator;
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
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.Matches;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.Connect;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SubSelect;

/**
 * A class to de-parse (that is, tranform from JSqlParser hierarchy into a string)
 * an {@link net.sf.jsqlparser.expression.Expression}
 */
public class ExpressionDeParser implements ExpressionVisitor, ItemsListVisitor {

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    protected StringBuilder buffer;
    protected SelectVisitor selectVisitor;
    protected boolean useBracketsInExprList = true;

    public ExpressionDeParser() {
    }

    /**
     * @param aSelectVisitor a SelectVisitor to de-parse SubSelects. It has to share the same<br>
     * StringBuilder as this object in order to work, as:
     * <pre>
     * <code>
     * StringBuilder myBuf = new StringBuilder();
     * MySelectDeparser selectDeparser = new  MySelectDeparser();
     * selectDeparser.setBuffer(myBuf);
     * ExpressionDeParser expressionDeParser = new ExpressionDeParser(selectDeparser, myBuf);
     * </code>
     * </pre>
     * @param aBuffer the buffer that will be filled with the expression
     */
    public ExpressionDeParser(SelectVisitor aSelectVisitor, StringBuilder aBuffer) {
        selectVisitor = aSelectVisitor;
        buffer = aBuffer;
    }

    public StringBuilder getBuffer() {
        return buffer;
    }

    public void setBuffer(StringBuilder aBuffer) {
        buffer = aBuffer;
    }

    public void visit(Addition addition) {
        visitBinaryExpression(addition, (addition.getComment() != null ? " " + addition.getComment() + ExpressionDeParser.LINE_SEPARATOR : "") + " + ");
    }

    public void visit(AndExpression andExpression) {
        visitBinaryExpression(andExpression, (andExpression.getComment() != null ? " " + andExpression.getComment() : "")
                + ExpressionDeParser.LINE_SEPARATOR + " and ");
    }

    public void visit(Between between) {
        between.getLeftExpression().accept(this);
        if (between.isNot()) {
            buffer.append(between.getCommentNot() != null ? " " + between.getCommentNot() + ExpressionDeParser.LINE_SEPARATOR : "").append(" not");
        }

        buffer.append(between.getCommentBetween() != null ? " " + between.getCommentBetween() + ExpressionDeParser.LINE_SEPARATOR : "").append(" between ");
        between.getBetweenExpressionStart().accept(this);
        buffer.append(between.getCommentAnd() != null ? " " + between.getCommentAnd() + ExpressionDeParser.LINE_SEPARATOR : "").append(" and ");
        between.getBetweenExpressionEnd().accept(this);

    }

    public void visit(Division division) {
        visitBinaryExpression(division, (division.getComment() != null ? " " + division.getComment() + ExpressionDeParser.LINE_SEPARATOR : "") + " / ");

    }

    public void visit(DoubleValue doubleValue) {
        buffer.append(doubleValue.getComment() != null ? doubleValue.getComment() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append(doubleValue.getValue());

    }

    public void visit(EqualsTo equalsTo) {
        visitBinaryExpression(equalsTo, (equalsTo.getComment() != null ? " " + equalsTo.getComment() + ExpressionDeParser.LINE_SEPARATOR : "") + " = ");
    }

    public void visit(GreaterThan greaterThan) {
        visitBinaryExpression(greaterThan, (greaterThan.getComment() != null ? " " + greaterThan.getComment() + ExpressionDeParser.LINE_SEPARATOR : "") + " > ");
    }

    public void visit(GreaterThanEquals greaterThanEquals) {
        visitBinaryExpression(greaterThanEquals, (greaterThanEquals.getComment() != null ? " " + greaterThanEquals.getComment() + ExpressionDeParser.LINE_SEPARATOR : "") + " >= ");

    }

    public void visit(InExpression inExpression) {
        inExpression.getLeftExpression().accept(this);
        if (inExpression.isNot()) {
            buffer.append(inExpression.getCommentNot() != null ? " " + inExpression.getCommentNot() + ExpressionDeParser.LINE_SEPARATOR : "").append(" not");
        }
        buffer.append(inExpression.getCommentIn() != null ? " " + inExpression.getCommentIn() + ExpressionDeParser.LINE_SEPARATOR : "").append(" in ");
        inExpression.getItemsList().accept(this);
    }

    public void visit(InverseExpression inverseExpression) {
        buffer.append(inverseExpression.getComment() != null ? inverseExpression.getComment() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append("-");
        inverseExpression.getExpression().accept(this);
    }

    public void visit(IsNullExpression isNullExpression) {
        isNullExpression.getLeftExpression().accept(this);
        if (isNullExpression.isNot()) {
            buffer.append(isNullExpression.getCommentIs() != null ? " " + isNullExpression.getCommentIs() + ExpressionDeParser.LINE_SEPARATOR : "").append(" is").append(isNullExpression.getCommentNot() != null ? " " + isNullExpression.getCommentNot() + ExpressionDeParser.LINE_SEPARATOR : "").append(" not").append(isNullExpression.getCommentNull() != null ? " " + isNullExpression.getCommentNull() + ExpressionDeParser.LINE_SEPARATOR : "").append(" null");
        } else {
            buffer.append(isNullExpression.getCommentIs() != null ? " " + isNullExpression.getCommentIs() + ExpressionDeParser.LINE_SEPARATOR : "").append(" is").append(isNullExpression.getCommentNull() != null ? " " + isNullExpression.getCommentNull() + ExpressionDeParser.LINE_SEPARATOR : "").append(" null");
        }
    }

    public void visit(JdbcParameter jdbcParameter) {
        buffer.append(jdbcParameter.getComment() != null ? jdbcParameter.getComment() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append("?");

    }

    public void visit(NamedParameter namedParameter) {
        buffer.append(namedParameter.toString());
    }

    public void visit(LikeExpression likeExpression) {
        visitBinaryExpression(likeExpression, (likeExpression.getCommentLike() != null ? " " + likeExpression.getCommentLike() + ExpressionDeParser.LINE_SEPARATOR : "") + " Like ");
    }

    public void visit(ExistsExpression existsExpression) {
        if (existsExpression.isNot()) {
            buffer.append(existsExpression.getCommentNot() != null ? " " + existsExpression.getCommentNot() + ExpressionDeParser.LINE_SEPARATOR : "").append(" not").append(existsExpression.getCommentExists() != null ? " " + existsExpression.getCommentExists() + ExpressionDeParser.LINE_SEPARATOR : "").append(" exists ");
        } else {
            buffer.append(existsExpression.getCommentExists() != null ? " " + existsExpression.getCommentExists() + ExpressionDeParser.LINE_SEPARATOR : "").append(" exists ");
        }
        existsExpression.getRightExpression().accept(this);
    }

    public void visit(LongValue longValue) {
        buffer.append(longValue.getComment() != null ? longValue.getComment() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append(longValue.getStringValue());

    }

    public void visit(MinorThan minorThan) {
        visitBinaryExpression(minorThan, (minorThan.getComment() != null ? " " + minorThan.getComment() + ExpressionDeParser.LINE_SEPARATOR : "") + " < ");

    }

    public void visit(MinorThanEquals minorThanEquals) {
        visitBinaryExpression(minorThanEquals, (minorThanEquals.getComment() != null ? " " + minorThanEquals.getComment() + ExpressionDeParser.LINE_SEPARATOR : "") + " <= ");

    }

    public void visit(Multiplication multiplication) {
        visitBinaryExpression(multiplication, (multiplication.getComment() != null ? " " + multiplication.getComment() + ExpressionDeParser.LINE_SEPARATOR : "") + " * ");

    }

    public void visit(NotEqualsTo notEqualsTo) {
        visitBinaryExpression(notEqualsTo, (notEqualsTo.getComment() != null ? " " + notEqualsTo.getComment() + ExpressionDeParser.LINE_SEPARATOR : "") + " <> ");

    }

    public void visit(NullValue nullValue) {
        buffer.append(nullValue.getComment() != null ? nullValue.getComment() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append("null");
    }

    public void visit(OrExpression orExpression) {
        visitBinaryExpression(orExpression, (orExpression.getComment() != null ? " " + orExpression.getComment() + ExpressionDeParser.LINE_SEPARATOR : "") + " or ");
    }

    public void visit(Parenthesis parenthesis) {
        if (parenthesis.isNot()) {
            buffer.append(parenthesis.getCommentNot() != null ? " " + parenthesis.getCommentNot() + ExpressionDeParser.LINE_SEPARATOR : "").append(" not ");
        }
        buffer.append(parenthesis.getCommentBeginBracket() != null ? parenthesis.getCommentBeginBracket() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append("(");
        parenthesis.getExpression().accept(this);
        buffer.append(parenthesis.getCommentEndBracket() != null ? parenthesis.getCommentEndBracket() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append(")");

    }

    public void visit(StringValue stringValue) {
        buffer.append(stringValue.getComment() != null ? stringValue.getComment() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append("'").append(stringValue.getValue()).append("'");
    }

    public void visit(Subtraction subtraction) {
        visitBinaryExpression(subtraction, (subtraction.getComment() != null ? subtraction.getComment() + " " + ExpressionDeParser.LINE_SEPARATOR : "") + "-");
    }

    private void visitBinaryExpression(BinaryExpression binaryExpression, String operator) {
        if (binaryExpression.isNot()) {
            buffer.append(binaryExpression.getCommentNot() != null ? " " + binaryExpression.getCommentNot() + ExpressionDeParser.LINE_SEPARATOR : "").append(" not ");
        }
        binaryExpression.getLeftExpression().accept(this);
        buffer.append(operator);
        binaryExpression.getRightExpression().accept(this);
    }

    public void visit(SubSelect subSelect) {
        buffer.append(subSelect.getCommentBeginBracket() != null ? subSelect.getCommentBeginBracket() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append("(");
        subSelect.getSelectBody().accept(selectVisitor);
        buffer.append(subSelect.getCommentEndBracket() != null ? subSelect.getCommentEndBracket() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append(")");
    }

    public void visit(Column tableColumn) {
        buffer.append(tableColumn.getComment() != null ? tableColumn.getComment() + " " + ExpressionDeParser.LINE_SEPARATOR : "");
        if (tableColumn.getTable().getName() != null) {
            String tableName = tableColumn.getTable().getWholeTableName();
            buffer.append(tableName).append(".");
        }
        buffer.append(tableColumn.getColumnName());
    }

    public void visit(Function function) {
        if (function.isEscaped()) {
            buffer.append(function.getCommentBeginEscaped() != null ? function.getCommentBeginEscaped() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append("{fn ");
        }
        buffer.append(function.getCommentName() != null ? function.getCommentName() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append(function.getName());
        if (function.isAllColumns()) {
            buffer.append(function.getCommentBeginEscaped() != null ? function.getCommentBeginBracket() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append("(").append(function.getCommentBeginEscaped() != null ? function.getCommentAllColumns() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append("*").append(function.getCommentBeginEscaped() != null ? function.getCommentEndBracket() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append(")");
        } else if (function.getParameters() == null) {
            buffer.append(function.getCommentBeginEscaped() != null ? function.getCommentBeginBracket() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append("(").append(function.getCommentEndEscaped() != null ? function.getCommentEndBracket() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append(")");
        } else {
            boolean oldUseBracketsInExprList = useBracketsInExprList;
            if (function.isDistinct()) {
                useBracketsInExprList = false;
                buffer.append(function.getCommentBeginEscaped() != null ? function.getCommentBeginBracket() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append("(").append(function.getCommentDistinct() != null ? function.getCommentDistinct() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append("Distinct ");
            }
            visit(function.getParameters());
            useBracketsInExprList = oldUseBracketsInExprList;
            if (function.isDistinct()) {
                buffer.append(function.getCommentBeginEscaped() != null ? function.getCommentEndBracket() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append(")");
            }
        }
        if (function.isEscaped()) {
            buffer.append(function.getCommentEndEscaped() != null ? function.getCommentEndEscaped() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append("}");
        }
    }

    public void visit(ExpressionList expressionList) {
        if (useBracketsInExprList) {
            buffer.append("(");
        }
        for (int i = 0; i < expressionList.getExpressions().size(); i++) {
            Expression expression = (Expression) expressionList.getExpressions().get(i);
            expression.accept(this);
            buffer.append((i < expressionList.getExpressions().size() - 1) ? (!"".equals(expressionList.getCommentsComma().get(i)) ? " "
                    + expressionList.getCommentsComma().get(i) + ExpressionDeParser.LINE_SEPARATOR : "") + ", " : "");
        }
        if (useBracketsInExprList) {
            buffer.append(expressionList.getCommentEndBracket() != null ? expressionList.getCommentEndBracket() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append(")");
        }
    }

    public SelectVisitor getSelectVisitor() {
        return selectVisitor;
    }

    public void setSelectVisitor(SelectVisitor visitor) {
        selectVisitor = visitor;
    }

    public void visit(DateValue dateValue) {
        buffer.append(dateValue.getComment() != null ? dateValue.getComment() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append("{d '").append(dateValue.getValue().toString()).append("'}");
    }

    public void visit(TimestampValue timestampValue) {
        buffer.append(timestampValue.getComment() != null ? timestampValue.getComment() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append("{ts '").append(timestampValue.getValue().toString()).append("'}");
    }

    public void visit(TimeValue timeValue) {
        buffer.append(timeValue.getComment() != null ? timeValue.getComment() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append("{t '").append(timeValue.getValue().toString()).append("'}");
    }

    public void visit(CaseExpression caseExpression) {
        buffer.append(caseExpression.getCommentCase() != null ? caseExpression.getCommentElse() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append("Case ");
        Expression switchExp = caseExpression.getSwitchExpression();
        if (switchExp != null) {
            switchExp.accept(this);
        }
        List clauses = caseExpression.getWhenClauses();
        for (Iterator iter = clauses.iterator(); iter.hasNext();) {
            Expression exp = (Expression) iter.next();
            exp.accept(this);
        }
        Expression elseExp = caseExpression.getElseExpression();
        if (elseExp != null) {
            elseExp.accept(this);
        }
        buffer.append(caseExpression.getCommentEnd() != null ? " " + caseExpression.getCommentEnd() + ExpressionDeParser.LINE_SEPARATOR : "").append(" End");
    }

    public void visit(WhenClause whenClause) {
        buffer.append(whenClause.getCommentWhen() != null ? " " + whenClause.getCommentWhen() + ExpressionDeParser.LINE_SEPARATOR : "").append(" When ");
        whenClause.getWhenExpression().accept(this);
        buffer.append(whenClause.getCommentThen() != null ? " " + whenClause.getCommentThen() + ExpressionDeParser.LINE_SEPARATOR : "").append(" Then ");
        whenClause.getThenExpression().accept(this);
    }

    public void visit(AllComparisonExpression allComparisonExpression) {
        buffer.append(allComparisonExpression.getComment() != null ? " " + allComparisonExpression.getComment() + ExpressionDeParser.LINE_SEPARATOR : "").append(" all ");
        allComparisonExpression.GetSubSelect().accept((ExpressionVisitor) this);
    }

    public void visit(AnyComparisonExpression anyComparisonExpression) {
        buffer.append(anyComparisonExpression.getComment() != null ? " " + anyComparisonExpression.getComment() + ExpressionDeParser.LINE_SEPARATOR : "").append(" any ");
        anyComparisonExpression.GetSubSelect().accept((ExpressionVisitor) this);
    }

    public void visit(Concat concat) {
        visitBinaryExpression(concat, (concat.getComment() != null ? " " + concat.getComment() + ExpressionDeParser.LINE_SEPARATOR : "") + " || ");
    }

    public void visit(Matches matches) {
        visitBinaryExpression(matches, (matches.getComment() != null ? " " + matches.getComment() + ExpressionDeParser.LINE_SEPARATOR : "") + " @@ ");
    }

    public void visit(BitwiseAnd bitwiseAnd) {
        visitBinaryExpression(bitwiseAnd, (bitwiseAnd.getComment() != null ? " " + bitwiseAnd.getComment() + ExpressionDeParser.LINE_SEPARATOR : "") + " & ");
    }

    public void visit(BitwiseOr bitwiseOr) {
        visitBinaryExpression(bitwiseOr, (bitwiseOr.getComment() != null ? " " + bitwiseOr.getComment() + ExpressionDeParser.LINE_SEPARATOR : "") + " | ");
    }

    public void visit(BitwiseXor bitwiseXor) {
        visitBinaryExpression(bitwiseXor, (bitwiseXor.getComment() != null ? " " + bitwiseXor.getComment() + ExpressionDeParser.LINE_SEPARATOR : "") + " ^ ");
    }

    public void visit(Connect aConnect) {
        buffer.append(" ").append(aConnect.toString());
    }
}
