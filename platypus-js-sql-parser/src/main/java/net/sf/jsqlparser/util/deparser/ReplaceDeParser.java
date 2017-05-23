package net.sf.jsqlparser.util.deparser;

import java.util.Iterator;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SubSelect;

/**
 * A class to de-parse (that is, tranform from JSqlParser hierarchy into a
 * string) a {@link net.sf.jsqlparser.statement.replace.Replace}
 */
public class ReplaceDeParser implements ItemsListVisitor {

    protected StringBuilder buffer;
    protected ExpressionVisitor expressionVisitor;
    protected SelectVisitor selectVisitor;

    public ReplaceDeParser() {
    }

    /**
     * @param expressionVisitor a {@link ExpressionVisitor} to de-parse
     * expressions. It has to share the same<br>
     * StringBuilder (buffer parameter) as this object in order to work
     * @param selectVisitor a {@link SelectVisitor} to de-parse
     * {@link net.sf.jsqlparser.statement.select.Select}s. It has to share the
     * same<br>
     * StringBuilder (buffer parameter) as this object in order to work
     * @param buffer the buffer that will be filled with the select
     */
    public ReplaceDeParser(ExpressionVisitor expressionVisitor, SelectVisitor selectVisitor, StringBuilder buffer) {
        this.buffer = buffer;
        this.expressionVisitor = expressionVisitor;
        this.selectVisitor = selectVisitor;
    }

    public StringBuilder getBuffer() {
        return buffer;
    }

    public void setBuffer(StringBuilder buffer) {
        this.buffer = buffer;
    }

    public void deParse(Replace aReplace) {
        buffer.append(aReplace.getComment() != null ? aReplace.getComment() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append("Replace ");
        if (aReplace.isUseInto()) {
            buffer.append(aReplace.getCommentInto() != null ? aReplace.getCommentInto() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append("Into ");
        }
        buffer.append(aReplace.getTable().getComment() != null ? aReplace.getTable().getComment() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append(aReplace.getTable().getWholeTableName());
        if (aReplace.getExpressions() != null && aReplace.getColumns() != null) {
            buffer.append(aReplace.getCommentSet() != null ? " " + aReplace.getCommentSet() + ExpressionDeParser.LINE_SEPARATOR : "").append(" SET ");
            //each element from expressions match up with a column from columns.
            int columnsCounter = 0;
            for (int i = 0, s = aReplace.getColumns().size(); i < s; i++) {
                Column column = (Column) aReplace.getColumns().get(i);
                buffer.append(column.getComment() != null ? column.getComment() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append(column.getWholeColumnName())
                        .append(!aReplace.getCommentEqlasColums().get(i).toString().isEmpty() ? " " + aReplace.getCommentEqlasColums().get(i) + ExpressionDeParser.LINE_SEPARATOR : "").append(" = ");
                Expression expression = (Expression) aReplace.getExpressions().get(i);
                expression.accept(expressionVisitor);
                if (i < aReplace.getColumns().size() - 1) {
                    buffer.append(!aReplace.getCommentCommaExpr().get(i).toString().isEmpty() ? " " + aReplace.getCommentCommaExpr().get(i) + " " : "");
                    if (columnsCounter++ == 2) {
                        columnsCounter = 0;
                        buffer.append(ExpressionDeParser.LINE_SEPARATOR).append(", ");
                    } else {
                        buffer.append(", ");
                    }
                }
            }
        } else {
            if (aReplace.getColumns() != null) {
                buffer.append(aReplace.getCommentBeforeColums() != null ? " " + aReplace.getCommentBeforeColums() + ExpressionDeParser.LINE_SEPARATOR : "").append(" (");
                for (int i = 0; i < aReplace.getColumns().size(); i++) {
                    Column column = (Column) aReplace.getColumns().get(i);
                    buffer.append(column.getComment() != null ? column.getComment() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append(column.getWholeColumnName());
                    if (i < aReplace.getColumns().size() - 1) {
                        buffer.append(!"".equals(aReplace.getCommentCommaColums().get(i)) ? " " + aReplace.getCommentCommaColums().get(i) + ExpressionDeParser.LINE_SEPARATOR : "")
                                .append(", ");
                    }
                }
                buffer.append(aReplace.getCommentAfterColums() != null ? aReplace.getCommentAfterColums() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append(") ");
            }
        }
        if (aReplace.isUseValues()) {
            buffer.append(aReplace.getCommentValues() != null ? " " + aReplace.getCommentValues() : "")
                    .append(ExpressionDeParser.LINE_SEPARATOR).append(" Values ")
                    .append(aReplace.getCommentBeforeItems() != null ? aReplace.getCommentBeforeItems() + " " + ExpressionDeParser.LINE_SEPARATOR : "");
        }
        aReplace.getItemsList().accept(this);
        if (aReplace.isUseValues()) {
            buffer.append(aReplace.getCommentAfterItems() != null ? aReplace.getCommentAfterItems() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append(")");
        }
        buffer.append(!"".equals(aReplace.getEndComment()) ? " " + aReplace.getEndComment() : "");
    }

    public void visit(ExpressionList expressionList) {
        buffer.append(ExpressionDeParser.LINE_SEPARATOR).append("(");
        int valuesCounter = 0;
        for (Iterator iter = expressionList.getExpressions().iterator(); iter.hasNext();) {
            Expression expression = (Expression) iter.next();
            expression.accept(expressionVisitor);
            if (iter.hasNext()) {
                if (valuesCounter++ == 2) {
                    valuesCounter = 0;
                    buffer.append(ExpressionDeParser.LINE_SEPARATOR).append(", ");
                } else {
                    buffer.append(", ");
                }
            }
        }
    }

    public void visit(SubSelect subSelect) {
        subSelect.getSelectBody().accept(selectVisitor);
    }

    public ExpressionVisitor getExpressionVisitor() {
        return expressionVisitor;
    }

    public SelectVisitor getSelectVisitor() {
        return selectVisitor;
    }

    public void setExpressionVisitor(ExpressionVisitor visitor) {
        expressionVisitor = visitor;
    }

    public void setSelectVisitor(SelectVisitor visitor) {
        selectVisitor = visitor;
    }
}
