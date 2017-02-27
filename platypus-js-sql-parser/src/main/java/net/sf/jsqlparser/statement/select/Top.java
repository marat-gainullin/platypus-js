package net.sf.jsqlparser.statement.select;

/**
 * A top clause in the form [TOP row_count]
 */
public class Top {

    private long rowCount;
    private boolean rowCountJdbcParameter = false;
    private String commentTop;
    private String commentTopValue;

    public long getRowCount() {
        return rowCount;
    }

    public void setRowCount(long l) {
        rowCount = l;
    }

    public boolean isRowCountJdbcParameter() {
        return rowCountJdbcParameter;
    }

    public void setRowCountJdbcParameter(boolean b) {
        rowCountJdbcParameter = b;
    }

    public String toString() {
        return (getCommentTop() != null ? getCommentTop() + " " : "") + "TOP " + (getCommentTopValue() != null ? getCommentTopValue() + " " : "")
                + (rowCountJdbcParameter ? "?" : rowCount + "");
    }

    /**
     * @return the commentTop
     */
    public String getCommentTop() {
        return commentTop;
    }

    /**
     * @param commentTop the commentTop to set
     */
    public void setCommentTop(String commentTop) {
        this.commentTop = commentTop;
    }

    /**
     * @return the commentTopValue
     */
    public String getCommentTopValue() {
        return commentTopValue;
    }

    /**
     * @param commentTopValue the commentTopValue to set
     */
    public void setCommentTopValue(String commentTopValue) {
        this.commentTopValue = commentTopValue;
    }
}
