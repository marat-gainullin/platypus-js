/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.jsqlparser.statement.select;

import java.util.List;

/**
 *
 * @author AB
 */
public class Into {
    private List commentsComma = null;
    private List tables = null;
    private String commentInto;
    
     @Override
    public String toString() {
        return (getCommentInto() != null ? getCommentInto()+" " : "") + "INTO " 
                + PlainSelect.getStringListWithCommaComment(tables,commentsComma,true,false,null);
    }

    /**
     * @return the commentsComma
     */
    public List getCommentsComma() {
        return commentsComma;
    }

    /**
     * @param commentsComma the commentsComma to set
     */
    public void setCommentsComma(List commentsComma) {
        this.commentsComma = commentsComma;
    }

    /**
     * @return the tables
     */
    public List getTables() {
        return tables;
    }

    /**
     * @param tables the tables to set
     */
    public void setTables(List tables) {
        this.tables = tables;
    }

    /**
     * @return the commentInto
     */
    public String getCommentInto() {
        return commentInto;
    }

    /**
     * @param commentInto the commentInto to set
     */
    public void setCommentInto(String commentInto) {
        this.commentInto = commentInto;
    }
}
