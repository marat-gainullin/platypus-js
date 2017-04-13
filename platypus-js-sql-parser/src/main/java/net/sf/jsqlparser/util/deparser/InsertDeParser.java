package net.sf.jsqlparser.util.deparser;

import java.util.Iterator;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SubSelect;

/**
 * A class to de-parse (that is, tranform from JSqlParser hierarchy into a
 * string) an {@link net.sf.jsqlparser.statement.insert.Insert}
 */
public class InsertDeParser implements ItemsListVisitor {

    protected StringBuilder buffer;
    protected ExpressionVisitor expressionVisitor;
    protected SelectVisitor selectVisitor;

    public InsertDeParser() {
    }

    /**
     * @param expressionVisitor a {@link ExpressionVisitor} to de-parse
     * {@link net.sf.jsqlparser.expression.Expression}s. It has to share the
     * same<br>
     * StringBuilder (buffer parameter) as this object in order to work
     * @param selectVisitor a {@link SelectVisitor} to de-parse
     * {@link net.sf.jsqlparser.statement.select.Select}s. It has to share the
     * same<br>
     * StringBuilder (buffer parameter) as this object in order to work
     * @param buffer the buffer that will be filled with the insert
     */
    public InsertDeParser(ExpressionVisitor expressionVisitor, SelectVisitor selectVisitor, StringBuilder buffer) {
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

    public void deParse(Insert insert) {
        buffer
                .append(insert.getComment() != null ? insert.getComment() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append("Insert")
                .append(insert.getCommentInto() != null ? " " + insert.getCommentInto() + ExpressionDeParser.LINE_SEPARATOR : "").append(" into ")
                .append(insert.getTable().getComment() != null ? insert.getTable().getComment() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append(insert.getTable().getWholeTableName());
        if (insert.getColumns() != null) {
            buffer.append(insert.getCommentBeforeColums() != null ? insert.getCommentBeforeColums() + " " : "")
                    .append(ExpressionDeParser.LINE_SEPARATOR).append("(");
            int columnsCounter = 0;
            for (int i = 0; i < insert.getColumns().size(); i++) {
                Column column = (Column) insert.getColumns().get(i);
                buffer.append(column.getComment() != null ? column.getComment() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append(column.getWholeColumnName());
                buffer.append((i < insert.getColumns().size() - 1) ? (!"".equals(insert.getColumsComment().get(i)) ? " " + insert.getColumsComment().get(i) : "") : "");
                if (i < insert.getColumns().size() - 1) {
                    if (columnsCounter++ == 2) {
                        columnsCounter = 0;
                        buffer.append(ExpressionDeParser.LINE_SEPARATOR).append(", ");
                    } else {
                        buffer.append(", ");
                    }
                }
            }
            buffer.append(insert.getCommentAfterColums() != null ? insert.getCommentAfterColums() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append(")");
        }
        if (insert.isUseValues()) {
            buffer.append(insert.getCommentValues() != null ? " " + insert.getCommentValues() : "")
                    .append(ExpressionDeParser.LINE_SEPARATOR).append(" Values ")
                    .append(insert.getCommentItemsList() != null ? insert.getCommentItemsList() + " " + ExpressionDeParser.LINE_SEPARATOR : "");
        }
        insert.getItemsList().accept(this);
        if (insert.isUseValues()) {
            buffer.append(insert.getCommentAfterItemsList() != null ? insert.getCommentAfterItemsList() + " " + ExpressionDeParser.LINE_SEPARATOR : "").append(")");
        }
        buffer.append(!"".equals(insert.getEndComment()) ? " " + insert.getEndComment() : "");
    }

    public void visit(ExpressionList expressionList) {
        buffer.append("(");
        int valuesCounter = 0;
        for (int i = 0; i < expressionList.getExpressions().size(); i++) {
            Expression expression = (Expression) expressionList.getExpressions().get(i);
            expression.accept(expressionVisitor);
            buffer.append((i < expressionList.getExpressions().size() - 1) ? (!"".equals(expressionList.getCommentsComma().get(i)) ? " "
                    + expressionList.getCommentsComma().get(i) + ExpressionDeParser.LINE_SEPARATOR : "") : "");
            if (i < expressionList.getExpressions().size() - 1) {
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
