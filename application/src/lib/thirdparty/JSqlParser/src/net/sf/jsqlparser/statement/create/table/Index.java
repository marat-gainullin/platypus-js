package net.sf.jsqlparser.statement.create.table;

import java.util.List;
import net.sf.jsqlparser.statement.select.PlainSelect;

/**
 * An index (unique, primary etc.) in a CREATE TABLE statement
 */
public class Index {

    private String type;
    private List<String> columnsNames;
    private String name;
    private String commentType;
    private String commentKey;
    private String commentName;
    private String commentBeginBracket;
    private String commentEndBracket;
    private List commentsComma;
    private List commentsValue;
    private boolean key = false;

    /**
     * A list of strings of all the columns regarding this index
     */
    public List<String> getColumnsNames() {
        return columnsNames;
    }

    public void setColumnsNames(List<String> list) {
        columnsNames = list;
    }

    public String getName() {
        return name;
    }

    /**
     * The type of this index: "PRIMARY KEY", "UNIQUE", "INDEX"
     */
    public String getType() {
        return type;
    }

    public void setName(String string) {
        name = string;
    }

    public void setType(String string) {
        type = string;
    }

    @Override
    public String toString() {
        return (getCommentType() != null ? getCommentType() + " " : "")
                + (!isKey() ? type : "PRIMARY " + (getCommentKey() != null ? getCommentKey() + " " : "") + "KEY") + " "
                + (name != null ? (getCommentName() != null ? getCommentName() + " " : "") + name + " " : "")
                + (getCommentBeginBracket() != null ? getCommentBeginBracket() + " " : "")
                + PlainSelect.getStringListWithItemAndCommaComment(columnsNames, commentsValue, commentsComma, true, true, commentEndBracket);
    }

    /**
     * @return the commentsValue
     */
    public List getCommentsValue() {
        return commentsValue;
    }

    /**
     * @param commentsValue the commentsValue to set
     */
    public void setCommentsValue(List commentsValue) {
        this.commentsValue = commentsValue;
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

    /**
     * @return the key
     */
    public boolean isKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(boolean key) {
        this.key = key;
    }

    /**
     * @return the commentKey
     */
    public String getCommentKey() {
        return commentKey;
    }

    /**
     * @param commentKey the commentKey to set
     */
    public void setCommentKey(String commentKey) {
        this.commentKey = commentKey;
    }
}
