package net.sf.jsqlparser.statement.select;

import java.util.List;

/**
 * A DISTINCT [ON (expression, ...)] clause
 */
public class Distinct {

    private List onSelectItems;
    private String comment;
    private String commentOn;
    private String commentBeginBracket;
    private String commentEndBracket;
    private List commentsComma;
    
    /**
     * A list of {@link SelectItem}s expressions, as in "select DISTINCT ON (a,b,c) a,b FROM..."
     * @return a list of {@link SelectItem}s expressions
     */
    public List getOnSelectItems() {
        return onSelectItems;
    }

    public void setOnSelectItems(List list) {
        onSelectItems = list;
    }

    @Override
    public String toString() {
        String sql = (getComment() != null ? getComment()+" " : "")+"DISTINCT";

        if (onSelectItems != null && onSelectItems.size() > 0) {
            sql += (getCommentOn() != null ? " "+getCommentOn() : "") + " ON " + (getCommentBeginBracket() != null ? getCommentBeginBracket()+" " : "") 
                   + PlainSelect.getStringListWithCommaComment(onSelectItems, commentsComma, true, true, commentEndBracket);
        }

        return sql;
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
}
