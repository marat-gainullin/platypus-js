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

import net.sf.jsqlparser.expression.operators.relational.ExpressionList;

/**
 * A function as MAX,COUNT...
 */
public class Function implements Expression {

    private String name;
    private ExpressionList parameters;
    private boolean allColumns = false;
    private boolean distinct = false;
    private boolean isEscaped = false;
    private String commentBeginEscaped;
    private String commentEndEscaped;
    private String commentName;
    private String commentAll;
    private String commentDistinct;
    private String commentBeginBracket;
    private String commentEndBracket;
    private String commentAllColumns;

    @Override
    public void accept(ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }

    /**
     * The name of he function, i.e. "MAX"
     *
     * @return the name of he function
     */
    public String getName() {
        return name;
    }

    public void setName(String string) {
        name = string;
    }

    /**
     * true if the parameter to the function is "*"
     *
     * @return true if the parameter to the function is "*"
     */
    public boolean isAllColumns() {
        return allColumns;
    }

    public void setAllColumns(boolean b) {
        allColumns = b;
    }

    /**
     * true if the function is "distinct"
     *
     * @return true if the function is "distinct"
     */
    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct(boolean b) {
        distinct = b;
    }

    /**
     * The list of parameters of the function (if any, else null) If the
     * parameter is "*", allColumns is set to true
     *
     * @return the list of parameters of the function (if any, else null)
     */
    public ExpressionList getParameters() {
        return parameters;
    }

    public void setParameters(ExpressionList list) {
        parameters = list;
    }

    /**
     * Return true if it's in the form "{fn function_body() }"
     *
     * @return true if it's java-escaped
     */
    public boolean isEscaped() {
        return isEscaped;
    }

    public void setEscaped(boolean isEscaped) {
        this.isEscaped = isEscaped;
    }

    public String toString() {
        String params = "";

        if (allColumns) {
            params = (getCommentBeginBracket() != null ? " " + getCommentBeginBracket() + " " : "") + "("
                    + (getCommentAllColumns() != null ? getCommentAllColumns() + " " : "") + "*"
                    + (getCommentEndBracket() != null ? " " + getCommentEndBracket() + " " : "") + ")";
        } else if (parameters != null) {
            params = (getCommentBeginBracket() != null ? " " + getCommentBeginBracket() + " " : "") + parameters.toString();
            if (isDistinct()) {
                params = params.replaceFirst("\\(", "(" + (getCommentDistinct() != null ? getCommentDistinct() + " " : "")
                        + "DISTINCT ");
            }
        } else {
            params = (getCommentBeginBracket() != null ? " " + getCommentBeginBracket() + " " : "") + "("
                    + (getCommentEndBracket() != null ? getCommentEndBracket() + " " : "") + ")";
        }

        String ans = (getCommentName() != null ? getCommentName() + " " : "") + name + "" + params + "";

        if (isEscaped) {
            ans = (getCommentBeginEscaped() != null ? getCommentBeginEscaped() + " " : "") + "{fn "
                    + ans + (getCommentEndEscaped() != null ? " " + getCommentEndEscaped() + " " : "") + "}";
        }

        return ans;
    }

    /**
     * @return the commentBeginEscaped
     */
    public String getCommentBeginEscaped() {
        return commentBeginEscaped;
    }

    /**
     * @param commentBeginEscaped the commentBeginEscaped to set
     */
    public void setCommentBeginEscaped(String commentBeginEscaped) {
        this.commentBeginEscaped = commentBeginEscaped;
    }

    /**
     * @return the commentEndEscaped
     */
    public String getCommentEndEscaped() {
        return commentEndEscaped;
    }

    /**
     * @param commentEndEscaped the commentEndEscaped to set
     */
    public void setCommentEndEscaped(String commentEndEscaped) {
        this.commentEndEscaped = commentEndEscaped;
    }

    /**
     * @return the commentName
     */
    public String getCommentName() {
        return commentName;
    }

    /**
     * @param commentName the commentName to set
     */
    public void setCommentName(String commentName) {
        this.commentName = commentName;
    }

    /**
     * @return the commentAll
     */
    public String getCommentAll() {
        return commentAll;
    }

    /**
     * @param commentAll the commentAll to set
     */
    public void setCommentAll(String commentAll) {
        this.commentAll = commentAll;
    }

    /**
     * @return the commentDistinct
     */
    public String getCommentDistinct() {
        return commentDistinct;
    }

    /**
     * @param commentDistinct the commentDistinct to set
     */
    public void setCommentDistinct(String commentDistinct) {
        this.commentDistinct = commentDistinct;
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
     * @return the commentAllColumns
     */
    public String getCommentAllColumns() {
        return commentAllColumns;
    }

    /**
     * @param commentAllColumns the commentAllColumns to set
     */
    public void setCommentAllColumns(String commentAllColumns) {
        this.commentAllColumns = commentAllColumns;
    }
}
