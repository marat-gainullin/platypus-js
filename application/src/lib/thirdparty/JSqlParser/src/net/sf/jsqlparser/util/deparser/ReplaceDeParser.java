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
 * A class to de-parse (that is, tranform from JSqlParser hierarchy into a string)
 * a {@link net.sf.jsqlparser.statement.replace.Replace}
 */
public class ReplaceDeParser implements ItemsListVisitor {

    protected StringBuilder buffer;
    protected ExpressionVisitor expressionVisitor;
    protected SelectVisitor selectVisitor;

    public ReplaceDeParser() {
    }

    /**
     * @param expressionVisitor a {@link ExpressionVisitor} to de-parse expressions. It has to share the same<br>
     * StringBuilder (buffer parameter) as this object in order to work
     * @param selectVisitor a {@link SelectVisitor} to de-parse {@link net.sf.jsqlparser.statement.select.Select}s.
     * It has to share the same<br>
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

    public void deParse(Replace replace) {
        buffer.append(replace.getComment() != null ? replace.getComment()+" "+ExpressionDeParser.EOL : "").append("Replace ");
        if (replace.isUseInto()){
            buffer.append(replace.getComment() != null ? replace.getCommentInto()+" "+ExpressionDeParser.EOL : "").append("Into ");
        }
        buffer.append(replace.getComment() != null ? replace.getComment()+" "+ExpressionDeParser.EOL : "").append(replace.getTable().getWholeTableName());
        if (replace.getExpressions() != null && replace.getColumns() != null) {
            buffer.append(replace.getCommentSet() != null ? " "+replace.getCommentSet()+ExpressionDeParser.EOL : "").append(" SET ");
            //each element from expressions match up with a column from columns.
            int columnsCounter = 0;
            for (int i = 0, s = replace.getColumns().size(); i < s; i++) {
                Column column = (Column) replace.getColumns().get(i);
                buffer.append(column.getComment() != null ? column.getComment()+" "+ExpressionDeParser.EOL : "").append(column.getWholeColumnName())
                      .append(!replace.getCommentEqlasColums().get(i).toString().isEmpty()?" "+replace.getCommentEqlasColums().get(i)+ExpressionDeParser.EOL : "" ).append(" = ");
                Expression expression = (Expression) replace.getExpressions().get(i);
                expression.accept(expressionVisitor);
                if (i < replace.getColumns().size() - 1) {
                    buffer.append(!replace.getCommentCommaExpr().get(i).toString().isEmpty()?" "+replace.getCommentCommaExpr().get(i)+" ":"");
                    if (columnsCounter++ == 2) {
                        columnsCounter = 0;
                        buffer.append(ExpressionDeParser.EOL).append(", ");
                    } else {
                        buffer.append(", ");
                    }
                }
            }
        } else {
            if (replace.getColumns() != null) {
                buffer.append(replace.getCommentBeforeColums() != null ? " "+replace.getCommentBeforeColums()+ExpressionDeParser.EOL : "").append(" (");
                for (int i = 0; i < replace.getColumns().size(); i++) {
                    Column column = (Column) replace.getColumns().get(i);
                    buffer.append(column.getComment() != null ? column.getComment()+" "+ExpressionDeParser.EOL : "").append(column.getWholeColumnName());
                    if (i < replace.getColumns().size() - 1) {
                        buffer.append(!"".equals(replace.getCommentCommaColums().get(i)) ? " " + replace.getCommentCommaColums().get(i)+ExpressionDeParser.EOL : "")
                              .append(", ");
                    }
                }
                buffer.append(replace.getCommentAfterColums() != null ? replace.getCommentAfterColums()+" "+ExpressionDeParser.EOL : "").append(") ");
            }
        }
        if (replace.isUseValues()) {
            buffer.append(replace.getCommentValues() != null ? " "+replace.getCommentValues() : "")
                  .append(ExpressionDeParser.EOL).append(" Values ")
                  .append(replace.getCommentBeforeItems()!= null ? replace.getCommentBeforeItems()+" "+ExpressionDeParser.EOL : ""); 
        }
        replace.getItemsList().accept(this);
        if (replace.isUseValues()) {
            buffer.append(replace.getCommentAfterItems() != null ? replace.getCommentAfterItems()+" "+ExpressionDeParser.EOL : "").append(")");
        }
        buffer.append(!"".equals(replace.getEndComment()) ? " "+replace.getEndComment() : "");
    }

    public void visit(ExpressionList expressionList) {
        buffer.append(ExpressionDeParser.EOL).append("(");
        int valuesCounter = 0;
        for (Iterator iter = expressionList.getExpressions().iterator(); iter.hasNext();) {
            Expression expression = (Expression) iter.next();
            expression.accept(expressionVisitor);
            if (iter.hasNext()) {
                if (valuesCounter++ == 2) {
                    valuesCounter = 0;
                    buffer.append(ExpressionDeParser.EOL).append(", ");
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
