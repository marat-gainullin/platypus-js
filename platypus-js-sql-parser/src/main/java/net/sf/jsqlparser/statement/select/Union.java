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

/**
 * A UNION statement
 */
public class Union implements SelectBody {

    private List<PlainSelect> plainSelects;
    private List<OrderByElement> orderByElements;
    private List<UnionTypes> typeOperations;
    private Limit limit;
    private List<String> commentsBeginBracket;
    private List<String> commentsEndBracket;
    private String commentOrder;
    private String commentOrderBy;
    private List<String> commentCommaOrderBy;

    @Override
    public void accept(SelectVisitor selectVisitor) {
        selectVisitor.visit(this);
    }

    public List<OrderByElement> getOrderByElements() {
        return orderByElements;
    }

    public void setOrderByElements(List<OrderByElement> orderByElements) {
        this.orderByElements = orderByElements;
    }

    /**
     * the list of {@link PlainSelect}s in this UNION
     *
     * @return the list of {@link PlainSelect}s
     */
    public List<PlainSelect> getPlainSelects() {
        return plainSelects;
    }

    public void setPlainSelects(List<PlainSelect> list) {
        plainSelects = list;
    }

    public Limit getLimit() {
        return limit;
    }

    public void setLimit(Limit limit) {
        this.limit = limit;
    }

    /**
     * This is not 100% right; every UNION should have their own All/Distinct
     * clause...
     */
    @Override
    public String toString() {

        String selects = "";
        String allDistinct = "";

        if (getCommentsBeginBracket() != null && getCommentsEndBracket() != null) {
            for (int i = 0; i < plainSelects.size(); i++) {
                selects += (!commentsBeginBracket.get(i).toString().isEmpty() ? commentsBeginBracket.get(i) + " " : "")
                        + "(" + plainSelects.get(i) + ((i < plainSelects.size() - 1)
                        ? (!commentsEndBracket.get(i).toString().isEmpty() ? " " + commentsEndBracket.get(i) : "")
                        + ") " + getTypeOperations().get(i) : (!commentsEndBracket.get(i).toString().isEmpty() ? " " + commentsEndBracket.get(i) : "")
                        + ")");
            }
        } else {
            for (int i = 0; i < plainSelects.size(); i++) {
                selects += plainSelects.get(i) + ((i < plainSelects.size() - 1) ? " " + getTypeOperations().get(i) : "");
            }
        }

        return selects
                + ((orderByElements != null) ? PlainSelect.commentOrderByToString(orderByElements, commentCommaOrderBy, commentOrder, commentOrderBy) : "")
                + ((limit != null) ? limit + "" : "");
    }

    /**
     * @return the typeOperations
     */
    public List<UnionTypes> getTypeOperations() {
        return typeOperations;
    }

    /**
     * @param aValue the typeOperations to set
     */
    public void setTypeOperations(List<UnionTypes> aValue) {
        typeOperations = aValue;
    }

    /**
     * @return the commentsBeginBracket
     */
    public List<String> getCommentsBeginBracket() {
        return commentsBeginBracket;
    }

    /**
     * @param commentsBeginBracket the commentsBeginBracket to set
     */
    public void setCommentsBeginBracket(List<String> commentsBeginBracket) {
        this.commentsBeginBracket = commentsBeginBracket;
    }

    /**
     * @return the commentsEndBracket
     */
    public List<String> getCommentsEndBracket() {
        return commentsEndBracket;
    }

    /**
     * @param commentsEndBracket the commentsEndBracket to set
     */
    public void setCommentsEndBracket(List<String> commentsEndBracket) {
        this.commentsEndBracket = commentsEndBracket;
    }

    /**
     * @return the commentOrder
     */
    public String getCommentOrder() {
        return commentOrder;
    }

    /**
     * @param commentOrder the commentOrder to set
     */
    public void setCommentOrder(String commentOrder) {
        this.commentOrder = commentOrder;
    }

    /**
     * @return the commentOrderBy
     */
    public String getCommentOrderBy() {
        return commentOrderBy;
    }

    /**
     * @param commentOrderBy the commentOrderBy to set
     */
    public void setCommentOrderBy(String commentOrderBy) {
        this.commentOrderBy = commentOrderBy;
    }

    /**
     * @return the commentCommaOrderBy
     */
    public List getCommentCommaOrderBy() {
        return commentCommaOrderBy;
    }

    /**
     * @param commentCommaOrderBy the commentCommaOrderBy to set
     */
    public void setCommentCommaOrderBy(List<String> commentCommaOrderBy) {
        this.commentCommaOrderBy = commentCommaOrderBy;
    }
}
