package net.sf.jsqlparser.util.deparser;

import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.statement.delete.Delete;

/**
 * A class to de-parse (that is, tranform from JSqlParser hierarchy into a
 * string) a {@link net.sf.jsqlparser.statement.delete.Delete}
 */
public class DeleteDeParser {

    protected StringBuilder buffer;
    protected ExpressionVisitor expressionVisitor;

    public DeleteDeParser() {
    }

    /**
     * @param aExpressionVisitor a {@link ExpressionVisitor} to de-parse
     * expressions. It has to share the same<br>
     * StringBuilder (buffer parameter) as this object in order to work
     * @param aBuffer the buffer that will be filled with the select
     */
    public DeleteDeParser(ExpressionVisitor aExpressionVisitor, StringBuilder aBuffer) {
        this.buffer = aBuffer;
        this.expressionVisitor = aExpressionVisitor;
    }

    public StringBuilder getBuffer() {
        return buffer;
    }

    public void setBuffer(StringBuilder aBuffer) {
        buffer = aBuffer;
    }

    public void deParse(Delete aDelete) {
        buffer
                .append(aDelete.getComment() != null ? aDelete.getComment() + " " + ExpressionDeParser.LINE_SEPARATOR : "")
                .append("Delete").append(aDelete.getFromComment() != null ? " " + aDelete.getFromComment() + ExpressionDeParser.LINE_SEPARATOR : "").append(" From ")
                .append(aDelete.getTable().getComment() != null ? aDelete.getTable().getComment() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append(aDelete.getTable().getWholeTableName());
        if (aDelete.getWhere() != null) {
            buffer.append(aDelete.getWhereComment() != null ? " " + aDelete.getWhereComment() : "").append(ExpressionDeParser.LINE_SEPARATOR).append(" Where ");
            aDelete.getWhere().accept(expressionVisitor);
        }
        buffer.append(!"".equals(aDelete.getEndComment()) ? " " + aDelete.getEndComment() : "");
    }

    public ExpressionVisitor getExpressionVisitor() {
        return expressionVisitor;
    }

    public void setExpressionVisitor(ExpressionVisitor aVisitor) {
        expressionVisitor = aVisitor;
    }
}
