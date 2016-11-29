package net.sf.jsqlparser.statement.select;

import java.util.List;

/**
 * One of the parts of a "WITH" clause of a "SELECT" statement
 */
public class WithItem {

    private String name;
    private List<SelectItem> withItemList;
    private SelectBody selectBody;
    private String commentName;
    private List<String> commentsCommaWith;
    private String commentBeginBracketWith;
    private String commentEndBracketWith;
    private String commentBeginBracketAs;
    private String commentEndBracketAs;
    private String commentAs;
    private String comment;

    /**
     * The name of this WITH item (for example, "myWITH" in "WITH myWITH AS
     * (SELECT A,B,C))"
     *
     * @return the name of this WITH
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The {@link SelectBody} of this WITH item is the part after the "AS"
     * keyword
     *
     * @return {@link SelectBody} of this WITH item
     */
    public SelectBody getSelectBody() {
        return selectBody;
    }

    public void setSelectBody(SelectBody selectBody) {
        this.selectBody = selectBody;
    }

    /**
     * The {@link SelectItem}s in this WITH (for example the A,B,C in "WITH
     * mywith (A,B,C) AS ...")
     *
     * @return a list of {@link SelectItem}s
     */
    public List<SelectItem> getWithItemList() {
        return withItemList;
    }

    public void setWithItemList(List<SelectItem> withItemList) {
        this.withItemList = withItemList;
    }

    @Override
    public String toString() {
        return (getCommentName() != null ? getCommentName() + " " : "") + name + (getCommentBeginBracketWith() != null ? " " + getCommentBeginBracketWith() : "")
                + ((withItemList != null) ? " " + PlainSelect.getStringListWithCommaComment(withItemList, commentsCommaWith, true, true, commentEndBracketWith) : "")
                + (getCommentAs() != null ? " " + getCommentAs() : "") + " AS " + (getCommentBeginBracketAs() != null ? getCommentBeginBracketAs() + " " : "") + "("
                + selectBody + (getCommentEndBracketAs() != null ? " " + getCommentEndBracketAs() : "") + ")";
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
     * @return the commentsCommaWith
     */
    public List<String> getCommentsCommaWith() {
        return commentsCommaWith;
    }

    /**
     * @param commentsCommaWith the commentsCommaWith to set
     */
    public void setCommentsCommaWith(List<String> commentsCommaWith) {
        this.commentsCommaWith = commentsCommaWith;
    }

    /**
     * @return the commentBeginBracketWith
     */
    public String getCommentBeginBracketWith() {
        return commentBeginBracketWith;
    }

    /**
     * @param commentBeginBracketWith the commentBeginBracketWith to set
     */
    public void setCommentBeginBracketWith(String commentBeginBracketWith) {
        this.commentBeginBracketWith = commentBeginBracketWith;
    }

    /**
     * @return the commentEndBracketWith
     */
    public String getCommentEndBracketWith() {
        return commentEndBracketWith;
    }

    /**
     * @param commentEndBracketWith the commentEndBracketWith to set
     */
    public void setCommentEndBracketWith(String commentEndBracketWith) {
        this.commentEndBracketWith = commentEndBracketWith;
    }

    /**
     * @return the commentBeginBracketAs
     */
    public String getCommentBeginBracketAs() {
        return commentBeginBracketAs;
    }

    /**
     * @param commentBeginBracketAs the commentBeginBracketAs to set
     */
    public void setCommentBeginBracketAs(String commentBeginBracketAs) {
        this.commentBeginBracketAs = commentBeginBracketAs;
    }

    /**
     * @return the commentEndBracketAs
     */
    public String getCommentEndBracketAs() {
        return commentEndBracketAs;
    }

    /**
     * @param commentEndBracketAs the commentEndBracketAs to set
     */
    public void setCommentEndBracketAs(String commentEndBracketAs) {
        this.commentEndBracketAs = commentEndBracketAs;
    }

    /**
     * @return the commentAS
     */
    public String getCommentAs() {
        return commentAs;
    }

    /**
     * @param commentAS the commentAS to set
     */
    public void setCommentAs(String commentAS) {
        this.commentAs = commentAS;
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
