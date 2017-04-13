package net.sf.jsqlparser.util.deparser;

import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.statement.drop.Drop;

/**
 * A class to de-parse (that is, tranform from JSqlParser hierarchy into a
 * string) a {@link net.sf.jsqlparser.statement.delete.Delete}
 */
public class DropDeParser {

    protected StringBuilder buffer;
    protected ExpressionVisitor expressionVisitor;

    public DropDeParser() {
    }

    /**
     * @param aExpressionVisitor a {@link ExpressionVisitor} to de-parse
     * expressions. It has to share the same<br>
     * StringBuilder (buffer parameter) as this object in order to work
     * @param aBuffer the buffer that will be filled with the select
     */
    public DropDeParser(ExpressionVisitor aExpressionVisitor, StringBuilder aBuffer) {
        buffer = aBuffer;
        expressionVisitor = aExpressionVisitor;
    }

    public StringBuilder getBuffer() {
        return buffer;
    }

    public void setBuffer(StringBuilder aBuffer) {
        buffer = aBuffer;
    }

    public void deParse(Drop aDrop) {
        buffer.append(aDrop.getComment() != null ? aDrop.getComment() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append("Drop")
                .append(aDrop.getTypeComment() != null ? " " + aDrop.getTypeComment() + ExpressionDeParser.LINE_SEPARATOR : "").append(" ")
                .append(aDrop.getType()).append(" ")
                .append(aDrop.getNameComment() != null ? " " + aDrop.getNameComment() + ExpressionDeParser.LINE_SEPARATOR : "")
                .append(aDrop.getName()).append(" ");
        if (aDrop.getParameters() != null && aDrop.getParameters().size() > 0) {
            for (int i = 0; i < aDrop.getParameters().size(); i++) {
                buffer.append(aDrop.getParameters().get(i)).append(" ");
                if (i < aDrop.getParameters().size() - 1) {
                    buffer.append(!"".equals(aDrop.getParametersComment().get(i)) ? " " + aDrop.getParametersComment().get(i) + ExpressionDeParser.LINE_SEPARATOR : "")
                            .append(", ");
                }
            }
        }
        buffer.append(!"".equals(aDrop.getEndComment()) ? " " + aDrop.getEndComment() : "");
    }

    public ExpressionVisitor getExpressionVisitor() {
        return expressionVisitor;
    }

    public void setExpressionVisitor(ExpressionVisitor aVisitor) {
        expressionVisitor = aVisitor;
    }
}
