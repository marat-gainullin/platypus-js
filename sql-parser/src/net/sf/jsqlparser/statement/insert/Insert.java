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
package net.sf.jsqlparser.statement.insert;

import java.util.List;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.select.PlainSelect;

/**
 * The insert statement. Every column name in
 * <code>columnNames</code> matches an item in
 * <code>itemsList</code>
 */
public class Insert implements Statement {

    private Table table;
    private List<Column> columns;
    private List<String> columsComment;
    private ItemsList itemsList;
    private List<String> itemsListComments;
    private boolean useValues = true;
    private String comment;
    private String endComment = new String();
    private String commentInto;
    private String commentValues;
    private String commentItemsList;
    private String commentAfterItemsList;
    private String commentBeforeColums;
    private String commentAfterColums;

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
     * Get the columns (found in "INSERT INTO (col1,col2..) [...]" )
     *
     * @return a list of {@link net.sf.jsqlparser.schema.Column}
     */
    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> list) {
        columns = list;
    }

    /**
     * Get the values (as VALUES (...) or SELECT)
     *
     * @return the values of the insert
     */
    public ItemsList getItemsList() {
        return itemsList;
    }

    public void setItemsList(ItemsList list) {
        itemsList = list;
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

        sql += "INSERT" + (getCommentInto() != null ? " " + getCommentInto() : "") + " INTO ";
        sql += table + (getCommentBeforeColums() != null ? " " + getCommentBeforeColums() + " " : " ");
        sql += ((columns != null) ? PlainSelect.getStringListWithCommaComment(columns, columsComment, true, true, commentAfterColums) + " " : "");

        if (useValues) {
            sql += (getCommentValues() != null ? getCommentValues() + " " : "") + "VALUES " + (getCommentItemsList() != null ? getCommentItemsList() + " " : "") + itemsList + "";
        } else {
            sql += (getCommentItemsList() != null ? getCommentItemsList() + " " : "") + itemsList + "";
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
     * @return the commentItemsList
     */
    public String getCommentItemsList() {
        return commentItemsList;
    }

    /**
     * @param commentItemsList the commentItemsList to set
     */
    public void setCommentItemsList(String commentItemsList) {
        this.commentItemsList = commentItemsList;
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
     * @return the columsComment
     */
    public List<String> getColumsComment() {
        return columsComment;
    }

    /**
     * @param columsComment the columsComment to set
     */
    public void setColumsComment(List<String> columsComment) {
        this.columsComment = columsComment;
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
     * @return the commentAfterItemsList
     */
    public String getCommentAfterItemsList() {
        return commentAfterItemsList;
    }

    /**
     * @param commentAfterItemsList the commentAfterItemsList to set
     */
    public void setCommentAfterItemsList(String commentAfterItemsList) {
        this.commentAfterItemsList = commentAfterItemsList;
    }

    /**
     * @return the itemsListComments
     */
    public List<String> getItemsListComments() {
        return itemsListComments;
    }

    /**
     * @param itemsListComments the itemsListComments to set
     */
    public void setItemsListComments(List<String> itemsListComments) {
        this.itemsListComments = itemsListComments;
    }
}
