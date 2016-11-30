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
package net.sf.jsqlparser.statement.delete;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;

public class Delete implements Statement {

    private Table table;
    private Expression where;
    private String comment;
    private String endComment = new String();
    private String fromComment;
    private String whereComment;

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

    public String toString() {
        String sql;
        sql = getComment() != null ? getComment() + " " : "";
        sql += "DELETE " + (getFromComment() != null ? getFromComment() + " " : "");
        sql += "FROM " + table + ((where != null) ? ((getWhereComment() != null ? " " + getWhereComment() : "") + " WHERE " + where) : "");
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
     * @return the fromComment
     */
    public String getFromComment() {
        return fromComment;
    }

    /**
     * @param fromComment the fromComment to set
     */
    public void setFromComment(String fromComment) {
        this.fromComment = fromComment;
    }

    /**
     * @return the whereComment
     */
    public String getWhereComment() {
        return whereComment;
    }

    /**
     * @param whereComment the whereComment to set
     */
    public void setWhereComment(String whereComment) {
        this.whereComment = whereComment;
    }
}
