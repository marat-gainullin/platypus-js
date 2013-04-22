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

/**
 * A UNION statement
 */
public class UnionTypes {

    private boolean union;
    private boolean except;
    private boolean intersect;
    private boolean distinct;
    private boolean all;
    private String commentUnion;
    private String commentAll;

    public UnionTypes() {
        union = except = intersect = all = distinct = false;
    }

    @Override
    public String toString() {

        String unionStruct = (getCommentUnion() != null ? getCommentUnion() + " " : "");

        if (isUnion()) {
            unionStruct += "UNION ";
        } else {
            if (isExcept()) {
                unionStruct += "EXCEPT ";
            } else {
                if (isIntersect()) {
                    unionStruct += "INTERSECT ";
                }
            }
        }

        unionStruct += (getCommentAll() != null ? getCommentAll() + " " : "");

        if (isAll()) {
            unionStruct += "ALL ";
        } else {
            if (isDistinct()) {
                unionStruct += "DISTINCT ";
            }
        }
        return unionStruct;
    }

    /**
     * @return the union
     */
    public boolean isUnion() {
        return union;
    }

    /**
     * @param union the union to set
     */
    public void setUnion(boolean union) {
        this.union = union;
    }

    /**
     * @return the except
     */
    public boolean isExcept() {
        return except;
    }

    /**
     * @param except the except to set
     */
    public void setExcept(boolean except) {
        this.except = except;
    }

    /**
     * @return the intersect
     */
    public boolean isIntersect() {
        return intersect;
    }

    /**
     * @param intersect the intersect to set
     */
    public void setIntersect(boolean intersect) {
        this.intersect = intersect;
    }

    /**
     * @return the distinct
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * @param distinct the distinct to set
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * @return the all
     */
    public boolean isAll() {
        return all;
    }

    /**
     * @param all the all to set
     */
    public void setAll(boolean all) {
        this.all = all;
    }

    /**
     * @return the commentUnion
     */
    public String getCommentUnion() {
        return commentUnion;
    }

    /**
     * @param commentUnion the commentUnion to set
     */
    public void setCommentUnion(String commentUnion) {
        this.commentUnion = commentUnion;
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
}
