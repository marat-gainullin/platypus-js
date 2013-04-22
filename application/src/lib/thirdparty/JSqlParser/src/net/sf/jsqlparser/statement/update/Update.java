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
package net.sf.jsqlparser.statement.update;

import java.util.List;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;

/**
 * The update statement.
 */
public class Update implements Statement {

    private Table table;
    private Expression where;
    private List<Column> columns;
    private List<Expression> expressions;
    private String comment;
    private String endComment = new String();
    private String commentSet;
    private String commentWhere;
    private List<String> commentsComma;
    private List<String> commentsEqaulas;

    @Override
    public void accept(StatementVisitor statementVisitor) {
        statementVisitor.visit(this);
    }

    public Table getTable() {
        return table;
    }

    public Expression getWhere() {
        return where;
    }

    public void setTable(Table name) {
        table = name;
    }

    public void setWhere(Expression expression) {
        where = expression;
    }

    /**
     * The {@link net.sf.jsqlparser.schema.Column}s in this update (as col1 and
     * col2 in UPDATE col1='a', col2='b')
     *
     * @return a list of {@link net.sf.jsqlparser.schema.Column}s
     */
    public List<Column> getColumns() {
        return columns;
    }

    /**
     * The {@link Expression}s in this update (as 'a' and 'b' in UPDATE
     * col1='a', col2='b')
     *
     * @return a list of {@link Expression}s
     */
    public List<Expression> getExpressions() {
        return expressions;
    }

    public void setColumns(List<Column> list) {
        columns = list;
    }

    public void setExpressions(List<Expression> list) {
        expressions = list;
    }

    @Override
    public String toString() {
        String sql = getComment() != null ? getComment() + " " : "";

        sql += "UPDATE ";
        sql += getTable() + (getCommentSet() != null ? " " + getCommentSet() : "") + " SET ";
        for (int i = 0; i < getColumns().size(); i++) {
            sql += getColumns().get(i) + (!commentsEqaulas.get(i).toString().isEmpty() ? " " + commentsEqaulas.get(i) : "")
                    + " = " + getExpressions().get(i);
            if (i < getColumns().size() - 1) {
                sql += (!commentsComma.get(i).toString().isEmpty() ? " " + commentsComma.get(i) : "") + ", ";
            }
        }

        sql += (getCommentWhere() != null ? " " + getCommentWhere() : "") + " WHERE " + getWhere();

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
     * @return the commentWhere
     */
    public String getCommentWhere() {
        return commentWhere;
    }

    /**
     * @param commentWhere the commentWhere to set
     */
    public void setCommentWhere(String commentWhere) {
        this.commentWhere = commentWhere;
    }

    /**
     * @return the commentsComma
     */
    public List<String> getCommentsComma() {
        return commentsComma;
    }

    /**
     * @param commentsComma the commentsComma to set
     */
    public void setCommentsComma(List<String> commentsComma) {
        this.commentsComma = commentsComma;
    }

    /**
     * @return the commentsEqaulas
     */
    public List<String> getCommentsEqaulas() {
        return commentsEqaulas;
    }

    /**
     * @param commentsEqaulas the commentsEqaulas to set
     */
    public void setCommentsEqaulas(List<String> commentsEqaulas) {
        this.commentsEqaulas = commentsEqaulas;
    }
}
