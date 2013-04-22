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

public class InExpression implements Relation {

    private Expression leftExpression;
    private ItemsList itemsList;
    private boolean not = false;
    private boolean prior = false;
    private String commentIn;
    private String commentNot;
    private String commentPrior;
    private String commentBeginBracket;

    public InExpression() {
    }

    public InExpression(Expression leftExpression, ItemsList itemsList) {
        this.leftExpression = leftExpression;
        this.itemsList = itemsList;
    }

    public ItemsList getItemsList() {
        return itemsList;
    }

    public Expression getLeftExpression() {
        return leftExpression;
    }

    public void setItemsList(ItemsList list) {
        itemsList = list;
    }

    public void setLeftExpression(Expression expression) {
        leftExpression = expression;
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

    @Override
    public String toString() {
        return (prior ? (getCommentPrior() != null ? getCommentPrior()+" " : "") + "PRIOR " : "") 
               + leftExpression + " " + ((not) ? (getCommentNot() != null ? getCommentNot()+" " : "") + "NOT " : "") 
               + (getCommentIn() != null ? getCommentIn()+" " : "") + "IN " 
               + (getCommentBeginBracket() != null ? getCommentBeginBracket()+" " : "") + itemsList + "";
    }

    /**
     * @return the commentIn
     */
    public String getCommentIn() {
        return commentIn;
    }

    /**
     * @param commentIn the commentIn to set
     */
    public void setCommentIn(String commentIn) {
        this.commentIn = commentIn;
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
     * @return the commentBeginBracket
     */
    public String getCommentBeginBracket() {
        return commentBeginBracket;
    }

    /**
     * @param commentBeginBracket the commentBeginBracket to set
     */
    public void setCommentBeginBracket(String commentBeginBracket) {
        this.commentBeginBracket = commentBeginBracket;
    }

}
