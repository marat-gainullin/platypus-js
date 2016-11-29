/* ================================================================
 * JSQLParser : java based sql parser 
 * ================================================================
 *
 * Project Info:  http://jsqlparser.sourceforge.net
 * Project Lead:  Leonardo Francalanci (leoonardoo@yahoo.it);
 *
 * (C) Copyright 2004, by Leonardo Francalanci
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */
package net.sf.jsqlparser.statement.replace;

import java.util.List;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.select.PlainSelect;

/**
 * The replace statement.
 */
public class Replace implements Statement {

    private Table table;
    private List<Column> columns;
    private ItemsList itemsList;
    private List<Expression> expressions;
    private boolean useValues = true;
    private boolean useInto = false;
    private String comment;
    private String commentInto;
    private String commentSet;
    private String commentValues;
    private String commentBeforeColums;
    private String commentAfterColums;
    private List<String> commentCommaColums;
    private List<String> commentEqlasColums;
    private List<String> commentCommaExpr;
    private List<String> commentCommaItems;
    private String commentBeforeItems;
    private String commentAfterItems;
    private String endComment = new String();

    @Override
    public void accept(StatementVisitor statementVisitor) {
        statementVisitor.visit(this);
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table name) {
        table = name;
    }

    /**
     * A list of {@link net.sf.jsqlparser.schema.Column}s either from a "REPLACE
     * mytab (col1, col2) [...]" or a "REPLACE mytab SET col1=exp1, col2=exp2".
     *
     * @return a list of {@link net.sf.jsqlparser.schema.Column}s
     */
    public List<Column> getColumns() {
        return columns;
    }

    /**
     * An {@link ItemsList} (either from a "REPLACE mytab VALUES (exp1,exp2)" or
     * a "REPLACE mytab SELECT * FROM mytab2") it is null in case of a "REPLACE
     * mytab SET col1=exp1, col2=exp2"
     */
    public ItemsList getItemsList() {
        return itemsList;
    }

    public void setColumns(List<Column> list) {
        columns = list;
    }

    public void setItemsList(ItemsList list) {
        itemsList = list;
    }

    /**
     * A list of {@link net.sf.jsqlparser.expression.Expression}s (from a
     * "REPLACE mytab SET col1=exp1, col2=exp2"). <br> it is null in case of a
     * "REPLACE mytab (col1, col2) [...]"
     */
    public List<Expression> getExpressions() {
        return expressions;
    }

    public void setExpressions(List<Expression> list) {
        expressions = list;
    }

    public boolean isUseValues() {
        return useValues;
    }

    public void setUseValues(boolean useValues) {
        this.useValues = useValues;
    }

    @Override
    public String toString() {
        String sql = getComment() != null ? getComment() + " " : "";

        sql += "REPLACE " + (isUseInto() ? (getCommentInto() != null ? getCommentInto() + " " : "") + "INTO " : "") + table;

        if (expressions != null && columns != null) {
            //the SET col1=exp1, col2=exp2 case
            sql += (getCommentSet() != null ? " " + getCommentSet() : "") + " SET ";
            //each element from expressions match up with a column from columns.
            for (int i = 0, s = columns.size(); i < s; i++) {
                sql += "" + columns.get(i) + (!commentEqlasColums.get(i).toString().isEmpty() ? " " + commentEqlasColums.get(i) + " " : "") + "=" + expressions.get(i);
                sql += (i < s - 1) ? (!commentCommaExpr.get(i).toString().isEmpty() ? " " + commentCommaExpr.get(i) + " " : "") + ", " : "";
            }
        } else if (columns != null) {
            //the REPLACE mytab (col1, col2) [...] case
            sql += " " + (getCommentBeforeColums() != null ? getCommentBeforeColums() + " " : "")
                    + PlainSelect.getStringListWithCommaComment(columns, commentCommaColums, true, true, commentAfterColums);
        }

        if (itemsList != null) {
            //REPLACE mytab SELECT * FROM mytab2
            //or VALUES ('as', ?, 565)

            if (useValues) {
                sql += (getCommentValues() != null ? " " + getCommentValues() : "") + " VALUES";
            }

            sql += " " + (getCommentBeforeItems() != null ? getCommentBeforeItems() + " " : "") + itemsList;
        }
        sql += !"".equals(getEndComment()) ? " " + getEndComment() : "";

        return sql;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public void setEndComment(String endComment) {
        this.endComment = endComment;
    }

    @Override
    public String getEndComment() {
        return endComment;
    }

    /**
     * @return the commentInto
     */
    public String getCommentInto() {
        return commentInto;
    }

    /**
     * @param commentInto the commentInto to set
     */
    public void setCommentInto(String commentInto) {
        this.commentInto = commentInto;
    }

    /**
     * @return the commentSet
     */
    public String getCommentSet() {
        return commentSet;
    }

    /**
     * @param commentSet the commentSet to set
     */
    public void setCommentSet(String commentSet) {
        this.commentSet = commentSet;
    }

    /**
     * @return the commentValues
     */
    public String getCommentValues() {
        return commentValues;
    }

    /**
     * @param commentValues the commentValues to set
     */
    public void setCommentValues(String commentValues) {
        this.commentValues = commentValues;
    }

    /**
     * @return the commentBeforeColums
     */
    public String getCommentBeforeColums() {
        return commentBeforeColums;
    }

    /**
     * @param commentBeforeColums the commentBeforeColums to set
     */
    public void setCommentBeforeColums(String commentBeforeColums) {
        this.commentBeforeColums = commentBeforeColums;
    }

    /**
     * @return the commentAfterColums
     */
    public String getCommentAfterColums() {
        return commentAfterColums;
    }

    /**
     * @param commentAfterColums the commentAfterColums to set
     */
    public void setCommentAfterColums(String commentAfterColums) {
        this.commentAfterColums = commentAfterColums;
    }

    /**
     * @return the commentCommaColums
     */
    public List<String> getCommentCommaColums() {
        return commentCommaColums;
    }

    /**
     * @param commentCommaColums the commentCommaColums to set
     */
    public void setCommentCommaColums(List<String> commentCommaColums) {
        this.commentCommaColums = commentCommaColums;
    }

    /**
     * @return the commentEqlasColums
     */
    public List<String> getCommentEqlasColums() {
        return commentEqlasColums;
    }

    /**
     * @param commentEqlasColums the commentEqlasColums to set
     */
    public void setCommentEqlasColums(List<String> commentEqlasColums) {
        this.commentEqlasColums = commentEqlasColums;
    }

    /**
     * @return the commentCommaExpr
     */
    public List<String> getCommentCommaExpr() {
        return commentCommaExpr;
    }

    /**
     * @param commentCommaExpr the commentCommaExpr to set
     */
    public void setCommentCommaExpr(List<String> commentCommaExpr) {
        this.commentCommaExpr = commentCommaExpr;
    }

    /**
     * @return the commentCommaItems
     */
    public List<String> getCommentCommaItems() {
        return commentCommaItems;
    }

    /**
     * @param commentCommaItems the commentCommaItems to set
     */
    public void setCommentCommaItems(List<String> commentCommaItems) {
        this.commentCommaItems = commentCommaItems;
    }

    /**
     * @return the commentBeforeItems
     */
    public String getCommentBeforeItems() {
        return commentBeforeItems;
    }

    /**
     * @param commentBeforeItems the commentBeforeItems to set
     */
    public void setCommentBeforeItems(String commentBeforeItems) {
        this.commentBeforeItems = commentBeforeItems;
    }

    /**
     * @return the commentAfterItems
     */
    public String getCommentAfterItems() {
        return commentAfterItems;
    }

    /**
     * @param commentAfterItems the commentAfterItems to set
     */
    public void setCommentAfterItems(String commentAfterItems) {
        this.commentAfterItems = commentAfterItems;
    }

    /**
     * @return the useInto
     */
    public boolean isUseInto() {
        return useInto;
    }

    /**
     * @param useInto the useInto to set
     */
    public void setUseInto(boolean useInto) {
        this.useInto = useInto;
    }
}
