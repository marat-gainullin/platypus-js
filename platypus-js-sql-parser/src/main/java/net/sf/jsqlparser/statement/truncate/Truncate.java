package net.sf.jsqlparser.statement.truncate;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;

/**
 * A TRUNCATE TABLE statement
 */
public class Truncate implements Statement {

    private Table table;
    private String comment;
    private String commentTable;
    private String endComment = new String();

    @Override
    public void accept(StatementVisitor statementVisitor) {
        statementVisitor.visit(this);
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    @Override
    public String toString() {
        String sql = getComment() != null ? getComment() + " " : "";
        sql += "TRUNCATE " + (getCommentTable() != null ? getCommentTable() + " " : "") + "TABLE " + table;
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
}
