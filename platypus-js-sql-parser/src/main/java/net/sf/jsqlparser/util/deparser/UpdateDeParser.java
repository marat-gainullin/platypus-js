package net.sf.jsqlparser.util.deparser;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.update.Update;

/**
 * A class to de-parse (that is, tranform from JSqlParser hierarchy into a
 * string) an {@link net.sf.jsqlparser.statement.update.Update}
 */
public class UpdateDeParser {

    protected StringBuilder buffer;
    protected ExpressionVisitor expressionVisitor;

    public UpdateDeParser() {
    }

    /**
     * @param aExpressionVisitor a {@link ExpressionVisitor} to de-parse
     * expressions. It has to share the same<br>
     * StringBuilder (buffer parameter) as this object in order to work
     * @param aBuffer the buffer that will be filled with the select
     */
    public UpdateDeParser(ExpressionVisitor aExpressionVisitor, StringBuilder aBuffer) {
        buffer = aBuffer;
        expressionVisitor = aExpressionVisitor;
    }

    public StringBuilder getBuffer() {
        return buffer;
    }

    public void setBuffer(StringBuilder aBuffer) {
        buffer = aBuffer;
    }

    public void deParse(Update aUpdate) {
        buffer.append(aUpdate.getComment() != null ? aUpdate.getComment() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append("Update ").append(ExpressionDeParser.LINE_SEPARATOR)
                .append(aUpdate.getTable().getComment() != null ? aUpdate.getTable().getComment() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append(aUpdate.getTable().getWholeTableName()).append(ExpressionDeParser.LINE_SEPARATOR)
                .append(aUpdate.getCommentSet() != null ? " " + aUpdate.getCommentSet() + ExpressionDeParser.LINE_SEPARATOR : "").append(" set ");
        int columnsCounter = 0;
        for (int i = 0, s = aUpdate.getColumns().size(); i < s; i++) {
            Column column = (Column) aUpdate.getColumns().get(i);
            buffer.append(column.getComment() != null ? column.getComment() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append(column.getWholeColumnName())
                    .append(!aUpdate.getCommentsEqaulas().get(i).toString().isEmpty() ? " " + aUpdate.getCommentsEqaulas().get(i) + ExpressionDeParser.LINE_SEPARATOR : "").append(" = ");
            Expression expression = (Expression) aUpdate.getExpressions().get(i);
            expression.accept(expressionVisitor);
            if (i < aUpdate.getColumns().size() - 1) {
                buffer.append(!aUpdate.getCommentsComma().get(i).toString().isEmpty() ? " " + aUpdate.getCommentsComma().get(i) + " " : "");
                if (columnsCounter++ == 2) {
                    columnsCounter = 0;
                    buffer.append(ExpressionDeParser.LINE_SEPARATOR).append(", ");
                } else {
                    buffer.append(", ");
                }
            }
        }

        if (aUpdate.getWhere() != null) {
            buffer.append(aUpdate.getCommentWhere() != null ? " " + aUpdate.getCommentWhere() : "")
                    .append(ExpressionDeParser.LINE_SEPARATOR).append(" Where ");
            aUpdate.getWhere().accept(expressionVisitor);
        }
        buffer.append(!"".equals(aUpdate.getEndComment()) ? " " + aUpdate.getEndComment() : "");
    }

    public ExpressionVisitor getExpressionVisitor() {
        return expressionVisitor;
    }

    public void setExpressionVisitor(ExpressionVisitor visitor) {
        expressionVisitor = visitor;
    }
}
