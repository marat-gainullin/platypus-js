/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.jsqlparser.statement.select;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;

/**
 *
 * @author mg
 */
public class Connect implements Expression {

    private Expression startWith;
    private Expression connectBy;
    private boolean noCycle = false;
    private String comment;
    private String commentWith;
    private String commentConnect;
    private String commentBy;
    private String commentNoCycle;

    public Expression getStartWith() {
        return startWith;
    }

    public Expression getConnectBy() {
        return connectBy;
    }

    public void setStartWith(Expression aValue) {
        startWith = aValue;
    }

    public void setConnectBy(Expression aValue) {
        connectBy = aValue;
    }

    public boolean isNoCycle() {
        return noCycle;
    }

    public void setNoCycle(boolean noCycle) {
        this.noCycle = noCycle;
    }

    @Override
    public String toString() {
        return (startWith != null ? (getComment() != null ? getComment() + " " : "") + "START " + (getCommentWith() != null ? getCommentWith() + " " : "")
                + "WITH " + startWith.toString() : "") + (getCommentConnect() != null ? " " + getCommentConnect() : "") + " CONNECT "
                + (getCommentBy() != null ? getCommentBy() + " " : "") + "BY " + (noCycle ? (getCommentNoCycle() != null ? getCommentNoCycle() + " " : "")
                + "NOCYCLE " : "") + connectBy.toString();
    }

    public void accept(ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
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
     * @return the commentWith
     */
    public String getCommentWith() {
        return commentWith;
    }

    /**
     * @param commentWith the commentWith to set
     */
    public void setCommentWith(String commentWith) {
        this.commentWith = commentWith;
    }

    /**
     * @return the commentConnect
     */
    public String getCommentConnect() {
        return commentConnect;
    }

    /**
     * @param commentConnect the commentConnect to set
     */
    public void setCommentConnect(String commentConnect) {
        this.commentConnect = commentConnect;
    }

    /**
     * @return the commentBy
     */
    public String getCommentBy() {
        return commentBy;
    }

    /**
     * @param commentBy the commentBy to set
     */
    public void setCommentBy(String commentBy) {
        this.commentBy = commentBy;
    }

    /**
     * @return the commentNoCycle
     */
    public String getCommentNoCycle() {
        return commentNoCycle;
    }

    /**
     * @param commentNoCycle the commentNoCycle to set
     */
    public void setCommentNoCycle(String commentNoCycle) {
        this.commentNoCycle = commentNoCycle;
    }
}
