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
package net.sf.jsqlparser.expression;

/**
 * It represents an expression like "(" expression ")"
 */
public class Parenthesis implements Expression {

    private Expression expression;
    private boolean not = false;
    private boolean prior = false;
    private String commentNot;
    private String commentPrior;
    private String commentBeginBracket;
    private String commentEndBracket;

    public Parenthesis() {
    }

    public Parenthesis(Expression expression) {
        setExpression(expression);
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public void accept(ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }

    public void setNot() {
        not = true;
    }

    public boolean isNot() {
        return not;
    }

    public boolean isPrior() {
        return prior;
    }

    public void setPrior(boolean prior) {
        this.prior = prior;
    }

    @Override
    public String toString() {
        return (prior ? (getCommentPrior() != null ? getCommentPrior() + " " : "") + "PRIOR " : "")
                + (not ? (getCommentNot() != null ? getCommentNot() + " " : "") + "NOT " : "")
                + (getCommentBeginBracket() != null ? getCommentBeginBracket() + " " : "") + "(" + expression
                + (getCommentEndBracket() != null ? " " + getCommentEndBracket() + " " : "") + ")";
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

    /**
     * @return the commentEndBracket
     */
    public String getCommentEndBracket() {
        return commentEndBracket;
    }

    /**
     * @param commentEndBracket the commentEndBracket to set
     */
    public void setCommentEndBracket(String commentEndBracket) {
        this.commentEndBracket = commentEndBracket;
    }
}
