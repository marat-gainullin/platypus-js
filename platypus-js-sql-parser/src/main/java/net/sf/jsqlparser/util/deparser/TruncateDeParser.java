package net.sf.jsqlparser.util.deparser;

import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.statement.truncate.Truncate;

/**
 * A class to de-parse (that is, tranform from JSqlParser hierarchy into a
 * string) a {@link net.sf.jsqlparser.statement.delete.Delete}
 */
public class TruncateDeParser {

    protected StringBuilder buffer;
    protected ExpressionVisitor expressionVisitor;

    public TruncateDeParser() {
    }

    /**
     * @param expressionVisitor a {@link ExpressionVisitor} to de-parse
     * expressions. It has to share the same<br>
     * StringBuilder (buffer parameter) as this object in order to work
     * @param buffer the buffer that will be filled with the select
     */
    public TruncateDeParser(ExpressionVisitor expressionVisitor, StringBuilder buffer) {
        this.buffer = buffer;
        this.expressionVisitor = expressionVisitor;
    }

    public StringBuilder getBuffer() {
        return buffer;
    }

    public void setBuffer(StringBuilder buffer) {
        this.buffer = buffer;
    }

    public void deParse(Truncate truncate) {
        buffer.append(truncate.getComment() != null ? truncate.getComment() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append("Truncate")
                .append(truncate.getCommentTable() != null ? " " + truncate.getCommentTable() + ExpressionDeParser.LINE_SEPARATOR : "").append(" Table ")
                .append(truncate.getTable().getComment() != null ? truncate.getTable().getComment() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append(truncate.getTable().getWholeTableName());
        buffer.append(!"".equals(truncate.getEndComment()) ? " " + truncate.getEndComment() : "");
    }

    public ExpressionVisitor getExpressionVisitor() {
        return expressionVisitor;
    }

    public void setExpressionVisitor(ExpressionVisitor visitor) {
        expressionVisitor = visitor;
    }
}
