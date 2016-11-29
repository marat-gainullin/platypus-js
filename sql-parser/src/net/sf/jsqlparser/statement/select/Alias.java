/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.jsqlparser.statement.select;

/**
 *
 * @author AB
 */
public class Alias {
    private String name;
    private boolean as = false;
    private String commentAs = null;
    private String commentName = null;
    
    /**
     * The name of this Alias item (for example, "selct * from table1 as t1")"
     * @return the name of this alias
     */
    public String getName() {
        return name != null ? name : "";
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        String retval = "";
        /**
         * Этот хак используется для того что бы при обнулении имени алиаса,
         * а не самого алиаса правильно собирался текст запроса
         */
        if (name == null || "".equals(name)) {
            retval = getName();
        } else {
            retval = (getCommentAs() != null) ? getCommentAs()+" " : "";
            retval += (isAs()) ? "AS " : "";
            retval += (getCommentName() != null) ? getCommentName() + " " : "";
            retval += getName();
        }
        return retval;
    }

    /**
     * Comment before AS constraction of Alias
     * @return the commentAs
     */
    public String getCommentAs() {
        return commentAs;
    }

    /**
     * Comment before AS constraction of Alias
     * @param commentAs the commentAs to set
     */
    public void setCommentAs(String commentAs) {
        this.commentAs = commentAs;
    }

    /**
     * Comment before name Alias
     * @return the commentName
     */
    public String getCommentName() {
        return commentName;
    }

    /**
     * Comment before name Alias
     * @param commentName the commentName to set
     */
    public void setCommentName(String commentName) {
        this.commentName = commentName;
    }

    /**
     * Flag as = true when Alias conctraction contain AS keyword
     * @return the as
     */
    public boolean isAs() {
        return as;
    }

    /**
     * @param as the as to set
     */
    public void setAs(boolean as) {
        this.as = as;
    }

}
