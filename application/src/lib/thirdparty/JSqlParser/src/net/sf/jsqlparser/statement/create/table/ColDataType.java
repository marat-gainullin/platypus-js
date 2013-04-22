package net.sf.jsqlparser.statement.create.table;

import java.util.List;
import net.sf.jsqlparser.statement.select.PlainSelect;

public class ColDataType {

    private String dataType;
    private List<String> argumentsStringList;
    private String commentType;
    private String commentBeginBracket;
    private String commentEndBracket;
    private List<String> commentsComma;
    private List<String> commentsValue;

    public List<String> getArgumentsStringList() {
        return argumentsStringList;
    }

    public void setArgumentsStringList(List<String> list) {
        argumentsStringList = list;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String string) {
        dataType = string;
    }

    @Override
    public String toString() {
        return (getCommentType() != null ? getCommentType() + " " : "") + dataType
                + (argumentsStringList != null ? " " + (getCommentBeginBracket() != null ? getCommentBeginBracket() + " " : "")
                + PlainSelect.getStringListWithItemAndCommaComment(argumentsStringList, commentsValue, commentsComma, true, true, commentEndBracket) : "");
    }

    /**
     * @return the commentType
     */
    public String getCommentType() {
        return commentType;
    }

    /**
     * @param commentType the commentType to set
     */
    public void setCommentType(String commentType) {
        this.commentType = commentType;
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
    public List<String> getCommentsComma() {
        return commentsComma;
    }

    /**
     * @param commentsComma the commentsComma to set
     */
    public void setCommentsComma(List<String> commentsComma) {
        this.commentsComma = commentsComma;
    }

    /**
     * @return the commentsValue
     */
    public List<String> getCommentsValue() {
        return commentsValue;
    }

    /**
     * @param commentsValue the commentsValue to set
     */
    public void setCommentsValue(List<String> commentsValue) {
        this.commentsValue = commentsValue;
    }
}
