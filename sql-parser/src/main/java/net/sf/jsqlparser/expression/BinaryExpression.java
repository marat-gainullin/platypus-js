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
 * A basic class for binary expressions, that is expressions having a left
 * member and a right member which are in turn expressions.
 */
public abstract class BinaryExpression implements Expression {

    private boolean leftPrior = false;
    private Expression leftExpression;
    private boolean rightPrior = false;
    private Expression rightExpression;
    private boolean not = false;
    private String commentNot;
    private String commentLeftPrior;
    private String commentRightPrior;

    public BinaryExpression() {
    }

    public Expression getLeftExpression() {
        return leftExpression;
    }

    public boolean isLeftPrior() {
        return leftPrior;
    }

    public Expression getRightExpression() {
        return rightExpression;
    }

    public boolean isRightPrior() {
        return rightPrior;
    }

    public void setLeftExpression(Expression expression) {
        leftExpression = expression;
    }

    public void setLeftPrior(boolean leftPrior) {
        this.leftPrior = leftPrior;
    }

    public void setRightExpression(Expression expression) {
        rightExpression = expression;
    }

    public void setRightPrior(boolean rightPrior) {
        this.rightPrior = rightPrior;
    }

    public void setNot(boolean not) {
        this.not = not;
    }

    public void setNot() {
        not = true;
    }

    public boolean isNot() {
        return not;
    }

    @Override
    public String toString() {
        return (not ? (getCommentNot() != null ? getCommentNot() + " " : "") + "NOT " : "")
                + (leftPrior ? (getCommentLeftPrior() != null ? getCommentLeftPrior() + " " : "") + "PRIOR " : "")
                + getLeftExpression() + " " + getStringExpression() + " "
                + (rightPrior ? (getCommentRightPrior() != null ? getCommentRightPrior() + " " : "") + "PRIOR " : "")
                + getRightExpression();
    }

    public abstract String getStringExpression();

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
     * @return the commentLeftPrior
     */
    public String getCommentLeftPrior() {
        return commentLeftPrior;
    }

    /**
     * @param commentLeftPrior the commentLeftPrior to set
     */
    public void setCommentLeftPrior(String commentLeftPrior) {
        this.commentLeftPrior = commentLeftPrior;
    }

    /**
     * @return the commentRightPrior
     */
    public String getCommentRightPrior() {
        return commentRightPrior;
    }

    /**
     * @param commentRightPrior the commentRightPrior to set
     */
    public void setCommentRightPrior(String commentRightPrior) {
        this.commentRightPrior = commentRightPrior;
    }
}
