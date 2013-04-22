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

import java.util.Iterator;
import java.util.List;
import net.sf.jsqlparser.expression.Expression;

/**
 * The core of a "SELECT" statement (no UNION, no ORDER BY)
 */
public class PlainSelect implements SelectBody {

    private Distinct distinct = null;
    private List<SelectItem> selectItems;
    private Into into;
    private FromItem fromItem;
    private List<Join> joins;
    private Expression where;
    private Connect connect;
    private List<Expression> groupByColumnReferences;
    private List<OrderByElement> orderByElements;
    private Expression having;
    private Limit limit;
    private Top top;
    private String comment;
    private String commentFrom;
    private String commentWhere;
    private String commentGroup;
    private String commentGroupBy;
    private String commentHaving;
    private String commentOrder;
    private String commentOrderBy;
    private List<String> commentCommaOrderBy;
    private List<String> commentCommaGroupBy;
    private List<String> commentCommaItems;

    /**
     * The {@link FromItem} in this query
     *
     * @return the {@link FromItem}
     */
    public FromItem getFromItem() {
        return fromItem;
    }

    public Into getInto() {
        return into;
    }

    /**
     * The {@link SelectItem}s in this query (for example the A,B,C in "SELECT
     * A,B,C")
     *
     * @return a list of {@link SelectItem}s
     */
    public List<SelectItem> getSelectItems() {
        return selectItems;
    }

    public Expression getWhere() {
        return where;
    }

    public void setFromItem(FromItem item) {
        fromItem = item;
    }

    public void setInto(Into into) {
        this.into = into;
    }

    public void setSelectItems(List<SelectItem> list) {
        selectItems = list;
    }

    public void setWhere(Expression aWhere) {
        where = aWhere;
    }

    public Connect getConnect() {
        return connect;
    }

    public void setConnect(Connect aValue) {
        connect = aValue;
    }

    /**
     * The list of {@link Join}s
     *
     * @return the list of {@link Join}s
     */
    public List<Join> getJoins() {
        return joins;
    }

    public void setJoins(List<Join> list) {
        joins = list;
    }

    @Override
    public void accept(SelectVisitor selectVisitor) {
        selectVisitor.visit(this);
    }

    public List<OrderByElement> getOrderByElements() {
        return orderByElements;
    }

    public void setOrderByElements(List<OrderByElement> aValue) {
        orderByElements = aValue;
    }

    public Limit getLimit() {
        return limit;
    }

    public void setLimit(Limit aValue) {
        limit = aValue;
    }

    public Top getTop() {
        return top;
    }

    public void setTop(Top aValue) {
        top = aValue;
    }

    public Distinct getDistinct() {
        return distinct;
    }

    public void setDistinct(Distinct aDistinct) {
        distinct = aDistinct;
    }

    public Expression getHaving() {
        return having;
    }

    public void setHaving(Expression aExpression) {
        having = aExpression;
    }

    /**
     * A list of {@link Expression}s of the GROUP BY clause. It is null in case
     * there is no GROUP BY clause
     *
     * @return a list of {@link Expression}s
     */
    public List<Expression> getGroupByColumnReferences() {
        return groupByColumnReferences;
    }

    public void setGroupByColumnReferences(List<Expression> list) {
        groupByColumnReferences = list;
    }

    @Override
    public String toString() {
        String sql = "";

        sql = (getComment() != null ? getComment() + " " : "") + "SELECT ";
        sql += ((distinct != null) ? "" + distinct + " " : "");
        sql += ((top != null) ? "" + top + " " : "");
        sql += getStringListWithCommaComment(selectItems, commentCommaItems, true, false, null);
        sql += getInto() != null ? " " + getInto() : "";
        sql += (getCommentFrom() != null ? " " + getCommentFrom() : "") + " FROM " + fromItem;
        if (joins != null) {
            Iterator<Join> it = joins.iterator();
            while (it.hasNext()) {
                Join join = it.next();
                if (join.isSimple()) {
                    sql += ", " + join;
                } else {
                    sql += " " + join;
                }
            }
        }
        //sql += getFormatedList(joins, "", false, false);
        sql += ((where != null) ? (getCommentWhere() != null ? " " + getCommentWhere() : "") + " WHERE " + where : "");
        sql += ((connect != null) ? (sql.startsWith(" ") ? connect.toString() : " " + connect.toString()) : "");

        sql += commentGroupByToString(groupByColumnReferences, getCommentCommaGroupBy(), commentGroup, commentGroupBy);
        sql += ((having != null) ? (getCommentHaving() != null ? " " + getCommentHaving() : "") + " HAVING " + having : "");
        sql += commentOrderByToString(orderByElements, commentCommaOrderBy, commentOrder, commentOrderBy);
        sql += ((limit != null) ? limit + "" : "");

        return sql;
    }

    public static String commentOrderByToString(List<?> orderByElements, List<String> commentsOrder, String order, String by) {
        if (orderByElements != null && orderByElements.size() > 0) {
            return (order != null ? " " + order + " " : " ") + "ORDER" + (by != null ? " " + by : "") + " BY " + getStringListWithCommaComment(orderByElements, commentsOrder, true, false, null);
        } else {
            return "";
        }
    }

    public static String commentGroupByToString(List<?> groupByElements, List<String> commentsOrder, String group, String by) {
        if (groupByElements != null && groupByElements.size() > 0) {
            return (group != null ? " " + group + " " : " ") + "GROUP" + (by != null ? " " + by : "") + " BY " + getStringListWithCommaComment(groupByElements, commentsOrder, true, false, null);
        } else {
            return "";
        }
    }

    public static String getFormatedList(List list, String expression) {
        return getFormatedList(list, expression, true, false);
    }

    public static String getFormatedList(List list, String expression, boolean useComma, boolean useBrackets) {
        String sql = getStringList(list, useComma, useBrackets);

        if (sql.length() > 0) {
            if (expression.length() > 0) {
                sql = " " + expression + " " + sql;
            } else {
                sql = " " + sql;
            }
        }

        return sql;
    }

    /**
     * List the toString out put of the objects in the List comma separated. If
     * the List is null or empty an empty string is returned.
     *
     * The same as getStringList(list, true, false)
     *
     * @see #getStringList(List, boolean, boolean)
     * @param list list of objects with toString methods
     * @return comma separated list of the elements in the list
     */
    public static String getStringList(List list) {
        return getStringList(list, true, false);
    }

    /**
     * List the toString out put of the objects in the List that can be comma
     * separated. If the List is null or empty an empty string is returned.
     *
     * @param list list of objects with toString methods
     * @param useComma true if the list has to be comma separated
     * @param useBrackets true if the list has to be enclosed in brackets
     * @return comma separated list of the elements in the list
     */
    public static String getStringList(List list, boolean useComma, boolean useBrackets) {
        String ans = "";
        String comma = ",";
        if (!useComma) {
            comma = "";
        }
        if (list != null) {
            if (useBrackets) {
                ans += "(";
            }

            for (int i = 0; i < list.size(); i++) {
                ans += "" + list.get(i) + ((i < list.size() - 1) ? comma + " " : "");
            }

            if (useBrackets) {
                ans += ")";
            }
        }

        return ans;
    }

    /**
     * List the toString out put of the objects in the List that can be comma
     * separated. If the List is null or empty an empty string is returned.
     *
     * @param list list of objects with toString methods
     * @param comments comments to elements
     * @param useComma true if the list has to be comma separated
     * @param useBrackets true if the list has to be enclosed in brackets
     * @return comma separated list of the elements in the list
     */
    public static String getStringListWithComments(List list, List comments, boolean useComma, boolean useBrackets) {
        String ans = "";
        String comma = ",";
        if (!useComma) {
            comma = "";
        }
        if (list != null) {
            if (useBrackets) {
                ans += "(";
            }

            for (int i = 0; i < list.size(); i++) {
                ans += "" + (!"".equals(comments.get(i)) ? comments.get(i) + " " : "") + list.get(i) + ((i < list.size() - 1) ? comma + " " : "");
            }

            if (useBrackets) {
                ans += ")";
            }
        }

        return ans;
    }

    /**
     * List the toString out put of the objects in the List that can be comma
     * separated. If the List is null or empty an empty string is returned.
     *
     * @param list list of objects with toString methods
     * @param comments comments to comma
     * @param useComma true if the list has to be comma separated
     * @param useBrackets true if the list has to be enclosed in brackets
     * @param endBracketComment comment before end bracket
     * @return comma separated list of the elements in the list
     */
    public static String getStringListWithCommaComment(List<?> list, List<String> comments, boolean useComma, boolean useBrackets, String endBracketComment) {
        String ans = "";
        String endComment = endBracketComment == null ? "" : endBracketComment;
        String comma = ",";
        if (!useComma) {
            comma = "";
        }
        if (list != null) {
            if (useBrackets) {
                ans += "(";
            }

            for (int i = 0; i < list.size(); i++) {
                ans += "" + list.get(i) + ((i < list.size() - 1) ? (!"".equals(comments.get(i)) ? " " + comments.get(i) : "") + comma + " " : "");
            }

            if (useBrackets) {
                ans += (endComment.isEmpty() ? "" : " " + endComment + " ") + ")";
            }
        }

        return ans;
    }

    /**
     * List the toString out put of the objects in the List that can be comma
     * separated. If the List is null or empty an empty string is returned.
     *
     * @param list list of objects with toString methods
     * @param comments
     * @param commentsComma commentsComma to comma
     * @param useComma true if the list has to be comma separated
     * @param useBrackets true if the list has to be enclosed in brackets
     * @param endBracketComment comment before end bracket
     * @return comma separated list of the elements in the list
     */
    public static String getStringListWithItemAndCommaComment(List<?> list, List<String> comments, List<String> commentsComma, boolean useComma, boolean useBrackets, String endBracketComment) {
        String ans = "";
        String endComment = endBracketComment == null ? "" : endBracketComment;
        String comma = ",";
        if (!useComma) {
            comma = "";
        }
        if (list != null) {
            if (useBrackets) {
                ans += "(";
            }

            for (int i = 0; i < list.size(); i++) {
                ans += "" + (!"".equals(comments.get(i)) ? comments.get(i) + " " : "") + list.get(i)
                        + ((i < list.size() - 1) ? (!"".equals(commentsComma.get(i)) ? " " + commentsComma.get(i) : "") + comma + " " : "");
            }

            if (useBrackets) {
                ans += (endComment.isEmpty() ? "" : " " + endComment + " ") + ")";
            }
        }

        return ans;
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
     * @return the commentFrom
     */
    public String getCommentFrom() {
        return commentFrom;
    }

    /**
     * @param commentFrom the commentFrom to set
     */
    public void setCommentFrom(String commentFrom) {
        this.commentFrom = commentFrom;
    }

    /**
     * @return the commentWhere
     */
    public String getCommentWhere() {
        return commentWhere;
    }

    /**
     * @param commentWhere the commentWhere to set
     */
    public void setCommentWhere(String commentWhere) {
        this.commentWhere = commentWhere;
    }

    /**
     * @return the commentGroup
     */
    public String getCommentGroup() {
        return commentGroup;
    }

    /**
     * @param commentGroup the commentGroup to set
     */
    public void setCommentGroup(String commentGroup) {
        this.commentGroup = commentGroup;
    }

    /**
     * @return the commentGroupBy
     */
    public String getCommentGroupBy() {
        return commentGroupBy;
    }

    /**
     * @param commentGroupBy the commentGroupBy to set
     */
    public void setCommentGroupBy(String commentGroupBy) {
        this.commentGroupBy = commentGroupBy;
    }

    /**
     * @return the commentHaving
     */
    public String getCommentHaving() {
        return commentHaving;
    }

    /**
     * @param commentHaving the commentHaving to set
     */
    public void setCommentHaving(String commentHaving) {
        this.commentHaving = commentHaving;
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
     * @param aValue the commentCommaOrderBy to set
     */
    public void setCommentCommaOrderBy(List<String> aValue) {
        commentCommaOrderBy = aValue;
    }

    /**
     * @return the commentCommaGroupBy
     */
    public List<String> getCommentCommaGroupBy() {
        return commentCommaGroupBy;
    }

    /**
     * @param aValue the commentCommaGroupBy to set
     */
    public void setCommentCommaGroupBy(List<String> aValue) {
        commentCommaGroupBy = aValue;
    }

    /**
     * @return the commentCommaItems
     */
    public List<String> getCommentCommaItems() {
        return commentCommaItems;
    }

    /**
     * @param aValue the commentCommaItems to set
     */
    public void setCommentCommaItems(List<String> aValue) {
        commentCommaItems = aValue;
    }
}
