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
import net.sf.jsqlparser.statement.select.Distinct;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SubSelect;

/**
 * A class to de-parse (that is, tranform from JSqlParser hierarchy into a string)
 * an {@link net.sf.jsqlparser.expression.Expression}
 */
public class ExpressionDeParser implements ExpressionVisitor, ItemsListVisitor {

    public static final String EOL = System.getProperty("line.separator", "\n");
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

    @Override
    public void visit(Addition addition) {
        visitBinaryExpression(addition, (addition.getComment() != null ? " " + addition.getComment() + ExpressionDeParser.EOL : "") + " + ");
    }

    @Override
    public void visit(AndExpression andExpression) {
        visitBinaryExpression(andExpression, (andExpression.getComment() != null ? " " + andExpression.getComment() : "")
                + ExpressionDeParser.EOL + " and ");
    }

    @Override
    public void visit(Between between) {
        between.getLeftExpression().accept(this);
        if (between.isNot()) {
            buffer.append(between.getCommentNot() != null ? " " + between.getCommentNot() + ExpressionDeParser.EOL : "").append(" not");
        }

        buffer.append(between.getCommentBetween() != null ? " " + between.getCommentBetween() + ExpressionDeParser.EOL : "").append(" between ");
        between.getBetweenExpressionStart().accept(this);
        buffer.append(between.getCommentAnd() != null ? " " + between.getCommentAnd() + ExpressionDeParser.EOL : "").append(" and ");
        between.getBetweenExpressionEnd().accept(this);

    }

    @Override
    public void visit(Division division) {
        visitBinaryExpression(division, (division.getComment() != null ? " " + division.getComment() + ExpressionDeParser.EOL : "") + " / ");

    }

    @Override
    public void visit(DoubleValue doubleValue) {
        buffer.append(doubleValue.getComment() != null ? doubleValue.getComment() + " " + ExpressionDeParser.EOL : "").append(doubleValue.getValue());

    }

    @Override
    public void visit(EqualsTo equalsTo) {
        visitBinaryExpression(equalsTo, (equalsTo.getComment() != null ? " " + equalsTo.getComment() + ExpressionDeParser.EOL : "") + " = ");
    }

    @Override
    public void visit(GreaterThan greaterThan) {
        visitBinaryExpression(greaterThan, (greaterThan.getComment() != null ? " " + greaterThan.getComment() + ExpressionDeParser.EOL : "") + " > ");
    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {
        visitBinaryExpression(greaterThanEquals, (greaterThanEquals.getComment() != null ? " " + greaterThanEquals.getComment() + ExpressionDeParser.EOL : "") + " >= ");

    }

    @Override
    public void visit(InExpression inExpression) {
        inExpression.getLeftExpression().accept(this);
        if (inExpression.isNot()) {
            buffer.append(inExpression.getCommentNot() != null ? " " + inExpression.getCommentNot() + ExpressionDeParser.EOL : "").append(" not");
        }
        buffer.append(inExpression.getCommentIn() != null ? " " + inExpression.getCommentIn() + ExpressionDeParser.EOL : "").append(" in ");
        inExpression.getItemsList().accept(this);
    }

    @Override
    public void visit(InverseExpression inverseExpression) {
        buffer.append(inverseExpression.getComment() != null ? inverseExpression.getComment() + " " + ExpressionDeParser.EOL : "").append("-");
        inverseExpression.getExpression().accept(this);
    }

    @Override
    public void visit(IsNullExpression isNullExpression) {
        isNullExpression.getLeftExpression().accept(this);
        if (isNullExpression.isNot()) {
            buffer.append(isNullExpression.getCommentIs() != null ? " " + isNullExpression.getCommentIs() + ExpressionDeParser.EOL : "").append(" is").append(isNullExpression.getCommentNot() != null ? " " + isNullExpression.getCommentNot() + ExpressionDeParser.EOL : "").append(" not").append(isNullExpression.getCommentNull() != null ? " " + isNullExpression.getCommentNull() + ExpressionDeParser.EOL : "").append(" null");
        } else {
            buffer.append(isNullExpression.getCommentIs() != null ? " " + isNullExpression.getCommentIs() + ExpressionDeParser.EOL : "").append(" is").append(isNullExpression.getCommentNull() != null ? " " + isNullExpression.getCommentNull() + ExpressionDeParser.EOL : "").append(" null");
        }
    }

    @Override
    public void visit(JdbcParameter jdbcParameter) {
        buffer.append(jdbcParameter.getComment() != null ? jdbcParameter.getComment() + " " + ExpressionDeParser.EOL : "").append("?");

    }

    @Override
    public void visit(NamedParameter namedParameter) {
        buffer.append(namedParameter.toString());
    }

    @Override
    public void visit(LikeExpression likeExpression) {
        visitBinaryExpression(likeExpression, (likeExpression.getCommentLike() != null ? " " + likeExpression.getCommentLike() + ExpressionDeParser.EOL : "") + " Like ");
    }

    @Override
    public void visit(ExistsExpression existsExpression) {
        if (existsExpression.isNot()) {
            buffer.append(existsExpression.getCommentNot() != null ? " " + existsExpression.getCommentNot() + ExpressionDeParser.EOL : "").append(" not").append(existsExpression.getCommentExists() != null ? " " + existsExpression.getCommentExists() + ExpressionDeParser.EOL : "").append(" exists ");
        } else {
            buffer.append(existsExpression.getCommentExists() != null ? " " + existsExpression.getCommentExists() + ExpressionDeParser.EOL : "").append(" exists ");
        }
        existsExpression.getRightExpression().accept(this);
    }

    @Override
    public void visit(LongValue longValue) {
        buffer.append(longValue.getComment() != null ? longValue.getComment() + " " + ExpressionDeParser.EOL : "").append(longValue.getStringValue());

    }

    @Override
    public void visit(MinorThan minorThan) {
        visitBinaryExpression(minorThan, (minorThan.getComment() != null ? " " + minorThan.getComment() + ExpressionDeParser.EOL : "") + " < ");

    }

    @Override
    public void visit(MinorThanEquals minorThanEquals) {
        visitBinaryExpression(minorThanEquals, (minorThanEquals.getComment() != null ? " " + minorThanEquals.getComment() + ExpressionDeParser.EOL : "") + " <= ");

    }

    @Override
    public void visit(Multiplication multiplication) {
        visitBinaryExpression(multiplication, (multiplication.getComment() != null ? " " + multiplication.getComment() + ExpressionDeParser.EOL : "") + " * ");

    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) {
        visitBinaryExpression(notEqualsTo, (notEqualsTo.getComment() != null ? " " + notEqualsTo.getComment() + ExpressionDeParser.EOL : "") + " <> ");

    }

    @Override
    public void visit(NullValue nullValue) {
        buffer.append(nullValue.getComment() != null ? nullValue.getComment() + " " + ExpressionDeParser.EOL : "").append("null");
    }

    @Override
    public void visit(OrExpression orExpression) {
        visitBinaryExpression(orExpression, (orExpression.getComment() != null ? " " + orExpression.getComment() + ExpressionDeParser.EOL : "") + " or ");
    }

    @Override
    public void visit(Parenthesis parenthesis) {
        if (parenthesis.isNot()) {
            buffer.append(parenthesis.getCommentNot() != null ? " " + parenthesis.getCommentNot() + ExpressionDeParser.EOL : "").append(" not ");
        }
        buffer.append(parenthesis.getCommentBeginBracket() != null ? parenthesis.getCommentBeginBracket() + " " + ExpressionDeParser.EOL : "").append("(");
        parenthesis.getExpression().accept(this);
        buffer.append(parenthesis.getCommentEndBracket() != null ? parenthesis.getCommentEndBracket() + " " + ExpressionDeParser.EOL : "").append(")");

    }

    @Override
    public void visit(StringValue stringValue) {
        buffer.append(stringValue.getComment() != null ? stringValue.getComment() + " " + ExpressionDeParser.EOL : "").append("'").append(stringValue.getValue()).append("'");
    }

    @Override
    public void visit(Subtraction subtraction) {
        visitBinaryExpression(subtraction, (subtraction.getComment() != null ? subtraction.getComment() + " " + ExpressionDeParser.EOL : "") + "-");
    }

    private void visitBinaryExpression(BinaryExpression binaryExpression, String operator) {
        if (binaryExpression.isNot()) {
            buffer.append(binaryExpression.getCommentNot() != null ? " " + binaryExpression.getCommentNot() + ExpressionDeParser.EOL : "").append(" not ");
        }
        binaryExpression.getLeftExpression().accept(this);
        buffer.append(operator);
        binaryExpression.getRightExpression().accept(this);
    }

    @Override
    public void visit(SubSelect subSelect) {
        buffer.append(subSelect.getCommentBeginBracket() != null ? subSelect.getCommentBeginBracket() + " " + ExpressionDeParser.EOL : "").append("(");
        subSelect.getSelectBody().accept(selectVisitor);
        buffer.append(subSelect.getCommentEndBracket() != null ? subSelect.getCommentEndBracket() + " " + ExpressionDeParser.EOL : "").append(")");
    }

    @Override
    public void visit(Column tableColumn) {
        buffer.append(tableColumn.getComment() != null ? tableColumn.getComment() + " " + ExpressionDeParser.EOL : "");
        if (tableColumn.getTable().getName() != null) {
            String tableName = tableColumn.getTable().getWholeTableName();
            buffer.append(tableName).append(".");
        }
        buffer.append(tableColumn.getColumnName());
    }

    @Override
    public void visit(Function function) {
        if (function.isEscaped()) {
            buffer.append(function.getCommentBeginEscaped() != null ? function.getCommentBeginEscaped() + " " + ExpressionDeParser.EOL : "").append("{fn ");
        }
        buffer.append(function.getCommentName() != null ? function.getCommentName() + " " + ExpressionDeParser.EOL : "").append(function.getName());
        if (function.isAllColumns()) {
            buffer.append(function.getCommentBeginEscaped() != null ? function.getCommentBeginBracket() + " " + ExpressionDeParser.EOL : "").append("(").append(function.getCommentBeginEscaped() != null ? function.getCommentAllColumns() + " " + ExpressionDeParser.EOL : "").append("*").append(function.getCommentBeginEscaped() != null ? function.getCommentEndBracket() + " " + ExpressionDeParser.EOL : "").append(")");
        } else if (function.getParameters() == null) {
            buffer.append(function.getCommentBeginEscaped() != null ? function.getCommentBeginBracket() + " " + ExpressionDeParser.EOL : "").append("(").append(function.getCommentEndEscaped() != null ? function.getCommentEndBracket() + " " + ExpressionDeParser.EOL : "").append(")");
        } else {
            boolean oldUseBracketsInExprList = useBracketsInExprList;
            if (function.isDistinct()) {
                useBracketsInExprList = false;
                buffer.append(function.getCommentBeginEscaped() != null ? function.getCommentBeginBracket() + " " + ExpressionDeParser.EOL : "").append("(").append(function.getCommentDistinct() != null ? function.getCommentDistinct() + " " + ExpressionDeParser.EOL : "").append("Distinct ");
            }
            visit(function.getParameters());
            useBracketsInExprList = oldUseBracketsInExprList;
            if (function.isDistinct()) {
                buffer.append(function.getCommentBeginEscaped() != null ? function.getCommentEndBracket() + " " + ExpressionDeParser.EOL : "").append(")");
            }
        }
        if (function.isEscaped()) {
            buffer.append(function.getCommentEndEscaped() != null ? function.getCommentEndEscaped() + " " + ExpressionDeParser.EOL : "").append("}");
        }
    }

    @Override
    public void visit(ExpressionList expressionList) {
        if (useBracketsInExprList) {
            buffer.append("(");
        }
        for (int i = 0; i < expressionList.getExpressions().size(); i++) {
            Expression expression = (Expression) expressionList.getExpressions().get(i);
            expression.accept(this);
            buffer.append((i < expressionList.getExpressions().size() - 1) ? (!"".equals(expressionList.getCommentsComma().get(i)) ? " "
                    + expressionList.getCommentsComma().get(i) + ExpressionDeParser.EOL : "") + ", " : "");
        }
        if (useBracketsInExprList) {
            buffer.append(expressionList.getCommentEndBracket() != null ? expressionList.getCommentEndBracket() + " " + ExpressionDeParser.EOL : "").append(")");
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
        buffer.append(dateValue.getComment() != null ? dateValue.getComment() + " " + ExpressionDeParser.EOL : "").append("{d '").append(dateValue.getValue().toString()).append("'}");
    }

    @Override
    public void visit(TimestampValue timestampValue) {
        buffer.append(timestampValue.getComment() != null ? timestampValue.getComment() + " " + ExpressionDeParser.EOL : "").append("{ts '").append(timestampValue.getValue().toString()).append("'}");
    }

    @Override
    public void visit(TimeValue timeValue) {
        buffer.append(timeValue.getComment() != null ? timeValue.getComment() + " " + ExpressionDeParser.EOL : "").append("{t '").append(timeValue.getValue().toString()).append("'}");
    }

    @Override
    public void visit(CaseExpression caseExpression) {
        buffer.append(caseExpression.getCommentCase() != null ? caseExpression.getCommentElse() + " " + ExpressionDeParser.EOL : "").append("Case ");
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
        buffer.append(caseExpression.getCommentEnd() != null ? " " + caseExpression.getCommentEnd() + ExpressionDeParser.EOL : "").append(" End");
    }

    @Override
    public void visit(WhenClause whenClause) {
        buffer.append(whenClause.getCommentWhen() != null ? " " + whenClause.getCommentWhen() + ExpressionDeParser.EOL : "").append(" When ");
        whenClause.getWhenExpression().accept(this);
        buffer.append(whenClause.getCommentThen() != null ? " " + whenClause.getCommentThen() + ExpressionDeParser.EOL : "").append(" Then ");
        whenClause.getThenExpression().accept(this);
    }

    @Override
    public void visit(AllComparisonExpression allComparisonExpression) {
        buffer.append(allComparisonExpression.getComment() != null ? " " + allComparisonExpression.getComment() + ExpressionDeParser.EOL : "").append(" all ");
        allComparisonExpression.GetSubSelect().accept((ExpressionVisitor) this);
    }

    @Override
    public void visit(AnyComparisonExpression anyComparisonExpression) {
        buffer.append(anyComparisonExpression.getComment() != null ? " " + anyComparisonExpression.getComment() + ExpressionDeParser.EOL : "").append(" any ");
        anyComparisonExpression.GetSubSelect().accept((ExpressionVisitor) this);
    }

    @Override
    public void visit(Concat concat) {
        visitBinaryExpression(concat, (concat.getComment() != null ? " " + concat.getComment() + ExpressionDeParser.EOL : "") + " || ");
    }

    @Override
    public void visit(Matches matches) {
        visitBinaryExpression(matches, (matches.getComment() != null ? " " + matches.getComment() + ExpressionDeParser.EOL : "") + " @@ ");
    }

    @Override
    public void visit(BitwiseAnd bitwiseAnd) {
        visitBinaryExpression(bitwiseAnd, (bitwiseAnd.getComment() != null ? " " + bitwiseAnd.getComment() + ExpressionDeParser.EOL : "") + " & ");
    }

    @Override
    public void visit(BitwiseOr bitwiseOr) {
        visitBinaryExpression(bitwiseOr, (bitwiseOr.getComment() != null ? " " + bitwiseOr.getComment() + ExpressionDeParser.EOL : "") + " | ");
    }

    @Override
    public void visit(BitwiseXor bitwiseXor) {
        visitBinaryExpression(bitwiseXor, (bitwiseXor.getComment() != null ? " " + bitwiseXor.getComment() + ExpressionDeParser.EOL : "") + " ^ ");
    }

    @Override
    public void visit(Connect aConnect) {
        buffer.append(" ").append(aConnect.toString());
    }
    
    @Override
    public void visit(Distinct aDistinct) {
    }
}
