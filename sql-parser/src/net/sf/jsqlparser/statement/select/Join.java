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
package net.sf.jsqlparser.statement.select;

import java.util.List;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;

/**
 * A join clause
 */
public class Join {

    private boolean outer = false;
    private boolean right = false;
    private boolean left = false;
    private boolean natural = false;
    private boolean full = false;
    private boolean inner = false;
    private boolean simple = false;
    private FromItem rightItem;
    private Expression onExpression;
    private List<Column> usingColumns;
    private String comment;
    private String commentRight;
    private String commentOuter;
    private String commentLeft;
    private String commentNatural;
    private String commentFull;
    private String commentInner;
    private String commentJoin;
    private String commentOn;
    private String commentUsing;
    private String commentBeginBracket;
    private String commentEndBracket;
    private List<String> commentComma;

    /**
     * Whether is a tab1,tab2 join
     *
     * @return true if is a "tab1,tab2" join
     */
    public boolean isSimple() {
        return simple;
    }

    public void setSimple(boolean b) {
        simple = b;
    }

    /**
     * Whether is a "INNER" join
     *
     * @return true if is a "INNER" join
     */
    public boolean isInner() {
        return inner;
    }

    public void setInner(boolean b) {
        inner = b;
    }

    /**
     * Whether is a "OUTER" join
     *
     * @return true if is a "OUTER" join
     */
    public boolean isOuter() {
        return outer;
    }

    public void setOuter(boolean b) {
        outer = b;
    }

    /**
     * Whether is a "LEFT" join
     *
     * @return true if is a "LEFT" join
     */
    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean b) {
        left = b;
    }

    /**
     * Whether is a "RIGHT" join
     *
     * @return true if is a "RIGHT" join
     */
    public boolean isRight() {
        return right;
    }

    public void setRight(boolean b) {
        right = b;
    }

    /**
     * Whether is a "NATURAL" join
     *
     * @return true if is a "NATURAL" join
     */
    public boolean isNatural() {
        return natural;
    }

    public void setNatural(boolean b) {
        natural = b;
    }

    /**
     * Whether is a "FULL" join
     *
     * @return true if is a "FULL" join
     */
    public boolean isFull() {
        return full;
    }

    public void setFull(boolean b) {
        full = b;
    }

    /**
     * Returns the right item of the join
     */
    public FromItem getRightItem() {
        return rightItem;
    }

    public void setRightItem(FromItem item) {
        rightItem = item;
    }

    /**
     * Returns the "ON" expression (if any)
     */
    public Expression getOnExpression() {
        return onExpression;
    }

    public void setOnExpression(Expression expression) {
        onExpression = expression;
    }

    /**
     * Returns the "USING" list of {@link net.sf.jsqlparser.schema.Column}s (if
     * any)
     */
    public List<Column> getUsingColumns() {
        return usingColumns;
    }

    public void setUsingColumns(List<Column> list) {
        usingColumns = list;
    }

    @Override
    public String toString() {
        if (isSimple()) {
            return "" + (getComment() != null ? getComment() + " " : "") + rightItem;
        } else {
            String type = "";

            if (isRight()) {
                type += (getCommentRight() != null ? getCommentRight() + " " : "") + "RIGHT ";
            } else if (isNatural()) {
                type += (getCommentNatural() != null ? getCommentNatural() + " " : "") + "NATURAL ";
            } else if (isFull()) {
                type += (getCommentFull() != null ? getCommentFull() + " " : "") + "FULL ";
            } else if (isLeft()) {
                type += (getCommentLeft() != null ? getCommentLeft() + " " : "") + "LEFT ";
            }

            if (isOuter()) {
                type += (getCommentOuter() != null ? getCommentOuter() + " " : "") + "OUTER ";
            } else if (isInner()) {
                type += (getCommentInner() != null ? getCommentInner() + " " : "") + "INNER ";
            }

            return type + (getCommentJoin() != null ? getCommentJoin() + " " : "") + "JOIN "
                    + rightItem
                    + ((onExpression != null) ? (getCommentOn() != null ? " " + getCommentOn() : "") + " ON " + onExpression + "" : "")
                    + (usingColumns != null ? ((getCommentUsing() != null ? " " + getCommentUsing() : "") + " USING "
                    + (getCommentBeginBracket() != null ? getCommentBeginBracket() + " " : "")
                    + PlainSelect.getStringListWithCommaComment(usingColumns, commentComma, true, true, getCommentEndBracket())) : "");
        }

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

    /**
     * @return the commentRight
     */
    public String getCommentRight() {
        return commentRight;
    }

    /**
     * @param commentRight the commentRight to set
     */
    public void setCommentRight(String commentRight) {
        this.commentRight = commentRight;
    }

    /**
     * @return the commentOuter
     */
    public String getCommentOuter() {
        return commentOuter;
    }

    /**
     * @param commentOuter the commentOuter to set
     */
    public void setCommentOuter(String commentOuter) {
        this.commentOuter = commentOuter;
    }

    /**
     * @return the commentLeft
     */
    public String getCommentLeft() {
        return commentLeft;
    }

    /**
     * @param commentLeft the commentLeft to set
     */
    public void setCommentLeft(String commentLeft) {
        this.commentLeft = commentLeft;
    }

    /**
     * @return the commentNatural
     */
    public String getCommentNatural() {
        return commentNatural;
    }

    /**
     * @param commentNatural the commentNatural to set
     */
    public void setCommentNatural(String commentNatural) {
        this.commentNatural = commentNatural;
    }

    /**
     * @return the commentFull
     */
    public String getCommentFull() {
        return commentFull;
    }

    /**
     * @param commentFull the commentFull to set
     */
    public void setCommentFull(String commentFull) {
        this.commentFull = commentFull;
    }

    /**
     * @return the commentInner
     */
    public String getCommentInner() {
        return commentInner;
    }

    /**
     * @param commentInner the commentInner to set
     */
    public void setCommentInner(String commentInner) {
        this.commentInner = commentInner;
    }

    /**
     * @return the commentJoin
     */
    public String getCommentJoin() {
        return commentJoin;
    }

    /**
     * @param commentJoin the commentJoin to set
     */
    public void setCommentJoin(String commentJoin) {
        this.commentJoin = commentJoin;
    }

    /**
     * @return the commentOn
     */
    public String getCommentOn() {
        return commentOn;
    }

    /**
     * @param commentOn the commentOn to set
     */
    public void setCommentOn(String commentOn) {
        this.commentOn = commentOn;
    }

    /**
     * @return the commentUsing
     */
    public String getCommentUsing() {
        return commentUsing;
    }

    /**
     * @param commentUsing the commentUsing to set
     */
    public void setCommentUsing(String commentUsing) {
        this.commentUsing = commentUsing;
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

    /**
     * @return the commentComma
     */
    public List<String> getCommentComma() {
        return commentComma;
    }

    /**
     * @param aValue the commentComma to set
     */
    public void setCommentComma(List<String> aValue) {
        commentComma = aValue;
    }
}
