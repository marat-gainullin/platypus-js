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
package net.sf.jsqlparser.expression.operators.relational;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.Relation;

public class LikeExpression extends BinaryExpression implements Relation {

    private boolean not = false;
    private String escape = null;
    private String commentLike;
    private String commentNot;
    private String commentEscape;
    private String commentLiteral;

    @Override
    public boolean isNot() {
        return not;
    }

    @Override
    public void setNot(boolean b) {
        not = b;
    }

    public void accept(ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }

    public String getStringExpression() {
        return (not ? (getCommentNot() != null ? getCommentNot()+" " : "") + "NOT " : "") 
               + (getCommentLike() != null ? getCommentLike()+" " : "") + "LIKE";
    }

    @Override
    public String toString() {
        String retval = super.toString();
        if (escape != null) {
            retval += (getCommentEscape() != null ? " "+getCommentEscape() : "") + " ESCAPE " + (getCommentLiteral() != null ? getCommentLiteral()+" " : "") + "'" + escape + "'";
        }

        return retval;
    }

    public String getEscape() {
        return escape;
    }

    public void setEscape(String escape) {
        this.escape = escape;
    }
   
    /**
     * @return the commentLike
     */
    public String getCommentLike() {
        return commentLike;
    }

    /**
     * @param commentLike the commentLike to set
     */
    public void setCommentLike(String commentLike) {
        this.commentLike = commentLike;
    }

    /**
     * @return the commentNot
     */
    public String getCommentNot() {
        return commentNot;
    }

    /**
     * @param commentNot the commentNot to set
     */
    public void setCommentNot(String commentNot) {
        this.commentNot = commentNot;
    }

    /**
     * @return the commentEscape
     */
    public String getCommentEscape() {
        return commentEscape;
    }

    /**
     * @param commentEscape the commentEscape to set
     */
    public void setCommentEscape(String commentEscape) {
        this.commentEscape = commentEscape;
    }

    /**
     * @return the commentLiteral
     */
    public String getCommentLiteral() {
        return commentLiteral;
    }

    /**
     * @param commentLiteral the commentLiteral to set
     */
    public void setCommentLiteral(String commentLiteral) {
        this.commentLiteral = commentLiteral;
    }
}
