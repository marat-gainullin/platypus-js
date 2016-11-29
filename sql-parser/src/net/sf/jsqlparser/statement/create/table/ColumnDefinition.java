package net.sf.jsqlparser.statement.create.table;

import java.util.List;
import net.sf.jsqlparser.statement.select.PlainSelect;

/**
 * A column definition in a CREATE TABLE statement.<br> Example: mycol
 * VARCHAR(30) NOT NULL
 */
public class ColumnDefinition {

    private String columnName;
    private ColDataType colDataType;
    private List<String> columnSpecStrings;
    private String commentName;
    private List<String> commentsSpec;

    /**
     * A list of strings of every word after the datatype of the column.<br>
     * Example ("NOT", "NULL")
     */
    public List<String> getColumnSpecStrings() {
        return columnSpecStrings;
    }

    public void setColumnSpecStrings(List<String> list) {
        columnSpecStrings = list;
    }

    /**
     * The {@link ColDataType} of this column definition
     */
    public ColDataType getColDataType() {
        return colDataType;
    }

    public void setColDataType(ColDataType type) {
        colDataType = type;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String string) {
        columnName = string;
    }

    @Override
    public String toString() {
        return (getCommentName() != null ? getCommentName() + " " : "") + columnName + " " + colDataType + " " + PlainSelect.getStringListWithComments(columnSpecStrings, commentsSpec, false, false);//PlainSelect.getStringList(columnSpecStrings, false, false); 
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
     * @return the commentsSpec
     */
    public List<String> getCommentsSpec() {
        return commentsSpec;
    }

    /**
     * @param commentsSpec the commentsSpec to set
     */
    public void setCommentsSpec(List<String> commentsSpec) {
        this.commentsSpec = commentsSpec;
    }
}
