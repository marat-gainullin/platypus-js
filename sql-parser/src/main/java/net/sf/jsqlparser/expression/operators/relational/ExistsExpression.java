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

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.Relation;

public class ExistsExpression implements Relation {

    private Expression rightExpression;
    private boolean not = false;
    private boolean prior = false;
    private String commentExists;
    private String commentPrior;
    private String commentNot;

    public Expression getRightExpression() {
        return rightExpression;
    }

    public void setRightExpression(Expression expression) {
        rightExpression = expression;
    }

    public boolean isNot() {
        return not;
    }

    public void setNot(boolean b) {
        not = b;
    }

    public boolean isPrior() {
        return prior;
    }

    public void setPrior(boolean prior) {
        this.prior = prior;
    }

    public void accept(ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }

    public String getStringExpression() {
        return (prior ? (getCommentPrior() != null ? getCommentPrior()+" " : "") + "PRIOR " : "") 
               + (not ? (getCommentNot() != null ? getCommentNot()+" " : "") + "NOT " : "") 
               + (getCommentExists() != null ? getCommentExists()+" " : "") + "EXISTS";
    }

    @Override
    public String toString() {
        return getStringExpression() + " " + rightExpression.toString();
    }

    /**
     * @return the commentExists
     */
    public String getCommentExists() {
        return commentExists;
    }

    /**
     * @param commentExists the commentExists to set
     */
    public void setCommentExists(String commentExists) {
        this.commentExists = commentExists;
    }

    /**
     * @return the commentPrior
     */
    public String getCommentPrior() {
        return commentPrior;
    }

    /**
     * @param commentPrior the commentPrior to set
     */
    public void setCommentPrior(String commentPrior) {
        this.commentPrior = commentPrior;
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
}
