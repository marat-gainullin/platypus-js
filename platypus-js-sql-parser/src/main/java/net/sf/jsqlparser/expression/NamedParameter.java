/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.jsqlparser.expression;

/**
 *
 * @author mg
 */
public class NamedParameter implements Expression {

    private String name;
    private String comment;

    public NamedParameter() {
        super();
    }

    public NamedParameter(String aName) {
        this();
        name = aName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void accept(ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }

    @Override
    public String toString() {
        return (getComment() != null ? getComment() + " " : "") + ":" + name;
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
}
