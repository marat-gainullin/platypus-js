package net.sf.jsqlparser.statement.create.table;

import java.util.List;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.select.PlainSelect;

/**
 * A "CREATE TABLE" statement
 */
public class CreateTable implements Statement {

    private Table table;
    private List<String> tableOptionsStrings;
    private List<String> createOptions;
    private List<ColumnDefinition> columnDefinitions;
    private List<Index> indexes;
    private String comment;
    private String commentTable;
    private String commentBeginBracket;
    private String commentEndBracket;
    private List<String> commentCommaIndexes;
    private List<String> commentTableOptions;
    private List<String> commentCreateOptions;
    private String endComment = new String();

    @Override
    public void accept(StatementVisitor statementVisitor) {
        statementVisitor.visit(this);
    }

    /**
     * The name of the table to be created
     */
    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    /**
     * A list of {@link ColumnDefinition}s of this table.
     */
    public List<ColumnDefinition> getColumnDefinitions() {
        return columnDefinitions;
    }

    public void setColumnDefinitions(List<ColumnDefinition> list) {
        columnDefinitions = list;
    }

    /**
     * A list of options (as simple strings) of this table definition, as
     * ("TYPE", "=", "MYISAM")
     */
    public List<String> getTableOptionsStrings() {
        return tableOptionsStrings;
    }

    public void setTableOptionsStrings(List<String> list) {
        tableOptionsStrings = list;
    }

    /**
     * A list of {@link Index}es (for example "PRIMARY KEY") of this table.<br>
     * Indexes created with column definitions (as in mycol INT PRIMARY KEY) are
     * not inserted into this list.
     */
    public List<Index> getIndexes() {
        return indexes;
    }

    public void setIndexes(List<Index> list) {
        indexes = list;
    }

    @Override
    public String toString() {
        String sql = "";

        sql = (getComment() != null ? getComment() + " " : "") + "CREATE ";
        sql += PlainSelect.getStringListWithComments(createOptions, commentCreateOptions, false, false)
                + (createOptions != null ? " " : "")
                + (getCommentTable() != null ? getCommentTable() + " " : "") + "TABLE " + table
                + (getCommentBeginBracket() != null ? " " + getCommentBeginBracket() : "") + " (";

        sql += PlainSelect.getStringListWithCommaComment(columnDefinitions, commentCommaIndexes, true, false, null);
        //for (int i = 0; i<columnDefinitions.size()-1; i++) {commentCommaIndexes.remove(0);}
        if (indexes != null && !indexes.isEmpty()) {
            if (commentCommaIndexes.size() > columnDefinitions.size()) {
                sql += (!"".equals(commentCommaIndexes.get(columnDefinitions.size() - 1)) ? " " + commentCommaIndexes.get(columnDefinitions.size() - 1) : "");
            }
            sql += ", ";
            //commentCommaIndexes.remove(0);
            //sql += PlainSelect.getStringListWithCommaComment(indexes,commentCommaIndexes,true,false,null);
            String comma = ",";
            if (indexes != null) {

                for (int i = 0; i < indexes.size(); i++) {
                    sql += "" + indexes.get(i) + ((i < indexes.size() - 1)
                            ? (!"".equals(commentCommaIndexes.get(i + columnDefinitions.size())) ? " " + commentCommaIndexes.get(i + columnDefinitions.size()) : "") + comma + " " : "");
                }


            }
        }
        sql += (getCommentEndBracket() != null ? " " + getCommentEndBracket() + " " : "") + ") ";
        sql += PlainSelect.getStringListWithComments(tableOptionsStrings, commentTableOptions, false, false);
        sql += !"".equals(getEndComment()) ? " " + getEndComment() : "";
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

    @Override
    public void setEndComment(String endComment) {
        this.endComment = endComment;
    }

    @Override
    public String getEndComment() {
        return endComment;
    }

    /**
     * @return the commentTable
     */
    public String getCommentTable() {
        return commentTable;
    }

    /**
     * @param commentTable the commentTable to set
     */
    public void setCommentTable(String commentTable) {
        this.commentTable = commentTable;
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
     * @return the commentCommaIndexes
     */
    public List<String> getCommentCommaIndexes() {
        return commentCommaIndexes;
    }

    /**
     * @param commentCommaIndexes the commentCommaIndexes to set
     */
    public void setCommentCommaIndexes(List<String> commentCommaIndexes) {
        this.commentCommaIndexes = commentCommaIndexes;
    }

    /**
     * @return the commentTableOptions
     */
    public List<String> getCommentTableOptions() {
        return commentTableOptions;
    }

    /**
     * @param commentTableOptions the commentTableOptions to set
     */
    public void setCommentTableOptions(List<String> commentTableOptions) {
        this.commentTableOptions = commentTableOptions;
    }

    /**
     * @return the createOptions
     */
    public List<String> getCreateOptions() {
        return createOptions;
    }

    /**
     * @param createOptions the createOptions to set
     */
    public void setCreateOptions(List<String> createOptions) {
        this.createOptions = createOptions;
    }

    /**
     * @return the commentCreateOptions
     */
    public List getCommentCreateOptions() {
        return commentCreateOptions;
    }

    /**
     * @param commentCreateOptions the commentCreateOptions to set
     */
    public void setCommentCreateOptions(List commentCreateOptions) {
        this.commentCreateOptions = commentCreateOptions;
    }
}
